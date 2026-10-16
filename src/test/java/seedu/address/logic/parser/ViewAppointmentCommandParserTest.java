package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_VIEW_APPOINTMENT_FROM_AFTER_TO;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FROM;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TO;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.commons.util.DateUtil;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.ViewAppointmentCommand;

public class ViewAppointmentCommandParserTest {

    private static final String INVALID_FORMAT_MESSAGE =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewAppointmentCommand.MESSAGE_USAGE);
    private static final LocalDate START = LocalDate.of(2026, 10, 1);
    private static final LocalDate END = LocalDate.of(2026, 10, 31);

    private ViewAppointmentCommandParser parser = new ViewAppointmentCommandParser();

    @Test
    public void parse_validDateOnly_returnsCommand() {
        assertParseSuccess(parser, "2026-10-15", new ViewAppointmentCommand(LocalDate.of(2026, 10, 15)));
    }

    @Test
    public void parse_validDateWithWhitespace_returnsCommand() {
        assertParseSuccess(parser, "  2026-10-15  ", new ViewAppointmentCommand(LocalDate.of(2026, 10, 15)));
    }

    @Test
    public void parse_invalidDate_throwsParseException() {
        for (String date : List.of("2026/10/15", "invalid", "2026-13-01", "2026-02-30")) {
            assertParseFailure(parser, date, DateUtil.MESSAGE_CONSTRAINTS);
            assertParseFailure(parser, "--from " + date, DateUtil.MESSAGE_CONSTRAINTS);
            assertParseFailure(parser, "--to " + date, DateUtil.MESSAGE_CONSTRAINTS);
        }
    }

    @Test
    public void parse_emptyString_throwsParseException() {
        assertParseFailure(parser, "", INVALID_FORMAT_MESSAGE);
        assertParseFailure(parser, " \t ", INVALID_FORMAT_MESSAGE);
    }

    @Test
    public void parse_multipleDates_throwsParseException() {
        assertParseFailure(parser, "2026-10-15 2026-10-20", DateUtil.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_startAndEnd_returnsCommand() {
        ViewAppointmentCommand expectedCommand = new ViewAppointmentCommand(Optional.of(START), Optional.of(END));
        assertParseSuccess(parser, "--from 2026-10-01 --to 2026-10-31", expectedCommand);
        assertParseSuccess(parser, " --to 2026-10-31 --from 2026-10-01 ", expectedCommand);
        assertParseSuccess(parser, "\t--from\t2026-10-01\t--to\t2026-10-31", expectedCommand);
    }

    @Test
    public void parse_sameStartAndEnd_returnsSingleDateCommand() {
        assertParseSuccess(parser, "--from 2026-10-01 --to 2026-10-01", new ViewAppointmentCommand(START));
    }

    @Test
    public void parse_startOnly_returnsCommand() {
        assertParseSuccess(parser, "--from 2026-10-01",
                new ViewAppointmentCommand(Optional.of(START), Optional.empty()));
    }

    @Test
    public void parse_endOnly_returnsCommand() {
        assertParseSuccess(parser, "--to 2026-10-31",
                new ViewAppointmentCommand(Optional.empty(), Optional.of(END)));
    }

    @Test
    public void parse_startAfterEnd_throwsParseException() {
        assertParseFailure(parser, "--from 2026-10-31 --to 2026-10-01", MESSAGE_VIEW_APPOINTMENT_FROM_AFTER_TO);
    }

    @Test
    public void parse_missingOptionValue_throwsParseException() {
        assertParseFailure(parser, "--from", DateUtil.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "--to", DateUtil.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "--from 2026-10-01 --to", DateUtil.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_dateWithOptions_throwsParseException() {
        assertParseFailure(parser, "2026-10-15 --to 2026-10-31", INVALID_FORMAT_MESSAGE);
        assertParseFailure(parser, "2026-10-15 --from 2026-10-01", INVALID_FORMAT_MESSAGE);
    }

    @Test
    public void parse_duplicateOptions_throwsParseException() {
        assertParseFailure(parser, "--from 2026-10-01 --from 2026-10-02",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_FROM));
        assertParseFailure(parser, "--to 2026-10-30 --to 2026-10-31",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_TO));
    }

    @Test
    public void parse_unknownOption_throwsParseException() {
        for (String arguments : List.of("--till 2026-10-31", "--from 2026-10-01 --till 2026-10-31", "--from=2026-10-01",
                "--From 2026-10-01")) {
            assertParseFailure(parser, arguments, INVALID_FORMAT_MESSAGE);
        }
    }
}
