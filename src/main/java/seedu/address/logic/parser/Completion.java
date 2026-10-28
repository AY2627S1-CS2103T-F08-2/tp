package seedu.address.logic.parser;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

/**
 * One way to complete a partly typed command.
 *
 * @param text The whole command after the completion, which replaces the text in the command box.
 * @param word The word that the completion fills in, such as a command word, an option or a patient ID.
 */
public record Completion(String text, String word) {

    /**
     * Creates a {@code Completion}. Both arguments must be non-null.
     */
    public Completion {
        requireAllNonNull(text, word);
    }
}
