package ui;

import game2_1.events.InputEvent;
import game2_1.events.MouseEvent;
import utility.*;

import processing.core.PGraphics;

public class MenuDropdownFramework extends MenuFramework {
    private Bounds2 collapsedBounds;
    public Bounds2 expandedBounds;

    public MenuText collapsedText;

    private boolean expanded;
    private boolean animating;
    private float animationTimer;

    private float innerPadding, outerPadding;

    //region Constructors
    public MenuDropdownFramework(String name, UIListener parent, Bounds2 bounds) {
        super(name, parent, null);
        expandedBounds = bounds;

        foregroundColor = new Color(ColorMode.RGBA, 1, 1, 1, 1);
        backgroundColor = new Color(ColorMode.RGBA, 0, 0, 0, 0.9f);
    }
    public MenuDropdownFramework(String name, UIListener parent, float x, float y, float w, float h) {
        this(name, parent, new Bounds2(x, y, w, h));
    }
    public MenuDropdownFramework(String name, UIListener parent) {
        this(name, parent, null);
    }
    public MenuDropdownFramework(String name, float x, float y, float w, float h) {
        this(name, null, new Bounds2(x, y, w, h));
    }
    public MenuDropdownFramework(String name, Bounds2 bounds) {
        this(name, null, bounds);
    }
    public MenuDropdownFramework(String name) {
        this(name, null, null);
    }
    //endregion

    @Override
    public void setBounds(Bounds2 bounds) {
        if (bounds == null)
            return;

        super.setBounds(bounds);
        collapsedBounds = new Bounds2(bounds);

        if (expandedBounds == null) {
            expandedBounds = new Bounds2(bounds);
        }
    }

    @Override
    public void fitElements(float innerPadding, float outerPadding) {
        this.innerPadding = innerPadding;
        this.outerPadding = outerPadding;
        super.fitElements(innerPadding, outerPadding);
    }


    @Override
    public void onRenderElement(PGraphics g) {
        g.stroke(foregroundColor.getRed(), foregroundColor.getGreen(), foregroundColor.getBlue(), foregroundColor.getAlpha() * (1 - animationTimer / 2));
        g.fill(backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue(), backgroundColor.getAlpha());
        g.rect(bounds.x, bounds.y, bounds.w, bounds.h);

        boolean toggleAnimation = false;
        if (expanded) {
            if (animationTimer < 1) {
                animationTimer = MathF.clamp(animationTimer + .05f, 0, 1);
                bounds.w = collapsedBounds.w + animationTimer * (expandedBounds.w - collapsedBounds.w);
                bounds.h = collapsedBounds.h + animationTimer * (expandedBounds.h - collapsedBounds.h);
                fitElements(innerPadding, outerPadding);

                if (!animating) {
                    toggleAnimation = true;
                }
            } else if (animating) {
                toggleAnimation = true;
            }

            super.onRenderElement(g);
        } else {
            if (animationTimer > 0) {
                animationTimer = MathF.clamp(animationTimer - .06f, 0, 1);
                bounds.w = collapsedBounds.w + animationTimer * (expandedBounds.w - collapsedBounds.w);
                bounds.h = collapsedBounds.h + animationTimer * (expandedBounds.h - collapsedBounds.h);
                fitElements(innerPadding, outerPadding);

                if (!animating) {
                    toggleAnimation = true;
                }
            } else if (animating) {
                toggleAnimation = true;
            }

            if (collapsedText != null) {
                collapsedText.setBounds(bounds);
                collapsedText.onRenderElement(g);
            }
        }

        if (toggleAnimation) {
            animating = !animating;
            if (animating) {
                animationStart();
            } else {
                animationStop();
            }
        }
    }

    @Override
    public boolean handleEvent(InputEvent event, Vector2 translation) {
        if (event instanceof MouseEvent mouseEvent) {
            if (bounds.inBounds(mouseEvent.MouseX - translation.x, mouseEvent.MouseY - translation.y)) {
                expanded = true;

                super.handleEvent(event, translation);

                return true;
            } else
                expanded = false;
        }

        if (expanded) {
            if (super.handleEvent(event, translation))
                return true;
        }


        return (animationTimer > 0);
    }
}
