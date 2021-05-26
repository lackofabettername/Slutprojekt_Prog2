package ui;



import game2_1.events.InputEvent;
import game2_1.events.MouseEvent;
import utility.Bounds2;
import utility.Color;
import utility.Vector2;

import processing.core.PGraphics;

import java.io.Serializable;

public abstract class MenuObject implements Serializable {

    public final String name;
    public UIListener parent;
    public Bounds2 bounds;

    public boolean renderBounds = true;
    public Color foregroundColor;
    public Color backgroundColor;

    public MenuObject(String name) {
        this.name = name;
    }

    public MenuObject(String name, MenuFramework parent, Bounds2 bounds) {
        this.name = name;
        this.parent = parent;
        setBounds(bounds);
    }

    public void setBounds(Bounds2 bounds) {
        this.bounds = bounds;
    }

    public void setParent(UIListener parent) {
        this.parent = parent;
    }

    protected void animationStart() {
        if (parent instanceof MenuFramework framework) {
            framework.animationStart();
        }
    }

    protected void animationStop() {
        if (parent instanceof MenuFramework framework) {
            framework.animationStop();
        }
    }

    protected void renderBounds(PGraphics g) {
        if (renderBounds) {
            g.stroke(1);
            g.fill(backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue(), backgroundColor.getAlpha());
            g.rect(bounds.x, bounds.y, bounds.w, bounds.h);
        }
    }

    public abstract void onRenderElement(PGraphics g);

    public boolean handleEvent(InputEvent event, Vector2 translation) {
        if (event instanceof MouseEvent mouseEvent)
            return bounds.inBounds(Vector2.sub(mouseEvent.MouseX, mouseEvent.MouseY, translation));
        return false;
    }

    @Override
    public String toString() {
        return "MenuObject{" +
                "name='" + name + '\'' +
                '}';
    }
}
