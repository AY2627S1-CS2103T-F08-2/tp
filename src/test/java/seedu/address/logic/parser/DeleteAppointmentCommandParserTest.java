package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_APPOINTMENT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.DeleteAppointmentCommand;
import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.PatientId;

public class DeleteAppointmentCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteAppointmentCommand.MESSAGE_USAGE);

    private static final PatientId ID = new PatientId("S1234567A");
    private static final Appointment APPOINTMENT = new Appointment("2026-10-05 09:30");

    private final DeleteAppointmentCommandParser parser = new DeleteAppointmentCommandParser();

    @Test
    public void parse_validAppointment_success() {
        assertParseSuccess(parser, "S1234567A --appt 2026-10-05 09:30",
                new DeleteAppointmentCommand(ID, APPOINTMENT));

        // lower-case ID, tabs and extra spaces
        assertParseSuccess(parser, "  s1234567a\t--appt\t2026-10-05   09:30 ",
                new DeleteAppointmentCommand(ID, APPOINTMENT));
    }

    @Test
    public void parse_dateOnlyAppointment_success() {
        // a date alone matches an appointment with no time, and no timed one
        assertParseSuccess(parser, "S1234567A --appt 2026-10-05",
                new DeleteAppointmentCommand(ID, new Appointment("2026-10-05")));
    }

    @Test
    public void parse_missingParts_failure() {
        // no ID, an invalid ID, or a list number in place of the ID
        assertParseFailure(parser, "--appt 2026-10-05 09:30", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "S12345678901 --appt 2026-10-05 09:30", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "S1234567A extra --appt 2026-10-05 09:30", MESSAGE_INVALID_FORMAT);

        // no appointment to delete
        assertParseFailure(parser, "S1234567A", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidValues_failure() {
        assertParseFailure(parser, "S1234567A --appt 2026-02-30", Appointment.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "S1234567A --appt 2026-10-05 9:30", Appointment.MESSAGE_CONSTRAINTS);

        // an option with no value at the end of the command names its field
        assertParseFailure(parser, "S1234567A --appt", Appointment.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_repeatedOption_failure() {
        assertParseFailure(parser, "S1234567A --appt 2026-10-05 --appt 2026-10-06",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_APPOINTMENT));
    }

    @Test
    public void parse_unsupportedOption_failure() {
        // options of other commands, and a misspelt option
        assertParseFailure(parser, "S1234567A --appt 2026-10-05 --name Alex", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "S1234567A --appt 2026-10-05 --to 2026-10-06", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "S1234567A --appt 2026-10-05 --details Bring pen", MESSAGE_INVALID_FORMAT);
    }
}
