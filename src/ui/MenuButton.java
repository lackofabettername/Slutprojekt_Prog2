package ui;

import game2_1.events.InputEvent;
import game2_1.events.MouseEvent;
import game2_1.events.MouseEventType;
import utility.*;

import processing.core.PGraphics;

import java.io.Serializable;

public class MenuButton extends MenuObject implements Serializable {
    MenuText text;

    boolean hovered;
    boolean pressed;


    public MenuButton(String name) {
        this(name, null, 10);
    }
    public MenuButton(String name, float textSize) {
        this(name, null, textSize);
    }
    public MenuButton(String name, Bounds2 bounds) {
        this(name, bounds, 10);
    }
    public MenuButton(String name, Bounds2 bounds, float textSize) {
        super(name);
        this.text = new MenuText(name, textSize);
        setBounds(bounds);
        //this.textSize = textSize;

        foregroundColor = new Color(ColorMode.RGBA, 1, 1, 1, 1);
        backgroundColor = new Color(ColorMode.RGBA, 0.2f, 0.2f, 0.2f, 0);
    }

    @Override
    public void setBounds(Bounds2 bounds) {
        text.setBounds(bounds);
        super.setBounds(bounds);
    }

    @Override
    public void onRenderElement(PGraphics g) {
        text.foregroundColor = foregroundColor;
        text.backgroundColor = backgroundColor;

        text.onRenderElement(g);
    }

    @Override
    public boolean handleEvent(InputEvent event, Vector2 translation) {
        if (event instanceof MouseEvent mouseEvent) {
            if (bounds.inBounds(mouseEvent.MouseX - translation.x, mouseEvent.MouseY - translation.y)) {
                switch (mouseEvent.Type) {
                    case MOUSE_BUTTON_PRESSED:
                        pressed = true;
                        backgroundColor.setLightness(0.4f);
                        parent.uiEvent(this);
                    case MOUSE_MOVED, MOUSE_DRAGGED:
                        backgroundColor.setAlpha(1);
                        return hovered = true;

                    case MOUSE_BUTTON_RELEASED:
                        pressed = false;
                        backgroundColor.setLightness(0.2f);
                        return true;

                    case MOUSE_BUTTON_CLICKED:
                        return true;
                }
            } else if (pressed) {
                if (mouseEvent.Type == MouseEventType.MOUSE_DRAGGED)
                    return true;
            }
        }

        backgroundColor.setLightness(0.2f);
        backgroundColor.setAlpha(0);
        return hovered = pressed = false;
    }
}
