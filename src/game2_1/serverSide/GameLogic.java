package game2_1.serverSide;

import game2_1.Game;
import game2_1.GameState;
import game2_1.events.InputEvent;
import game2_1.events.KeyEvent;
import game2_1.events.KeyEventType;
import game2_1.internet.ClientInfo;
import game2_1.internet.NetPacket;
import game2_1.internet.NetPacketType;
import game2_1.internet.Server;
import game2_1.music.BeatHandler;
import game2_1.music.DummyMusicPlayer;
import utility.Debug;

import java.util.HashSet;
import java.util.ListIterator;
import java.util.Queue;

public class GameLogic {
    final Game parent;

    public final GameState gameState;//todo: protect with synchronized blocks
    private final HashSet<Byte> readyPlayers;

    public final int ups;
    Server server;

    public GameLogic(Game parent, Server server, int ups) {
        this.parent = parent;
        this.server = server;
        this.ups = ups;

        //String file = "music/Jonathon Young - Jetpack Race";
        String file = "music/Trigger - KUURO/";

        gameState = new GameState(file);
        readyPlayers = new HashSet<>();

        onClientJoin((byte) 100); //dummy
        gameState.players.get((byte) 100).pos.set(10, 10);
        readyPlayers.add((byte) 100);
        ++gameState.readyPlayers;

        //gameState.beats = BeatHandler.load(file + ".txt");
        gameState.music = new DummyMusicPlayer("", 1.0f);
    }

    public void mainLoop() {
        double minDeltaTime = 1000. / ups; //milliseconds

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
                update((float) minDeltaTime / 1000); //seconds
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
                case Message -> {
                    if (packet.object() instanceof String message) {
                        //Todo, use dedicated classes instead of strings
                        if (message.equals("Client Ready")) {
                            if (readyPlayers.add(id)) {
                                ++gameState.readyPlayers;
                                Debug.log("Client " + id + " ready");

                                if (gameState.readyPlayers == gameState.players.size())
                                    gameState.gameRunning = true;

                                server.queue(new NetPacket(NetPacketType.ServerCommand, 0, "Start Music" + (System.currentTimeMillis() + 500)));
                                gameState.music.start(0, 500);
                            }
                        } else if (message.startsWith("Selected")) {
                            gameState.songPath = Debug.log("music/" + message.substring(8) + "/");
                            gameState.beats = BeatHandler.load(gameState.songPath + "beats.txt");
                            gameState.music = new DummyMusicPlayer("", 1.0f);
                        } else if (message.startsWith("Weapon")) {
                            byte weaponType = Byte.parseByte(message.substring(6));
                            Debug.log("Switch player " + id + " weaponType to " + weaponType);
                            gameState.players.get(id).weaponType = weaponType;
                        }
                    }
                }
                case Empty -> {
                }
            }
        }
    }

    private void handleClientInput(byte id, InputEvent event) {
        gameState.players.get(id).handleEvent(event);
    }

    void update(float deltaTime) {
        handleNetPackets();

        ++gameState.updateCount;

        if (!gameState.gameRunning)
            return;

        for (ListIterator<Projectile> iterator = gameState.projectiles.listIterator(); iterator.hasNext(); ) {
            Projectile projectile = iterator.next();

            if (projectile == null) {
                continue;
            }

            if (projectile.delete) {
                //Debug.log("Removed: " + projectile);
                iterator.remove();
            }

            projectile.update(parent.window.Bounds);
        }

        gameState.players.forEach((id, playerLogic) -> {
            playerLogic.update(deltaTime, parent.window.Bounds);
            playerLogic.checkIfHit(gameState.projectiles);
        });
    }

    public void onClientJoin(byte clientID) {
        //synchronized (gameState) {
        PlayerLogic playerLogic = new PlayerLogic(clientID, gameState);
        gameState.players.put(clientID, playerLogic);
        //}


        try {
            server.sendTo(new NetPacket(
                            NetPacketType.Information,
                            (byte) 0,
                            new ClientInfo(
                                    clientID,
                                    ups,
                                    gameState
                            )),
                    clientID
            );
        } catch (Exception e) {
            if (clientID == 100) {
                Debug.logError("Suppressed " + e);
                Debug.logWarning("Remove this try catch when the dummy player is removed");
            } else
                throw e;
        }
    }
}
