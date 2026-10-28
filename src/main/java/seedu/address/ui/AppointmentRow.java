package seedu.address.ui;

import java.time.LocalDateTime;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.Patient;

/**
 * A UI component that displays one appointment as a row of the appointment list: its number on the page, its time,
 * its patient's name, ID and number, and under them its optional details. A pin before the time shows that it is
 * marked. An appointment that has
 * passed is greyed out. {@link AppointmentListPanel} draws the day headings between the rows.
 * A list cell keeps one row and shows each of its appointments in it, since loading a row's FXML for every
 * appointment that scrolls into view makes scrolling stutter.
 */
public class AppointmentRow extends UiPart<Region> {

    private static final String FXML = "AppointmentRow.fxml";
    private static final String TIME_STYLE_CLASS = "appointment-entry-time";
    private static final String NO_TIME_STYLE_CLASS = "appointment-entry-no-time";
    private static final String PASSED_STYLE_CLASS = "appointment-entry-passed";
    private static final String CLASH_STYLE_CLASS = "appointment-entry-clash";

    @FXML
    private Label number;
    @FXML
    private Label time;
    @FXML
    private Label name;
    @FXML
    private Label patientId;
    @FXML
    private Label appointmentNumber;
    @FXML
    private Label details;

    private final Region clashMark = ClashMark.create();
    private final Region pinMark = PinMark.create();

    /**
     * Creates an {@code AppointmentRow} that shows no appointment until {@link #show} is called.
     */
    public AppointmentRow() {
        super(FXML);
    }

    /**
     * Creates an {@code AppointmentRow} that shows {@code entry}, greyed out if it has passed at {@code now}.
     */
    public AppointmentRow(AppointmentPage.Entry entry, LocalDateTime now) {
        this(entry, now, false);
    }

    /**
     * Creates a row whose time is marked red, with a clash mark before it, when it clashes with another visit.
     */
    public AppointmentRow(AppointmentPage.Entry entry, LocalDateTime now, boolean isClashing) {
        this();
        show(entry, now, isClashing);
    }

    /**
     * Shows {@code entry} in place of what the row showed before, greyed out if it has passed at {@code now}, and
     * with its time marked red, with a clash mark before it, if {@code isClashing}.
     */
    public void show(AppointmentPage.Entry entry, LocalDateTime now, boolean isClashing) {
        Appointment appointment = entry.appointment().getAppointment();
        Patient patient = entry.appointment().getPatient();
        number.setText(entry.number() + ".");
        time.setText(AppointmentFormatter.formatTime(appointment));
        time.getStyleClass().removeAll(TIME_STYLE_CLASS, NO_TIME_STYLE_CLASS);
        time.getStyleClass().add(appointment.getTime().isPresent() ? TIME_STYLE_CLASS : NO_TIME_STYLE_CLASS);
        TimeMark.show(time, clashMark, pinMark, isClashing, appointment.isDone());
        name.setText(patient.getName().fullName);
        patientId.setText(patient.getId().value);
        appointmentNumber.setText("#" + appointment.getNumber());
        details.setText(appointment.getDetails().map(Object::toString).orElse(""));
        // Hide absent details without leaving an empty line below the name
        details.setVisible(appointment.getDetails().isPresent());
        details.setManaged(appointment.getDetails().isPresent());
        getRoot().getStyleClass().removeAll(PASSED_STYLE_CLASS, CLASH_STYLE_CLASS);
        if (AppointmentFormatter.hasPassed(appointment, now)) {
            getRoot().getStyleClass().add(PASSED_STYLE_CLASS);
        }
        if (isClashing) {
            getRoot().getStyleClass().add(CLASH_STYLE_CLASS);
        }
    }
}
