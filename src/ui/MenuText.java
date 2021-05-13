package ui;


import utility.Bounds2;
import utility.Color;
import utility.ColorMode;

import processing.core.PGraphics;

import java.io.Serializable;

public class MenuText extends MenuObject implements Serializable {
    public String text;
    float textSize;

    public MenuText(String text) {
        this(text, null, 10);
    }
    public MenuText(String text, float textSize) {
        this(text, null, textSize);
    }
    public MenuText(String text, Bounds2 bounds) {
        this(text, bounds, 10);
    }
    public MenuText(String text, Bounds2 bounds, float textSize) {
        super(text);
        this.text = text;
        setBounds(bounds);
        this.textSize = textSize;

        foregroundColor = new Color(ColorMode.RGBA, 1, 1, 1, 1);
        backgroundColor = new Color(ColorMode.RGBA, 1, 1, 1, 0);
    }

    @Override
    public void onRenderElement(PGraphics g) {
        g.fill(backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue(), backgroundColor.getAlpha());

        g.stroke(1);
        g.rect(bounds.x, bounds.y, bounds.w, bounds.h);

        g.textSize(textSize);

        g.fill(foregroundColor.getRed(), foregroundColor.getGreen(), foregroundColor.getBlue(), foregroundColor.getAlpha());

        g.text(text, bounds.x, bounds.y, bounds.w, bounds.h);
    }
}
