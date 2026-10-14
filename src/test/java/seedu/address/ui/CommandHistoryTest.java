package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;

public class CommandHistoryTest {

    private final CommandHistory history = new CommandHistory();

    @Test
    public void previousAndNext_emptyHistory_returnEmpty() {
        assertEquals(Optional.empty(), history.previous("typing"));
        assertEquals(Optional.empty(), history.next());
    }

    @Test
    public void previousAndNext_twoCommands_goBackAndForth() {
        history.add("list");
        history.add("view S1234567A");

        assertEquals(Optional.of("view S1234567A"), history.previous(""));
        assertEquals(Optional.of("list"), history.previous("view S1234567A"));
        // already at the oldest command
        assertEquals(Optional.empty(), history.previous("list"));

        assertEquals(Optional.of("view S1234567A"), history.next());
        // after the newest command comes the line that was being typed
        assertEquals(Optional.of(""), history.next());
        // already at the line being typed
        assertEquals(Optional.empty(), history.next());
    }

    @Test
    public void next_afterPrevious_givesBackTextBeingTyped() {
        history.add("list");

        assertEquals(Optional.of("list"), history.previous("view S12"));
        assertEquals(Optional.of("view S12"), history.next());
    }

    @Test
    public void add_sameAsNewestCommand_isNotAddedAgain() {
        history.add("list");
        history.add("list");

        assertEquals(Optional.of("list"), history.previous(""));
        assertEquals(Optional.empty(), history.previous("list"));
    }

    @Test
    public void add_sameAsOlderCommand_isAdded() {
        history.add("list");
        history.add("find tan");
        history.add("list");

        assertEquals(Optional.of("list"), history.previous(""));
        assertEquals(Optional.of("find tan"), history.previous("list"));
        assertEquals(Optional.of("list"), history.previous("find tan"));
    }

    @Test
    public void add_whileGoingBack_returnsToLineBeingTyped() {
        history.add("list");
        history.add("find tan");
        history.previous("draft");
        history.previous("find tan");

        history.add("view S1234567A");

        assertEquals(Optional.empty(), history.next());
        assertEquals(Optional.of("view S1234567A"), history.previous(""));
    }

    @Test
    public void resetPosition_whileGoingBack_forgetsTextBeingTyped() {
        history.add("list");
        history.previous("draft");

        history.resetPosition();

        assertEquals(Optional.empty(), history.next());
        assertEquals(Optional.of("list"), history.previous(""));
        assertEquals(Optional.of(""), history.next());
    }

    @Test
    public void nullArguments_throwNullPointerException() {
        assertThrows(NullPointerException.class, () -> history.add(null));
        assertThrows(NullPointerException.class, () -> history.previous(null));
    }
}
