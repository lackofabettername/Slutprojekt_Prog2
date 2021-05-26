package ui;

import game2_1.events.InputEvent;
import game2_1.events.MouseEvent;
import utility.*;

import processing.core.PGraphics;

//TODO cleanup
public class MenuScrollFrameWork extends MenuFramework {
    Vector2 minElementSize;


    Vector2 translation = new Vector2();
    float scrollbarThickness = 20;

    //Classer är överskattade
    Color scrollBarColorY;
    Color scrollBarColorX;

    float scrollbarPosY = 0;
    float scrollbarPosX = 0;

    boolean scrollbarX;
    boolean scrollbarY;
    float scrollbarRatioX;
    float scrollbarRatioY;
    boolean scrollbarActiveX;
    boolean scrollbarActiveY;

    //region Constructors
    public MenuScrollFrameWork(String name, UIListener parent, Vector2 minElementSize, Bounds2 bounds) {
        super(name, parent, bounds);

        this.minElementSize = minElementSize;

        scrollBarColorY = new Color(ColorMode.RGBA, 1, 1, 1, 0.5f);
        scrollBarColorX = new Color(ColorMode.RGBA, 1, 1, 1, 0.5f);
    }
    public MenuScrollFrameWork(String name, UIListener parent, Vector2 minElementSize, float x, float y, float w, float h) {
        this(name, parent, minElementSize, new Bounds2(x, y, w, h));
    }
    public MenuScrollFrameWork(String name, UIListener parent, Vector2 minElementSize) {
        this(name, parent, minElementSize, null);
    }
    public MenuScrollFrameWork(String name, Vector2 minElementSize, float x, float y, float w, float h) {
        this(name, null, minElementSize, new Bounds2(x, y, w, h));
    }
    public MenuScrollFrameWork(String name, Bounds2 bounds, Vector2 minElementSize) {
        this(name, null, minElementSize, bounds);
    }
    public MenuScrollFrameWork(String name, Vector2 minElementSize) {
        this(name, null, minElementSize, null);
    }
    //endregion

    @Override
    public void setParent(UIListener parent) {
        super.setParent(parent);
    }

    @Override
    public void removeMenuObject(MenuObject menuObject) {
        super.removeMenuObject(menuObject);
    }

    @Override
    public void fitElements(float innerPadding, float outerPadding) {
        if (bounds == null)
            return;

        Bounds2 orig = new Bounds2(bounds);
        if (bounds.w / width < minElementSize.x) {
            scrollbarX = true;

            bounds.w = minElementSize.x * width;
            bounds.h -= scrollbarThickness;
            setBounds(bounds);

            scrollbarRatioX = orig.w / bounds.w;
        }
        if (bounds.h / height < minElementSize.y) {
            scrollbarY = true;

            bounds.h = minElementSize.y * height;
            bounds.w -= scrollbarThickness;
            setBounds(bounds);

            scrollbarRatioY = orig.h / bounds.h;
        }

        super.fitElements(innerPadding, outerPadding);

        setBounds(orig);
    }

    @Override
    public void onRenderElement(PGraphics g) {
        renderBounds = true;
        renderBounds(g);
        renderBounds = false;

        translation.set(0, 0);
        if (scrollbarX) {
            //length of scrollbar rect
            float w = (bounds.w - (scrollbarY ? scrollbarThickness : 0)) * scrollbarRatioX;

            //coordinates of scrollbar rect
            float x = ((bounds.w - (scrollbarY ? scrollbarThickness : 0)) - w) * scrollbarPosX + bounds.x;
            float y = bounds.h + bounds.y - scrollbarThickness;

            g.line(bounds.x, y, bounds.x + bounds.w, y);

            g.fill(scrollBarColorX.getRed(), scrollBarColorX.getGreen(), scrollBarColorX.getBlue(), scrollBarColorX.getAlpha());
            g.rect(x, y, w, scrollbarThickness);

            //move all menuitems
            translation.add(-scrollbarPosX * bounds.w * (1 / scrollbarRatioX - 1), 0);
        }

        if (scrollbarY) {
            //length of scrollbar rect
            float h = (bounds.h - (scrollbarX ? scrollbarThickness : 0)) * scrollbarRatioY;

            //coordinates of scrollbar rect
            float x = bounds.w + bounds.x - scrollbarThickness;
            float y = ((bounds.h - (scrollbarX ? scrollbarThickness : 0)) - h) * scrollbarPosY + bounds.y;

            g.line(x, bounds.y, x, bounds.y + bounds.h);

            g.fill(scrollBarColorY.getRed(), scrollBarColorY.getGreen(), scrollBarColorY.getBlue(), scrollBarColorY.getAlpha());
            g.rect(x, y, scrollbarThickness, h);

            //move all menuitems
            translation.add(0, -scrollbarPosY * bounds.h * (1 / scrollbarRatioY - 1));
        }

        g.clip(bounds.x, bounds.y, bounds.w + 1 - (scrollbarY ? scrollbarThickness : 0), bounds.h + 1 - (scrollbarX ? scrollbarThickness : 0));
        g.translate(translation.x, translation.y);

        super.onRenderElement(g);

        g.noClip();
    }

