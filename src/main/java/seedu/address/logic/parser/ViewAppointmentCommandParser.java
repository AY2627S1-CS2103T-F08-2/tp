package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_VIEW_APPOINTMENT_FROM_AFTER_TO;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FROM;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TO;

import java.time.LocalDate;
import java.util.Optional;

import seedu.address.logic.commands.ViewAppointmentCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.patient.PatientId;

/**
 * Parses input arguments and creates a new ViewAppointmentCommand object.
 */
public class ViewAppointmentCommandParser implements Parser<ViewAppointmentCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the ViewAppointmentCommand
     * and returns a ViewAppointmentCommand object for execution.
     * The arguments are an optional patient ID, combined with a date, a {@code --from} date, a {@code --to} date,
     * or none of these. A date with no option is the preamble, and comes before the options.
     *
     * @throws ParseException if the user input does not conform to the expected format.
     */
    public ViewAppointmentCommand parse(String args) throws ParseException {
        String invalidFormatMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                ViewAppointmentCommand.MESSAGE_USAGE);
        String normalizedArgs = ArgumentTokenizer.normalizeOptionSeparators(args, invalidFormatMessage,
                PREFIX_FROM, PREFIX_TO, PREFIX_ID);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(normalizedArgs, PREFIX_FROM, PREFIX_TO, PREFIX_ID);
        String preamble = argMultimap.getPreamble();
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_FROM, PREFIX_TO, PREFIX_ID);
        Optional<PatientId> patientId = parseOptionalPatientId(argMultimap, invalidFormatMessage);
        boolean hasDateFlags = argMultimap.getValue(PREFIX_FROM).isPresent()
                || argMultimap.getValue(PREFIX_TO).isPresent();

        if (!hasDateFlags) {
            if (preamble.isEmpty()) {
                // A plain viewappt shows the upcoming appointments
                return new ViewAppointmentCommand(Optional.empty(), Optional.empty(), patientId);
            }
            LocalDate date = ParserUtil.parseDate(preamble);
            return new ViewAppointmentCommand(Optional.of(date), Optional.of(date), patientId);
        }

        if (!preamble.isEmpty()) {
            throw new ParseException(invalidFormatMessage);
        }
        Optional<LocalDate> startDate = parseOptionalDate(argMultimap, PREFIX_FROM);
        Optional<LocalDate> endDate = parseOptionalDate(argMultimap, PREFIX_TO);
        if (startDate.isPresent() && endDate.isPresent() && startDate.get().isAfter(endDate.get())) {
            throw new ParseException(MESSAGE_VIEW_APPOINTMENT_FROM_AFTER_TO);
        }
        return new ViewAppointmentCommand(startDate, endDate, patientId);
    }

    private static Optional<LocalDate> parseOptionalDate(ArgumentMultimap argMultimap, Prefix prefix)
            throws ParseException {
        Optional<String> date = argMultimap.getValue(prefix);
        return date.isPresent() ? Optional.of(ParserUtil.parseDate(date.get())) : Optional.empty();
    }

    private static Optional<PatientId> parseOptionalPatientId(ArgumentMultimap argMultimap,
            String invalidFormatMessage) throws ParseException {
        Optional<String> value = argMultimap.getValue(PREFIX_ID);
        if (value.isEmpty()) {
            return Optional.empty();
        }
        // A date after the options would end up inside the ID value, so an ID keeps to one token
        String trimmedId = value.get().trim();
        if (trimmedId.isEmpty() || trimmedId.contains(" ")) {
            throw new ParseException(invalidFormatMessage);
        }
        return Optional.of(ParserUtil.parseId(trimmedId));
    }
}
