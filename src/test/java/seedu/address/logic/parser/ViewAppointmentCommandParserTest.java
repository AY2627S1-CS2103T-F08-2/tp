package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_VIEW_APPOINTMENT_FROM_AFTER_TO;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FROM;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
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
import seedu.address.model.patient.PatientId;

public class ViewAppointmentCommandParserTest {

    private static final String INVALID_FORMAT_MESSAGE =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewAppointmentCommand.MESSAGE_USAGE);

    private static final String ID_FLAG = PREFIX_ID.getPrefix();
    private static final String FROM_FLAG = PREFIX_FROM.getPrefix();
    private static final String TO_FLAG = PREFIX_TO.getPrefix();

    private static final String VALID_ID = "S1234567A";
    private static final String OTHER_VALID_ID = "S2345678B";
    private static final String INVALID_ID = "12#";

    private static final LocalDate VALID_DATE = LocalDate.of(2026, 10, 15);
    private static final String VALID_DATE_STRING = VALID_DATE.toString();
    private static final LocalDate START = LocalDate.of(2026, 10, 1);
    private static final LocalDate END = LocalDate.of(2026, 10, 31);

    private static final String NOT_A_DATE = "invalid";
    private static final String WRONG_FORMAT_DATE = "2026/10/15";
    private static final String MONTH_13_DATE = "2026-13-01";
    private static final String IMPOSSIBLE_DATE = "2026-02-30";

    private ViewAppointmentCommandParser parser = new ViewAppointmentCommandParser();

    @Test
    public void parse_validDateOnly_returnsCommand() {
        assertParseSuccess(parser, VALID_DATE_STRING, new ViewAppointmentCommand(VALID_DATE));
    }

    @Test
    public void parse_validDateWithWhitespace_returnsCommand() {
        assertParseSuccess(parser, "  " + VALID_DATE_STRING + "  ", new ViewAppointmentCommand(VALID_DATE));
    }

    @Test
    public void parse_plainViewappt_returnsCommand() {
        ViewAppointmentCommand expectedCommand = new ViewAppointmentCommand(
                Optional.empty(), Optional.empty(), Optional.empty());
        assertParseSuccess(parser, "", expectedCommand);
        assertParseSuccess(parser, " \t ", expectedCommand);
    }

    @Test
    public void parse_idOnly_returnsCommand() {
        PatientId id = new PatientId(VALID_ID);
        ViewAppointmentCommand expectedCommand = new ViewAppointmentCommand(
                Optional.empty(), Optional.empty(), Optional.of(id));
        assertParseSuccess(parser, ID_FLAG + VALID_ID, expectedCommand);
        assertParseSuccess(parser, " " + ID_FLAG + VALID_ID + " ", expectedCommand);
    }

    @Test
    public void parse_dateWithId_returnsCommand() {
        PatientId id = new PatientId(VALID_ID);
        assertParseSuccess(parser, VALID_DATE_STRING + " " + ID_FLAG + VALID_ID,
                new ViewAppointmentCommand(Optional.of(VALID_DATE), Optional.of(VALID_DATE), Optional.of(id)));
    }

    @Test
    public void parse_idWithFromAndTo_returnsCommand() {
        PatientId id = new PatientId(VALID_ID);
        ViewAppointmentCommand expectedCommand = new ViewAppointmentCommand(
                Optional.of(START), Optional.of(END), Optional.of(id));
        assertParseSuccess(parser, ID_FLAG + VALID_ID + " " + FROM_FLAG + START + " " + TO_FLAG + END,
                expectedCommand);
        assertParseSuccess(parser, FROM_FLAG + START + " " + ID_FLAG + VALID_ID + " " + TO_FLAG + END,
                expectedCommand);
        assertParseSuccess(parser, TO_FLAG + END + " " + FROM_FLAG + START + " " + ID_FLAG + VALID_ID,
                expectedCommand);
    }

    @Test
    public void parse_startAndEnd_returnsCommand() {
        ViewAppointmentCommand expectedCommand = new ViewAppointmentCommand(
                Optional.of(START), Optional.of(END));
        assertParseSuccess(parser, FROM_FLAG + START + " " + TO_FLAG + END, expectedCommand);
        assertParseSuccess(parser, " " + TO_FLAG + END + " " + FROM_FLAG + START + " ",
                expectedCommand);
        assertParseSuccess(parser, "\t" + FROM_FLAG + "\t" + START + "\t" + TO_FLAG + "\t" + END,
                expectedCommand);
    }

    @Test
    public void parse_sameStartAndEnd_returnsSingleDateCommand() {
        assertParseSuccess(parser, FROM_FLAG + START + " " + TO_FLAG + START,
                new ViewAppointmentCommand(START));
    }

    @Test
    public void parse_startOnly_returnsCommand() {
        assertParseSuccess(parser, FROM_FLAG + START,
                new ViewAppointmentCommand(Optional.of(START), Optional.empty()));
    }

    @Test
    public void parse_endOnly_returnsCommand() {
        assertParseSuccess(parser, TO_FLAG + END,
                new ViewAppointmentCommand(Optional.empty(), Optional.of(END)));
    }

    @Test
    public void parse_startAfterEnd_throwsParseException() {
        assertParseFailure(parser, FROM_FLAG + END + " " + TO_FLAG + START,
                MESSAGE_VIEW_APPOINTMENT_FROM_AFTER_TO);
    }

    @Test
    public void parse_missingOptionValue_throwsParseException() {
        assertParseFailure(parser, FROM_FLAG, DateUtil.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, TO_FLAG, DateUtil.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, FROM_FLAG + START + " " + TO_FLAG, DateUtil.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidDate_throwsParseException() {
        for (String date : List.of(WRONG_FORMAT_DATE, MONTH_13_DATE, IMPOSSIBLE_DATE)) {
            // A word of digits and punctuation can only be meant as a date, so it gets the date error
            assertParseFailure(parser, date, DateUtil.MESSAGE_CONSTRAINTS);
            assertParseFailure(parser, date + " " + ID_FLAG + VALID_ID, DateUtil.MESSAGE_CONSTRAINTS);
            // A date given to an option gets the same error
            assertParseFailure(parser, FROM_FLAG + date, DateUtil.MESSAGE_CONSTRAINTS);
            assertParseFailure(parser, TO_FLAG + date, DateUtil.MESSAGE_CONSTRAINTS);
        }
    }

    @Test
    public void parse_wordWithLetters_showsCommandFormat() {
        // A bare word with letters could be a mistyped option, so the command format is shown
        assertParseFailure(parser, NOT_A_DATE, INVALID_FORMAT_MESSAGE);
        // An ID given without --id
        assertParseFailure(parser, VALID_ID, INVALID_FORMAT_MESSAGE);
        // The same word given to an option is a date, so it gets the date error
        assertParseFailure(parser, FROM_FLAG + NOT_A_DATE, DateUtil.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, TO_FLAG + NOT_A_DATE, DateUtil.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_multipleDates_throwsParseException() {
        // Two positional dates have no place in the format; the usage shows the range options
        assertParseFailure(parser, VALID_DATE_STRING + " " + END, INVALID_FORMAT_MESSAGE);
    }

    @Test
    public void parse_dateWithFromOrTo_throwsParseException() {
        assertParseFailure(parser, VALID_DATE_STRING + " " + TO_FLAG + END, INVALID_FORMAT_MESSAGE);
        assertParseFailure(parser, VALID_DATE_STRING + " " + FROM_FLAG + START, INVALID_FORMAT_MESSAGE);
    }

    @Test
    public void parse_duplicateOptions_throwsParseException() {
        assertParseFailure(parser, FROM_FLAG + START + " " + FROM_FLAG + END,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_FROM));
        assertParseFailure(parser, TO_FLAG + START + " " + TO_FLAG + END,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_TO));
        // A duplicate option is reported even when a date is also present
        assertParseFailure(parser, VALID_DATE_STRING + " " + FROM_FLAG + START + " " + FROM_FLAG + END,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_FROM));
    }

    @Test
    public void parse_duplicateId_throwsParseException() {
        assertParseFailure(parser, ID_FLAG + VALID_ID + " " + ID_FLAG + OTHER_VALID_ID,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ID));
    }

    @Test
    public void parse_dateAfterOptions_throwsParseException() {
        // The date ends up in the ID value, which fails as an invalid ID
        assertParseFailure(parser, ID_FLAG + VALID_ID + " " + VALID_DATE_STRING,
                PatientId.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidId_throwsParseException() {
        assertParseFailure(parser, ID_FLAG + INVALID_ID, PatientId.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, ID_FLAG, PatientId.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_unknownOption_throwsParseException() {
        assertParseFailure(parser, "--till " + END, INVALID_FORMAT_MESSAGE);
        assertParseFailure(parser, FROM_FLAG + START + " --till " + END, INVALID_FORMAT_MESSAGE);
        assertParseFailure(parser, "--from=" + START, INVALID_FORMAT_MESSAGE);
        assertParseFailure(parser, "--From " + START, INVALID_FORMAT_MESSAGE);
    }
}
