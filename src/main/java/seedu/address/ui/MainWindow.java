package seedu.address.ui;

import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * The Main Window. Provides the basic application layout containing
 * a menu bar and space where other JavaFX elements can be placed.
 */
public class MainWindow extends UiPart<Stage> {

    private static final String FXML = "MainWindow.fxml";

    private final Logger logger = LogsCenter.getLogger(getClass());

    private Stage primaryStage;
    private Logic logic;
    private Path dataFilePath;

    // Independent Ui parts residing in this Ui container
    private PatientListPanel patientListPanel;
    private PatientDetailPanel patientDetailPanel;
    private CommandBox commandBox;
    private ResultDisplay resultDisplay;
    private HelpWindow helpWindow;

    @FXML
    private StackPane commandBoxPlaceholder;

    @FXML
    private MenuItem helpMenuItem;

    @FXML
    private StackPane patientListPanelPlaceholder;

    @FXML
    private StackPane patientDetailPanelPlaceholder;

    @FXML
    private StackPane resultDisplayPlaceholder;

    @FXML
    private StackPane statusbarPlaceholder;

    /**
     * Creates a {@code MainWindow} with the given {@code Stage}, {@code Logic},
     * and the data file path to show in the status bar.
     */
    public MainWindow(Stage primaryStage, Logic logic, Path dataFilePath) {
        super(FXML, primaryStage);

        // Set dependencies
        this.primaryStage = primaryStage;
        this.logic = logic;
        this.dataFilePath = dataFilePath;

        // Configure the UI
        setWindowDefaultSize(logic.getGuiSettings());

        setAccelerators();

        helpWindow = new HelpWindow();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private void setAccelerators() {
        setAccelerator(helpMenuItem, KeyCombination.valueOf("F1"));
    }

    /**
     * Sets the accelerator of a MenuItem.
     * @param keyCombination the KeyCombination value of the accelerator
     */
    private void setAccelerator(MenuItem menuItem, KeyCombination keyCombination) {
        menuItem.setAccelerator(keyCombination);

        /*
         * TODO: the code below can be removed once the bug reported here
         * https://bugs.openjdk.java.net/browse/JDK-8131666
         * is fixed in a later version of the SDK.
         *
         * According to the bug report, TextInputControl (TextField, TextArea) will
         * consume function-key events. Because CommandBox contains a TextField and
         * ResultDisplay contains a TextArea, some accelerators (e.g., F1) will
         * not work when the focus is in them because the key event is consumed by
         * the TextInputControl(s).
         *
         * For now, we add the following event filter to capture such key events and open
         * the help window purposely so as to support accelerators even when focus is
         * in CommandBox or ResultDisplay.
         */
        getRoot().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getTarget() instanceof TextInputControl && keyCombination.match(event)) {
                menuItem.getOnAction().handle(new ActionEvent());
                event.consume();
            }
        });
    }

    /**
     * Fills up all the placeholders of this window.
     */
    void fillInnerParts() {
        patientDetailPanel = new PatientDetailPanel(logic.getPatientList());
        patientDetailPanelPlaceholder.getChildren().add(patientDetailPanel.getRoot());

        patientListPanel = new PatientListPanel(logic.getFilteredPatientList(), patientDetailPanel::show);
        patientListPanelPlaceholder.getChildren().add(patientListPanel.getRoot());

        resultDisplay = new ResultDisplay();
        resultDisplayPlaceholder.getChildren().add(resultDisplay.getRoot());

        StatusBarFooter statusBarFooter = new StatusBarFooter(dataFilePath);
        statusbarPlaceholder.getChildren().add(statusBarFooter.getRoot());

        commandBox = new CommandBox(this::executeCommand, logic::getCompletions, () -> moveFocus(true));
        commandBoxPlaceholder.getChildren().add(commandBox.getRoot());

        // The window sees these keys before the focused part does, so it can move the focus away from it.
        getRoot().addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPressed);
        getRoot().addEventFilter(KeyEvent.KEY_TYPED, this::handleKeyTyped);
    }

    /**
     * Handles the keys that move the focus or show all patients. In the command box, only Shift+Tab is handled
     * here, because the command box uses the other keys itself.
     */
    private void handleKeyPressed(KeyEvent event) {
        boolean isShortcutDown = event.isControlDown() || event.isAltDown() || event.isMetaDown();
        Optional<BrowseKeys.Action> action = BrowseKeys.actionFor(event.getCode(), event.isShiftDown(),
                isShortcutDown);
        if (action.isEmpty()
                || commandBox.isFocused() && action.get() != BrowseKeys.Action.FOCUS_PREVIOUS_AREA) {
            return;
        }

        switch (action.get()) {
            case RETURN_TO_COMMAND_BOX -> commandBox.requestFocus();
            case SHOW_ALL_PATIENTS -> showAllPatients();
            case FOCUS_NEXT_AREA -> moveFocus(true);
            case FOCUS_PREVIOUS_AREA -> moveFocus(false);
            default -> throw new AssertionError("Unknown action: " + action.get());
        }
        event.consume();
    }

    /**
     * Moves the focus to the command box when the user types text outside it, and adds the text there.
     */
    private void handleKeyTyped(KeyEvent event) {
        boolean isShortcutDown = event.isControlDown() || event.isAltDown() || event.isMetaDown();
        if (commandBox.isFocused() || !BrowseKeys.isCommandText(event.getCharacter(), isShortcutDown)) {
            return;
        }
        commandBox.requestFocusAndType(event.getCharacter());
        event.consume();
    }

    /**
     * Shows all patients, as the {@code list} command does.
     */
    private void showAllPatients() {
        try {
            executeCommand(ListCommand.COMMAND_WORD);
        } catch (CommandException | ParseException e) {
            // executeCommand has already shown the error to the user
            logger.warning("Showing all patients failed: " + e.getMessage());
        }
    }

    /**
     * Moves the focus to the next area that can take it, or to the previous one if {@code isForward} is false.
     * An empty patient list, and the details when no patient is shown, are skipped.
     */
    private void moveFocus(boolean isForward) {
        // The menu bar and the result display are not areas, so going forward from them starts at the command box
        FocusArea area = getFocusedArea().orElse(FocusArea.PATIENT_DETAILS);
        for (int i = 0; i < FocusArea.values().length; i++) {
            area = isForward ? area.next() : area.previous();
            if (requestFocus(area)) {
                return;
            }
        }
    }

    private Optional<FocusArea> getFocusedArea() {
        if (commandBox.isFocused()) {
            return Optional.of(FocusArea.COMMAND_BOX);
        } else if (patientListPanel.isFocused()) {
            return Optional.of(FocusArea.PATIENT_LIST);
        } else if (patientDetailPanel.isFocused()) {
            return Optional.of(FocusArea.PATIENT_DETAILS);
        }
        return Optional.empty();
    }

    /**
     * Moves the focus to {@code area}, and returns true if it took the focus.
     */
    private boolean requestFocus(FocusArea area) {
        return switch (area) {
            case COMMAND_BOX -> {
                commandBox.requestFocus();
                yield true;
            }
            case PATIENT_LIST -> requestFocusOnPatientList();
            case PATIENT_DETAILS -> patientDetailPanel.requestFocus();
        };
    }

    /**
     * Moves the focus to the patient list. If no patient in the list is selected, selects the first one and
     * shows their details, so that the selected patient is always the one shown.
     */
    private boolean requestFocusOnPatientList() {
        if (!patientListPanel.requestFocus()) {
            return false;
        }
        patientListPanel.selectFirstIfNoneSelected().ifPresent(patientDetailPanel::show);
        return true;
    }

    /**
     * Sets the default size based on {@code guiSettings}.
     */
    private void setWindowDefaultSize(GuiSettings guiSettings) {
        primaryStage.setHeight(guiSettings.getWindowHeight());
        primaryStage.setWidth(guiSettings.getWindowWidth());
        if (guiSettings.getWindowCoordinates() != null) {
            primaryStage.setX(guiSettings.getWindowCoordinates().getX());
            primaryStage.setY(guiSettings.getWindowCoordinates().getY());
        }
    }

    /**
     * Opens the help window or focuses on it if it's already opened.
     */
    @FXML
    public void handleHelp() {
        if (!helpWindow.isShowing()) {
            helpWindow.show();
        } else {
            helpWindow.focus();
        }
    }

    void show() {
        primaryStage.show();
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        GuiSettings guiSettings = new GuiSettings(primaryStage.getWidth(), primaryStage.getHeight(),
                (int) primaryStage.getX(), (int) primaryStage.getY());
        logic.setGuiSettings(guiSettings);
        helpWindow.hide();
        primaryStage.hide();
    }

    public PatientListPanel getPatientListPanel() {
        return patientListPanel;
    }

    /**
     * Executes the command and returns the result.
     *
     * @see seedu.address.logic.Logic#execute(String)
     */
    private CommandResult executeCommand(String commandText) throws CommandException, ParseException {
        try {
            CommandResult commandResult = logic.execute(commandText);
            logger.info("Result: " + commandResult.getFeedbackToUser());
            resultDisplay.setFeedbackToUser(commandResult.getFeedbackToUser());

            commandResult.getPatientToView().ifPresent(patientDetailPanel::show);
            // Removing or filtering rows can move the list's selection to another patient, so match it to the panel.
            patientDetailPanel.getShownId().ifPresentOrElse(patientListPanel::select, patientListPanel::clearSelection);

            if (commandResult.isShowHelp()) {
                handleHelp();
            }

            if (commandResult.isExit()) {
                handleExit();
            }

            return commandResult;
        } catch (CommandException | ParseException e) {
            logger.info("An error occurred while executing command: " + commandText);
            resultDisplay.setFeedbackToUser(e.getMessage());
            throw e;
        }
    }
}
