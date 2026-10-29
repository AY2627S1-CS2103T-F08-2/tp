package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ALL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ARCHIVED;

import java.util.Optional;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.ViewCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.PatientScope;
import seedu.address.model.patient.PatientId;

/**
 * Parses input arguments and creates a new ViewCommand object, or a ListCommand if no ID is given.
 */
public class ViewCommandParser implements Parser<Command> {

    /**
     * Parses the given {@code String} of arguments in the context of the ViewCommand and returns a ViewCommand
     * object for execution. Without an ID, it returns a ListCommand that shows the patients in the scope given by
     * {@code --all} or {@code --archived}, or else the default scope.
     *
     * @throws ParseException if the user input does not conform to the expected format.
     */
    public Command parse(String args) throws ParseException {
        String invalidFormatMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewCommand.MESSAGE_USAGE);
        String normalizedArgs = ArgumentTokenizer.normalizeOptionSeparators(args, invalidFormatMessage,
                PREFIX_ALL, PREFIX_ARCHIVED);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(normalizedArgs, PREFIX_ALL, PREFIX_ARCHIVED);
        Optional<PatientScope> scope = ParserUtil.parseScope(argMultimap, invalidFormatMessage);
        String preamble = argMultimap.getPreamble();
        if (preamble.isEmpty()) {
            return new ListCommand(scope.orElse(PatientScope.DEFAULT));
        }
        if (scope.isPresent()) {
            throw new ParseException(Messages.MESSAGE_SCOPE_WITH_ID);
        }
        try {
            PatientId id = ParserUtil.parseId(preamble);
            return new ViewCommand(id);
        } catch (ParseException pe) {
            throw new ParseException(invalidFormatMessage, pe);
        }
    }

}
