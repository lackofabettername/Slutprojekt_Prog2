package ui;


import game2_1.Application;
import game2_1.events.InputEvent;
import utility.Vector2;

import processing.core.PConstants;
import processing.core.PGraphics;

/**
 * Used to display ui in applications or windowLogics.
 * This wraps a {@link MenuFramework} using an offscreen buffer to only render
 * it when needed and thereby improving performance slightly.
 * @see MenuFramework
 */
public class UI {
    private final MenuFramework framework;
    private final transient PGraphics buffer;
    private transient boolean update = true;

    public UI(Application parent, MenuFramework framework) {
        this.framework = framework;

        buffer = parent.createBuffer(parent.WINDOW_W, parent.WINDOW_H);
        buffer.beginDraw();
        buffer.colorMode(PConstants.RGB, 1);
        buffer.textAlign(PConstants.CENTER, PConstants.CENTER);
        buffer.endDraw();

        //menu.addMenuObject(new MenuFramework(new Bounds2(10, 10, 50, 30)));
    }

    /**
     * Recursively searches through the ui after the MenuObject with the given name.
     * @param name The name of the MenuObject
     * @return The menuObject with the given name or null if it wasn't found.
     */
    public MenuObject getMenuObject(String name) {
        return framework.getMenuObject(name);
    }

    /**
     * Render the UI. This uses an offscreen buffer that is only updated when the ui is,
     * this may cause lagspikes if the UI is large and complicated.
     * @param g the PGraphics to draw the UI on.
     */
    public void onRender(PGraphics g) {
        //Only update when needed
        if (update || framework.animationActive()) {
            buffer.beginDraw();
            buffer.translate(-framework.bounds.x, -framework.bounds.y);
            buffer.background(0);

            framework.onRenderElement(buffer);

            buffer.endDraw();

            update = false;
        }

        g.image(buffer, framework.bounds.x, framework.bounds.y);
    }

    /**
     * Tell the ui to handle the given inputevent
     * @return true if the event affected the ui in some way.
     * If you press a button or type in a text field this return true.
     * If you move you're mouse between buttons it returns false.
     */
    public boolean handleEvent(InputEvent event) {
        if (framework.handleEvent(event, new Vector2())) {
            update = true;
            return true;
        } else {
            return false;
        }
    }
}
