package seedu.address.testutil;

import java.util.Optional;

import seedu.address.model.patient.Address;
import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.AppointmentDetails;
import seedu.address.model.patient.AppointmentSchedule;
import seedu.address.model.patient.MedicalHistory;
import seedu.address.model.patient.Name;
import seedu.address.model.patient.Notes;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientId;
import seedu.address.model.patient.Phone;
import seedu.address.model.util.SampleDataUtil;

/**
 * A utility class to help with building Patient objects.
 */
public class PatientBuilder {

    public static final String DEFAULT_ID = "S9876543Z";
    public static final String DEFAULT_NAME = "Amy Bee";
    public static final String DEFAULT_PHONE = "85355255";
    public static final String DEFAULT_ADDRESS = "123, Jurong West Ave 6, #08-111";

    private PatientId id;
    private Name name;
    private Phone phone;
    private Address address;
    private Optional<MedicalHistory> medicalHistory;
    private Optional<Notes> notes;
    private AppointmentSchedule appointments;
    private boolean isArchived;

    /**
     * Creates a {@code PatientBuilder} with the default details and no optional fields.
     */
    public PatientBuilder() {
        id = new PatientId(DEFAULT_ID);
        name = new Name(DEFAULT_NAME);
        phone = new Phone(DEFAULT_PHONE);
        address = new Address(DEFAULT_ADDRESS);
        medicalHistory = Optional.empty();
        notes = Optional.empty();
        appointments = AppointmentSchedule.empty();
    }

    /**
     * Initializes the PatientBuilder with the data of {@code patientToCopy}.
     */
    public PatientBuilder(Patient patientToCopy) {
        id = patientToCopy.getId();
        name = patientToCopy.getName();
        phone = patientToCopy.getPhone();
        address = patientToCopy.getAddress();
        medicalHistory = patientToCopy.getMedicalHistory();
        notes = patientToCopy.getNotes();
        appointments = patientToCopy.getSchedule();
        isArchived = patientToCopy.isArchived();
    }

    /**
     * Sets the {@code PatientId} of the {@code Patient} that we are building.
     */
    public PatientBuilder withId(String id) {
        this.id = new PatientId(id);
        return this;
    }

    /**
     * Sets the {@code Name} of the {@code Patient} that we are building.
     */
    public PatientBuilder withName(String name) {
        this.name = new Name(name);
        return this;
    }

    /**
     * Sets the {@code Address} of the {@code Patient} that we are building.
     */
    public PatientBuilder withAddress(String address) {
        this.address = new Address(address);
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code Patient} that we are building.
     */
    public PatientBuilder withPhone(String phone) {
        this.phone = new Phone(phone);
        return this;
    }

    /**
     * Sets the {@code MedicalHistory} of the {@code Patient} that we are building.
     */
    public PatientBuilder withMedicalHistory(String medicalHistory) {
        this.medicalHistory = Optional.of(new MedicalHistory(medicalHistory));
        return this;
    }

    /**
     * Removes the {@code MedicalHistory} of the {@code Patient} that we are building.
     */
    public PatientBuilder withoutMedicalHistory() {
        this.medicalHistory = Optional.empty();
        return this;
    }

    /**
     * Sets the {@code Notes} of the {@code Patient} that we are building.
     */
    public PatientBuilder withNotes(String notes) {
        this.notes = Optional.of(new Notes(notes));
        return this;
    }

    /**
     * Removes the {@code Notes} of the {@code Patient} that we are building.
     */
    public PatientBuilder withoutNotes() {
        this.notes = Optional.empty();
        return this;
    }

    /**
     * Sets the {@code AppointmentSchedule} of the {@code Patient} that we are building, to appointments with no
     * details.
     */
    public PatientBuilder withAppointments(String... appointments) {
        this.appointments = SampleDataUtil.getSchedule(appointments);
        return this;
    }

    /**
     * Sets the {@code AppointmentSchedule} of the {@code Patient} that we are building to {@code schedule}.
     */
    public PatientBuilder withSchedule(AppointmentSchedule schedule) {
        this.appointments = schedule;
        return this;
    }

    /**
     * Adds an appointment at {@code appointment}, a date or a date and time, with {@code details} to the
     * {@code AppointmentSchedule} of the {@code Patient} that we are building.
     */
    public PatientBuilder withAppointment(String appointment, String details) {
        this.appointments = appointments.add(
                new Appointment(appointment, Optional.of(new AppointmentDetails(details))));
        return this;
    }

    /**
     * Sets whether the {@code Patient} that we are building is archived.
     */
    public PatientBuilder withArchived(boolean isArchived) {
        this.isArchived = isArchived;
        return this;
    }

    public Patient build() {
        return new Patient(id, name, phone, address, medicalHistory, notes, appointments, isArchived);
    }

}
