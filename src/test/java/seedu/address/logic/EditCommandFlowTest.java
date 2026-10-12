package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.MedicalHistory;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientId;
import seedu.address.model.patient.Phone;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PatientBuilder;

/**
 * Tests edit commands from user input through model updates and JSON persistence, starting from the sample data.
 */
public class EditCommandFlowTest {
    private static final String ALEX_ID = "S1234567A"; // appointments 2026-10-05 09:30 and 2026-11-02
    private static final String BERNICE_ID = "S2345678B"; // appointment 2026-10-06
    private static final String CHARLOTTE_ID = "T0123456C"; // no medical history or appointments
    private static final String IRFAN_ID = "G1234567E"; // appointment 2026-10-07 14:00

    private static final String INVALID_FORMAT_MESSAGE =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE);

    @TempDir
    public Path temporaryFolder;

    private Model model;
    private Storage storage;
    private Logic logic;

    @BeforeEach
    public void setUp() throws Exception {
        model = new ModelManager(SampleDataUtil.getSampleAddressBook(), new UserPrefs());
        storage = new StorageManager(new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json")),
                new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json")));
        storage.saveAddressBook(model.getAddressBook());
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_editPhone_onlyPhoneChanged() throws Exception {
        Patient expected = new PatientBuilder(patient(ALEX_ID)).withPhone("91234567").build();
        assertEditPersisted("edit S1234567A --number 91234567", ALEX_ID, expected);
    }

    @Test
    public void execute_tabSeparatedOptions_edited() throws Exception {
        Patient expected = new PatientBuilder(patient(ALEX_ID)).withPhone("71234567").build();
        assertEditPersisted("edit S1234567A\t--number\t71234567", ALEX_ID, expected);
    }

    @Test
    public void execute_lowerCaseIdAndExtraSpaces_normalized() throws Exception {
        Patient expected = new PatientBuilder(patient(ALEX_ID)).withName("Alex Tan").build();
        assertEditPersisted("edit   s1234567a   --name   alex    tan  ", ALEX_ID, expected);
    }

    @Test
    public void execute_patientHiddenByFind_editedAndListReset() throws Exception {
        logic.execute("find bernice");
        assertEquals(1, model.getFilteredPatientList().size());

        Patient expected = new PatientBuilder(patient(ALEX_ID)).withAddress("Blk 1 Test Road").build();
        assertEditPersisted("edit S1234567A --address Blk 1 Test Road", ALEX_ID, expected);
        assertEquals(model.getAddressBook().getPatientList(), model.getFilteredPatientList());
    }

    @Test
    public void execute_addMedicalHistoryToPatientWithout_persisted() throws Exception {
        Patient expected = new PatientBuilder(patient(CHARLOTTE_ID)).withMedicalHistory("Asthma").build();
        assertEditPersisted("edit T0123456C --medical-history Asthma", CHARLOTTE_ID, expected);
    }

    @Test
    public void execute_addAppointmentsOutOfOrder_storedSorted() throws Exception {
        Patient expected = new PatientBuilder(patient(CHARLOTTE_ID))
                .withAppointments("2026-11-15", "2026-12-01 10:00").build();
        assertEditPersisted("edit T0123456C --add-appt 2026-12-01 10:00 --add-appt 2026-11-15",
                CHARLOTTE_ID, expected);
        assertEquals("2026-11-15, 2026-12-01 10:00", patient(CHARLOTTE_ID).getAppointments().toString());
    }

    @Test
    public void execute_addExistingAppointment_noDuplicate() throws Exception {
        assertEditPersisted("edit S2345678B --add-appt 2026-10-06", BERNICE_ID, patient(BERNICE_ID));
    }

    @Test
    public void execute_moveAppointment_persisted() throws Exception {
        Patient expected = new PatientBuilder(patient(BERNICE_ID)).withAppointments("2026-10-08 09:00").build();
        assertEditPersisted("edit S2345678B --remove-appt 2026-10-06 --add-appt 2026-10-08 09:00",
                BERNICE_ID, expected);
    }

    @Test
    public void execute_removeOnlyAppointment_appointmentsEmpty() throws Exception {
        Patient expected = new PatientBuilder(patient(IRFAN_ID)).withAppointments().build();
        assertEditPersisted("edit G1234567E --remove-appt 2026-10-07 14:00", IRFAN_ID, expected);
    }

    @Test
    public void execute_removeAppointmentAndEditPhone_bothApplied() throws Exception {
        Patient expected = new PatientBuilder(patient(ALEX_ID)).withPhone("99999999")
                .withAppointments("2026-10-05 09:30").build();
        assertEditPersisted("edit S1234567A --remove-appt 2026-11-02 --number 99999999", ALEX_ID, expected);
    }

    @Test
    public void execute_dateOnlyAppointment_sortedBeforeTimedSameDay() throws Exception {
        Patient expected = new PatientBuilder(patient(ALEX_ID))
                .withAppointments("2026-10-05", "2026-10-05 08:00", "2026-10-05 09:30", "2026-11-02").build();
        assertEditPersisted("edit S1234567A --add-appt 2026-10-05 08:00 --add-appt 2026-10-05", ALEX_ID, expected);
        assertEquals("2026-10-05, 2026-10-05 08:00, 2026-10-05 09:30, 2026-11-02",
                patient(ALEX_ID).getAppointments().toString());
    }

    @Test
    public void execute_editedAppointmentFoundByViewAppt() throws Exception {
        logic.execute("edit T0123456C --add-appt 2026-10-20");
        logic.execute("viewappt 2026-10-20");
        assertEquals(List.of(patient(CHARLOTTE_ID)), model.getFilteredPatientList());
    }

    @Test
    public void execute_unknownId_rejectedWithoutChanges() throws Exception {
        assertRejectedCommandLeavesDataUnchanged("edit S0000000Z --number 91234567", CommandException.class,
                Messages.MESSAGE_INVALID_PATIENT_ID);

        // a list number is read as an ID, which no patient has
        assertRejectedCommandLeavesDataUnchanged("edit 1 --name Test", CommandException.class,
                Messages.MESSAGE_INVALID_PATIENT_ID);
    }

    @Test
    public void execute_removeMissingAppointment_rejectedWithoutChanges() throws Exception {
        // the date-only appointment on 2026-11-02 does not match a timed one, and the phone is not changed either
        assertRejectedCommandLeavesDataUnchanged("edit S1234567A --number 99999999 --remove-appt 2026-11-02 09:30",
                CommandException.class,
                String.format(EditCommand.MESSAGE_APPOINTMENT_NOT_FOUND, ALEX_ID, "2026-11-02 09:30"));
    }

    @Test
    public void execute_addAndRemoveSameNewAppointment_rejectedWithoutChanges() throws Exception {
        // removals happen before additions, so the appointment is not there yet when it is removed
        assertRejectedCommandLeavesDataUnchanged("edit S1234567A --add-appt 2026-10-11 --remove-appt 2026-10-11",
                CommandException.class,
                String.format(EditCommand.MESSAGE_APPOINTMENT_NOT_FOUND, ALEX_ID, "2026-10-11"));
    }

    @Test
    public void execute_invalidValues_rejectedWithoutChanges() throws Exception {
        assertRejectedCommandLeavesDataUnchanged("edit S1234567A --add-appt 2026-02-30", ParseException.class,
                Appointment.MESSAGE_CONSTRAINTS);
        assertRejectedCommandLeavesDataUnchanged("edit S1234567A --remove-appt 2026-10-05 9:30",
                ParseException.class, Appointment.MESSAGE_CONSTRAINTS);
        assertRejectedCommandLeavesDataUnchanged("edit S1234567A --number 12", ParseException.class,
                Phone.MESSAGE_CONSTRAINTS);
        assertRejectedCommandLeavesDataUnchanged("edit S1234567A --medical-history  --name Alex",
                ParseException.class, MedicalHistory.MESSAGE_CONSTRAINTS);

        // option with no value at the end of the command
        assertRejectedCommandLeavesDataUnchanged("edit S1234567A --add-appt", ParseException.class,
                Appointment.MESSAGE_CONSTRAINTS);
        assertRejectedCommandLeavesDataUnchanged("edit S1234567A --number", ParseException.class,
                Phone.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void execute_invalidFormat_rejectedWithoutChanges() throws Exception {
        // nothing to edit
        assertRejectedCommandLeavesDataUnchanged("edit S1234567A", ParseException.class,
                EditCommand.MESSAGE_NOT_EDITED);

        // missing or invalid ID
        assertRejectedCommandLeavesDataUnchanged("edit --name Alex", ParseException.class, INVALID_FORMAT_MESSAGE);
        assertRejectedCommandLeavesDataUnchanged("edit S12345678901 --name Alex", ParseException.class,
                INVALID_FORMAT_MESSAGE);

        // the ID cannot be edited, and --appt belongs to add
        assertRejectedCommandLeavesDataUnchanged("edit S1234567A --id S9999999Z", ParseException.class,
                INVALID_FORMAT_MESSAGE);
        assertRejectedCommandLeavesDataUnchanged("edit S1234567A --appt 2026-10-10", ParseException.class,
                INVALID_FORMAT_MESSAGE);

        // single-valued field repeated
        assertRejectedCommandLeavesDataUnchanged("edit S1234567A --name Alex --name Bob", ParseException.class,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));
    }

    /**
     * Returns the patient with the given ID from the model.
     */
    private Patient patient(String id) {
        PatientId patientId = new PatientId(id);
        return model.getAddressBook().getPatientList().stream()
                .filter(patient -> patient.getId().equals(patientId)).findFirst().orElseThrow();
    }

    /**
     * Executes {@code command} and asserts that only the patient with {@code id} changed, to {@code expected},
     * and that the change is saved to the data file.
     */
    private void assertEditPersisted(String command, String id, Patient expected) throws Exception {
        AddressBook expectedAddressBook = new AddressBook(model.getAddressBook());
        expectedAddressBook.setPatient(patient(id), expected);

        CommandResult result = logic.execute(command);

        assertEquals(String.format(EditCommand.MESSAGE_EDIT_PATIENT_SUCCESS, Messages.format(expected)),
                result.getFeedbackToUser());
        assertEquals(expectedAddressBook, model.getAddressBook());
        assertEquals(model.getAddressBook().getPatientList(), model.getFilteredPatientList());
        assertEquals(expectedAddressBook, storage.readAddressBook().orElseThrow());
    }

    /**
     * Asserts that {@code command} fails with the given exception and message,
     * and leaves the model, the displayed list and the data file unchanged.
     */
    private void assertRejectedCommandLeavesDataUnchanged(String command, Class<? extends Throwable> expectedException,
            String expectedMessage) throws Exception {
        AddressBook expectedAddressBook = new AddressBook(model.getAddressBook());
        List<Patient> expectedFilteredList = List.copyOf(model.getFilteredPatientList());
        String expectedJson = Files.readString(storage.getAddressBookFilePath());

        assertThrows(expectedException, expectedMessage, () -> logic.execute(command));

        assertEquals(expectedAddressBook, model.getAddressBook());
        assertEquals(expectedFilteredList, model.getFilteredPatientList());
        assertEquals(expectedJson, Files.readString(storage.getAddressBookFilePath()));
    }
}
