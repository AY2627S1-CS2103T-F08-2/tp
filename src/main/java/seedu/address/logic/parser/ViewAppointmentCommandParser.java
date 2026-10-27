package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.ViewAppointmentCommand.MESSAGE_VIEW_APPOINTMENT_DATE_AFTER_OPTIONS;
import static seedu.address.logic.commands.ViewAppointmentCommand.MESSAGE_VIEW_APPOINTMENT_FROM_AFTER_TO;
import static seedu.address.logic.commands.ViewAppointmentCommand.MESSAGE_VIEW_APPOINTMENT_TWO_DATES_IN_ONE_OPTION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FROM;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TO;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.regex.Pattern;

import seedu.address.commons.util.DateUtil;
import seedu.address.logic.commands.ViewAppointmentCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.patient.PatientId;

/**
 * Parses input arguments and creates a new ViewAppointmentCommand object.
 */
public class ViewAppointmentCommandParser implements Parser<ViewAppointmentCommand> {

    /** One word of digits and punctuation, with at least one digit. */
    private static final Pattern DATE_LIKE_WORD = Pattern.compile("[\\d\\p{Punct}]*\\d[\\d\\p{Punct}]*");

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
            // A plain viewappt shows this week
            return new ViewAppointmentCommand(Optional.empty(), Optional.empty(), Optional.empty());
        }
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(normalizedArgs, PREFIX_ID, PREFIX_FROM, PREFIX_TO);
        // Duplicate options are reported whatever the rest of the input looks like
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_ID, PREFIX_FROM, PREFIX_TO);
        // A date after --id is read into the ID value, so the order is reported before that value is parsed
        verifyNoDateAfterId(argMultimap);
        // A second date is read into --from or --to, so the missing option is reported before the value is parsed
        verifyNoSecondDate(argMultimap, PREFIX_FROM);
        verifyNoSecondDate(argMultimap, PREFIX_TO);
        String preamble = argMultimap.getPreamble();
        if (isDateLike(preamble)) {
            // A date that is not real gets the date error, as a date given to --from or --to does
            return parseWithDate(argMultimap, ParserUtil.parseDate(preamble), invalidFormatMessage);
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

    /**
     * Rejects a DATE that comes after {@code PREFIX_ID}: the tokenizer reads it as part of the ID value, so the
     * ID error would blame an ID that is itself valid. The order is reported only when the value ends in a real
     * date that cannot be a patient ID. A value that ends in anything else, such as a digit-only patient ID
     * typed with a stray space, fails as the ID that the user gave to {@code PREFIX_ID}.
     *
     * @throws ParseException if the ID value ends in a real date that cannot be a patient ID.
     */
    private static void verifyNoDateAfterId(ArgumentMultimap argMultimap) throws ParseException {
        Optional<String> id = argMultimap.getValue(PREFIX_ID);
        if (id.isEmpty()) {
            return;
        }
        String[] wordsInId = id.get().split("\\s+");
        String lastWord = wordsInId[wordsInId.length - 1];
        if (wordsInId.length > 1 && isMisplacedDate(lastWord)) {
            throw new ParseException(MESSAGE_VIEW_APPOINTMENT_DATE_AFTER_OPTIONS);
        }
    }

    /**
     * Rejects a second date typed into a range option's value: the tokenizer reads it as part of that value,
     * and the date error would blame dates that are themselves valid. A range gives each date its own option,
     * with {@code --from} for the start and {@code --to} for the end. A value whose last word is not a real
     * date fails as the date that the option takes, as before.
     *
     * @throws ParseException if the {@code prefix} value has several words and ends in a real date.
     */
    private static void verifyNoSecondDate(ArgumentMultimap argMultimap, Prefix prefix) throws ParseException {
        Optional<String> value = argMultimap.getValue(prefix);
        if (value.isEmpty()) {
            return;
        }
        String[] words = value.get().split("\\s+");
        String lastWord = words[words.length - 1];
        if (words.length > 1 && isRealDate(lastWord)) {
            throw new ParseException(MESSAGE_VIEW_APPOINTMENT_TWO_DATES_IN_ONE_OPTION);
        }
    }

    /**
     * Returns true if {@code word} can only have been typed as a date after {@code PREFIX_ID}: it is a real
     * date that cannot be a patient ID. A real date contains punctuation, so it is never a valid ID today;
     * if a format change ever makes both possible, the ID wins, as the value of the ID option.
     */
    private static boolean isMisplacedDate(String word) {
        return isRealDate(word) && !PatientId.isValidId(word);
    }

    /** Returns true if {@code word} is a real date in the {@code yyyy-MM-dd} format. */
    private static boolean isRealDate(String word) {
        try {
            DateUtil.parseDate(word);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Returns true if {@code word} is one word of digits and punctuation, such as {@code 2026-02-30} or
     * {@code 2026/10/15}, which before the options can only be meant as a date. Any other word, such as
     * {@code invalid}, could be a mistyped option, so it gets the command format instead of the date error.
     */
    private static boolean isDateLike(String word) {
        return DATE_LIKE_WORD.matcher(word).matches();
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
