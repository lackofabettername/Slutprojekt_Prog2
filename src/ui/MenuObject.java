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

    /**
     * If the MenuObject implementing this class has some kind of animation, call this when it starts.
     */
    protected void animationStart() {
        if (parent instanceof MenuFramework framework) {
            framework.animationStart();
        }
    }

    /**
     * If the MenuObject implementing this class has some kind of animation, call this when it stops.
     */
    protected void animationStop() {
        if (parent instanceof MenuFramework framework) {
            framework.animationStop();
        }
    }

    /**
     * Render the bounds of this menuObject.
     */
    protected void renderBounds(PGraphics g) {
        if (renderBounds) {
            g.stroke(1);
            g.fill(backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue(), backgroundColor.getAlpha());
            g.rect(bounds.x, bounds.y, bounds.w, bounds.h);
        }
    }

    public abstract void onRenderElement(PGraphics g);

    /**
     * @param event The given input event.
     * @param translation If the event is a {@link MouseEvent} this translation should
     *                    be added to the {@link MouseEvent#mouseX} and {@link MouseEvent#mouseY}
     * @return true if the event affected this menuobject in some way. False if it did not.
     */
    public boolean handleEvent(InputEvent event, Vector2 translation) {
        if (event instanceof MouseEvent mouseEvent)
            return bounds.inBounds(Vector2.sub(mouseEvent.mouseX, mouseEvent.mouseY, translation));
        return false;
    }

    @Override
    public String toString() {
        return "MenuObject{" +
                "name='" + name + '\'' +
                '}';
    }
}
