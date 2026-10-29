package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.Patient;

public class JsonAdaptedAppointmentTest {

    @Test
    public void toModelType_mark_roundTripsBothStatusesWithDetails() throws Exception {
        for (boolean marked : List.of(false, true)) {
            Patient patient = readPatient("[{\"dateTime\":\"2026-10-05 09:30\","
                    + "\"details\":\"Insulin review\",\"marked\":" + marked + "}]");
            Appointment appointment = patient.getSchedule().findByNumber(1).orElseThrow();
            assertEquals(marked, appointment.isDone());
            assertEquals("INSULIN REVIEW", appointment.getDetails().orElseThrow().value);
            String json = JsonUtil.toJsonString(new JsonAdaptedPatient(patient));
            assertTrue(json.contains("\"marked\" : " + marked));
            assertFalse(json.contains("\"done\""));
            // the number is the appointment's place in date order, so it is not saved
            assertFalse(json.contains("\"number\""));
            assertEquals(patient, JsonUtil.fromJsonString(json, JsonAdaptedPatient.class).toModelType());
        }
    }

    @Test
    public void toModelType_missingMark_defaultsToUnmarked() throws Exception {
        Patient patient = readPatient("[\"2026-10-05\",{\"dateTime\":\"2026-10-06\"},"
                + "{\"dateTime\":\"2026-10-07\"}]");
        assertEquals(List.of(1, 2, 3), patient.getSchedule().asList().stream().map(Appointment::getNumber).toList());
        patient.getSchedule().asList().forEach(appointment -> assertFalse(appointment.isDone()));
        // v1.5 and earlier saved the mark as "done"
        Patient doneLegacy = readPatient("[{\"dateTime\":\"2026-10-05\",\"done\":true}]");
        assertTrue(doneLegacy.getSchedule().findByNumber(1).orElseThrow().isDone());
        // "marked" wins over "done" if a file has both
        Patient both = readPatient("[{\"dateTime\":\"2026-10-05\",\"done\":true,\"marked\":false}]");
        assertFalse(both.getSchedule().findByNumber(1).orElseThrow().isDone());
    }

    @Test
    public void toModelType_invalidMark_rejectsWithoutCoercion() throws Exception {
        for (String key : List.of("marked", "done")) {
            for (String value : List.of("null", "0", "1", "\"true\"", "\"false\"", "{}", "[]")) {
                String json = "{\"dateTime\":\"2026-10-05\",\"" + key + "\":" + value + "}";
                JsonAdaptedAppointment adapted = JsonUtil.fromJsonString(json, JsonAdaptedAppointment.class);
                assertThrows(IllegalValueException.class, JsonAdaptedAppointment.MESSAGE_MARKED_CONSTRAINTS,
                        adapted::toModelType);
            }
        }
    }

    @Test
    public void toModelType_savedNumber_ignored() throws Exception {
        // v1.5 and earlier saved a number, which is ignored whatever its value
        for (String number : List.of("7", "0", "-1", "1.5", "\"2\"", "2147483648", "true", "null", "{}", "[]")) {
            Patient patient = readPatient("[{\"number\":" + number + ",\"dateTime\":\"2026-10-05\"}]");
            assertEquals("2026-10-05", patient.getSchedule().findByNumber(1).orElseThrow().value);
        }
        // numbers out of date order, and the same number twice, are numbered again in date order
        Patient patient = readPatient("[{\"number\":2,\"dateTime\":\"2026-11-02\"},"
                + "{\"number\":2,\"dateTime\":\"2026-10-06\"},{\"number\":1,\"dateTime\":\"2026-10-07\"}]");
        assertEquals(List.of("2026-10-06", "2026-10-07", "2026-11-02"), patient.getSchedule().asList().stream()
                .map(appointment -> appointment.value).toList());
        assertEquals(List.of(1, 2, 3), patient.getSchedule().asList().stream().map(Appointment::getNumber).toList());
    }

    @Test
    public void toModelType_sameDateAndTime_keepsSavedOrderAcrossRoundTrip() throws Exception {
        // the saved numbers would put the second visit first, but the order in the file decides
        Patient patient = readPatient("[\"2026-11-02\",{\"number\":9,\"dateTime\":\"2026-10-05 09:30\","
                + "\"details\":\"First visit\"},{\"number\":2,\"dateTime\":\"2026-10-05 09:30\","
                + "\"details\":\"Second visit\"}]");
        assertEquals(List.of(1, 2, 3), patient.getSchedule().asList().stream().map(Appointment::getNumber).toList());
        assertEquals("FIRST VISIT", patient.getSchedule().findByNumber(1).orElseThrow()
                .getDetails().orElseThrow().value);
        assertEquals("SECOND VISIT", patient.getSchedule().findByNumber(2).orElseThrow()
                .getDetails().orElseThrow().value);
        String json = JsonUtil.toJsonString(new JsonAdaptedPatient(patient));
        assertEquals(patient, JsonUtil.fromJsonString(json, JsonAdaptedPatient.class).toModelType());
    }

    private static Patient readPatient(String appointments) throws Exception {
        String json = "{\"id\":\"S1234567A\",\"name\":\"Alex\",\"phone\":\"91234567\","
                + "\"address\":\"123 Main Street\",\"appointments\":" + appointments + "}";
        return JsonUtil.fromJsonString(json, JsonAdaptedPatient.class).toModelType();
    }
}
