package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Represents a Person (a patient) in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person {

    // Identity fields
    private final PatientId id;

    // Data fields
    private final Name name;
    private final Phone phone;
    private final Address address;
    private final MedicalHistory medicalHistory; // null if not given
    private final NextAppointment nextAppointment; // null if not given
    private final Remark remark;

    /**
     * Every field must be present and not null. Optional fields are passed as {@code Optional#empty()}.
     */
    public Person(PatientId id, Name name, Phone phone, Address address,
            Optional<MedicalHistory> medicalHistory, Optional<NextAppointment> nextAppointment, Remark remark) {
        requireAllNonNull(id, name, phone, address, medicalHistory, nextAppointment, remark);
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.medicalHistory = medicalHistory.orElse(null);
        this.nextAppointment = nextAppointment.orElse(null);
        this.remark = remark;
    }

    /**
     * Creates a person with an empty remark.
     * Every field must be present and not null. Optional fields are passed as {@code Optional#empty()}.
     */
    public Person(PatientId id, Name name, Phone phone, Address address,
            Optional<MedicalHistory> medicalHistory, Optional<NextAppointment> nextAppointment) {
        this(id, name, phone, address, medicalHistory, nextAppointment, new Remark(""));
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

    public Remark getRemark() {
        return remark;
    }

    /**
     * Returns true if both persons have the same patient ID.
     * This defines a weaker notion of equality between two persons.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null
                && otherPerson.getId().equals(getId());
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Person otherPerson)) {
            return false;
        }

        return id.equals(otherPerson.id)
                && name.equals(otherPerson.name)
                && phone.equals(otherPerson.phone)
                && address.equals(otherPerson.address)
                && Objects.equals(medicalHistory, otherPerson.medicalHistory)
                && Objects.equals(nextAppointment, otherPerson.nextAppointment)
                && remark.equals(otherPerson.remark);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(id, name, phone, address, medicalHistory, nextAppointment, remark);
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
                .add("remark", remark)
                .toString();
    }

}
