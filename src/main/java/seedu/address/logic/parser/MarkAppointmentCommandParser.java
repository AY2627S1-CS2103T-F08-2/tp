package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.MarkAppointmentCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.patient.PatientId;

/**
 * Parses a patient ID and appointment number into a MarkAppointmentCommand.
 */
public class MarkAppointmentCommandParser implements Parser<MarkAppointmentCommand> {

    /**
     * Parses exactly a patient ID followed by a positive appointment number.
     *
     * @throws ParseException if the arguments do not conform to the expected format.
     */
    public MarkAppointmentCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String invalidFormatMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                MarkAppointmentCommand.MESSAGE_USAGE);
        String[] arguments = args.strip().split("\\s+");
        if (arguments.length != 2) {
            throw new ParseException(invalidFormatMessage);
        }
        PatientId id;
        try {
            id = ParserUtil.parseId(arguments[0]);
        } catch (ParseException pe) {
            throw new ParseException(invalidFormatMessage, pe);
        }
        return new MarkAppointmentCommand(id, ParserUtil.parseAppointmentNumber(arguments[1]));
    }
}
