package seedu.address.logic.parser;

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
import static seedu.address.logic.commands.CommandTestUtil.VALID_NEXT_APPOINTMENT_BOB;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MEDICAL_HISTORY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NEXT_APPOINTMENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.AMY;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.MedicalHistory;
import seedu.address.model.person.Name;
import seedu.address.model.person.NextAppointment;
import seedu.address.model.person.PatientId;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.PersonUtil;

/**
 * Tests option boundaries and validation for add commands.
 */
public class AddCommandParserValidationTest {
    private static final String REQUIRED_ARGUMENTS = ID_DESC_AMY + NAME_DESC_AMY + PHONE_DESC_AMY + ADDRESS_DESC_AMY;
    private static final String INVALID_FORMAT_MESSAGE =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);

    private final AddCommandParser parser = new AddCommandParser();
    private final AddressBookParser commandParser = new AddressBookParser();

    @Test
    public void parse_optionalFieldCombinationsInDifferentOrders_success() {
        assertParseSuccess(parser, ADDRESS_DESC_AMY + PHONE_DESC_AMY + NAME_DESC_AMY + ID_DESC_AMY,
                new AddCommand(AMY));

        Person historyOnly = new PersonBuilder(AMY).withMedicalHistory(VALID_MEDICAL_HISTORY_BOB).build();
        assertParseSuccess(parser, MEDICAL_HISTORY_DESC_BOB + PHONE_DESC_AMY + ADDRESS_DESC_AMY
                + ID_DESC_AMY + NAME_DESC_AMY, new AddCommand(historyOnly));

        Person appointmentOnly = new PersonBuilder(AMY).withNextAppointment(VALID_NEXT_APPOINTMENT_BOB).build();
        assertParseSuccess(parser, NAME_DESC_AMY + NEXT_APPOINTMENT_DESC_BOB + ADDRESS_DESC_AMY
                + ID_DESC_AMY + PHONE_DESC_AMY, new AddCommand(appointmentOnly));

        assertParseSuccess(parser, NEXT_APPOINTMENT_DESC_BOB + ADDRESS_DESC_BOB + NAME_DESC_BOB
                + MEDICAL_HISTORY_DESC_BOB + PHONE_DESC_BOB + ID_DESC_BOB, new AddCommand(BOB));
    }

    @Test
    public void parse_dateOnlyAppointment_success() {
        Person expectedPerson = new PersonBuilder(AMY).withNextAppointment("2026-09-20").build();
        assertParseSuccess(parser, REQUIRED_ARGUMENTS + " --next-appointment 2026-09-20",
                new AddCommand(expectedPerson));
    }

    @Test
    public void parse_tabSeparatedOptions_success() {
        String arguments = "\t--medical-history\tdementia\t--number\t22222222\t--id\tS2222222B"
                + "\t--address\tBlock 123, Bobby Street 3\t--next-appointment\t2026-11-30 09:00\t--name\tBob Choo";
        assertParseSuccess(parser, arguments, new AddCommand(BOB));
    }

    @Test
    public void parse_noLeadingWhitespace_success() {
        assertParseSuccess(parser, PersonUtil.getPersonDetails(AMY).trim(), new AddCommand(AMY));
    }

    @Test
    public void parseCommand_emptyFinalValue_failure() {
        assertCommandParseFailure(NAME_DESC_AMY + PHONE_DESC_AMY + ADDRESS_DESC_AMY + " --id",
                PatientId.MESSAGE_CONSTRAINTS);
        assertCommandParseFailure(ID_DESC_AMY + PHONE_DESC_AMY + ADDRESS_DESC_AMY + " --name",
                Name.MESSAGE_CONSTRAINTS);
        assertCommandParseFailure(ID_DESC_AMY + NAME_DESC_AMY + ADDRESS_DESC_AMY + " --number",
                Phone.MESSAGE_CONSTRAINTS);
        assertCommandParseFailure(ID_DESC_AMY + NAME_DESC_AMY + PHONE_DESC_AMY + " --address",
                Address.MESSAGE_CONSTRAINTS);
        assertCommandParseFailure(REQUIRED_ARGUMENTS + " --medical-history", MedicalHistory.MESSAGE_CONSTRAINTS);
        assertCommandParseFailure(REQUIRED_ARGUMENTS + " --next-appointment", NextAppointment.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_emptyOptionalValueBetweenOptions_failure() {
        assertParseFailure(parser, " --medical-history" + REQUIRED_ARGUMENTS, MedicalHistory.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " --next-appointment" + REQUIRED_ARGUMENTS, NextAppointment.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_unknownOptions_failure() {
        for (String option : List.of("--medical-histroy dementia", "--name=Bob", "--unknown value", "--")) {
            assertParseFailure(parser, REQUIRED_ARGUMENTS + " " + option, INVALID_FORMAT_MESSAGE);
        }

        assertParseFailure(parser, ID_DESC_AMY + NAME_DESC_AMY + " --unknown value" + PHONE_DESC_AMY
                + ADDRESS_DESC_AMY, INVALID_FORMAT_MESSAGE);
        assertParseFailure(parser, REQUIRED_ARGUMENTS + MEDICAL_HISTORY_DESC_BOB + "\t--unknown\tvalue",
                INVALID_FORMAT_MESSAGE);
    }

    @Test
    public void parseCommand_repeatedFinalOption_failure() {
        String arguments = REQUIRED_ARGUMENTS + MEDICAL_HISTORY_DESC_BOB + NEXT_APPOINTMENT_DESC_BOB;
        for (Prefix prefix : List.of(PREFIX_ID, PREFIX_NAME, PREFIX_PHONE, PREFIX_ADDRESS,
                PREFIX_MEDICAL_HISTORY, PREFIX_NEXT_APPOINTMENT)) {
            assertCommandParseFailure(arguments + " " + prefix.getPrefix().trim(),
                    Messages.getErrorMessageForDuplicatePrefixes(prefix));
        }
    }

    @Test
    public void parse_embeddedDoubleHyphenText_success() {
        Person expectedPerson = new PersonBuilder(AMY).withName("Anne--Marie")
                .withAddress("Block--A, West Street").withMedicalHistory("Follow-up--stable").build();
        assertParseSuccess(parser, " " + PersonUtil.getPersonDetails(expectedPerson), new AddCommand(expectedPerson));
    }

    @Test
    public void parse_invalidAppointment_failure() {
        for (String appointment : List.of("2026-02-30", "2026-11-30 24:00", "2026/11/30")) {
            assertParseFailure(parser, REQUIRED_ARGUMENTS + " --next-appointment " + appointment,
                    NextAppointment.MESSAGE_CONSTRAINTS);
        }
    }

    @Test
    public void parse_missingRequiredFieldBeforeDuplicateCheck_failure() {
        assertParseFailure(parser, NAME_DESC_AMY + NAME_DESC_AMY + PHONE_DESC_AMY + ADDRESS_DESC_AMY,
                INVALID_FORMAT_MESSAGE);
    }

    private void assertCommandParseFailure(String arguments, String expectedMessage) {
        String command = AddCommand.COMMAND_WORD + arguments + " \t";
        assertThrows(ParseException.class, expectedMessage, () -> commandParser.parseCommand(command));
    }
}
