package game2_1.clientSide;

import ch.bildspur.postfx.builder.PostFX;
import game2_1.Game;
import game2_1.GameState;
import game2_1.WindowLogic;
import game2_1.clientSide.shaders.BloomPass;
import game2_1.events.KeyEvent;
import game2_1.events.MouseEvent;
import game2_1.internet.Client;
import game2_1.internet.ClientInfo;
import game2_1.internet.NetPacket;
import game2_1.music.BeatHandler;
import game2_1.music.MusicPlayer;
import game2_1.serverSide.PlayerLogic;
import processing.core.PGraphics;
import utility.Debug;
import utility.NormalizedMath;

import java.util.Objects;
import java.util.Queue;

//TODO:
// - Run a local simulation for the local player, reduces input lag.


/**
 * This is the client's main class. It receives gamestates from the server and renders them.
 * It interpolates between gamestate if the client's framerate and server's updaterate don't match.
 *
 * @see GameState
 */
public class RenderLogic implements WindowLogic {
    private final Game parent;

    //The previous and current gamestate sent from the server
    private GameState previousGameState;
    GameState currentGameState;

    //The client's current gamestate
    private GameState clientGameState;
    //This client's player
    private PlayerLogic player;

    private int serverUPS;
    private long gameStateTimeStamp;//used when interpolating between the server's gamestates

    public Client client;

    boolean showBeatsOnCurson;

    //region PostFX
    private PostFX postFX;
    private BloomPass bloomPass;
    //endregion

    public RenderLogic(Game parent) {
        this.parent = parent;
        previousGameState = new GameState();
        currentGameState = new GameState();
        clientGameState = new GameState();
    }

    public void handleNetPackets() {
        if (client.getReceiveQueue().size() > 0) {
            Queue<NetPacket> queue = client.getAndClearReceiveQueue();
            for (NetPacket packet : queue) {
                switch (packet.type()) {
                    case GAME_STATE -> {
                        previousGameState = currentGameState;
                        currentGameState = (GameState) packet.object();
                        gameStateTimeStamp = packet.timeStamp();

                        player = currentGameState.players.get(client.id);
                    }
                    case GAME_STATE_DELTA -> {//todo
                        //previousGameState.mutableMembers = currentGameState.mutableMembers;
                        //currentGameState.mutableMembers = (GameState.MutableGameState) packet.object();
                        //gameStateTimeStamp = packet.timeStamp();
                    }
                    case CLIENT_INPUT -> throw new UnsupportedOperationException("This should not happen"); //todo: ignore it? or disconnect from the server?
                    case INFORMATION -> {//Sent once from the server when the client (this) connects.
                        ClientInfo info = (ClientInfo) packet.object();
                        Debug.logNamedShort(info);

                        client.id = info.clientId();
                        serverUPS = info.serverUPS();

                        clientGameState = info.gameState();
                        player = clientGameState.players.get(client.id);

                        clientGameState.music = new MusicPlayer(clientGameState.songPath + "song.wav", 1);
                        for (byte id : clientGameState.players.keySet())
                            clientGameState.players.compute(id, (ignored, player) -> new Player(Objects.requireNonNull(player), id));
                    }
                    case SERVER_COMMAND -> {
                        String command = (String) packet.object();
                        if (command.startsWith("Start Music")) {//Start the music player with the delayed start specified by the server. This is so all clients start at the same time regardless of their ping
                            clientGameState.music = new MusicPlayer(clientGameState.songPath + "song.wav", 1);

                            long startDelay = Long.parseLong(command.substring(11));
                            startDelay -= System.currentTimeMillis() - packet.timeStamp();
                            clientGameState.music.start(0, startDelay);
                        }
                    }
                    case EMPTY -> {
                    }
                }
            }
        }
    }

