package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
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
 * Contains integration tests (interaction with the Model) and unit tests for AddAppointmentCommand.
 * BENSON has one appointment, on 2026-10-05 09:30, with no details.
 */
public class AddAppointmentCommandTest {

    private static final Appointment BENSON_APPOINTMENT = new Appointment("2026-10-05 09:30");
    private static final Appointment NEW_APPOINTMENT = new Appointment("2026-10-06 10:00");
    private static final AppointmentDetails DETAILS = new AppointmentDetails("Bring insulin pen");

    private final Model model = new ModelManager(getTypicalLaiNurse(), new UserPrefs());

    @Test
    public void execute_addOnAnotherDay_success() {
        Patient editedBenson = new PatientBuilder(BENSON)
                .withAppointments("2026-10-05 09:30", "2026-10-06 10:00").build();
        assertAddSuccess(BENSON,
                new AddAppointmentCommand(BENSON.getId(), NEW_APPOINTMENT), editedBenson, NEW_APPOINTMENT,
                "2026-10-06 10:00");
    }

    @Test
    public void execute_addToEmptySchedule_success() {
        Patient bensonEmpty = new PatientBuilder(BENSON).withAppointments().build();
        model.setPatient(BENSON, bensonEmpty);
        Patient editedBenson = new PatientBuilder(BENSON).withAppointments("2026-10-10").build();
        assertAddSuccess(bensonEmpty,
                new AddAppointmentCommand(BENSON.getId(), new Appointment("2026-10-10")), editedBenson,
                new Appointment("2026-10-10"), "2026-10-10");
    }

    @Test
    public void execute_addWithDetails_success() {
        Patient editedBenson = new PatientBuilder(BENSON)
                .withAppointments("2026-10-05 09:30")
                .withAppointment("2026-10-06 10:00", "Bring insulin pen").build();
        Appointment appointmentWithDetails =
                new Appointment("2026-10-06 10:00", Optional.of(DETAILS));
        assertAddSuccess(BENSON,
                new AddAppointmentCommand(BENSON.getId(), appointmentWithDetails), editedBenson,
                appointmentWithDetails, "2026-10-06 10:00; Details: BRING INSULIN PEN");
    }

    @Test
    public void execute_addTimedOnSameDayAsDateOnly_success() {
        // a timed slot and a date-only slot on one day are different appointments
        Patient bensonDateOnly = new PatientBuilder(BENSON).withAppointments("2026-10-05").build();
        model.setPatient(BENSON, bensonDateOnly);
        Patient editedBenson = new PatientBuilder(BENSON)
                .withAppointments("2026-10-05", "2026-10-05 09:30").build();
        assertAddSuccess(bensonDateOnly,
                new AddAppointmentCommand(BENSON.getId(), BENSON_APPOINTMENT), editedBenson, BENSON_APPOINTMENT,
                "2026-10-05 09:30");
    }

    @Test
    public void execute_duplicateSlot_failure() {
        String expectedMessage = String.format(AddAppointmentCommand.MESSAGE_APPOINTMENT_TAKEN, BENSON.getId(),
                "2026-10-05 09:30");
        assertCommandFailure(new AddAppointmentCommand(BENSON.getId(), BENSON_APPOINTMENT), model, expectedMessage);
        // the slot is taken whatever the details of the new appointment
        assertCommandFailure(new AddAppointmentCommand(BENSON.getId(),
                new Appointment("2026-10-05 09:30", Optional.of(DETAILS))), model, expectedMessage);
    }

    @Test
    public void execute_unknownId_failure() {
        AddAppointmentCommand command = new AddAppointmentCommand(new PatientId("S0000000Z"), NEW_APPOINTMENT);
        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_PATIENT_ID);
    }

    @Test
    public void getView_returnsAppointments() {
        AddAppointmentCommand command = new AddAppointmentCommand(BENSON.getId(), NEW_APPOINTMENT);
        assertEquals(Optional.of(View.APPOINTMENTS), command.getView());
    }

    @Test
    public void equals() {
        AddAppointmentCommand command = new AddAppointmentCommand(BENSON.getId(), NEW_APPOINTMENT);

        // same values -> returns true
        assertTrue(command.equals(new AddAppointmentCommand(BENSON.getId(), NEW_APPOINTMENT)));

        // same object -> returns true
        assertTrue(command.equals(command));

        // null or another type -> returns false
        assertFalse(command.equals(null));
        assertFalse(command.equals(new ClearCommand()));

        // any field different -> returns false
        assertFalse(command.equals(new AddAppointmentCommand(new PatientId("S0000000Z"), NEW_APPOINTMENT)));
        assertFalse(command.equals(new AddAppointmentCommand(BENSON.getId(), BENSON_APPOINTMENT)));
        assertFalse(command.equals(new AddAppointmentCommand(BENSON.getId(),
                new Appointment("2026-10-06 10:00", Optional.of(DETAILS)))));
    }

    @Test
    public void toStringMethod() {
        AddAppointmentCommand command = new AddAppointmentCommand(BENSON.getId(), NEW_APPOINTMENT);
        String expected = AddAppointmentCommand.class.getCanonicalName() + "{targetId=" + BENSON.getId()
                + ", toAdd=" + NEW_APPOINTMENT + "}";
        assertEquals(expected, command.toString());
    }

    /**
     * Asserts that {@code command} replaces {@code patient} by {@code editedPatient}, reports
     * {@code expectedAppointmentText}, and selects {@code appointment} in the appointment view.
     */
    private void assertAddSuccess(Patient patient, AddAppointmentCommand command, Patient editedPatient,
            Appointment appointment, String expectedAppointmentText) {
        String expectedMessage = String.format(AddAppointmentCommand.MESSAGE_ADD_APPOINTMENT_SUCCESS,
                editedPatient.getName(), editedPatient.getId(), expectedAppointmentText);
        Model expectedModel = new ModelManager(new LaiNurse(model.getLaiNurse()), new UserPrefs());
        expectedModel.setPatient(patient, editedPatient);
        CommandResult expectedResult = new CommandResult(expectedMessage)
                .withNavigation(Navigation.toAppointment(new PatientAppointment(editedPatient, appointment)));

        assertCommandSuccess(command, model, expectedResult, expectedModel);
    }
}
