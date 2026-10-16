package seedu.address.ui;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.LocalDate;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Consumer;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.PatientAppointment;

/**
 * Panel that shows the appointments of one week, day by day: the appointment view's list.
 * With the focus in the list, Up and Down move between appointments, and Left and Right go to the previous or
 * next week. Moving the selection runs no command, so the rest of the window keeps its state.
 */
public class AppointmentListPanel extends UiPart<Region> {

    private static final String FXML = "AppointmentListPanel.fxml";
    private static final String THIS_WEEK_STYLE_CLASS = "week-relative-this";
    private static final String DAY_CELL_STYLE_CLASS = "appointment-day-cell";
    private static final String ENTRY_CELL_STYLE_CLASS = "appointment-entry-cell";

    private final ObservableList<PatientAppointment> allAppointments;
    private final Consumer<Optional<PatientAppointment>> onAppointmentChosen;
    private final Runnable onUpAtTop;
    private final ObservableList<AppointmentPage.Row> rows = FXCollections.observableArrayList();

    private AppointmentPage page;

    @FXML
    private Label weekDates;
    @FXML
    private Label weekRelative;
    @FXML
    private Label weekCount;
    @FXML
    private ListView<AppointmentPage.Row> appointmentListView;

    /**
     * Creates an {@code AppointmentListPanel} that shows the appointments in {@code allAppointments}, starting
     * with this week. {@code onAppointmentChosen} receives each appointment that the user selects, or
     * {@code Optional#empty()} when no appointment is selected any more. {@code onUpAtTop} runs when the user
     * presses Up at the first appointment of the week, or in a week with none, so that the focus can move on.
     */
    public AppointmentListPanel(ObservableList<PatientAppointment> allAppointments,
            Consumer<Optional<PatientAppointment>> onAppointmentChosen, Runnable onUpAtTop) {
        super(FXML);
        requireAllNonNull(allAppointments, onAppointmentChosen, onUpAtTop);
        this.allAppointments = allAppointments;
        this.onAppointmentChosen = onAppointmentChosen;
        this.onUpAtTop = onUpAtTop;
        appointmentListView.setItems(rows);
        appointmentListView.setCellFactory(listView -> new RowCell());
        appointmentListView.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPressed);
        appointmentListView.setOnMouseClicked(event -> chooseSelected());
        allAppointments.addListener((ListChangeListener<PatientAppointment>) change -> refresh());
        showPage(AppointmentWeek.of(LocalDate.now()));
    }

    /**
     * Shows the week that contains {@code date}, and selects {@code appointment} if it is given and in that
     * week. Otherwise, selects nothing.
     */
    public void showWeekOf(LocalDate date, Optional<PatientAppointment> appointment) {
        requireAllNonNull(date, appointment);
        showPage(AppointmentWeek.of(date));
        OptionalInt index = appointment.map(page::indexOf).orElse(OptionalInt.empty());
        if (index.isPresent()) {
            select(index.getAsInt());
        } else {
            clearSelection();
        }
    }

    /**
     * Shows this week again, and selects its first appointment from today on, as Esc does in the list.
     */
    public void showThisWeek() {
        LocalDate today = LocalDate.now();
        showPage(AppointmentWeek.of(today));
        page.defaultEntryIndex(today).ifPresentOrElse(this::select, this::clearSelection);
    }

    /**
     * Moves the keyboard focus to the list. The list takes the focus even in a week with no appointments,
     * so that Left and Right can still go to another week.
     */
    public void requestFocus() {
        appointmentListView.requestFocus();
    }

    /**
     * Returns true if the list has the keyboard focus.
     */
    public boolean isFocused() {
        return appointmentListView.isFocused();
    }

    /**
     * Returns the property that is true while the list has the keyboard focus.
     */
    public ReadOnlyBooleanProperty focusedProperty() {
        return appointmentListView.focusedProperty();
    }

    /**
     * Selects the appointment to start from, if no appointment is selected: in this week, the first one from
     * today on, and in another week, its first one.
     */
    public void selectDefaultIfNoneSelected() {
        if (appointmentListView.getSelectionModel().isEmpty()) {
            page.defaultEntryIndex(LocalDate.now()).ifPresent(this::select);
        }
    }

    @FXML
    private void handlePreviousWeek() {
        showPageAndSelectFirst(page.getWeek().previous());
    }

    @FXML
    private void handleNextWeek() {
        showPageAndSelectFirst(page.getWeek().next());
    }

    /**
     * Handles the keys that move between appointments and weeks, so that a heading is never selected.
     * Space, which would select whichever row the list last focused, does nothing. Other keys keep their
     * usual effect.
     */
    private void handleKeyPressed(KeyEvent event) {
        if (event.isControlDown() || event.isAltDown() || event.isMetaDown()) {
            return;
        }
        int selected = appointmentListView.getSelectionModel().getSelectedIndex();
        switch (event.getCode()) {
            case UP, KP_UP -> page.entryIndexBefore(selected).ifPresentOrElse(this::select, this::leaveTop);
            case DOWN, KP_DOWN -> (selected < 0 ? page.firstEntryIndex() : page.entryIndexAfter(selected))
                    .ifPresent(this::select);
            case HOME, PAGE_UP -> page.firstEntryIndex().ifPresent(this::select);
            case END, PAGE_DOWN -> page.lastEntryIndex().ifPresent(this::select);
            case LEFT, KP_LEFT -> handlePreviousWeek();
            case RIGHT, KP_RIGHT -> handleNextWeek();
            case SPACE -> {
                // Consumed below, so that the list does not select a heading
            }
            default -> {
                return;
            }
        }
        event.consume();
    }

    /**
     * Shows the top of the week, such as the days before its first appointment, and lets the focus move on,
     * as Up does at the first appointment.
     */
    private void leaveTop() {
        appointmentListView.scrollTo(0);
        onUpAtTop.run();
    }

    private void showPageAndSelectFirst(AppointmentWeek week) {
        showPage(week);
        page.firstEntryIndex().ifPresentOrElse(this::select, this::clearSelection);
    }

    private void showPage(AppointmentWeek week) {
        page = new AppointmentPage(week, allAppointments);
        appointmentListView.getSelectionModel().clearSelection();
        rows.setAll(page.getRows());
        appointmentListView.scrollTo(0);

        weekDates.setText(week.describeDates());
        Optional<String> relative = week.describeRelativeTo(LocalDate.now());
        weekRelative.setText(relative.orElse(""));
        weekRelative.getStyleClass().remove(THIS_WEEK_STYLE_CLASS);
        if (week.contains(LocalDate.now())) {
            weekRelative.getStyleClass().add(THIS_WEEK_STYLE_CLASS);
        }
        weekCount.setText(describeCount(page.getEntryCount()));
    }

    /**
     * Rebuilds the shown week after appointments change, and keeps the selected appointment if it is still
     * there, even if its patient's other details changed.
     */
    private void refresh() {
        Optional<PatientAppointment> selected = page.getEntryAt(
                appointmentListView.getSelectionModel().getSelectedIndex());
        showPage(page.getWeek());
        OptionalInt index = selected.map(page::indexOf).orElse(OptionalInt.empty());
        if (index.isPresent()) {
            select(index.getAsInt());
        } else if (selected.isPresent()) {
            clearSelection();
        }
    }

    private void select(int index) {
        appointmentListView.getSelectionModel().select(index);
        appointmentListView.getFocusModel().focus(index);
        scrollIntoView(index);
        chooseSelected();
    }

    private void clearSelection() {
        appointmentListView.getSelectionModel().clearSelection();
        onAppointmentChosen.accept(Optional.empty());
    }

    private void chooseSelected() {
        page.getEntryAt(appointmentListView.getSelectionModel().getSelectedIndex())
                .ifPresent(entry -> onAppointmentChosen.accept(Optional.of(entry)));
    }

    /**
     * Scrolls the list as little as needed to show the row at {@code index} and its day's heading, so that the
     * earlier days of the week stay above it when they fit.
     */
    private void scrollIntoView(int index) {
        if (!(appointmentListView.lookup(".virtual-flow") instanceof VirtualFlow<?> flow)) {
            // The list has not been shown yet, so it has no rows to scroll
            appointmentListView.scrollTo(index);
            return;
        }
        // A command can show this view and its week at once, so lay the window out first, or the list would
        // scroll by the size that it had while hidden
        Parent windowRoot = appointmentListView.getScene() == null ? null : appointmentListView.getScene().getRoot();
        if (windowRoot != null) {
            windowRoot.applyCss();
            windowRoot.layout();
        }
        int heading = index;
        while (heading > 0 && !(rows.get(heading) instanceof AppointmentPage.DayHeading)) {
            heading--;
        }
        flow.scrollTo(heading);
        flow.scrollTo(index);
    }

    private static String describeCount(int count) {
        return switch (count) {
            case 0 -> "No appointments";
            case 1 -> "1 appointment";
            default -> count + " appointments";
        };
    }

    /**
     * A {@code ListCell} that shows a day's heading or one appointment. Headings ignore the mouse, so that only
     * appointments can be selected.
     */
    private class RowCell extends ListCell<AppointmentPage.Row> {
        RowCell() {
            // Sizes the cell to the list instead of its content, so the list never scrolls sideways.
            setPrefWidth(0);
        }

        @Override
        protected void updateItem(AppointmentPage.Row row, boolean empty) {
            super.updateItem(row, empty);
            getStyleClass().removeAll(DAY_CELL_STYLE_CLASS, ENTRY_CELL_STYLE_CLASS);
            setMouseTransparent(false);
            setText(null);

            if (empty || row == null) {
                setGraphic(null);
            } else if (row instanceof AppointmentPage.DayHeading heading) {
                getStyleClass().add(DAY_CELL_STYLE_CLASS);
                setMouseTransparent(true);
                setGraphic(createHeading(heading));
            } else if (row instanceof AppointmentPage.Entry entry) {
                getStyleClass().add(ENTRY_CELL_STYLE_CLASS);
                setGraphic(createEntry(entry));
            }
        }

        private Node createHeading(AppointmentPage.DayHeading heading) {
            LocalDate today = LocalDate.now();
            Label day = new Label(AppointmentFormatter.formatDayHeading(heading.date(), today));
            day.getStyleClass().add(heading.date().equals(today) ? "appointment-day-today" : "appointment-day");
            Label count = new Label(heading.appointmentCount() == 0
                    ? "None" : String.valueOf(heading.appointmentCount()));
            count.getStyleClass().add("appointment-day-count");
            count.setMinWidth(Region.USE_PREF_SIZE);
            Region filler = new Region();
            HBox.setHgrow(filler, Priority.ALWAYS);
            HBox box = new HBox(day, filler, count);
            box.getStyleClass().add("appointment-day-box");
            return box;
        }

        private Node createEntry(AppointmentPage.Entry entry) {
            Appointment appointment = entry.appointment().getAppointment();
            Label number = new Label(entry.number() + ".");
            number.getStyleClass().add("appointment-entry-number");
            number.setMinWidth(28);
            Label time = new Label(AppointmentFormatter.formatTime(appointment));
            time.getStyleClass().add(appointment.getTime().isPresent()
                    ? "appointment-entry-time" : "appointment-entry-no-time");
            time.setMinWidth(64);

            Label name = new Label(entry.appointment().getPatient().getName().fullName);
            name.getStyleClass().add("appointment-entry-name");
            name.setMinWidth(0);
            Label id = new Label(entry.appointment().getPatient().getId().value);
            id.getStyleClass().add("appointment-entry-id");
            id.setMinWidth(Region.USE_PREF_SIZE);
            HBox patient = new HBox(8, name, id);
            patient.setAlignment(Pos.BASELINE_LEFT);
            patient.setMinWidth(0);
            // A later field for the appointment's details goes under the patient
            VBox text = new VBox(2, patient);
            text.setMinWidth(0);
            HBox.setHgrow(text, Priority.ALWAYS);

            HBox box = new HBox(10, number, time, text);
            box.setAlignment(Pos.BASELINE_LEFT);
            box.getStyleClass().add("appointment-entry-box");
            return box;
        }
    }
}
