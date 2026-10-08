package seedu.address.model.patient;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Represents a patient in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Patient {

    // Identity fields
    private final PatientId id;

    // Data fields
    private final Name name;
    private final Phone phone;
    private final Address address;
    private final MedicalHistory medicalHistory; // null if not given
    private final NextAppointment nextAppointment; // null if not given

    /**
     * Every field must be present and not null. Optional fields are passed as {@code Optional#empty()}.
     */
    public Patient(PatientId id, Name name, Phone phone, Address address,
            Optional<MedicalHistory> medicalHistory, Optional<NextAppointment> nextAppointment) {
        requireAllNonNull(id, name, phone, address, medicalHistory, nextAppointment);
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.medicalHistory = medicalHistory.orElse(null);
        this.nextAppointment = nextAppointment.orElse(null);
    }

    public PatientId getId() {
        return id;
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Address getAddress() {
        return address;
    }

    /**
     * Returns the medical history, or {@code Optional#empty()} if none was given.
     */
    public Optional<MedicalHistory> getMedicalHistory() {
        return Optional.ofNullable(medicalHistory);
    }

    /**
     * Returns the next appointment, or {@code Optional#empty()} if none was given.
     */
    public Optional<NextAppointment> getNextAppointment() {
        return Optional.ofNullable(nextAppointment);
    }

    /**
     * Returns true if both patients have the same patient ID.
     * This defines a weaker notion of equality between two patients.
     */
    public boolean isSamePatient(Patient otherPatient) {
        if (otherPatient == this) {
            return true;
        }

        return otherPatient != null
                && otherPatient.getId().equals(getId());
    }

    /**
     * Returns true if both patients have the same identity and data fields.
     * This defines a stronger notion of equality between two patients.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Patient otherPatient)) {
            return false;
        }

        return id.equals(otherPatient.id)
                && name.equals(otherPatient.name)
                && phone.equals(otherPatient.phone)
                && address.equals(otherPatient.address)
                && Objects.equals(medicalHistory, otherPatient.medicalHistory)
                && Objects.equals(nextAppointment, otherPatient.nextAppointment);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(id, name, phone, address, medicalHistory, nextAppointment);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("id", id)
                .add("name", name)
                .add("phone", phone)
                .add("address", address)
                .add("medicalHistory", medicalHistory)
                .add("nextAppointment", nextAppointment)
                .toString();
    }

}
