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
        String invalidFormatMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                ViewAppointmentCommand.MESSAGE_USAGE);
        String normalizedArgs = ArgumentTokenizer.normalizeOptionSeparators(args, invalidFormatMessage,
                PREFIX_FROM, PREFIX_TO, PREFIX_ID);
        if (normalizedArgs.isBlank()) {
            // A plain viewappt shows the upcoming appointments
            return new ViewAppointmentCommand(Optional.empty(), Optional.empty(), Optional.empty());
        }
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(normalizedArgs, PREFIX_ID, PREFIX_FROM, PREFIX_TO);
        // Duplicate options are reported whatever the rest of the input looks like
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_ID, PREFIX_FROM, PREFIX_TO);
        Optional<LocalDate> date = parseDateOrEmpty(argMultimap.getPreamble());
        if (date.isPresent()) {
            return parseWithDate(argMultimap, date.get(), invalidFormatMessage);
        }
        return parseWithOptionalDateRange(argMultimap, invalidFormatMessage);
    }

    /**
     * Parses the date form {@code DATE [--id ID]}, which cannot combine with the range options.
     *
     * @throws ParseException if a range option is also present.
     */
    private static ViewAppointmentCommand parseWithDate(ArgumentMultimap argMultimap, LocalDate date,
            String invalidFormatMessage) throws ParseException {
        boolean hasRangeOptions = argMultimap.getValue(PREFIX_FROM).isPresent()
                || argMultimap.getValue(PREFIX_TO).isPresent();
        if (hasRangeOptions) {
            throw new ParseException(invalidFormatMessage);
        }
        return new ViewAppointmentCommand(Optional.of(date), Optional.of(date),
                parseOptionalPatientId(argMultimap));
    }

    /**
     * Parses the range form {@code [--id ID] [--from START_DATE] [--to END_DATE]}, in any order.
     *
     * @throws ParseException if the user input does not conform to the expected format.
     */
    private static ViewAppointmentCommand parseWithOptionalDateRange(ArgumentMultimap argMultimap,
            String invalidFormatMessage) throws ParseException {
        if (!argMultimap.getPreamble().isEmpty()) {
            // A bare word that is not a date has no place in the range form
            throw new ParseException(invalidFormatMessage);
        }
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
