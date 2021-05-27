package game2_1;

import game2_1.clientSide.MainMenu;
import game2_1.clientSide.RenderLogic;
import game2_1.internet.Client;
import game2_1.internet.NetPacket;
import game2_1.internet.Server;
import game2_1.serverSide.GameLogic;
import utility.Debug;
import utility.style.Foreground;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Locale;

/**
 * This is the main class. Run the main method to play the game.
 */
public class Game {
    /**
     * Updates per second. Changing this number will change the speed of the game.
     */
    public final int SERVER_UPS = 30;

    public volatile Server server;
    public volatile Client client;

    public final Application window;

    public final RenderLogic clientSide;
    public volatile GameLogic serverSide;

    public final MainMenu menu;

    public Game() {
        Locale.setDefault(Locale.ENGLISH);

        try {
            //Debug will print everything into this file as well as the console
            Debug.logFile = new PrintWriter(new BufferedOutputStream(new FileOutputStream("Output.log")));
        } catch (FileNotFoundException e) {
            Debug.logError(e);
        }

        //Start the window
        Debug.logPush("Starting window...");
        window = new Application(900, 600);
        window.init();
        Debug.logPop("Window started");

        //Create the various window logics
        Debug.logPush("Starting UI...");
        clientSide = new RenderLogic(window);
        menu = new MainMenu(this);
        window.setLogic(menu);
        Debug.logPop("UI started");

        Debug.logLine();

        while (window.isRunning()) {
            try {
                synchronized (this) {
                    Thread.sleep(500);
                }

                //If the server is active, start the serverside logic.
                if (server != null) {
                    //The thread that started the server may not have initialized the serverSide yet.
                    while (serverSide == null)
                        Thread.onSpinWait();

                    //Start the server logic.
                    serverSide.mainLoop();
                }
            } catch (InterruptedException e) {
                Debug.logError(e);
            }
        }
    }

    /**
     * Start the server and create the ServerSide logic.
     *
     * @throws SocketException Thrown by the Server's DatagramSocket.
     * @see Server
     * @see GameLogic
     * @see java.net.DatagramSocket
     */
    public void startServer() throws SocketException {
        server = new Server(serverSide);
        server.start();

        Debug.decorateThreadOutput(server.getThread(), Foreground.Magenta);

        serverSide = new GameLogic(window, server, SERVER_UPS);
    }

    /**
     * Close the server and the ServerSide logic.
     *
     * @see Server
     * @see GameLogic
     */
    public void closeServer() {
        if (server != null) {
            //serverSide.stop();
            server.close();
        }
        server = null;
        serverSide = null;
    }

    /**
     * Tries to start the client and connected it to the address.
     *
     * @param address The address of the server the client is connecting to.
     * @return true if the client was started and has connected successfully. False if it failed after five attempts.
     * @throws SocketException Thrown by the Client's DatagramSocket.
     * @see Client
     * @see Server
     * @see java.net.DatagramSocket
     */
    public boolean startClient(InetAddress address) throws SocketException {
        client = new Client(address);
        client.start();
        clientSide.client = client;

        for (int i = 0; i < 5; ++i) {
            //Send a blank package with nothing in it. This is to tell the server that the client exists
            client.send(NetPacket.EMPTY_PACKET);

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Debug.logError(e);
            }

            clientSide.handleNetPackets();

            //If the server accepted the client it will have sent a Information packet with the clients id.
            if (client.id != -1) {
                return true;
            }
        }

        Debug.logWarning("Client failed to connect");
        closeClient();
        return false;
    }

    /**
     * Closes the client.
     *
     * @see Client
     */
    public void closeClient() {
        if (client != null) {
            client.close();
            if (clientSide != null)
                clientSide.client = null;
        }
        client = null;
    }

    public static void main(String[] args) throws UnknownHostException {
        try {
            new Game();
        } catch (Throwable e) {
            Debug.closeLog();

            throw e;
        }
    }
}
