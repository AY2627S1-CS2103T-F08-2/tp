package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.util.ToStringBuilder;
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
         * Constructs an {@code AppointmentRange} with the appointments of every patient in it.
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

    /**
     * Constructs a {@code CommandResult} with the specified fields.
     */
    public CommandResult(String feedbackToUser, boolean showHelp, boolean exit) {
        this(feedbackToUser, showHelp, exit, null, null, null);
    }

    /**
     * Constructs a {@code CommandResult} that asks for the details of the patient with {@code patientToView}
     * to be shown, with other fields set to their default value.
     */
    public CommandResult(String feedbackToUser, PatientId patientToView) {
        this(feedbackToUser, false, false, requireNonNull(patientToView), null, null);
    }

    /**
     * Constructs a {@code CommandResult} that asks the appointment view to show the appointments in
     * {@code appointmentRange} on one page, with other fields set to their default value.
     */
    public CommandResult(String feedbackToUser, AppointmentRange appointmentRange) {
        this(feedbackToUser, false, false, null, null, requireNonNull(appointmentRange));
    }

    /**
     * Constructs a {@code CommandResult} with the specified {@code feedbackToUser},
     * and other fields set to their default value.
     */
    public CommandResult(String feedbackToUser) {
        this(feedbackToUser, false, false, null, null, null);
    }

    private CommandResult(String feedbackToUser, boolean showHelp, boolean exit, PatientId patientToView,
            View view, AppointmentRange appointmentRange) {
        this.feedbackToUser = requireNonNull(feedbackToUser);
        this.showHelp = showHelp;
        this.exit = exit;
        this.patientToView = patientToView;
        this.view = view;
        this.appointmentRange = appointmentRange;
    }

    /**
     * Returns a copy of this result that asks the main window to switch to {@code view}.
     */
    public CommandResult withView(View view) {
        requireNonNull(view);
        return new CommandResult(feedbackToUser, showHelp, exit, patientToView, view, appointmentRange);
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
                && Objects.equals(appointmentRange, otherCommandResult.appointmentRange);
    }

    @Override
    public int hashCode() {
        return Objects.hash(feedbackToUser, showHelp, exit, patientToView, view, appointmentRange);
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
                .toString();
    }

}
