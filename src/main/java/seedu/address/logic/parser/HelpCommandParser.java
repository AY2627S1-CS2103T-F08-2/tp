package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Locale;

import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new HelpCommand object.
 */
public class HelpCommandParser implements Parser<HelpCommand> {

    private static final String TARGET_FLAG_MARKER = "--";

    @Override
    public HelpCommand parse(String args) throws ParseException {
        String trimmed = args.strip();

        if (trimmed.isEmpty()) {
            return new HelpCommand();
        }

        String commandTarget;
        if (trimmed.startsWith(TARGET_FLAG_MARKER)) {
            // Reject '-- add' where a space follows '--'
            if (trimmed.length() > TARGET_FLAG_MARKER.length()
                    && Character.isWhitespace(trimmed.charAt(TARGET_FLAG_MARKER.length()))) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
            }
            commandTarget = trimmed.substring(TARGET_FLAG_MARKER.length()).strip();
        } else {
            commandTarget = trimmed;
        }

        commandTarget = commandTarget.toLowerCase(Locale.ROOT);

        // Reject multi-token input like 'help add patient' or empty flag like 'help --'
        if (commandTarget.isEmpty() || commandTarget.contains(" ") || commandTarget.contains("\t")) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        return new HelpCommand(commandTarget);
    }
}
