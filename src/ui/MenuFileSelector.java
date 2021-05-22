package ui;

import game2_1.events.InputEvent;
import game2_1.events.MouseEvent;
import utility.Bounds2;
import utility.Vector2;

import processing.core.PGraphics;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;

public class MenuFileSelector extends MenuObject {
    File folder;
    File[] files;

    MenuFramework framework;
    float textSize;

    public MenuFileSelector (String name, String filepath) {
        this(name, filepath, pathname -> true);
    }
    public MenuFileSelector(String name, String filepath, FileFilter filter) {
        super(name);

        folder = new File(filepath);
        if (!folder.isDirectory())
            throw new IllegalArgumentException("Filepath must lead to a folder");

        files = folder.listFiles(filter);
        //noinspection ConstantConditions
        Arrays.sort(files);

        framework = new MenuFramework(name);

        textSize = 20;
        for (int i = 0; i < files.length; ++i) {
            framework.addMenuObject(new MenuButton(files[i].getName(), textSize), 1);
        }
    }

    @Override
    public void setBounds(Bounds2 bounds) {
        super.setBounds(bounds);
        framework.setBounds(bounds);
        framework.fitElements(0);
    }

    @Override
    public void setParent(UIListener parent) {
        framework.setParent(parent);
    }

    @Override
    public void onRenderElement(PGraphics g) {
        renderBounds(g);

        g.fill(1);

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
}
