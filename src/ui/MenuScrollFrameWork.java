package ui;

import utility.Bounds2;
import utility.Vector2;

import processing.core.PGraphics;

//TODO
public class MenuScrollFrameWork extends MenuFramework {
    Vector2 minElementSize;

    boolean scrollBarX;
    boolean scrollBarY;

    //region Constructors
    public MenuScrollFrameWork(String name, UIListener parent, Vector2 minElementSize, Bounds2 bounds) {
        super(name, parent, bounds);

        this.minElementSize = minElementSize;
    }
    public MenuScrollFrameWork(String name, UIListener parent, Bounds2 bounds) {
        this(name, parent, null, bounds);
    }
    public MenuScrollFrameWork(String name, UIListener parent, float x, float y, float w, float h) {
        this(name, parent, new Bounds2(x, y, w, h));
    }
    public MenuScrollFrameWork(String name, UIListener parent) {
        this(name, parent, null);
    }
    public MenuScrollFrameWork(String name, float x, float y, float w, float h) {
        this(name, null, new Bounds2(x, y, w, h));
    }
    public MenuScrollFrameWork(String name, Bounds2 bounds) {
        this(name, null, bounds);
    }
    public MenuScrollFrameWork(String name) {
        this(name, null, null);
    }
    //endregion

    @Override
    public void setParent(UIListener parent) {
        super.setParent(parent);
    }
    @Override
    public void addMenuObject(MenuObject menuObject) {
        super.addMenuObject(menuObject);
    }
    @Override
    public void addMenuObject(MenuObject menuObject, int axis) {
        super.addMenuObject(menuObject, axis);
    }
    @Override
    public void addMenuObject(MenuObject menuObject, int x, int y) {
        super.addMenuObject(menuObject, x, y);
    }
    @Override
    public void addMenuObject(MenuObject menuObject, int x, int y, int w, int h) {
        super.addMenuObject(menuObject, x, y, w, h);
    }

    @Override
    public void removeMenuObject(MenuObject menuObject) {
        super.removeMenuObject(menuObject);
    }

    @Override
    public void fitElements(float innerPadding, float outerPadding) {
        if ()

        super.fitElements(innerPadding, outerPadding);
    }

    @Override
    public void onRenderElement(PGraphics g) {
        super.onRenderElement(g);
    }

    @Override
    public void setBounds(Bounds2 bounds) {
        super.setBounds(bounds);
    }
}
