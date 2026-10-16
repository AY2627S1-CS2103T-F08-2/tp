package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.LaiNurse;
import seedu.address.model.ReadOnlyLaiNurse;
import seedu.address.model.patient.Patient;

/**
 * An Immutable LaiNurse that is serializable to JSON format.
 */
@JsonRootName(value = "lainurse")
class JsonSerializableLaiNurse {

    public static final String MESSAGE_DUPLICATE_PATIENT = "Patients list contains duplicate patient(s).";

    // The field is named "persons" because Jackson uses it as the key in the data file.
    // Keep the name so that existing data files still load.
    private final List<JsonAdaptedPatient> persons = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableLaiNurse} with the given patients.
     */
    @JsonCreator
    public JsonSerializableLaiNurse(@JsonProperty("persons") List<JsonAdaptedPatient> persons) {
        this.persons.addAll(persons);
    }

    /**
     * Converts a given {@code ReadOnlyLaiNurse} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableLaiNurse}.
     */
    public JsonSerializableLaiNurse(ReadOnlyLaiNurse source) {
        persons.addAll(source.getPatientList().stream().map(JsonAdaptedPatient::new).collect(Collectors.toList()));
    }

    /**
     * Converts this LaiNurse data into the model's {@code LaiNurse} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public LaiNurse toModelType() throws IllegalValueException {
        LaiNurse laiNurse = new LaiNurse();
        for (JsonAdaptedPatient jsonAdaptedPatient : persons) {
            Patient patient = jsonAdaptedPatient.toModelType();
            if (laiNurse.hasPatient(patient)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_PATIENT);
            }
            laiNurse.addPatient(patient);
        }
        return laiNurse;
    }

}
