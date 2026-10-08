package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MEDICAL_HISTORY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NEXT_APPOINTMENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.patient.Address;
import seedu.address.model.patient.MedicalHistory;
import seedu.address.model.patient.Name;
import seedu.address.model.patient.NextAppointment;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientId;
import seedu.address.model.patient.Phone;

/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddCommandParser implements Parser<AddCommand> {

    private static final Pattern OPTION_PATTERN = Pattern.compile("(?<!\\S)--\\S*");

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    public AddCommand parse(String args) throws ParseException {
        String normalizedArgs = normalizeOptionSeparators(args);
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(normalizedArgs, PREFIX_ID, PREFIX_NAME, PREFIX_PHONE, PREFIX_ADDRESS,
                        PREFIX_MEDICAL_HISTORY, PREFIX_NEXT_APPOINTMENT);

        if (!arePrefixesPresent(argMultimap, PREFIX_ID, PREFIX_NAME, PREFIX_PHONE, PREFIX_ADDRESS)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_ID, PREFIX_NAME, PREFIX_PHONE, PREFIX_ADDRESS,
                PREFIX_MEDICAL_HISTORY, PREFIX_NEXT_APPOINTMENT);
        PatientId id = ParserUtil.parseId(argMultimap.getValue(PREFIX_ID).get());
        Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
        Phone phone = ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get());
        Address address = ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS).get());

        MedicalHistory medicalHistory =
                argMultimap.getValue(PREFIX_MEDICAL_HISTORY).isPresent()
                ? ParserUtil.parseMedicalHistory(argMultimap.getValue(PREFIX_MEDICAL_HISTORY).get())
                : null;
        NextAppointment nextAppointment =
                argMultimap.getValue(PREFIX_NEXT_APPOINTMENT).isPresent()
                ? ParserUtil.parseNextAppointment(argMultimap.getValue(PREFIX_NEXT_APPOINTMENT).get())
                : null;

        Patient patient = new Patient(id, name, phone, address, Optional.ofNullable(medicalHistory),
                Optional.ofNullable(nextAppointment));

        return new AddCommand(patient);
    }

    /**
     * Recognizes whitespace-delimited long options and supplies space separators for the existing tokenizer.
     *
     * @throws ParseException if an option is not supported by the add command.
     */
    private static String normalizeOptionSeparators(String args) throws ParseException {
        Matcher matcher = OPTION_PATTERN.matcher(args);
        StringBuilder normalizedArgs = new StringBuilder();
        while (matcher.find()) {
            String option = matcher.group();
            if (Stream.of(PREFIX_ID, PREFIX_NAME, PREFIX_PHONE, PREFIX_ADDRESS,
                    PREFIX_MEDICAL_HISTORY, PREFIX_NEXT_APPOINTMENT)
                    .noneMatch(prefix -> prefix.getPrefix().trim().equals(option))) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
            }
            matcher.appendReplacement(normalizedArgs, Matcher.quoteReplacement(" " + option + " "));
        }
        matcher.appendTail(normalizedArgs);
        return normalizedArgs.toString();
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

}
