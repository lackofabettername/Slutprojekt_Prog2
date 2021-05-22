package game2_1;

import game2_1.events.KeyEvent;
import game2_1.events.MouseEvent;

import processing.core.PGraphics;

public interface WindowLogic {
    void render(PGraphics g);

    void onKeyEvent(KeyEvent event);
    void onMouseEvent(MouseEvent event);
}
