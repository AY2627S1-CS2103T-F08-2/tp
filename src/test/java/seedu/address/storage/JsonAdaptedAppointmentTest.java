package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.AppointmentSchedule;
import seedu.address.model.patient.Patient;

public class JsonAdaptedAppointmentTest {

    @Test
    public void toModelType_invalidNumber_rejectsWithoutCoercion() throws Exception {
        for (String number : List.of("0", "-1", "1.5", "\"2\"", "2147483648", "true", "null", "{}", "[]")) {
            String json = "{\"number\":" + number + ",\"dateTime\":\"2026-10-05\"}";
            JsonAdaptedAppointment adapted = JsonUtil.fromJsonString(json, JsonAdaptedAppointment.class);
            assertThrows(IllegalValueException.class, Appointment.MESSAGE_NUMBER_CONSTRAINTS, adapted::toModelType);
        }
    }

    @Test
    public void toModelType_legacyDuplicates_assignsChronologicallyAndPreservesDetails() throws Exception {
        Patient patient = readPatient("[\"2026-11-02\",{\"dateTime\":\"2026-10-05 09:30\","
                + "\"details\":\"First visit\"},{\"dateTime\":\"2026-10-05 09:30\",\"details\":\"Second visit\"}]");
        assertEquals(List.of(1, 2, 3), patient.getSchedule().asList().stream().map(Appointment::getNumber).toList());
        assertEquals("FIRST VISIT", patient.getSchedule().findByNumber(1).orElseThrow()
                .getDetails().orElseThrow().value);
        assertEquals("SECOND VISIT", patient.getSchedule().findByNumber(2).orElseThrow()
                .getDetails().orElseThrow().value);
        String json = JsonUtil.toJsonString(new JsonAdaptedPatient(patient));
        assertTrue(json.contains("\"number\" : 1"));
        assertEquals(patient, JsonUtil.fromJsonString(json, JsonAdaptedPatient.class).toModelType());
    }

    @Test
    public void toModelType_mixedNumbers_keepsExplicitAndFillsGaps() throws Exception {
        Patient patient = readPatient("[{\"number\":2,\"dateTime\":\"2026-10-05\"},"
                + "\"2026-11-02\",\"2026-10-06\"]");
        assertEquals(List.of(2, 1, 3), patient.getSchedule().asList().stream().map(Appointment::getNumber).toList());
    }

    @Test
    public void toModelType_duplicateExplicitNumbers_rejectsEvenAtDifferentTimes() {
        String appointments = "[{\"number\":2,\"dateTime\":\"2026-10-05\"},"
                + "{\"number\":2,\"dateTime\":\"2026-11-02\"}]";
        String message = "Patient S1234567A: " + AppointmentSchedule.MESSAGE_DUPLICATE_NUMBER;
        assertThrows(IllegalValueException.class, message, () -> readPatient(appointments));
    }

    @Test
    public void toModelType_numberedSameTime_keepsNumbersAcrossRoundTrip() throws Exception {
        Patient patient = readPatient("[{\"number\":9,\"dateTime\":\"2026-10-05\"},"
                + "{\"number\":2,\"dateTime\":\"2026-10-05\"}]");
        assertEquals(List.of(2, 9), patient.getSchedule().asList().stream().map(Appointment::getNumber).toList());
        String json = JsonUtil.toJsonString(new JsonAdaptedPatient(patient));
        assertEquals(patient, JsonUtil.fromJsonString(json, JsonAdaptedPatient.class).toModelType());
    }

    private static Patient readPatient(String appointments) throws Exception {
        String json = "{\"id\":\"S1234567A\",\"name\":\"Alex\",\"phone\":\"91234567\","
                + "\"address\":\"123 Main Street\",\"appointments\":" + appointments + "}";
        return JsonUtil.fromJsonString(json, JsonAdaptedPatient.class).toModelType();
    }
}
