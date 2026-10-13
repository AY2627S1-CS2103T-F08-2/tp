package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import seedu.address.commons.util.HelpBookUtil;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * Displays command formats and examples extracted from the bundled User Guide.
 */
public class HelpCommand extends Command {

    public static final String COMMAND_WORD = "help";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Shows program usage instructions.\n"
            + "Parameters: [--COMMAND_NAME]\n"
            + "Example 1: " + COMMAND_WORD + "\n"
            + "Example 2: " + COMMAND_WORD + " --delete";

    public static final String MESSAGE_UNKNOWN_TARGET = "Unknown command '--%1$s'.\n"
            + "Type 'help' to see all available commands.";

    private final Optional<String> targetCommand;

    /**
     * Creates a HelpCommand that displays a summary of all commands.
     */
    public HelpCommand() {
        this.targetCommand = Optional.empty();
    }

    /**
     * Creates a HelpCommand for a specific target command.
     *
     * @param targetCommand The command keyword to show specific help for.
     */
    public HelpCommand(String targetCommand) {
        requireNonNull(targetCommand);
        this.targetCommand = Optional.of(targetCommand);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        // Case 3: 'help' in input -> lists all available commands and their formats
        if (targetCommand.isEmpty()) {
            return new CommandResult(HelpBookUtil.getAllCommandsSummary(), false, false);
        }

        // Case 4: 'help --<command>' -> shows the specific command format + examples
        String command = targetCommand.get().toLowerCase();
        Optional<String> specificHelp = HelpBookUtil.getSpecificCommandHelp(command);

        if (specificHelp.isEmpty()) {
            throw new CommandException(String.format(MESSAGE_UNKNOWN_TARGET, command));
        }

        return new CommandResult(specificHelp.get(), false, false);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof HelpCommand otherHelpCommand)) {
            return false;
        }
        return targetCommand.equals(otherHelpCommand.targetCommand);
    }
}
