package seedu.address.ui;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Logger;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientId;

/**
 * Panel containing the list of patients, under a header that says whether the list is filtered, as after
 * {@code find}, and how many patients it shows.
 */
public class PatientListPanel extends UiPart<Region> {
    private static final String FXML = "PatientListPanel.fxml";
    private static final String NO_MATCH_PLACEHOLDER = "No patients match the filter.";
    private static final String NO_PATIENTS_PLACEHOLDER = "No patients yet.";

    /** Keys that move the selection in the list. */
    private static final Set<KeyCode> NAVIGATION_KEYS = Set.of(KeyCode.UP, KeyCode.DOWN, KeyCode.KP_UP,
            KeyCode.KP_DOWN, KeyCode.PAGE_UP, KeyCode.PAGE_DOWN, KeyCode.HOME, KeyCode.END);

    private final Logger logger = LogsCenter.getLogger(PatientListPanel.class);

    private final ObservableList<Patient> shownPatients;
    private final ObservableList<Patient> allPatients;
    private final Label placeholder = new Label();

    @FXML
    private HBox header;
    @FXML
    private Label listTitle;
    @FXML
    private Label listCount;
    @FXML
    private ListView<Patient> patientListView;

    /** Whether the user is holding a key that moves the selection, so that a change of selection is theirs. */
    private boolean isNavigating;

