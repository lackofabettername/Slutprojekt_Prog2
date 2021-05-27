package game2_1;

import game2_1.events.KeyEvent;
import game2_1.events.MouseEvent;

import processing.core.PGraphics;

/**
 * Used by {@link game2_1.Application} to handle all input events and rendering.
 * The application is just a window, the WindowLogic is the actual program that runs in the Application.
 * @see Application
 */
public interface WindowLogic {
    /**
     * Called when the window renders the next frame
     * @see PGraphics
     */
    void render(PGraphics g);

    /**
     * Called when the window registers a KeyEvent
     * @see KeyEvent
     */
    void onKeyEvent(KeyEvent event);
    /**
     * Called when the window registers a MouseEvent
     * @see MouseEvent
     */
    void onMouseEvent(MouseEvent event);

    /**
     * Called when the window closes.
     */
    void onExit();
}
