package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPatients.BENSON;
import static seedu.address.testutil.TypicalPatients.getTypicalLaiNurse;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.CommandResult.Navigation;
import seedu.address.logic.commands.CommandResult.View;
import seedu.address.model.LaiNurse;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.AppointmentDetails;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientAppointment;
import seedu.address.model.patient.PatientId;
import seedu.address.testutil.PatientBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for EditAppointmentCommand.
 * BENSON has one appointment, on 2026-10-05 09:30, with no details.
 */
public class EditAppointmentCommandTest {

    private static final Appointment BENSON_APPOINTMENT = new Appointment("2026-10-05 09:30");
    private static final Appointment NEW_TIME = new Appointment("2026-10-06 10:00");
    private static final AppointmentDetails DETAILS = new AppointmentDetails("Bring insulin pen");

    private final Model model = new ModelManager(getTypicalLaiNurse(), new UserPrefs());

    @Test
    public void constructor_nothingToChange_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new EditAppointmentCommand(BENSON.getId(),
                BENSON_APPOINTMENT, Optional.empty(), Optional.empty()));
    }

    @Test
    public void execute_move_success() {
        Patient editedBenson = new PatientBuilder(BENSON).withAppointments("2026-10-06 10:00").build();
        assertEditSuccess(BENSON, new EditAppointmentCommand(BENSON.getId(), BENSON_APPOINTMENT,
                Optional.of(NEW_TIME), Optional.empty()), editedBenson, NEW_TIME, "2026-10-06 10:00",
                "2026-10-05 09:30");
    }

    @Test
    public void execute_detailsOnly_success() {
        Patient editedBenson = new PatientBuilder(BENSON).withAppointments()
                .withAppointment("2026-10-05 09:30", "Bring insulin pen").build();
        Appointment edited = new Appointment("2026-10-05 09:30", Optional.of(DETAILS));
        assertEditSuccess(BENSON, new EditAppointmentCommand(BENSON.getId(), BENSON_APPOINTMENT,
                Optional.empty(), Optional.of(DETAILS)), editedBenson, edited,
                "2026-10-05 09:30; Details: BRING INSULIN PEN", null);
    }

    @Test
    public void execute_moveAppointmentWithDetails_keepsDetails() {
        Patient bensonWithDetails = new PatientBuilder(BENSON).withAppointments()
                .withAppointment("2026-10-05 09:30", "Wound dressing").build();
        model.setPatient(BENSON, bensonWithDetails);
        Patient editedBenson = new PatientBuilder(BENSON).withAppointments()
                .withAppointment("2026-10-06 10:00", "Wound dressing").build();
        Appointment edited = new Appointment("2026-10-06 10:00", Optional.of(new AppointmentDetails("Wound dressing")));

        assertEditSuccess(bensonWithDetails, new EditAppointmentCommand(BENSON.getId(), BENSON_APPOINTMENT,
                Optional.of(NEW_TIME), Optional.empty()), editedBenson, edited,
                "2026-10-06 10:00; Details: WOUND DRESSING", "2026-10-05 09:30");
    }

    @Test
    public void execute_moveAndReplaceDetails_success() {
        Patient bensonWithDetails = new PatientBuilder(BENSON).withAppointments()
                .withAppointment("2026-10-05 09:30", "Wound dressing").build();
        model.setPatient(BENSON, bensonWithDetails);
        Patient editedBenson = new PatientBuilder(BENSON).withAppointments()
                .withAppointment("2026-10-06 10:00", "Bring insulin pen").build();
        Appointment edited = new Appointment("2026-10-06 10:00", Optional.of(DETAILS));

        assertEditSuccess(bensonWithDetails, new EditAppointmentCommand(BENSON.getId(), BENSON_APPOINTMENT,
                Optional.of(NEW_TIME), Optional.of(DETAILS)), editedBenson, edited,
                "2026-10-06 10:00; Details: BRING INSULIN PEN", "2026-10-05 09:30");
    }

    @Test
    public void execute_moveToSameTimeWithDetails_notReportedAsMoved() {
        Patient editedBenson = new PatientBuilder(BENSON).withAppointments()
                .withAppointment("2026-10-05 09:30", "Bring insulin pen").build();
        Appointment edited = new Appointment("2026-10-05 09:30", Optional.of(DETAILS));
        assertEditSuccess(BENSON, new EditAppointmentCommand(BENSON.getId(), BENSON_APPOINTMENT,
                Optional.of(BENSON_APPOINTMENT), Optional.of(DETAILS)), editedBenson, edited,
                "2026-10-05 09:30; Details: BRING INSULIN PEN", null);
    }

    @Test
    public void execute_dateOnlyToTimedOnSameDay_success() {
        // a date-only appointment can be given a time on its own day
        Patient bensonDateOnly = new PatientBuilder(BENSON).withAppointments("2026-10-05").build();
        model.setPatient(BENSON, bensonDateOnly);
        Patient editedBenson = new PatientBuilder(BENSON).withAppointments("2026-10-05 08:00").build();
        Appointment edited = new Appointment("2026-10-05 08:00");

        assertEditSuccess(bensonDateOnly, new EditAppointmentCommand(BENSON.getId(), new Appointment("2026-10-05"),
                Optional.of(edited), Optional.empty()), editedBenson, edited, "2026-10-05 08:00", "2026-10-05");
    }

    @Test
    public void execute_unknownId_failure() {
        EditAppointmentCommand command = new EditAppointmentCommand(new PatientId("S0000000Z"), BENSON_APPOINTMENT,
                Optional.of(NEW_TIME), Optional.empty());
        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_PATIENT_ID);
    }

    @Test
    public void execute_noAppointmentThatDay_failure() {
        EditAppointmentCommand command = new EditAppointmentCommand(BENSON.getId(), new Appointment("2026-10-07"),
                Optional.of(NEW_TIME), Optional.empty());
        assertCommandFailure(command, model,
                String.format(Messages.MESSAGE_APPOINTMENT_NOT_FOUND, BENSON.getId(), "2026-10-07"));
    }

    @Test
    public void execute_noAppointmentAtThatTime_failureListsThatDay() {
        Patient bensonWithTwo = new PatientBuilder(BENSON).withAppointments("2026-10-05 09:30", "2026-10-05 14:00")
                .build();
        model.setPatient(BENSON, bensonWithTwo);

        // a date alone does not match the timed appointments on that day
        EditAppointmentCommand command = new EditAppointmentCommand(BENSON.getId(), new Appointment("2026-10-05"),
                Optional.of(NEW_TIME), Optional.empty());
        assertCommandFailure(command, model,
                String.format(Messages.MESSAGE_APPOINTMENT_NOT_FOUND, BENSON.getId(), "2026-10-05")
                + String.format(Messages.MESSAGE_APPOINTMENTS_ON_DATE,
                        "2026-10-05 09:30, 2026-10-05 14:00"));
    }

    @Test
    public void execute_moveOntoOtherAppointment_failure() {
        Patient bensonWithTwo = new PatientBuilder(BENSON).withAppointments("2026-10-05 09:30", "2026-10-05 14:00")
                .build();
        model.setPatient(BENSON, bensonWithTwo);

        EditAppointmentCommand command = new EditAppointmentCommand(BENSON.getId(), BENSON_APPOINTMENT,
                Optional.of(new Appointment("2026-10-05 14:00")), Optional.empty());
        assertCommandFailure(command, model,
                String.format(Messages.MESSAGE_APPOINTMENT_TAKEN, BENSON.getId(), "2026-10-05 14:00"));
    }

    @Test
    public void getView_returnsAppointments() {
        EditAppointmentCommand command = new EditAppointmentCommand(BENSON.getId(), BENSON_APPOINTMENT,
                Optional.of(NEW_TIME), Optional.empty());
        assertEquals(Optional.of(View.APPOINTMENTS), command.getView());
    }

    @Test
    public void equals() {
        EditAppointmentCommand command = new EditAppointmentCommand(BENSON.getId(), BENSON_APPOINTMENT,
                Optional.of(NEW_TIME), Optional.of(DETAILS));

        // same values -> returns true
        assertTrue(command.equals(new EditAppointmentCommand(BENSON.getId(), BENSON_APPOINTMENT,
                Optional.of(NEW_TIME), Optional.of(DETAILS))));

        // same object -> returns true
        assertTrue(command.equals(command));

        // null or another type -> returns false
        assertFalse(command.equals(null));
        assertFalse(command.equals(new ClearCommand()));

        // any field different -> returns false
        assertFalse(command.equals(new EditAppointmentCommand(new PatientId("S0000000Z"), BENSON_APPOINTMENT,
                Optional.of(NEW_TIME), Optional.of(DETAILS))));
        assertFalse(command.equals(new EditAppointmentCommand(BENSON.getId(), new Appointment("2026-10-05"),
                Optional.of(NEW_TIME), Optional.of(DETAILS))));
        assertFalse(command.equals(new EditAppointmentCommand(BENSON.getId(), BENSON_APPOINTMENT,
                Optional.empty(), Optional.of(DETAILS))));
        assertFalse(command.equals(new EditAppointmentCommand(BENSON.getId(), BENSON_APPOINTMENT,
                Optional.of(NEW_TIME), Optional.empty())));
    }

    @Test
    public void toStringMethod() {
        EditAppointmentCommand command = new EditAppointmentCommand(BENSON.getId(), BENSON_APPOINTMENT,
                Optional.of(NEW_TIME), Optional.empty());
        String expected = EditAppointmentCommand.class.getCanonicalName() + "{targetId=" + BENSON.getId()
                + ", targetAppointment=" + BENSON_APPOINTMENT + ", newDateTime=" + Optional.of(NEW_TIME)
                + ", newDetails=" + Optional.empty() + "}";
        assertEquals(expected, command.toString());
    }

    /**
     * Asserts that {@code command} replaces {@code patient} by {@code editedPatient}, reports
     * {@code expectedAppointmentText}, and the old date and time if {@code movedFrom} is not null, and selects
     * {@code editedAppointment} in the appointment view.
     */
    private void assertEditSuccess(Patient patient, EditAppointmentCommand command, Patient editedPatient,
            Appointment editedAppointment, String expectedAppointmentText, String movedFrom) {
        String expectedMessage = String.format(EditAppointmentCommand.MESSAGE_EDIT_APPOINTMENT_SUCCESS,
                editedPatient.getName(), editedPatient.getId(), expectedAppointmentText);
        if (movedFrom != null) {
            expectedMessage += String.format(EditAppointmentCommand.MESSAGE_MOVED_FROM, movedFrom);
        }
        Model expectedModel = new ModelManager(new LaiNurse(model.getLaiNurse()), new UserPrefs());
        expectedModel.setPatient(patient, editedPatient);
        CommandResult expectedResult = new CommandResult(expectedMessage)
                .withNavigation(Navigation.toAppointment(new PatientAppointment(editedPatient, editedAppointment)));

        assertCommandSuccess(command, model, expectedResult, expectedModel);
    }
}
