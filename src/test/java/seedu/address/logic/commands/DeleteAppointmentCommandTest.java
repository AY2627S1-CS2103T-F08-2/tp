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
import seedu.address.logic.commands.CommandResult.View;
import seedu.address.model.LaiNurse;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientId;
import seedu.address.testutil.PatientBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for DeleteAppointmentCommand.
 * BENSON has one appointment, on 2026-10-05 09:30, with no details.
 */
public class DeleteAppointmentCommandTest {

    private static final Appointment BENSON_APPOINTMENT = new Appointment("2026-10-05 09:30");

    private final Model model = new ModelManager(getTypicalLaiNurse(), new UserPrefs());

    @Test
    public void execute_delete_success() {
        Patient editedBenson = new PatientBuilder(BENSON).withAppointments().build();
        assertDeleteSuccess(BENSON,
                new DeleteAppointmentCommand(BENSON.getId(), BENSON_APPOINTMENT), editedBenson,
                "2026-10-05 09:30");
    }

    @Test
    public void execute_deleteWithDetails_reportsDetails() {
        Patient bensonWithDetails = new PatientBuilder(BENSON).withAppointments()
                .withAppointment("2026-10-05 09:30", "Bring insulin pen").build();
        model.setPatient(BENSON, bensonWithDetails);
        Patient editedBenson = new PatientBuilder(BENSON).withAppointments().build();

        assertDeleteSuccess(bensonWithDetails,
                new DeleteAppointmentCommand(BENSON.getId(), new Appointment("2026-10-05 09:30")), editedBenson,
                "2026-10-05 09:30; Details: BRING INSULIN PEN");
    }

    @Test
    public void execute_deleteFromDateOnlySlot_success() {
        Patient bensonDateOnly = new PatientBuilder(BENSON).withAppointments("2026-10-05").build();
        model.setPatient(BENSON, bensonDateOnly);
        Patient editedBenson = new PatientBuilder(BENSON).withAppointments().build();

        assertDeleteSuccess(bensonDateOnly,
                new DeleteAppointmentCommand(BENSON.getId(), new Appointment("2026-10-05")), editedBenson,
                "2026-10-05");
    }

    @Test
    public void execute_dateOnlyDoesNotMatchTimed_failureListsThatDay() {
        DeleteAppointmentCommand command =
                new DeleteAppointmentCommand(BENSON.getId(), new Appointment("2026-10-05"));
        assertCommandFailure(command, model,
                String.format(Messages.MESSAGE_APPOINTMENT_NOT_FOUND, BENSON.getId(), "2026-10-05")
                + String.format(Messages.MESSAGE_APPOINTMENTS_ON_DATE, "2026-10-05 09:30"));
    }

    @Test
    public void execute_noAppointmentThatDay_failure() {
        DeleteAppointmentCommand command =
                new DeleteAppointmentCommand(BENSON.getId(), new Appointment("2026-10-07"));
        assertCommandFailure(command, model,
                String.format(Messages.MESSAGE_APPOINTMENT_NOT_FOUND, BENSON.getId(), "2026-10-07"));
    }

    @Test
    public void execute_unknownId_failure() {
        DeleteAppointmentCommand command =
                new DeleteAppointmentCommand(new PatientId("S0000000Z"), BENSON_APPOINTMENT);
        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_PATIENT_ID);
    }

    @Test
    public void getView_returnsAppointments() {
        DeleteAppointmentCommand command =
                new DeleteAppointmentCommand(BENSON.getId(), BENSON_APPOINTMENT);
        assertEquals(Optional.of(View.APPOINTMENTS), command.getView());
    }

    @Test
    public void equals() {
        DeleteAppointmentCommand command =
                new DeleteAppointmentCommand(BENSON.getId(), BENSON_APPOINTMENT);

        // same values -> returns true
        assertTrue(command.equals(new DeleteAppointmentCommand(BENSON.getId(), BENSON_APPOINTMENT)));

        // same object -> returns true
        assertTrue(command.equals(command));

        // null or another type -> returns false
        assertFalse(command.equals(null));
        assertFalse(command.equals(new ClearCommand()));

        // any field different -> returns false
        assertFalse(command.equals(new DeleteAppointmentCommand(new PatientId("S0000000Z"), BENSON_APPOINTMENT)));
        assertFalse(command.equals(new DeleteAppointmentCommand(BENSON.getId(), new Appointment("2026-10-05"))));
    }

    @Test
    public void toStringMethod() {
        DeleteAppointmentCommand command =
                new DeleteAppointmentCommand(BENSON.getId(), BENSON_APPOINTMENT);
        String expected = DeleteAppointmentCommand.class.getCanonicalName() + "{targetId=" + BENSON.getId()
                + ", targetAppointment=" + BENSON_APPOINTMENT + "}";
        assertEquals(expected, command.toString());
    }

    /**
     * Asserts that {@code command} replaces {@code patient} by {@code editedPatient}, reports
     * {@code expectedAppointmentText}, and returns no navigation, since the appointment no longer exists.
     */
    private void assertDeleteSuccess(Patient patient, DeleteAppointmentCommand command, Patient editedPatient,
            String expectedAppointmentText) {
        String expectedMessage = String.format(DeleteAppointmentCommand.MESSAGE_DELETE_APPOINTMENT_SUCCESS,
                editedPatient.getName(), editedPatient.getId(), expectedAppointmentText);
        Model expectedModel = new ModelManager(new LaiNurse(model.getLaiNurse()), new UserPrefs());
        expectedModel.setPatient(patient, editedPatient);
        CommandResult expectedResult = new CommandResult(expectedMessage);

        assertCommandSuccess(command, model, expectedResult, expectedModel);
    }
}
