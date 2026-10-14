package seedu.address.ui;

import java.util.List;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * The UI component that is responsible for receiving user command inputs.
 */
public class CommandBox extends UiPart<Region> {

    public static final String ERROR_STYLE_CLASS = "error";
    private static final String FXML = "CommandBox.fxml";

    private final CommandExecutor commandExecutor;
    private final CommandCompleter commandCompleter;
    private final Runnable escapeHandler;

    /** The completions that pressing Tab goes through, for the text that the user last typed. */
    private List<String> completions = List.of();
    private int completionIndex;
    /** The text that the last Tab put in the command box, or null if the user has typed since. */
    private String lastCompletion;

    private final CommandHistory history = new CommandHistory();

    @FXML
    private TextField commandTextField;

    /**
     * Creates a {@code CommandBox} with the given {@code CommandExecutor} and {@code CommandCompleter}.
     * {@code escapeHandler} runs when the user presses Esc, which leaves the text in the command box unchanged.
     */
    public CommandBox(CommandExecutor commandExecutor, CommandCompleter commandCompleter, Runnable escapeHandler) {
        super(FXML);
        this.commandExecutor = commandExecutor;
        this.commandCompleter = commandCompleter;
        this.escapeHandler = escapeHandler;
        // calls #setStyleToDefault() whenever there is a change to the text of the command box.
        commandTextField.textProperty().addListener((unused1, unused2, unused3) -> setStyleToDefault());
        // A filter sees these keys before the text field uses them, for example to move the focus away.
        commandTextField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.isShiftDown() || event.isControlDown() || event.isAltDown() || event.isMetaDown()) {
                return;
            }
            switch (event.getCode()) {
                case TAB -> handleTabPressed();
                case ESCAPE -> escapeHandler.run();
                case UP -> history.previous(commandTextField.getText()).ifPresent(this::replaceText);
                case DOWN -> history.next().ifPresent(this::replaceText);
                default -> {
                    return;
                }
            }
            event.consume();
        });
    }

    /**
     * Handles the Enter button pressed event.
     */
    @FXML
    private void handleCommandEntered() {
        String commandText = commandTextField.getText();
        if (commandText.equals("")) {
            return;
        }

        history.add(commandText);
        try {
            commandExecutor.execute(commandText);
            commandTextField.setText("");
        } catch (CommandException | ParseException e) {
            setStyleToIndicateCommandFailure();
        }
    }

    /**
     * Replaces the text with its next completion. The first Tab after typing gives the first completion,
     * and each further Tab gives the next one, going back to the first after the last.
     */
    private void handleTabPressed() {
        String commandText = commandTextField.getText();
        if (!commandText.equals(lastCompletion)) {
            completions = commandCompleter.complete(commandText);
            completionIndex = -1;
        }
        if (completions.isEmpty()) {
            return;
        }

        completionIndex = (completionIndex + 1) % completions.size();
        lastCompletion = completions.get(completionIndex);
        replaceText(lastCompletion);
    }

    /**
     * Moves the keyboard focus to the command box, with the caret after the text and nothing selected.
     */
    public void requestFocus() {
        commandTextField.requestFocus();
        commandTextField.positionCaret(commandTextField.getLength());
    }

    /**
     * Moves the keyboard focus to the command box and adds {@code text} after the text that is already there,
     * as if the user had typed it in the command box.
     */
    public void requestFocusAndType(String text) {
        requestFocus();
        commandTextField.appendText(text);
        commandTextField.positionCaret(commandTextField.getLength());
    }

    /**
     * Returns true if the command box has the keyboard focus.
     */
    public boolean isFocused() {
        return commandTextField.isFocused();
    }

    /**
     * Replaces the text in the command box with {@code text}, and puts the caret at the end.
     */
    private void replaceText(String text) {
        commandTextField.setText(text);
        commandTextField.positionCaret(text.length());
    }

    /**
     * Sets the command box style to use the default style.
     */
    private void setStyleToDefault() {
        commandTextField.getStyleClass().remove(ERROR_STYLE_CLASS);
    }

    /**
     * Sets the command box style to indicate a failed command.
     */
    private void setStyleToIndicateCommandFailure() {
        ObservableList<String> styleClass = commandTextField.getStyleClass();

        if (styleClass.contains(ERROR_STYLE_CLASS)) {
            return;
        }

        styleClass.add(ERROR_STYLE_CLASS);
    }

    /**
     * Represents a function that can execute commands.
     */
    @FunctionalInterface
    public interface CommandExecutor {
        /**
         * Executes the command and returns the result.
         *
         * @see seedu.address.logic.Logic#execute(String)
         */
        CommandResult execute(String commandText) throws CommandException, ParseException;
    }

    /**
     * Represents a function that lists the ways to complete a partly typed command.
     */
    @FunctionalInterface
    public interface CommandCompleter {
        /**
         * Returns each way to complete {@code commandText}, or an empty list if there is none.
         *
         * @see seedu.address.logic.Logic#getCompletions(String)
         */
        List<String> complete(String commandText);
    }

}
