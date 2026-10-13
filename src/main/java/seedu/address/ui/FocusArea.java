package seedu.address.ui;

/**
 * The parts of the main window that the keyboard focus moves between with Tab, in Tab order.
 */
public enum FocusArea {
    COMMAND_BOX,
    PATIENT_LIST,
    PATIENT_DETAILS;

    /**
     * Returns the area after this one, which Tab moves the focus to. The last area is followed by the first.
     */
    public FocusArea next() {
        FocusArea[] areas = values();
        return areas[(ordinal() + 1) % areas.length];
    }

    /**
     * Returns the area before this one, which Shift+Tab moves the focus to. The first area is preceded by the last.
     */
    public FocusArea previous() {
        FocusArea[] areas = values();
        return areas[(ordinal() + areas.length - 1) % areas.length];
    }
}
