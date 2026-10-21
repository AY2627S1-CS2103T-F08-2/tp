package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_APPOINTMENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DETAILS;

import java.util.Optional;

import seedu.address.logic.commands.AddAppointmentCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.AppointmentDetails;
import seedu.address.model.patient.PatientId;

/**
 * Parses input arguments and creates a new AddAppointmentCommand object.
 */
public class AddAppointmentCommandParser implements Parser<AddAppointmentCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddAppointmentCommand
     * and returns an AddAppointmentCommand object for execution.
     *
     * @throws ParseException if the user input does not conform to the expected format.
     */
    public AddAppointmentCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String invalidFormatMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                AddAppointmentCommand.MESSAGE_USAGE);
        String normalizedArgs = ArgumentTokenizer.normalizeOptionSeparators(args, invalidFormatMessage,
                PREFIX_APPOINTMENT, PREFIX_DETAILS);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(normalizedArgs, PREFIX_APPOINTMENT,
                PREFIX_DETAILS);

        PatientId id;
        try {
            id = ParserUtil.parseId(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(invalidFormatMessage, pe);
        }
        if (argMultimap.getValue(PREFIX_APPOINTMENT).isEmpty()) {
            throw new ParseException(invalidFormatMessage);
        }
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_APPOINTMENT, PREFIX_DETAILS);

        Appointment appointment = ParserUtil.parseAppointment(argMultimap.getValue(PREFIX_APPOINTMENT).get());
        Optional<AppointmentDetails> details = argMultimap.getValue(PREFIX_DETAILS).isPresent()
                ? Optional.of(ParserUtil.parseAppointmentDetails(argMultimap.getValue(PREFIX_DETAILS).get()))
                : Optional.empty();
        Appointment toAdd = details.isEmpty() ? appointment
                : new Appointment(appointment.toString(), details);

        return new AddAppointmentCommand(id, toAdd);
    }
}
