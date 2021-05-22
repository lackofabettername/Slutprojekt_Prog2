package game2_1.events;

import java.io.Serializable;

public class MouseEvent extends InputEvent implements Serializable {

    public static final int LeftMouseButton = 1;
    public static final int ScrollWheel = 2;
    public static final int RightMouseButton = 3;

    public final MouseEventType Type;
    public final int MouseX;
    public final int MouseY;
    /**
     * It represents the button used for MouseDragged, pressed, released and clicked.
     * It represents how much was scrolled for MouseWheel.
     */
    private final int _info;

    public MouseEvent(MouseEventType type, int mouseX, int mouseY, int info) {

        super(InputEventType.Mouse);

        Type = type;
        MouseX = mouseX;
        MouseY = mouseY;
        _info = info;
    }
    public MouseEvent(MouseEventType type, int mouseX, int mouseY) {
        this(type, mouseX, mouseY, -1);
    }

    public int mouseButton() {
        return _info;
    }

    public int scrollWheel() {
        return _info;
    }

    @Override
    public String toString() {
        return "MouseEvent{" +
                "Type=" + Type +
                '}';
    }
}
