package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.MarkAppointmentCommand;
import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.PatientId;

public class MarkAppointmentCommandParserTest {

    private static final PatientId ID = new PatientId("S1234567A");
    private static final String INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, MarkAppointmentCommand.MESSAGE_USAGE);

    private final MarkAppointmentCommandParser parser = new MarkAppointmentCommandParser();

    @Test
    public void parse_validIdAndNumber_success() {
        assertParseSuccess(parser, "S1234567A 2", new MarkAppointmentCommand(ID, 2));
        assertParseSuccess(parser, "  s1234567a\t2  ", new MarkAppointmentCommand(ID, 2));
        assertParseSuccess(parser, "S1234567A 2147483647", new MarkAppointmentCommand(ID, Integer.MAX_VALUE));
        assertParseSuccess(parser, "S1234567A 999999999999999", new MarkAppointmentCommand(ID, 999999999999999L));
        assertParseSuccess(parser, "2 1", new MarkAppointmentCommand(new PatientId("2"), 1));
    }

    @Test
    public void parse_missingExtraOrInvalidId_failure() {
        for (String args : List.of("", " ", "S1234567A", "2", "bad-id 2", "12345678901 1", "S1234567A 1 2",
                "S1234567A --appt 2026-10-05", "S1234567A 2 --details Review")) {
            assertParseFailure(parser, args, INVALID_FORMAT);
        }
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_invalidAppointmentNumber_failure() {
        for (String number : List.of("0", "-1", "+1", "1.5", "#2", "two",
                "2026-10-05")) {
            assertParseFailure(parser, "S1234567A " + number, Appointment.MESSAGE_NUMBER_CONSTRAINTS);
        }
    }

    @Test
    public void parseCommand_dispatchAndHelp_registered() throws Exception {
        assertEquals(new MarkAppointmentCommand(ID, 2),
                new LaiNurseParser().parseCommand("markappt\u00a0s1234567a\u202f2"));
        assertEquals(MarkAppointmentCommand.MESSAGE_USAGE,
                HelpCommand.COMMAND_USAGE_MAP.get(MarkAppointmentCommand.COMMAND_WORD));
        assertParseSuccess(new HelpCommandParser(), "markappt",
                new HelpCommand(MarkAppointmentCommand.COMMAND_WORD));
        assertParseFailure(new HelpCommandParser(), "MARKAPPT",
                HelpCommand.MESSAGE_INVALID_HELP_ARGUMENT);
    }
}
