package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.storage.JsonAdaptedPatient.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPatients.BENSON;
import static seedu.address.testutil.TypicalPatients.CARL;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.patient.Address;
import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.AppointmentDetails;
import seedu.address.model.patient.DateOfBirth;
import seedu.address.model.patient.MedicalHistory;
import seedu.address.model.patient.Name;
import seedu.address.model.patient.Notes;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientId;
import seedu.address.model.patient.Phone;
import seedu.address.model.patient.VoucherBalance;
import seedu.address.testutil.PatientBuilder;

public class JsonAdaptedPatientTest {
    private static final String INVALID_ID = "S1234-567";
    private static final String INVALID_NAME = " ";
    private static final String INVALID_PHONE = "+65abc";
    private static final String INVALID_ADDRESS = " ";
    private static final String INVALID_MEDICAL_HISTORY = " ";
    private static final String INVALID_APPOINTMENT = "2026-02-30";
    private static final String INVALID_NOTES = " ";
    private static final String INVALID_DETAILS = " ";
    private static final String INVALID_VOUCHER_DATE = "03-10-2026";

    private static final String VALID_ID = BENSON.getId().toString();
    private static final String VALID_NAME = BENSON.getName().toString();
    private static final String VALID_PHONE = BENSON.getPhone().toString();
    private static final String VALID_ADDRESS = BENSON.getAddress().toString();
    private static final String VALID_MEDICAL_HISTORY = BENSON.getMedicalHistory().get().toString();
    private static final String VALID_APPOINTMENT = BENSON.getSchedule().asList().get(0).toString();
    private static final double VALID_VOUCHER_COUNT = 12.1;
    private static final String VALID_VOUCHER_DATE = "2026-10-03";

    @Test
    public void toModelType_validPatientDetails_returnsPatient() throws Exception {
        JsonAdaptedPatient patient = new JsonAdaptedPatient(BENSON);
        assertEquals(BENSON, patient.toModelType());
    }

    @Test
    public void toModelType_archivedPatient_staysArchived() throws Exception {
        Patient archivedBenson = BENSON.withArchived(true);
        assertEquals(archivedBenson, new JsonAdaptedPatient(archivedBenson).toModelType());
        assertTrue(JsonUtil.toJsonString(new JsonAdaptedPatient(archivedBenson)).contains("\"archived\" : true"));
        // a patient who is not archived is saved without the field, as before archiving existed
        assertFalse(JsonUtil.toJsonString(new JsonAdaptedPatient(BENSON)).contains("archived"));
    }

    @Test
    public void toModelType_archivedNotBoolean_throwsIllegalValueException() throws Exception {
        for (String value : new String[] {"\"true\"", "1", "0", "\"yes\""}) {
            JsonAdaptedPatient patient = JsonUtil.fromJsonString("{\"id\": \"S1234567A\", \"name\": \"Alex\", "
                    + "\"phone\": \"91234567\", \"address\": \"Blk 1\", \"archived\": " + value + "}",
                    JsonAdaptedPatient.class);
            assertThrows(IllegalValueException.class, JsonAdaptedPatient.MESSAGE_ARCHIVED_CONSTRAINTS,
                    patient::toModelType);
        }
        // a boolean loads, and false or null means not archived
        JsonAdaptedPatient notArchived = JsonUtil.fromJsonString("{\"id\": \"S1234567A\", \"name\": \"Alex\", "
                + "\"phone\": \"91234567\", \"address\": \"Blk 1\", \"archived\": false}", JsonAdaptedPatient.class);
        assertFalse(notArchived.toModelType().isArchived());
        JsonAdaptedPatient nullArchived = JsonUtil.fromJsonString("{\"id\": \"S1234567A\", \"name\": \"Alex\", "
                + "\"phone\": \"91234567\", \"address\": \"Blk 1\", \"archived\": null}", JsonAdaptedPatient.class);
        assertFalse(nullArchived.toModelType().isArchived());
    }

