package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import javafx.scene.input.KeyCode;
import seedu.address.ui.BrowseKeys.Action;

public class BrowseKeysTest {

    @Test
    public void actionFor_browseKeys_returnsTheirActions() {
        assertEquals(Optional.of(Action.RETURN_TO_COMMAND_BOX), BrowseKeys.actionFor(KeyCode.ENTER, false, false));
        assertEquals(Optional.of(Action.RESET_VIEW), BrowseKeys.actionFor(KeyCode.ESCAPE, false, false));
        // Tab goes between the view tabs and the list, and Shift+Tab does nothing
        assertEquals(Optional.of(Action.SWITCH_AREA), BrowseKeys.actionFor(KeyCode.TAB, false, false));
        assertEquals(Optional.of(Action.IGNORE), BrowseKeys.actionFor(KeyCode.TAB, true, false));
    }

    @Test
    public void actionFor_shiftUpOrDown_scrollsDetails() {
        assertEquals(Optional.of(Action.SCROLL_DETAILS_UP), BrowseKeys.actionFor(KeyCode.UP, true, false));
        assertEquals(Optional.of(Action.SCROLL_DETAILS_UP), BrowseKeys.actionFor(KeyCode.KP_UP, true, false));
        assertEquals(Optional.of(Action.SCROLL_DETAILS_DOWN), BrowseKeys.actionFor(KeyCode.DOWN, true, false));
        assertEquals(Optional.of(Action.SCROLL_DETAILS_DOWN), BrowseKeys.actionFor(KeyCode.KP_DOWN, true, false));
    }

    @Test
    public void actionFor_otherKeys_keepTheirUsualEffect() {
        // The arrow keys move the selection in a list or switch the view tab, and Page Up and Page Down scroll
        for (KeyCode key : List.of(KeyCode.UP, KeyCode.DOWN, KeyCode.LEFT, KeyCode.RIGHT, KeyCode.PAGE_UP,
                KeyCode.PAGE_DOWN, KeyCode.A, KeyCode.SPACE, KeyCode.F1)) {
            assertEquals(Optional.empty(), BrowseKeys.actionFor(key, false, false), key.toString());
        }
    }

    @Test
    public void actionFor_withShift_enterAndEscapeKeepUsualEffect() {
        assertEquals(Optional.empty(), BrowseKeys.actionFor(KeyCode.ENTER, true, false));
        assertEquals(Optional.empty(), BrowseKeys.actionFor(KeyCode.ESCAPE, true, false));
    }

    @Test
    public void actionFor_withShortcutKey_keepsUsualEffect() {
        // Ctrl+Up and Ctrl+Down jump to the ends of a list, so the shortcut key also leaves Up and Down alone
        for (KeyCode key : List.of(KeyCode.ENTER, KeyCode.ESCAPE, KeyCode.TAB, KeyCode.UP, KeyCode.DOWN)) {
            assertEquals(Optional.empty(), BrowseKeys.actionFor(key, false, true), key.toString());
            assertEquals(Optional.empty(), BrowseKeys.actionFor(key, true, true), key.toString());
        }
    }

    @Test
    public void actionFor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> BrowseKeys.actionFor(null, false, false));
    }

    @Test
    public void isCommandText_printableText_returnsTrue() {
        for (String character : List.of("v", "V", "1", " ", "-", "李", "é")) {
            assertTrue(BrowseKeys.isCommandText(character, false), character);
        }
    }

    @Test
    public void isCommandText_controlCharacters_returnsFalse() {
        // Enter, Tab, Esc, Backspace and Delete type control characters
        for (String character : List.of("\r", "\n", "\t", "\u001b", "\b", "\u007f")) {
            assertFalse(BrowseKeys.isCommandText(character, false));
        }
        assertFalse(BrowseKeys.isCommandText("", false));
    }

    @Test
    public void isCommandText_withShortcutKey_returnsFalse() {
        // On macOS, Cmd+C types "c" with Cmd held
        assertFalse(BrowseKeys.isCommandText("c", true));
    }

    @Test
    public void isCommandText_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> BrowseKeys.isCommandText(null, false));
    }
}
