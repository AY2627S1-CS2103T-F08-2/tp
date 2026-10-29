package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_ID;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.ViewCommand;
import seedu.address.model.PatientScope;
import seedu.address.model.patient.PatientId;

public class ViewCommandParserTest {

    private ViewCommandParser parser = new ViewCommandParser();

    @Test
    public void parse_validArgs_returnsViewCommand() {
        ViewCommand expectedCommand = new ViewCommand(new PatientId("S1111111A"));
        assertParseSuccess(parser, "S1111111A", expectedCommand);
        assertParseSuccess(parser, "  S1111111A  ", expectedCommand);
        assertParseSuccess(parser, "s1111111a", expectedCommand);
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewCommand.MESSAGE_USAGE);
        assertParseFailure(parser, INVALID_ID, expectedMessage);
        assertParseFailure(parser, "S1111111A S2222222B", expectedMessage);
        assertParseFailure(parser, "--unarchived", expectedMessage);
        // the scope options take no value
        assertParseFailure(parser, "--all S1111111A", expectedMessage);
    }

    @Test
    public void parse_noId_returnsListCommandOfScope() {
        assertParseSuccess(parser, "", new ListCommand(PatientScope.DEFAULT));
        assertParseSuccess(parser, "   ", new ListCommand(PatientScope.DEFAULT));
        assertParseSuccess(parser, "--all", new ListCommand(PatientScope.ALL));
        assertParseSuccess(parser, " \t--archived ", new ListCommand(PatientScope.ARCHIVED));
    }

    @Test
    public void parse_scopeOptionsMisused_throwsParseException() {
        assertParseFailure(parser, "--all --archived", Messages.MESSAGE_CONFLICTING_SCOPES);
        assertParseFailure(parser, "--all --all", Messages.getErrorMessageForDuplicatePrefixes(
                CliSyntax.PREFIX_ALL));
        // an ID shows that patient, archived or not
        assertParseFailure(parser, "S1111111A --all", Messages.MESSAGE_SCOPE_WITH_ID);
        assertParseFailure(parser, "S1111111A --archived", Messages.MESSAGE_SCOPE_WITH_ID);
    }
}
