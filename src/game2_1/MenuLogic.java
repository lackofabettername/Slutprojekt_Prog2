package game2_1;

import game2_1.events.KeyEvent;
import game2_1.events.MouseEvent;
import ui.*;
import utility.Debug;
import utility.style.Foreground;

import processing.core.PGraphics;

import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class MenuLogic implements WindowLogic, UIListener {
    final Game parent;
    final Application window;
    final UI ui;

    private final MenuButton btnStartServer, btnStopServer;
    private final MenuButton btnStartClient, btnStopClient;
    private final MenuButton btnStartGame, btnMapSong;
    private final MenuText lblServerStat, lblClientStat;
    private final MenuTextField txfClientAddress, txfClientPort;


    MenuLogic(Game parent) {
        this.parent = parent;
        window = parent.window;

        float padding = 5;

        MenuFramework framework = new MenuFramework("Menu", this, padding, padding, window.WindowW - padding * 2, window.WindowH - padding * 2);

        MenuDropdownFramework dropdown = new MenuDropdownFramework("dropdown");
        dropdown.addMenuObject(new MenuFileSelector("files", "music"));
        dropdown.collapsedText = new MenuText("Cool", 20);

        //framework.addMenuObject(dropdown, 1);

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
        clientStats.addMenuObject(lblClientStat = new MenuText("Client Status", 18), 0, 0, 2, 3);

        clientStats.addMenuObject(txfClientAddress = new MenuTextField("address", 14), 0, 3);
        clientStats.addMenuObject(txfClientPort = new MenuTextField("port", 14), 1, 3);
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

        //dropdown.expandedBounds.h *= 3;

        ui = new UI(window, framework);
    }

    @Override
    public void render(PGraphics g) {
        g.background(0);

        if (parent.server != null) {
            lblServerStat.text = "Running" +
                    "\n" + parent.server.serverAddress +
                    "\nOpen port: " + parent.server.getOpenPort() +
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
    public void keyEvent(KeyEvent event) {
        ui.handleEvent(event);
    }
    @Override
    public void mouseEvent(MouseEvent event) {
        ui.handleEvent(event);
    }

    @Override
    public void uiEvent(MenuObject caller) {
        Debug.logDecorated(caller.name, Foreground.Blue);
        if (caller == btnStartServer) {
            try {
                parent.startServer();

                //wait for server to Start
                for (long start = System.currentTimeMillis(); parent.server.getOpenPort() == -1 && start > System.currentTimeMillis() - 1000; ) {
                    Thread.onSpinWait();
                }

                lblServerStat.text = "Running" +
                        "\n" + parent.server.serverAddress +
                        "\n" + parent.server.getOpenPort();

                txfClientAddress.text = "localhost";
                txfClientPort.text = "" + parent.server.getOpenPort();
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
                int port = Integer.parseInt(txfClientPort.text);

                parent.startClient(address, port);

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
            parent.window.currentLogic = parent.clientSide;
        } else if (caller == btnMapSong) {
            parent.window.currentLogic = parent.songMapper;
        }
    }
}
