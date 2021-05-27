package ui;


import game2_1.events.*;
import utility.*;

import processing.core.PGraphics;

import java.io.Serializable;

public class MenuTextField extends MenuObject implements Serializable {
    public String text;

    private final MenuText menuText;
    boolean hovered;
    boolean active;

    public MenuTextField(String name) {
        this(name, null, 10);
    }
    public MenuTextField(String name, float textSize) {
        this(name, null, textSize);
    }
    public MenuTextField(String name, Bounds2 bounds) {
        this(name, bounds, 10);
    }
    public MenuTextField(String name, Bounds2 bounds, float textSize) {
        super(name);

        menuText = new MenuText("", textSize);
        text = "";

        setBounds(bounds);

        foregroundColor = new Color(ColorMode.RGBA, 1, 1, 1, 1);
        backgroundColor = new Color(ColorMode.RGBA, 0.2f, 0.2f, 0.2f, 0);
    }

    @Override
    public void setBounds(Bounds2 bounds) {
        menuText.setBounds(bounds);
        super.setBounds(bounds);
    }

    @Override
    public void onRenderElement(PGraphics g) {
        foregroundColor.setAlpha(active && text.length() == 0 ? 0.2f : 1);

        menuText.foregroundColor = foregroundColor;
        menuText.backgroundColor = backgroundColor;

        String displayText = (text.length() > 0 ? text : name);
        menuText.text = displayText + (active && System.currentTimeMillis() / 500 % 2 == 0 ? "_" : "");
        menuText.onRenderElement(g);

//        if (active && System.currentTimeMillis() / 500 % 2 == 0) {
//            displayText += "_";
//            float x = 0;
//            float y = 0;
//            for (char c : displayText.toCharArray()) {
//                x += g.textWidth(c + "");
//                if (x >= bounds.w) {
//                    x = 0;
//                    y += g.textAscent();
//
//                    if (y >= bounds.h)
//                        break;
//                }
//            }
//
//            if (y < bounds.h) {
//                g.menuText(
//                        "_",
//                        bounds.x + bounds.w / 2 + x / 2,
//                        bounds.y + bounds.h / 2 + y / 2
//                );
//            }
//    }
    }

    @Override
    public boolean handleEvent(InputEvent event, Vector2 translation) {

        if (event instanceof MouseEvent mouseEvent) {

            if (bounds.inBounds(mouseEvent.mouseX - translation.x, mouseEvent.mouseY - translation.y)) {
                backgroundColor.setAlpha(1);

                if (mouseEvent.type == MouseEventType.MOUSE_BUTTON_PRESSED) {
                    if (!active) animationStart();
                    active = true;
                }

                return hovered = true;
            } else if (active && mouseEvent.type != MouseEventType.MOUSE_BUTTON_PRESSED) {
                return true;
            }
        } else if (active) {

            KeyEvent keyEvent = (KeyEvent) event;
            if (keyEvent.TYPE == KeyEventType.KEY_PRESSED) {
                if (keyEvent.KEY == 8) {//backspace
                    text = text.substring(0, Math.max(text.length() - 1, 0));
                } else if (keyEvent.KEY == 10) {//enter
                    animationStop();
                    active = false;
                    parent.uiEvent(this);
                } else if (keyEvent.KEY == 65535) {//CODED, not supported yet
                    //todo
                } else {
                    text += keyEvent.KEY;
                }
            }
            return true;
        }

        backgroundColor.setAlpha(0);
        if (active) animationStop();
        active = false;
        return hovered = false;
    }
}
