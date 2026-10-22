package seedu.address.logic.parser;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.commands.AddAppointmentCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditAppointmentCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.ViewCommand;
import seedu.address.model.patient.PatientId;

/**
 * Completes the patient ID in a command that expects the ID of an existing patient, such as {@code view S12},
 * or {@code edit S12 --phone 91234567}.
 */
public class IdCompleter {

    /** Commands whose only parameter is the ID of an existing patient. */
    private static final Set<String> COMMANDS_TAKING_ONLY_ID =
            Set.of(ViewCommand.COMMAND_WORD, DeleteCommand.COMMAND_WORD);

    /** Commands whose first parameter is the ID of an existing patient, and which take options after it. */
    private static final Set<String> COMMANDS_TAKING_ID_FIRST =
            Set.of(EditCommand.COMMAND_WORD, EditAppointmentCommand.COMMAND_WORD,
                    AddAppointmentCommand.COMMAND_WORD);

    /**
     * A command word, optionally followed by whitespace and the start of an ID, and then optionally by the rest of
     * the command, which starts with whitespace.
     */
    private static final Pattern COMMAND_FORMAT = Pattern.compile(
            "(?<commandWord>\\S+)(?:(?<separator>\\s+)(?<partialId>\\S*)(?<rest>\\s.*)?)?", Pattern.DOTALL);

    private IdCompleter() {}

    /**
     * Returns each way to complete {@code commandText} with one of {@code ids}, in the order of {@code ids}.
     * The text after the ID, such as the options of {@code edit}, is kept unchanged.
     * Returns an empty list if {@code commandText} has no patient ID where its command expects one,
     * or if no ID starts with what was typed. Matching ignores case.
     */
    public static List<String> complete(String commandText, List<PatientId> ids) {
        requireAllNonNull(commandText, ids);
        Matcher matcher = COMMAND_FORMAT.matcher(commandText.stripLeading());
        if (!matcher.matches()) {
            return List.of();
        }

        String commandWord = matcher.group("commandWord");
        String rest = matcher.group("rest") == null ? "" : matcher.group("rest");
        boolean isIdLast = COMMANDS_TAKING_ONLY_ID.contains(commandWord) && rest.isEmpty();
        if (!isIdLast && !COMMANDS_TAKING_ID_FIRST.contains(commandWord)) {
            return List.of();
        }

        String separator = matcher.group("separator") == null ? " " : matcher.group("separator");
        String partialId = matcher.group("partialId") == null
                ? ""
                : matcher.group("partialId").toUpperCase(Locale.ROOT);
        String commandStart = commandWord + separator;
        return ids.stream()
                .map(id -> id.value)
                .filter(id -> id.startsWith(partialId))
                .map(id -> commandStart + id + rest)
                .toList();
    }
}
