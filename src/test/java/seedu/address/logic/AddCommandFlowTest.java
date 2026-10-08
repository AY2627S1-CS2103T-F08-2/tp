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
import static seedu.address.testutil.TypicalPersons.AMY;
import static seedu.address.testutil.TypicalPersons.BOB;

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
import seedu.address.model.person.MedicalHistory;
import seedu.address.model.person.NextAppointment;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.PersonUtil;

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
        assertAddedPersonPersisted("add --address Block 312, Amy Street 1 --name Amy   Bee"
                + " --number 11111111 --id s1111111a", AMY);
    }

    @Test
    public void execute_medicalHistoryOnly_persisted() throws Exception {
        Person expectedPerson = new PersonBuilder(AMY).withMedicalHistory(VALID_MEDICAL_HISTORY_BOB).build();
        assertAddedPersonPersisted(AddCommand.COMMAND_WORD + MEDICAL_HISTORY_DESC_BOB + PHONE_DESC_AMY
                + ADDRESS_DESC_AMY + ID_DESC_AMY + NAME_DESC_AMY, expectedPerson);
    }

    @Test
    public void execute_dateOnlyAppointment_persisted() throws Exception {
        Person expectedPerson = new PersonBuilder(AMY).withNextAppointment("2026-09-20").build();
        assertAddedPersonPersisted(AddCommand.COMMAND_WORD + " --next-appointment 2026-09-20"
                + NAME_DESC_AMY + ADDRESS_DESC_AMY + ID_DESC_AMY + PHONE_DESC_AMY, expectedPerson);
    }

    @Test
    public void execute_allFields_persisted() throws Exception {
        assertAddedPersonPersisted(AddCommand.COMMAND_WORD + NEXT_APPOINTMENT_DESC_BOB + ADDRESS_DESC_BOB
                + NAME_DESC_BOB + MEDICAL_HISTORY_DESC_BOB + PHONE_DESC_BOB + ID_DESC_BOB, BOB);
    }

    @Test
    public void execute_caseInsensitiveDuplicateIdOutsideFilteredList_rejectedWithoutChanges() throws Exception {
        logic.execute(PersonUtil.getAddCommand(AMY));
        model.updateFilteredPersonList(person -> false);

        assertRejectedCommandLeavesDataUnchanged("add --name Different Patient --number 98765432"
                + " --address Different Address --id s1111111a", CommandException.class,
                AddCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_unknownOption_rejectedWithoutChanges() throws Exception {
        logic.execute(PersonUtil.getAddCommand(AMY));
        String command = PersonUtil.getAddCommand(new PersonBuilder().build()) + " --medical-histroy asthma";

        assertRejectedCommandLeavesDataUnchanged(command, ParseException.class,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
    }

    @Test
    public void execute_emptyOptionalFlags_rejectedWithoutChanges() throws Exception {
        logic.execute(PersonUtil.getAddCommand(AMY));
        String command = PersonUtil.getAddCommand(new PersonBuilder().build());

        assertRejectedCommandLeavesDataUnchanged(command + " --medical-history", ParseException.class,
                MedicalHistory.MESSAGE_CONSTRAINTS);
        assertRejectedCommandLeavesDataUnchanged(command + " --next-appointment", ParseException.class,
                NextAppointment.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void execute_repeatedFinalFlag_rejectedWithoutChanges() throws Exception {
        logic.execute(PersonUtil.getAddCommand(AMY));
        String command = PersonUtil.getAddCommand(new PersonBuilder().build()) + " --name";

        assertRejectedCommandLeavesDataUnchanged(command, ParseException.class,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));
    }

    private void assertAddedPersonPersisted(String command, Person expectedPerson) throws Exception {
        CommandResult result = logic.execute(command);

        assertEquals(String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(expectedPerson)),
                result.getFeedbackToUser());
        assertEquals(List.of(expectedPerson), model.getAddressBook().getPersonList());
        assertEquals(List.of(expectedPerson), model.getFilteredPersonList());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }

    private void assertRejectedCommandLeavesDataUnchanged(String command, Class<? extends Throwable> expectedException,
            String expectedMessage) throws Exception {
        AddressBook expectedAddressBook = new AddressBook(model.getAddressBook());
        List<Person> expectedFilteredList = List.copyOf(model.getFilteredPersonList());
        String expectedJson = Files.readString(storage.getAddressBookFilePath());

        assertThrows(expectedException, expectedMessage, () -> logic.execute(command));

        assertEquals(expectedAddressBook, model.getAddressBook());
        assertEquals(expectedFilteredList, model.getFilteredPersonList());
        assertEquals(expectedJson, Files.readString(storage.getAddressBookFilePath()));
        assertEquals(expectedAddressBook, storage.readAddressBook().orElseThrow());
    }
}
