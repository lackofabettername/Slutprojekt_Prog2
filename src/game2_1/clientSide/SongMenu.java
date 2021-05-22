package game2_1.clientSide;

import game2_1.Application;
import game2_1.WindowLogic;
import game2_1.events.KeyEvent;
import game2_1.events.MouseEvent;
import ui.*;
import utility.Bounds2;
import utility.Debug;

import processing.core.PGraphics;

import java.io.File;

public class SongMenu implements WindowLogic, UIListener {

    private final UI ui;
    private final MenuFileSelector fslSongSelector;
    private final MenuText txtSelectedSong;
    private final MenuText txtStatus;
    private final MenuButton btnReady;

    public SongMenu(Application window) {
        MenuFramework framework = new MenuFramework("FrameWork", this, window.Bounds);

        int buff = 20;

        //region Songs
        fslSongSelector = new MenuFileSelector("Songs", "music", File::isDirectory, new Bounds2(buff, buff, window.WindowH - buff * 2, window.WindowH - buff * 2));

        fslSongSelector.renderBounds = true;
        framework.addMenuObject(fslSongSelector);
        //endregion

        //region Settings
        MenuFramework settingsPane = new MenuFramework("settings", window.WindowH, buff, window.WindowW - window.WindowH - buff, window.WindowH - buff * 2);

        settingsPane.addMenuObject(txtSelectedSong = new MenuText("Selected Song", 24), 1);
        txtSelectedSong.renderBounds = false;
        settingsPane.addMenuObject(txtStatus = new MenuText("Status", 24), 1);
        txtStatus.renderBounds = false;

        settingsPane.addMenuObject(btnReady = new MenuButton("Ready", 24), 0, 4, 1, 4);

        settingsPane.fitElements(0, 0);
        settingsPane.renderBounds = true;
        framework.addMenuObject(settingsPane);
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
        ui.handleEvent(event);
    }
    @Override
    public void onMouseEvent(MouseEvent event) {
        ui.handleEvent(event);
    }

    @Override
    public void uiEvent(MenuObject caller) {
        if (caller.parent == fslSongSelector) {
            Debug.log("ajdiso");
        }
    }
}
