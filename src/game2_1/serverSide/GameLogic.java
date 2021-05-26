package game2_1.serverSide;

import game2_1.Application;
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

/**
 * This is the core of the game simulation. It simulates the game and sends the GameState to the clients.
 * @see GameState
 */
public class GameLogic {
    private final Application parent;
    private final Server server; //Todo, disconnect a client if it doesn't send any inputs in a certain time

    public final GameState gameState;
    private final HashSet<Byte> readyPlayers;

    public final int ups;

    public GameLogic(Application parent, Server server, int ups) {
        this.parent = parent;
        this.server = server;
        this.ups = ups;

        //TODO: make this blank?
        String file = "music/Trigger - KUURO/";

        gameState = new GameState(file);
        readyPlayers = new HashSet<>();

        //Notice: remove this
        //region Add dummy player
        onClientJoin((byte) 100);
        gameState.players.get((byte) 100).pos.set(10, 10);
        readyPlayers.add((byte) 100);
        ++gameState.readyPlayers;
        //endregion

        gameState.music = new DummyMusicPlayer("", 1.0f);
    }

    /**
     * The core of the game simulation. This is a while(true) loop that runs until the server closes.
     */
    public void mainLoop() {
        //https://gafferongames.com/post/fix_your_timestep/

        //The size of timesteps the simulation uses
        double timeStep = 1000. / ups; //milliseconds

        //Keeps track of residual time. The time that's left over after a timestep was run.
        double accumulator = 0;
        long lastFrame = System.currentTimeMillis();
        long deltaTime;

        while (!server.getThread().isInterrupted()) {

            long frame = System.currentTimeMillis();
            deltaTime = frame - lastFrame;

            if (deltaTime <= timeStep) {
                continue;
            }

            //Add the unsimulated time to the accumulator
            accumulator += deltaTime;
            lastFrame = frame;

            //Catch up the simulation to the realworld time.
            for (int simulations = 0; accumulator > timeStep; ++simulations) {
                synchronized (gameState) {
                    update((float) timeStep / 1000); //seconds
                }
                accumulator -= timeStep;

                if (simulations > 10) {
                    //If the simulation takes more time than the timestep, it'll take more and more time to run the simulation.
                    //The simulation will fall further and further behind. This is unacceptable. Stop the simulation.
                    throw new IllegalStateException("The simulation is running too slow!");
                }
            }

            server.send(new NetPacket(NetPacketType.GameState, NetPacket.Server, gameState));
        }
    }

    private void handleNetPackets() {
        Queue<NetPacket> input = server.getAndClearReceiveQueue();
        for (NetPacket packet : input) {
            byte id = packet.sender();
            switch (packet.type()) {
                case GameState, GameStateDelta -> throw new UnsupportedOperationException("error");//TODO: Just ignore it instead? or disconnect the client?
                case ClientInput -> handleClientInput(id, (InputEvent) packet.object());
                case Message -> {//Todo: use dedicated classes instead of strings

                    if (packet.object() instanceof String message) {
                        if (message.equals("Client Ready")) {
                            if (readyPlayers.add(id)) {
                                ++gameState.readyPlayers;
                                Debug.log("Client " + id + " ready");

                                //All players are ready, start the game
                                if (gameState.readyPlayers == gameState.players.size()) {
                                    gameState.gameRunning = true;

                                    server.send(new NetPacket(NetPacketType.ServerCommand, NetPacket.Server, "Start Music" + (System.currentTimeMillis() + 500)));
                                    gameState.music.start(0, 500);
                                }
                            }

                        } else if (message.startsWith("Selected")) {
                            //Todo: voting system? or first come first serve?
                            //Todo: make sure all players have the song downloaded. Send it to them over the internet?

                            //Change the selected music
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

    private void update(float deltaTime) {
        //new clients may have joined, existing clients may have sent some inputs
        handleNetPackets();

        ++gameState.updateCount;

        if (!gameState.gameRunning)
            return;

        //Update the projectiles
        for (ListIterator<Projectile> iterator = gameState.projectiles.listIterator(); iterator.hasNext(); ) {
            Projectile projectile = iterator.next();

            if (projectile.delete) {
                //Debug.log("Removed: " + projectile);
                iterator.remove();
            }

            projectile.update(parent.Bounds);
        }

        //Update the players
        gameState.players.forEach((id, playerLogic) -> {
            playerLogic.update(deltaTime, parent.Bounds);
            playerLogic.checkIfHit(gameState.projectiles);
        });
    }

    /**
     * Called when a new client joins the game
     *
     * @param clientID the id the client should be assigned
     */
    public void onClientJoin(byte clientID) {
        //todo: don't allow player to join if game is already running

        synchronized (gameState) {
            PlayerLogic playerLogic = new PlayerLogic(clientID, gameState);
            gameState.players.put(clientID, playerLogic);
        }

        try {
            //Send the client the information it needs to start.
            server.sendTo(
                    new NetPacket(
                            NetPacketType.Information,
                            NetPacket.Server,
                            new ClientInfo(
                                    clientID,
                                    ups,
                                    gameState
                            )),
                    clientID
            );
        } catch (Exception e) {
            if (clientID == 100) {
                //There's always an exception thrown when the dummy player is added
                // since the server doesn't have an InetAddress associated with the dummyplayer's id.
                Debug.logError("Suppressed " + e);
                Debug.logWarning("Remove this try catch when the dummy player is removed");
            } else
                throw e;
        }
    }
}
