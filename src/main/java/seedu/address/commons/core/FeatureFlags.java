package seedu.address.commons.core;

/**
 * Switches for features that are built and tested, but not released to users yet. While a switch is off, LaiNurse
 * behaves as if its feature did not exist: its commands are unknown, and its settings keep their defaults. To release
 * a feature, change the initial value of its switch to true, and document the feature in the User Guide.
 */
public final class FeatureFlags {

    /** The settings of #216, changed with the {@code set} command. */
    private static boolean isSettingsEnabled = false;

    /** The selective clearing of #217: {@code clear --archived} and {@code clearappt}, with a confirmation. */
    private static boolean isSelectiveClearEnabled = false;

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

    /**
     * Returns true if users can clear patients and appointments selectively, and must confirm a clear (#217).
     */
    public static boolean isSelectiveClearEnabled() {
        return isSelectiveClearEnabled;
    }

    /**
     * Turns the selective clearing of #217 on or off. Only tests call this, to test the feature before its release;
     * the app uses the initial value.
     */
    public static void setSelectiveClearEnabled(boolean isEnabled) {
        isSelectiveClearEnabled = isEnabled;
    }
}
