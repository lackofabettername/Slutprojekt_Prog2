package game2_1.clientSide;

import game2_1.Application;
import game2_1.Game;
import game2_1.WindowLogic;
import game2_1.events.KeyEvent;
import game2_1.events.MouseEvent;
import ui.*;
import utility.Debug;
import utility.style.Foreground;

import processing.core.PGraphics;

import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MainMenu implements WindowLogic, UIListener {
    final Game parent;
    final Application window;
    final UI ui;

    private final MenuButton btnStartServer, btnStopServer;
    private final MenuButton btnStartClient, btnStopClient;
    private final MenuButton btnStartGame, btnMapSong;
    private final MenuText lblServerStat, lblClientStat;
    private final MenuTextField txfClientAddress;


    public MainMenu(Game parent) {
        this.parent = parent;
        window = parent.window;

        float padding = 5;

        MenuFramework framework = new MenuFramework("Menu", this, padding, padding, window.WindowW - padding * 2, window.WindowH - padding * 2);

        //region Add elements
        //region Server
        MenuFramework server = new MenuFramework("Server pane");
        server.addMenuObject(btnStartServer = new MenuButton("Start Server", 18));
        server.addMenuObject(btnStopServer = new MenuButton("Stop Server", 18));
        server.addMenuObject(lblServerStat = new MenuText("Server Status", 18), 2, 0, 3, 1);
        framework.addMenuObject(server, 1);
        //endregion

        //region Client
        MenuFramework client = new MenuFramework("Client pane");
        client.addMenuObject(btnStartClient = new MenuButton("Start Client", 18));
        client.addMenuObject(btnStopClient = new MenuButton("Stop Client", 18));

        MenuFramework clientStats = new MenuFramework("Client Status pane");
        clientStats.addMenuObject(lblClientStat = new MenuText("Client Status", 18), 0, 0, 1, 3);

        clientStats.addMenuObject(txfClientAddress = new MenuTextField("address", 14), 0, 3);
        client.addMenuObject(clientStats, 2, 0, 3, 1);

        framework.addMenuObject(client, 1);
        //endregion

        //region Start clientSide
        MenuFramework startPane = new MenuFramework("Start pane");

        startPane.addMenuObject(btnStartGame = new MenuButton("Start Game", 32), 0, 0, 5, 1);
        startPane.addMenuObject(btnMapSong = new MenuButton("Map song", 12), 5, 0);

        framework.addMenuObject(startPane, 1);
        //endregion
        //endregion

        //region Fit
        framework.fitElements(padding * 2);
        server.fitElements(padding, 0);
        client.fitElements(padding, 0);
        clientStats.fitElements(padding, 0);
        startPane.fitElements(padding, 0);

        //dropdown.fitElements(padding);
        //endregion

        ui = new UI(window, framework);
    }

    @Override
    public void render(PGraphics g) {
        g.background(0);

        if (parent.server != null) {
            lblServerStat.text = "Running" +
                    "\n" + parent.server.LocalAddress +
                    "\n" + parent.server.getClientCount() + " connected client" + (parent.server.getClientCount() == 1 ? "" : "s");

        }// else
        //    lblServerStat.text = "Off";

        if (parent.client != null) {
            if (!parent.client.getThread().isAlive())
                lblClientStat.text = "dead";
        }

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
        Debug.logDecorated(caller.name, Foreground.Blue);
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
            } catch (UnknownHostException | NullPointerException e) {
                Debug.logError(e);
            }
        } else if (caller == btnStopServer) {
            if (parent.server != null)
                parent.closeServer();

            lblServerStat.text = "Off";
        } else if (caller == btnStartClient) {
            try {
                InetAddress address = InetAddress.getByName(txfClientAddress.text);
                parent.startClient(address);

                lblClientStat.text = "Running";
            } catch (UnknownHostException e) {
                Debug.logError(e);
                lblClientStat.text = "Invalid address";
            } catch (NumberFormatException | UncheckedIOException e) {
                Debug.logError(e);
                lblClientStat.text = "Invalid port";
            } catch (Exception e) {
                Debug.logError(e);
            }
        } else if (caller == btnStopClient) {
            if (parent.client != null)
                parent.closeClient();

            lblClientStat.text = "Off";
        } else if (caller == btnStartGame) {
            parent.window.setLogic(parent.clientSide);
        } else if (caller == btnMapSong) {
            parent.window.setLogic(parent.songMapper);
        }
    }

    @Override
    public void onExit() {
        parent.closeClient();
        parent.closeServer();
    }
}
