package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_PATIENT_NOT_FOUND;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.ViewAppointmentCommand.MESSAGE_VIEW_APPOINTMENT_FROM_LISTED_OVERVIEW;
import static seedu.address.logic.commands.ViewAppointmentCommand.MESSAGE_VIEW_APPOINTMENT_LISTED_OVERVIEW;
import static seedu.address.logic.commands.ViewAppointmentCommand.MESSAGE_VIEW_APPOINTMENT_PATIENT_ALL_LISTED_OVERVIEW;
import static seedu.address.logic.commands.ViewAppointmentCommand.MESSAGE_VIEW_APPOINTMENT_PATIENT_FROM_LISTED_OVERVIEW;
import static seedu.address.logic.commands.ViewAppointmentCommand.MESSAGE_VIEW_APPOINTMENT_PATIENT_LISTED_OVERVIEW;
import static seedu.address.logic.commands.ViewAppointmentCommand.MESSAGE_VIEW_APPOINTMENT_PATIENT_RANGE_LISTED_OVERVIEW;
import static seedu.address.logic.commands.ViewAppointmentCommand.MESSAGE_VIEW_APPOINTMENT_RANGE_LISTED_OVERVIEW;
import static seedu.address.logic.commands.ViewAppointmentCommand.MESSAGE_VIEW_APPOINTMENT_TO_BEFORE_TODAY;
import static seedu.address.logic.commands.ViewAppointmentCommand.MESSAGE_VIEW_APPOINTMENT_WEEK_LISTED_OVERVIEW;
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

