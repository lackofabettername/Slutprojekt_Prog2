package game2_1.events;

/**
 * @author Respar
 * @see KeyEventType
 */
public class KeyEvent extends InputEvent {

    public final KeyEventType TYPE;
    public final boolean CODED;
    /**
     * If {@link #CODED} is false this is the char character of the key that was pressed. if {@link #CODED} is true,
     * it contains a code rather than a char. The arrow keys for example don't have a char representation and are therefore "Coded".
     * @see processing.core.PConstants
     */
    public final char KEY;

    //TODO
    // - arrow keys
    // - shift, ctrl, alt, other modifiers?
    public KeyEvent(KeyEventType type, char key) {
        super(InputEventType.KEY);

        TYPE = type;
        CODED = false;
        KEY = key;
    }
    public KeyEvent(KeyEventType type, int keyCode) {
        super(InputEventType.KEY);

        TYPE = type;
        CODED = true;
        KEY = (char) keyCode;
    }
}
