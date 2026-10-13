package seedu.address.logic.parser;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.ViewCommand;
import seedu.address.model.patient.PatientId;

/**
 * Completes the patient ID at the end of a command that expects the ID of an existing patient,
 * such as {@code view S12}.
 */
public class IdCompleter {

    /** Commands whose only parameter is the ID of an existing patient. */
    private static final Set<String> COMMANDS_TAKING_ID = Set.of(ViewCommand.COMMAND_WORD, DeleteCommand.COMMAND_WORD);

    /** A command word, optionally followed by whitespace and the start of an ID, with nothing after it. */
    private static final Pattern COMMAND_FORMAT =
            Pattern.compile("(?<commandWord>\\S+)(?:(?<separator>\\s+)(?<partialId>\\S*))?");

    private IdCompleter() {}

    /**
     * Returns each way to complete {@code commandText} with one of {@code ids}, in the order of {@code ids}.
     * Returns an empty list if {@code commandText} does not end where a command expects a patient ID,
     * or if no ID starts with what was typed. Matching ignores case.
     */
    public static List<String> complete(String commandText, List<PatientId> ids) {
        requireAllNonNull(commandText, ids);
        Matcher matcher = COMMAND_FORMAT.matcher(commandText.stripLeading());
        if (!matcher.matches() || !COMMANDS_TAKING_ID.contains(matcher.group("commandWord"))) {
            return List.of();
        }

        String separator = matcher.group("separator") == null ? " " : matcher.group("separator");
        String partialId = matcher.group("partialId") == null
                ? ""
                : matcher.group("partialId").toUpperCase(Locale.ROOT);
        String commandStart = matcher.group("commandWord") + separator;
        return ids.stream()
                .map(id -> id.value)
                .filter(id -> id.startsWith(partialId))
                .map(id -> commandStart + id)
                .toList();
    }
}
