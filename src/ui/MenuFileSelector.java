package ui;

import game2_1.events.InputEvent;
import game2_1.events.MouseEvent;
import utility.Bounds2;
import utility.Color;
import utility.ColorMode;
import utility.Vector2;

import processing.core.PGraphics;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;

public class MenuFileSelector extends MenuObject implements UIListener {
    File folder;
    File[] files;

    MenuScrollFrameWork framework;
    float textSize;

    public MenuFileSelector(String name, String filepath) {
        this(name, filepath, pathname -> true, null);
    }
    public MenuFileSelector(String name, String filepath, FileFilter filter) {
        this(name, filepath, filter, null);
    }
    public MenuFileSelector(String name, String filepath, FileFilter filter, Bounds2 bounds) {
        super(name, null, bounds);

        folder = new File(filepath);
        if (!folder.isDirectory())
            throw new IllegalArgumentException("Filepath must lead to a folder");

        files = folder.listFiles(filter);
        if (files != null)
            Arrays.sort(files);

        framework = new MenuScrollFrameWork(name + " dummy framework", bounds, new Vector2(100));

        textSize = 20;
        for (int i = 0; i < files.length; ++i) {
            MenuButton btn = new MenuButton(files[i].getName(), textSize);
            framework.addMenuObject(btn, 1);
            btn.setParent(this);
        }

        framework.fitElements(0);

        foregroundColor = new Color(ColorMode.RGBA, 1, 1, 1, 1);
        backgroundColor = new Color(ColorMode.RGBA, 1, 1, 1, 0);
    }

    @Override
    public void setBounds(Bounds2 bounds) {
        super.setBounds(bounds);
        if (framework != null) {
            framework.setBounds(bounds);
            framework.fitElements(0);
        }
    }

    @Override
    public void setParent(UIListener parent) {
        this.parent = parent;
        framework.setParent(parent);
    }

    @Override
    public void onRenderElement(PGraphics g) {
        g.textSize(textSize);

        framework.onRenderElement(g);
    }

    @Override
    public boolean handleEvent(InputEvent event, Vector2 translation) {
        if (event instanceof MouseEvent) {
            return framework.handleEvent(event, translation);
        }
        return false;
    }

    @Override
    public void uiEvent(MenuObject caller) {
        framework.uiEvent(caller);
    }
}
