package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.HelpCommand;

public class HelpCommandParserTest {

    // Every invalid help argument (flag form, unknown target, multi-token) throws this message.
    private static final String INVALID_FORMAT_MESSAGE = HelpCommand.MESSAGE_INVALID_HELP_ARGUMENT;

    private final HelpCommandParser parser = new HelpCommandParser();

    @Test
    public void parse_noArgs_returnsDefaultHelpCommand() {
        assertParseSuccess(parser, "", new HelpCommand());
        assertParseSuccess(parser, "   ", new HelpCommand());
    }

    @Test
    public void parse_validCommands_returnsHelpCommand() {
        assertParseSuccess(parser, "add", new HelpCommand("add"));
        assertParseSuccess(parser, "delete", new HelpCommand("delete"));
        assertParseSuccess(parser, "viewappt", new HelpCommand("viewappt"));
        assertParseSuccess(parser, "window", new HelpCommand("window"));
    }

    @Test
    public void parse_caseInsensitive_returnsLowercaseHelpCommand() {
        assertParseSuccess(parser, "DELETE", new HelpCommand("delete"));
        assertParseSuccess(parser, "ADD", new HelpCommand("add"));
        assertParseSuccess(parser, "ViewAppt", new HelpCommand("viewappt"));
        assertParseSuccess(parser, "WINDOW", new HelpCommand("window"));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        // 'help [COMMAND]' does not accept the '--' flag form
        assertParseFailure(parser, "--add", INVALID_FORMAT_MESSAGE);
        assertParseFailure(parser, "--window", INVALID_FORMAT_MESSAGE);
        assertParseFailure(parser, "-- add", INVALID_FORMAT_MESSAGE);
        assertParseFailure(parser, "--", INVALID_FORMAT_MESSAGE);

        // Unknown targets are rejected instead of passing through
        assertParseFailure(parser, "3", INVALID_FORMAT_MESSAGE);
        assertParseFailure(parser, "nonexistent", INVALID_FORMAT_MESSAGE);
        assertParseFailure(parser, "help", INVALID_FORMAT_MESSAGE);

        // Multiple words / extra tokens
        assertParseFailure(parser, "add patient", INVALID_FORMAT_MESSAGE);
    }
}