import seedu.address.logic.commands.CommandResult.AppointmentRange;
import seedu.address.logic.commands.CommandResult.Navigation;
import seedu.address.logic.commands.CommandResult.View;
import seedu.address.model.LaiNurse;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.patient.AppointmentBetweenDatesPredicate;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientId;
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
                new ViewAppointmentCommand(Optional.empty(), Optional.empty(), (Optional<PatientId>) null));
        assertThrows(NullPointerException.class, () ->
                new ViewAppointmentCommand(Optional.empty(), Optional.empty(), (Clock) null));
        assertThrows(NullPointerException.class, () ->
                new ViewAppointmentCommand(Optional.empty(), Optional.empty(), null, FIXED_CLOCK));
    }

    @Test
    public void execute_noAppointmentsFound_emptyList() {
        LocalDate date = LocalDate.of(2030, 1, 1); // Far future, no appointments
        ViewAppointmentCommand command = new ViewAppointmentCommand(date);

        // Create expected model with same filtered list state
        expectedModel.updateFilteredPatientList(new AppointmentBetweenDatesPredicate(date, date));

        String expectedMessage = String.format(MESSAGE_VIEW_APPOINTMENT_LISTED_OVERVIEW, 0, date.toString());
        // the appointment view shows the range on one page, here with no appointments
        assertCommandSuccess(command, model, pageResult(expectedMessage, new AppointmentRange(date, date)),
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
        assertCommandSuccess(command, model, pageResult(expectedMessage, new AppointmentRange(date, date)),
                expectedModel);
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
        assertCommandSuccess(command, model, pageResult(expectedMessage, new AppointmentRange(start, end)),
                expectedModel);
        assertEquals(List.of(BENSON, DANIEL), model.getFilteredPatientList());
    }

    @Test
    public void execute_rangeEndsBeforeSecondAppointment_onlyFirstFound() {
        LocalDate start = LocalDate.of(2026, 10, 1);
        LocalDate end = LocalDate.of(2026, 10, 5);
        ViewAppointmentCommand command = new ViewAppointmentCommand(Optional.of(start), Optional.of(end));

        expectedModel.updateFilteredPatientList(new AppointmentBetweenDatesPredicate(start, end));
        String expectedMessage = String.format(MESSAGE_VIEW_APPOINTMENT_RANGE_LISTED_OVERVIEW, 1, start, end);
        assertCommandSuccess(command, model, pageResult(expectedMessage, new AppointmentRange(start, end)),
                expectedModel);
        assertEquals(List.of(BENSON), model.getFilteredPatientList());
    }

    @Test
    public void execute_startOnly_appointmentsOnOrAfterStartFound() {
        LocalDate start = LocalDate.of(2026, 10, 6);
        ViewAppointmentCommand command = new ViewAppointmentCommand(Optional.of(start), Optional.empty());

        expectedModel.updateFilteredPatientList(new AppointmentBetweenDatesPredicate(start, LocalDate.MAX));
        String expectedMessage = String.format(MESSAGE_VIEW_APPOINTMENT_FROM_LISTED_OVERVIEW, 1, start);
        // a range with no end ends at LocalDate.MAX
        assertCommandSuccess(command, model,
                pageResult(expectedMessage, new AppointmentRange(start, LocalDate.MAX)), expectedModel);
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
        // a range with no start starts today
        CommandResult expectedResult = pageResult(expectedMessage, new AppointmentRange(today, end));
        assertCommandSuccess(command, relativeModel, expectedResult, expectedRelativeModel);
        assertEquals(List.of(onToday, inThreeDays), relativeModel.getFilteredPatientList());

        // An end date of today shows only today's appointments
        command = new ViewAppointmentCommand(Optional.empty(), Optional.of(today), FIXED_CLOCK);
        expectedRelativeModel.updateFilteredPatientList(new AppointmentBetweenDatesPredicate(today, today));
        expectedMessage = String.format(MESSAGE_VIEW_APPOINTMENT_LISTED_OVERVIEW, 1, today);
        expectedResult = pageResult(expectedMessage, new AppointmentRange(today, today));
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
    public void execute_plain_showsThisWeek() {
        LaiNurse laiNurse = laiNurseWithDatedAppointments();
        Model relativeModel = new ModelManager(laiNurse, new UserPrefs());
        Model expectedRelativeModel = new ModelManager(laiNurse, new UserPrefs());
        // TODAY is a Monday, so this week is TODAY to the Sunday after it
        LocalDate sunday = TODAY.plusDays(6);

        ViewAppointmentCommand command = new ViewAppointmentCommand(
                Optional.empty(), Optional.empty(), FIXED_CLOCK);
        expectedRelativeModel.updateFilteredPatientList(new AppointmentBetweenDatesPredicate(TODAY, sunday));
        String expectedMessage = String.format(MESSAGE_VIEW_APPOINTMENT_WEEK_LISTED_OVERVIEW, 2, TODAY, sunday);
        // No range, so the appointment view shows the weekly view of this week, and the next appointment from now
        CommandResult expectedResult = new CommandResult(expectedMessage)
                .withNavigation(Navigation.toView(View.APPOINTMENTS));
        assertCommandSuccess(command, relativeModel, expectedResult, expectedRelativeModel);
        assertEquals(List.of(patientWithAppointment("S0000002B", TODAY),
                patientWithAppointment("S0000003C", TODAY.plusDays(3))),
                relativeModel.getFilteredPatientList());
    }

    @Test
    public void execute_plainOnSunday_showsTheWeekFromMonday() {
        LaiNurse laiNurse = laiNurseWithDatedAppointments();
        Model relativeModel = new ModelManager(laiNurse, new UserPrefs());
        Model expectedRelativeModel = new ModelManager(laiNurse, new UserPrefs());
        LocalDate sunday = TODAY.plusDays(6);
        Clock sundayClock = Clock.fixed(Instant.parse("2026-10-18T10:00:00Z"), ZoneOffset.UTC);

        ViewAppointmentCommand command = new ViewAppointmentCommand(Optional.empty(), Optional.empty(), sundayClock);
        expectedRelativeModel.updateFilteredPatientList(new AppointmentBetweenDatesPredicate(TODAY, sunday));
        String expectedMessage = String.format(MESSAGE_VIEW_APPOINTMENT_WEEK_LISTED_OVERVIEW, 2, TODAY, sunday);
        assertCommandSuccess(command, relativeModel, new CommandResult(expectedMessage)
                .withNavigation(Navigation.toView(View.APPOINTMENTS)), expectedRelativeModel);
    }

    @Test
    public void execute_idOnly_showsEveryAppointmentOfThatPatient() {
        Patient multi = multiAppointmentPatient();
        Patient other = patientWithAppointment("S0000001A", TODAY);
        LaiNurse laiNurse = new LaiNurseBuilder().withPatient(multi).withPatient(other).build();
        Model relativeModel = new ModelManager(laiNurse, new UserPrefs());
        Model expectedRelativeModel = new ModelManager(laiNurse, new UserPrefs());

        PatientId multiId = multi.getId();
        ViewAppointmentCommand command = new ViewAppointmentCommand(
                Optional.empty(), Optional.empty(), Optional.of(multiId), FIXED_CLOCK);
        expectedRelativeModel.updateFilteredPatientList(candidate -> candidate.getId().equals(multiId));
        // all three appointments, past and upcoming, from the first to the last
        String expectedMessage = String.format(
                MESSAGE_VIEW_APPOINTMENT_PATIENT_ALL_LISTED_OVERVIEW, 3, multi.getName().fullName);
        CommandResult expectedResult = pageResult(expectedMessage,
                new AppointmentRange(TODAY.minusDays(1), TODAY.plusDays(3), multiId));
        assertCommandSuccess(command, relativeModel, expectedResult, expectedRelativeModel);
        assertEquals(List.of(multi), relativeModel.getFilteredPatientList());
    }

    @Test
    public void execute_idOnlyWithoutAppointments_emptyPageOfToday() {
        Patient none = new PatientBuilder().withId("S0000006F").build();
        LaiNurse laiNurse = new LaiNurseBuilder().withPatient(none).build();
        Model relativeModel = new ModelManager(laiNurse, new UserPrefs());
        Model expectedRelativeModel = new ModelManager(laiNurse, new UserPrefs());

        ViewAppointmentCommand command = new ViewAppointmentCommand(
                Optional.empty(), Optional.empty(), Optional.of(none.getId()), FIXED_CLOCK);
        expectedRelativeModel.updateFilteredPatientList(candidate -> false);
        String expectedMessage = String.format(
                MESSAGE_VIEW_APPOINTMENT_PATIENT_ALL_LISTED_OVERVIEW, 0, none.getName().fullName);
        assertCommandSuccess(command, relativeModel,
                pageResult(expectedMessage, new AppointmentRange(TODAY, TODAY, none.getId())), expectedRelativeModel);
        assertEquals(List.of(), relativeModel.getFilteredPatientList());
    }

    @Test
    public void execute_idWithDate_showsThatPatientsAppointmentsOnDate() {
        Patient multi = multiAppointmentPatient();
        Patient other = patientWithAppointment("S0000001A", TODAY);
        LaiNurse laiNurse = new LaiNurseBuilder().withPatient(multi).withPatient(other).build();
        Model relativeModel = new ModelManager(laiNurse, new UserPrefs());
        Model expectedRelativeModel = new ModelManager(laiNurse, new UserPrefs());

        PatientId multiId = multi.getId();
        ViewAppointmentCommand command = new ViewAppointmentCommand(
                Optional.of(TODAY), Optional.of(TODAY), Optional.of(multiId), FIXED_CLOCK);
        expectedRelativeModel.updateFilteredPatientList(candidate -> candidate.getId().equals(multiId)
                && new AppointmentBetweenDatesPredicate(TODAY, TODAY).test(candidate));
        String expectedMessage = String.format(
                MESSAGE_VIEW_APPOINTMENT_PATIENT_LISTED_OVERVIEW, 1, multi.getName().fullName, TODAY);
        CommandResult expectedResult = pageResult(expectedMessage,
                new AppointmentRange(TODAY, TODAY, multiId));
        assertCommandSuccess(command, relativeModel, expectedResult, expectedRelativeModel);
        assertEquals(List.of(multi), relativeModel.getFilteredPatientList());
    }

    @Test
    public void execute_idWithRange_showsPastAndUpcoming() {
        Patient multi = multiAppointmentPatient();
        Patient other = patientWithAppointment("S0000001A", TODAY);
        LaiNurse laiNurse = new LaiNurseBuilder().withPatient(multi).withPatient(other).build();
        Model relativeModel = new ModelManager(laiNurse, new UserPrefs());
        Model expectedRelativeModel = new ModelManager(laiNurse, new UserPrefs());

        LocalDate start = TODAY.minusDays(1);
        LocalDate end = TODAY.plusDays(3);
        PatientId multiId = multi.getId();
        ViewAppointmentCommand command = new ViewAppointmentCommand(
                Optional.of(start), Optional.of(end), Optional.of(multiId), FIXED_CLOCK);
        expectedRelativeModel.updateFilteredPatientList(candidate -> candidate.getId().equals(multiId)
                && new AppointmentBetweenDatesPredicate(start, end).test(candidate));
        // --from unlocks the past, so all three appointments are in the range
        String expectedMessage = String.format(
                MESSAGE_VIEW_APPOINTMENT_PATIENT_RANGE_LISTED_OVERVIEW, 3, multi.getName().fullName, start, end);
        CommandResult expectedResult = pageResult(expectedMessage,
                new AppointmentRange(start, end, multiId));
        assertCommandSuccess(command, relativeModel, expectedResult, expectedRelativeModel);
        assertEquals(List.of(multi), relativeModel.getFilteredPatientList());
    }

    @Test
    public void execute_idWithoutAppointmentsInRange_emptyPage() {
        Patient pastOnly = patientWithAppointment("S0000001A", TODAY.minusDays(30));
        LaiNurse laiNurse = new LaiNurseBuilder().withPatient(pastOnly).build();
        Model relativeModel = new ModelManager(laiNurse, new UserPrefs());
        Model expectedRelativeModel = new ModelManager(laiNurse, new UserPrefs());

        // --from today, as --id alone would show the past appointment too
        ViewAppointmentCommand command = new ViewAppointmentCommand(
                Optional.of(TODAY), Optional.empty(), Optional.of(pastOnly.getId()), FIXED_CLOCK);
        expectedRelativeModel.updateFilteredPatientList(candidate -> candidate.getId()
                .equals(pastOnly.getId()) && new AppointmentBetweenDatesPredicate(TODAY, LocalDate.MAX)
                .test(candidate));
        String expectedMessage = String.format(
                MESSAGE_VIEW_APPOINTMENT_PATIENT_FROM_LISTED_OVERVIEW, 0, pastOnly.getName().fullName, TODAY);
        CommandResult expectedResult = pageResult(expectedMessage,
                new AppointmentRange(TODAY, LocalDate.MAX, pastOnly.getId()));
        assertCommandSuccess(command, relativeModel, expectedResult, expectedRelativeModel);
        assertEquals(List.of(), relativeModel.getFilteredPatientList());
    }

    @Test
    public void execute_idUnknownPatient_throwsCommandException() {
        ViewAppointmentCommand command = new ViewAppointmentCommand(
                Optional.empty(), Optional.empty(), Optional.of(new PatientId("S9999999Z")), FIXED_CLOCK);
        assertCommandFailure(command, model, String.format(MESSAGE_PATIENT_NOT_FOUND, "S9999999Z"));
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

        // Different patients -> returns false
        assertFalse(new ViewAppointmentCommand(Optional.empty(), Optional.empty(), Optional.of(new PatientId(
                "S1234567A"))).equals(new ViewAppointmentCommand(Optional.empty(), Optional.empty(),
                        Optional.of(new PatientId("S2345678B")))));

        // With and without a patient -> returns false
        assertFalse(command1.equals(new ViewAppointmentCommand(Optional.of(date1), Optional.of(date1),
                Optional.of(new PatientId("S1234567A")))));

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
                + "{startDate=Optional.empty, endDate=Optional[" + date + "], patientId=Optional.empty}";
        assertEquals(expected, command.toString());
    }

    /**
     * Returns the result of a viewappt command that shows {@code range} on one page and selects the next
     * appointment from now.
     */
    private static CommandResult pageResult(String message, AppointmentRange range) {
        return new CommandResult(message, range).withNavigation(Navigation.toView(View.APPOINTMENTS));
    }

    private static Patient patientWithAppointment(String id, LocalDate date) {
        return new PatientBuilder().withId(id).withAppointments(date.toString()).build();
    }

    /** Returns a LaiNurse with four patients, having appointments yesterday, today, in three days and in a month. */
    private static LaiNurse laiNurseWithDatedAppointments() {
        return new LaiNurseBuilder()
                .withPatient(patientWithAppointment("S0000001A", TODAY.minusDays(1)))
                .withPatient(patientWithAppointment("S0000002B", TODAY))
                .withPatient(patientWithAppointment("S0000003C", TODAY.plusDays(3)))
                .withPatient(patientWithAppointment("S0000004D", TODAY.plusDays(30)))
                .build();
    }

    /** Returns a patient with the default name and three appointments: yesterday, today and in three days. */
    private static Patient multiAppointmentPatient() {
        return new PatientBuilder().withId("S0000005E")
                .withAppointments(TODAY.minusDays(1).toString(), TODAY.toString(),
                        TODAY.plusDays(3).toString()).build();
    }
}
