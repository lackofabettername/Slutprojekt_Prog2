package game2_1;

import game2_1.events.KeyEvent;
import game2_1.events.MouseEvent;

import processing.core.PGraphics;

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
