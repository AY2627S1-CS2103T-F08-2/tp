package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_ADDRESS_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_ID;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_PHONE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.MEDICAL_HISTORY_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ID_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ID_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_MEDICAL_HISTORY_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADD_MEDICAL_HISTORY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADD_NOTES;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MEDICAL_HISTORY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTES;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPatientDescriptor;
import seedu.address.model.patient.Address;
import seedu.address.model.patient.MedicalHistory;
import seedu.address.model.patient.Name;
import seedu.address.model.patient.Notes;
import seedu.address.model.patient.PatientId;
import seedu.address.model.patient.Phone;
import seedu.address.testutil.EditPatientDescriptorBuilder;

public class EditCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE);

    private EditCommandParser parser = new EditCommandParser();

    @Test
    public void parse_missingParts_failure() {
        // no ID specified
        assertParseFailure(parser, NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // no field specified
        assertParseFailure(parser, VALID_ID_AMY, EditCommand.MESSAGE_NOT_EDITED);

        // no ID and no field specified
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidPreamble_failure() {
        // invalid ID
        assertParseFailure(parser, INVALID_ID + NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // "-" is not allowed in IDs
        assertParseFailure(parser, "-5" + NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // invalid arguments being parsed as preamble
        assertParseFailure(parser, VALID_ID_AMY + " some random string", MESSAGE_INVALID_FORMAT);

        // invalid prefix being parsed as preamble
        assertParseFailure(parser, VALID_ID_AMY + " i/ string", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parser, VALID_ID_AMY + INVALID_NAME_DESC, Name.MESSAGE_CONSTRAINTS); // invalid name
        assertParseFailure(parser, VALID_ID_AMY + INVALID_PHONE_DESC, Phone.MESSAGE_CONSTRAINTS); // invalid phone
        assertParseFailure(parser, VALID_ID_AMY + INVALID_ADDRESS_DESC, Address.MESSAGE_CONSTRAINTS); // invalid address
        assertParseFailure(parser, VALID_ID_AMY + " " + PREFIX_ADD_MEDICAL_HISTORY + NAME_DESC_AMY,
                MedicalHistory.MESSAGE_CONSTRAINTS); // blank medical history to add

        // invalid phone followed by valid address
        assertParseFailure(parser, VALID_ID_AMY + INVALID_PHONE_DESC + ADDRESS_DESC_AMY, Phone.MESSAGE_CONSTRAINTS);

        // multiple invalid values, but only the first invalid value is captured
        assertParseFailure(parser, VALID_ID_AMY + INVALID_NAME_DESC + INVALID_ADDRESS_DESC + VALID_PHONE_AMY,
                Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_allFieldsSpecified_success() {
        PatientId targetId = new PatientId(VALID_ID_BOB);
        String userInput = VALID_ID_BOB + PHONE_DESC_BOB + ADDRESS_DESC_AMY + NAME_DESC_AMY;

        EditPatientDescriptor descriptor = new EditPatientDescriptorBuilder().withName(VALID_NAME_AMY)
                .withPhone(VALID_PHONE_BOB).withAddress(VALID_ADDRESS_AMY).build();
        EditCommand expectedCommand = new EditCommand(targetId, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_someFieldsSpecified_success() {
        PatientId targetId = new PatientId(VALID_ID_AMY);
        String userInput = VALID_ID_AMY + PHONE_DESC_BOB + ADDRESS_DESC_AMY;

        EditPatientDescriptor descriptor = new EditPatientDescriptorBuilder().withPhone(VALID_PHONE_BOB)
                .withAddress(VALID_ADDRESS_AMY).build();
        EditCommand expectedCommand = new EditCommand(targetId, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_oneFieldSpecified_success() {
        // name
        PatientId targetId = new PatientId(VALID_ID_AMY);
        String userInput = VALID_ID_AMY + NAME_DESC_AMY;
        EditPatientDescriptor descriptor = new EditPatientDescriptorBuilder().withName(VALID_NAME_AMY).build();
        EditCommand expectedCommand = new EditCommand(targetId, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // phone
        userInput = VALID_ID_AMY + PHONE_DESC_AMY;
        descriptor = new EditPatientDescriptorBuilder().withPhone(VALID_PHONE_AMY).build();
        expectedCommand = new EditCommand(targetId, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // address
        userInput = VALID_ID_AMY + ADDRESS_DESC_AMY;
        descriptor = new EditPatientDescriptorBuilder().withAddress(VALID_ADDRESS_AMY).build();
        expectedCommand = new EditCommand(targetId, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // medical history
        userInput = VALID_ID_AMY + MEDICAL_HISTORY_DESC_BOB;
        descriptor = new EditPatientDescriptorBuilder().withMedicalHistory(VALID_MEDICAL_HISTORY_BOB).build();
        expectedCommand = new EditCommand(targetId, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_notesOnly_success() {
        EditPatientDescriptor descriptor = new EditPatientDescriptorBuilder().withNotes("Enjoys gardening").build();
        EditCommand expectedCommand = new EditCommand(new PatientId(VALID_ID_AMY), descriptor);
        assertParseSuccess(parser, VALID_ID_AMY + " --notes Enjoys   gardening", expectedCommand);
        assertParseSuccess(parser, VALID_ID_AMY + "\t--notes\tEnjoys\t gardening", expectedCommand);
    }

    @Test
    public void parse_notesWithOtherFields_success() {
        EditPatientDescriptor descriptor = new EditPatientDescriptorBuilder().withNotes("Enjoys gardening")
                .withPhone(VALID_PHONE_BOB).withMedicalHistory(VALID_MEDICAL_HISTORY_BOB).build();
        assertParseSuccess(parser, VALID_ID_AMY + " --notes Enjoys gardening" + PHONE_DESC_BOB
                + MEDICAL_HISTORY_DESC_BOB,
                new EditCommand(new PatientId(VALID_ID_AMY), descriptor));
    }

    @Test
    public void parse_blankOrRepeatedNotes_failure() {
        assertParseFailure(parser, VALID_ID_AMY + " --add-notes \t " + NAME_DESC_AMY, Notes.MESSAGE_CONSTRAINTS);
        for (String repeatedNotes : List.of(" --notes First --notes Second", " --notes First --notes",
                " --notes --notes Second")) {
            assertParseFailure(parser, VALID_ID_AMY + repeatedNotes,
                    Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NOTES));
        }
        assertParseFailure(parser, VALID_ID_AMY + " --add-notes First --add-notes Second",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ADD_NOTES));
    }

    @Test
    public void parse_emptyValue_clearsField() {
        PatientId targetId = new PatientId(VALID_ID_AMY);

        // at the end of the command, before another option, or with only whitespace
        assertParseSuccess(parser, VALID_ID_AMY + " --notes",
                new EditCommand(targetId, new EditPatientDescriptorBuilder().withoutNotes().build()));
        assertParseSuccess(parser, VALID_ID_AMY + " --medical-history \t --phone " + VALID_PHONE_BOB,
                new EditCommand(targetId, new EditPatientDescriptorBuilder().withoutMedicalHistory()
                        .withPhone(VALID_PHONE_BOB).build()));
        assertParseSuccess(parser, VALID_ID_AMY + " --notes --medical-history",
                new EditCommand(targetId, new EditPatientDescriptorBuilder().withoutMedicalHistory().withoutNotes()
                        .build()));
    }

    @Test
    public void parse_addToField_success() {
        PatientId targetId = new PatientId(VALID_ID_AMY);
        assertParseSuccess(parser, VALID_ID_AMY + " --add-medical-history Asthma",
                new EditCommand(targetId, new EditPatientDescriptorBuilder().withMedicalHistoryAdded("Asthma")
                        .build()));

        // adding to one field combines with replacing or clearing the other
        assertParseSuccess(parser, VALID_ID_AMY + " --add-notes Prefers  mornings --medical-history Asthma",
                new EditCommand(targetId, new EditPatientDescriptorBuilder().withNotesAdded("Prefers mornings")
                        .withMedicalHistory("Asthma").build()));
        assertParseSuccess(parser, VALID_ID_AMY + "\t--add-medical-history\tAsthma\t--notes",
                new EditCommand(targetId, new EditPatientDescriptorBuilder().withMedicalHistoryAdded("Asthma")
                        .withoutNotes().build()));
    }

    @Test
    public void parse_replaceAndAddSameField_failure() {
        String notesConflict = String.format(Messages.MESSAGE_CONFLICTING_FIELD_EDITS, "--notes", "--add-notes");
        assertParseFailure(parser, VALID_ID_AMY + " --notes First --add-notes Second", notesConflict);
        assertParseFailure(parser, VALID_ID_AMY + " --add-notes Second --notes", notesConflict);
        assertParseFailure(parser, VALID_ID_AMY + " --medical-history Asthma --add-medical-history Eczema",
                String.format(Messages.MESSAGE_CONFLICTING_FIELD_EDITS, "--medical-history",
                        "--add-medical-history"));
    }

    @Test
    public void parse_tabSeparatedOptions_success() {
        PatientId targetId = new PatientId(VALID_ID_AMY);
        EditPatientDescriptor descriptor = new EditPatientDescriptorBuilder().withPhone(VALID_PHONE_BOB).build();
        assertParseSuccess(parser, VALID_ID_AMY + "\t--phone\t" + VALID_PHONE_BOB,
                new EditCommand(targetId, descriptor));
    }

    @Test
    public void parse_finalOptionWithoutValue_failure() {
        // the option is recognized, so the error names the field instead of showing the usage
        assertParseFailure(parser, VALID_ID_AMY + " --phone", Phone.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, VALID_ID_AMY + " --add-medical-history", MedicalHistory.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, VALID_ID_AMY + " --add-notes", Notes.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_unsupportedOption_failure() {
        // misspelt option, the old name of --phone, the id, which cannot be edited, and the retired
        // appointment flags, which are rejected like any other unknown option
        assertParseFailure(parser, VALID_ID_AMY + " --nmae Alex", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, VALID_ID_AMY + " --number 91234567", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, VALID_ID_AMY + " --appt 2026-10-20", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, VALID_ID_AMY + " --add-appt 2026-10-20", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, VALID_ID_AMY + " --remove-appt 2026-10-20", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, VALID_ID_AMY + " --id S9999999Z", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_multipleRepeatedFields_failure() {
        // More extensive testing of duplicate parameter detections is done in
        // AddCommandParserTest#parse_repeatedValue_failure()

        // valid followed by invalid
        String userInput = VALID_ID_AMY + INVALID_PHONE_DESC + PHONE_DESC_BOB;

        assertParseFailure(parser, userInput, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // invalid followed by valid
        userInput = VALID_ID_AMY + PHONE_DESC_BOB + INVALID_PHONE_DESC;

        assertParseFailure(parser, userInput, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // multiple valid fields repeated
        userInput = VALID_ID_AMY + PHONE_DESC_AMY + ADDRESS_DESC_AMY
                + PHONE_DESC_AMY + ADDRESS_DESC_AMY + PHONE_DESC_BOB + ADDRESS_DESC_BOB;

        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE, PREFIX_ADDRESS));

        // multiple invalid values
        userInput = VALID_ID_AMY + INVALID_PHONE_DESC + INVALID_ADDRESS_DESC
                + INVALID_PHONE_DESC + INVALID_ADDRESS_DESC;

        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE, PREFIX_ADDRESS));

        // repeated medical history
        userInput = VALID_ID_AMY + MEDICAL_HISTORY_DESC_BOB + MEDICAL_HISTORY_DESC_BOB;

        assertParseFailure(parser, userInput, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_MEDICAL_HISTORY));
    }
}
