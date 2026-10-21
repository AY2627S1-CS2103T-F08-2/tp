package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_APPOINTMENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DETAILS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TO;

import java.util.Optional;

import seedu.address.logic.commands.EditAppointmentCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.AppointmentDetails;
import seedu.address.model.patient.PatientId;

/**
 * Parses input arguments and creates a new EditAppointmentCommand object.
 */
public class EditAppointmentCommandParser implements Parser<EditAppointmentCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the EditAppointmentCommand
     * and returns an EditAppointmentCommand object for execution.
     *
     * @throws ParseException if the user input does not conform to the expected format.
     */
    public EditAppointmentCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String invalidFormatMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                EditAppointmentCommand.MESSAGE_USAGE);
        String normalizedArgs = ArgumentTokenizer.normalizeOptionSeparators(args, invalidFormatMessage,
                PREFIX_APPOINTMENT, PREFIX_TO, PREFIX_DETAILS);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(normalizedArgs, PREFIX_APPOINTMENT, PREFIX_TO,
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
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_APPOINTMENT, PREFIX_TO, PREFIX_DETAILS);

        Appointment targetAppointment = ParserUtil.parseAppointment(argMultimap.getValue(PREFIX_APPOINTMENT).get());
        Optional<Appointment> newDateTime = argMultimap.getValue(PREFIX_TO).isPresent()
                ? Optional.of(ParserUtil.parseAppointment(argMultimap.getValue(PREFIX_TO).get()))
                : Optional.empty();
        Optional<AppointmentDetails> newDetails = argMultimap.getValue(PREFIX_DETAILS).isPresent()
                ? Optional.of(ParserUtil.parseAppointmentDetails(argMultimap.getValue(PREFIX_DETAILS).get()))
                : Optional.empty();
        if (newDateTime.isEmpty() && newDetails.isEmpty()) {
            throw new ParseException(EditAppointmentCommand.MESSAGE_NOT_EDITED);
        }

        return new EditAppointmentCommand(id, targetAppointment, newDateTime, newDetails);
    }
}
