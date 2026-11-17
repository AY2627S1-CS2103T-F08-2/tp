package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.RedeemVoucherCommand;
import seedu.address.model.patient.PatientId;
import seedu.address.model.patient.VoucherNumber;

public class RedeemVoucherCommandParserTest {

    private static final PatientId ID = new PatientId("S1234567A");
    private static final String INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, RedeemVoucherCommand.MESSAGE_USAGE);

    private final RedeemVoucherCommandParser parser = new RedeemVoucherCommandParser();

    @Test
    public void parse_validIdAndNumber_success() {
        assertParseSuccess(parser, "S1234567A 2",
                new RedeemVoucherCommand(ID, new VoucherNumber("2")));
        assertParseSuccess(parser, "  s1234567a\t02  ",
                new RedeemVoucherCommand(ID, new VoucherNumber("02")));
        assertParseSuccess(parser, "S1234567A 9223372036854775807",
                new RedeemVoucherCommand(ID, new VoucherNumber("9223372036854775807")));
        assertParseSuccess(parser, "2 1", new RedeemVoucherCommand(new PatientId("2"), new VoucherNumber("1")));
    }

    @Test
    public void parse_missingExtraOrInvalidId_failure() {
        for (String args : List.of("", " ", "S1234567A", "2", "bad-id 2", "S1234567A 1 2",
                "S1234567A --notes Extra", "S1234567A 2 extra")) {
            assertParseFailure(parser, args, INVALID_FORMAT);
        }
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_invalidNumberOfVouchers_failure() {
        for (String number : List.of("0", "00", "-1", "+1", "1.5", "two", "1e3")) {
            assertParseFailure(parser, "S1234567A " + number, VoucherNumber.MESSAGE_CONSTRAINTS);
        }
        assertParseFailure(parser, "S1234567A 99999999999999999999",
                String.format(VoucherNumber.MESSAGE_TOO_LARGE, "99999999999999999999"));
    }

    @Test
    public void parseCommand_dispatchAndHelp_registered() throws Exception {
        assertEquals(new RedeemVoucherCommand(ID, new VoucherNumber("2")),
                new LaiNurseParser().parseCommand("redeemvoucher\u00a0s1234567a\u202f2"));
        assertEquals(RedeemVoucherCommand.MESSAGE_USAGE,
                HelpCommand.COMMAND_USAGE_MAP.get(RedeemVoucherCommand.COMMAND_WORD));
        assertParseSuccess(new HelpCommandParser(), "redeemvoucher",
                new HelpCommand(RedeemVoucherCommand.COMMAND_WORD));
        assertParseFailure(new HelpCommandParser(), "REDEEMVOUCHER", HelpCommand.MESSAGE_INVALID_HELP_ARGUMENT);
    }

}
