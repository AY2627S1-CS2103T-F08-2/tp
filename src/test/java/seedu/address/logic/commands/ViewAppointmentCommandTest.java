package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_VIEW_APPOINTMENT_FROM_LISTED_OVERVIEW;
import static seedu.address.logic.Messages.MESSAGE_VIEW_APPOINTMENT_LISTED_OVERVIEW;
import static seedu.address.logic.Messages.MESSAGE_VIEW_APPOINTMENT_RANGE_LISTED_OVERVIEW;
import static seedu.address.logic.Messages.MESSAGE_VIEW_APPOINTMENT_TO_BEFORE_TODAY;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPatients.BENSON;
import static seedu.address.testutil.TypicalPatients.DANIEL;
import static seedu.address.testutil.TypicalPatients.getTypicalLaiNurse;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.model.LaiNurse;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.AppointmentBetweenDatesPredicate;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientAppointment;
import seedu.address.testutil.LaiNurseBuilder;
import seedu.address.testutil.PatientBuilder;

public class ViewAppointmentCommandTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 10, 12);
    private static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2026-10-12T10:00:00Z"), ZoneOffset.UTC);

    private Model model = new ModelManager(getTypicalLaiNurse(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalLaiNurse(), new UserPrefs());

    @Test
    public void constructor_startAfterEnd_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new ViewAppointmentCommand(
                Optional.of(LocalDate.of(2026, 10, 2)), Optional.of(LocalDate.of(2026, 10, 1))));
    }

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ViewAppointmentCommand(null));
        assertThrows(NullPointerException.class, () -> new ViewAppointmentCommand(null, Optional.empty()));
        assertThrows(NullPointerException.class, () -> new ViewAppointmentCommand(Optional.empty(), null));
        assertThrows(NullPointerException.class, () ->
                new ViewAppointmentCommand(Optional.empty(), Optional.empty(), null));
    }

    @Test
    public void execute_noAppointmentsFound_emptyList() {
        LocalDate date = LocalDate.of(2030, 1, 1); // Far future, no appointments
        ViewAppointmentCommand command = new ViewAppointmentCommand(date);

        // Create expected model with same filtered list state
        expectedModel.updateFilteredPatientList(new AppointmentBetweenDatesPredicate(date, date));

        String expectedMessage = String.format(MESSAGE_VIEW_APPOINTMENT_LISTED_OVERVIEW, 0, date.toString());
        // the appointment view still opens at the date's week, with nothing selected
        assertCommandSuccess(command, model, new CommandResult(expectedMessage, date, Optional.empty()),
                expectedModel);
    }

    @Test
    public void execute_validDate_appointmentFound() {
        // Daniel has appointment on 2026-10-06
        LocalDate date = LocalDate.of(2026, 10, 6);
        ViewAppointmentCommand command = new ViewAppointmentCommand(date);

        // Create expected model with same filtered list state
        expectedModel.updateFilteredPatientList(new AppointmentBetweenDatesPredicate(date, date));

        String expectedMessage = String.format(MESSAGE_VIEW_APPOINTMENT_LISTED_OVERVIEW, 1, date.toString());
        assertCommandSuccess(command, model,
                new CommandResult(expectedMessage, date, Optional.of(entry(DANIEL, "2026-10-06"))), expectedModel);
        assertEquals(List.of(DANIEL), model.getFilteredPatientList());
    }

    @Test
    public void execute_startAndEnd_appointmentsInRangeFound() {
        // Benson has an appointment on 2026-10-05 and Daniel on 2026-10-06
        LocalDate start = LocalDate.of(2026, 10, 1);
        LocalDate end = LocalDate.of(2026, 10, 31);
        ViewAppointmentCommand command = new ViewAppointmentCommand(Optional.of(start), Optional.of(end));

        expectedModel.updateFilteredPatientList(new AppointmentBetweenDatesPredicate(start, end));
        String expectedMessage = String.format(MESSAGE_VIEW_APPOINTMENT_RANGE_LISTED_OVERVIEW, 2, start, end);
        // the appointment view opens at the first appointment in the range, not at the start of the range
        assertCommandSuccess(command, model, new CommandResult(expectedMessage, LocalDate.of(2026, 10, 5),
                Optional.of(entry(BENSON, "2026-10-05 09:30"))), expectedModel);
        assertEquals(List.of(BENSON, DANIEL), model.getFilteredPatientList());
    }

    @Test
    public void execute_rangeEndsBeforeSecondAppointment_onlyFirstFound() {
        LocalDate start = LocalDate.of(2026, 10, 1);
        LocalDate end = LocalDate.of(2026, 10, 5);
        ViewAppointmentCommand command = new ViewAppointmentCommand(Optional.of(start), Optional.of(end));

        expectedModel.updateFilteredPatientList(new AppointmentBetweenDatesPredicate(start, end));
        String expectedMessage = String.format(MESSAGE_VIEW_APPOINTMENT_RANGE_LISTED_OVERVIEW, 1, start, end);
        assertCommandSuccess(command, model, new CommandResult(expectedMessage, end,
                Optional.of(entry(BENSON, "2026-10-05 09:30"))), expectedModel);
        assertEquals(List.of(BENSON), model.getFilteredPatientList());
    }

    @Test
    public void execute_startOnly_appointmentsOnOrAfterStartFound() {
        LocalDate start = LocalDate.of(2026, 10, 6);
        ViewAppointmentCommand command = new ViewAppointmentCommand(Optional.of(start), Optional.empty());

        expectedModel.updateFilteredPatientList(new AppointmentBetweenDatesPredicate(start, LocalDate.MAX));
        String expectedMessage = String.format(MESSAGE_VIEW_APPOINTMENT_FROM_LISTED_OVERVIEW, 1, start);
        // Benson's appointment is before the start, so Daniel's is the first one in the range
        assertCommandSuccess(command, model,
                new CommandResult(expectedMessage, start, Optional.of(entry(DANIEL, "2026-10-06"))), expectedModel);
        assertEquals(List.of(DANIEL), model.getFilteredPatientList());
    }

    @Test
    public void execute_endOnly_appointmentsFromTodayFound() {
        LocalDate today = TODAY;
        Patient yesterday = patientWithAppointment("S0000001A", today.minusDays(1));
        Patient onToday = patientWithAppointment("S0000002B", today);
        Patient inThreeDays = patientWithAppointment("S0000003C", today.plusDays(3));
        Patient inAMonth = patientWithAppointment("S0000004D", today.plusDays(30));
        LaiNurse laiNurse = new LaiNurseBuilder().withPatient(yesterday).withPatient(onToday)
                .withPatient(inThreeDays).withPatient(inAMonth).build();
        Model relativeModel = new ModelManager(laiNurse, new UserPrefs());
        Model expectedRelativeModel = new ModelManager(laiNurse, new UserPrefs());

        LocalDate end = today.plusDays(3);
        ViewAppointmentCommand command = new ViewAppointmentCommand(Optional.empty(), Optional.of(end), FIXED_CLOCK);
        expectedRelativeModel.updateFilteredPatientList(new AppointmentBetweenDatesPredicate(today, end));
        String expectedMessage = String.format(MESSAGE_VIEW_APPOINTMENT_RANGE_LISTED_OVERVIEW, 2, today, end);
        CommandResult expectedResult = new CommandResult(expectedMessage, today,
                Optional.of(new PatientAppointment(onToday, onToday.getSchedule().asList().get(0))));
        assertCommandSuccess(command, relativeModel, expectedResult, expectedRelativeModel);
        assertEquals(List.of(onToday, inThreeDays), relativeModel.getFilteredPatientList());

        // An end date of today shows only today's appointments
        command = new ViewAppointmentCommand(Optional.empty(), Optional.of(today), FIXED_CLOCK);
        expectedRelativeModel.updateFilteredPatientList(new AppointmentBetweenDatesPredicate(today, today));
        expectedMessage = String.format(MESSAGE_VIEW_APPOINTMENT_LISTED_OVERVIEW, 1, today);
        expectedResult = new CommandResult(expectedMessage, today, expectedResult.getAppointmentToSelect());
        assertCommandSuccess(command, relativeModel, expectedResult, expectedRelativeModel);
        assertEquals(List.of(onToday), relativeModel.getFilteredPatientList());
    }

    @Test
    public void execute_endOnlyBeforeToday_throwsCommandException() {
        LocalDate yesterday = TODAY.minusDays(1);
        ViewAppointmentCommand command =
                new ViewAppointmentCommand(Optional.empty(), Optional.of(yesterday), FIXED_CLOCK);
        assertCommandFailure(command, model,
                String.format(MESSAGE_VIEW_APPOINTMENT_TO_BEFORE_TODAY, yesterday, TODAY));
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

        // Different clocks -> returns true, as the clock is not part of the command's input
        assertTrue(new ViewAppointmentCommand(Optional.empty(), Optional.of(date1))
                .equals(new ViewAppointmentCommand(Optional.empty(), Optional.of(date1), FIXED_CLOCK)));

        // A one-day range is the same as a single date -> returns true
        assertTrue(command1.equals(new ViewAppointmentCommand(Optional.of(date1), Optional.of(date1))));

        // Same object -> returns true
        assertTrue(command1.equals(command1));

        // Different values -> returns false
        assertFalse(command1.equals(command3));
        assertFalse(command1.equals(new ViewAppointmentCommand(Optional.of(date1), Optional.of(date2))));
        assertFalse(command1.equals(new ViewAppointmentCommand(Optional.of(date1), Optional.empty())));
        assertFalse(command1.equals(new ViewAppointmentCommand(Optional.empty(), Optional.of(date1))));

        // null -> returns false
        assertFalse(command1.equals(null));

        // different types -> returns false
        assertFalse(command1.equals(5.0f));
    }

    @Test
    public void toStringMethod() {
        LocalDate date = LocalDate.of(2026, 10, 15);
        ViewAppointmentCommand command = new ViewAppointmentCommand(Optional.empty(), Optional.of(date));
        String expected = ViewAppointmentCommand.class.getCanonicalName()
                + "{startDate=Optional.empty, endDate=Optional[" + date + "]}";
        assertEquals(expected, command.toString());
    }

    private static PatientAppointment entry(Patient patient, String appointment) {
        return new PatientAppointment(patient, new Appointment(appointment));
    }

    private static Patient patientWithAppointment(String id, LocalDate date) {
        return new PatientBuilder().withId(id).withAppointments(date.toString()).build();
    }
}
