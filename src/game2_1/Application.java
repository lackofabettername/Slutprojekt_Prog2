package game2_1;

import game2_1.events.*;
import game2_1.internet.Client;
import utility.Bounds2;
import utility.Debug;

import processing.core.PApplet;
import processing.core.PGraphics;

public class Application {
    public final Bounds2 Bounds;
    public final int WindowW;
    public final int WindowH;

    GameState state;

    private final PAppletImpl window;
    private final Thread windowThread;

    WindowLogic currentLogic;

    public Application(int width, int height) {
        Bounds = new Bounds2(0, 0, width, height);
        WindowW = width;
        WindowH = height;

        window = new PAppletImpl();
        windowThread = new Thread(window);
    }

    public void init() {
        windowThread.start();
        while (window.g == null)
            Thread.onSpinWait();
//        size(600, 600);
//
//        currentGameState = new GameState();
//        currentGameState.players.add(new Player());
//
//        try {
//            client = new Client("Client Thread", InetAddress.getByName("localhost"), 0);
//        } catch (SocketException | UnknownHostException e) {
//            Debug.logError(e);
//            exit();
//        }
//
//        client.Start();
//
//
//        //        textAlign(LEFT, TOP);
    }

    public void setLogic(WindowLogic logic) {
        currentLogic = logic;
    }


    public float getFrameRate() {
        return window.frameRate;
    }

    public PGraphics createBuffer(int width, int height) {
        return window.createGraphics(width, height);
    }

    private class PAppletImpl extends PApplet implements Runnable {

        @Override
        public void run() {
            PApplet.runSketch(new String[]{""}, window);
        }

        @Override
        public void settings() {
            size(WindowW, WindowH);
            registerMethod("dispose", this);
        }

        @Override
        public void setup() {
            g.textAlign(CENTER, CENTER);
            colorMode(RGB, 1);
        }

        @Override
        public void draw() {
            if (currentLogic != null) {
                currentLogic.render(this.g);
            } else {

                background(0);
                text("no Logic loaded\n" + frameCount, 0, 0, width, height);
            }
        }


        @Override
        public void dispose() { // Called before PApplet closes, can be used to detect crashes.
            Debug.closeLog();
        }


        //region Input Events
        private void keyEvent(KeyEvent event) {
            if (currentLogic != null)
                currentLogic.keyEvent(event);
        }
        private void mouseEvent(MouseEvent event) {
            if (currentLogic != null)
                currentLogic.mouseEvent(event);
        }


        @Override
        public void keyPressed(processing.event.KeyEvent event) {
            if (event.getKey() != CODED)
                keyEvent(new KeyEvent(KeyEventType.KeyPressed, event.getKey()));
            else
                keyEvent(new KeyEvent(KeyEventType.KeyPressed, event.getKeyCode()));
        }

        @Override
        public void keyReleased(processing.event.KeyEvent event) {
            if (event.getKey() != CODED)
                keyEvent(new KeyEvent(KeyEventType.KeyReleased, event.getKey()));
            else
                keyEvent(new KeyEvent(KeyEventType.KeyReleased, event.getKeyCode()));
        }

        @Override
        public void keyTyped(processing.event.KeyEvent event) {
            if (event.getKey() != CODED)
                keyEvent(new KeyEvent(KeyEventType.KeyTyped, event.getKey()));
            else
                keyEvent(new KeyEvent(KeyEventType.KeyTyped, event.getKeyCode()));
        }

        @Override
        public void mouseMoved(processing.event.MouseEvent event) {
            mouseEvent(new MouseEvent(MouseEventType.MouseMoved, event.getX(), event.getY()));
        }

        @Override
        public void mouseDragged(processing.event.MouseEvent event) {
            mouseEvent(new MouseEvent(MouseEventType.MouseDragged, event.getX(), event.getY(), getMouseButton(event)));
        }

        @Override
        public void mouseClicked(processing.event.MouseEvent event) {
            mouseEvent(new MouseEvent(MouseEventType.MouseButtonClicked, event.getX(), event.getY(), getMouseButton(event)));
        }

        @Override
        public void mousePressed(processing.event.MouseEvent event) {
            mouseEvent(new MouseEvent(MouseEventType.MouseButtonPressed, event.getX(), event.getY(), getMouseButton(event)));
        }

        @Override
        public void mouseReleased(processing.event.MouseEvent event) {
            mouseEvent(new MouseEvent(MouseEventType.MouseButtonReleased, event.getX(), event.getY(), getMouseButton(event)));
        }

        @Override
        public void mouseWheel(processing.event.MouseEvent event) {
            mouseEvent(new MouseEvent(MouseEventType.MouseWheel, event.getX(), event.getY(), event.getCount()));
        }
        //endregion

        private int getMouseButton(processing.event.MouseEvent event) {
            return ((java.awt.event.MouseEvent) event.getNative()).getButton();
        }
    }
}
