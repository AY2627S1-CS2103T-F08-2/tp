package seedu.address.ui;

/**
 * The parts of the main window that the keyboard focus moves between. In the view, Tab and Up and Down move
 * between the view tabs and the list. The list is that of the view that is shown: patients or appointments.
 * In the command box, Tab completes a patient ID instead. The details beside the list are not among these parts.
 */
public enum FocusArea {
    COMMAND_BOX,
    VIEW_TABS,
    LIST;

    /**
     * Returns true for the parts of the view: the view tabs and the list.
     */
    public boolean isInView() {
        return this != COMMAND_BOX;
    }

    /**
     * Returns the area that Tab moves the focus to from this area: from the view tabs to the list, and back.
     * The command box keeps the focus, as Tab completes a patient ID there.
     */
    public FocusArea afterTab() {
        return switch (this) {
            case VIEW_TABS -> LIST;
            case LIST -> VIEW_TABS;
            case COMMAND_BOX -> COMMAND_BOX;
        };
    }
}
