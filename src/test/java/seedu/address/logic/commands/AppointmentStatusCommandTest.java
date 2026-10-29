package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.CommandResult.Navigation;
import seedu.address.logic.commands.CommandResult.View;
import seedu.address.model.LaiNurse;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.AppointmentSchedule;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientAppointment;
import seedu.address.model.patient.PatientId;
import seedu.address.testutil.PatientBuilder;

public class AppointmentStatusCommandTest {

    private Patient patient;
    private Model model;

    @BeforeEach
    public void setUp() {
        patient = new PatientBuilder().withId("S1234567A").withMedicalHistory("Diabetes").withNotes("Call first")
                .withAppointment("2026-10-05 09:30", "Insulin review")
                .withAppointment("2026-10-05 09:30", "Blood test").build();
        LaiNurse data = new LaiNurse();
        data.addPatient(patient);
        model = new ModelManager(data, new UserPrefs());
    }

    @Test
    public void execute_markChangesOnlyTargetStatus_success() {
        Appointment updated = appointment(2).withDone(true);
        Patient expectedPatient = new PatientBuilder(patient)
                .withSchedule(new AppointmentSchedule(List.of(appointment(1), updated))).build();
        Model expectedModel = new ModelManager(new LaiNurse(model.getLaiNurse()), new UserPrefs());
        expectedModel.setPatient(patient, expectedPatient);
        CommandResult expectedResult = new CommandResult(
                "Marked appointment 2 of AMY BEE (S1234567A) as done: 2026-10-05 09:30; Details: BLOOD TEST")
                .withNavigation(Navigation.toAppointment(new PatientAppointment(expectedPatient, updated)));

        assertCommandSuccess(new MarkAppointmentCommand(patient.getId(), 2), model, expectedResult, expectedModel);
        assertFalse(appointment(1).isDone());
        assertFalse(appointment(2).isDone());
    }

    @Test
    public void execute_unmarkRestoresClash_success() throws Exception {
        new MarkAppointmentCommand(patient.getId(), 2).execute(model);
        CommandResult result = new UnmarkAppointmentCommand(patient.getId(), 2).execute(model);
        assertEquals("Unmarked appointment 2 of AMY BEE (S1234567A) as undone: "
                + "2026-10-05 09:30; Details: BLOOD TEST\n"
                + "Warning: it clashes with this patient's appointment #1.", result.getFeedbackToUser());
        assertEquals(Optional.of(View.APPOINTMENTS), result.getView());
        assertEquals(Optional.of(Navigation.toAppointment(new PatientAppointment(patient, appointment(2)))),
                result.getNavigation());
        assertEquals(patient, model.findPatientById(patient.getId()).orElseThrow());
    }

    @Test
    public void execute_unmarkWithoutClash_noWarning() throws Exception {
        Patient onlyVisit = new PatientBuilder(patient).withSchedule(
                new AppointmentSchedule(List.of(appointment(1).withDone(true)))).build();
        model.setPatient(patient, onlyVisit);
        CommandResult result = new UnmarkAppointmentCommand(patient.getId(), 1).execute(model);
        assertEquals("Unmarked appointment 1 of AMY BEE (S1234567A) as undone: "
                + "2026-10-05 09:30; Details: INSULIN REVIEW", result.getFeedbackToUser());
    }

    @Test
    public void execute_repeatedStatus_failureWithoutChanges() throws Exception {
        assertCommandFailure(new UnmarkAppointmentCommand(patient.getId(), 1), model,
                String.format(UnmarkAppointmentCommand.MESSAGE_ALREADY_UNDONE, 1, patient.getId()));
        new MarkAppointmentCommand(patient.getId(), 1).execute(model);
        assertCommandFailure(new MarkAppointmentCommand(patient.getId(), 1), model,
                String.format(MarkAppointmentCommand.MESSAGE_ALREADY_DONE, 1, patient.getId()));
    }

    @Test
    public void execute_missingPatientOrAppointment_failureWithoutChanges() {
        for (boolean done : List.of(false, true)) {
            assertCommandFailure(command(new PatientId("S0000000Z"), 1, done), model,
                    String.format(Messages.MESSAGE_PATIENT_NOT_FOUND, "S0000000Z"));
            assertCommandFailure(command(patient.getId(), 3, done), model,
                    String.format(Messages.MESSAGE_APPOINTMENT_NUMBER_NOT_FOUND, patient.getId(), 3));
        }
    }

    @Test
    public void execute_hiddenPatient_keepsFilterAndUpdatesStatus() throws Exception {
        model.updateFilteredPatientList(ignored -> false);
        new MarkAppointmentCommand(patient.getId(), 1).execute(model);
        assertTrue(model.getFilteredPatientList().isEmpty());
        assertTrue(model.findPatientById(patient.getId()).orElseThrow().getSchedule()
                .findByNumber(1).orElseThrow().isDone());
        new UnmarkAppointmentCommand(patient.getId(), 1).execute(model);
        assertTrue(model.getFilteredPatientList().isEmpty());
        assertEquals(patient, model.findPatientById(patient.getId()).orElseThrow());
    }

    @Test
    public void execute_pastAndFutureDateOnlyVisits_allowed() throws Exception {
        for (String date : List.of("2000-01-01", "2099-12-31")) {
            Patient dated = new PatientBuilder(patient).withAppointments(date).build();
            model.setPatient(model.findPatientById(patient.getId()).orElseThrow(), dated);
            new MarkAppointmentCommand(patient.getId(), 1).execute(model);
            assertTrue(model.findPatientById(patient.getId()).orElseThrow().getSchedule()
                    .findByNumber(1).orElseThrow().isDone());
            new UnmarkAppointmentCommand(patient.getId(), 1).execute(model);
            assertEquals(dated, model.findPatientById(patient.getId()).orElseThrow());
        }
    }

    @Test
    public void constructorAndExecute_invalidArguments_throw() {
        for (boolean done : List.of(false, true)) {
            assertThrows(NullPointerException.class, () -> command(null, 1, done));
            assertThrows(IllegalArgumentException.class, () -> command(patient.getId(), 0, done));
            assertThrows(IllegalArgumentException.class, () -> command(patient.getId(), -1, done));
            assertThrows(NullPointerException.class, () -> command(patient.getId(), 1, done).execute(null));
        }
    }

    @Test
    public void equalsAndToString_distinguishPatientNumberAndStatus() {
        for (boolean done : List.of(false, true)) {
            Command command = command(patient.getId(), 1, done);
            assertTrue(command.equals(command));
            assertTrue(command.equals(command(patient.getId(), 1, done)));
            assertFalse(command.equals(command(patient.getId(), 2, done)));
            assertFalse(command.equals(command(new PatientId("S0000000Z"), 1, done)));
            assertFalse(command.equals(command(patient.getId(), 1, !done)));
            assertFalse(command.equals(null));
            assertFalse(command.equals(new ClearCommand()));
            assertEquals(Optional.of(View.APPOINTMENTS), command.getView());
            assertEquals(command.getClass().getCanonicalName()
                    + "{targetId=S1234567A, targetAppointment=#1, done=" + done + "}", command.toString());
        }
    }

    private Appointment appointment(int number) {
        return patient.getSchedule().findByNumber(number).orElseThrow();
    }

    private Command command(PatientId id, int number, boolean done) {
        return done ? new MarkAppointmentCommand(id, number) : new UnmarkAppointmentCommand(id, number);
    }
}
