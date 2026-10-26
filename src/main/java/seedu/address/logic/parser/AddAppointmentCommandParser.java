package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
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
     * Parses a patient ID followed by an appointment date, optional time and optional details.
     *
     * @throws ParseException if the user input does not conform to the expected format.
     */
    public AddAppointmentCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String invalidFormatMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                AddAppointmentCommand.MESSAGE_USAGE);
        String normalizedArgs = ArgumentTokenizer.normalizeOptionSeparators(args, invalidFormatMessage,
                PREFIX_DETAILS);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(normalizedArgs, PREFIX_DETAILS);
        String[] preamble = argMultimap.getPreamble().split("\\s+", 2);
        if (preamble.length != 2) {
            logger.fine("Rejected addappt: a patient ID and appointment date are required.");
            throw new ParseException(invalidFormatMessage);
        }

        PatientId id;
        try {
            id = ParserUtil.parseId(preamble[0]);
        } catch (ParseException pe) {
            throw new ParseException(invalidFormatMessage, pe);
        }
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_DETAILS);

        Appointment appointment = ParserUtil.parseAppointment(preamble[1]);
        Optional<AppointmentDetails> details = argMultimap.getValue(PREFIX_DETAILS).isPresent()
                ? Optional.of(ParserUtil.parseAppointmentDetails(argMultimap.getValue(PREFIX_DETAILS).get()))
                : Optional.empty();
        Appointment toAdd = new Appointment(appointment.toString(), details);

        logger.fine("Parsed positional appointment date and optional details.");
        return new AddAppointmentCommand(id, toAdd);
    }
}
