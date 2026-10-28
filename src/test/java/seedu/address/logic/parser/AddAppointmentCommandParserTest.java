package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DETAILS;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddAppointmentCommand;
import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.AppointmentDetails;
import seedu.address.model.patient.PatientId;

public class AddAppointmentCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddAppointmentCommand.MESSAGE_USAGE);

    private static final PatientId ID = new PatientId("S1234567A");
    private static final Appointment APPOINTMENT = new Appointment("2026-10-05 09:30");
    private static final AppointmentDetails DETAILS = new AppointmentDetails("Bring insulin pen");

    private final AddAppointmentCommandParser parser = new AddAppointmentCommandParser();

    @Test
    public void parse_apptOnly_success() {
        assertParseSuccess(parser, "S1234567A --date 2026-10-05 09:30",
                new AddAppointmentCommand(ID, APPOINTMENT));
    }

    @Test
    public void parse_apptWithDetails_success() {
        Appointment withDetails = new Appointment("2026-10-05 09:30", Optional.of(DETAILS));

        assertParseSuccess(parser, "S1234567A --date 2026-10-05 09:30 --details Bring insulin pen",
                new AddAppointmentCommand(ID, withDetails));

        // options in any order
        assertParseSuccess(parser, "S1234567A --details Bring insulin pen --date 2026-10-05 09:30",
                new AddAppointmentCommand(ID, withDetails));

        // lower-case ID, tabs and extra spaces
        assertParseSuccess(parser, "  s1234567a\t--date 2026-10-05   09:30"
                + "\t--details  Bring   insulin pen ", new AddAppointmentCommand(ID, withDetails));
    }

    @Test
    public void parse_dateOnlyAppointment_success() {
        assertParseSuccess(parser, "S1234567A --date 2026-10-05",
                new AddAppointmentCommand(ID, new Appointment("2026-10-05")));
    }

    @Test
    public void parse_zeroYear_success() {
        assertParseSuccess(parser, "S1234567A --date 0000-01-01",
                new AddAppointmentCommand(ID, new Appointment("0000-01-01")));
    }

    @Test
    public void parse_missingOrInvalidId_failure() {
        // no ID, an invalid ID, or the options before the ID
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "--date 2026-10-05 09:30", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "S12345678901 --date 2026-10-05 09:30", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_missingDate_failure() {
        assertParseFailure(parser, "S1234567A", AddAppointmentCommand.MESSAGE_MISSING_DATE);
        assertParseFailure(parser, "S1234567A --details Bring pen", AddAppointmentCommand.MESSAGE_MISSING_DATE);
    }

    @Test
    public void parse_wordsBetweenIdAndOptions_failure() {
        // the date without its option, as addappt took it before, and other extra words
        assertParseFailure(parser, "S1234567A 2026-10-05 09:30", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "S1234567A 2026-10-05 --date 2026-10-05", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "S1234567A extra --date 2026-10-05", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidValues_failure() {
        for (String dateTime : new String[] {"2026-02-30", "2026-10-05 9:30", "+12345-01-01",
            "+2026-01-01", "-2026-01-01", "2026-1-05", "2026-01-5", "2026-10-05 09:30 extra"}) {
            assertParseFailure(parser, "S1234567A --date " + dateTime, Appointment.MESSAGE_CONSTRAINTS);
        }
        assertParseFailure(parser, "S1234567A --date 2026-10-05 --details \t ",
                AppointmentDetails.MESSAGE_CONSTRAINTS);

        // an option with no value names its field
        assertParseFailure(parser, "S1234567A --date", Appointment.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "S1234567A --date --details Bring pen", Appointment.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "S1234567A --date 2026-10-05 --details", AppointmentDetails.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_repeatedOption_failure() {
        assertParseFailure(parser, "S1234567A --date 2026-10-05 --details A --details B",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_DETAILS));
        assertParseFailure(parser, "S1234567A --date 2026-10-05 --date 2026-10-06",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_DATE));
    }

    @Test
    public void parse_unsupportedOption_failure() {
        // options of other commands, and a misspelt option
        assertParseFailure(parser, "S1234567A --date 2026-10-05 --name Alex", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "S1234567A --date 2026-10-05 --to 2026-10-06", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "S1234567A --date 2026-10-05 --detail Bring pen", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "S1234567A --appt 2026-10-05", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "S1234567A --date 2026-10-05 --appt 2026-10-06", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "S1234567A --DATE 2026-10-05", MESSAGE_INVALID_FORMAT);
    }
}
