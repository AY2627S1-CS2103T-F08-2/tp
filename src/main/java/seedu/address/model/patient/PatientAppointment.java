package seedu.address.model.patient;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Comparator;
import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Represents one appointment of a patient, as listed in the appointment view.
 * Ordered by appointment, then by the patient's name, then by their ID, so that every patient's appointments
 * can be listed together in one fixed order.
 * Guarantees: immutable; the appointment is one of the patient's appointments.
 */
public class PatientAppointment implements Comparable<PatientAppointment> {

    private static final Comparator<PatientAppointment> ORDER = Comparator
            .comparing(PatientAppointment::getAppointment)
            .thenComparing(entry -> entry.patient.getName().fullName)
            .thenComparing(entry -> entry.patient.getId().value);

    private final Patient patient;
    private final Appointment appointment;

    /**
     * Constructs a {@code PatientAppointment}.
     *
     * @throws IllegalArgumentException if {@code appointment} is not one of {@code patient}'s appointments.
     */
    public PatientAppointment(Patient patient, Appointment appointment) {
        requireAllNonNull(patient, appointment);
        if (!patient.getSchedule().contains(appointment)) {
            throw new IllegalArgumentException("The appointment must be one of the patient's appointments.");
        }
        this.patient = patient;
        this.appointment = appointment;
    }

    public Patient getPatient() {
        return patient;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    /**
     * Returns true if {@code other} is the same appointment of the same patient, even if the patient's other
     * details, or the appointment's details, have changed since.
     */
    public boolean isSameEntry(PatientAppointment other) {
        return other != null
                && patient.getId().equals(other.patient.getId())
                && appointment.isSameAppointment(other.appointment);
    }

    @Override
    public int compareTo(PatientAppointment other) {
        return ORDER.compare(this, other);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof PatientAppointment otherEntry)) {
            return false;
        }

        return patient.equals(otherEntry.patient) && appointment.equals(otherEntry.appointment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(patient, appointment);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("patientId", patient.getId())
                .add("appointment", appointment)
                .toString();
    }
}