    /**
     * Creates a {@code PatientListPanel} that lists {@code shownPatients}, the patients that the filter shows,
     * out of {@code allPatients}.
     * {@code onPatientChosen} receives the ID of each patient that the user clicks, or moves to with the keyboard.
     * {@code onUpAtTop} runs when the user presses Up at the first patient, so that the focus can move on.
     */
    public PatientListPanel(ObservableList<Patient> shownPatients, ObservableList<Patient> allPatients,
            Consumer<PatientId> onPatientChosen, Runnable onUpAtTop) {
        super(FXML);
        requireAllNonNull(shownPatients, allPatients, onPatientChosen, onUpAtTop);
        this.shownPatients = shownPatients;
        this.allPatients = allPatients;
        patientListView.setItems(shownPatients);
        patientListView.setCellFactory(listView -> new PatientListViewCell());
        placeholder.getStyleClass().add("list-placeholder");
        patientListView.setPlaceholder(placeholder);
        ListHeaders.alignEndWithRows(header, patientListView, PatientRow.END_PADDING);
        ListChangeListener<Patient> headerUpdater = change -> updateHeader();
        shownPatients.addListener(headerUpdater);
        allPatients.addListener(headerUpdater);
        updateHeader();
        // A mouse click, unlike a change of selection, never comes from the list updating itself.
        patientListView.setOnMouseClicked(event -> {
            Patient clickedPatient = patientListView.getSelectionModel().getSelectedItem();
            if (clickedPatient != null) {
                onPatientChosen.accept(clickedPatient.getId());
            }
        });
        // Filtering or removing rows also moves the selection, so only a change made while the user holds
        // a navigation key counts as the user's choice.
        patientListView.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (isJumpKey(event)) {
                isNavigating = false;
                boolean isUp = event.getCode() == KeyCode.UP || event.getCode() == KeyCode.KP_UP;
                jumpTo(isUp ? 0 : patientListView.getItems().size() - 1, onPatientChosen);
                event.consume();
                return;
            }
            if (isUpAtTop(event)) {
                isNavigating = false;
                onUpAtTop.run();
                event.consume();
                return;
            }
            isNavigating = NAVIGATION_KEYS.contains(event.getCode());
        });
        patientListView.addEventFilter(KeyEvent.KEY_RELEASED, event -> isNavigating = false);
        patientListView.focusedProperty().addListener((unused, wasFocused, isFocused) -> isNavigating = false);
        patientListView.getSelectionModel().selectedItemProperty().addListener((unused, oldPatient, newPatient) -> {
            if (isNavigating && newPatient != null) {
                onPatientChosen.accept(newPatient.getId());
            }
        });
    }

    /**
     * Shows in the header whether the list is filtered, and how many patients it shows. The list counts as
     * filtered while it shows fewer patients than there are. Also sets the message of an empty list.
     */
    private void updateHeader() {
        int shown = shownPatients.size();
        int total = allPatients.size();
        boolean isFiltered = shown < total;
        listTitle.setText(isFiltered ? "Filtered patients" : "All patients");
        if (total == 0) {
            listCount.setText("No patients");
        } else if (isFiltered) {
            listCount.setText(shown + " of " + total);
        } else {
            listCount.setText(total == 1 ? "1 patient" : total + " patients");
        }
        placeholder.setText(total == 0 ? NO_PATIENTS_PLACEHOLDER : NO_MATCH_PLACEHOLDER);
    }

    /**
     * Redraws the rows as of now, so that each patient shows their next appointment, after one has passed or a new
     * day has begun. Keeps the selection and the scroll position.
     */
    public void refresh() {
        patientListView.refresh();
    }

    /**
     * Moves the keyboard focus to the list, unless the list is empty.
     *
     * @return true if the list has the focus.
     */
    public boolean requestFocus() {
        if (patientListView.getItems().isEmpty()) {
            return false;
        }
        patientListView.requestFocus();
        return true;
    }

    /**
     * Returns true if the list has the keyboard focus.
     */
    public boolean isFocused() {
        return patientListView.isFocused();
    }

    /**
     * Returns the property that is true while the list has the keyboard focus.
     */
    public ReadOnlyBooleanProperty focusedProperty() {
        return patientListView.focusedProperty();
    }

    /**
     * Returns true if {@code event} is Ctrl+Up or Ctrl+Down (Cmd on macOS), which jump to the first or last
     * patient, in a list that has patients.
     */
    private boolean isJumpKey(KeyEvent event) {
        boolean isUpOrDown = Set.of(KeyCode.UP, KeyCode.KP_UP, KeyCode.DOWN, KeyCode.KP_DOWN)
                .contains(event.getCode());
        return isUpOrDown && event.isShortcutDown() && !event.isAltDown() && !patientListView.getItems().isEmpty();
    }

    /**
     * Selects the patient at {@code index}, scrolls to them and shows their details.
     */
    private void jumpTo(int index, Consumer<PatientId> onPatientChosen) {
        patientListView.getSelectionModel().select(index);
        patientListView.scrollTo(index);
        onPatientChosen.accept(patientListView.getItems().get(index).getId());
    }

    /**
     * Returns true if {@code event} is Up, without Ctrl, Alt or Cmd, while the first patient or no patient is
     * selected, so that the list cannot move up any further.
     */
    private boolean isUpAtTop(KeyEvent event) {
        boolean isUp = event.getCode() == KeyCode.UP || event.getCode() == KeyCode.KP_UP;
        boolean isShortcutDown = event.isControlDown() || event.isAltDown() || event.isMetaDown();
        return isUp && !isShortcutDown && patientListView.getSelectionModel().getSelectedIndex() <= 0;
    }

    /**
     * Selects the first patient if no patient is selected.
     *
     * @return the ID of the patient that was selected, or {@code Optional#empty()} if a patient was already
     *         selected or the list is empty.
     */
    public Optional<PatientId> selectFirstIfNoneSelected() {
        if (!patientListView.getSelectionModel().isEmpty() || patientListView.getItems().isEmpty()) {
            return Optional.empty();
        }
        patientListView.getSelectionModel().selectFirst();
        patientListView.scrollTo(0);
        return Optional.of(patientListView.getItems().get(0).getId());
    }

    /**
     * Selects the patient with {@code id} and scrolls to them, or clears the selection if that patient
     * is not in the displayed list.
     */
    public void select(PatientId id) {
        requireNonNull(id);
        ObservableList<Patient> patients = patientListView.getItems();
        for (int i = 0; i < patients.size(); i++) {
            if (patients.get(i).getId().equals(id)) {
                patientListView.getSelectionModel().select(i);
                patientListView.scrollTo(i);
                return;
            }
        }
        logger.fine("Patient " + id + " is not in the displayed list, so nothing is selected");
        clearSelection();
    }

    /**
     * Clears the selection, so that no row is highlighted.
     */
    public void clearSelection() {
        patientListView.getSelectionModel().clearSelection();
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code Patient} using a {@code PatientRow}.
     */
    class PatientListViewCell extends ListCell<Patient> {
        // One row for the cell's life: building a row loads its FXML, too slow to do for each row scrolled into view
        private final PatientRow row = new PatientRow();

        PatientListViewCell() {
            // Sizes the cell to the list instead of its content, so the list never scrolls sideways.
            setPrefWidth(0);
            // Fits the row to the cell, so a long name ends in "..." and the ID stays visible.
            row.getRoot().prefWidthProperty().bind(widthProperty());
        }

        @Override
        protected void updateItem(Patient patient, boolean empty) {
            super.updateItem(patient, empty);

            if (empty || patient == null) {
                setGraphic(null);
                setText(null);
            } else {
                row.show(patient, getIndex() + 1);
                setGraphic(row.getRoot());
            }
        }
    }

}