    @Override
    public boolean handleEvent(InputEvent event, Vector2 translation) {
        if (event instanceof MouseEvent mouseEvent) {
            float x = mouseEvent.MouseX - translation.x;
            float y = mouseEvent.MouseY - translation.y;

            if (scrollbarX) {
                if (scrollbarActiveX || (y > bounds.h + bounds.y - scrollbarThickness && y < bounds.y + bounds.h)) {
                    scrollBarColorX.setAlpha(0.6f);

                    switch (mouseEvent.Type) {
                        case MOUSE_BUTTON_PRESSED -> scrollbarActiveX = true;
                        case MOUSE_BUTTON_RELEASED -> scrollbarActiveX = false;
                        case MOUSE_DRAGGED -> {
                            x -= bounds.x;
                            x -= (bounds.w - (scrollbarY ? scrollbarThickness : 0)) * scrollbarRatioX / 2;
                            x /= (bounds.w - (scrollbarY ? scrollbarThickness : 0)) - (bounds.w - (scrollbarY ? scrollbarThickness : 0)) * scrollbarRatioX;
                            scrollbarPosX = MathF.clamp(x, 0, 1);
                            scrollBarColorX.setAlpha(1);
                        }
                        case MOUSE_WHEEL -> {
                            scrollbarPosX += ((MouseEvent) event).scrollWheel() * scrollbarRatioX / 10;
                            scrollbarPosX = MathF.clamp(scrollbarPosX, 0, 1);
                        }
                    }

                    return true;
                } else {
                    if (scrollBarColorX.getAlpha() != 0.5f) {
                        scrollBarColorX.setAlpha(0.5f);
                        return true;
                    }
                }
            }

            if (scrollbarY) {
                if (scrollbarActiveY || (x > bounds.w + bounds.x - scrollbarThickness && x < bounds.x + bounds.w)) {
                    scrollBarColorY.setAlpha(0.6f);

                    switch (mouseEvent.Type) {
                        case MOUSE_BUTTON_PRESSED -> scrollbarActiveY = true;
                        case MOUSE_BUTTON_RELEASED -> scrollbarActiveY = false;
                        case MOUSE_DRAGGED -> {

                            y -= bounds.y;
                            y -= (bounds.h - (scrollbarX ? scrollbarThickness : 0)) * scrollbarRatioY / 2;
                            y /= (bounds.h - (scrollbarX ? scrollbarThickness : 0)) - (bounds.h - (scrollbarX ? scrollbarThickness : 0)) * scrollbarRatioY;
                            scrollbarPosY = MathF.clamp(y, 0, 1);
                            scrollBarColorY.setAlpha(1);
                        }
                        case MOUSE_WHEEL -> {
                            scrollbarPosY += ((MouseEvent) event).scrollWheel() * scrollbarRatioY / 10;
                            scrollbarPosY = MathF.clamp(scrollbarPosY, 0, 1);
                        }
                    }

                    return true;
                } else {
                    if (scrollBarColorY.getAlpha() != 0.5f) {
                        scrollBarColorY.setAlpha(0.5f);
                        return true;
                    }
                }
            }
        }

        translation.add(this.translation);
        return super.handleEvent(event, translation);
    }

    @Override
    public void setBounds(Bounds2 bounds) {
        super.setBounds(bounds);
    }
}
