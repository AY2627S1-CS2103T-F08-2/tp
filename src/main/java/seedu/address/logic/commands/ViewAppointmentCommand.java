package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_PATIENT_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FROM;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TO;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import seedu.address.commons.util.DateUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.CommandResult.Navigation;
import seedu.address.logic.commands.CommandResult.View;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.AppointmentBetweenDatesPredicate;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientId;

/**
 * Views the appointments on a date, or between two dates (inclusive), in the appointment view, and lists the
 * patients who have one. With {@code --id}, only that patient's appointments are shown. Without a date, it shows
 * the weekly view of this week, or with {@code --id}, every appointment of that patient. In each case, the next
 * appointment from now is selected.
 */
public class ViewAppointmentCommand extends Command {

    public static final String COMMAND_WORD = "viewappt";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Shows the appointments on a date, or within a date "
            + "range, in the appointment view, and lists the patients who have one, in name order. Without a date "
            + "or range, shows this week, or with " + PREFIX_ID + "alone, all of that patient's appointments. "
            + "A DATE must come before other options.\n"
            + "Parameters: [DATE] or [" + PREFIX_FROM + "START_DATE] [" + PREFIX_TO + "END_DATE], with an optional ["
            + PREFIX_ID + "ID] (dates in yyyy-MM-dd)\n"
            + "Example: " + COMMAND_WORD + " 2026-10-15";

    public static final String MESSAGE_VIEW_APPOINTMENT_LISTED_OVERVIEW = "%1$d patient(s) with appointments on %2$s";
    public static final String MESSAGE_VIEW_APPOINTMENT_RANGE_LISTED_OVERVIEW =
            "%1$d patient(s) with appointments from %2$s to %3$s";
    public static final String MESSAGE_VIEW_APPOINTMENT_FROM_LISTED_OVERVIEW =
            "%1$d patient(s) with appointments on or after %2$s";
    public static final String MESSAGE_VIEW_APPOINTMENT_WEEK_LISTED_OVERVIEW =
            "%1$d patient(s) with appointments this week, from %2$s to %3$s";
    public static final String MESSAGE_VIEW_APPOINTMENT_PATIENT_ALL_LISTED_OVERVIEW = "%1$d appointment(s) of %2$s";
    public static final String MESSAGE_VIEW_APPOINTMENT_PATIENT_LISTED_OVERVIEW =
            "%1$d appointment(s) of %2$s on %3$s";
    public static final String MESSAGE_VIEW_APPOINTMENT_PATIENT_RANGE_LISTED_OVERVIEW =
            "%1$d appointment(s) of %2$s from %3$s to %4$s";
    public static final String MESSAGE_VIEW_APPOINTMENT_PATIENT_FROM_LISTED_OVERVIEW =
            "%1$d appointment(s) of %2$s on or after %3$s";
    public static final String MESSAGE_VIEW_APPOINTMENT_TO_BEFORE_TODAY =
            "The --to date %1$s is before today (%2$s). To see past appointments, also give --from.";
    public static final String MESSAGE_VIEW_APPOINTMENT_FROM_AFTER_TO =
            "The --from date must not be after the --to date.";
    public static final String MESSAGE_VIEW_APPOINTMENT_DATE_AFTER_OPTIONS =
            "A DATE must come before other options, for example: viewappt 2026-10-15 --id S1234567A";

    private final Optional<LocalDate> startDate;
    private final Optional<LocalDate> endDate;
    private final Optional<PatientId> patientId; // narrows the appointments and the patient list to one patient
    private final Clock clock; // gives today's date when no start date is given

    /**
     * Creates a ViewAppointmentCommand to view appointments on a single date.
     */
    public ViewAppointmentCommand(LocalDate date) {
        this(Optional.of(date), Optional.of(date), Optional.empty());
    }

    /**
     * Creates a ViewAppointmentCommand to view appointments between two dates (inclusive).
     *
     * @param startDate First date of the range, or {@code Optional#empty()} to start on the day the command runs.
     * @param endDate Last date of the range, or {@code Optional#empty()} for a range with no end.
     * @throws IllegalArgumentException if both dates are given and {@code startDate} is after {@code endDate}.
     */
    public ViewAppointmentCommand(Optional<LocalDate> startDate, Optional<LocalDate> endDate) {
        this(startDate, endDate, Optional.empty());
    }

    /**
     * Creates a ViewAppointmentCommand to view one patient's appointments between two dates (inclusive).
     *
     * @param patientId The patient whose appointments are shown, or {@code Optional#empty()} for every patient's.
     */
    public ViewAppointmentCommand(Optional<LocalDate> startDate, Optional<LocalDate> endDate,
            Optional<PatientId> patientId) {
        this(startDate, endDate, patientId, Clock.systemDefaultZone());
    }

    /**
     * Creates a ViewAppointmentCommand that reads today's date from {@code clock}, so that tests can fix the date.
     */
    ViewAppointmentCommand(Optional<LocalDate> startDate, Optional<LocalDate> endDate, Clock clock) {
        this(startDate, endDate, Optional.empty(), clock);
    }

