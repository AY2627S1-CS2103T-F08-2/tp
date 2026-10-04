package seedu.address.storage;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Address;
import seedu.address.model.person.MedicalHistory;
import seedu.address.model.person.Name;
import seedu.address.model.person.NextAppointment;
import seedu.address.model.person.PatientId;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;

/**
 * Jackson-friendly version of {@link Person}.
 * Optional fields that are not set are left out of the JSON file.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
class JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Person's %s field is missing!";

    private final String id;
    private final String name;
    private final String phone;
    private final String address;
    private final String medicalHistory; // null if not set
    private final String nextAppointment; // null if not set

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     */
    @JsonCreator
    public JsonAdaptedPerson(@JsonProperty("id") String id, @JsonProperty("name") String name,
            @JsonProperty("phone") String phone, @JsonProperty("address") String address,
            @JsonProperty("medicalHistory") String medicalHistory,
            @JsonProperty("nextAppointment") String nextAppointment) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.medicalHistory = medicalHistory;
        this.nextAppointment = nextAppointment;
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        id = source.getId().value;
        name = source.getName().fullName;
        phone = source.getPhone().value;
        address = source.getAddress().value;
        medicalHistory = source.getMedicalHistory().map(history -> history.value).orElse(null);
        nextAppointment = source.getNextAppointment().map(appointment -> appointment.value).orElse(null);
    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's {@code Person} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person.
     */
    public Person toModelType() throws IllegalValueException {
        if (id == null) {
            throw new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, PatientId.class.getSimpleName()));
        }
        if (!PatientId.isValidId(id)) {
            throw new IllegalValueException(PatientId.MESSAGE_CONSTRAINTS);
        }
        final PatientId modelId = new PatientId(id);

        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(name);

        if (phone == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName()));
        }
        if (!Phone.isValidPhone(phone)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        final Phone modelPhone = new Phone(phone);

        if (address == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Address.class.getSimpleName()));
        }
        if (!Address.isValidAddress(address)) {
            throw new IllegalValueException(Address.MESSAGE_CONSTRAINTS);
        }
        final Address modelAddress = new Address(address);

        if (medicalHistory != null && !MedicalHistory.isValidMedicalHistory(medicalHistory)) {
            throw new IllegalValueException(MedicalHistory.MESSAGE_CONSTRAINTS);
        }
        final Optional<MedicalHistory> modelMedicalHistory =
                Optional.ofNullable(medicalHistory).map(MedicalHistory::new);

        if (nextAppointment != null && !NextAppointment.isValidNextAppointment(nextAppointment)) {
            throw new IllegalValueException(NextAppointment.MESSAGE_CONSTRAINTS);
        }
        final Optional<NextAppointment> modelNextAppointment =
                Optional.ofNullable(nextAppointment).map(NextAppointment::new);

        return new Person(modelId, modelName, modelPhone, modelAddress, modelMedicalHistory, modelNextAppointment);
    }

}
