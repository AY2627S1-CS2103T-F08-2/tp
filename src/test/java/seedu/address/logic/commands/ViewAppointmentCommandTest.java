package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_VIEW_APPOINTMENT_LISTED_OVERVIEW;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPatients.getTypicalAddressBook;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.commons.util.DateParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

public class ViewAppointmentCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

    @Test
    public void execute_noAppointmentsFound_emptyList() {
        LocalDate date = LocalDate.of(2030, 1, 1); // Far future, no appointments
        ViewAppointmentCommand command = new ViewAppointmentCommand(date);
        String expectedMessage = String.format(MESSAGE_VIEW_APPOINTMENT_LISTED_OVERVIEW, 0, date.toString());
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_validDate_appointmentFound() {
        // Bob has appointment on 2026-10-06
        LocalDate date = LocalDate.of(2026, 10, 6);
        ViewAppointmentCommand command = new ViewAppointmentCommand(date);
        String expectedMessage = String.format(MESSAGE_VIEW_APPOINTMENT_LISTED_OVERVIEW, 1, date.toString());
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void equals() {
        LocalDate date1 = LocalDate.of(2026, 10, 15);
        LocalDate date2 = LocalDate.of(2026, 10, 16);

        ViewAppointmentCommand command1 = new ViewAppointmentCommand(date1);
        ViewAppointmentCommand command2 = new ViewAppointmentCommand(date1);
        ViewAppointmentCommand command3 = new ViewAppointmentCommand(date2);

        // Same values -> returns true
        assertTrue(command1.equals(command2));

        // Same object -> returns true
        assertTrue(command1.equals(command1));

        // Different values -> returns false
        assertFalse(command1.equals(command3));

        // null -> returns false
        assertFalse(command1.equals(null));

        // different types -> returns false
        assertFalse(command1.equals(5.0f));
    }

    @Test
    public void toStringMethod() {
        LocalDate date = LocalDate.of(2026, 10, 15);
        ViewAppointmentCommand command = new ViewAppointmentCommand(date);
        String expected = "ViewAppointmentCommand{" + "date=" + date + '}';
        assertEquals(expected, command.toString());
    }

    @Test
    public void isValidDate() {
        // valid dates
        assertTrue(DateParser.isValidDate("2026-01-01"));
        assertTrue(DateParser.isValidDate("2026-12-31"));
        assertTrue(DateParser.isValidDate("2028-02-29")); // leap year
        assertTrue(DateParser.isValidDate(" 2026-01-01 ")); // with whitespace

        // invalid dates
        assertFalse(DateParser.isValidDate("2026/01/01")); // wrong separator
        assertFalse(DateParser.isValidDate("2026-1-01")); // month not zero-padded
        assertFalse(DateParser.isValidDate("2026-13-01")); // invalid month
        assertFalse(DateParser.isValidDate("2026-02-30")); // invalid date
        assertFalse(DateParser.isValidDate("2026-02-29")); // not a leap year
        assertFalse(DateParser.isValidDate("")); // empty
        assertFalse(DateParser.isValidDate("tomorrow")); // not a date
    }

    @Test
    public void parseDate_invalidDate_throwsParseException() {
        assertThrows(ParseException.class, () -> DateParser.parseDate("invalid"));
        assertThrows(ParseException.class, () -> DateParser.parseDate("2026-02-30"));
        assertThrows(ParseException.class, () -> DateParser.parseDate("2026-13-01"));
    }

    @Test
    public void parseDate_validDate_success() throws ParseException {
        assertEquals(LocalDate.of(2026, 1, 15), DateParser.parseDate("2026-01-15"));
        assertEquals(LocalDate.of(2026, 1, 15), DateParser.parseDate(" 2026-01-15 "));
    }
}
