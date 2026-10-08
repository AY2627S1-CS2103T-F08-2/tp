package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.ID_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.ID_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.MEDICAL_HISTORY_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.NEXT_APPOINTMENT_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_MEDICAL_HISTORY_BOB;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPatients.AMY;
import static seedu.address.testutil.TypicalPatients.BOB;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.patient.MedicalHistory;
import seedu.address.model.patient.NextAppointment;
import seedu.address.model.patient.Patient;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PatientBuilder;
import seedu.address.testutil.PatientUtil;

/**
 * Tests add commands from user input through model updates and JSON persistence.
 */
public class AddCommandFlowTest {
    @TempDir
    public Path temporaryFolder;

    private Model model;
    private Storage storage;
    private Logic logic;

    @BeforeEach
    public void setUp() {
        model = new ModelManager();
        storage = new StorageManager(new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json")),
                new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json")));
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_requiredFieldsOnly_normalizedAndPersisted() throws Exception {
        assertAddedPatientPersisted("add --address Block 312, Amy Street 1 --name Amy   Bee"
                + " --number 11111111 --id s1111111a", AMY);
    }

    @Test
    public void execute_medicalHistoryOnly_persisted() throws Exception {
        Patient expectedPatient = new PatientBuilder(AMY).withMedicalHistory(VALID_MEDICAL_HISTORY_BOB).build();
        assertAddedPatientPersisted(AddCommand.COMMAND_WORD + MEDICAL_HISTORY_DESC_BOB + PHONE_DESC_AMY
                + ADDRESS_DESC_AMY + ID_DESC_AMY + NAME_DESC_AMY, expectedPatient);
    }

    @Test
    public void execute_dateOnlyAppointment_persisted() throws Exception {
        Patient expectedPatient = new PatientBuilder(AMY).withNextAppointment("2026-09-20").build();
        assertAddedPatientPersisted(AddCommand.COMMAND_WORD + " --next-appointment 2026-09-20"
                + NAME_DESC_AMY + ADDRESS_DESC_AMY + ID_DESC_AMY + PHONE_DESC_AMY, expectedPatient);
    }

    @Test
    public void execute_allFields_persisted() throws Exception {
        assertAddedPatientPersisted(AddCommand.COMMAND_WORD + NEXT_APPOINTMENT_DESC_BOB + ADDRESS_DESC_BOB
                + NAME_DESC_BOB + MEDICAL_HISTORY_DESC_BOB + PHONE_DESC_BOB + ID_DESC_BOB, BOB);
    }

    @Test
    public void execute_caseInsensitiveDuplicateIdOutsideFilteredList_rejectedWithoutChanges() throws Exception {
        logic.execute(PatientUtil.getAddCommand(AMY));
        model.updateFilteredPatientList(patient -> false);

        assertRejectedCommandLeavesDataUnchanged("add --name Different Patient --number 98765432"
                + " --address Different Address --id s1111111a", CommandException.class,
                AddCommand.MESSAGE_DUPLICATE_PATIENT);
    }

    @Test
    public void execute_unknownOption_rejectedWithoutChanges() throws Exception {
        logic.execute(PatientUtil.getAddCommand(AMY));
        String command = PatientUtil.getAddCommand(new PatientBuilder().build()) + " --medical-histroy asthma";

        assertRejectedCommandLeavesDataUnchanged(command, ParseException.class,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
    }

    @Test
    public void execute_emptyOptionalFlags_rejectedWithoutChanges() throws Exception {
        logic.execute(PatientUtil.getAddCommand(AMY));
        String command = PatientUtil.getAddCommand(new PatientBuilder().build());

        assertRejectedCommandLeavesDataUnchanged(command + " --medical-history", ParseException.class,
                MedicalHistory.MESSAGE_CONSTRAINTS);
        assertRejectedCommandLeavesDataUnchanged(command + " --next-appointment", ParseException.class,
                NextAppointment.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void execute_repeatedFinalFlag_rejectedWithoutChanges() throws Exception {
        logic.execute(PatientUtil.getAddCommand(AMY));
        String command = PatientUtil.getAddCommand(new PatientBuilder().build()) + " --name";

        assertRejectedCommandLeavesDataUnchanged(command, ParseException.class,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));
    }

    private void assertAddedPatientPersisted(String command, Patient expectedPatient) throws Exception {
        CommandResult result = logic.execute(command);

        assertEquals(String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(expectedPatient)),
                result.getFeedbackToUser());
        assertEquals(List.of(expectedPatient), model.getAddressBook().getPatientList());
        assertEquals(List.of(expectedPatient), model.getFilteredPatientList());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }

    private void assertRejectedCommandLeavesDataUnchanged(String command, Class<? extends Throwable> expectedException,
            String expectedMessage) throws Exception {
        AddressBook expectedAddressBook = new AddressBook(model.getAddressBook());
        List<Patient> expectedFilteredList = List.copyOf(model.getFilteredPatientList());
        String expectedJson = Files.readString(storage.getAddressBookFilePath());

        assertThrows(expectedException, expectedMessage, () -> logic.execute(command));

        assertEquals(expectedAddressBook, model.getAddressBook());
        assertEquals(expectedFilteredList, model.getFilteredPatientList());
        assertEquals(expectedJson, Files.readString(storage.getAddressBookFilePath()));
        assertEquals(expectedAddressBook, storage.readAddressBook().orElseThrow());
    }
}
