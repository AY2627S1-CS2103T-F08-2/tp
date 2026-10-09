package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_VIEW_APPOINTMENT_LISTED_OVERVIEW;

import java.time.LocalDate;
import java.util.List;

import seedu.address.model.Model;
import seedu.address.model.patient.Patient;

/**
 * Views all patients with appointments on the specified date.
 */
public class ViewAppointmentCommand extends Command {

    public static final String COMMAND_WORD = "viewappt";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Views all patients with appointments on "
            + "the specified date.\n"
            + "Parameters: DATE\n"
            + "Example: " + COMMAND_WORD + " 2026-10-15";

    private final LocalDate date;

    /**
     * Creates a ViewAppointmentCommand to view appointments on a single date.
     */
    public ViewAppointmentCommand(LocalDate date) {
        requireNonNull(date);
        this.date = date;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        List<Patient> patients = model.findPatientsWithAppointmentOn(date, date);
        return new CommandResult(
                String.format(MESSAGE_VIEW_APPOINTMENT_LISTED_OVERVIEW, patients.size(), date.toString()));
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

        return date.equals(otherViewAppointmentCommand.date);
    }

    @Override
    public String toString() {
        return "ViewAppointmentCommand{" + "date=" + date + '}';
    }
}
