package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.RedeemVoucherCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.patient.PatientId;

/**
 * Parses input arguments and creates a new RedeemVoucherCommand object.
 */
public class RedeemVoucherCommandParser implements Parser<RedeemVoucherCommand> {

    /**
     * Parses exactly a patient ID followed by a positive number of vouchers.
     *
     * @throws ParseException if the user input does not conform to the expected format.
     */
    public RedeemVoucherCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String invalidFormatMessage =
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, RedeemVoucherCommand.MESSAGE_USAGE);
        String[] preamble = args.trim().split("\\s+");
        if (preamble.length != 2) {
            throw new ParseException(invalidFormatMessage);
        }
        PatientId id;
        try {
            id = ParserUtil.parseId(preamble[0]);
        } catch (ParseException pe) {
            throw new ParseException(invalidFormatMessage, pe);
        }
        return new RedeemVoucherCommand(id, ParserUtil.parseVoucherNumber(preamble[1]));
    }

}
