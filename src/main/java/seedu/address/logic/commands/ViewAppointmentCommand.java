package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.Messages.MESSAGE_VIEW_APPOINTMENT_FROM_AFTER_TILL;
import static seedu.address.logic.Messages.MESSAGE_VIEW_APPOINTMENT_FROM_LISTED_OVERVIEW;
import static seedu.address.logic.Messages.MESSAGE_VIEW_APPOINTMENT_LISTED_OVERVIEW;
import static seedu.address.logic.Messages.MESSAGE_VIEW_APPOINTMENT_RANGE_LISTED_OVERVIEW;
import static seedu.address.logic.Messages.MESSAGE_VIEW_APPOINTMENT_TILL_BEFORE_TODAY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FROM;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TILL;

import java.time.LocalDate;
import java.util.Optional;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.patient.AppointmentBetweenDatesPredicate;

/**
 * Views patients with appointments on a date, or between two dates (inclusive).
 */
public class ViewAppointmentCommand extends Command {

    public static final String COMMAND_WORD = "viewappt";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Views patients with appointments on a date, "
            + "or between two dates (inclusive).\n"
            + "Parameters: DATE, or [" + PREFIX_FROM + "START_DATE] [" + PREFIX_TILL + "END_DATE] "
            + "(dates in yyyy-MM-dd)\n"
            + "Without " + PREFIX_FROM + "the range starts today. Without " + PREFIX_TILL + "it has no end.\n"
            + "Examples: " + COMMAND_WORD + " 2026-10-15, "
            + COMMAND_WORD + " " + PREFIX_FROM + "2026-10-01 " + PREFIX_TILL + "2026-10-31";

    private final Optional<LocalDate> startDate;
    private final Optional<LocalDate> endDate;

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
        requireAllNonNull(startDate, endDate);
        checkArgument(startDate.isEmpty() || endDate.isEmpty() || !startDate.get().isAfter(endDate.get()),
                MESSAGE_VIEW_APPOINTMENT_FROM_AFTER_TILL);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        LocalDate today = LocalDate.now();
        LocalDate start = startDate.orElse(today);
        LocalDate end = endDate.orElse(LocalDate.MAX);
        if (start.isAfter(end)) {
            // Only an end date without a start date can come before the start
            throw new CommandException(String.format(MESSAGE_VIEW_APPOINTMENT_TILL_BEFORE_TODAY, end, today));
        }

        model.updateFilteredPatientList(new AppointmentBetweenDatesPredicate(start, end));
        int count = model.getFilteredPatientList().size();
        if (endDate.isEmpty()) {
            return new CommandResult(String.format(MESSAGE_VIEW_APPOINTMENT_FROM_LISTED_OVERVIEW, count, start));
        }
        if (start.equals(end)) {
            return new CommandResult(String.format(MESSAGE_VIEW_APPOINTMENT_LISTED_OVERVIEW, count, start));
        }
        return new CommandResult(String.format(MESSAGE_VIEW_APPOINTMENT_RANGE_LISTED_OVERVIEW, count, start, end));
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
