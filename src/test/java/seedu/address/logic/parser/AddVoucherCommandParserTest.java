package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddVoucherCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.model.patient.PatientId;
import seedu.address.model.patient.VoucherNumber;

public class AddVoucherCommandParserTest {

    private static final PatientId ID = new PatientId("S1234567A");
    private static final String INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddVoucherCommand.MESSAGE_USAGE);

    private final AddVoucherCommandParser parser = new AddVoucherCommandParser();

    @Test
    public void parse_validIdAndNumber_success() {
        assertParseSuccess(parser, "S1234567A 5",
                new AddVoucherCommand(ID, new VoucherNumber("5")));
        assertParseSuccess(parser, "  s1234567a\t05  ",
                new AddVoucherCommand(ID, new VoucherNumber("05")));
        assertParseSuccess(parser, "S1234567A 9223372036854775807",
                new AddVoucherCommand(ID, new VoucherNumber("9223372036854775807")));
        assertParseSuccess(parser, "2 1", new AddVoucherCommand(new PatientId("2"), new VoucherNumber("1")));
    }

    @Test
    public void parse_missingExtraOrInvalidId_failure() {
        for (String args : List.of("", " ", "S1234567A", "5", "bad-id 5", "S1234567A 1 2",
                "S1234567A --notes Extra", "S1234567A 5 extra")) {
            assertParseFailure(parser, args, INVALID_FORMAT);
        }
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_invalidNumberOfVouchers_failure() {
        for (String number : List.of("0", "00", "-1", "+1", "1.5", "five", "1e3")) {
            assertParseFailure(parser, "S1234567A " + number, VoucherNumber.MESSAGE_CONSTRAINTS);
        }
        assertParseFailure(parser, "S1234567A 99999999999999999999",
                String.format(VoucherNumber.MESSAGE_TOO_LARGE, "99999999999999999999"));
    }

    @Test
    public void parseCommand_dispatchAndHelp_registered() throws Exception {
        assertEquals(new AddVoucherCommand(ID, new VoucherNumber("5")),
                new LaiNurseParser().parseCommand("addvoucher\u00a0s1234567a\u202f5"));
        assertEquals(AddVoucherCommand.MESSAGE_USAGE,
                HelpCommand.COMMAND_USAGE_MAP.get(AddVoucherCommand.COMMAND_WORD));
        assertParseSuccess(new HelpCommandParser(), "addvoucher", new HelpCommand(AddVoucherCommand.COMMAND_WORD));
        assertParseFailure(new HelpCommandParser(), "ADDVOUCHER", HelpCommand.MESSAGE_INVALID_HELP_ARGUMENT);
    }

}
