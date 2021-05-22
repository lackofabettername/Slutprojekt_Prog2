package ui;



import game2_1.events.InputEvent;
import utility.Bounds2;
import utility.Coord2;
import utility.Vector2;

import processing.core.PGraphics;

import java.util.*;

public class MenuFramework extends MenuObject implements UIListener {

    private final LinkedHashMap<MenuObject, Alignment> alignments;
    private final HashMap<String, MenuObject> menu;

    private MenuObject selectedObject;

    private int minX, minY, width, height;
    private ArrayList<Coord2> openSpots;
    private HashSet<MenuObject> floatingPositions;

    private transient int activeAnimations;

    //region Constructors
    public MenuFramework(String name, UIListener parent, Bounds2 bounds) {
        super(name);
        setParent(parent);
        setBounds(bounds);

        menu = new HashMap<>();
        alignments = new LinkedHashMap<>();

        openSpots = new ArrayList<>();
        openSpots.add(new Coord2(0, 0));
        floatingPositions = new HashSet<>();

        renderBounds = false;
    }
    public MenuFramework(String name, UIListener parent, float x, float y, float w, float h) {
        this(name, parent, new Bounds2(x, y, w, h));
    }
    public MenuFramework(String name, UIListener parent) {
        this(name, parent, null);
    }
    public MenuFramework(String name, float x, float y, float w, float h) {
        this(name, null, new Bounds2(x, y, w, h));
    }
    public MenuFramework(String name, Bounds2 bounds) {
        this(name, null, bounds);
    }
    public MenuFramework(String name) {
        this(name, null, null);
    }
    //endregion

    @Override
    public void setParent(UIListener parent) {
        super.setParent(parent);

        if (menu != null) {
            menu.forEach((name, menuObject) -> {
                if (menuObject.parent != this) {
                    if (menuObject.parent instanceof MenuFramework oldParent)
                        oldParent.removeMenuObject(menuObject);
                    menuObject.setParent(this);
                }
            });
        }
    }

    public void addMenuObject(MenuObject menuObject) {
        floatingPositions.add(menuObject);
        if (openSpots.size() == 0) {
            for (int j = 0; j < height + (width <= height ? 0 : 1); ++j) {
                for (int i = 0; i < width + (width <= height ? 1 : 0); ++i) {
                    openSpots.add(new Coord2(minX + i, minY + j));
                }
            }
            alignments.forEach((object, alignment) -> {
                for (int i = 0; i < alignment.w(); ++i) {
                    for (int j = 0; j < alignment.h(); ++j) {
                        openSpots.remove(new Coord2(alignment.x() + i, alignment.y() + j));
                    }
                }
            });
        }

        Coord2 pos = openSpots.remove(0);
        addMenuObject(menuObject, pos.x, pos.y, 1, 1);
    }
    public void addMenuObject(MenuObject menuObject, int axis) {
        floatingPositions.add(menuObject);
        if (openSpots.size() == 0) {
            for (int j = 0; j < height + (axis % 2); ++j) {
                for (int i = 0; i < width + ((axis + 1) % 2); ++i) {
                    openSpots.add(new Coord2(minX + i, minY + j));
                }
            }
            alignments.forEach((object, alignment) -> {
                for (int i = 0; i < alignment.w(); ++i) {
                    for (int j = 0; j < alignment.h(); ++j) {
                        openSpots.remove(new Coord2(alignment.x() + i, alignment.y() + j));
                    }
                }
            });
        }

        Coord2 pos = openSpots.remove(0);
        addMenuObject(menuObject, pos.x, pos.y, 1, 1);
    }
    public void addMenuObject(MenuObject menuObject, int x, int y) {
        addMenuObject(menuObject, x, y, 1, 1);
    }
    public void addMenuObject(MenuObject menuObject, int x, int y, int w, int h) {
        if (w <= 0) throw new IllegalArgumentException("width must be greater than 0");
        if (h <= 0) throw new IllegalArgumentException("height must be greater than 0");

        if (menuObject.parent != this) {
            if (menuObject.parent instanceof MenuFramework parent)
                parent.removeMenuObject(menuObject);
            menuObject.setParent(this);
        }

        menu.put(menuObject.name, menuObject);

        minX = Math.min(minX, x);
        minY = Math.min(minY, y);
        width = Math.max(width, x - minX + w);
        height = Math.max(height, y - minY + h);
        alignments.put(menuObject, new Alignment(x, y, w, h));
    }

