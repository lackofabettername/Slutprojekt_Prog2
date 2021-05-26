package game2_1;

import game2_1.events.*;
import utility.Bounds2;
import utility.Debug;

import processing.core.PApplet;
import processing.core.PGraphics;

import java.util.EmptyStackException;
import java.util.Stack;

/**
 * A wrapper for the PApplet class. Creating an instance of this class and starting it with init() will create a new window.
 * Use setLogic() or pushLogic() to set what this window should render.
 * @see PApplet
 * @see WindowLogic
 */
public final class Application {
    /**
     * The window's Bounds.
     */
    public final Bounds2 BOUNDS;
    /**
     * The window's Width.
     */
    public final int WINDOW_W;
    /**
     * The window's Height.
     */
    public final int WINDOW_H;

    private volatile boolean running;

    private final PAppletImpl window;
    private final Thread windowThread;

    private volatile WindowLogic currentLogic;
    private final Stack<WindowLogic> logicStack;

    /**
     * Create a new window.
     * @param width The windows Width.
     * @param height The windows Height.
     */
    public Application(int width, int height) {
        BOUNDS = new Bounds2(0, 0, width, height);
        WINDOW_W = width;
        WINDOW_H = height;

        window = new PAppletImpl();
        windowThread = new Thread(window);

        logicStack = new Stack<>();
    }

    /**
     * Start the window. This method locks until the window has started.
     */
    public void init() {
        windowThread.start();
        while (window.g == null)
            Thread.onSpinWait();

        running = true;
    }

    /**
     * @return true if the window is running.
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Set the window's logic. This ignores the logicStack.
     * @param logic The logic the window should use.
     */
    public void setLogic(WindowLogic logic) {
        currentLogic = logic;
    }
    /**
     * Set the window's logic. This pushes the previous WindowLogic to the logicStack.
     * @param logic The logic the window should use.
     */
    public void pushLogic(WindowLogic logic) {
        logicStack.push(currentLogic);
        currentLogic = logic;
    }
    /**
     * Set the window's logic to the previous one.
     * @throws EmptyStackException If the stack is empty.
     */
    public void popLogic() throws EmptyStackException {
        currentLogic = logicStack.pop();
    }

    /**
     * @return The window's framerate.
     */
    public float getFrameRate() {
        return window.frameRate;
    }

    /**
     * Creates a new PGraphics with the same renderer as the window (PGraphics2D).
     * @param width The new PGraphics' width.
     * @param height The new PGraphics' height.
     * @return A new PGraphics with the given with and height.
     */
    public PGraphics createBuffer(int width, int height) {
        return window.createGraphics(width, height);
    }

    /**
     * @return the PApplet this class wraps
     */
    public PApplet getApplet() {
        return window;
    }

    private class PAppletImpl extends PApplet implements Runnable {//TODO: Make the renderer customizable? choose between default, 2d, 3d and pdf?

        @Override
        public void run() {
            PApplet.runSketch(new String[]{""}, window);
        }

        @Override
        public void settings() {
            size(WINDOW_W, WINDOW_H, "processing.opengl.PGraphics2D");
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
        public void dispose() {// Called before PApplet closes, can be used to detect crashes.
            Debug.closeLog();
            currentLogic.onExit();

            running = false;
        }


        //region Input Events
        private void keyEvent(KeyEvent event) {
            if (currentLogic != null)
                currentLogic.onKeyEvent(event);
        }
        private void mouseEvent(MouseEvent event) {
            if (currentLogic != null)
                currentLogic.onMouseEvent(event);
        }

        @Override
        public void keyPressed(processing.event.KeyEvent event) {
            if (event.getKey() != CODED)
                keyEvent(new KeyEvent(KeyEventType.KEY_PRESSED, event.getKey()));
            else
                keyEvent(new KeyEvent(KeyEventType.KEY_PRESSED, event.getKeyCode()));
        }

        @Override
        public void keyReleased(processing.event.KeyEvent event) {
            if (event.getKey() != CODED)
                keyEvent(new KeyEvent(KeyEventType.KEY_RELEASED, event.getKey()));
            else
                keyEvent(new KeyEvent(KeyEventType.KEY_RELEASED, event.getKeyCode()));
        }

        @Override
        public void keyTyped(processing.event.KeyEvent event) {
            if (event.getKey() != CODED)
                keyEvent(new KeyEvent(KeyEventType.KEY_TYPED, event.getKey()));
            else
                keyEvent(new KeyEvent(KeyEventType.KEY_TYPED, event.getKeyCode()));
        }

        @Override
        public void mouseMoved(processing.event.MouseEvent event) {
            mouseEvent(new MouseEvent(MouseEventType.MOUSE_MOVED, event.getX(), event.getY()));
        }

        @Override
        public void mouseDragged(processing.event.MouseEvent event) {
            mouseEvent(new MouseEvent(MouseEventType.MOUSE_DRAGGED, event.getX(), event.getY(), getMouseButton(event)));
        }

        @Override
        public void mouseClicked(processing.event.MouseEvent event) {
            mouseEvent(new MouseEvent(MouseEventType.MOUSE_BUTTON_CLICKED, event.getX(), event.getY(), getMouseButton(event)));
        }

        @Override
        public void mousePressed(processing.event.MouseEvent event) {
            mouseEvent(new MouseEvent(MouseEventType.MOUSE_BUTTON_PRESSED, event.getX(), event.getY(), getMouseButton(event)));
        }

        @Override
        public void mouseReleased(processing.event.MouseEvent event) {
            mouseEvent(new MouseEvent(MouseEventType.MOUSE_BUTTON_RELEASED, event.getX(), event.getY(), getMouseButton(event)));
        }

        @Override
        public void mouseWheel(processing.event.MouseEvent event) {
            mouseEvent(new MouseEvent(MouseEventType.MOUSE_WHEEL, event.getX(), event.getY(), event.getCount()));
        }
        //endregion

        private int getMouseButton(processing.event.MouseEvent event) {
            if (event.getNative() instanceof java.awt.event.MouseEvent mouseEvent) //Standard renderer
                return mouseEvent.getButton();
            else if (event.getNative() instanceof com.jogamp.newt.event.MouseEvent mouseEvent) //processing.opengl.PGraphics2D renderer
                return mouseEvent.getButton();
            else
                return -1;
        }
    }
}
