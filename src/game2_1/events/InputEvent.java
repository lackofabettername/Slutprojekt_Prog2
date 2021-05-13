package game2_1.events;

import java.io.Serializable;

public abstract class InputEvent implements Serializable {

    public final InputEventType Type;

    public InputEvent(InputEventType type) {
        Type = type;
    }
}
