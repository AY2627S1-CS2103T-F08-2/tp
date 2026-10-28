package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DETAILS;

import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.AddAppointmentCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.AppointmentDetails;
import seedu.address.model.patient.PatientId;

/**
 * Parses input arguments and creates a new AddAppointmentCommand object.
 */
public class AddAppointmentCommandParser implements Parser<AddAppointmentCommand> {

    private static final Logger logger = LogsCenter.getLogger(AddAppointmentCommandParser.class);

    /**
     * Parses a patient ID followed by the appointment's date, with an optional time, given with {@code --date},
     * and optional details given with {@code --details}.
     *
     * @throws ParseException if the user input does not conform to the expected format.
     */
    public AddAppointmentCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String invalidFormatMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                AddAppointmentCommand.MESSAGE_USAGE);
        String normalizedArgs = ArgumentTokenizer.normalizeOptionSeparators(args, invalidFormatMessage,
                PREFIX_DATE, PREFIX_DETAILS);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(normalizedArgs, PREFIX_DATE, PREFIX_DETAILS);
        String preamble = argMultimap.getPreamble();
        if (preamble.isEmpty() || preamble.split("\\s+").length != 1) {
            logger.fine("Rejected addappt: exactly one patient ID must come before the options.");
            throw new ParseException(invalidFormatMessage);
        }

        PatientId id;
        try {
            id = ParserUtil.parseId(preamble);
        } catch (ParseException pe) {
            throw new ParseException(invalidFormatMessage, pe);
        }
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_DATE, PREFIX_DETAILS);
        if (argMultimap.getValue(PREFIX_DATE).isEmpty()) {
            logger.fine("Rejected addappt: the appointment date is missing.");
            throw new ParseException(AddAppointmentCommand.MESSAGE_MISSING_DATE);
        }

        Appointment appointment = ParserUtil.parseAppointment(argMultimap.getValue(PREFIX_DATE).get());
        Optional<AppointmentDetails> details = argMultimap.getValue(PREFIX_DETAILS).isPresent()
                ? Optional.of(ParserUtil.parseAppointmentDetails(argMultimap.getValue(PREFIX_DETAILS).get()))
                : Optional.empty();
        Appointment toAdd = new Appointment(appointment.toString(), details);

        logger.fine("Parsed appointment date and optional details.");
        return new AddAppointmentCommand(id, toAdd);
    }
}