    @Override
    public void render(PGraphics g) {
        //Create shaders
        if (postFX == null) {
            var temp = parent.window.getApplet();
            postFX = new PostFX(temp);
            bloomPass = new BloomPass(temp);
        }

        handleNetPackets();

        //If the game hasn't started yet, enter the songmenu
        //Todo: move this somewhere else?
        if (!currentGameState.gameRunning) {
            parent.window.pushLogic(new SongMenu(parent.window, this));
            return;
        }

        if (previousGameState == null) return;

        //Lerp between the server's gamestates
        float t = (System.currentTimeMillis() - gameStateTimeStamp) / (1000f / serverUPS);
        GameState serverGameState = GameState.lerp(t, previousGameState, currentGameState);

        //region Render
        g.background(0);

        //notice: this is for debugging, remove this
        //region Debug
        g.fill(0);
        g.rect(g.width / 2f - 100, g.height / 2f - 50, 200, 100);
        g.fill(1);

        g.text(String.format("""
                        %1.1f %1.1f
                        %1.2f
                        %s""",
                parent.window.getFrameRate(),
                t,
                serverGameState.updateCount,
                Debug.collectionCompare(clientGameState.projectiles, serverGameState.projectiles)
        ), g.width / 2f, g.height / 2f);
        //endregion

        //The server doesn't care about graphical things like particle systems. Therefore we can't
        //use the server's gamestates immediately, but must apply them to the local gamestate.
        // Things like positions or velocities of projectiles are updated, while particle systems are preserved.
        clientGameState.lerp(0.8f, serverGameState);

        //region Players
        //all the PlayerLogic's in this map are actually players.
        clientGameState.players.forEach((id, player) -> ((Player) player).render(g));
        //endregion

        //region Projectiles
        clientGameState.projectiles.forEach(projectile -> {
            if (projectile != null) projectile.render(g);
        });
        //endregion

        //region Beats

        {
            g.push();
            final float[] highestBeatStrength = {0};

            final long time = clientGameState.music.getMicrosecondPosition() / 1000 + clientGameState.beats.startOffset;//millis
            final int pixelsPerSecond = 200;
            final float lookAhead = 0.75f; //How early a beat should be visible. If this is one each beat will be visible one second before they hit

            final float cenX = showBeatsOnCurson ? player.cursor.x : parent.window.WINDOW_W / 2f;
            final float cenY = showBeatsOnCurson ? player.cursor.y : parent.window.WINDOW_H - 15;

            //Draw the coming beats
            g.strokeWeight(1);
            clientGameState.beats.forEach((type, queue) -> {
                if (type != player.weaponType) return;//Only render the beats the player cares about

                //The bloom is dependent on the music, find the closest beat
                highestBeatStrength[0] = Math.max(highestBeatStrength[0], clientGameState.beats.getStrength(type, time));

                for (BeatHandler.Beat beat : queue) {
                    float x = beat.timeStamp() - time;

                    if (x < 0) continue;

                    x /= 1000f;
                    x *= pixelsPerSecond;

                    if (x > pixelsPerSecond * lookAhead) break;
                    g.stroke(NormalizedMath.smoothStart3(1 - x / (pixelsPerSecond * lookAhead)));

                    g.line(cenX + x, cenY - 5, cenX + x, cenY + 5);
                    g.line(cenX - x, cenY - 5, cenX - x, cenY + 5);
                }
            });

            bloomPass.setStrength(NormalizedMath.smoothStop2(highestBeatStrength[0]));
            g.pop();
        }

        //endregion

        //region Score
        final StringBuilder scoreText = new StringBuilder();
        clientGameState.scores.forEach((playerId, score) ->
                scoreText.append(String.format("%d: %d\n", playerId, score))
        );
        g.text(scoreText.toString().trim(), parent.window.WINDOW_W - 100, 0, 100, 200);
        //endregion

        //Apply shaders
        try {
            postFX.render()
                    .custom(bloomPass)
                    .compose();
        } catch (Exception e) {
            Debug.logError(e.toString());
        }
        //endregion
    }

    @Override
    public void onKeyEvent(KeyEvent event) {
        if (event.KEY == 'u') {//notice: this is for debugging, remove this
            bloomPass.reload();
        }

        client.send(event);
    }
    @Override
    public void onMouseEvent(MouseEvent event) {
        client.send(event);
    }

    @Override
    public void onExit() {
        parent.close();
    }

    public void close() {
        clientGameState.music.stop();
    }
}
