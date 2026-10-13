package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.util.Optional;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientId;

/**
 * Panel that shows all the details of one patient. It follows the list of all patients, so it shows the
 * new details after the patient is edited, and goes back to its starting message after they are deleted.
 */
public class PatientDetailPanel extends UiPart<Region> {

    private static final String FXML = "PatientDetailPanel.fxml";
    private static final String NO_MEDICAL_HISTORY = "None recorded";
    private static final String NO_NEXT_APPOINTMENT = "None upcoming";
    private static final String EMPTY_VALUE_STYLE_CLASS = "patient-detail-value-empty";

    private final ObservableList<Patient> allPatients;

    /** The ID of the patient being shown, or null if no patient is shown. */
    private PatientId shownId;

    @FXML
    private Label placeholder;
    @FXML
    private ScrollPane details;
    @FXML
    private Label name;
    @FXML
    private Label patientId;
    @FXML
    private Label phone;
    @FXML
    private Label address;
    @FXML
    private Label medicalHistory;
    @FXML
    private Label nextAppointment;

    /**
     * Creates a {@code PatientDetailPanel} that looks patients up in {@code allPatients}.
     */
    public PatientDetailPanel(ObservableList<Patient> allPatients) {
        super(FXML);
        this.allPatients = requireNonNull(allPatients);
        allPatients.addListener((ListChangeListener<Patient>) change -> refresh());
        refresh();
    }

    /**
     * Shows the details of the patient with {@code id}.
     */
    public void show(PatientId id) {
        shownId = requireNonNull(id);
        refresh();
    }

    /**
     * Returns the ID of the patient being shown, or {@code Optional#empty()} if no patient is shown.
     */
    public Optional<PatientId> getShownId() {
        return Optional.ofNullable(shownId);
    }

    private void refresh() {
        Optional<Patient> patient = Optional.ofNullable(shownId).flatMap(id -> allPatients.stream()
                .filter(p -> p.getId().equals(id)).findFirst());
        if (patient.isPresent()) {
            fill(patient.get());
        } else {
            shownId = null;
            setShown(placeholder, true);
            setShown(details, false);
        }
    }

    private void fill(Patient patient) {
        // A newly shown patient starts at the top of their details, but an edit keeps the scroll position.
        if (!patient.getId().value.equals(patientId.getText())) {
            details.setVvalue(0);
        }
        name.setText(patient.getName().fullName);
        patientId.setText(patient.getId().value);
        phone.setText(patient.getPhone().value);
        address.setText(patient.getAddress().value);
        setValue(medicalHistory, patient.getMedicalHistory().map(Object::toString), NO_MEDICAL_HISTORY);
        LocalDate today = LocalDate.now();
        setValue(nextAppointment, patient.getSchedule().getNextOnOrAfter(today)
                .map(appointment -> AppointmentFormatter.formatFull(appointment, today)),
                NO_NEXT_APPOINTMENT);
        setShown(placeholder, false);
        setShown(details, true);
    }

    /**
     * Sets the text of {@code label} to {@code text}, or to {@code textIfEmpty} in a dimmed style.
     */
    private static void setValue(Label label, Optional<String> text, String textIfEmpty) {
        label.setText(text.orElse(textIfEmpty));
        label.getStyleClass().remove(EMPTY_VALUE_STYLE_CLASS);
        if (text.isEmpty()) {
            label.getStyleClass().add(EMPTY_VALUE_STYLE_CLASS);
        }
    }

    private static void setShown(Node node, boolean isShown) {
        node.setVisible(isShown);
        node.setManaged(isShown);
    }
}
