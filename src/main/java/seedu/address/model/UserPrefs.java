package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import seedu.address.commons.core.GuiSettings;

/**
 * Represents User's preferences.
 */
public class UserPrefs implements ReadOnlyUserPrefs {

    private GuiSettings guiSettings = new GuiSettings();
    private Settings settings = new Settings();

    /**
     * Creates a {@code UserPrefs} with default values.
     */
    public UserPrefs() {}

    /**
     * Creates a {@code UserPrefs} with the prefs in {@code userPrefs}.
     */
    public UserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        setGuiSettings(userPrefs.getGuiSettings());
        setSettings(userPrefs.getSettings());
    }

    public GuiSettings getGuiSettings() {
        return guiSettings;
    }

    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        this.guiSettings = guiSettings;
    }

    @Override
    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        requireNonNull(settings);
        this.settings = settings;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof UserPrefs otherUserPrefs)) {
            return false;
        }

        return guiSettings.equals(otherUserPrefs.guiSettings) && settings.equals(otherUserPrefs.settings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(guiSettings, settings);
    }

    @Override
    public String toString() {
        return "Gui Settings : " + guiSettings + "\nSettings : " + settings;
    }

}
