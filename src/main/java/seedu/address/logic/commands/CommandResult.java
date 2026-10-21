package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.patient.PatientAppointment;
import seedu.address.model.patient.PatientId;

/**
 * Represents the result of a command execution.
 */
public class CommandResult {

    /**
     * The views of the main window, each on its own tab.
     */
    public enum View {
        /** The patient list and the details of one patient. */
        PATIENTS,
        /** The appointments, day by day, and the details of one appointment's patient. */
        APPOINTMENTS
    }

    /**
     * The dates, both included, whose appointments the appointment view shows on one page, as {@code viewappt}
     * finds them. An end of {@code LocalDate.MAX} means that the range has no end. A patient ID of {@code null}
     * means that the appointments of every patient are shown; otherwise, only that patient's are.
     */
    public record AppointmentRange(LocalDate start, LocalDate end, PatientId patientId) {

        /**
         * Constructs an {@code AppointmentRange} of every patient's appointments from {@code start} to {@code end}.
         */
        public AppointmentRange(LocalDate start, LocalDate end) {
            this(start, end, null);
        }

        /**
         * Constructs an {@code AppointmentRange}.
         *
         * @throws IllegalArgumentException if {@code start} is after {@code end}.
         */
        public AppointmentRange {
            requireAllNonNull(start, end);
            checkArgument(!start.isAfter(end), "The start of the range must not be after its end.");
        }

        /**
         * Returns true if {@code date} is within the range.
         */
        public boolean contains(LocalDate date) {
            requireNonNull(date);
            return !date.isBefore(start) && !date.isAfter(end);
        }

        /**
         * Returns true if {@code entry} is on a date within the range, and is of the range's patient, if it has one.
         */
        public boolean contains(PatientAppointment entry) {
            requireNonNull(entry);
            return contains(entry.getAppointment().getDate())
                    && (patientId == null || entry.getPatient().getId().equals(patientId));
        }
    }

    /**
     * Where the main window goes after a command: the view to switch to, and what to select in that view's list.
     * A result with no {@code Navigation} keeps what both views select.
     * <p>
     * {@link #toPatient} selects that patient. {@link #toAppointment} selects that appointment, on the result's
     * {@link AppointmentRange} if it has one. Without a range, the appointment view stays on the page shown if that
     * page has the appointment, and otherwise shows the appointment's week. {@link #toView} selects the entry to start
     * from: the first patient in the list, or the next appointment from now (the first, if all have passed), on the
     * result's range if it has one, and otherwise in this week.
     * <p>
     * The entry to select must be in the list that its view shows after the command: a patient in the filtered
     * patient list, and an appointment in the result's range, if it has one. Otherwise the command has a bug, and
     * {@code LogicManager} throws an {@code IllegalStateException}.
     */
    public record Navigation(View view, Optional<PatientId> patient, Optional<PatientAppointment> appointment) {

        /**
         * Constructs a {@code Navigation}.
         *
         * @throws IllegalArgumentException if it selects a patient outside the patient view, or an appointment
         *         outside the appointment view.
         */
        public Navigation {
            requireAllNonNull(view, patient, appointment);
            checkArgument(patient.isEmpty() || view == View.PATIENTS, "Only the patient view can select a patient.");
            checkArgument(appointment.isEmpty() || view == View.APPOINTMENTS,
                    "Only the appointment view can select an appointment.");
        }

        /**
         * Returns a {@code Navigation} to {@code view} that selects the entry to start from.
         */
        public static Navigation toView(View view) {
            return new Navigation(view, Optional.empty(), Optional.empty());
        }

        /**
         * Returns a {@code Navigation} to the patient view that selects the patient with {@code id}.
         */
        public static Navigation toPatient(PatientId id) {
            return new Navigation(View.PATIENTS, Optional.of(id), Optional.empty());
        }

        /**
         * Returns a {@code Navigation} to the appointment view that selects {@code appointment}.
         */
        public static Navigation toAppointment(PatientAppointment appointment) {
            return new Navigation(View.APPOINTMENTS, Optional.empty(), Optional.of(appointment));
        }
    }

    private final String feedbackToUser;

    /** Help information should be shown to the user. */
    private final boolean showHelp;

    /** The application should exit. */
    private final boolean exit;

    /** The ID of the patient whose details should be shown, or null to keep showing the current patient. */
    private final PatientId patientToView;

    /** The view to switch to, or null to keep the current view. */
    private final View view;

    /** The range whose appointments the appointment view should show, or null to keep what it shows. */
    private final AppointmentRange appointmentRange;

