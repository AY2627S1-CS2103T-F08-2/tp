package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FROM;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TO;

import java.util.Optional;

import seedu.address.logic.commands.ClearAppointmentCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.PatientId;

/**
 * Parses input arguments and creates a new ClearAppointmentCommand object.
 */
public class ClearAppointmentCommandParser implements Parser<ClearAppointmentCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the ClearAppointmentCommand and returns a
     * ClearAppointmentCommand object for execution: a patient ID alone, or {@code --from} and {@code --to}, with an
     * optional {@code --id}, in any order.
     *
     * @throws ParseException if the user input does not conform to the expected format.
     */
    public ClearAppointmentCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String invalidFormatMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                ClearAppointmentCommand.MESSAGE_USAGE);
        String normalizedArgs = ArgumentTokenizer.normalizeOptionSeparators(args, invalidFormatMessage,
                PREFIX_ID, PREFIX_FROM, PREFIX_TO);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(normalizedArgs, PREFIX_ID, PREFIX_FROM, PREFIX_TO);
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_ID, PREFIX_FROM, PREFIX_TO);

        String preamble = argMultimap.getPreamble();
        Optional<String> idOption = argMultimap.getValue(PREFIX_ID);
        if (!preamble.isEmpty() && idOption.isPresent()) {
            throw new ParseException(invalidFormatMessage);
        }
        Optional<PatientId> patientId = preamble.isEmpty()
                ? (idOption.isPresent() ? Optional.of(ParserUtil.parseId(idOption.get())) : Optional.empty())
                : Optional.of(ParserUtil.parseId(preamble));
        Optional<Appointment> from = parseOptionalDateTime(argMultimap, PREFIX_FROM);
        Optional<Appointment> to = parseOptionalDateTime(argMultimap, PREFIX_TO);
        if (patientId.isEmpty() && from.isEmpty() && to.isEmpty()) {
            throw new ParseException(invalidFormatMessage);
        }
        if (from.isPresent() && to.isPresent()
                && ClearAppointmentCommand.startOf(from.get()).isAfter(ClearAppointmentCommand.endOf(to.get()))) {
            throw new ParseException(ClearAppointmentCommand.MESSAGE_FROM_AFTER_TO);
        }
        return new ClearAppointmentCommand(patientId, from, to);
    }

    /**
     * Returns the date, or date and time, given to {@code prefix}, or {@code Optional#empty()} if it is absent.
     */
    private static Optional<Appointment> parseOptionalDateTime(ArgumentMultimap argMultimap, Prefix prefix)
            throws ParseException {
        Optional<String> value = argMultimap.getValue(prefix);
        return value.isPresent() ? Optional.of(ParserUtil.parseAppointment(value.get())) : Optional.empty();
    }
}
