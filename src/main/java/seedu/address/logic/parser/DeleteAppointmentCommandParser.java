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

        boolean usesDateTime = argMultimap.getValue(PREFIX_APPOINTMENT).isPresent();
        String[] preamble = argMultimap.getPreamble().split("\\s+");
        if (preamble.length != (usesDateTime ? 1 : 2)) {
            throw new ParseException(invalidFormatMessage);
        }
        PatientId id;
        try {
            id = ParserUtil.parseId(preamble[0]);
        } catch (ParseException pe) {
            throw new ParseException(invalidFormatMessage, pe);
        }
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_APPOINTMENT);

        if (usesDateTime) {
            Appointment target = ParserUtil.parseAppointment(argMultimap.getValue(PREFIX_APPOINTMENT).orElseThrow());
            return new DeleteAppointmentCommand(id, target);
        }
        return new DeleteAppointmentCommand(id, ParserUtil.parseAppointmentNumber(preamble[1]));
    }
}
