package game2_1;

import game2_1.clientSide.RenderLogic;
import game2_1.internet.Client;
import game2_1.internet.NetPacket;
import game2_1.internet.Server;
import game2_1.music.BeatMapper;
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

public class Game {
    public final int ServerUPS = 30;

    public volatile Server server;
    public volatile Client client;

    public final Application window;

    public final RenderLogic clientSide;
    public volatile GameLogic serverSide;

    public BeatMapper songMapper;

    public final MenuLogic menu;

    public Game() {
        Locale.setDefault(Locale.ENGLISH);

        try {
            Debug.logFile = new PrintWriter(new BufferedOutputStream(new FileOutputStream("Output.log")));
        } catch (FileNotFoundException e) {
            Debug.logError(e);
        }

        Debug.logPush("Starting window...");
        window = new Application(900, 600);
        window.init();
        Debug.logPop("Window started");

        Debug.logPush("Starting UI...");
        clientSide = new RenderLogic(window);
        menu = new MenuLogic(this);
        window.setLogic(menu);
        Debug.logPop("UI started");

        songMapper = new BeatMapper(window);

        Debug.logLine();

        while (true) {
            try {
                synchronized (this) {
                    wait(500);
                }

                if (server != null) {
                    while (serverSide == null)
                        Thread.onSpinWait();

                    serverSide.mainLoop();
                }
            } catch (InterruptedException e) {
                Debug.logError(e);
            }
        }
    }

    public void startServer() throws UnknownHostException {
        server = new Server(this);
        server.start();

        Debug.decorateThreadOutput(server.getThread(), Foreground.Magenta);

        serverSide = new GameLogic(this, server, ServerUPS);
    }

    public void closeServer() {
        server.close();
        server = null;
    }

    public void startClient(InetAddress address, int port) throws SocketException {
        client = new Client("Client", address, port);
        client.start();

        clientSide.client = client;

        for (int i = 0; i < 5; ++i) {
            client.queue(NetPacket.EmptyPacket);
            client.sendQueued();

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Debug.logError(e);
            }

            clientSide.handleNetPackets();

            if (client.id != -1)
                return;
        }

        Debug.logWarning("Client failed to connect");
    }

    public void closeClient() {
        client.close();
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
