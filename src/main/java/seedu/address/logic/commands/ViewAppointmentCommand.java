package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.Messages.MESSAGE_VIEW_APPOINTMENT_FROM_AFTER_TO;
import static seedu.address.logic.Messages.MESSAGE_VIEW_APPOINTMENT_FROM_LISTED_OVERVIEW;
import static seedu.address.logic.Messages.MESSAGE_VIEW_APPOINTMENT_LISTED_OVERVIEW;
import static seedu.address.logic.Messages.MESSAGE_VIEW_APPOINTMENT_RANGE_LISTED_OVERVIEW;
import static seedu.address.logic.Messages.MESSAGE_VIEW_APPOINTMENT_TO_BEFORE_TODAY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FROM;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TO;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Optional;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.patient.AppointmentBetweenDatesPredicate;
import seedu.address.model.patient.PatientAppointment;

/**
 * Views the appointments on a date, or between two dates (inclusive), in the appointment view, and lists the
 * patients who have one.
 */
public class ViewAppointmentCommand extends Command {

    public static final String COMMAND_WORD = "viewappt";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Shows the appointments on a date, "
            + "or between two dates (inclusive), in the appointment view, and lists the patients who have one.\n"
            + "Parameters: DATE, or [" + PREFIX_FROM + "START_DATE] [" + PREFIX_TO + "END_DATE] "
            + "(dates in yyyy-MM-dd)\n"
            + "Give a DATE, or at least one of " + PREFIX_FROM + "and " + PREFIX_TO.getPrefix().trim() + ". "
            + "Without " + PREFIX_FROM + "the range starts today. Without " + PREFIX_TO + "it has no end.\n"
            + "Examples: " + COMMAND_WORD + " 2026-10-15, "
            + COMMAND_WORD + " " + PREFIX_FROM + "2026-10-01 " + PREFIX_TO + "2026-10-31";

    private final Optional<LocalDate> startDate;
    private final Optional<LocalDate> endDate;
    private final Clock clock; // gives today's date when no start date is given

    /**
     * Creates a ViewAppointmentCommand to view appointments on a single date.
     */
    public ViewAppointmentCommand(LocalDate date) {
        this(Optional.of(date), Optional.of(date));
    }

    /**
     * Creates a ViewAppointmentCommand to view appointments between two dates (inclusive).
     *
     * @param startDate First date of the range, or {@code Optional#empty()} to start on the day the command runs.
     * @param endDate Last date of the range, or {@code Optional#empty()} for a range with no end.
     * @throws IllegalArgumentException if both dates are given and {@code startDate} is after {@code endDate}.
     */
    public ViewAppointmentCommand(Optional<LocalDate> startDate, Optional<LocalDate> endDate) {
        this(startDate, endDate, Clock.systemDefaultZone());
    }

    /**
     * Creates a ViewAppointmentCommand that reads today's date from {@code clock}, so that tests can fix the date.
     */
    ViewAppointmentCommand(Optional<LocalDate> startDate, Optional<LocalDate> endDate, Clock clock) {
        requireAllNonNull(startDate, endDate, clock);
        checkArgument(startDate.isEmpty() || endDate.isEmpty() || !startDate.get().isAfter(endDate.get()),
                MESSAGE_VIEW_APPOINTMENT_FROM_AFTER_TO);
        this.startDate = startDate;
        this.endDate = endDate;
        this.clock = clock;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        LocalDate today = LocalDate.now(clock);
        LocalDate start = startDate.orElse(today);
        LocalDate end = endDate.orElse(LocalDate.MAX);
        if (start.isAfter(end)) {
            // Only an end date without a start date can come before the start
            throw new CommandException(String.format(MESSAGE_VIEW_APPOINTMENT_TO_BEFORE_TODAY, end, today));
        }

        AppointmentBetweenDatesPredicate predicate = new AppointmentBetweenDatesPredicate(start, end);
        model.updateFilteredPatientList(predicate);
        int count = model.getFilteredPatientList().size();
        String message;
        if (endDate.isEmpty()) {
            message = String.format(MESSAGE_VIEW_APPOINTMENT_FROM_LISTED_OVERVIEW, count, start);
        } else if (start.equals(end)) {
            message = String.format(MESSAGE_VIEW_APPOINTMENT_LISTED_OVERVIEW, count, start);
        } else {
            message = String.format(MESSAGE_VIEW_APPOINTMENT_RANGE_LISTED_OVERVIEW, count, start, end);
        }

        // The appointment view opens at the first appointment in the range, or at the start if there is none
        Optional<PatientAppointment> firstInRange = model.getAppointmentList().stream()
                .filter(entry -> predicate.isInRange(entry.getAppointment()))
                .findFirst();
        LocalDate dateToShow = firstInRange.map(entry -> entry.getAppointment().getDate()).orElse(start);
        return new CommandResult(message, dateToShow, firstInRange);
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
                && endDate.equals(otherViewAppointmentCommand.endDate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("startDate", startDate)
                .add("endDate", endDate)
                .toString();
    }
}
