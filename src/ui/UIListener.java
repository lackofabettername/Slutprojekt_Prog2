package ui;

/**
 * Listens to the events thrown by a UI.
 * @see UI
 * @see MenuObject
 */
public interface UIListener {
    void uiEvent(MenuObject caller);
}
