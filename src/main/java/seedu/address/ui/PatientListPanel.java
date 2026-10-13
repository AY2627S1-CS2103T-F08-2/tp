package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientId;

/**
 * Panel containing the list of patients.
 */
public class PatientListPanel extends UiPart<Region> {
    private static final String FXML = "PatientListPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(PatientListPanel.class);

    @FXML
    private ListView<Patient> patientListView;

    /**
     * Creates a {@code PatientListPanel} with the given {@code ObservableList}.
     * {@code onPatientClicked} receives the ID of each patient that the user clicks.
     */
    public PatientListPanel(ObservableList<Patient> patientList, Consumer<PatientId> onPatientClicked) {
        super(FXML);
        requireNonNull(onPatientClicked);
        patientListView.setItems(patientList);
        patientListView.setCellFactory(listView -> new PatientListViewCell());
        // A mouse click, unlike a change of selection, never comes from the list updating itself.
        patientListView.setOnMouseClicked(event -> {
            Patient clickedPatient = patientListView.getSelectionModel().getSelectedItem();
            if (clickedPatient != null) {
                onPatientClicked.accept(clickedPatient.getId());
            }
        });
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
     * Custom {@code ListCell} that displays the graphics of a {@code Patient} using a {@code PatientCard}.
     */
    class PatientListViewCell extends ListCell<Patient> {
        PatientListViewCell() {
            // Sizes the cell to the list instead of its content, so the list never scrolls sideways.
            setPrefWidth(0);
        }

        @Override
        protected void updateItem(Patient patient, boolean empty) {
            super.updateItem(patient, empty);

            if (empty || patient == null) {
                setGraphic(null);
                setText(null);
            } else {
                Region row = new PatientCard(patient, getIndex() + 1).getRoot();
                // Fits the row to the cell, so a long name ends in "..." and the ID stays visible.
                row.prefWidthProperty().bind(widthProperty());
                setGraphic(row);
            }
        }
    }

}
