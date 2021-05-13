package game2_1;

import game2_1.events.KeyEvent;
import game2_1.events.MouseEvent;

import processing.core.PGraphics;

public interface WindowLogic {
    void render(PGraphics g);

    void keyEvent(KeyEvent event);
    void mouseEvent(MouseEvent event);
}
