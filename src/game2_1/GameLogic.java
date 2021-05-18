package game2_1;

import game2_1.events.InputEvent;
import game2_1.events.KeyEvent;
import game2_1.events.KeyEventType;
import game2_1.internet.NetPacket;
import game2_1.internet.NetPacketType;
import game2_1.internet.Server;
import game2_1.music.DummyMusicPlayer;

import java.util.ListIterator;
import java.util.Queue;

public class GameLogic {
    final Game parent;

    public final GameState gameState;

    public final int ups;
    Server server;

    GameLogic(Game parent, Server server, int ups) {
        this.parent = parent;
        this.server = server;
        this.ups = ups;

        //String file = "music/Jonathon Young - Jetpack Race";
        String file = "music/60 BPM - Simple Straight Beat";

        gameState = new GameState(file);

        onClientJoin((byte) 100); //dummy
        gameState.players.get((byte) 100).pos.set(10, 10);


        //gameState.beats = BeatHandler.load(file + ".txt");
        gameState.music = new DummyMusicPlayer(file + ".wav",1.0f);
    }

    public void mainLoop() {
        double minDeltaTime = 1000. / ups;

        double acumulator = 0;
        long lastFrame = System.currentTimeMillis();
        long deltaTime;
        while (server != null && server.getThread().isAlive()) {
            long frame = System.currentTimeMillis();
            deltaTime = frame - lastFrame;

            if (deltaTime <= minDeltaTime) {
                continue;
            }

            lastFrame = frame;
            acumulator += deltaTime;

            while (acumulator > minDeltaTime) {
                update((float) minDeltaTime / 1000);
                acumulator -= minDeltaTime;
            }

            //Debug.logAll("send", serverSide.currentGameState);
            server.queue(new NetPacket(NetPacketType.GameState, 0, gameState));
            server.sendQueued();
        }
    }

    private void handleNetPackets() {
        Queue<NetPacket> input = server.getAndClearReceiveQueue();
        for (NetPacket packet : input) {
            byte id = packet.sender();
            switch (packet.type()) {
                case GameState, GameStateDelta -> throw new UnsupportedOperationException("error");
                case ClientInput -> handleClientInput(id, (InputEvent) packet.object());
                case Empty -> {
                }
            }
        }
    }

    private void handleClientInput(byte id, InputEvent event) {
        if (event instanceof KeyEvent keyEvent) {
            if (keyEvent.Type == KeyEventType.KeyPressed && keyEvent.Key == 'k') {
                server.queue(new NetPacket(NetPacketType.ServerCommand, 0, "Start Music" + (System.currentTimeMillis() + 500)));
                gameState.music.start(0, 500);
            }
        }

        gameState.players.get(id).handleEvent(event);
    }

    void update(float deltaTime) {
        handleNetPackets();

        ++gameState.frameCount;

        for (ListIterator<Projectile> iterator = gameState.projectiles.listIterator(); iterator.hasNext(); ) {
            Projectile projectile = iterator.next();

            if (projectile == null) {
                continue;
            }

            projectile.update(parent.window.Bounds);

            if (projectile.delete)
                iterator.set(null);
        }

        gameState.players.forEach((id, player) -> {
            player.update(deltaTime, parent.window.Bounds);
            player.checkIfHit(gameState.projectiles);
        });
    }

    public void onClientJoin(byte id) {
        synchronized (gameState) {
            Player player = new Player(id, gameState);
            gameState.players.put(id, player);
        }
    }
}
