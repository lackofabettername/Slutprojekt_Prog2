package game2_1.clientSide;

import ch.bildspur.postfx.builder.PostFX;
import game2_1.Application;
import game2_1.GameState;
import game2_1.WindowLogic;
import game2_1.clientSide.shaders.BlurPass;
import game2_1.events.KeyEvent;
import game2_1.events.MouseEvent;
import game2_1.internet.Client;
import game2_1.internet.ClientInfo;
import game2_1.internet.NetPacket;
import game2_1.music.BeatHandler;
import game2_1.music.MusicPlayer;

import processing.core.PGraphics;

import game2_1.serverSide.PlayerLogic;
import utility.Debug;

import java.util.Objects;
import java.util.Queue;

public class RenderLogic implements WindowLogic {
    private GameState previousGameState;
    GameState currentGameState;

    private GameState clientGameState;
    private PlayerLogic player;

    private int serverUPS;
    private long gameStateTimeStamp;

    private int frameCount;

    public Client client;

    private final Application parent;
    //region PostFX
    private PostFX postFX;
    private BlurPass blurPassX;
    private BlurPass blurPassY;
    //endregion

    public RenderLogic(Application parent) {
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
                    case GameState -> {
                        previousGameState = currentGameState;
                        currentGameState = (GameState) packet.object();
                        gameStateTimeStamp = packet.timeStamp();

                        player = currentGameState.players.get(client.id);
                    }
                    case GameStateDelta -> {
                        //previousGameState.mutableMembers = currentGameState.mutableMembers;
                        //currentGameState.mutableMembers = (GameState.MutableGameState) packet.object();
                        //gameStateTimeStamp = packet.timeStamp();
                    }
                    case ClientInput -> throw new UnsupportedOperationException("This should not happen");
                    case Information -> {
                        ClientInfo info = (ClientInfo) packet.object();
                        Debug.log(info);

                        client.id = info.clientId();
                        serverUPS = info.serverUPS();

                        clientGameState = info.gameState();
                        player = clientGameState.players.get(client.id);

                        clientGameState.music = new MusicPlayer(clientGameState.songPath + "song.wav", 1);
                        //clientGameState.beats = BeatHandler.load(clientGameState.songPath + ".txt");
                        for (byte id : clientGameState.players.keySet())
                            clientGameState.players.compute(id, (ignored, player) -> new Player(Objects.requireNonNull(player), id));
                    }
                    case ServerCommand -> {
                        String command = (String) packet.object();
                        if (command.startsWith("Start Music")) {
                            long timeStamp = Long.parseLong(command.substring(11));
                            clientGameState.music.start(0, timeStamp);
                        }
                    }
                    case Empty -> {
                    }
                }
            }
        }

        if (frameCount % 1 == 0)
            client.sendQueued();
    }

    @Override
    public void render(PGraphics g) {
        if (postFX == null) {
            var temp = parent.getApplet();
            postFX = new PostFX(temp);
            blurPassX = new BlurPass(temp, false);
            blurPassY = new BlurPass(temp, true);
        }

        ++frameCount;

        handleNetPackets();

        if (!currentGameState.gameRunning) {
            parent.setLogic(new SongMenu(parent, this));
            return;
        }

        if (previousGameState == null) return;

        float t = (System.currentTimeMillis() - gameStateTimeStamp) / (1000f / serverUPS);
        //t = 1;
        GameState serverGameState = GameState.lerp(t, previousGameState, currentGameState);

        //region Render
        g.background(0);

        //region Debug
        g.fill(0);
        g.rect(g.width / 2f - 100, g.height / 2f - 50, 200, 100);
        g.fill(1);

        g.text(String.format("%d, %1.1f %1.1f\n" +
                        "%1.2f\n%s",
                frameCount,
                parent.getFrameRate(),
                t,
                serverGameState.updateCount,
                Debug.collectionCompare(clientGameState.projectiles, serverGameState.projectiles)
        ), g.width / 2f, g.height / 2f);
        //endregion

        clientGameState.lerp(0.8f, serverGameState); //fixme

        //region Show
        clientGameState.players.forEach((id, player) -> ((Player) player).render(g));
        clientGameState.projectiles.forEach(projectile -> {
            if (projectile != null) projectile.render(g);
        });

        g.push();
        clientGameState.beats.forEach((type, queue) -> {
            if (type != player.weaponType)
                return;

            long time = clientGameState.music.getMicrosecondPosition() / 1000;//millis
            time += clientGameState.beats.startOffset;

            g.stroke(1);
            int i = 0;
            for (BeatHandler.Beat beat : queue) {
                float x = beat.timeStamp() - time;

                if (x < 0) continue;
                //if (i++ > 3) break;//Only show the next three beats

                x /= 1000f;
                x *= 300;//pixels per second

                g.line(g.width / 2f + x, g.height - 12, g.width / 2f + x, g.height - 2);
                g.line(g.width / 2f - x, g.height - 12, g.width / 2f - x, g.height - 2);
            }
        });
        g.pop();

        final StringBuilder scoreText = new StringBuilder();
        clientGameState.scores.forEach((playerId, score) ->
                scoreText.append(String.format("%d: %d\n", playerId, score))
        );
        g.text(scoreText.toString().trim(), parent.WindowW - 100, 0, 100, 200);
        //endregion


        try {
            postFX.render()
                    .custom(blurPassX)
                    .custom(blurPassY)
                    .compose();
        } catch (Exception e) {
            Debug.logError(e.toString());
        }
        //endregion
    }

    @Override
    public void onKeyEvent(KeyEvent event) {
        if (event.Key == 'u') {
            blurPassX.reload();
            blurPassY.reload();
        }

        client.queue(event);
    }
    @Override
    public void onMouseEvent(MouseEvent event) {
        client.queue(event);
    }

}
