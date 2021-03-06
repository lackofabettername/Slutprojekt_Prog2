package game2_1.clientSide;

import game2_1.Application;
import game2_1.Game;
import game2_1.WindowLogic;
import game2_1.events.KeyEvent;
import game2_1.events.MouseEvent;
import game2_1.music.BeatMapper;
import ui.*;
import utility.Debug;
import utility.internet.IO;
import utility.style.Foreground;

import processing.core.PGraphics;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * This is the games MainMenu, it is the first thing you see when you start the game.
 */
public class MainMenu implements WindowLogic, UIListener {
    private final Game parent;
    private final Application window;
    private final UI ui;

    private final MenuButton btnStartServer, btnStopServer;
    private final MenuButton btnStartClient, btnStopClient;
    private final MenuButton btnStartGame, btnMapSong;
    private final MenuText lblServerStat, lblClientStat;
    private final MenuNumberField nmfClientPort;
    private final MenuTextField txfClientAddress;


    public MainMenu(Game parent) {
        this.parent = parent;
        window = parent.window;

        float padding = 5;

        MenuFramework framework = new MenuFramework("Main menu framework", this, padding, padding, window.WINDOW_W - padding * 2, window.WINDOW_H - padding * 2);

        //region Add elements
        //region Server
        MenuFramework serverFramework = new MenuFramework("Server framework");

        serverFramework.addMenuObject(btnStartServer = new MenuButton("Start Server", 18));
        serverFramework.addMenuObject(btnStopServer = new MenuButton("Stop Server", 18));
        serverFramework.addMenuObject(lblServerStat = new MenuText("Server Status", 18), 2, 0, 3, 1);

        framework.addMenuObject(serverFramework, 1);
        //endregion

        //region Client
        MenuFramework clientFramework = new MenuFramework("Client framework");
        clientFramework.addMenuObject(btnStartClient = new MenuButton("Start Client", 18));
        clientFramework.addMenuObject(btnStopClient = new MenuButton("Stop Client", 18));

        MenuFramework clientStats = new MenuFramework("Client Status framework");
        clientStats.addMenuObject(lblClientStat = new MenuText("Client Status", 18), 0, 0, 2, 3);

        clientStats.addMenuObject(txfClientAddress = new MenuTextField("address", 14), 0, 3);
        clientStats.addMenuObject(nmfClientPort = new MenuNumberField("port", 1), 1, 3);
        nmfClientPort.value = IO.ClientPort;
        clientFramework.addMenuObject(clientStats, 2, 0, 3, 1);

        framework.addMenuObject(clientFramework, 1);
        //endregion

        //region Start clientSide
        MenuFramework startFramework = new MenuFramework("Start framework");

        startFramework.addMenuObject(btnStartGame = new MenuButton("Start Game", 32), 0, 0, 5, 1);
        startFramework.addMenuObject(btnMapSong = new MenuButton("Map song", 12), 5, 0);

        framework.addMenuObject(startFramework, 1);
        //endregion
        //endregion

        //region Fit
        framework.fitElements(padding * 2);
        serverFramework.fitElements(padding, 0);
        clientFramework.fitElements(padding, 0);
        clientStats.fitElements(padding, 0);
        startFramework.fitElements(padding, 0);
        //endregion

        ui = new UI(window, framework);
    }

    @Override
    public void render(PGraphics g) {
        g.background(0);

        //Update the server statistics
        if (parent.server != null) {
            lblServerStat.text = "Running" +
                    "\n" + parent.server.LocalAddress +
                    "\n" + parent.server.getClientCount() + " connected client" + (parent.server.getClientCount() == 1 ? "" : "s");
        }

        //Update the client statistics if it were to crash
        if (parent.client != null) {
            if (!parent.client.getThread().isAlive())
                lblClientStat.text = "dead";
        }

        //Render the menu
        ui.onRender(g);
    }
    @Override
    public void onKeyEvent(KeyEvent event) {
        ui.handleEvent(event);
    }
    @Override
    public void onMouseEvent(MouseEvent event) {
        ui.handleEvent(event);
    }

    @Override
    public void uiEvent(MenuObject caller) {
        Debug.logDecorated("MainMenu: " + caller.name, Foreground.Blue);
        if (caller == btnStartServer) {
            try {
                try {
                    parent.startServer();
                } catch (SocketException e) {
                    Debug.logError(e);
                }

                lblServerStat.text = "Running" +
                        "\n" + parent.server.LocalAddress;

                txfClientAddress.text = "localhost";
            } catch (NullPointerException e) {
                Debug.logError(e);
            }
        } else if (caller == btnStopServer) {
            if (parent.server != null)
                parent.closeServer();

            lblServerStat.text = "Off";
        } else if (caller == btnStartClient) {
            try {
                InetAddress address = InetAddress.getByName(txfClientAddress.text);
                int port = (int) nmfClientPort.value;
                if (parent.startClient(port, address))
                    lblClientStat.text = "Running";
                else
                    lblClientStat.text = "Client could not connect to the server";
            } catch (UnknownHostException e) {
                Debug.logError(e);
                lblClientStat.text = "Invalid address";
            } catch (Exception e) {
                Debug.logError(e);
                lblClientStat.text = "An error occurred";
            }
        } else if (caller == btnStopClient) {
            if (parent.client != null)
                parent.closeClient();

            lblClientStat.text = "Off";
        } else if (caller == btnStartGame) {
            parent.window.pushLogic(parent.clientSide);
        } else if (caller == btnMapSong) {
            parent.window.pushLogic(new BeatMapper(parent.window));
        }
    }

    @Override
    public void onExit() {
        parent.closeClient();
        parent.closeServer();
    }
}