    /** Where the main window goes, or null to keep the selections of both views. */
    private final Navigation navigation;

    /**
     * Constructs a {@code CommandResult} with the specified fields.
     */
    public CommandResult(String feedbackToUser, boolean showHelp, boolean exit) {
        this(feedbackToUser, showHelp, exit, null, null, null, null);
    }

    /**
     * Constructs a {@code CommandResult} that asks for the details of the patient with {@code patientToView}
     * to be shown, with other fields set to their default value.
     */
    public CommandResult(String feedbackToUser, PatientId patientToView) {
        this(feedbackToUser, false, false, requireNonNull(patientToView), null, null, null);
    }

    /**
     * Constructs a {@code CommandResult} that asks the appointment view to show the appointments in
     * {@code appointmentRange} on one page, with other fields set to their default value.
     */
    public CommandResult(String feedbackToUser, AppointmentRange appointmentRange) {
        this(feedbackToUser, false, false, null, null, requireNonNull(appointmentRange), null);
    }

    /**
     * Constructs a {@code CommandResult} with the specified {@code feedbackToUser},
     * and other fields set to their default value.
     */
    public CommandResult(String feedbackToUser) {
        this(feedbackToUser, false, false, null, null, null, null);
    }

    private CommandResult(String feedbackToUser, boolean showHelp, boolean exit, PatientId patientToView,
            View view, AppointmentRange appointmentRange, Navigation navigation) {
        this.feedbackToUser = requireNonNull(feedbackToUser);
        this.showHelp = showHelp;
        this.exit = exit;
        this.patientToView = patientToView;
        this.view = view;
        this.appointmentRange = appointmentRange;
        this.navigation = navigation;
    }

    /**
     * Returns a copy of this result that asks the main window to switch to {@code view}.
     *
     * @throws IllegalArgumentException if this result navigates to another view.
     */
    public CommandResult withView(View view) {
        requireNonNull(view);
        checkArgument(navigation == null || navigation.view() == view,
                "The view must be the one that the result navigates to.");
        return new CommandResult(feedbackToUser, showHelp, exit, patientToView, view, appointmentRange, navigation);
    }

    /**
     * Returns a copy of this result that asks the main window to go where {@code navigation} says, which also
     * switches to its view. See {@link Navigation} for what it selects.
     *
     * @throws IllegalArgumentException if this result already switches to another view.
     */
    public CommandResult withNavigation(Navigation navigation) {
        requireNonNull(navigation);
        checkArgument(view == null || view == navigation.view(),
                "The view must be the one that the result switches to.");
        return new CommandResult(feedbackToUser, showHelp, exit, patientToView, navigation.view(), appointmentRange,
                navigation);
    }

    public String getFeedbackToUser() {
        return feedbackToUser;
    }

    public boolean isShowHelp() {
        return showHelp;
    }

    public boolean isExit() {
        return exit;
    }

    /**
     * Returns the ID of the patient whose details should be shown, or {@code Optional#empty()} if the
     * shown patient should not change.
     */
    public Optional<PatientId> getPatientToView() {
        return Optional.ofNullable(patientToView);
    }

    /**
     * Returns the view to switch to, or {@code Optional#empty()} to keep the current view.
     */
    public Optional<View> getView() {
        return Optional.ofNullable(view);
    }

    /**
     * Returns the range whose appointments the appointment view should show, or {@code Optional#empty()} if
     * what it shows should not change.
     */
    public Optional<AppointmentRange> getAppointmentRange() {
        return Optional.ofNullable(appointmentRange);
    }

    /**
     * Returns where the main window goes, or {@code Optional#empty()} if the selections of both views should not
     * change.
     */
    public Optional<Navigation> getNavigation() {
        return Optional.ofNullable(navigation);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof CommandResult otherCommandResult)) {
            return false;
        }

        return feedbackToUser.equals(otherCommandResult.feedbackToUser)
                && showHelp == otherCommandResult.showHelp
                && exit == otherCommandResult.exit
                && Objects.equals(patientToView, otherCommandResult.patientToView)
                && view == otherCommandResult.view
                && Objects.equals(appointmentRange, otherCommandResult.appointmentRange)
                && Objects.equals(navigation, otherCommandResult.navigation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(feedbackToUser, showHelp, exit, patientToView, view, appointmentRange, navigation);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("feedbackToUser", feedbackToUser)
                .add("showHelp", showHelp)
                .add("exit", exit)
                .add("patientToView", patientToView)
                .add("view", view)
                .add("appointmentRange", appointmentRange)
                .add("navigation", navigation)
                .toString();
    }

}
