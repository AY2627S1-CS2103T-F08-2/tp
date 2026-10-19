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
     * The arguments are a date with an optional patient ID, or a patient ID with an optional date range,
     * or none of these.
     *
     * @throws ParseException if the user input does not conform to the expected format.
     */
    public ViewAppointmentCommand parse(String args) throws ParseException {
        String normalizedArgs = ArgumentTokenizer.normalizeOptionSeparators(args,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewAppointmentCommand.MESSAGE_USAGE),
                PREFIX_FROM, PREFIX_TO, PREFIX_ID);
        if (normalizedArgs.isBlank()) {
            // A plain viewappt shows the upcoming appointments
            return new ViewAppointmentCommand(Optional.empty(), Optional.empty(), Optional.empty());
        }
        Optional<ViewAppointmentCommand> dateCommand = parseWithDate(normalizedArgs);
        if (dateCommand.isPresent()) {
            return dateCommand.get();
        }
        return parseWithOptionalDateRange(normalizedArgs);
    }

    /**
     * Parses the given {@code String} of arguments of the form {@code DATE [--id ID]}
     * Returns the corresponding {@code Optional<ViewAppointmentCommand>}, or {@code Optional#empty()} if
     * the preamble is not a date or a range option is present.
     */
    private static Optional<ViewAppointmentCommand> parseWithDate(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_ID, PREFIX_FROM, PREFIX_TO);
        Optional<LocalDate> date = parseDateOrEmpty(argMultimap.getPreamble());
        if (date.isEmpty()) {
            return Optional.empty();
        }
        boolean hasRangeOptions = argMultimap.getValue(PREFIX_FROM).isPresent()
                || argMultimap.getValue(PREFIX_TO).isPresent();
        if (hasRangeOptions) {
            return Optional.empty();
        }
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_ID);
        Optional<PatientId> patientId = parseOptionalPatientId(argMultimap);
        return Optional.of(new ViewAppointmentCommand(
                Optional.of(date.get()), Optional.of(date.get()), patientId));
    }

    /**
     * Parses the given {@code String} of arguments of the form {@code [--id ID] [--from START_DATE] [--to END_DATE]},
     * in any order, and returns the corresponding {@code Optional<ViewAppointmentCommand>}.
     *
     * @throws ParseException if the user input does not conform to the expected format.
     */
    private static ViewAppointmentCommand parseWithOptionalDateRange(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_ID, PREFIX_FROM, PREFIX_TO);
        if (!argMultimap.getPreamble().isEmpty()) {
            // A date that does not parse, or any other word, cannot be used here
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    ViewAppointmentCommand.MESSAGE_USAGE));
        }
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_ID, PREFIX_FROM, PREFIX_TO);
        Optional<PatientId> patientId = parseOptionalPatientId(argMultimap);
        Optional<LocalDate> startDate = parseOptionalDate(argMultimap, PREFIX_FROM);
        Optional<LocalDate> endDate = parseOptionalDate(argMultimap, PREFIX_TO);
        if (startDate.isPresent() && endDate.isPresent() && startDate.get().isAfter(endDate.get())) {
            throw new ParseException(MESSAGE_VIEW_APPOINTMENT_FROM_AFTER_TO);
        }
        return new ViewAppointmentCommand(startDate, endDate, patientId);
    }

    /** Returns the date, or {@code Optional#empty()} if {@code date} is not a real date in {@code yyyy-MM-dd}. */
    private static Optional<LocalDate> parseDateOrEmpty(String date) {
        try {
            return Optional.of(ParserUtil.parseDate(date));
        } catch (ParseException e) {
            return Optional.empty();
        }
    }

    /** Returns the date given to {@code prefix}, or {@code Optional#empty()} if the prefix is absent. */
    private static Optional<LocalDate> parseOptionalDate(ArgumentMultimap argMultimap, Prefix prefix)
            throws ParseException {
        Optional<String> date = argMultimap.getValue(prefix);
        return date.isPresent() ? Optional.of(ParserUtil.parseDate(date.get())) : Optional.empty();
    }

    /** Returns the patient ID given to {@code PREFIX_ID}, or {@code Optional#empty()} if the prefix is absent. */
    private static Optional<PatientId> parseOptionalPatientId(ArgumentMultimap argMultimap) throws ParseException {
        Optional<String> id = argMultimap.getValue(PREFIX_ID);
        return id.isPresent() ? Optional.of(ParserUtil.parseId(id.get())) : Optional.empty();
    }
}
