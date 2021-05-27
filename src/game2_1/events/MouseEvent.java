package game2_1.events;

import java.io.Serializable;

/**
 * @author Respar
 * @see MouseEventType
 */
public class MouseEvent extends InputEvent implements Serializable {

    public static final int LEFT_MOUSE_BUTTON = 1;
    public static final int SCROLL_WHEEL = 2;
    public static final int RIGHT_MOUSE_BUTTON = 3;

    public final MouseEventType type;
    public final int mouseX;
    public final int mouseY;
    /**
     * It represents the button used for MOUSE_DRAGGED, pressed, released and clicked.
     * It represents how much was scrolled for MOUSE_WHEEL.
     */
    private final int info;

    public MouseEvent(MouseEventType type, int mouseX, int mouseY, int info) {

        super(InputEventType.MOUSE);

        this.type = type;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.info = info;
    }
    public MouseEvent(MouseEventType type, int mouseX, int mouseY) {
        this(type, mouseX, mouseY, -1);
    }

    /**
     * @return the button that was used. The return value is most likely:
     * <ul>
     *     <li>1 - {@link #LEFT_MOUSE_BUTTON}</li>
     *     <li>2 - {@link #SCROLL_WHEEL}</li>
     *     <li>3 - {@link #RIGHT_MOUSE_BUTTON}</li>
     * </ul>
     * But some mice have more buttons which get different values associated.
     */
    public int mouseButton() {
        return info;
    }

    /**
     * @return -1 if the scrollwheel scrolled up (away from you). 1 if the scrollWheel scrolled down (towards you).
     */
    public int scrollWheel() {
        return info;
    }

    @Override
    public String toString() {
        return "MouseEvent{" +
                "TYPE=" + type +
                '}';
    }
}
