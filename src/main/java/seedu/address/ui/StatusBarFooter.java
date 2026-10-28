package seedu.address.ui;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

/**
 * A UI for the status bar that is displayed at the footer of the application.
 * It shows where the data is saved and, while the user types a command, what Tab can complete.
 */
public class StatusBarFooter extends UiPart<Region> {

    /** Goes between the words that Tab can complete. */
    static final String COMPLETION_SEPARATOR = " · ";
    /** The most words that the hint shows. A count of the others follows them. */
    static final int MAX_COMPLETIONS_SHOWN = 5;

    private static final String FXML = "StatusBarFooter.fxml";

    @FXML
    private HBox completionHint;
    @FXML
    private Label completionList;
    @FXML
    private Label saveLocationStatus;

    /**
     * Creates a {@code StatusBarFooter} with the given {@code Path}.
     */
    public StatusBarFooter(Path saveLocation) {
        super(FXML);
        saveLocationStatus.setText(Paths.get(".").resolve(saveLocation).toString());
    }

    /**
     * Shows that Tab can complete the command with each of {@code words}, in order, or hides the hint if
     * {@code words} is empty. Only the first {@value #MAX_COMPLETIONS_SHOWN} words show, followed by how many
     * others there are, such as {@code +19 more}.
     */
    public void showCompletions(List<String> words) {
        completionList.setText(formatCompletions(words));
        completionHint.setVisible(!words.isEmpty());
    }

    /**
     * Returns the first {@value #MAX_COMPLETIONS_SHOWN} of {@code words}, with the number of the others after them.
     */
    static String formatCompletions(List<String> words) {
        if (words.size() <= MAX_COMPLETIONS_SHOWN) {
            return String.join(COMPLETION_SEPARATOR, words);
        }
        return String.join(COMPLETION_SEPARATOR, words.subList(0, MAX_COMPLETIONS_SHOWN))
                + COMPLETION_SEPARATOR + "+" + (words.size() - MAX_COMPLETIONS_SHOWN) + " more";
    }

    /**
     * Returns the words of the completion hint as shown, or an empty string if the hint is hidden.
     */
    String getCompletionHint() {
        return completionHint.isVisible() ? completionList.getText() : "";
    }
}
