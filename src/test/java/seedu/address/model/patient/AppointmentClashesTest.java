package seedu.address.model.patient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.LaiNurse;
import seedu.address.model.ModelManager;
import seedu.address.model.Settings;
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
    public void find_dateOnly_neverClashes() {
        Patient patient = new PatientBuilder().withAppointments("2026-10-05", "2026-10-05",
                "2026-10-05 00:00", "2026-10-06").build();
        List<PatientAppointment> entries = patient.getSchedule().asList().stream()
                .map(appointment -> new PatientAppointment(patient, appointment)).toList();
        // two date-only visits on one day, and a date-only visit and one at 00:00 on that day
        assertTrue(AppointmentClashes.find(entries, entry(patient, 1)).isEmpty());
        assertTrue(AppointmentClashes.find(entries, entry(patient, 2)).isEmpty());
        assertTrue(AppointmentClashes.find(entries, entry(patient, 3)).isEmpty());
        assertThrows(NullPointerException.class, () -> AppointmentClashes.find(null, entry(patient, 1)));
        assertThrows(NullPointerException.class, () -> AppointmentClashes.find(entries, null));
    }

    private static PatientAppointment entry(Patient patient, int number) {
        return new PatientAppointment(patient, patient.getSchedule().findByNumber(number).orElseThrow());
    }

    @Test
    public void find_doneAppointments_neitherCauseNorReceiveClashes() {
        Appointment first = new Appointment("2026-10-05 09:30").withNumber(1);
        Appointment second = new Appointment("2026-10-05 09:30").withNumber(2).withDone(true);
        Appointment third = new Appointment("2026-10-05 09:30").withNumber(3);
        Patient patient = new PatientBuilder().withSchedule(new AppointmentSchedule(List.of(first, second, third)))
                .build();
        List<PatientAppointment> entries = patient.getSchedule().asList().stream()
                .map(appointment -> new PatientAppointment(patient, appointment)).toList();
        assertEquals(List.of(entry(patient, 3)), AppointmentClashes.find(entries, entry(patient, 1)));
        assertEquals(List.of(), AppointmentClashes.find(entries, entry(patient, 2)));
        assertEquals(List.of(entry(patient, 1)), AppointmentClashes.find(entries, entry(patient, 3)));
    }

    @Test
    public void find_archivedPatient_neitherCausesNorReceivesClashes() {
        Patient alex = new PatientBuilder().withId("S1234567A").withAppointments("2026-10-05 09:30").build();
        Patient ben = new PatientBuilder().withId("S2345678B").withAppointments("2026-10-05 09:30")
                .withArchived(true).build();
        Patient carl = new PatientBuilder().withId("S3456789C").withAppointments("2026-10-05 09:30").build();
        List<PatientAppointment> entries = List.of(entry(alex, 1), entry(ben, 1), entry(carl, 1));
        assertEquals(List.of(entry(carl, 1)), AppointmentClashes.find(entries, entry(alex, 1)));
        assertTrue(AppointmentClashes.find(entries, entry(ben, 1)).isEmpty());
    }

    @Test
    public void find_settings_decideWhichAppointmentsClash() {
        LocalDateTime now = LocalDateTime.of(2026, 10, 10, 12, 0);
        Patient alex = new PatientBuilder().withId("S1234567A").withSchedule(new AppointmentSchedule(List.of(
                new Appointment("2026-10-05 09:30").withNumber(1),
                new Appointment("2026-10-20 09:30").withNumber(2).withDone(true)))).build();
        Patient ben = new PatientBuilder().withId("S2345678B").withAppointments("2026-10-05 09:30",
                "2026-10-20 09:30").build();
        Patient carl = new PatientBuilder().withId("S3456789C").withAppointments("2026-10-20 09:30")
                .withArchived(true).build();
        List<PatientAppointment> entries = List.of(entry(alex, 1), entry(alex, 2), entry(ben, 1), entry(ben, 2),
                entry(carl, 1));

        // the defaults: a past clash counts, and the marked and archived appointments do not
        assertEquals(List.of(entry(ben, 1)),
                AppointmentClashes.find(entries, entry(alex, 1), Settings.DEFAULT, now));
        assertEquals(List.of(), AppointmentClashes.find(entries, entry(ben, 2), Settings.DEFAULT, now));

        // past clashes off: appointments that have passed never clash (#216)
        Settings noPast = Settings.DEFAULT.with(Settings.Key.PAST_CLASHES, false);
        assertEquals(List.of(), AppointmentClashes.find(entries, entry(alex, 1), noPast, now));

        // a mark that does not stop clashes, and archived patients that clash
        Settings all = Settings.DEFAULT.with(Settings.Key.MARK_STOPS_CLASHES, false)
                .with(Settings.Key.ARCHIVED_CLASHES, true);
        assertEquals(List.of(entry(alex, 2), entry(carl, 1)),
                AppointmentClashes.find(entries, entry(ben, 2), all, now));
        assertEquals(List.of(entry(ben, 2), entry(carl, 1)),
                AppointmentClashes.find(entries, entry(alex, 2), all, now));
    }
}
