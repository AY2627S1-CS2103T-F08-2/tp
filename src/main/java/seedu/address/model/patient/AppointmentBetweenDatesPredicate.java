package seedu.address.model.patient;

import java.time.LocalDate;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Patient} has an appointment within a given date range (inclusive).
 */
public class AppointmentBetweenDatesPredicate implements Predicate<Patient> {
    private final LocalDate startDate;
    private final LocalDate endDate;

    /**
     * Creates an {@code AppointmentBetweenDatesPredicate} to filter patients by appointment date range.
     *
     * @param startDate Start of date range (inclusive)
     * @param endDate   End of date range (inclusive)
     */
    public AppointmentBetweenDatesPredicate(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public boolean test(Patient patient) {
        return patient.getNextAppointment()
                .map(a -> !a.getDate().isBefore(startDate) && !a.getDate().isAfter(endDate))
                .orElse(false);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AppointmentBetweenDatesPredicate otherAppointmentBetweenDatesPredicate)) {
            return false;
        }

        return startDate.equals(otherAppointmentBetweenDatesPredicate.startDate)
                && endDate.equals(otherAppointmentBetweenDatesPredicate.endDate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("startDate", startDate)
                .add("endDate", endDate)
                .toString();
    }
}
