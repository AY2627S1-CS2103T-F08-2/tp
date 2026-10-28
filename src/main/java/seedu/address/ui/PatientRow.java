package seedu.address.ui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.Patient;

/**
 * A UI component that displays one {@code Patient} as a row of the patient list: the list number, name,
 * patient ID and next appointment. {@link PatientDetailPanel} shows the other details.
 * A list cell keeps one row and shows each of its patients in it, since loading a row's FXML for every patient
 * that scrolls into view makes scrolling stutter.
 */
public class PatientRow extends UiPart<Region> {

    /** The space after the last column of the row, as set in its FXML. The list header lines up with it. */
    static final double END_PADDING = 12;

    private static final String FXML = "PatientRow.fxml";
    private static final String TODAY_STYLE_CLASS = "patient-row-appointment-today";

    /*
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * See https://github.com/se-edu/addressbook-level4/issues/336 (the issue on AddressBook level 4).
     */

    @FXML
    private HBox rowPane;
    @FXML
    private Label index;
    @FXML
    private Label name;
    @FXML
    private Label patientId;
    @FXML
    private Label nextAppointment;

    /**
     * Creates a {@code PatientRow} that shows no patient until {@link #show} is called.
     */
    public PatientRow() {
        super(FXML);
    }

    /**
     * Shows {@code patient} with the list number {@code displayedIndex}, in place of what the row showed before.
     */
    public void show(Patient patient, int displayedIndex) {
        index.setText(displayedIndex + ".");
        name.setText(patient.getName().fullName);
        patientId.setText(patient.getId().value);
        LocalDateTime now = LocalDateTime.now();
        Optional<Appointment> next = AppointmentFormatter.findFirstNotPassed(patient.getSchedule().asList().stream()
                .filter(appointment -> !appointment.isDone()).toList(), now);
        nextAppointment.getStyleClass().remove(TODAY_STYLE_CLASS);
        nextAppointment.setVisible(next.isPresent());
        nextAppointment.setManaged(next.isPresent());
        next.ifPresent(appointment -> showNextAppointment(appointment, now.toLocalDate()));
    }

    /**
     * Shows {@code appointment}, the patient's next one, highlighted if it is today.
     */
    private void showNextAppointment(Appointment appointment, LocalDate today) {
        nextAppointment.setText(AppointmentFormatter.formatShort(appointment, today));
        if (AppointmentFormatter.isToday(appointment, today)) {
            nextAppointment.getStyleClass().add(TODAY_STYLE_CLASS);
        }
    }
}
