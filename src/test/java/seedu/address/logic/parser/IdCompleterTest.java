package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.patient.PatientId;

public class IdCompleterTest {

    private static final List<PatientId> IDS = List.of(new PatientId("S1234567A"), new PatientId("T7654321B"),
            new PatientId("S1999999C"));

    @Test
    public void complete_partialId_returnsMatchesInGivenOrder() {
        assertEquals(List.of("view S1234567A", "view S1999999C"), IdCompleter.complete("view S1", IDS));
        assertEquals(List.of("delete T7654321B"), IdCompleter.complete("delete T7", IDS));
    }

    @Test
    public void complete_lowercasePartialId_matchesIgnoringCase() {
        assertEquals(List.of("view T7654321B"), IdCompleter.complete("view t76", IDS));
    }

    @Test
    public void complete_noIdTyped_returnsEveryId() {
        List<String> expected = List.of("view S1234567A", "view T7654321B", "view S1999999C");
        assertEquals(expected, IdCompleter.complete("view ", IDS));
        // the command word alone gets a space before the ID
        assertEquals(expected, IdCompleter.complete("view", IDS));
    }

    @Test
    public void complete_fullId_returnsItself() {
        assertEquals(List.of("view S1234567A"), IdCompleter.complete("view S1234567A", IDS));
    }

    @Test
    public void complete_spacing_keepsSpacesBetweenWordsAndDropsLeadingSpaces() {
        assertEquals(List.of("view   T7654321B"), IdCompleter.complete("   view   T7", IDS));
    }

    @Test
    public void complete_noMatchingId_returnsEmptyList() {
        assertEquals(List.of(), IdCompleter.complete("view X", IDS));
        assertEquals(List.of(), IdCompleter.complete("view S1", List.of()));
    }

    @Test
    public void complete_editWithIdLast_completesId() {
        assertEquals(List.of("edit S1234567A", "edit S1999999C"), IdCompleter.complete("edit s1", IDS));
        assertEquals(List.of("edit S1234567A", "edit T7654321B", "edit S1999999C"),
                IdCompleter.complete("edit", IDS));
    }

    @Test
    public void complete_editApptWithOptionsAfterId_completesIdAndKeepsOptions() {
        assertEquals(List.of("editappt T7654321B --appt 2026-10-05 09:30 --to 2026-10-06"),
                IdCompleter.complete("editappt t7 --appt 2026-10-05 09:30 --to 2026-10-06", IDS));
        assertEquals(List.of("editappt S1234567A", "editappt S1999999C"), IdCompleter.complete("editappt S1", IDS));
    }

    @Test
    public void complete_editWithOptionsAfterId_completesIdAndKeepsOptions() {
        assertEquals(List.of("edit T7654321B --phone 91234567 --add-appt 2026-12-01 10:00"),
                IdCompleter.complete("edit t7 --phone 91234567 --add-appt 2026-12-01 10:00", IDS));
        // the spacing around the ID and in the options is kept
        assertEquals(List.of("edit\tT7654321B\t--name\tAlex  Tan"),
                IdCompleter.complete("edit\tT7\t--name\tAlex  Tan", IDS));
        // a space after the ID, before any option is typed
        assertEquals(List.of("edit T7654321B "), IdCompleter.complete("edit T7 ", IDS));
    }

    @Test
    public void complete_editWithoutId_returnsEmptyList() {
        // the options come first, so there is no ID to complete
        assertEquals(List.of(), IdCompleter.complete("edit --phone 91234567", IDS));
        assertEquals(List.of(), IdCompleter.complete("edit X --phone 91234567", IDS));
    }

    @Test
    public void complete_nothingToComplete_returnsEmptyList() {
        assertEquals(List.of(), IdCompleter.complete("", IDS));
        assertEquals(List.of(), IdCompleter.complete("   ", IDS));
        // view and delete take only an ID, so it must end the command
        assertEquals(List.of(), IdCompleter.complete("view S1 ", IDS));
        assertEquals(List.of(), IdCompleter.complete("view S1 extra", IDS));
        assertEquals(List.of(), IdCompleter.complete("delete S1 extra", IDS));
        // commands that do not take the ID of an existing patient
        assertEquals(List.of(), IdCompleter.complete("add --id S1", IDS));
        assertEquals(List.of(), IdCompleter.complete("find S1", IDS));
        // command words are case-sensitive, as in LaiNurseParser
        assertEquals(List.of(), IdCompleter.complete("VIEW S1", IDS));
    }

    @Test
    public void complete_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> IdCompleter.complete(null, IDS));
        assertThrows(NullPointerException.class, () -> IdCompleter.complete("view S1", null));
    }
}
