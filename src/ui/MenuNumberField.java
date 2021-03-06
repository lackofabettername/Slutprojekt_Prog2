package ui;

import game2_1.events.*;
import utility.*;

import processing.core.PGraphics;

import java.io.Serializable;

//optimize: extend MenuTextField for cleaner code?
public class MenuNumberField extends MenuObject implements Serializable {
    public float value;
    private final MenuText menuText;
    boolean hovered;
    boolean active;

    private transient float previousMouseX = -1;
    public float stepSize;
    public float resolution;

    private float accumulator;

    private transient boolean userPlacedPeriod;
    private transient boolean empty = true;

    public MenuNumberField(String name) {
        this(name, null, 1, 1, 10);
    }
    public MenuNumberField(String name, float stepSize) {
        this(name, null, stepSize, 1, 10);
    }
    public MenuNumberField(String name, float stepSize, float textSize) {
        this(name, null, stepSize, 1, textSize);
    }
    public MenuNumberField(String name, Bounds2 bounds) {
        this(name, bounds, 1, 1, 10);
    }
    public MenuNumberField(String name, Bounds2 bounds, float stepSize, float textSize) {
        this(name, bounds, stepSize, 1, textSize);
    }
    public MenuNumberField(String name, Bounds2 bounds, float stepSize, float resolution, float textSize) {
        super(name);

        menuText = new MenuText("", textSize);
        value = 0;

        setBounds(bounds);
        this.stepSize = stepSize;
        this.resolution = resolution;

        foregroundColor = new Color(ColorMode.RGBA, 1, 1, 1, 1);
        backgroundColor = new Color(ColorMode.RGBA, 0.2f, 0.2f, 0.2f, 0);

    }

    @Override
    public void setBounds(Bounds2 bounds) {
        menuText.setBounds(bounds);
        super.setBounds(bounds);
    }

    private String getText() {
        String text;
        if (stepSize % 1 != 0) {
            int decimals = (int) Math.ceil(-Math.log10(stepSize % 1));
            text = String.format("%" + 1 + "." + decimals + "f", Math.round(value / stepSize) * stepSize);
        } else {
            text = "" + value;
        }
        if (text.endsWith(".0")) {
            if (userPlacedPeriod)
                return text.substring(0, text.length() - 1);
            else
                return text.substring(0, text.length() - 2);
        }

        return text;
    }

    @Override
    public void onRenderElement(PGraphics g) {
        foregroundColor.setAlpha(active && empty ? 0.2f : 1);

        menuText.foregroundColor = foregroundColor;
        menuText.backgroundColor = backgroundColor;

        String displayText = (getText().length() > 0 ? getText() : name);
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
//                g.text(
//                        "_",
//                        bounds.x + bounds.w / 2 + x / 2,
//                        bounds.y + bounds.h / 2 + y / 2
//                );
//            }
//    }
    }

    @Override
    public boolean handleEvent(InputEvent event, Vector2 translation) {//TODO fixed period logic

        if (event instanceof MouseEvent mouseEvent) {
            if (active && mouseEvent.type == MouseEventType.MOUSE_DRAGGED) {
                if (previousMouseX != -1) {
                    accumulator += (mouseEvent.mouseX - previousMouseX) * resolution;
                    for (; accumulator >= 1; --accumulator)
                        value += stepSize;
                    for (; accumulator <= -1; ++accumulator)
                        value -= stepSize;


                    empty = false;
                    parent.uiEvent(this);
                }
                previousMouseX = mouseEvent.mouseX;
            }

            if (bounds.inBounds(mouseEvent.mouseX - translation.x, mouseEvent.mouseY - translation.y)) {
                backgroundColor.setAlpha(1);

                if (mouseEvent.type == MouseEventType.MOUSE_BUTTON_PRESSED) {
                    if (!active) animationStart();
                    active = true;
                    previousMouseX = mouseEvent.mouseX;
                }

                return hovered = true;
            } else if (active && mouseEvent.type != MouseEventType.MOUSE_BUTTON_PRESSED) {
                return true;
            }
        } else if (active) {

            KeyEvent keyEvent = (KeyEvent) event;
            if (keyEvent.TYPE == KeyEventType.KEY_PRESSED) {
                String text = getText();

                if (keyEvent.KEY == 8) {//backspace
                    if (text.equals("0"))
                        empty = true;

                    text = text.substring(0, Math.max(text.length() - 1, 0));
                    userPlacedPeriod = false;
                    if (text.length() == 0)
                        text = "0";
                } else if (keyEvent.KEY == 10) {//enter
                    animationStop();
                    active = false;
                    parent.uiEvent(this);
                } else if (keyEvent.KEY == 65535) {//CODED, not supported yet
                    //todo
                } else {
                    //if (userPlacedPeriod && Character.isDigit(keyEvent.KEY))
                    //    text += '.';

                    text += keyEvent.KEY;
                    if (keyEvent.KEY == '.')
                        userPlacedPeriod = true;

                    empty = false;
                }

                System.out.println(text);

                try {
                    value = Float.parseFloat(text);
                } catch (NumberFormatException ignored) {
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
