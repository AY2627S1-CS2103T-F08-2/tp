package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MEDICAL_HISTORY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NEXT_APPOINTMENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.FindCommand;
import seedu.address.model.patient.PatientContainsSubstringsPredicate;

public class FindCommandParserTest {

    private static final String INVALID_FORMAT_MESSAGE =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE);

    private final FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_nullArg_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "", INVALID_FORMAT_MESSAGE);
        assertParseFailure(parser, " \t\n ", INVALID_FORMAT_MESSAGE);
    }

    @Test
    public void parse_eachOption_returnsFindCommand() {
        assertParseSuccess(parser, "--id 123", command("123", null, null, null, null, null));
        assertParseSuccess(parser, "--name Mary", command(null, "Mary", null, null, null, null));
        assertParseSuccess(parser, "--number 5", command(null, null, "5", null, null, null));
        assertParseSuccess(parser, "--address #01-", command(null, null, null, "#01-", null, null));
        assertParseSuccess(parser, "--medical-history diabet", command(null, null, null, null, "diabet", null));
        assertParseSuccess(parser, "--next-appointment 2026-10", command(null, null, null, null, null, "2026-10"));
        assertParseSuccess(parser, "--next-appointment 09:30", command(null, null, null, null, null, "09:30"));
        assertParseSuccess(parser, "--next-appointment 2026-02-30",
                command(null, null, null, null, null, "2026-02-30"));
    }

    @Test
    public void parse_allOptionsInDifferentOrders_returnsSameCommand() {
        FindCommand expectedCommand = command("123", "Mary Jane", "65", "Marina Bay", "Type 2", "2026-10 09");
        assertParseSuccess(parser, "--id 123 --name Mary Jane --number 65 --address Marina Bay"
                + " --medical-history Type 2 --next-appointment 2026-10 09", expectedCommand);
        assertParseSuccess(parser, " --next-appointment \"2026-10 09\" --medical-history “Type 2”"
                + " --address \"Marina Bay\" --number “65” --name “Mary Jane” --id \"123\"", expectedCommand);
    }

    @Test
    public void parse_whitespaceSeparators_returnsFindCommand() {
        FindCommand expectedCommand = command(null, "Mary Jane", null, "Marina Bay", null, null);
        assertParseSuccess(parser, "--name Mary Jane --address Marina Bay", expectedCommand);
        assertParseSuccess(parser, "\t--address\tMarina\t\tBay\t--name\tMary\tJane\t", expectedCommand);
        assertParseSuccess(parser, " \n --name \n Mary Jane \n --address Marina Bay  ", expectedCommand);
    }

    @Test
    public void parse_optionalQuotes_returnsSameCommand() {
        FindCommand expectedCommand = command(null, "Mary Jane", null, null, null, null);
        for (String value : List.of("Mary Jane", "\"Mary Jane\"", "“Mary Jane”", "\"  mary\t JANE  \"",
                "“ mary  Jane ”")) {
            assertParseSuccess(parser, "--name " + value, expectedCommand);
        }
    }

    @Test
    public void parse_unexpectedPreamble_failure() {
        for (String arguments : List.of("John", "John Mary", "preamble --name Mary", "123 --id 123")) {
            assertParseFailure(parser, arguments, INVALID_FORMAT_MESSAGE);
        }
    }

    @Test
    public void parse_emptyValues_failure() {
        for (Prefix prefix : List.of(PREFIX_ID, PREFIX_NAME, PREFIX_PHONE, PREFIX_ADDRESS,
                PREFIX_MEDICAL_HISTORY, PREFIX_NEXT_APPOINTMENT)) {
            for (String value : List.of("", " \t ", "\"\"", "\" \t \"", "“”", "“  ”")) {
                assertParseFailure(parser, prefix + value, INVALID_FORMAT_MESSAGE);
            }
        }
        assertParseFailure(parser, "--name --id 123", INVALID_FORMAT_MESSAGE);
        assertParseFailure(parser, "--id 123 --name", INVALID_FORMAT_MESSAGE);
        assertParseFailure(parser, "--name \"\" --id 123", INVALID_FORMAT_MESSAGE);
    }

    @Test
    public void parse_duplicateOptions_failure() {
        for (Prefix prefix : List.of(PREFIX_ID, PREFIX_NAME, PREFIX_PHONE, PREFIX_ADDRESS,
                PREFIX_MEDICAL_HISTORY, PREFIX_NEXT_APPOINTMENT)) {
            String expectedMessage = Messages.getErrorMessageForDuplicatePrefixes(prefix);
            assertParseFailure(parser, prefix + "first " + prefix + "second", expectedMessage);
            assertParseFailure(parser, prefix + "first " + prefix.getPrefix().trim(), expectedMessage);
        }
    }

    @Test
    public void parse_unknownOptions_failure() {
        for (String option : List.of("--unknown value", "--medical-histroy asthma", "--name=Mary", "--", "--Name Mary",
                "--phone 123")) {
            assertParseFailure(parser, option, INVALID_FORMAT_MESSAGE);
            assertParseFailure(parser, "--name Mary " + option, INVALID_FORMAT_MESSAGE);
        }
    }

    @Test
    public void parse_malformedQuotes_failure() {
        for (String value : List.of("\"", "“", "\"Mary", "“Mary", "\"Mary”", "“Mary\"", "Mary\"", "Mary”",
                "”Mary”", "\"Mary\" trailing", "“Mary” trailing", "\"Mary\" \"Jane\"", "“Mary” “Jane”")) {
            assertParseFailure(parser, "--name " + value, INVALID_FORMAT_MESSAGE);
        }
    }

    @Test
    public void parse_reservedOptionsInsideValues_failure() {
        for (String arguments : List.of("--name \"Mary --unknown value\"", "--name \"--unknown\"",
                "--name “--id”", "--name \"Mary --address Jurong\"", "--name Mary\t--unknown\tvalue")) {
            assertParseFailure(parser, arguments, INVALID_FORMAT_MESSAGE);
        }
    }

    @Test
    public void parse_embeddedDoubleHyphenText_success() {
        assertParseSuccess(parser, "--name Anne--Marie --address \"Block--A\"",
                command(null, "Anne--Marie", null, "Block--A", null, null));
    }

    private FindCommand command(String id, String name, String phone, String address, String medicalHistory,
            String nextAppointment) {
        return new FindCommand(new PatientContainsSubstringsPredicate(Optional.ofNullable(id),
                Optional.ofNullable(name), Optional.ofNullable(phone), Optional.ofNullable(address),
                Optional.ofNullable(medicalHistory), Optional.ofNullable(nextAppointment)));
    }
}
