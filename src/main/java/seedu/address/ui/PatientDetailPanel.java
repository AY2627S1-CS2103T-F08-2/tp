package seedu.address.ui;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientId;

/**
 * Panel that shows all the details of one patient, including their upcoming and latest past appointments.
 * It follows the list of all patients, so it shows the new details after the patient is edited, and goes back
 * to its starting message after they are deleted. One appointment can be marked, as the appointment view does
 * for the appointment selected there.
 */
public class PatientDetailPanel extends UiPart<Region> {

    private static final String FXML = "PatientDetailPanel.fxml";
    private static final String NO_MEDICAL_HISTORY = "None recorded";
    private static final String EMPTY_VALUE_STYLE_CLASS = "patient-detail-value-empty";
    private static final String ROW_STYLE_CLASS = "detail-appointment-row";
    private static final String TODAY_STYLE_CLASS = "detail-appointment-today";
    private static final String PAST_STYLE_CLASS = "detail-appointment-past";
    private static final String MARKED_STYLE_CLASS = "detail-appointment-marked";
    /** The width of an appointment's date, the same as the keys above it. */
    private static final double DATE_WIDTH = 150;
    /** The space between an appointment's date and its time. */
    private static final double ROW_SPACING = 20;
    /** The width that a date may shrink to in a narrow panel: just enough for one such as "Mon 28 Dec 2026". */
    private static final double DATE_MIN_WIDTH = 125;
    /** How far one Shift+Up or Shift+Down scrolls the details, in pixels: about as far as a mouse wheel step. */
    private static final double SCROLL_STEP = 40;

    private final ObservableList<Patient> allPatients;
    /** The width of the date in every appointment row, so that the times line up. */
    private final DoubleProperty dateWidth = new SimpleDoubleProperty(DATE_WIDTH);
    /** The times of the appointment rows shown, whose widest decides {@link #dateWidth}. */
    private final List<Label> timeLabels = new ArrayList<>();

    /** The ID of the patient being shown, or null if no patient is shown. */
    private PatientId shownId;
    /** The appointment of the shown patient to mark, or null to mark none. */
    private Appointment markedAppointment;

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
    private Label upcomingNote;
    @FXML
    private VBox upcomingRows;
    @FXML
    private VBox pastSection;
    @FXML
    private Label pastNote;
    @FXML
    private VBox pastRows;

    /**
     * Creates a {@code PatientDetailPanel} that looks patients up in {@code allPatients}, and shows
     * {@code placeholderText} while no patient is shown.
     */
    public PatientDetailPanel(ObservableList<Patient> allPatients, String placeholderText) {
        super(FXML);
        requireAllNonNull(allPatients, placeholderText);
        this.allPatients = allPatients;
        placeholder.setText(placeholderText);
        allPatients.addListener((ListChangeListener<Patient>) change -> refresh());
        upcomingRows.widthProperty().addListener((unused, oldWidth, width) -> updateDateWidth());
        refresh();
    }

    /**
     * Shows the details of the patient with {@code id}, with no appointment marked.
     */
    public void show(PatientId id) {
        show(id, Optional.empty());
    }

    /**
     * Shows the details of the patient with {@code id}, and marks {@code appointment} among their appointments
     * if it is given.
     */
    public void show(PatientId id, Optional<Appointment> appointment) {
        requireAllNonNull(id, appointment);
        shownId = id;
        markedAppointment = appointment.orElse(null);
        refresh();
    }

    /**
     * Stops showing a patient, and shows the starting message.
     */
    public void clear() {
        shownId = null;
        markedAppointment = null;
        refresh();
    }

    /**
     * Returns the ID of the patient being shown, or {@code Optional#empty()} if no patient is shown.
     */
    public Optional<PatientId> getShownId() {
        return Optional.ofNullable(shownId);
    }

    /**
     * Scrolls the details by {@code steps} steps, up if it is negative and down if it is positive. Does nothing
     * if no patient is shown, or if the details fit without scrolling.
     */
    public void scroll(int steps) {
        double scrollableHeight = details.getContent().getLayoutBounds().getHeight()
                - details.getViewportBounds().getHeight();
        if (!details.isVisible() || scrollableHeight <= 0) {
            return;
        }
        double range = details.getVmax() - details.getVmin();
        double vvalue = details.getVvalue() + steps * SCROLL_STEP / scrollableHeight * range;
        details.setVvalue(Math.clamp(vvalue, details.getVmin(), details.getVmax()));
    }

