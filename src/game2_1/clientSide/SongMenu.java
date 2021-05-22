package game2_1.clientSide;

import game2_1.Application;
import game2_1.WindowLogic;
import game2_1.events.KeyEvent;
import game2_1.events.MouseEvent;
import ui.*;
import utility.Bounds2;

import processing.core.PGraphics;

import java.io.File;

public class SongMenu implements WindowLogic, UIListener {

    RenderLogic parent;

    //region UI
    private final UI ui;
    private final MenuFileSelector fslSongSelector;
    private final MenuText txtSelectedSong;
    private final MenuText txtStatus1;
    private final MenuText txtStatus2;
    private final MenuButton btnReady;
    //endregion

    public SongMenu(Application window, RenderLogic parent) {
        this.parent = parent;

        //region UI
        MenuFramework framework = new MenuFramework("FrameWork", this, window.Bounds);

        int buff = 20;

        //region Songs
        fslSongSelector = new MenuFileSelector("SongSelector", "music", File::isDirectory, new Bounds2(buff, buff, window.WindowH - buff * 2, window.WindowH - buff * 2));

        fslSongSelector.renderBounds = true;
        framework.addMenuObject(fslSongSelector);
        //endregion

        //region Settings
        MenuFramework settingsPane = new MenuFramework("settings", window.WindowH, buff, window.WindowW - window.WindowH - buff, window.WindowH - buff * 2);

        settingsPane.addMenuObject(txtSelectedSong = new MenuText("Selected Song", 24), 1);
        txtSelectedSong.renderBounds = false;
        txtSelectedSong.text = "Select Song";

        settingsPane.addMenuObject(txtStatus1 = new MenuText("Status1", 20), 1);
        txtStatus1.renderBounds = false;
        txtStatus1.text = "Selecting Song";
        settingsPane.addMenuObject(txtStatus2 = new MenuText("Status2", 20), 1);
        txtStatus2.renderBounds = false;
        txtStatus2.text = "asds";

        settingsPane.addMenuObject(btnReady = new MenuButton("Ready", 24), 0, 4, 1, 4);

        settingsPane.fitElements(0, 0);
        settingsPane.renderBounds = true;
        framework.addMenuObject(settingsPane);
        //endregion

        ui = new UI(window, framework);
        //endregion
    }

    @Override
    public void render(PGraphics g) {
        g.background(0);

        txtStatus2.text = String.format("%d/%d players ready.", 0, parent.clientGameState.players.size());

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
            txtSelectedSong.text = caller.name;
        } else if (caller == btnReady) {
            txtStatus1.text = "Ready";
        }
    }
}
