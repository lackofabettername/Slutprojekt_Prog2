package game2_1.events;

public class MouseEvent extends InputEvent {
    public final MouseEventType Type;
    public final MouseButton MouseButton;
    public final int MouseX;
    public final int MouseY;

    public MouseEvent(MouseEventType type, MouseButton mouseButton, int mouseX, int mouseY) {

        super(InputEventType.Mouse);

        Type = type;
        MouseButton = mouseButton;
        MouseX = mouseX;
        MouseY = mouseY;
    }

    @Override
    public String toString() {
        return "MouseEvent{" +
                "Type=" + Type +
                '}';
    }
}
