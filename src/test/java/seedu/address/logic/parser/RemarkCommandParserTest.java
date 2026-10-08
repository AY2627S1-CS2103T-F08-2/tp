package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_ID;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ID_AMY;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.RemarkCommand;
import seedu.address.model.person.PatientId;
import seedu.address.model.person.Remark;

public class RemarkCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemarkCommand.MESSAGE_USAGE);

    private RemarkCommandParser parser = new RemarkCommandParser();

    @Test
    public void parse_idAndRemark_success() {
        RemarkCommand expectedCommand = new RemarkCommand(new PatientId(VALID_ID_AMY), new Remark("Likes to swim."));
        assertParseSuccess(parser, VALID_ID_AMY + " Likes to swim.", expectedCommand);

        // extra whitespace around the ID and remark
        assertParseSuccess(parser, "  " + VALID_ID_AMY + "   Likes to swim.  ", expectedCommand);
    }

    @Test
    public void parse_idOnly_returnsEmptyRemark() {
        RemarkCommand expectedCommand = new RemarkCommand(new PatientId(VALID_ID_AMY), new Remark(""));
        assertParseSuccess(parser, VALID_ID_AMY, expectedCommand);
    }

    @Test
    public void parse_missingId_failure() {
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "   ", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidId_failure() {
        assertParseFailure(parser, INVALID_ID + " Likes to swim.", MESSAGE_INVALID_FORMAT);
    }
}
