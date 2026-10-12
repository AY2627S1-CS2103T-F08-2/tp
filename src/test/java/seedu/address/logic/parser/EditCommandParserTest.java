package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_ADDRESS_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_ID;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_MEDICAL_HISTORY_DESC;
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
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADD_APPOINTMENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MEDICAL_HISTORY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMOVE_APPOINTMENT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPatientDescriptor;
import seedu.address.model.patient.Address;
import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.MedicalHistory;
import seedu.address.model.patient.Name;
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
        assertParseFailure(parser, VALID_ID_AMY + INVALID_MEDICAL_HISTORY_DESC + NAME_DESC_AMY,
                MedicalHistory.MESSAGE_CONSTRAINTS); // blank medical history

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
    public void parse_appointments_success() {
        PatientId targetId = new PatientId(VALID_ID_AMY);

        // one appointment to add, given on its own
        String userInput = VALID_ID_AMY + " " + PREFIX_ADD_APPOINTMENT + "2026-10-20 09:30";
        EditPatientDescriptor descriptor = new EditPatientDescriptorBuilder()
                .withAppointmentsToAdd("2026-10-20 09:30").build();
        assertParseSuccess(parser, userInput, new EditCommand(targetId, descriptor));

        // several appointments to add
        userInput = VALID_ID_AMY + " " + PREFIX_ADD_APPOINTMENT + "2026-10-20 09:30 "
                + PREFIX_ADD_APPOINTMENT + "2026-11-03";
        descriptor = new EditPatientDescriptorBuilder().withAppointmentsToAdd("2026-10-20 09:30", "2026-11-03").build();
        assertParseSuccess(parser, userInput, new EditCommand(targetId, descriptor));

        // remove and add in one command, together with another field
        userInput = VALID_ID_AMY + PHONE_DESC_BOB + " " + PREFIX_REMOVE_APPOINTMENT + "2026-10-20 09:30 "
                + PREFIX_ADD_APPOINTMENT + "2026-10-22 14:00";
        descriptor = new EditPatientDescriptorBuilder().withPhone(VALID_PHONE_BOB)
                .withAppointmentsToRemove("2026-10-20 09:30").withAppointmentsToAdd("2026-10-22 14:00").build();
        assertParseSuccess(parser, userInput, new EditCommand(targetId, descriptor));
    }

    @Test
    public void parse_tabSeparatedOptions_success() {
        PatientId targetId = new PatientId(VALID_ID_AMY);
        EditPatientDescriptor descriptor = new EditPatientDescriptorBuilder().withPhone(VALID_PHONE_BOB)
                .withAppointmentsToAdd("2026-10-20 09:30").build();
        assertParseSuccess(parser, VALID_ID_AMY + "\t--number\t" + VALID_PHONE_BOB + "\t--add-appt\t2026-10-20 09:30",
                new EditCommand(targetId, descriptor));
    }

    @Test
    public void parse_finalOptionWithoutValue_failure() {
        // the option is recognized, so the error names the field instead of showing the usage
        assertParseFailure(parser, VALID_ID_AMY + " --add-appt", Appointment.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, VALID_ID_AMY + " --remove-appt", Appointment.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, VALID_ID_AMY + " --number", Phone.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, VALID_ID_AMY + " --medical-history", MedicalHistory.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_unsupportedOption_failure() {
        // misspelt option, an add-only option, and the ID, which cannot be edited
        assertParseFailure(parser, VALID_ID_AMY + " --nmae Alex", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, VALID_ID_AMY + " --next-appointment 2026-10-20", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, VALID_ID_AMY + " --id S9999999Z", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidAppointment_failure() {
        assertParseFailure(parser, VALID_ID_AMY + " " + PREFIX_ADD_APPOINTMENT + "2026-02-30",
                Appointment.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, VALID_ID_AMY + " " + PREFIX_REMOVE_APPOINTMENT + "2026/10/20",
                Appointment.MESSAGE_CONSTRAINTS);
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
