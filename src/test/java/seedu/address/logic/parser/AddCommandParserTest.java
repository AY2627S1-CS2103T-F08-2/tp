package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.ID_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.ID_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_ADDRESS_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_ID_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_MEDICAL_HISTORY_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_NEXT_APPOINTMENT_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_PHONE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.MEDICAL_HISTORY_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.NEXT_APPOINTMENT_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.PREAMBLE_NON_EMPTY;
import static seedu.address.logic.commands.CommandTestUtil.PREAMBLE_WHITESPACE;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ID_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_MEDICAL_HISTORY_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NEXT_APPOINTMENT_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MEDICAL_HISTORY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NEXT_APPOINTMENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalPersons.AMY;
import static seedu.address.testutil.TypicalPersons.BOB;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddCommand;
import seedu.address.model.person.Address;
import seedu.address.model.person.MedicalHistory;
import seedu.address.model.person.Name;
import seedu.address.model.person.NextAppointment;
import seedu.address.model.person.PatientId;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.testutil.PersonBuilder;

public class AddCommandParserTest {
    private AddCommandParser parser = new AddCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        Person expectedPerson = new PersonBuilder(BOB).build();

        // whitespace only preamble
        assertParseSuccess(parser,
                PREAMBLE_WHITESPACE + ID_DESC_BOB + NAME_DESC_BOB + PHONE_DESC_BOB + ADDRESS_DESC_BOB
                        + MEDICAL_HISTORY_DESC_BOB + NEXT_APPOINTMENT_DESC_BOB, new AddCommand(expectedPerson));

