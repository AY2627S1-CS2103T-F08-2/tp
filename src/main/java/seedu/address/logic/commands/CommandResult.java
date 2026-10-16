package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

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
        /** The appointments of one week, day by day, and the details of one appointment's patient. */
        APPOINTMENTS
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

    /** A date in the week that the appointment view should show, or null to keep the week it shows. */
    private final LocalDate appointmentDateToShow;

    /** The appointment to select in that week, or null to select none. Only set with a date to show. */
    private final PatientAppointment appointmentToSelect;

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
     * Constructs a {@code CommandResult} that asks the appointment view to show the week of
     * {@code appointmentDateToShow}, and to select {@code appointmentToSelect} if it is given,
     * with other fields set to their default value.
     */
    public CommandResult(String feedbackToUser, LocalDate appointmentDateToShow,
            Optional<PatientAppointment> appointmentToSelect) {
        this(feedbackToUser, false, false, null, null, requireNonNull(appointmentDateToShow),
                requireNonNull(appointmentToSelect).orElse(null));
    }

    /**
     * Constructs a {@code CommandResult} with the specified {@code feedbackToUser},
     * and other fields set to their default value.
     */
    public CommandResult(String feedbackToUser) {
        this(feedbackToUser, false, false, null, null, null, null);
    }

    private CommandResult(String feedbackToUser, boolean showHelp, boolean exit, PatientId patientToView,
            View view, LocalDate appointmentDateToShow, PatientAppointment appointmentToSelect) {
        this.feedbackToUser = requireNonNull(feedbackToUser);
        this.showHelp = showHelp;
        this.exit = exit;
        this.patientToView = patientToView;
        this.view = view;
        this.appointmentDateToShow = appointmentDateToShow;
        this.appointmentToSelect = appointmentToSelect;
    }

    /**
     * Returns a copy of this result that asks the main window to switch to {@code view}.
     */
    public CommandResult withView(View view) {
        requireNonNull(view);
        return new CommandResult(feedbackToUser, showHelp, exit, patientToView, view, appointmentDateToShow,
                appointmentToSelect);
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
     * Returns a date in the week that the appointment view should show, or {@code Optional#empty()} if the
     * shown week should not change.
     */
    public Optional<LocalDate> getAppointmentDateToShow() {
        return Optional.ofNullable(appointmentDateToShow);
    }

    /**
     * Returns the appointment to select in the appointment view, or {@code Optional#empty()} to select none.
     */
    public Optional<PatientAppointment> getAppointmentToSelect() {
        return Optional.ofNullable(appointmentToSelect);
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
                && Objects.equals(appointmentDateToShow, otherCommandResult.appointmentDateToShow)
                && Objects.equals(appointmentToSelect, otherCommandResult.appointmentToSelect);
    }

    @Override
    public int hashCode() {
        return Objects.hash(feedbackToUser, showHelp, exit, patientToView, view, appointmentDateToShow,
                appointmentToSelect);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("feedbackToUser", feedbackToUser)
                .add("showHelp", showHelp)
                .add("exit", exit)
                .add("patientToView", patientToView)
                .add("view", view)
                .add("appointmentDateToShow", appointmentDateToShow)
                .add("appointmentToSelect", appointmentToSelect)
                .toString();
    }

}
