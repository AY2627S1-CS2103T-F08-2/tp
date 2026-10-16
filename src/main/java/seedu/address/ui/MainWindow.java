package seedu.address.ui;

import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.collections.ListChangeListener;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.CommandResult.View;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.patient.PatientAppointment;

/**
 * The Main Window. Provides the basic application layout containing
 * a menu bar and space where other JavaFX elements can be placed.
 * It shows one view at a time, patients or appointments, on two tabs.
 */
public class MainWindow extends UiPart<Stage> {

    private static final String FXML = "MainWindow.fxml";
    private static final PseudoClass SELECTED = PseudoClass.getPseudoClass("selected");
    private static final String PATIENT_PLACEHOLDER =
            "To see a patient's details here, type view and their ID, for example: view S1234567A";
    private static final String APPOINTMENT_PLACEHOLDER =
            "To see a patient's details here, select one of their appointments.";
    private static final String PATIENT_PROMPT = "Type a command, or press Esc to browse patients";
    private static final String APPOINTMENT_PROMPT = "Type a command, or press Esc to browse appointments";

    private final Logger logger = LogsCenter.getLogger(getClass());

    private Stage primaryStage;
    private Logic logic;
    private Path dataFilePath;

    /** The view that is shown. */
    private View shownView = View.PATIENTS;

    // Independent Ui parts residing in this Ui container
    private PatientListPanel patientListPanel;
    private PatientDetailPanel patientDetailPanel;
    private AppointmentListPanel appointmentListPanel;
    private PatientDetailPanel appointmentDetailPanel;
    private CommandBox commandBox;
    private ResultDisplay resultDisplay;
    private HelpWindow helpWindow;

    @FXML
    private StackPane commandBoxPlaceholder;

    @FXML
    private MenuItem helpMenuItem;

    @FXML
    private HBox viewTabs;

    @FXML
    private Button patientsTab;

    @FXML
    private Button appointmentsTab;

    @FXML
    private HBox tabKeysHint;

    @FXML
    private Label viewSummary;

    @FXML
    private HBox patientView;

    @FXML
    private HBox appointmentView;

    @FXML
    private StackPane patientListPanelPlaceholder;

    @FXML
    private StackPane patientDetailPanelPlaceholder;

    @FXML
    private StackPane appointmentListPanelPlaceholder;

    @FXML
    private StackPane appointmentDetailPanelPlaceholder;

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
        patientDetailPanel = new PatientDetailPanel(logic.getPatientList(), PATIENT_PLACEHOLDER);
        patientDetailPanelPlaceholder.getChildren().add(patientDetailPanel.getRoot());

        patientListPanel = new PatientListPanel(logic.getFilteredPatientList(), patientDetailPanel::show);
        patientListPanelPlaceholder.getChildren().add(patientListPanel.getRoot());

        appointmentDetailPanel = new PatientDetailPanel(logic.getPatientList(), APPOINTMENT_PLACEHOLDER);
        appointmentDetailPanelPlaceholder.getChildren().add(appointmentDetailPanel.getRoot());

        appointmentListPanel = new AppointmentListPanel(logic.getAppointmentList(), this::showAppointmentDetails);
        appointmentListPanelPlaceholder.getChildren().add(appointmentListPanel.getRoot());

        resultDisplay = new ResultDisplay();
        resultDisplayPlaceholder.getChildren().add(resultDisplay.getRoot());

        StatusBarFooter statusBarFooter = new StatusBarFooter(dataFilePath);
        statusbarPlaceholder.getChildren().add(statusBarFooter.getRoot());

        commandBox = new CommandBox(this::executeCommand, logic::getCompletions, this::focusViewList,
                this::focusNextArea);
        commandBoxPlaceholder.getChildren().add(commandBox.getRoot());

