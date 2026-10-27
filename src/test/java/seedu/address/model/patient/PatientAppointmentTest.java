package seedu.address.model.patient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PatientBuilder;

public class PatientAppointmentTest {

    private static final Patient ALEX = new PatientBuilder().withId("S1234567A").withName("Alex Yeoh")
            .withAppointments("2026-10-16", "2026-10-16 09:30").build();
    private static final Patient BEN = new PatientBuilder().withId("S2345678B").withName("Ben Tan")
            .withAppointments("2026-10-16 09:30").build();
    private static final Patient OTHER_BEN = new PatientBuilder().withId("S0000001A").withName("Ben Tan")
            .withAppointments("2026-10-16 09:30").build();

    private static final Appointment TODAY = new Appointment("2026-10-16").withNumber(1);
    private static final Appointment TODAY_MORNING = new Appointment("2026-10-16 09:30").withNumber(2);

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new PatientAppointment(null, TODAY));
        assertThrows(NullPointerException.class, () -> new PatientAppointment(ALEX, null));
    }

    @Test
    public void constructor_appointmentOfAnotherPatient_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new PatientAppointment(BEN, TODAY));
    }

    @Test
    public void compareTo_ordersByAppointmentThenNameThenId() {
        PatientAppointment alexDateOnly = new PatientAppointment(ALEX, TODAY);
        PatientAppointment alexMorning = new PatientAppointment(ALEX, TODAY_MORNING);
        PatientAppointment benMorning = new PatientAppointment(BEN, BEN.getSchedule().asList().getFirst());
        PatientAppointment otherBenMorning =
                new PatientAppointment(OTHER_BEN, OTHER_BEN.getSchedule().asList().getFirst());

        List<PatientAppointment> sorted = Stream.of(benMorning, otherBenMorning, alexMorning, alexDateOnly)
                .sorted().toList();
        // a date-only appointment comes first in its day; two Ben Tans are told apart by ID
        assertEquals(List.of(alexDateOnly, alexMorning, otherBenMorning, benMorning), sorted);
    }

    @Test
    public void isSameEntry_samePatientIdAndAppointment_returnsTrue() {
        PatientAppointment entry = new PatientAppointment(ALEX, TODAY);
        Patient renamedAlex = new PatientBuilder(ALEX).withName("Alexandra Yeoh").build();

        assertTrue(entry.isSameEntry(new PatientAppointment(ALEX, TODAY)));
        assertTrue(entry.isSameEntry(new PatientAppointment(renamedAlex, TODAY)));
        assertFalse(entry.isSameEntry(new PatientAppointment(ALEX, TODAY_MORNING)));
        assertFalse(new PatientAppointment(BEN, BEN.getSchedule().asList().getFirst())
                .isSameEntry(new PatientAppointment(OTHER_BEN, OTHER_BEN.getSchedule().asList().getFirst())));
        assertFalse(entry.isSameEntry(null));
    }

    @Test
    public void equals() {
        PatientAppointment entry = new PatientAppointment(ALEX, TODAY);

        // same values -> returns true
        assertTrue(entry.equals(new PatientAppointment(ALEX, TODAY)));
        assertEquals(entry.hashCode(), new PatientAppointment(ALEX, TODAY).hashCode());

        // same object -> returns true
        assertTrue(entry.equals(entry));

        // null -> returns false
        assertFalse(entry.equals(null));

        // different types -> returns false
        assertFalse(entry.equals(TODAY));

        // different appointment -> returns false
        assertFalse(entry.equals(new PatientAppointment(ALEX, TODAY_MORNING)));

        // same patient with other details -> returns false
        Patient renamedAlex = new PatientBuilder(ALEX).withName("Alexandra Yeoh").build();
        assertFalse(entry.equals(new PatientAppointment(renamedAlex, TODAY)));
    }

    @Test
    public void toStringMethod() {
        PatientAppointment entry = new PatientAppointment(ALEX, TODAY_MORNING);
        String expected = PatientAppointment.class.getCanonicalName() + "{patientId=" + ALEX.getId()
                + ", appointment=" + TODAY_MORNING + "}";
        assertEquals(expected, entry.toString());
    }

    @Test
    public void isSameEntry_appointmentDetailsChanged_returnsTrue() {
        Patient alexWithDetails = new PatientBuilder(ALEX).withAppointments()
                .withAppointment("2026-10-16", "First visit")
                .withAppointment("2026-10-16 09:30", "Insulin review").build();
        PatientAppointment before = new PatientAppointment(ALEX, TODAY_MORNING);
        PatientAppointment after = new PatientAppointment(alexWithDetails,
                alexWithDetails.getSchedule().asList().get(1));
        assertTrue(before.isSameEntry(after));
    }
}
