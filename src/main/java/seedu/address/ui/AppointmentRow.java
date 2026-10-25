package seedu.address.ui;

import java.time.LocalDateTime;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.Patient;

/**
 * A UI component that displays one appointment as a row of the appointment list: its number on the page, its time,
 * its patient's name and ID, and under them the appointment's details, if it has any. An appointment that has
 * passed is greyed out. {@link AppointmentListPanel} draws the day headings between the rows.
 */
public class AppointmentRow extends UiPart<Region> {

    private static final String FXML = "AppointmentRow.fxml";
    private static final String TIME_STYLE_CLASS = "appointment-entry-time";
    private static final String NO_TIME_STYLE_CLASS = "appointment-entry-no-time";
    private static final String PASSED_STYLE_CLASS = "appointment-entry-passed";

    @FXML
    private Label number;
    @FXML
    private Label time;
    @FXML
    private Label name;
    @FXML
    private Label patientId;
    @FXML
    private Label details;

    /**
     * Creates an {@code AppointmentRow} that shows {@code entry}, greyed out if it has passed at {@code now}.
     */
    public AppointmentRow(AppointmentPage.Entry entry, LocalDateTime now) {
        this(entry, now, false);
    }

    /**
     * Creates a row whose time is marked red when it clashes with another visit.
     */
    public AppointmentRow(AppointmentPage.Entry entry, LocalDateTime now, boolean isClashing) {
        super(FXML);
        Appointment appointment = entry.appointment().getAppointment();
        Patient patient = entry.appointment().getPatient();
        number.setText(entry.number() + ".");
        time.setText(AppointmentFormatter.formatTime(appointment));
        time.getStyleClass().add(appointment.getTime().isPresent() ? TIME_STYLE_CLASS : NO_TIME_STYLE_CLASS);
        name.setText(patient.getName().fullName);
        patientId.setText(patient.getId().value + " #" + appointment.getNumber());
        details.setText(appointment.getDetails().map(Object::toString).orElse(""));
        // A row with no details stays one line high
        details.setVisible(appointment.getDetails().isPresent());
        details.setManaged(appointment.getDetails().isPresent());
        if (AppointmentFormatter.hasPassed(appointment, now)) {
            getRoot().getStyleClass().add(PASSED_STYLE_CLASS);
        }
        if (isClashing) {
            getRoot().getStyleClass().add("appointment-entry-clash");
        }
    }
}
