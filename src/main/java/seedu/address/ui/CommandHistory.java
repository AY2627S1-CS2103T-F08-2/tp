package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Keeps the commands that the user entered since the app started, and a position for going back and forth
 * through them with the Up and Down keys.
 */
public class CommandHistory {

    private final List<String> commands = new ArrayList<>();

    /** The position in {@code commands}, where {@code commands.size()} is the line that the user is typing. */
    private int position;

    /** The text that the user was typing before going back through the history. */
    private String draft = "";

    /**
     * Adds {@code command} as the newest command, unless it is the same as the newest command,
     * and goes back to the line that the user is typing.
     */
    public void add(String command) {
        requireNonNull(command);
        if (commands.isEmpty() || !commands.get(commands.size() - 1).equals(command)) {
            commands.add(command);
        }
        resetPosition();
    }

    /**
     * Moves to the command before the current position and returns it, or returns {@code Optional#empty()}
     * if the position is already at the oldest command. When moving away from the line that the user is
     * typing, keeps {@code currentText} so that {@link #next()} can give it back.
     */
    public Optional<String> previous(String currentText) {
        requireNonNull(currentText);
        if (position == 0) {
            return Optional.empty();
        }
        if (position == commands.size()) {
            draft = currentText;
        }
        position--;
        return Optional.of(commands.get(position));
    }

    /**
     * Moves to the command after the current position and returns it, or the text that the user was typing
     * after the newest command. Returns {@code Optional#empty()} if the position is already at the line that
     * the user is typing.
     */
    public Optional<String> next() {
        if (position == commands.size()) {
            return Optional.empty();
        }
        position++;
        return Optional.of(position == commands.size() ? draft : commands.get(position));
    }

    /**
     * Goes back to the line that the user is typing, and forgets the text that they were typing before.
     */
    public void resetPosition() {
        position = commands.size();
        draft = "";
    }
}
