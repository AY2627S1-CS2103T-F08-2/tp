package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DETAILS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TO;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditAppointmentCommand;
import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.AppointmentDetails;
import seedu.address.model.patient.PatientId;

public class EditAppointmentCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditAppointmentCommand.MESSAGE_USAGE);

    private static final PatientId ID = new PatientId("S1234567A");
    private static final Appointment NEW = new Appointment("2026-10-06 10:00");
    private static final AppointmentDetails DETAILS = new AppointmentDetails("Bring insulin pen");

    private final EditAppointmentCommandParser parser = new EditAppointmentCommandParser();

    @Test
    public void parse_moveOnly_success() {
        assertParseSuccess(parser, "S1234567A 2 --to 2026-10-06 10:00",
                new EditAppointmentCommand(ID, 2, Optional.of(NEW), Optional.empty()));
    }

    @Test
    public void parse_detailsOnly_success() {
        assertParseSuccess(parser, "S1234567A 2 --details Bring insulin pen",
                new EditAppointmentCommand(ID, 2, Optional.empty(), Optional.of(DETAILS)));
    }

    @Test
    public void parse_moveAndDetails_success() {
        EditAppointmentCommand expected = new EditAppointmentCommand(ID, 2, Optional.of(NEW), Optional.of(DETAILS));

        // options in any order, after the ID
        assertParseSuccess(parser, "S1234567A 2 --to 2026-10-06 10:00"
                + " --details Bring insulin pen", expected);
        assertParseSuccess(parser, "S1234567A 2 --details Bring insulin pen --to 2026-10-06 10:00", expected);

        // lower-case ID, tabs and extra spaces
        assertParseSuccess(parser, "  s1234567a\t2\t--to 2026-10-06 10:00"
                + "\t--details  Bring   insulin pen ", expected);
    }

    @Test
    public void parse_dateOnlyAppointments_success() {
        assertParseSuccess(parser, "S1234567A 2 --to 2026-10-05",
                new EditAppointmentCommand(ID, 2, Optional.of(new Appointment("2026-10-05")), Optional.empty()));
    }

    @Test
    public void parse_missingParts_failure() {
        // no ID, an invalid ID, or a list number in place of the ID
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "2 --to 2026-10-06", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "S12345678901 2 --to 2026-10-06", MESSAGE_INVALID_FORMAT);

        // no appointment to edit
        assertParseFailure(parser, "S1234567A --to 2026-10-06", MESSAGE_INVALID_FORMAT);

        // nothing to change
        assertParseFailure(parser, "S1234567A 2", EditAppointmentCommand.MESSAGE_NOT_EDITED);
    }

    @Test
    public void parse_invalidValues_failure() {
        for (String dateTime : new String[] {"2026-02-30", "2026-10-06 9:30", "+12345-01-01",
            "+2026-01-01", "-2026-01-01", "2026-1-05", "2026-01-5"}) {
            assertParseFailure(parser, "S1234567A 2 --to " + dateTime, Appointment.MESSAGE_CONSTRAINTS);
        }
        assertParseFailure(parser, "S1234567A 2 --details \t ",
                AppointmentDetails.MESSAGE_CONSTRAINTS);

        // an option with no value at the end of the command names its field
        assertParseFailure(parser, "S1234567A 2 --to", Appointment.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "S1234567A 2 --details", AppointmentDetails.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_repeatedOption_failure() {
        assertParseFailure(parser, "S1234567A 2 --to 2026-10-06 --to 2026-10-07",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_TO));
        assertParseFailure(parser, "S1234567A 2 --details A --details B",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_DETAILS));
    }

    @Test
    public void parse_unsupportedOption_failure() {
        // options of other commands, and a misspelt option
        assertParseFailure(parser, "S1234567A 2 --name Alex", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "S1234567A 2 --add-appt 2026-10-06", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "S1234567A 2 --from 2026-10-06", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "S1234567A 2 --detail Bring pen", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "S1234567A --appt 2026-10-05 --to 2026-10-06", MESSAGE_INVALID_FORMAT);
    }
    @Test
    public void parse_largestNumber_success() {
        assertParseSuccess(parser, "s1234567a\t2147483647 --to 2026-10-06 10:00",
                new EditAppointmentCommand(ID, Integer.MAX_VALUE, Optional.of(NEW), Optional.empty()));
    }

    @Test
    public void parse_invalidNumber_failure() {
        for (String number : new String[] {"0", "-1", "+1", "1.5", "two", "2147483648"}) {
            assertParseFailure(parser, "S1234567A " + number + " --to 2026-10-06 10:00",
                    Appointment.MESSAGE_NUMBER_CONSTRAINTS);
        }
    }

    @Test
    public void parse_conflictingOrExtraSelectors_failure() {
        assertParseFailure(parser, "S1234567A 2 --appt 2026-10-05 --to 2026-10-06 10:00", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "S1234567A 2 3 --to 2026-10-06 10:00", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_numberWithoutChange_failure() {
        assertParseFailure(parser, "S1234567A 2", EditAppointmentCommand.MESSAGE_NOT_EDITED);
        assertParseFailure(parser, "S1234567A 2 --to 2026-10-06 --to 2026-10-07",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_TO));
    }

}
