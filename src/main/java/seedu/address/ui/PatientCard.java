package seedu.address.ui;

import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.Patient;

/**
 * A UI component that displays one {@code Patient} as a row of the patient list: the list number, name,
 * patient ID and next appointment. {@link PatientDetailPanel} shows the other details.
 */
public class PatientCard extends UiPart<Region> {

    private static final String FXML = "PatientListCard.fxml";
    private static final String TODAY_STYLE_CLASS = "patient-row-appointment-today";
    private static final String PAST_STYLE_CLASS = "patient-row-appointment-past";

    public final Patient patient;

    /*
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * See https://github.com/se-edu/addressbook-level4/issues/336 (the issue on AddressBook level 4).
     */

    @FXML
    private HBox cardPane;
    @FXML
    private Label index;
    @FXML
    private Label name;
    @FXML
    private Label patientId;
    @FXML
    private Label nextAppointment;

    /**
     * Creates a {@code PatientCard} with the given {@code Patient} and list number to display.
     */
    public PatientCard(Patient patient, int displayedIndex) {
        super(FXML);
        this.patient = patient;
        index.setText(displayedIndex + ".");
        name.setText(patient.getName().fullName);
        patientId.setText(patient.getId().value);
        patient.getNextAppointment().ifPresentOrElse(this::showNextAppointment, () -> {
            nextAppointment.setVisible(false);
            nextAppointment.setManaged(false);
        });
    }

    private void showNextAppointment(Appointment appointment) {
        LocalDate today = LocalDate.now();
        nextAppointment.setText(AppointmentFormatter.formatShort(appointment, today));
        if (AppointmentFormatter.isToday(appointment, today)) {
            nextAppointment.getStyleClass().add(TODAY_STYLE_CLASS);
        } else if (AppointmentFormatter.isPast(appointment, today)) {
            nextAppointment.getStyleClass().add(PAST_STYLE_CLASS);
        }
    }
}
