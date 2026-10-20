package seedu.address.logic.parser;

import java.util.Locale;

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

        // Reject multi-token input like 'help add patient'
        if (trimmed.contains(" ") || trimmed.contains("\t")) {
            throw new ParseException(HelpCommand.MESSAGE_INVALID_HELP_ARGUMENT);
        }

        String commandTarget = trimmed.toLowerCase(Locale.ROOT);

        // Only valid command words (and 'window') are accepted; flags ('help --add') and
        // unknown targets ('help 3') are rejected.
        if (!HelpCommand.COMMAND_USAGE_MAP.containsKey(commandTarget)
                && !commandTarget.equals(HelpCommand.TARGET_WINDOW)) {
            throw new ParseException(HelpCommand.MESSAGE_INVALID_HELP_ARGUMENT);
        }

        return new HelpCommand(commandTarget);
    }
}
