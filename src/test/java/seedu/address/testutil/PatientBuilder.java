package seedu.address.testutil;

import java.util.Optional;

import seedu.address.model.patient.Address;
import seedu.address.model.patient.MedicalHistory;
import seedu.address.model.patient.Name;
import seedu.address.model.patient.NextAppointment;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientId;
import seedu.address.model.patient.Phone;

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
    private Optional<NextAppointment> nextAppointment;

    /**
     * Creates a {@code PatientBuilder} with the default details and no optional fields.
     */
    public PatientBuilder() {
        id = new PatientId(DEFAULT_ID);
        name = new Name(DEFAULT_NAME);
        phone = new Phone(DEFAULT_PHONE);
        address = new Address(DEFAULT_ADDRESS);
        medicalHistory = Optional.empty();
        nextAppointment = Optional.empty();
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
        nextAppointment = patientToCopy.getNextAppointment();
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
     * Sets the {@code NextAppointment} of the {@code Patient} that we are building.
     */
    public PatientBuilder withNextAppointment(String nextAppointment) {
        this.nextAppointment = Optional.of(new NextAppointment(nextAppointment));
        return this;
    }

    public Patient build() {
        return new Patient(id, name, phone, address, medicalHistory, nextAppointment);
    }

}
