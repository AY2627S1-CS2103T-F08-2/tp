package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BooleanNode;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.patient.Address;
import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.AppointmentSchedule;
import seedu.address.model.patient.MedicalHistory;
import seedu.address.model.patient.Name;
import seedu.address.model.patient.Notes;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientId;
import seedu.address.model.patient.Phone;

/**
 * Jackson-friendly version of {@link Patient}.
 * Optional fields that are not set are left out of the JSON file.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
class JsonAdaptedPatient {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Patient's %s field is missing!";
    public static final String MESSAGE_ARCHIVED_CONSTRAINTS = "Patient's archived field must be a boolean.";

    private static final Logger logger = LogsCenter.getLogger(JsonAdaptedPatient.class);

    private final String id;
    private final String name;
    private final String phone;
    private final String address;
    private final String medicalHistory; // null if not set
    private final String notes; // null if not set
    private final List<JsonAdaptedAppointment> appointments; // null if there are none
    // null, or a NullNode if missing, if not archived; any other value but a boolean fails to load, as "marked" does
    private final JsonNode archived;

    /**
     * Constructs a {@code JsonAdaptedPatient} with the given patient details.
     */
    @JsonCreator
    public JsonAdaptedPatient(@JsonProperty("id") String id, @JsonProperty("name") String name,
            @JsonProperty("phone") String phone, @JsonProperty("address") String address,
            @JsonProperty("medicalHistory") String medicalHistory, @JsonProperty("notes") String notes,
            @JsonProperty("appointments") List<JsonAdaptedAppointment> appointments,
            @JsonProperty("archived") JsonNode archived) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.medicalHistory = medicalHistory;
        this.notes = notes;
        this.appointments = appointments;
        this.archived = archived;
    }

    /**
     * Constructs a {@code JsonAdaptedPatient} of a patient who is not archived, with the given patient details.
     */
    public JsonAdaptedPatient(String id, String name, String phone, String address, String medicalHistory,
            String notes, List<JsonAdaptedAppointment> appointments) {
        this(id, name, phone, address, medicalHistory, notes, appointments, null);
    }

    /**
     * Converts a given {@code Patient} into this class for Jackson use.
     */
    public JsonAdaptedPatient(Patient source) {
        id = source.getId().value;
        name = source.getName().fullName;
        phone = source.getPhone().value;
        address = source.getAddress().value;
        medicalHistory = source.getMedicalHistory().map(history -> history.value).orElse(null);
        notes = source.getNotes().map(patientNotes -> patientNotes.value).orElse(null);
        appointments = source.getSchedule().isEmpty()
                ? null
                : source.getSchedule().asList().stream().map(JsonAdaptedAppointment::new).toList();
        // Saved only for an archived patient, so that a patient who is not archived is saved as before
        archived = source.isArchived() ? BooleanNode.TRUE : null;
    }

    /**
     * Converts this Jackson-friendly adapted patient object into the model's {@code Patient} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted patient.
     */
    public Patient toModelType() throws IllegalValueException {
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

        if (notes != null && !Notes.isValidNotes(notes)) {
            throw new IllegalValueException(Notes.MESSAGE_CONSTRAINTS);
        }
        final Optional<Notes> modelNotes = Optional.ofNullable(notes).map(Notes::new);

        final List<JsonAdaptedAppointment> savedAppointments = appointments == null ? List.of() : appointments;
        final List<Appointment> modelAppointmentList = new ArrayList<>();
        for (JsonAdaptedAppointment appointment : savedAppointments) {
            if (appointment == null) {
                throw new IllegalValueException(Appointment.MESSAGE_CONSTRAINTS);
            }
            modelAppointmentList.add(appointment.toModelType());
        }
        final AppointmentSchedule modelSchedule;
        try {
            modelSchedule = new AppointmentSchedule(modelAppointmentList);
        } catch (IllegalArgumentException e) {
            throw new IllegalValueException("Patient " + modelId + ": " + e.getMessage(), e);
        }
        if (modelAppointmentList.stream().anyMatch(appointment -> !appointment.isNumbered())) {
            logger.fine("Assigned missing appointment numbers for patient " + modelId + ".");
        }

        boolean isArchivedGiven = archived != null && !archived.isNull();
        if (isArchivedGiven && !archived.isBoolean()) {
            throw new IllegalValueException(MESSAGE_ARCHIVED_CONSTRAINTS);
        }
        return new Patient(modelId, modelName, modelPhone, modelAddress, modelMedicalHistory, modelNotes,
                modelSchedule, isArchivedGiven && archived.booleanValue());
    }

}
