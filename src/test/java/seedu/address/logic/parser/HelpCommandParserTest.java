package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.HelpCommand;

public class HelpCommandParserTest {

    private final HelpCommandParser parser = new HelpCommandParser();

    @Test
    public void parse_noArgs_returnsDefaultHelpCommand() {
        // "help" or "help   "
        assertParseSuccess(parser, "", new HelpCommand());
        assertParseSuccess(parser, "   ", new HelpCommand());
    }

    @Test
    public void parse_validTargetCommand_returnsTargetedHelpCommand() {
        // "help --delete", "help --add", "help   --find   "
        assertParseSuccess(parser, "--delete", new HelpCommand("delete"));
        assertParseSuccess(parser, "   --delete   ", new HelpCommand("delete"));
        assertParseSuccess(parser, "--add", new HelpCommand("add"));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE);

        // Missing dashes: "help delete"
        assertParseFailure(parser, "delete", expectedMessage);

        // Bare dashes with no command: "help --"
        assertParseFailure(parser, "--", expectedMessage);

        // Multiple words after dashes: "help --delete patient"
        assertParseFailure(parser, "--delete patient", expectedMessage);

        // Extra stray text: "help 123"
        assertParseFailure(parser, "123", expectedMessage);
    }
}
