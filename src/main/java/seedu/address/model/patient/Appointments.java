package seedu.address.model.patient;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Represents all the appointments of a Patient, in chronological order and without duplicates.
 * Guarantees: immutable; never contains null
 */
public class Appointments {

    private static final Appointments EMPTY = new Appointments(List.of());

    private final List<Appointment> appointments;

    /**
     * Constructs an {@code Appointments} holding the given appointments.
     * Duplicates are removed and the appointments are sorted.
     */
    public Appointments(Collection<Appointment> appointments) {
        requireNonNull(appointments);
        this.appointments = List.copyOf(new TreeSet<>(appointments));
    }

    /**
     * Returns an {@code Appointments} with no appointments.
     */
    public static Appointments empty() {
        return EMPTY;
    }

    /**
     * Returns a copy of these appointments with {@code appointment} added.
     * Adding an appointment that is already present has no effect.
     */
    public Appointments add(Appointment appointment) {
        requireNonNull(appointment);
        TreeSet<Appointment> updated = new TreeSet<>(appointments);
        updated.add(appointment);
        return new Appointments(updated);
    }

    /**
     * Returns a copy of these appointments with {@code appointment} removed.
     * Removing an appointment that is not present has no effect.
     */
    public Appointments remove(Appointment appointment) {
        requireNonNull(appointment);
        TreeSet<Appointment> updated = new TreeSet<>(appointments);
        updated.remove(appointment);
        return new Appointments(updated);
    }

    /**
     * Returns true if {@code appointment} is one of these appointments.
     */
    public boolean contains(Appointment appointment) {
        return appointments.contains(appointment);
    }

    public boolean isEmpty() {
        return appointments.isEmpty();
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
        if (!(other instanceof Appointments otherAppointments)) {
            return false;
        }

        return appointments.equals(otherAppointments.appointments);
    }

    @Override
    public int hashCode() {
        return appointments.hashCode();
    }

}
