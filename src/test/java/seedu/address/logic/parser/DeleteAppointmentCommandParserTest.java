package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.DeleteAppointmentCommand;
import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.PatientId;

public class DeleteAppointmentCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteAppointmentCommand.MESSAGE_USAGE);

    private static final PatientId ID = new PatientId("S1234567A");
    private final DeleteAppointmentCommandParser parser = new DeleteAppointmentCommandParser();

    @Test
    public void parse_validNumber_success() {
        assertParseSuccess(parser, "S1234567A 2", new DeleteAppointmentCommand(ID, 2));

        // lower-case ID, tabs and extra spaces
        assertParseSuccess(parser, "  s1234567a\t2 ", new DeleteAppointmentCommand(ID, 2));
    }

    @Test
    public void parse_largestNumber_success() {
        assertParseSuccess(parser, "S1234567A 2147483647", new DeleteAppointmentCommand(ID, Integer.MAX_VALUE));
        // larger numbers are kept, so that the command reports that the patient has no such appointment
        assertParseSuccess(parser, "S1234567A 2147483648", new DeleteAppointmentCommand(ID, 2147483648L));
    }

    @Test
    public void parse_missingParts_failure() {
        // no ID, an invalid ID, or a list number in place of the ID
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "2", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "S12345678901 2", MESSAGE_INVALID_FORMAT);

        // no appointment to delete
        assertParseFailure(parser, "S1234567A", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_dateSelector_failure() {
        assertParseFailure(parser, "S1234567A 2026-10-05", Appointment.MESSAGE_NUMBER_CONSTRAINTS);
        assertParseFailure(parser, "S1234567A 2026-10-05 09:30", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "S1234567A --appt 2026-10-05", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "S1234567A --appt", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_unsupportedOption_failure() {
        // options of other commands, and a misspelt option
        assertParseFailure(parser, "S1234567A 2 --name Alex", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "S1234567A 2 --to 2026-10-06", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "S1234567A 2 --details Bring pen", MESSAGE_INVALID_FORMAT);
    }
    @Test
    public void parse_numberWithLeadingZeros_success() {
        assertParseSuccess(parser, "s1234567a\t002", new DeleteAppointmentCommand(ID, 2));
    }

    @Test
    public void parse_invalidNumber_failure() {
        for (String number : new String[] {"0", "-1", "+1", "1.5", "two"}) {
            assertParseFailure(parser, "S1234567A " + number + "",
                    Appointment.MESSAGE_NUMBER_CONSTRAINTS);
        }
    }

    @Test
    public void parse_conflictingOrExtraSelectors_failure() {
        assertParseFailure(parser, "S1234567A 2 --appt 2026-10-05", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "S1234567A 2 3", MESSAGE_INVALID_FORMAT);
    }

}
