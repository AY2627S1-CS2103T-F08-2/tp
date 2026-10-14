package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * Displays help information or opens the full User Guide window.
 */
public class HelpCommand extends Command {

    public static final String COMMAND_WORD = "help";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Displays help and command instructions.\n"
            + "Parameters: [COMMAND]\n"
            + "Example: " + COMMAND_WORD + " add (or " + COMMAND_WORD + " --add)\n"
            + "To open the full User Guide window: " + COMMAND_WORD + " window (or " + COMMAND_WORD + " --window)";

    public static final String SHOWING_HELP_SUMMARY = "Commands available:\n"
            + "• add: Adds a patient to the records\n"
            + "• clear: Clears all patient records\n"
            + "• delete: Deletes a patient by ID\n"
            + "• edit: Edits an existing patient's details\n"
            + "• exit: Exits the program\n"
            + "• find: Finds patients matching keywords\n"
            + "• help: Shows program help or usage for a specific command\n"
            + "• list: Lists all patients\n"
            + "• view: Views a patient's full details\n"
            + "• viewappt: Views appointments on a date or date range\n\n"
            + "Type help [COMMAND] (e.g., help add) for detailed parameters, "
            + "help window to open the guide window, or press F1.";

    public static final String MESSAGE_UNKNOWN_HELP_TARGET = "Unknown help command: '%1$s'.\n"
            + "Available commands: add, clear, delete, edit, exit, find, help, list, view, viewappt, window\n"
            + "Format: " + COMMAND_WORD + " [COMMAND]";

    public static final String SHOWING_HELP_MESSAGE = "Opened User Guide window.";

    public static final String WINDOW_FLAG = "window";

    public static final String CLEAR_MESSAGE_USAGE = ClearCommand.COMMAND_WORD
            + ": Clears all patient records from LaiNurse.\n"
            + "Format: " + ClearCommand.COMMAND_WORD;

    public static final String EXIT_MESSAGE_USAGE = ExitCommand.COMMAND_WORD
            + ": Exits the program.\n"
            + "Format: " + ExitCommand.COMMAND_WORD;

    public static final String LIST_MESSAGE_USAGE = ListCommand.COMMAND_WORD
            + ": Lists all patients with their ID, name, and upcoming appointments.\n"
            + "Format: " + ListCommand.COMMAND_WORD;

    private static final Map<String, String> COMMAND_USAGE_MAP;

    static {
        Map<String, String> map = new LinkedHashMap<>();
        map.put(AddCommand.COMMAND_WORD, AddCommand.MESSAGE_USAGE);
        map.put(ClearCommand.COMMAND_WORD, CLEAR_MESSAGE_USAGE);
        map.put(DeleteCommand.COMMAND_WORD, DeleteCommand.MESSAGE_USAGE);
        map.put(EditCommand.COMMAND_WORD, EditCommand.MESSAGE_USAGE);
        map.put(ExitCommand.COMMAND_WORD, EXIT_MESSAGE_USAGE);
        map.put(FindCommand.COMMAND_WORD, FindCommand.MESSAGE_USAGE);
        map.put(HelpCommand.COMMAND_WORD, HelpCommand.MESSAGE_USAGE);
        map.put(ListCommand.COMMAND_WORD, LIST_MESSAGE_USAGE);
        map.put(ViewCommand.COMMAND_WORD, ViewCommand.MESSAGE_USAGE);
        map.put(ViewAppointmentCommand.COMMAND_WORD, ViewAppointmentCommand.MESSAGE_USAGE);
        COMMAND_USAGE_MAP = Collections.unmodifiableMap(map);
    }

    private final String targetCommand;
    private final boolean isWindowRequest;

    /**
     * Creates a HelpCommand that displays the overview summary.
     */
    public HelpCommand() {
        this.targetCommand = null;
        this.isWindowRequest = false;
    }

    /**
     * Creates a HelpCommand for a specific target command or the window trigger.
     */
    public HelpCommand(String targetCommand) {
        requireNonNull(targetCommand);
        this.targetCommand = targetCommand;
        this.isWindowRequest = targetCommand.equals(WINDOW_FLAG);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        if (isWindowRequest) {
            return new CommandResult(SHOWING_HELP_MESSAGE, true, false);
        }

        if (targetCommand == null) {
            return new CommandResult(SHOWING_HELP_SUMMARY);
        }

        String usage = COMMAND_USAGE_MAP.get(targetCommand);
        if (usage != null) {
            return new CommandResult(usage);
        }

        throw new CommandException(String.format(MESSAGE_UNKNOWN_HELP_TARGET, targetCommand));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof HelpCommand)) {
            return false;
        }
        HelpCommand otherHelpCommand = (HelpCommand) other;
        if (targetCommand == null) {
            return otherHelpCommand.targetCommand == null;
        }
        return targetCommand.equals(otherHelpCommand.targetCommand);
    }
}