    public void removeMenuObject(MenuObject menuObject) {
//        if (menuObject.parent != this) {
//            menuObject.parent.removeMenuObject(menuObject);
//            menuObject.parent = this;
//        }
//
//        //totalWeights -= alignments.get(menuObject);
//        alignments.remove(menuObject);

        throw new UnsupportedOperationException("todo");
    }

    public final void fitElements(float padding) {
        fitElements(padding, padding);
    }
    public void fitElements(float innerPadding, float outerPadding) {
        Vector2 objectDimensions = Vector2.sub(bounds.w, bounds.h, innerPadding * (width - 1), innerPadding * (height - 1))
                .sub(outerPadding * 2, outerPadding * 2)
                .div(width, height);

        alignments.forEach((menuObject, alignment) -> {
            menuObject.setBounds(new Bounds2(
                    Vector2.mult(objectDimensions, alignment.x(), alignment.y())
                            .add(outerPadding, outerPadding)
                            .add(innerPadding * alignment.x(), innerPadding * alignment.y()),
                    Vector2.mult(objectDimensions, alignment.w(), alignment.h())
                            .add(innerPadding * (alignment.w() - 1), innerPadding * (alignment.h() - 1))
            ));
        });


//        Vector2 objectDimensions = Vector2.sub(bounds.w, bounds.h, padding * 2, padding * 2);
//
//        objectDimensions.subAxis(axis, padding * (alignments.size() - 1))
//                .divAxis(axis, totalWeights);
//
//        Vector2 pos = new Vector2(padding, padding);
//        alignments.forEach((menuObject, weight) -> {
//            menuObject.bounds = new Bounds2(pos, Vector2.multAxis(axis, objectDimensions, weight));
//            pos.addAxis(axis, objectDimensions.getAxis(axis) * weight + padding);
//        });
    }

    public MenuObject getMenuObject(String name) {
        if (menu.containsKey(name))
            return menu.get(name);

        for (Map.Entry<String, MenuObject> entry : menu.entrySet()) {
            MenuObject menuObject = entry.getValue();
            if (menuObject instanceof MenuFramework childMenu) {
                menuObject = childMenu.getMenuObject(name);
                if (menuObject != null)
                    return menuObject;
            }
        }
        return null;
    }

    public void animationStart() {
        if (parent instanceof MenuFramework)
            ((MenuFramework) parent).animationStart();
        else
            ++activeAnimations;
    }
    public void animationStop() {
        if (parent instanceof MenuFramework)
            ((MenuFramework) parent).animationStop();
        else
            --activeAnimations;
    }
    public boolean animationActive() {
        return activeAnimations > 0;
    }

    @Override
    public void onRenderElement(PGraphics g) {
        renderBounds(g);

        g.push();
        g.translate(bounds.x, bounds.y);
        g.noFill();

        g.noStroke();
        g.strokeWeight(1);
        menu.forEach((name, menuObject) -> {
            if (menuObject == selectedObject) return;
            g.push();
            menuObject.onRenderElement(g);
            g.pop();
        });

        if (selectedObject != null) {
            g.push();
            selectedObject.onRenderElement(g);
            g.pop();
        }

        g.pop();
    }

    @Override
    public boolean handleEvent(InputEvent event, Vector2 translation) {
        translation.add(bounds.getCorner(0));

        if (selectedObject != null) {
            if (selectedObject.handleEvent(event, translation)) {
                translation.sub(bounds.getCorner(0));
                return true;
            }
        }


        selectedObject = null;

        for (Map.Entry<MenuObject, Alignment> entry : alignments.entrySet()) {
            MenuObject menuObject = entry.getKey();
            if (menuObject.handleEvent(event, translation)) {
                selectedObject = menuObject;
                return true;
            }
        }

        //for (MenuObject menuObject : alignments) {
        //    if (menuObject.handleEvent(event, translation)) {
        //        return true;
        //    }
        //}

        translation.sub(bounds.getCorner(0));
        return false;
    }

    @Override
    public void uiEvent(MenuObject caller) {
        if (parent != null)
            parent.uiEvent(caller);
    }
}