    /**
     * Creates a ViewAppointmentCommand that reads today's date from {@code clock}, so that tests can fix the date.
     */
    ViewAppointmentCommand(Optional<LocalDate> startDate, Optional<LocalDate> endDate,
            Optional<PatientId> patientId, Clock clock) {
        requireAllNonNull(startDate, endDate, patientId, clock);
        checkArgument(startDate.isEmpty() || endDate.isEmpty() || !startDate.get().isAfter(endDate.get()),
                MESSAGE_VIEW_APPOINTMENT_FROM_AFTER_TO);
        this.startDate = startDate;
        this.endDate = endDate;
        this.patientId = patientId;
        this.clock = clock;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        LocalDate today = LocalDate.now(clock);
        boolean hasDates = startDate.isPresent() || endDate.isPresent();
        if (!hasDates) {
            return patientId.isEmpty() ? showThisWeek(model, today) : showAllOfPatient(model, patientId.get(), today);
        }
        LocalDate start = startDate.orElse(today);
        LocalDate end = endDate.orElse(LocalDate.MAX);
        if (start.isAfter(end)) {
            // Only an end date without a start date can come before the start
            throw new CommandException(String.format(MESSAGE_VIEW_APPOINTMENT_TO_BEFORE_TODAY, end, today));
        }

        AppointmentBetweenDatesPredicate inRange = new AppointmentBetweenDatesPredicate(start, end);
        if (patientId.isEmpty()) {
            model.updateFilteredPatientList(inRange);
            int count = model.getFilteredPatientList().size();
            return new CommandResult(describeAll(count, start, end), new CommandResult.AppointmentRange(start, end))
                    .withNavigation(Navigation.toView(View.APPOINTMENTS));
        }

        PatientId id = patientId.get();
        Patient patient = model.findPatientById(id)
                .orElseThrow(() -> new CommandException(MESSAGE_INVALID_PATIENT_ID));
        // The patient list mirrors the appointment view: only this patient, and only with an appointment in range
        model.updateFilteredPatientList(candidate -> candidate.getId().equals(id) && inRange.test(candidate));
        long count = patient.getSchedule().asList().stream()
                .filter(appointment -> inRange.isWithinRange(appointment.getDate()))
                .count();
        return new CommandResult(describeForPatient(count, patient, start, end),
                new CommandResult.AppointmentRange(start, end, id))
                .withNavigation(Navigation.toView(View.APPOINTMENTS));
    }

    /**
     * Shows the weekly view of this week, from Monday to Sunday, and lists the patients with an appointment in it.
     * The result has no range, so the appointment view shows the weekly view, not a page of a range. The week comes
     * from {@code DateUtil}, as the weekly view's does, so the patient list matches the week shown.
     */
    private CommandResult showThisWeek(Model model, LocalDate today) {
        LocalDate monday = DateUtil.getStartOfWeek(today);
        LocalDate sunday = DateUtil.getEndOfWeek(today);
        model.updateFilteredPatientList(new AppointmentBetweenDatesPredicate(monday, sunday));
        int count = model.getFilteredPatientList().size();
        return new CommandResult(String.format(MESSAGE_VIEW_APPOINTMENT_WEEK_LISTED_OVERVIEW, count, monday, sunday))
                .withNavigation(Navigation.toView(View.APPOINTMENTS));
    }

    /**
     * Shows every appointment of the patient with {@code id}, past and upcoming, on one page, and lists only
     * that patient, if they have an appointment. A patient with none gets an empty page of today.
     */
    private CommandResult showAllOfPatient(Model model, PatientId id, LocalDate today) throws CommandException {
        Patient patient = model.findPatientById(id)
                .orElseThrow(() -> new CommandException(MESSAGE_INVALID_PATIENT_ID));
        List<Appointment> appointments = patient.getSchedule().asList(); // in time order
        LocalDate first = appointments.isEmpty() ? today : appointments.get(0).getDate();
        LocalDate last = appointments.isEmpty() ? today : appointments.get(appointments.size() - 1).getDate();
        model.updateFilteredPatientList(candidate -> candidate.getId().equals(id)
                && !candidate.getSchedule().isEmpty());
        return new CommandResult(String.format(MESSAGE_VIEW_APPOINTMENT_PATIENT_ALL_LISTED_OVERVIEW,
                appointments.size(), patient.getName().fullName), new CommandResult.AppointmentRange(first, last, id))
                .withNavigation(Navigation.toView(View.APPOINTMENTS));
    }

    /** Returns the status message for the appointments of every patient in the range. */
    private String describeAll(int count, LocalDate start, LocalDate end) {
        if (endDate.isEmpty()) {
            return String.format(MESSAGE_VIEW_APPOINTMENT_FROM_LISTED_OVERVIEW, count, start);
        }
        if (start.equals(end)) {
            return String.format(MESSAGE_VIEW_APPOINTMENT_LISTED_OVERVIEW, count, start);
        }
        return String.format(MESSAGE_VIEW_APPOINTMENT_RANGE_LISTED_OVERVIEW, count, start, end);
    }

    /** Returns the status message for the appointments of one patient in the range. */
    private String describeForPatient(long count, Patient patient, LocalDate start, LocalDate end) {
        String name = patient.getName().fullName;
        if (endDate.isEmpty()) {
            return String.format(MESSAGE_VIEW_APPOINTMENT_PATIENT_FROM_LISTED_OVERVIEW, count, name, start);
        }
        if (start.equals(end)) {
            return String.format(MESSAGE_VIEW_APPOINTMENT_PATIENT_LISTED_OVERVIEW, count, name, start);
        }
        return String.format(MESSAGE_VIEW_APPOINTMENT_PATIENT_RANGE_LISTED_OVERVIEW, count, name, start, end);
    }

    @Override
    public Optional<CommandResult.View> getView() {
        return Optional.of(CommandResult.View.APPOINTMENTS);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ViewAppointmentCommand otherViewAppointmentCommand)) {
            return false;
        }

        // The clock is not compared, as it is not part of what the user asked for
        return startDate.equals(otherViewAppointmentCommand.startDate)
                && endDate.equals(otherViewAppointmentCommand.endDate)
                && patientId.equals(otherViewAppointmentCommand.patientId);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("startDate", startDate)
                .add("endDate", endDate)
                .add("patientId", patientId)
                .toString();
    }
}
