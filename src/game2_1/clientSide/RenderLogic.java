package game2_1.clientSide;

import game2_1.Application;
import game2_1.GameState;
import game2_1.WindowLogic;
import game2_1.events.KeyEvent;
import game2_1.events.MouseEvent;
import game2_1.internet.Client;
import game2_1.internet.ClientInfo;
import game2_1.internet.NetPacket;
import game2_1.music.BeatHandler;
import game2_1.music.MusicPlayer;

import processing.core.PGraphics;

import utility.Debug;

import java.util.Objects;
import java.util.Queue;

public class RenderLogic implements WindowLogic {
    private GameState previousGameState;
    private GameState currentGameState;

    private GameState clientGameState;

    private int serverUPS;
    private long gameStateTimeStamp;

    private int frameCount;

    public Client client;

    private final Application parent;

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

                        clientGameState.music = new MusicPlayer(clientGameState.songPath + ".wav", 1);
                        clientGameState.beats = BeatHandler.load(clientGameState.songPath + ".txt");
                        for (byte key : clientGameState.players.keySet())
                            clientGameState.players.compute(key, (id, player) -> new Player(Objects.requireNonNull(player)));
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
        ++frameCount;

        handleNetPackets();

        if (previousGameState == null) return;

        float t = (System.currentTimeMillis() - gameStateTimeStamp) / (1000f / serverUPS);
        //t = 1;
        GameState serverGameState = GameState.lerp(t, previousGameState, currentGameState);
        //serverGameState = currentGameState;

        //region Debug
        g.background(0);
        g.fill(0);
        g.rect(g.width / 2f - 100, g.height / 2f - 50, 200, 100);
        g.fill(1);

        g.text(String.format("%d, %1.1f %1.1f\n" +
                        "%1.2f\n%s",
                frameCount,
                parent.getFrameRate(),
                t,
                serverGameState.frameCount,
                Debug.collectionCompare(clientGameState.projectiles, serverGameState.projectiles)
        ), g.width / 2f, g.height / 2f);
        //endregion


        clientGameState.lerp(1.f, serverGameState); //fixme
        //clientGameState.lerp(0.8f, serverGameState);
        //clientGameState = serverGameState;

        //region Show
        clientGameState.players.forEach((id, player) -> ((Player) player).render(g));
        clientGameState.projectiles.forEach(projectile -> {
            if (projectile != null) projectile.render(g);
        });

        g.push();
////        clientGameState.beats.forEach((type, queue) -> {
////            long time = clientGameState.music.getMicrosecondPosition() / 1000;
////            g.stroke(1);
////            for (BeatHandler.Beat beat : queue) {
////                float x = beat.timeStamp() - time;
////
////                if (x < 0) continue;
////
////                x /= 1000f;
////                x *= 250;//pixels per second
////
////                g.line(g.width / 2f + x, g.height - 12, g.width / 2f + x, g.height - 2);
////                g.line(g.width / 2f - x, g.height - 12, g.width / 2f - x, g.height - 2);
////            }
////        });
        g.pop();

        final StringBuilder scoreText = new StringBuilder();
        clientGameState.scores.forEach((playerId, score) ->
                scoreText.append(String.format("%d: %d\n", playerId, score))
        );
        g.text(scoreText.toString().trim(), parent.WindowW - 100, 0, 100, 200);
        //endregion
    }

    @Override
    public void keyEvent(KeyEvent event) {
        client.queue(event);
    }
    @Override
    public void mouseEvent(MouseEvent event) {
        client.queue(event);
    }

}
