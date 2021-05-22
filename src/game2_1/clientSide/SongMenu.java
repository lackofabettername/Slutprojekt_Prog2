package game2_1.clientSide;

import game2_1.Application;
import game2_1.WindowLogic;
import game2_1.events.KeyEvent;
import game2_1.events.MouseEvent;
import ui.*;
import utility.Bounds2;
import utility.Vector2;

import processing.core.PGraphics;

public class SongMenu implements WindowLogic, UIListener {

    private final UI ui;

    public SongMenu(Application window) {
        MenuFramework framework = new MenuFramework("FrameWork", this, window.Bounds);

        int buff = 20;

        //region Songs
        MenuScrollFrameWork songPane = new MenuScrollFrameWork("Songs", null, new Vector2(100), new Bounds2(buff, buff, window.WindowH - buff * 2, window.WindowH - buff * 2));

        for (int i = 0; i < 15; ++i) {
            songPane.addMenuObject(new MenuButton("" + i), 1);
        }

        songPane.fitElements(0);

        framework.addMenuObject(songPane);
        songPane.renderBounds = true;
        //endregion

        //region Todo
        MenuFramework todoPane = new MenuFramework("TODO", window.WindowH, buff, window.WindowW - window.WindowH - buff, window.WindowH - buff * 2);
        framework.addMenuObject(todoPane);
        todoPane.renderBounds = true;
        //endregion

        ui = new UI(window, framework);
    }

    @Override
    public void render(PGraphics g) {
        g.background(0);

        ui.onRender(g);
    }
    @Override
    public void onKeyEvent(KeyEvent event) {

    }
    @Override
    public void onMouseEvent(MouseEvent event) {

    }

    @Override
    public void uiEvent(MenuObject caller) {

    }
}
