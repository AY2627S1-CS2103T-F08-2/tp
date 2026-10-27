package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADD_DETAILS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DETAILS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TO;

import java.util.Optional;

import seedu.address.logic.commands.EditAppointmentCommand;
import seedu.address.logic.commands.OptionalFieldEdit;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.AppointmentDetails;
import seedu.address.model.patient.PatientId;

/**
 * Parses input arguments and creates a new EditAppointmentCommand object.
 */
public class EditAppointmentCommandParser implements Parser<EditAppointmentCommand> {

    /**
     * Parses a patient ID and appointment number followed by a new date/time, a change to the details, or both.
     *
     * @throws ParseException if the user input does not conform to the expected format.
     */
    public EditAppointmentCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String invalidFormatMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                EditAppointmentCommand.MESSAGE_USAGE);
        Prefix[] prefixes = {PREFIX_TO, PREFIX_DETAILS, PREFIX_ADD_DETAILS};
        String normalizedArgs = ArgumentTokenizer.normalizeOptionSeparators(args, invalidFormatMessage, prefixes);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(normalizedArgs, prefixes);

        String[] preamble = argMultimap.getPreamble().split("\\s+");
        if (preamble.length != 2) {
            throw new ParseException(invalidFormatMessage);
        }
        PatientId id;
        try {
            id = ParserUtil.parseId(preamble[0]);
        } catch (ParseException pe) {
            throw new ParseException(invalidFormatMessage, pe);
        }
        argMultimap.verifyNoDuplicatePrefixesFor(prefixes);

        int number = ParserUtil.parseAppointmentNumber(preamble[1]);
        Optional<Appointment> newDateTime = argMultimap.getValue(PREFIX_TO).isPresent()
                ? Optional.of(ParserUtil.parseAppointment(argMultimap.getValue(PREFIX_TO).get()))
                : Optional.empty();
        Optional<OptionalFieldEdit<AppointmentDetails>> detailsEdit = ParserUtil.parseOptionalFieldEdit(
                argMultimap, PREFIX_DETAILS, PREFIX_ADD_DETAILS, ParserUtil::parseAppointmentDetails);
        if (newDateTime.isEmpty() && detailsEdit.isEmpty()) {
            throw new ParseException(EditAppointmentCommand.MESSAGE_NOT_EDITED);
        }

        return new EditAppointmentCommand(id, number, newDateTime, detailsEdit);
    }
}
