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
        String trimmedArgs = args.trim();

        // Bare "help"
        if (trimmedArgs.isEmpty()) {
            return new HelpCommand();
        }

        // Targeted "help --<command>"
        if (trimmedArgs.startsWith("--")) {
            String target = trimmedArgs.substring(2).trim();
            if (!target.isEmpty() && !target.contains(" ")) {
                return new HelpCommand(target);
            }
        }

        // Any invalid format like "help foo" or "help --"
        throw new ParseException(
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
    }
}
