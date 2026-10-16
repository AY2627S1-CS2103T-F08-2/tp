package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import org.junit.jupiter.api.Test;

import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientAppointment;
import seedu.address.testutil.PatientBuilder;
import seedu.address.ui.AppointmentPage.DayHeading;
import seedu.address.ui.AppointmentPage.Entry;

public class AppointmentPageTest {

    /** A Friday. */
    private static final LocalDate TODAY = LocalDate.of(2026, 10, 16);
    private static final AppointmentWeek THIS_WEEK = AppointmentWeek.of(TODAY);

    private static final Patient ALEX = new PatientBuilder().withId("S1234567A").withName("Alex")
            .withAppointments("2026-10-12 10:00", "2026-10-16 09:30", "2026-10-23 09:30").build();
    private static final Patient BEN = new PatientBuilder().withId("S2345678B").withName("Ben")
            .withAppointments("2026-10-16", "2026-10-11 08:00").build();

    /** Alex on Monday at 10:00. */
    private static final PatientAppointment MONDAY_ALEX = entry(ALEX, "2026-10-12 10:00");
    /** Ben today, with no time, so before Alex today. */
    private static final PatientAppointment TODAY_BEN = entry(BEN, "2026-10-16");
    /** Alex today at 09:30. */
    private static final PatientAppointment TODAY_ALEX = entry(ALEX, "2026-10-16 09:30");
    /** Alex next Friday. */
    private static final PatientAppointment NEXT_WEEK_ALEX = entry(ALEX, "2026-10-23 09:30");
    /** Ben last Sunday. */
    private static final PatientAppointment LAST_WEEK_BEN = entry(BEN, "2026-10-11 08:00");

    private static final List<PatientAppointment> ALL =
            List.of(LAST_WEEK_BEN, MONDAY_ALEX, TODAY_BEN, TODAY_ALEX, NEXT_WEEK_ALEX);

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AppointmentPage(null, ALL));
        assertThrows(NullPointerException.class, () -> new AppointmentPage(THIS_WEEK, null));
    }

    @Test
    public void getRows_headingForEachDayAndAppointmentsNumberedAcrossWeek() {
        // in another order, to show that the page sorts them
        AppointmentPage page = new AppointmentPage(THIS_WEEK, List.of(TODAY_ALEX, NEXT_WEEK_ALEX, MONDAY_ALEX,
                TODAY_BEN, LAST_WEEK_BEN));
        List<AppointmentPage.Row> expected = List.of(
                new DayHeading(LocalDate.of(2026, 10, 12), 1),
                new Entry(1, MONDAY_ALEX),
                new DayHeading(LocalDate.of(2026, 10, 13), 0),
                new DayHeading(LocalDate.of(2026, 10, 14), 0),
                new DayHeading(LocalDate.of(2026, 10, 15), 0),
                new DayHeading(TODAY, 2),
                new Entry(2, TODAY_BEN),
                new Entry(3, TODAY_ALEX),
                new DayHeading(LocalDate.of(2026, 10, 17), 0),
                new DayHeading(LocalDate.of(2026, 10, 18), 0));
        assertEquals(expected, page.getRows());
        assertEquals(3, page.getEntryCount());
        assertEquals(THIS_WEEK, page.getWeek());
    }

    @Test
    public void getRows_emptyWeek_onlyHeadings() {
        AppointmentPage page = new AppointmentPage(THIS_WEEK.next().next(), ALL);
        assertEquals(7, page.getRows().size());
        assertTrue(page.getRows().stream().allMatch(row -> row instanceof DayHeading heading
                && heading.appointmentCount() == 0));
        assertEquals(0, page.getEntryCount());
        assertEquals(OptionalInt.empty(), page.firstEntryIndex());
        assertEquals(OptionalInt.empty(), page.lastEntryIndex());
        assertEquals(OptionalInt.empty(), page.defaultEntryIndex(TODAY));
    }

    @Test
    public void getEntryAt_headingsAndOutOfRange_returnEmpty() {
        AppointmentPage page = new AppointmentPage(THIS_WEEK, ALL);
        assertEquals(Optional.empty(), page.getEntryAt(0));
        assertEquals(Optional.of(MONDAY_ALEX), page.getEntryAt(1));
        assertEquals(Optional.empty(), page.getEntryAt(-1));
        assertEquals(Optional.empty(), page.getEntryAt(page.getRows().size()));
    }

    @Test
    public void firstAndLastEntryIndex_skipHeadings() {
        AppointmentPage page = new AppointmentPage(THIS_WEEK, ALL);
        assertEquals(OptionalInt.of(1), page.firstEntryIndex());
        assertEquals(OptionalInt.of(7), page.lastEntryIndex());
    }

    @Test
    public void entryIndexAfterAndBefore_skipHeadingsAndStopAtEnds() {
        AppointmentPage page = new AppointmentPage(THIS_WEEK, ALL);
        // from Monday's appointment, Down skips the empty days to today's first one
        assertEquals(OptionalInt.of(6), page.entryIndexAfter(1));
        assertEquals(OptionalInt.of(7), page.entryIndexAfter(6));
        assertEquals(OptionalInt.empty(), page.entryIndexAfter(7));
        assertEquals(OptionalInt.of(1), page.entryIndexAfter(-1));

        assertEquals(OptionalInt.of(6), page.entryIndexBefore(7));
        assertEquals(OptionalInt.of(1), page.entryIndexBefore(6));
        assertEquals(OptionalInt.empty(), page.entryIndexBefore(1));
        assertEquals(OptionalInt.empty(), page.entryIndexBefore(-1));
        assertEquals(OptionalInt.of(7), page.entryIndexBefore(page.getRows().size() + 5));
    }

    @Test
    public void indexOf_findsSameEntryAfterPatientEdit() {
        AppointmentPage page = new AppointmentPage(THIS_WEEK, ALL);
        assertEquals(OptionalInt.of(7), page.indexOf(TODAY_ALEX));
        assertEquals(OptionalInt.empty(), page.indexOf(NEXT_WEEK_ALEX));

        Patient renamedAlex = new PatientBuilder(ALEX).withName("Alexandra").build();
        assertEquals(OptionalInt.of(7), page.indexOf(entry(renamedAlex, "2026-10-16 09:30")));
    }

    @Test
    public void defaultEntryIndex_thisWeek_firstFromToday() {
        AppointmentPage page = new AppointmentPage(THIS_WEEK, ALL);
        // Monday's appointment is past, so today's first one is chosen
        assertEquals(OptionalInt.of(6), page.defaultEntryIndex(TODAY));
        // after the last appointment of the week, falls back to the first one
        assertEquals(OptionalInt.of(1), page.defaultEntryIndex(TODAY.plusDays(1)));
    }

    @Test
    public void defaultEntryIndex_otherWeek_firstOfWeek() {
        AppointmentPage page = new AppointmentPage(THIS_WEEK.next(), ALL);
        assertEquals(OptionalInt.of(5), page.defaultEntryIndex(TODAY));
        assertEquals(Optional.of(NEXT_WEEK_ALEX), page.getEntryAt(5));
    }

    private static PatientAppointment entry(Patient patient, String appointment) {
        return new PatientAppointment(patient, new Appointment(appointment));
    }
}
