package seedu.address.model.patient;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Represents all the appointments of a Patient, in chronological order, with at most one at each date and time.
 * Two appointments at the same date and time are the same appointment, whatever their details, as
 * {@link Appointment#isSameAppointment(Appointment)} gives.
 * Guarantees: immutable; never contains null
 */
public class AppointmentSchedule {

    private static final AppointmentSchedule EMPTY = new AppointmentSchedule(List.of());

    private final List<Appointment> appointments;

    /**
     * Constructs an {@code AppointmentSchedule} holding the given appointments, sorted.
     * Of several appointments at the same date and time, only the first is kept.
     */
    public AppointmentSchedule(Collection<Appointment> appointments) {
        requireNonNull(appointments);
        this.appointments = List.copyOf(new TreeSet<>(appointments));
    }

    /**
     * Returns an {@code AppointmentSchedule} with no appointments.
     */
    public static AppointmentSchedule empty() {
        return EMPTY;
    }

    /**
     * Returns a copy of these appointments with {@code appointment} added.
     * Adding an appointment at a date and time that already has one has no effect, whatever their details.
     */
    public AppointmentSchedule add(Appointment appointment) {
        requireNonNull(appointment);
        TreeSet<Appointment> updated = new TreeSet<>(appointments);
        updated.add(appointment);
        return new AppointmentSchedule(updated);
    }

    /**
     * Returns a copy of these appointments without the one at the date and time of {@code appointment}, whatever
     * its details. Removing an appointment that is not present has no effect.
     */
    public AppointmentSchedule remove(Appointment appointment) {
        requireNonNull(appointment);
        TreeSet<Appointment> updated = new TreeSet<>(appointments);
        updated.remove(appointment);
        return new AppointmentSchedule(updated);
    }

    /**
     * Returns true if one of these appointments is at the date and time of {@code appointment}, whatever its
     * details.
     */
    public boolean contains(Appointment appointment) {
        requireNonNull(appointment);
        return appointments.stream().anyMatch(appointment::isSameAppointment);
    }

    /**
     * Returns true if there are no appointments.
     */
    public boolean isEmpty() {
        return appointments.isEmpty();
    }

    /**
     * Returns the earliest appointment on or after {@code date}, or {@code Optional#empty()} if there is none.
     * Only dates are compared, so every appointment on {@code date} counts, whatever its time.
     */
    public Optional<Appointment> getNextOnOrAfter(LocalDate date) {
        requireNonNull(date);
        return appointments.stream().filter(appointment -> !appointment.getDate().isBefore(date)).findFirst();
    }

    /**
     * Returns the appointments as an unmodifiable list in chronological order.
     */
    public List<Appointment> asList() {
        return Collections.unmodifiableList(appointments);
    }

    @Override
    public String toString() {
        return appointments.stream().map(Appointment::toString).collect(Collectors.joining(", "));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AppointmentSchedule otherSchedule)) {
            return false;
        }

        return appointments.equals(otherSchedule.appointments);
    }

    @Override
    public int hashCode() {
        return appointments.hashCode();
    }

}
