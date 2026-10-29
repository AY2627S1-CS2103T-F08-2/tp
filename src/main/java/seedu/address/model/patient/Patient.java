package seedu.address.model.patient;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Represents a patient in LaiNurse. An archived patient, such as one who has been discharged, keeps every detail, but
 * is hidden from the patient list and the appointment view unless they show archived patients, and their
 * appointments never clash.
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
    private final Notes notes; // null if not given
    private final AppointmentSchedule schedule;
    private final boolean isArchived;

    /**
     * Every field must be present and not null. An absent medical history or absent notes are passed as
     * {@code Optional#empty()}, and a patient with no appointments is given {@link AppointmentSchedule#empty()}.
     * The patient is not archived.
     */
    public Patient(PatientId id, Name name, Phone phone, Address address,
            Optional<MedicalHistory> medicalHistory, Optional<Notes> notes, AppointmentSchedule schedule) {
        this(id, name, phone, address, medicalHistory, notes, schedule, false);
    }

    /**
     * Every field must be present and not null, as for the constructor above, and the patient is archived if
     * {@code isArchived}.
     */
    public Patient(PatientId id, Name name, Phone phone, Address address, Optional<MedicalHistory> medicalHistory,
            Optional<Notes> notes, AppointmentSchedule schedule, boolean isArchived) {
        requireAllNonNull(id, name, phone, address, medicalHistory, notes, schedule);
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.medicalHistory = medicalHistory.orElse(null);
        this.notes = notes.orElse(null);
        this.schedule = schedule;
        this.isArchived = isArchived;
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
     * Returns the notes, or {@code Optional#empty()} if none were given.
     */
    public Optional<Notes> getNotes() {
        return Optional.ofNullable(notes);
    }

    /**
     * Returns the patient's appointments, in chronological order.
     */
    public AppointmentSchedule getSchedule() {
        return schedule;
    }

    /**
     * Returns true if the patient is archived.
     */
    public boolean isArchived() {
        return isArchived;
    }

    /**
     * Returns a copy of this patient with {@code schedule} in place of their appointments, and every other detail kept,
     * also whether they are archived.
     */
    public Patient withSchedule(AppointmentSchedule schedule) {
        return new Patient(id, name, phone, address, getMedicalHistory(), getNotes(), schedule, isArchived);
    }

    /**
     * Returns a copy of this patient that is archived if {@code isArchived}, with every other detail kept.
     */
    public Patient withArchived(boolean isArchived) {
        return new Patient(id, name, phone, address, getMedicalHistory(), getNotes(), schedule, isArchived);
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
                && Objects.equals(notes, otherPatient.notes)
                && schedule.equals(otherPatient.schedule)
                && isArchived == otherPatient.isArchived;
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(id, name, phone, address, medicalHistory, notes, schedule, isArchived);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("id", id)
                .add("name", name)
                .add("phone", phone)
                .add("address", address)
                .add("medicalHistory", medicalHistory)
                .add("notes", notes)
                .add("schedule", schedule)
                .add("isArchived", isArchived)
                .toString();
    }

}
