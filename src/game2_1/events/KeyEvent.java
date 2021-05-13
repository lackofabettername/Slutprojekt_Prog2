package game2_1.events;

public class KeyEvent extends InputEvent {

    public final KeyEventType Type;
    public final boolean Coded;
    public final char Key;

    //TODO
    // - arrow keys
    // - shift, ctrl, alt, other modifiers?
    public KeyEvent(KeyEventType type, char key) {
        super(InputEventType.Key);

        Type = type;
        Coded = false;
        Key = key;
    }
    public KeyEvent(KeyEventType type, int keyCode) {
        super(InputEventType.Key);

        Type = type;
        Coded = true;
        Key = (char) keyCode;
    }
}
