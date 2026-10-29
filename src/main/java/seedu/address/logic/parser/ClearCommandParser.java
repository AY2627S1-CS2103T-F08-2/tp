package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ARCHIVED;

import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ClearCommand object, once selective clearing is released (#217).
 */
public class ClearCommandParser implements Parser<ClearCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the ClearCommand and returns a ClearCommand
     * object for execution: one that deletes every patient if there are no arguments, and only archived patients
     * with {@code --archived}.
     *
     * @throws ParseException if the user input does not conform to the expected format.
     */
    public ClearCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String invalidFormatMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ClearCommand.MESSAGE_USAGE);
        String normalizedArgs = ArgumentTokenizer.normalizeOptionSeparators(args, invalidFormatMessage,
                PREFIX_ARCHIVED);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(normalizedArgs, PREFIX_ARCHIVED);
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_ARCHIVED);
        boolean isArchivedOnly = argMultimap.getValue(PREFIX_ARCHIVED).isPresent();
        if (!argMultimap.getPreamble().isEmpty()
                || argMultimap.getValue(PREFIX_ARCHIVED).filter(value -> !value.isEmpty()).isPresent()) {
            throw new ParseException(invalidFormatMessage);
        }
        return new ClearCommand(isArchivedOnly);
    }
}
