package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MEDICAL_HISTORY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTES;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;

import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPatientDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.patient.PatientId;

/**
 * Parses input arguments and creates a new EditCommand object
 */
public class EditCommandParser implements Parser<EditCommand> {

    public static final String MESSAGE_RETIRED_APPOINTMENT_OPTIONS =
            "The --add-appt and --remove-appt options no longer exist. Add appointments with addappt, "
            + "change one with editappt, and remove one with deleteappt.";

    private static final Prefix PREFIX_RETIRED_ADD_APPOINTMENT = new Prefix("--add-appt");
    private static final Prefix PREFIX_RETIRED_REMOVE_APPOINTMENT = new Prefix("--remove-appt");

    /**
     * Parses the given {@code String} of arguments in the context of the EditCommand
     * and returns an EditCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public EditCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String invalidFormatMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE);
        Prefix[] prefixes = {PREFIX_NAME, PREFIX_PHONE, PREFIX_ADDRESS, PREFIX_MEDICAL_HISTORY, PREFIX_NOTES};
        String normalizedArgs;
        try {
            normalizedArgs = ArgumentTokenizer.normalizeOptionSeparators(args, invalidFormatMessage, prefixes);
        } catch (ParseException pe) {
            if (hasRetiredAppointmentOption(args)) {
                throw new ParseException(MESSAGE_RETIRED_APPOINTMENT_OPTIONS);
            }
            throw pe;
        }
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(normalizedArgs, prefixes);

        PatientId id;

        try {
            id = ParserUtil.parseId(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(invalidFormatMessage, pe);
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_ADDRESS, PREFIX_MEDICAL_HISTORY,
                PREFIX_NOTES);

        EditPatientDescriptor editPatientDescriptor = new EditPatientDescriptor();

        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            editPatientDescriptor.setName(ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get()));
        }
        if (argMultimap.getValue(PREFIX_PHONE).isPresent()) {
            editPatientDescriptor.setPhone(ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get()));
        }
        if (argMultimap.getValue(PREFIX_ADDRESS).isPresent()) {
            editPatientDescriptor.setAddress(ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS).get()));
        }
        if (argMultimap.getValue(PREFIX_MEDICAL_HISTORY).isPresent()) {
            editPatientDescriptor.setMedicalHistory(
                    ParserUtil.parseMedicalHistory(argMultimap.getValue(PREFIX_MEDICAL_HISTORY).get()));
        }
        if (argMultimap.getValue(PREFIX_NOTES).isPresent()) {
            editPatientDescriptor.setNotes(ParserUtil.parseNotes(argMultimap.getValue(PREFIX_NOTES).get()));
        }

        if (!editPatientDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditCommand.MESSAGE_NOT_EDITED);
        }

        return new EditCommand(id, editPatientDescriptor);
    }

    /**
     * Returns true if {@code args} contains a retired appointment option, which edit no longer accepts.
     */
    private static boolean hasRetiredAppointmentOption(String args) {
        ArgumentMultimap retiredTokens = ArgumentTokenizer.tokenize(args,
                PREFIX_RETIRED_ADD_APPOINTMENT, PREFIX_RETIRED_REMOVE_APPOINTMENT);
        return retiredTokens.getValue(PREFIX_RETIRED_ADD_APPOINTMENT).isPresent()
                || retiredTokens.getValue(PREFIX_RETIRED_REMOVE_APPOINTMENT).isPresent();
    }

}
