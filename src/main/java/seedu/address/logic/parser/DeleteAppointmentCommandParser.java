package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_APPOINTMENT;

import seedu.address.logic.commands.DeleteAppointmentCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.PatientId;

/**
 * Parses input arguments and creates a new DeleteAppointmentCommand object.
 */
public class DeleteAppointmentCommandParser implements Parser<DeleteAppointmentCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the DeleteAppointmentCommand
     * and returns a DeleteAppointmentCommand object for execution.
     *
     * @throws ParseException if the user input does not conform to the expected format.
     */
    public DeleteAppointmentCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String invalidFormatMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                DeleteAppointmentCommand.MESSAGE_USAGE);
        String normalizedArgs = ArgumentTokenizer.normalizeOptionSeparators(args, invalidFormatMessage,
                PREFIX_APPOINTMENT);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(normalizedArgs, PREFIX_APPOINTMENT);

        PatientId id;
        try {
            id = ParserUtil.parseId(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(invalidFormatMessage, pe);
        }
        if (argMultimap.getValue(PREFIX_APPOINTMENT).isEmpty()) {
            throw new ParseException(invalidFormatMessage);
        }
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_APPOINTMENT);

        Appointment targetAppointment = ParserUtil.parseAppointment(argMultimap.getValue(PREFIX_APPOINTMENT).get());

        return new DeleteAppointmentCommand(id, targetAppointment);
    }
}
