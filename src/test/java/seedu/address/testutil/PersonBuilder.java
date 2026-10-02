package seedu.address.testutil;

import java.util.Optional;

import seedu.address.model.person.Address;
import seedu.address.model.person.MedicalHistory;
import seedu.address.model.person.Name;
import seedu.address.model.person.NextAppointment;
import seedu.address.model.person.PatientId;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;

/**
 * A utility class to help with building Person objects.
 */
public class PersonBuilder {

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
     * Creates a {@code PersonBuilder} with the default details and no optional fields.
     */
    public PersonBuilder() {
        id = new PatientId(DEFAULT_ID);
        name = new Name(DEFAULT_NAME);
        phone = new Phone(DEFAULT_PHONE);
        address = new Address(DEFAULT_ADDRESS);
        medicalHistory = Optional.empty();
        nextAppointment = Optional.empty();
    }

    /**
     * Initializes the PersonBuilder with the data of {@code personToCopy}.
     */
    public PersonBuilder(Person personToCopy) {
        id = personToCopy.getId();
        name = personToCopy.getName();
        phone = personToCopy.getPhone();
        address = personToCopy.getAddress();
        medicalHistory = personToCopy.getMedicalHistory();
        nextAppointment = personToCopy.getNextAppointment();
    }

    /**
     * Sets the {@code PatientId} of the {@code Person} that we are building.
     */
    public PersonBuilder withId(String id) {
        this.id = new PatientId(id);
        return this;
    }

    /**
     * Sets the {@code Name} of the {@code Person} that we are building.
     */
    public PersonBuilder withName(String name) {
        this.name = new Name(name);
        return this;
    }

    /**
     * Sets the {@code Address} of the {@code Person} that we are building.
     */
    public PersonBuilder withAddress(String address) {
        this.address = new Address(address);
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code Person} that we are building.
     */
    public PersonBuilder withPhone(String phone) {
        this.phone = new Phone(phone);
        return this;
    }

    /**
     * Sets the {@code MedicalHistory} of the {@code Person} that we are building.
     */
    public PersonBuilder withMedicalHistory(String medicalHistory) {
        this.medicalHistory = Optional.of(new MedicalHistory(medicalHistory));
        return this;
    }

    /**
     * Sets the {@code NextAppointment} of the {@code Person} that we are building.
     */
    public PersonBuilder withNextAppointment(String nextAppointment) {
        this.nextAppointment = Optional.of(new NextAppointment(nextAppointment));
        return this;
    }

    public Person build() {
        return new Person(id, name, phone, address, medicalHistory, nextAppointment);
    }

}
