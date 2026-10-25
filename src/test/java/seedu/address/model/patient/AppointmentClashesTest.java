package seedu.address.model.patient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.LaiNurse;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.testutil.PatientBuilder;

public class AppointmentClashesTest {

    @Test
    public void find_sameAndOtherPatients_excludesOnlyTargetIdentity() {
        Patient alex = new PatientBuilder().withId("S1234567A").withName("Alex")
                .withAppointments("2026-10-05 09:30", "2026-10-05 09:30", "2026-10-05 10:00").build();
        Patient ben = new PatientBuilder().withId("S2345678B").withName("Ben")
                .withAppointments("2026-10-05 09:30").build();
        LaiNurse data = new LaiNurse();
        data.addPatient(ben);
        data.addPatient(alex);
        ModelManager model = new ModelManager(data, new UserPrefs());
        PatientAppointment target = entry(alex, 1);
        assertEquals(List.of(entry(alex, 2), entry(ben, 1)),
                AppointmentClashes.find(model.getAppointmentList(), target));
        model.updateFilteredPatientList(patient -> patient.getId().equals(alex.getId()));
        assertEquals(2, AppointmentClashes.find(model.getAppointmentList(), target).size());
        assertTrue(AppointmentClashes.find(model.getAppointmentList(), entry(alex, 3)).isEmpty());
        model.deletePatient(ben);
        assertEquals(List.of(entry(alex, 2)), AppointmentClashes.find(model.getAppointmentList(), target));
    }

    @Test
    public void find_dateOnly_matchesOnlyOtherDateOnlyOnSameDay() {
        Patient patient = new PatientBuilder().withAppointments("2026-10-05", "2026-10-05",
                "2026-10-05 00:00", "2026-10-06").build();
        List<PatientAppointment> entries = patient.getSchedule().asList().stream()
                .map(appointment -> new PatientAppointment(patient, appointment)).toList();
        assertEquals(List.of(entry(patient, 2)), AppointmentClashes.find(entries, entry(patient, 1)));
        assertTrue(AppointmentClashes.find(entries, entry(patient, 3)).isEmpty());
        assertThrows(NullPointerException.class, () -> AppointmentClashes.find(null, entry(patient, 1)));
        assertThrows(NullPointerException.class, () -> AppointmentClashes.find(entries, null));
    }

    private static PatientAppointment entry(Patient patient, int number) {
        return new PatientAppointment(patient, patient.getSchedule().findByNumber(number).orElseThrow());
    }
}
