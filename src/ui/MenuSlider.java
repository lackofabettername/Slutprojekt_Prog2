package ui;



import game2_1.events.InputEvent;
import game2_1.events.MouseEvent;
import game2_1.events.MouseEventType;
import utility.*;

import processing.core.PGraphics;

import java.io.Serializable;

public class MenuSlider extends MenuObject implements Serializable {
    public final float Start, Stop;
    private float t;

    private boolean vertical;
    private float padding;
    private float r = 15;

    private boolean hovered, active;

    public Color activeColor;

    public MenuSlider(String name, float start, float stop) {
        super(name);
        foregroundColor = new Color(ColorMode.RGBA, 1, 1, 1, 1);

        this.Start = start;
        this.Stop = stop;
        t = 0.5f;

        //backgroundColor = new Color(ColorMode.RGBA, 0.2f, 0.2f, 0.2f, 1);
        foregroundColor = new Color(ColorMode.RGBA, 0.4f, 0.4f, 0.4f, 1);
        activeColor = new Color(ColorMode.RGBA, 0.6f, 0.6f, 0.6f, 1);
    }

    public float getValue() {
        return Start + t * (Stop - Start);
    }

    @Override
    public void onRenderElement(PGraphics g) {
        if (backgroundColor != null)
            g.fill(backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue());


        g.stroke(1);
        g.rect(bounds.x, bounds.y, bounds.w, bounds.h);

        if (foregroundColor != null) {
            g.stroke(foregroundColor.getRed(), foregroundColor.getGreen(), foregroundColor.getBlue());

            if (hovered)
                g.fill(foregroundColor.getRed(), foregroundColor.getGreen(), foregroundColor.getBlue());
        }

        if (activeColor != null && active)
            g.fill(activeColor.getRed(), activeColor.getGreen(), activeColor.getBlue());

        float x, y;
        if (vertical) {
            padding = bounds.w / 2;

            y = bounds.y + t * (bounds.h - bounds.y - padding * 2) + padding;
            x = bounds.x + bounds.w / 2;

            if (y - r > padding)
                g.line(x, bounds.y + padding, x, y - r);
            if (y + r < bounds.y + bounds.h - padding)
                g.line(x, y + r, x, bounds.y + bounds.h - padding);
        } else {
            padding = bounds.h / 2;

            y = bounds.y + bounds.h / 2;
            x = bounds.x + t * (bounds.w - bounds.x - padding * 2) + padding;

            if (x - r > padding)
                g.line(bounds.x + padding, y, x - r, y);
            if (x + r < bounds.x + bounds.w - padding)
                g.line(x + r, y, bounds.x + bounds.w - padding, y);
        }
        g.ellipse(x, y, r * 2, r * 2);
    }

    @Override
    public boolean handleEvent(InputEvent event, Vector2 translation) {
        if (event instanceof MouseEvent mouseEvent) {

            float x, y;
            if (vertical) {
                y = bounds.y + t * (bounds.h - bounds.y - padding * 2) + padding;
                x = bounds.x + bounds.w / 2;
            } else {
                y = bounds.y + bounds.h / 2;
                x = bounds.x + t * (bounds.w - bounds.x - padding * 2) + padding;
            }

            Vector2 delta = Vector2.sub(x, y, mouseEvent.MouseX, mouseEvent.MouseY).add(translation);
            boolean overCircle = delta.magnitudeSqr() < r * r * 1.2f;

            switch (mouseEvent.Type) {
                case MouseMoved:
                    hovered = overCircle;
                    return overCircle;

                case MouseButtonPressed:
                    active = overCircle;
                case MouseDragged:
                case MouseButtonClicked:

                    if (overCircle || active) {
                        if (vertical) {
                            t = mouseEvent.MouseY - translation.y - bounds.y - padding;
                            t /= bounds.h - bounds.y - padding * 2;
                        } else {
                            t = mouseEvent.MouseX - translation.x - bounds.x - padding;
                            t /= bounds.w - bounds.x - padding * 2;
                        }
                        t = MathF.clamp(t, 0, 1);

                        parent.uiEvent(this);
                    }

                    if (mouseEvent.Type == MouseEventType.MouseButtonClicked)
                        active = false;
                    return true;

                case MouseButtonReleased:
                    active = false;
                    return hovered;

                case MouseWheel:
                    break;
            }
        }

        return false;
    }
}
