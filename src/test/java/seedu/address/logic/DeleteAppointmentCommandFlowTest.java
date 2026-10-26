package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.CommandResult.View;
import seedu.address.logic.commands.DeleteAppointmentCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.LaiNurse;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientId;
import seedu.address.storage.JsonLaiNurseStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PatientBuilder;
import seedu.address.testutil.SampleData;

/**
 * Tests deleteappt commands from user input through model updates and JSON persistence, starting from the sample data.
 */
public class DeleteAppointmentCommandFlowTest {
    // appointments 2026-10-05 09:30 (INSULIN REVIEW) and 2026-11-02 (MONTHLY BLOOD TEST)
    private static final String ALEX_ID = "S1234567A";
    private static final String BERNICE_ID = "S2345678B"; // appointment 2026-10-06, with no details

    private static final String INVALID_FORMAT_MESSAGE =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteAppointmentCommand.MESSAGE_USAGE);

    @TempDir
    public Path temporaryFolder;

    private Model model;
    private Storage storage;
    private Logic logic;

    @BeforeEach
    public void setUp() throws Exception {
        model = new ModelManager(SampleData.getSampleLaiNurse(), new UserPrefs());
        storage = new StorageManager(new JsonLaiNurseStorage(temporaryFolder.resolve("laiNurse.json")),
                new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json")));
        storage.saveLaiNurse(model.getLaiNurse());
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_deleteTimedAppointment_persists() throws Exception {
        Patient expected = new PatientBuilder(patient(ALEX_ID)).withSchedule(patient(ALEX_ID).getSchedule()
                        .remove(patient(ALEX_ID).getSchedule().findByNumber(1).orElseThrow())).build();
        assertDeletePersisted("deleteappt S1234567A 1", expected,
                "2026-10-05 09:30; Details: INSULIN REVIEW");
    }

    @Test
    public void execute_deleteDateOnlyAppointment_persists() throws Exception {
        Patient expected = new PatientBuilder(patient(ALEX_ID)).withAppointments()
                .withAppointment("2026-10-05 09:30", "Insulin review").build();
        assertDeletePersisted("deleteappt s1234567a\t2", expected,
                "2026-11-02; Details: MONTHLY BLOOD TEST");
    }

    @Test
    public void execute_patientNotInDisplayedList_deleted() throws Exception {
        logic.execute("find --name bernice");
        List<Patient> expectedFilteredList = List.copyOf(model.getFilteredPatientList());
        Patient expected = new PatientBuilder(patient(ALEX_ID)).withSchedule(patient(ALEX_ID).getSchedule()
                        .remove(patient(ALEX_ID).getSchedule().findByNumber(1).orElseThrow())).build();

        CommandResult result = logic.execute("deleteappt S1234567A 1");

        assertEquals(expected, patient(ALEX_ID));
        assertEquals(Optional.of(View.APPOINTMENTS), result.getView());
        // the find filter stays, so the patient remains hidden in the patient list
        assertEquals(expectedFilteredList, model.getFilteredPatientList());
    }

    @Test
    public void execute_missingAppointment_rejectedWithoutChanges() throws Exception {
        assertRejectedCommandLeavesDataUnchanged("deleteappt S1234567A 99",
                CommandException.class,
                String.format(Messages.MESSAGE_APPOINTMENT_NUMBER_NOT_FOUND, ALEX_ID, 99));
    }

    @Test
    public void execute_invalidInput_rejectedWithoutChanges() throws Exception {
        assertRejectedCommandLeavesDataUnchanged("deleteappt S0000000Z 1",
                CommandException.class, Messages.MESSAGE_INVALID_PATIENT_ID);
        assertRejectedCommandLeavesDataUnchanged("deleteappt S1234567A", ParseException.class, INVALID_FORMAT_MESSAGE);
        for (String number : List.of("0", "-1", "+1", "1.5", "2147483648", "2026-10-05")) {
            assertRejectedCommandLeavesDataUnchanged("deleteappt S1234567A " + number, ParseException.class,
                    Appointment.MESSAGE_NUMBER_CONSTRAINTS);
        }
        assertRejectedCommandLeavesDataUnchanged(
                "deleteappt S1234567A 1 --appt 2026-11-02", ParseException.class,
                INVALID_FORMAT_MESSAGE);
        assertRejectedCommandLeavesDataUnchanged("deleteappt S1234567A --appt 2026-10-05", ParseException.class,
                INVALID_FORMAT_MESSAGE);
    }

    /**
     * Asserts that {@code command} succeeds, replaces the patient with {@code expected}'s ID by {@code expected},
     * reports {@code expectedAppointmentText}, selects nothing in the appointment view, and saves the change to the
     * data file.
     */
    private void assertDeletePersisted(String command, Patient expected, String expectedAppointmentText)
            throws Exception {
        int deletedNumber = patient(expected.getId().value).getSchedule().asList().stream()
                .filter(appointment -> expectedAppointmentText.split(";", 2)[0].equals(appointment.value))
                .findFirst().orElseThrow().getNumber();
        LaiNurse expectedLaiNurse = new LaiNurse(model.getLaiNurse());
        expectedLaiNurse.setPatient(patient(expected.getId().value), expected);

        CommandResult result = logic.execute(command);

        assertEquals(String.format(DeleteAppointmentCommand.MESSAGE_DELETE_APPOINTMENT_SUCCESS, deletedNumber,
                expected.getName(),
                expected.getId(), expectedAppointmentText), result.getFeedbackToUser());
        assertEquals(Optional.empty(), result.getNavigation(), "no appointment exists to select any more");
        assertTrue(result.isSelectionCleared(), "the selection is cleared, as with delete and clear");
        assertEquals(expectedLaiNurse, model.getLaiNurse());
        assertEquals(expectedLaiNurse, storage.readLaiNurse().orElseThrow());
    }

    /**
     * Asserts that {@code command} fails with the given exception and message,
     * and leaves the model, the displayed list and the data file unchanged.
     */
    private void assertRejectedCommandLeavesDataUnchanged(String command, Class<? extends Throwable> expectedException,
            String expectedMessage) throws Exception {
        LaiNurse expectedLaiNurse = new LaiNurse(model.getLaiNurse());
        List<Patient> expectedFilteredList = List.copyOf(model.getFilteredPatientList());
        String expectedJson = Files.readString(storage.getLaiNurseFilePath());

        assertThrows(expectedException, expectedMessage, () -> logic.execute(command));

        assertEquals(expectedLaiNurse, model.getLaiNurse());
        assertEquals(expectedFilteredList, model.getFilteredPatientList());
        assertEquals(expectedJson, Files.readString(storage.getLaiNurseFilePath()));
    }

    private Patient patient(String id) {
        PatientId patientId = new PatientId(id);
        return model.getLaiNurse().getPatientList().stream()
                .filter(patient -> patient.getId().equals(patientId)).findFirst().orElseThrow();
    }
    @Test
    public void execute_deleteNumberFromSameTime_keepsOtherVisitsAndNumbers() throws Exception {
        logic.execute("addappt S1234567A 2026-10-05 09:30");
        CommandResult result = logic.execute("deleteappt S1234567A 1");
        assertEquals("Deleted appointment 1 of ALEX YEOH (S1234567A): "
                + "2026-10-05 09:30; Details: INSULIN REVIEW", result.getFeedbackToUser());
        assertEquals(List.of(3, 2), patient(ALEX_ID).getSchedule().asList().stream()
                .map(Appointment::getNumber).toList());
        assertTrue(result.isSelectionCleared());
        assertEquals(model.getLaiNurse(), storage.readLaiNurse().orElseThrow());
    }

}
