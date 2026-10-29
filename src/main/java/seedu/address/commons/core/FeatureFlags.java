package seedu.address.commons.core;

/**
 * Switches for features that are built and tested, but not released to users yet. While a switch is off, LaiNurse
 * behaves as if its feature did not exist: its commands are unknown, and its settings keep their defaults. To release
 * a feature, change the initial value of its switch to true, and document the feature in the User Guide.
 */
public final class FeatureFlags {

    /** The settings of #216, changed with the {@code set} command. */
    private static boolean isSettingsEnabled = false;

    private FeatureFlags() {} // prevents instantiation

    /**
     * Returns true if users can change the settings of #216 with the {@code set} command, and the settings apply.
     */
    public static boolean isSettingsEnabled() {
        return isSettingsEnabled;
    }

    /**
     * Turns the settings of #216 on or off. Only tests call this, to test the feature before its release; the app
     * uses the initial value.
     */
    public static void setSettingsEnabled(boolean isEnabled) {
        isSettingsEnabled = isEnabled;
    }
}
