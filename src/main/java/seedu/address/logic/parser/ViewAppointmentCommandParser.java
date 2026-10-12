package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_VIEW_APPOINTMENT_FROM_AFTER_TILL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FROM;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TILL;

import java.time.LocalDate;
import java.util.Optional;

import seedu.address.logic.commands.ViewAppointmentCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ViewAppointmentCommand object.
 */
public class ViewAppointmentCommandParser implements Parser<ViewAppointmentCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the ViewAppointmentCommand
     * and returns a ViewAppointmentCommand object for execution.
     * The arguments are either a single date, or a {@code --from} date, a {@code --till} date, or both.
     *
     * @throws ParseException if the user input does not conform to the expected format.
     */
    public ViewAppointmentCommand parse(String args) throws ParseException {
        String invalidFormatMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                ViewAppointmentCommand.MESSAGE_USAGE);
        String normalizedArgs = ArgumentTokenizer.normalizeOptionSeparators(args, invalidFormatMessage,
                PREFIX_FROM, PREFIX_TILL);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(normalizedArgs, PREFIX_FROM, PREFIX_TILL);
        String preamble = argMultimap.getPreamble();
        boolean hasRange = argMultimap.getValue(PREFIX_FROM).isPresent()
                || argMultimap.getValue(PREFIX_TILL).isPresent();

        if (!hasRange) {
            if (preamble.isEmpty()) {
                throw new ParseException(invalidFormatMessage);
            }
            return new ViewAppointmentCommand(ParserUtil.parseDate(preamble));
        }

        if (!preamble.isEmpty()) {
            throw new ParseException(invalidFormatMessage);
        }
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_FROM, PREFIX_TILL);
        Optional<LocalDate> startDate = parseOptionalDate(argMultimap, PREFIX_FROM);
        Optional<LocalDate> endDate = parseOptionalDate(argMultimap, PREFIX_TILL);
        if (startDate.isPresent() && endDate.isPresent() && startDate.get().isAfter(endDate.get())) {
            throw new ParseException(MESSAGE_VIEW_APPOINTMENT_FROM_AFTER_TILL);
        }
        return new ViewAppointmentCommand(startDate, endDate);
    }

    private static Optional<LocalDate> parseOptionalDate(ArgumentMultimap argMultimap, Prefix prefix)
            throws ParseException {
        Optional<String> date = argMultimap.getValue(prefix);
        return date.isPresent() ? Optional.of(ParserUtil.parseDate(date.get())) : Optional.empty();
    }
}
