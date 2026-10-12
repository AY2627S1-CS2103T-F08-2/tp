package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_VIEW_APPOINTMENT_LISTED_OVERVIEW;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPatients.DANIEL;
import static seedu.address.testutil.TypicalPatients.getTypicalAddressBook;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.patient.AppointmentBetweenDatesPredicate;

public class ViewAppointmentCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_noAppointmentsFound_emptyList() {
        LocalDate date = LocalDate.of(2030, 1, 1); // Far future, no appointments
        ViewAppointmentCommand command = new ViewAppointmentCommand(date);

        // Create expected model with same filtered list state
        expectedModel.updateFilteredPatientList(new AppointmentBetweenDatesPredicate(date, date));

        String expectedMessage = String.format(MESSAGE_VIEW_APPOINTMENT_LISTED_OVERVIEW, 0, date.toString());
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_validDate_appointmentFound() {
        // Daniel has appointment on 2026-10-06
        LocalDate date = LocalDate.of(2026, 10, 6);
        ViewAppointmentCommand command = new ViewAppointmentCommand(date);

        // Create expected model with same filtered list state
        expectedModel.updateFilteredPatientList(new AppointmentBetweenDatesPredicate(date, date));

        String expectedMessage = String.format(MESSAGE_VIEW_APPOINTMENT_LISTED_OVERVIEW, 1, date.toString());
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(DANIEL), model.getFilteredPatientList());
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
        String expected = ViewAppointmentCommand.class.getCanonicalName() + "{date=" + date + "}";
        assertEquals(expected, command.toString());
    }
}
