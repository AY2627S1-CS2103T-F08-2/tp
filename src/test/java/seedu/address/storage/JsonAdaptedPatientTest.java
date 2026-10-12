package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.storage.JsonAdaptedPatient.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPatients.BENSON;
import static seedu.address.testutil.TypicalPatients.CARL;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.patient.Address;
import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.MedicalHistory;
import seedu.address.model.patient.Name;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientId;
import seedu.address.model.patient.Phone;
import seedu.address.testutil.PatientBuilder;

public class JsonAdaptedPatientTest {
    private static final String INVALID_ID = "S1234-567";
    private static final String INVALID_NAME = " ";
    private static final String INVALID_PHONE = "+65abc";
    private static final String INVALID_ADDRESS = " ";
    private static final String INVALID_MEDICAL_HISTORY = " ";
    private static final String INVALID_APPOINTMENT = "2026-02-30";

    private static final String VALID_ID = BENSON.getId().toString();
    private static final String VALID_NAME = BENSON.getName().toString();
    private static final String VALID_PHONE = BENSON.getPhone().toString();
    private static final String VALID_ADDRESS = BENSON.getAddress().toString();
    private static final String VALID_MEDICAL_HISTORY = BENSON.getMedicalHistory().get().toString();
    private static final String VALID_APPOINTMENT = BENSON.getAppointments().asList().get(0).toString();

    @Test
    public void toModelType_validPatientDetails_returnsPatient() throws Exception {
        JsonAdaptedPatient patient = new JsonAdaptedPatient(BENSON);
        assertEquals(BENSON, patient.toModelType());
    }

    @Test
    public void toModelType_noOptionalFields_returnsPatient() throws Exception {
        JsonAdaptedPatient patient = new JsonAdaptedPatient(CARL);
        assertEquals(CARL, patient.toModelType());
    }

    @Test
    public void toModelType_invalidId_throwsIllegalValueException() {
        JsonAdaptedPatient patient = new JsonAdaptedPatient(INVALID_ID, VALID_NAME, VALID_PHONE, VALID_ADDRESS,
                VALID_MEDICAL_HISTORY, List.of(VALID_APPOINTMENT), null);
        String expectedMessage = PatientId.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, patient::toModelType);
    }

    @Test
    public void toModelType_nullId_throwsIllegalValueException() {
        JsonAdaptedPatient patient = new JsonAdaptedPatient(null, VALID_NAME, VALID_PHONE, VALID_ADDRESS,
                VALID_MEDICAL_HISTORY, List.of(VALID_APPOINTMENT), null);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, PatientId.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, patient::toModelType);
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedPatient patient = new JsonAdaptedPatient(VALID_ID, INVALID_NAME, VALID_PHONE, VALID_ADDRESS,
                VALID_MEDICAL_HISTORY, List.of(VALID_APPOINTMENT), null);
        String expectedMessage = Name.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, patient::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedPatient patient = new JsonAdaptedPatient(VALID_ID, null, VALID_PHONE, VALID_ADDRESS,
                VALID_MEDICAL_HISTORY, List.of(VALID_APPOINTMENT), null);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, patient::toModelType);
    }

    @Test
    public void toModelType_invalidPhone_throwsIllegalValueException() {
        JsonAdaptedPatient patient = new JsonAdaptedPatient(VALID_ID, VALID_NAME, INVALID_PHONE, VALID_ADDRESS,
                VALID_MEDICAL_HISTORY, List.of(VALID_APPOINTMENT), null);
        String expectedMessage = Phone.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, patient::toModelType);
    }

    @Test
    public void toModelType_nullPhone_throwsIllegalValueException() {
        JsonAdaptedPatient patient = new JsonAdaptedPatient(VALID_ID, VALID_NAME, null, VALID_ADDRESS,
                VALID_MEDICAL_HISTORY, List.of(VALID_APPOINTMENT), null);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, patient::toModelType);
    }

    @Test
    public void toModelType_invalidAddress_throwsIllegalValueException() {
        JsonAdaptedPatient patient = new JsonAdaptedPatient(VALID_ID, VALID_NAME, VALID_PHONE, INVALID_ADDRESS,
                VALID_MEDICAL_HISTORY, List.of(VALID_APPOINTMENT), null);
        String expectedMessage = Address.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, patient::toModelType);
    }

    @Test
    public void toModelType_nullAddress_throwsIllegalValueException() {
        JsonAdaptedPatient patient = new JsonAdaptedPatient(VALID_ID, VALID_NAME, VALID_PHONE, null,
                VALID_MEDICAL_HISTORY, List.of(VALID_APPOINTMENT), null);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Address.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, patient::toModelType);
    }

    @Test
    public void toModelType_invalidMedicalHistory_throwsIllegalValueException() {
        JsonAdaptedPatient patient = new JsonAdaptedPatient(VALID_ID, VALID_NAME, VALID_PHONE, VALID_ADDRESS,
                INVALID_MEDICAL_HISTORY, List.of(VALID_APPOINTMENT), null);
        String expectedMessage = MedicalHistory.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, patient::toModelType);
    }

    @Test
    public void toModelType_invalidAppointment_throwsIllegalValueException() {
        JsonAdaptedPatient patient = new JsonAdaptedPatient(VALID_ID, VALID_NAME, VALID_PHONE, VALID_ADDRESS,
                VALID_MEDICAL_HISTORY, List.of(VALID_APPOINTMENT, INVALID_APPOINTMENT), null);
        String expectedMessage = Appointment.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, patient::toModelType);
    }

    @Test
    public void toModelType_nullAppointmentInList_throwsIllegalValueException() {
        // a hand-edited data file can contain null in the appointments list
        JsonAdaptedPatient patient = new JsonAdaptedPatient(VALID_ID, VALID_NAME, VALID_PHONE, VALID_ADDRESS,
                VALID_MEDICAL_HISTORY, Arrays.asList(VALID_APPOINTMENT, null), null);
        String expectedMessage = Appointment.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, patient::toModelType);
    }

    @Test
    public void toModelType_invalidLegacyNextAppointment_throwsIllegalValueException() {
        JsonAdaptedPatient patient = new JsonAdaptedPatient(VALID_ID, VALID_NAME, VALID_PHONE, VALID_ADDRESS,
                VALID_MEDICAL_HISTORY, null, INVALID_APPOINTMENT);
        String expectedMessage = Appointment.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, patient::toModelType);
    }

    @Test
    public void toModelType_legacyNextAppointment_readAsAppointment() throws Exception {
        // Data files saved before multiple appointments store one appointment as "nextAppointment"
        JsonAdaptedPatient patient = new JsonAdaptedPatient(VALID_ID, VALID_NAME, VALID_PHONE, VALID_ADDRESS,
                VALID_MEDICAL_HISTORY, null, VALID_APPOINTMENT);
        assertEquals(BENSON, patient.toModelType());
    }

    @Test
    public void toModelType_multipleAppointments_returnsPatient() throws Exception {
        Patient patient = new PatientBuilder(BENSON).withAppointments("2026-10-06", "2026-12-01 10:00").build();
        assertEquals(patient, new JsonAdaptedPatient(patient).toModelType());
    }

}
