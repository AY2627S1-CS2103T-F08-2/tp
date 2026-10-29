package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_ID;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ID_AMY;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ArchiveCommand;
import seedu.address.model.patient.PatientId;

public class ArchiveCommandParserTest {

    private ArchiveCommandParser parser = new ArchiveCommandParser();

    @Test
    public void parse_validArgs_returnsArchiveCommand() {
        PatientId expectedId = new PatientId(VALID_ID_AMY);
        ArchiveCommand expectedCommand = new ArchiveCommand(expectedId);
        assertParseSuccess(parser, VALID_ID_AMY, expectedCommand);
        assertParseSuccess(parser, "  " + VALID_ID_AMY + "  ", expectedCommand);
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, INVALID_ID, String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                ArchiveCommand.MESSAGE_USAGE));
    }
}
