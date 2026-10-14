package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new HelpCommand object.
 */
public class HelpCommandParser implements Parser<HelpCommand> {

    @Override
    public HelpCommand parse(String args) throws ParseException {
        String trimmed = args.strip();

        if (trimmed.isEmpty()) {
            return new HelpCommand();
        }

        String commandTarget;
        if (trimmed.startsWith("--")) {
            // Reject '-- add' where a space follows '--'
            if (trimmed.length() > 2 && Character.isWhitespace(trimmed.charAt(2))) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
            }
            commandTarget = trimmed.substring(2).strip();
        } else {
            commandTarget = trimmed;
        }

        commandTarget = commandTarget.toLowerCase();

        // Reject multi-token input like 'help add patient' or empty flag like 'help --'
        if (commandTarget.isEmpty() || commandTarget.contains(" ") || commandTarget.contains("\t")) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        return new HelpCommand(commandTarget);
    }
}