    /**
     * Returns true if the details have the keyboard focus, as they do after the user clicks them.
     */
    public boolean isFocused() {
        return details.isFocused();
    }

    /**
     * Shows the patient's details again as of now, after the patient changes, an appointment passes or a new day
     * begins. Keeps the scroll position.
     */
    public void refresh() {
        Optional<Patient> patient = Optional.ofNullable(shownId).flatMap(id -> allPatients.stream()
                .filter(p -> p.getId().equals(id)).findFirst());
        if (patient.isPresent()) {
            fill(patient.get());
        } else {
            shownId = null;
            markedAppointment = null;
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

        // An edit can remove the marked appointment
        if (markedAppointment != null && !patient.getSchedule().contains(markedAppointment)) {
            markedAppointment = null;
        }
        LocalDateTime now = LocalDateTime.now();
        DetailAppointments appointments = DetailAppointments.of(patient.getSchedule(), now,
                Optional.ofNullable(markedAppointment));
        upcomingNote.setText(appointments.describeUpcoming());
        timeLabels.clear();
        upcomingRows.getChildren().setAll(createRows(appointments.getUpcoming(), now));
        pastNote.setText(appointments.describePast());
        pastRows.getChildren().setAll(createRows(appointments.getPastShown(), now));
        // The new rows need their styles, such as the font of today's rows, before their times can be measured
        upcomingRows.applyCss();
        pastRows.applyCss();
        updateDateWidth();
        setShown(pastSection, appointments.getPastCount() > 0);

        setShown(placeholder, false);
        setShown(details, true);
    }

    private List<Node> createRows(List<Appointment> appointments, LocalDateTime now) {
        return appointments.stream().map(appointment -> createRow(appointment, now)).toList();
    }

    /**
     * Returns one row of the appointments: the date, then the time. A later field for the appointment's
     * details fills the rest of the row. An appointment that has passed is dimmed, also one earlier today.
     * In a narrow panel, the dates of all rows shrink together, but never below the width of a long date, and the
     * time wraps, so that the row is never wider than the panel.
     */
    private Node createRow(Appointment appointment, LocalDateTime now) {
        LocalDate today = now.toLocalDate();
        Label date = new Label(AppointmentFormatter.formatDetailDate(appointment.getDate(), today));
        date.getStyleClass().add("detail-appointment-date");
        date.minWidthProperty().bind(dateWidth);
        date.prefWidthProperty().bind(dateWidth);
        Label time = new Label(AppointmentFormatter.formatDetailTime(appointment));
        time.getStyleClass().add(appointment.getTime().isPresent()
                ? "detail-appointment-time" : "detail-appointment-no-time");
        time.setWrapText(true);
        time.setMinWidth(0);
        time.setMinHeight(Region.USE_PREF_SIZE);
        timeLabels.add(time);
        Region filler = new Region();
        HBox.setHgrow(filler, Priority.ALWAYS);

        HBox row = new HBox(ROW_SPACING, date, time, filler);
        row.getStyleClass().add(ROW_STYLE_CLASS);
        if (AppointmentFormatter.hasPassed(appointment, now)) {
            row.getStyleClass().add(PAST_STYLE_CLASS);
        } else if (AppointmentFormatter.isToday(appointment, today)) {
            row.getStyleClass().add(TODAY_STYLE_CLASS);
        }
        if (appointment.equals(markedAppointment)) {
            row.getStyleClass().add(MARKED_STYLE_CLASS);
        }
        return row;
    }

    /**
     * Sets the width of the appointments' dates: the full width if the rows have room for it and their widest time
     * on one line, and otherwise less, down to the width of a long date, so that a time that wraps keeps whole
     * words. All rows get the same width, so that their times line up.
     */
    private void updateDateWidth() {
        double widestTime = timeLabels.stream().mapToDouble(label -> label.prefWidth(-1)).max().orElse(0);
        double rowPadding = upcomingRows.getChildren().isEmpty() && pastRows.getChildren().isEmpty() ? 0
                : rowPadding(upcomingRows.getChildren().isEmpty() ? pastRows : upcomingRows);
        double room = upcomingRows.getWidth() - rowPadding - ROW_SPACING - widestTime;
        dateWidth.set(Math.clamp(room, DATE_MIN_WIDTH, DATE_WIDTH));
    }

    private static double rowPadding(VBox rows) {
        Region row = (Region) rows.getChildren().get(0);
        return row.snappedLeftInset() + row.snappedRightInset();
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