    @Test
    public void toModelType_noOptionalFields_returnsPatient() throws Exception {
        JsonAdaptedPatient patient = new JsonAdaptedPatient(CARL);
        assertEquals(CARL, patient.toModelType());
    }

    @Test
    public void toModelType_invalidId_throwsIllegalValueException() {
        JsonAdaptedPatient patient = new JsonAdaptedPatient(INVALID_ID, VALID_NAME, VALID_PHONE, VALID_ADDRESS,
                VALID_MEDICAL_HISTORY, null, appointments(VALID_APPOINTMENT));
        String expectedMessage = PatientId.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, patient::toModelType);
    }

    @Test
    public void toModelType_nullId_throwsIllegalValueException() {
        JsonAdaptedPatient patient = new JsonAdaptedPatient(null, VALID_NAME, VALID_PHONE, VALID_ADDRESS,
                VALID_MEDICAL_HISTORY, null, appointments(VALID_APPOINTMENT));
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, PatientId.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, patient::toModelType);
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedPatient patient = new JsonAdaptedPatient(VALID_ID, INVALID_NAME, VALID_PHONE, VALID_ADDRESS,
                VALID_MEDICAL_HISTORY, null, appointments(VALID_APPOINTMENT));
        String expectedMessage = Name.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, patient::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedPatient patient = new JsonAdaptedPatient(VALID_ID, null, VALID_PHONE, VALID_ADDRESS,
                VALID_MEDICAL_HISTORY, null, appointments(VALID_APPOINTMENT));
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, patient::toModelType);
    }

    @Test
    public void toModelType_invalidPhone_throwsIllegalValueException() {
        JsonAdaptedPatient patient = new JsonAdaptedPatient(VALID_ID, VALID_NAME, INVALID_PHONE, VALID_ADDRESS,
                VALID_MEDICAL_HISTORY, null, appointments(VALID_APPOINTMENT));
        String expectedMessage = Phone.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, patient::toModelType);
    }

    @Test
    public void toModelType_nullPhone_throwsIllegalValueException() {
        JsonAdaptedPatient patient = new JsonAdaptedPatient(VALID_ID, VALID_NAME, null, VALID_ADDRESS,
                VALID_MEDICAL_HISTORY, null, appointments(VALID_APPOINTMENT));
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, patient::toModelType);
    }

    @Test
    public void toModelType_invalidAddress_throwsIllegalValueException() {
        JsonAdaptedPatient patient = new JsonAdaptedPatient(VALID_ID, VALID_NAME, VALID_PHONE, INVALID_ADDRESS,
                VALID_MEDICAL_HISTORY, null, appointments(VALID_APPOINTMENT));
        String expectedMessage = Address.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, patient::toModelType);
    }

    @Test
    public void toModelType_nullAddress_throwsIllegalValueException() {
        JsonAdaptedPatient patient = new JsonAdaptedPatient(VALID_ID, VALID_NAME, VALID_PHONE, null,
                VALID_MEDICAL_HISTORY, null, appointments(VALID_APPOINTMENT));
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Address.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, patient::toModelType);
    }

    @Test
    public void toModelType_invalidMedicalHistory_throwsIllegalValueException() {
        JsonAdaptedPatient patient = new JsonAdaptedPatient(VALID_ID, VALID_NAME, VALID_PHONE, VALID_ADDRESS,
                INVALID_MEDICAL_HISTORY, null, appointments(VALID_APPOINTMENT));
        String expectedMessage = MedicalHistory.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, patient::toModelType);
    }

    @Test
    public void toModelType_invalidAppointment_throwsIllegalValueException() {
        JsonAdaptedPatient patient = new JsonAdaptedPatient(VALID_ID, VALID_NAME, VALID_PHONE, VALID_ADDRESS,
                VALID_MEDICAL_HISTORY, null, appointments(VALID_APPOINTMENT, INVALID_APPOINTMENT));
        String expectedMessage = Appointment.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, patient::toModelType);
    }

    @Test
    public void toModelType_nullAppointmentInList_throwsIllegalValueException() {
        // a hand-edited data file can contain null in the appointments list
        JsonAdaptedPatient patient = new JsonAdaptedPatient(VALID_ID, VALID_NAME, VALID_PHONE, VALID_ADDRESS,
                VALID_MEDICAL_HISTORY, null, appointments(VALID_APPOINTMENT, null));
        String expectedMessage = Appointment.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, patient::toModelType);
    }

    @Test
    public void toModelType_legacyNextAppointment_ignored() throws Exception {
        // Data files from v1.2 or earlier store one appointment as "nextAppointment", which is no longer read
        JsonAdaptedPatient patient = JsonUtil.fromJsonString("{\"id\": \"S1234567A\", \"name\": \"Hans Muster\", "
                + "\"phone\": \"9482424\", \"address\": \"4th street\", \"nextAppointment\": \"2026-10-05\"}",
                JsonAdaptedPatient.class);
        assertTrue(patient.toModelType().getSchedule().isEmpty());
    }

    @Test
    public void toModelType_multipleAppointments_returnsPatient() throws Exception {
        Patient patient = new PatientBuilder(BENSON).withAppointments("2026-10-06", "2026-12-01 10:00").build();
        assertEquals(patient, new JsonAdaptedPatient(patient).toModelType());
    }

    @Test
    public void toModelType_notesAndAppointmentDetails_returnsPatient() throws Exception {
        Patient patient = new PatientBuilder(BENSON).withNotes("Has a dog")
                .withAppointment("2026-10-20", "Blood test").build();
        assertEquals(patient, new JsonAdaptedPatient(patient).toModelType());
    }

    @Test
    public void toModelType_invalidNotes_throwsIllegalValueException() {
        JsonAdaptedPatient patient = new JsonAdaptedPatient(VALID_ID, VALID_NAME, VALID_PHONE, VALID_ADDRESS,
                VALID_MEDICAL_HISTORY, INVALID_NOTES, appointments(VALID_APPOINTMENT));
        assertThrows(IllegalValueException.class, Notes.MESSAGE_CONSTRAINTS, patient::toModelType);
    }

    @Test
    public void toModelType_invalidAppointmentDetails_throwsIllegalValueException() {
        JsonAdaptedPatient patient = new JsonAdaptedPatient(VALID_ID, VALID_NAME, VALID_PHONE, VALID_ADDRESS,
                VALID_MEDICAL_HISTORY, null, List.of(new JsonAdaptedAppointment(VALID_APPOINTMENT, INVALID_DETAILS)));
        assertThrows(IllegalValueException.class, AppointmentDetails.MESSAGE_CONSTRAINTS, patient::toModelType);
    }

    @Test
    public void toModelType_appointmentWithoutDateTime_throwsIllegalValueException() {
        // a hand-edited data file can give an appointment details but no date
        JsonAdaptedPatient patient = new JsonAdaptedPatient(VALID_ID, VALID_NAME, VALID_PHONE, VALID_ADDRESS,
                VALID_MEDICAL_HISTORY, null, List.of(new JsonAdaptedAppointment(null, "Blood test")));
        String expectedMessage = String.format(JsonAdaptedAppointment.MISSING_FIELD_MESSAGE_FORMAT, "dateTime");
        assertThrows(IllegalValueException.class, expectedMessage, patient::toModelType);
    }

    @Test
    public void toModelType_validVouchers_returnsPatient() throws Exception {
        VoucherBalance vouchers = new VoucherBalance(12.1, LocalDate.parse("2026-10-03"));
        Patient voucherHolder = BENSON.withVoucherBalance(vouchers);
        assertEquals(voucherHolder, new JsonAdaptedPatient(voucherHolder).toModelType());
        // a patient who holds no vouchers is saved without the voucher fields, as before vouchers existed
        assertFalse(JsonUtil.toJsonString(new JsonAdaptedPatient(BENSON)).contains("voucher"));
        assertTrue(JsonUtil.toJsonString(new JsonAdaptedPatient(voucherHolder)).contains("\"voucherCount\" : 12.1"));
    }

    @Test
    public void toModelType_voucherCountWithoutDate_throwsIllegalValueException() throws Exception {
        JsonAdaptedPatient patient = JsonUtil.fromJsonString("{\"id\": \"S1234567A\", \"name\": \"Alex\", "
                + "\"phone\": \"91234567\", \"address\": \"Blk 1\", \"voucherCount\": 5}",
                JsonAdaptedPatient.class);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, "voucher");
        assertThrows(IllegalValueException.class, expectedMessage, patient::toModelType);
    }

    @Test
    public void toModelType_voucherDateWithoutCount_throwsIllegalValueException() throws Exception {
        JsonAdaptedPatient patient = JsonUtil.fromJsonString("{\"id\": \"S1234567A\", \"name\": \"Alex\", "
                + "\"phone\": \"91234567\", \"address\": \"Blk 1\", \"voucherInterestDate\": \"2026-10-03\"}",
                JsonAdaptedPatient.class);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, "voucher");
        assertThrows(IllegalValueException.class, expectedMessage, patient::toModelType);
    }

    @Test
    public void toModelType_invalidVoucherCount_throwsIllegalValueException() {
        for (double invalidCount : new double[] {-5, Double.NaN, Double.POSITIVE_INFINITY}) {
            JsonAdaptedPatient patient = new JsonAdaptedPatient(VALID_ID, VALID_NAME, VALID_PHONE, VALID_ADDRESS,
                    VALID_MEDICAL_HISTORY, null, null, invalidCount, VALID_VOUCHER_DATE, null, null);
            assertThrows(IllegalValueException.class, VoucherBalance.MESSAGE_COUNT_CONSTRAINTS,
                    patient::toModelType);
        }
    }

    @Test
    public void toModelType_invalidVoucherDate_throwsIllegalValueException() {
        JsonAdaptedPatient patient = new JsonAdaptedPatient(VALID_ID, VALID_NAME, VALID_PHONE, VALID_ADDRESS,
                VALID_MEDICAL_HISTORY, null, null, VALID_VOUCHER_COUNT, INVALID_VOUCHER_DATE, null, null);
        assertThrows(IllegalValueException.class, VoucherBalance.MESSAGE_DATE_CONSTRAINTS, patient::toModelType);
    }

    @Test
    public void toModelType_validDateOfBirth_returnsPatient() throws Exception {
        Patient born = new PatientBuilder(BENSON).withDateOfBirth("1990-11-16").build();
        assertEquals(born, new JsonAdaptedPatient(born).toModelType());
        // a patient with no date of birth is saved without the field, as before dates of birth existed
        assertFalse(JsonUtil.toJsonString(new JsonAdaptedPatient(BENSON)).contains("dateOfBirth"));
        assertTrue(JsonUtil.toJsonString(new JsonAdaptedPatient(born)).contains("\"dateOfBirth\" : \"1990-11-16\""));
    }

    @Test
    public void toModelType_invalidDateOfBirth_throwsIllegalValueException() {
        for (String invalidDateOfBirth : new String[] {"16-11-1990", "1990-02-30", "not a date", "2999-01-01"}) {
            JsonAdaptedPatient patient = new JsonAdaptedPatient(VALID_ID, VALID_NAME, VALID_PHONE, VALID_ADDRESS,
                    VALID_MEDICAL_HISTORY, null, null, null, null, invalidDateOfBirth, null);
            assertThrows(IllegalValueException.class, DateOfBirth.MESSAGE_CONSTRAINTS, patient::toModelType);
        }
    }

    private static List<JsonAdaptedAppointment> appointments(String... appointments) {
        // null stays null, as in a hand-edited data file
        return Arrays.stream(appointments)
                .map(appointment -> appointment == null ? null : JsonAdaptedAppointment.fromString(appointment))
                .toList();
    }

}
