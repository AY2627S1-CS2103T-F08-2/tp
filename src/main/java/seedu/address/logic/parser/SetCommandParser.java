package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Optional;

import seedu.address.logic.commands.SetCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Settings.Key;

/**
 * Parses input arguments and creates a new SetCommand object.
 */
public class SetCommandParser implements Parser<SetCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the SetCommand and returns a SetCommand object
     * for execution: one that shows the settings if there are no arguments, and otherwise one that turns a setting on
     * or off. The words are lowercase, as command words are.
     *
     * @throws ParseException if the user input does not conform to the expected format.
     */
    public SetCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            return new SetCommand();
        }
        String invalidFormatMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, SetCommand.MESSAGE_USAGE);
        String[] words = trimmedArgs.split("\\s+");
        if (words.length != 2) {
            throw new ParseException(invalidFormatMessage);
        }
        Optional<Key> key = Key.of(words[0]);
        if (key.isEmpty()) {
            throw new ParseException(invalidFormatMessage);
        }
        return switch (words[1]) {
            case SetCommand.WORD_ON -> new SetCommand(key.get(), true);
            case SetCommand.WORD_OFF -> new SetCommand(key.get(), false);
            default -> throw new ParseException(invalidFormatMessage);
        };
    }
}