        // reordered fields
        assertParseSuccess(parser,
                PREAMBLE_WHITESPACE + MEDICAL_HISTORY_DESC_BOB + NAME_DESC_BOB + NEXT_APPOINTMENT_DESC_BOB
                        + PHONE_DESC_BOB + ID_DESC_BOB + ADDRESS_DESC_BOB, new AddCommand(expectedPerson));
        assertParseSuccess(parser,
                PREAMBLE_WHITESPACE + ADDRESS_DESC_BOB + PHONE_DESC_BOB + ID_DESC_BOB + MEDICAL_HISTORY_DESC_BOB
                        + NAME_DESC_BOB + NEXT_APPOINTMENT_DESC_BOB, new AddCommand(expectedPerson));
    }

    @Test
    public void parse_repeatedValue_failure() {
        String validExpectedPersonString = ID_DESC_BOB + NAME_DESC_BOB + PHONE_DESC_BOB + ADDRESS_DESC_BOB
                + MEDICAL_HISTORY_DESC_BOB + NEXT_APPOINTMENT_DESC_BOB;

        // multiple ids
        assertParseFailure(parser, ID_DESC_AMY + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ID));

        // multiple names
        assertParseFailure(parser, NAME_DESC_AMY + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));

        // multiple phones
        assertParseFailure(parser, PHONE_DESC_AMY + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // multiple addresses
        assertParseFailure(parser, ADDRESS_DESC_AMY + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ADDRESS));

        // multiple medical histories
        assertParseFailure(parser, MEDICAL_HISTORY_DESC_BOB + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_MEDICAL_HISTORY));

        // multiple next appointments
        assertParseFailure(parser, NEXT_APPOINTMENT_DESC_BOB + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NEXT_APPOINTMENT));

        // multiple fields repeated
        assertParseFailure(parser,
                validExpectedPersonString + ID_DESC_AMY + PHONE_DESC_AMY + NAME_DESC_AMY + ADDRESS_DESC_AMY
                        + MEDICAL_HISTORY_DESC_BOB + NEXT_APPOINTMENT_DESC_BOB,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ID, PREFIX_NAME, PREFIX_ADDRESS, PREFIX_PHONE,
                        PREFIX_MEDICAL_HISTORY, PREFIX_NEXT_APPOINTMENT));

        assertParseFailure(parser,
                validExpectedPersonString + ID_DESC_AMY + PHONE_DESC_AMY + NAME_DESC_AMY + ADDRESS_DESC_AMY,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ID, PREFIX_NAME, PREFIX_ADDRESS, PREFIX_PHONE));

        assertParseFailure(parser,
                validExpectedPersonString + MEDICAL_HISTORY_DESC_BOB + NEXT_APPOINTMENT_DESC_BOB,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_MEDICAL_HISTORY, PREFIX_NEXT_APPOINTMENT));

        // invalid value followed by valid value

        // invalid id
        assertParseFailure(parser, INVALID_ID_DESC + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ID));

        // invalid name
        assertParseFailure(parser, INVALID_NAME_DESC + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));

        // invalid phone
        assertParseFailure(parser, INVALID_PHONE_DESC + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // invalid address
        assertParseFailure(parser, INVALID_ADDRESS_DESC + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ADDRESS));

        // invalid medical history
        assertParseFailure(parser, INVALID_MEDICAL_HISTORY_DESC + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_MEDICAL_HISTORY));

        // invalid next appointment
        assertParseFailure(parser, INVALID_NEXT_APPOINTMENT_DESC + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NEXT_APPOINTMENT));

        // valid value followed by invalid value

        // invalid id
        assertParseFailure(parser, validExpectedPersonString + INVALID_ID_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ID));

        // invalid name
        assertParseFailure(parser, validExpectedPersonString + INVALID_NAME_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));

        // invalid phone
        assertParseFailure(parser, validExpectedPersonString + INVALID_PHONE_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // invalid address
        assertParseFailure(parser, validExpectedPersonString + INVALID_ADDRESS_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ADDRESS));

        // invalid medical history
        assertParseFailure(parser, validExpectedPersonString + INVALID_MEDICAL_HISTORY_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_MEDICAL_HISTORY));

        // invalid next appointment
        assertParseFailure(parser, validExpectedPersonString + INVALID_NEXT_APPOINTMENT_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NEXT_APPOINTMENT));
    }

    @Test
    public void parse_optionalFieldsMissing_success() {
        // no medical history or next appointment
        Person expectedPerson = new PersonBuilder(AMY).build();
        assertParseSuccess(parser, ID_DESC_AMY + NAME_DESC_AMY + PHONE_DESC_AMY + ADDRESS_DESC_AMY,
                new AddCommand(expectedPerson));
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);

        // missing id prefix
        assertParseFailure(parser, VALID_ID_BOB + NAME_DESC_BOB + PHONE_DESC_BOB + ADDRESS_DESC_BOB
                        + MEDICAL_HISTORY_DESC_BOB + NEXT_APPOINTMENT_DESC_BOB,
                expectedMessage);

        // missing name prefix
        assertParseFailure(parser, ID_DESC_BOB + VALID_NAME_BOB + PHONE_DESC_BOB + ADDRESS_DESC_BOB
                        + MEDICAL_HISTORY_DESC_BOB + NEXT_APPOINTMENT_DESC_BOB,
                expectedMessage);

        // missing phone prefix
        assertParseFailure(parser, ID_DESC_BOB + NAME_DESC_BOB + VALID_PHONE_BOB + ADDRESS_DESC_BOB
                        + MEDICAL_HISTORY_DESC_BOB + NEXT_APPOINTMENT_DESC_BOB,
                expectedMessage);

        // missing address prefix
        assertParseFailure(parser, ID_DESC_BOB + NAME_DESC_BOB + PHONE_DESC_BOB + VALID_ADDRESS_BOB
                        + MEDICAL_HISTORY_DESC_BOB + NEXT_APPOINTMENT_DESC_BOB,
                expectedMessage);

        // all prefixes missing
        assertParseFailure(parser, VALID_ID_BOB + VALID_NAME_BOB + VALID_PHONE_BOB + VALID_ADDRESS_BOB
                        + VALID_MEDICAL_HISTORY_BOB + VALID_NEXT_APPOINTMENT_BOB,
                expectedMessage);
    }

    @Test
    public void parse_invalidValue_failure() {
        // invalid id
        assertParseFailure(parser, INVALID_ID_DESC + NAME_DESC_BOB + PHONE_DESC_BOB + ADDRESS_DESC_BOB
                        + MEDICAL_HISTORY_DESC_BOB + NEXT_APPOINTMENT_DESC_BOB,
                PatientId.MESSAGE_CONSTRAINTS);

        // invalid name
        assertParseFailure(parser, ID_DESC_BOB + INVALID_NAME_DESC + PHONE_DESC_BOB + ADDRESS_DESC_BOB
                        + MEDICAL_HISTORY_DESC_BOB + NEXT_APPOINTMENT_DESC_BOB,
                Name.MESSAGE_CONSTRAINTS);

        // invalid phone
        assertParseFailure(parser, ID_DESC_BOB + NAME_DESC_BOB + INVALID_PHONE_DESC + ADDRESS_DESC_BOB
                        + MEDICAL_HISTORY_DESC_BOB + NEXT_APPOINTMENT_DESC_BOB,
                Phone.MESSAGE_CONSTRAINTS);

        // invalid address
        assertParseFailure(parser, ID_DESC_BOB + NAME_DESC_BOB + PHONE_DESC_BOB + INVALID_ADDRESS_DESC
                        + MEDICAL_HISTORY_DESC_BOB + NEXT_APPOINTMENT_DESC_BOB,
                Address.MESSAGE_CONSTRAINTS);

        // invalid medical history
        assertParseFailure(parser, ID_DESC_BOB + NAME_DESC_BOB + PHONE_DESC_BOB + ADDRESS_DESC_BOB
                        + INVALID_MEDICAL_HISTORY_DESC + NEXT_APPOINTMENT_DESC_BOB,
                MedicalHistory.MESSAGE_CONSTRAINTS);

        // invalid next appointment
        assertParseFailure(parser, ID_DESC_BOB + NAME_DESC_BOB + PHONE_DESC_BOB + ADDRESS_DESC_BOB
                        + MEDICAL_HISTORY_DESC_BOB + INVALID_NEXT_APPOINTMENT_DESC,
                NextAppointment.MESSAGE_CONSTRAINTS);

        // two invalid values, only first invalid value reported
        assertParseFailure(parser, ID_DESC_BOB + INVALID_NAME_DESC + PHONE_DESC_BOB + INVALID_ADDRESS_DESC
                        + MEDICAL_HISTORY_DESC_BOB + NEXT_APPOINTMENT_DESC_BOB,
                Name.MESSAGE_CONSTRAINTS);

        assertParseFailure(parser, ID_DESC_BOB + NAME_DESC_BOB + PHONE_DESC_BOB + ADDRESS_DESC_BOB
                        + INVALID_MEDICAL_HISTORY_DESC + INVALID_NEXT_APPOINTMENT_DESC,
                MedicalHistory.MESSAGE_CONSTRAINTS);

        // reordered invalid values, invalid value reported follows the following precedence order:
        // ID, name, number, address, medical history, next appointment
        assertParseFailure(parser, ID_DESC_BOB + PHONE_DESC_BOB + INVALID_ADDRESS_DESC + INVALID_NAME_DESC
                        + MEDICAL_HISTORY_DESC_BOB + NEXT_APPOINTMENT_DESC_BOB,
                Name.MESSAGE_CONSTRAINTS);

        assertParseFailure(parser, ID_DESC_BOB + NAME_DESC_BOB + PHONE_DESC_BOB + ADDRESS_DESC_BOB
                        + INVALID_NEXT_APPOINTMENT_DESC + INVALID_MEDICAL_HISTORY_DESC,
                MedicalHistory.MESSAGE_CONSTRAINTS);

        // non-empty preamble
        assertParseFailure(parser, PREAMBLE_NON_EMPTY + ID_DESC_BOB + NAME_DESC_BOB + PHONE_DESC_BOB
                + ADDRESS_DESC_BOB, String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
    }
}
