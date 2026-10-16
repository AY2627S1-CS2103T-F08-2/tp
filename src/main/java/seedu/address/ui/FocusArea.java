package seedu.address.ui;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * The parts of the main window that the keyboard focus moves between. Tab goes back and forth between the command
 * box and the view, and in the view, Up and Down move between the view tabs and the list. The list is that of the
 * view that is shown: patients or appointments. The details beside the list are not among these parts.
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
     * Returns the area that Tab, or Shift+Tab, moves the focus to from this area. From the view, Tab goes to the
     * command box. From the command box, it goes back to {@code lastViewArea}, the part of the view that last had
     * the focus.
     *
     * @throws IllegalArgumentException if {@code lastViewArea} is not in the view.
     */
    public FocusArea afterTab(FocusArea lastViewArea) {
        requireNonNull(lastViewArea);
        checkArgument(lastViewArea.isInView(), "The last view area must be in the view.");
        return this == COMMAND_BOX ? lastViewArea : COMMAND_BOX;
    }
}
