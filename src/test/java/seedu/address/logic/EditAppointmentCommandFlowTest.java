package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import seedu.address.logic.commands.CommandResult.Navigation;
import seedu.address.logic.commands.CommandResult.View;
import seedu.address.logic.commands.EditAppointmentCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.LaiNurse;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.AppointmentDetails;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientAppointment;
import seedu.address.model.patient.PatientId;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.JsonLaiNurseStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PatientBuilder;

/**
 * Tests editappt commands from user input through model updates and JSON persistence, starting from the sample data.
 */
public class EditAppointmentCommandFlowTest {
    // appointments 2026-10-05 09:30 (INSULIN REVIEW) and 2026-11-02 (MONTHLY BLOOD TEST)
    private static final String ALEX_ID = "S1234567A";
    private static final String BERNICE_ID = "S2345678B"; // appointment 2026-10-06, with no details

    private static final String INVALID_FORMAT_MESSAGE =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditAppointmentCommand.MESSAGE_USAGE);

    @TempDir
    public Path temporaryFolder;

    private Model model;
    private Storage storage;
    private Logic logic;

    @BeforeEach
    public void setUp() throws Exception {
        model = new ModelManager(SampleDataUtil.getSampleLaiNurse(), new UserPrefs());
        storage = new StorageManager(new JsonLaiNurseStorage(temporaryFolder.resolve("laiNurse.json")),
                new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json")));
        storage.saveLaiNurse(model.getLaiNurse());
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_moveAppointment_keepsDetailsAndPersists() throws Exception {
        Patient expected = new PatientBuilder(patient(ALEX_ID)).withAppointments()
                .withAppointment("2026-10-06 10:00", "Insulin review")
                .withAppointment("2026-11-02", "Monthly blood test").build();
        assertEditPersisted("editappt S1234567A --appt 2026-10-05 09:30 --to 2026-10-06 10:00", expected,
                appointment("2026-10-06 10:00", "Insulin review"),
                "2026-10-06 10:00; Details: INSULIN REVIEW (moved from 2026-10-05 09:30)");
    }

    @Test
    public void execute_setDetails_persists() throws Exception {
        Patient expected = new PatientBuilder(patient(BERNICE_ID)).withAppointments()
                .withAppointment("2026-10-06", "Check blood pressure").build();
        assertEditPersisted("editappt s2345678b --details Check   blood pressure --appt 2026-10-06", expected,
                appointment("2026-10-06", "Check blood pressure"), "2026-10-06; Details: CHECK BLOOD PRESSURE");
    }

    @Test
    public void execute_moveAndReplaceDetails_persists() throws Exception {
        Patient expected = new PatientBuilder(patient(ALEX_ID)).withAppointments()
                .withAppointment("2026-10-05 09:30", "Insulin review")
                .withAppointment("2026-11-03 08:00", "Fasting blood test").build();
        assertEditPersisted("editappt S1234567A\t--appt\t2026-11-02\t--to\t2026-11-03 08:00"
                + "\t--details\tFasting blood test", expected, appointment("2026-11-03 08:00", "Fasting blood test"),
                "2026-11-03 08:00; Details: FASTING BLOOD TEST (moved from 2026-11-02)");
    }

    @Test
    public void execute_patientNotInDisplayedList_edited() throws Exception {
        logic.execute("find --name bernice");
        Patient expected = new PatientBuilder(patient(ALEX_ID)).withAppointments()
                .withAppointment("2026-10-05 09:30", "Bring insulin pen")
                .withAppointment("2026-11-02", "Monthly blood test").build();

        CommandResult result = logic.execute("editappt S1234567A --appt 2026-10-05 09:30 --details Bring insulin pen");

        assertEquals(expected, patient(ALEX_ID));
        assertEquals(Optional.of(View.APPOINTMENTS), result.getView());
    }

    @Test
    public void execute_missingAppointment_rejectedWithoutChanges() throws Exception {
        // a date alone does not match the timed appointment on that day, which the error lists
        assertRejectedCommandLeavesDataUnchanged("editappt S1234567A --appt 2026-10-05 --to 2026-10-06",
                CommandException.class,
                String.format(EditAppointmentCommand.MESSAGE_APPOINTMENT_NOT_FOUND, ALEX_ID, "2026-10-05")
                + String.format(EditAppointmentCommand.MESSAGE_APPOINTMENTS_ON_DATE, "2026-10-05 09:30"));

        // nothing on that day
        assertRejectedCommandLeavesDataUnchanged("editappt S1234567A --appt 2026-10-20 --details Check",
                CommandException.class,
                String.format(EditAppointmentCommand.MESSAGE_APPOINTMENT_NOT_FOUND, ALEX_ID, "2026-10-20"));
    }

    @Test
    public void execute_moveOntoOtherAppointment_rejectedWithoutChanges() throws Exception {
        assertRejectedCommandLeavesDataUnchanged("editappt S1234567A --appt 2026-10-05 09:30 --to 2026-11-02",
                CommandException.class,
                String.format(EditAppointmentCommand.MESSAGE_APPOINTMENT_TAKEN, ALEX_ID, "2026-11-02"));
    }

    @Test
    public void execute_invalidInput_rejectedWithoutChanges() throws Exception {
        assertRejectedCommandLeavesDataUnchanged("editappt S0000000Z --appt 2026-10-05 --to 2026-10-06",
                CommandException.class, Messages.MESSAGE_INVALID_PATIENT_ID);
        assertRejectedCommandLeavesDataUnchanged("editappt S1234567A --appt 2026-10-05 09:30",
                ParseException.class, EditAppointmentCommand.MESSAGE_NOT_EDITED);
        assertRejectedCommandLeavesDataUnchanged("editappt S1234567A --to 2026-10-06", ParseException.class,
                INVALID_FORMAT_MESSAGE);
        assertRejectedCommandLeavesDataUnchanged("editappt S1234567A --appt 2026-10-05 09:30 --to 2026-02-30",
                ParseException.class, Appointment.MESSAGE_CONSTRAINTS);
        assertRejectedCommandLeavesDataUnchanged("editappt S1234567A --appt 2026-10-05 09:30 --details",
                ParseException.class, AppointmentDetails.MESSAGE_CONSTRAINTS);
    }

    /**
     * Asserts that {@code command} succeeds, replaces the patient with {@code expected}'s ID by {@code expected},
     * reports {@code expectedAppointmentText}, selects {@code expectedAppointment} in the appointment view, and
     * saves the change to the data file.
     */
    private void assertEditPersisted(String command, Patient expected, Appointment expectedAppointment,
            String expectedAppointmentText) throws Exception {
        LaiNurse expectedLaiNurse = new LaiNurse(model.getLaiNurse());
        expectedLaiNurse.setPatient(patient(expected.getId().value), expected);

        CommandResult result = logic.execute(command);

        assertEquals(String.format(EditAppointmentCommand.MESSAGE_EDIT_APPOINTMENT_SUCCESS, expected.getName(),
                expected.getId(), expectedAppointmentText), result.getFeedbackToUser());
        assertEquals(Optional.of(Navigation.toAppointment(new PatientAppointment(expected, expectedAppointment))),
                result.getNavigation());
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

    private static Appointment appointment(String dateTime, String details) {
        return new Appointment(dateTime, Optional.of(new AppointmentDetails(details)));
    }
}
