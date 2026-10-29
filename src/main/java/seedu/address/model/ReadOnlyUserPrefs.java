package seedu.address.model;

import seedu.address.commons.core.GuiSettings;

/**
 * Unmodifiable view of user prefs.
 */
public interface ReadOnlyUserPrefs {

    GuiSettings getGuiSettings();

    /**
     * Returns the settings that decide how marks, past clashes and archived patients behave (#216).
     */
    Settings getSettings();

}
