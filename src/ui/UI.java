package ui;



import game2_1.Application;
import game2_1.events.InputEvent;
import utility.Vector2;

import processing.core.PConstants;
import processing.core.PGraphics;

public class UI {
    private final MenuFramework _framework;
    private final transient PGraphics buffer;
    private transient boolean update = true;

    public UI(Application parent, MenuFramework framework) {
        _framework = framework;

        buffer = parent.createBuffer(parent.WindowW, parent.WindowH);
        buffer.beginDraw();
        buffer.colorMode(PConstants.RGB, 1);
        buffer.textAlign(PConstants.CENTER, PConstants.CENTER);
        buffer.endDraw();

        //menu.addMenuObject(new MenuFramework(new Bounds2(10, 10, 50, 30)));
    }

    public MenuObject getMenuObject(String name) {
        return _framework.getMenuObject(name);
    }

    public void onRender(PGraphics g) {
        if (update || _framework.animationActive()) {
            buffer.beginDraw();
            buffer.translate(-_framework.bounds.x, -_framework.bounds.y);
            buffer.background(0);

            _framework.onRenderElement(buffer);

            buffer.endDraw();

            update = false;
        }

        g.image(buffer, _framework.bounds.x, _framework.bounds.y);
        //g.push();
        //_framework.onRenderElement(g);
        //g.pop();
    }

    public boolean handleEvent(InputEvent event) {
        if (_framework.handleEvent(event, new Vector2())) {
            update = true;
            return true;
        } else {
            return false;
        }
    }
}
