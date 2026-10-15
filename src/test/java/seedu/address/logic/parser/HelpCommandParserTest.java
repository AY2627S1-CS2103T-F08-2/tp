package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.HelpCommand;

public class HelpCommandParserTest {

    private static final String INVALID_FORMAT_MESSAGE =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE);

    private final HelpCommandParser parser = new HelpCommandParser();

    @Test
    public void parse_noArgs_returnsDefaultHelpCommand() {
        assertParseSuccess(parser, "", new HelpCommand());
        assertParseSuccess(parser, "   ", new HelpCommand());
    }

    @Test
    public void parse_validCommandsWithoutFlag_returnsHelpCommand() {
        assertParseSuccess(parser, "add", new HelpCommand("add"));
        assertParseSuccess(parser, "delete", new HelpCommand("delete"));
        assertParseSuccess(parser, "viewappt", new HelpCommand("viewappt"));
        assertParseSuccess(parser, "window", new HelpCommand("window"));
    }

    @Test
    public void parse_validCommandsWithFlag_returnsHelpCommand() {
        assertParseSuccess(parser, "--add", new HelpCommand("add"));
        assertParseSuccess(parser, "--delete", new HelpCommand("delete"));
        assertParseSuccess(parser, "--window", new HelpCommand("window"));
    }

    @Test
    public void parse_caseInsensitive_returnsLowercaseHelpCommand() {
        assertParseSuccess(parser, "--DELETE", new HelpCommand("delete"));
        assertParseSuccess(parser, "ADD", new HelpCommand("add"));
        assertParseSuccess(parser, "--ViewAppt", new HelpCommand("viewappt"));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        // Space after '--'
        assertParseFailure(parser, "-- add", INVALID_FORMAT_MESSAGE);

        // Multiple words / extra tokens
        assertParseFailure(parser, "add patient", INVALID_FORMAT_MESSAGE);
        assertParseFailure(parser, "--add patient", INVALID_FORMAT_MESSAGE);
    }
}