        // The window sees these keys before the focused part does, so it can move the focus away from it.
        getRoot().addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPressed);
        getRoot().addEventFilter(KeyEvent.KEY_TYPED, this::handleKeyTyped);

        viewTabs.addEventFilter(KeyEvent.KEY_PRESSED, this::handleTabKeyPressed);
        patientsTab.focusedProperty().addListener((unused1, unused2, unused3) -> updateTabKeysHint());
        appointmentsTab.focusedProperty().addListener((unused1, unused2, unused3) -> updateTabKeysHint());
        logic.getFilteredPatientList().addListener((ListChangeListener<Object>) change -> updateViewSummary());
        logic.getPatientList().addListener((ListChangeListener<Object>) change -> updateViewSummary());
        showView(View.PATIENTS);
    }

    /**
     * Shows {@code view} and hides the other view. Each view keeps its state while it is hidden.
     */
    private void showView(View view) {
        // A hidden part must not keep the focus, or the keys would go to it unseen
        boolean isFocusInHiddenView = view == View.PATIENTS
                ? appointmentListPanel.isFocused() || appointmentDetailPanel.isFocused()
                : patientListPanel.isFocused() || patientDetailPanel.isFocused();
        shownView = view;
        setShown(patientView, view == View.PATIENTS);
        setShown(appointmentView, view == View.APPOINTMENTS);
        patientsTab.pseudoClassStateChanged(SELECTED, view == View.PATIENTS);
        appointmentsTab.pseudoClassStateChanged(SELECTED, view == View.APPOINTMENTS);
        commandBox.setPromptText(view == View.PATIENTS ? PATIENT_PROMPT : APPOINTMENT_PROMPT);
        updateViewSummary();
        if (isFocusInHiddenView) {
            getTab(view).requestFocus();
        }
    }

    @FXML
    private void handlePatientsTab() {
        showView(View.PATIENTS);
    }

    @FXML
    private void handleAppointmentsTab() {
        showView(View.APPOINTMENTS);
    }

    /**
     * Switches the view with Left and Right while the focus is on a tab, and moves the focus to the tab shown.
     */
    private void handleTabKeyPressed(KeyEvent event) {
        if (event.isShiftDown() || event.isControlDown() || event.isAltDown() || event.isMetaDown()) {
            return;
        }
        switch (event.getCode()) {
            case LEFT, KP_LEFT -> showView(View.PATIENTS);
            case RIGHT, KP_RIGHT -> showView(View.APPOINTMENTS);
            default -> {
                return;
            }
        }
        getTab(shownView).requestFocus();
        event.consume();
    }

    private Button getTab(View view) {
        return view == View.PATIENTS ? patientsTab : appointmentsTab;
    }

    private void updateTabKeysHint() {
        setShown(tabKeysHint, patientsTab.isFocused() || appointmentsTab.isFocused());
    }

    /**
     * Shows what the view lists, at the right end of the tabs: the number of patients, or whose appointments.
     */
    private void updateViewSummary() {
        if (shownView == View.APPOINTMENTS) {
            viewSummary.setText("All patients");
            return;
        }
        int shown = logic.getFilteredPatientList().size();
        int total = logic.getPatientList().size();
        String patients = total == 1 ? "patient" : "patients";
        viewSummary.setText(shown == total ? total + " " + patients : shown + " of " + total + " " + patients);
    }

    private void showAppointmentDetails(Optional<PatientAppointment> appointment) {
        appointment.ifPresentOrElse(entry -> appointmentDetailPanel.show(entry.getPatient().getId(),
                Optional.of(entry.getAppointment())), appointmentDetailPanel::clear);
    }

    /**
     * Handles the keys that move the focus or reset the view. In the command box, only Shift+Tab is handled
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
            case RESET_VIEW -> resetView();
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
     * Shows the current view from its start: all patients, as the {@code list} command does, or this week's
     * appointments.
     */
    private void resetView() {
        if (shownView == View.APPOINTMENTS) {
            appointmentListPanel.showThisWeek();
            return;
        }
        try {
            executeCommand(ListCommand.COMMAND_WORD);
        } catch (CommandException | ParseException e) {
            // executeCommand has already shown the error to the user
            logger.warning("Showing all patients failed: " + e.getMessage());
        }
    }

    /**
     * Moves the focus from the command box to the list of the view that is shown, as Esc does there, or to
     * the details if the list cannot take it.
     */
    private void focusViewList() {
        if (!requestFocus(FocusArea.LIST)) {
            requestFocus(FocusArea.DETAILS);
        }
    }

    private void focusNextArea() {
        moveFocus(true);
    }

    /**
     * Moves the focus to the next area that can take it, or to the previous one if {@code isForward} is false.
     * An empty patient list, and the details when no patient is shown, are skipped.
     */
    private void moveFocus(boolean isForward) {
        // The menu bar and the result display are not areas, so going forward from them starts at the command box
        FocusArea area = getFocusedArea().orElse(FocusArea.DETAILS);
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
        } else if (patientsTab.isFocused() || appointmentsTab.isFocused()) {
            return Optional.of(FocusArea.VIEW_TABS);
        } else if (shownView == View.PATIENTS ? patientListPanel.isFocused() : appointmentListPanel.isFocused()) {
            return Optional.of(FocusArea.LIST);
        } else if (getShownDetailPanel().isFocused()) {
            return Optional.of(FocusArea.DETAILS);
        }
        return Optional.empty();
    }

    /**
     * Moves the focus to {@code area} of the view that is shown, and returns true if it took the focus.
     */
    private boolean requestFocus(FocusArea area) {
        return switch (area) {
            case COMMAND_BOX -> {
                commandBox.requestFocus();
                yield true;
            }
            case VIEW_TABS -> {
                getTab(shownView).requestFocus();
                yield true;
            }
            case LIST -> shownView == View.PATIENTS ? requestFocusOnPatientList() : requestFocusOnAppointmentList();
            case DETAILS -> getShownDetailPanel().requestFocus();
        };
    }

    private PatientDetailPanel getShownDetailPanel() {
        return shownView == View.PATIENTS ? patientDetailPanel : appointmentDetailPanel;
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
     * Moves the focus to the appointment list. If no appointment is selected, selects the one to start from,
     * which also shows its patient's details.
     */
    private boolean requestFocusOnAppointmentList() {
        appointmentListPanel.requestFocus();
        appointmentListPanel.selectDefaultIfNoneSelected();
        return true;
    }

    private static void setShown(Node node, boolean isShown) {
        node.setVisible(isShown);
        node.setManaged(isShown);
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

            commandResult.getView().ifPresent(this::showView);
            commandResult.getAppointmentDateToShow().ifPresent(date ->
                    appointmentListPanel.showWeekOf(date, commandResult.getAppointmentToSelect()));

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
