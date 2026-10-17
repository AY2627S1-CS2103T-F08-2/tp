package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    /** 08:00 today, before Alex's appointment today, so that nothing today has passed. */
    private static final LocalDateTime NOW = TODAY.atTime(8, 0);

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
        assertThrows(NullPointerException.class, () -> AppointmentPage.ofWeek(null, ALL, NOW));
        assertThrows(NullPointerException.class, () -> AppointmentPage.ofWeek(THIS_WEEK, null, NOW));
        assertThrows(NullPointerException.class, () -> AppointmentPage.ofWeek(THIS_WEEK, ALL, null));
    }

    @Test
    public void getRows_headingForEachDayAndAppointmentsNumberedAcrossWeek() {
        // in another order, to show that the page sorts them
        AppointmentPage page = AppointmentPage.ofWeek(THIS_WEEK, List.of(TODAY_ALEX, NEXT_WEEK_ALEX, MONDAY_ALEX,
                TODAY_BEN, LAST_WEEK_BEN), NOW);
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
    }

    @Test
    public void getRows_emptyWeek_onlyHeadings() {
        AppointmentPage page = AppointmentPage.ofWeek(THIS_WEEK.next().next(), ALL, NOW);
        assertEquals(7, page.getRows().size());
        assertTrue(page.getRows().stream().allMatch(row -> row instanceof DayHeading heading
                && heading.appointmentCount() == 0));
        assertEquals(0, page.getEntryCount());
        assertEquals(OptionalInt.empty(), page.firstEntryIndex());
        assertEquals(OptionalInt.empty(), page.lastEntryIndex());
        assertEquals(OptionalInt.empty(), page.defaultEntryIndex());
    }

    @Test
    public void getEntryAt_headingsAndOutOfRange_returnEmpty() {
        AppointmentPage page = AppointmentPage.ofWeek(THIS_WEEK, ALL, NOW);
        assertEquals(Optional.empty(), page.getEntryAt(0));
        assertEquals(Optional.of(MONDAY_ALEX), page.getEntryAt(1));
        assertEquals(Optional.empty(), page.getEntryAt(-1));
        assertEquals(Optional.empty(), page.getEntryAt(page.getRows().size()));
    }

    @Test
    public void firstAndLastEntryIndex_skipHeadings() {
        AppointmentPage page = AppointmentPage.ofWeek(THIS_WEEK, ALL, NOW);
        assertEquals(OptionalInt.of(1), page.firstEntryIndex());
        assertEquals(OptionalInt.of(7), page.lastEntryIndex());
    }

    @Test
    public void entryIndexAfterAndBefore_skipHeadingsAndStopAtEnds() {
        AppointmentPage page = AppointmentPage.ofWeek(THIS_WEEK, ALL, NOW);
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
        AppointmentPage page = AppointmentPage.ofWeek(THIS_WEEK, ALL, NOW);
        assertEquals(OptionalInt.of(7), page.indexOf(TODAY_ALEX));
        assertEquals(OptionalInt.empty(), page.indexOf(NEXT_WEEK_ALEX));

        Patient renamedAlex = new PatientBuilder(ALEX).withName("Alexandra").build();
        assertEquals(OptionalInt.of(7), page.indexOf(entry(renamedAlex, "2026-10-16 09:30")));
    }

    @Test
    public void defaultEntryIndex_thisWeek_firstFromToday() {
        AppointmentPage page = AppointmentPage.ofWeek(THIS_WEEK, ALL, NOW);
        // Monday's appointment is past, so today's first one is chosen
        assertEquals(OptionalInt.of(6), page.defaultEntryIndex());
        // at 10:00, Alex's 09:30 has passed, so Ben's appointment with no time, now after it, is chosen
        AppointmentPage atTen = AppointmentPage.ofWeek(THIS_WEEK, ALL, TODAY.atTime(10, 0));
        assertEquals(OptionalInt.of(7), atTen.defaultEntryIndex());
        assertEquals(Optional.of(TODAY_BEN), atTen.getEntryAt(7));
        // after the last appointment of the week, falls back to the first one
        AppointmentPage onSaturday = AppointmentPage.ofWeek(THIS_WEEK, ALL, TODAY.plusDays(1).atTime(8, 0));
        assertEquals(OptionalInt.of(1), onSaturday.defaultEntryIndex());
    }

    @Test
    public void defaultEntryIndex_otherWeek_firstOfWeek() {
        AppointmentPage page = AppointmentPage.ofWeek(THIS_WEEK.next(), ALL, NOW);
        assertEquals(OptionalInt.of(5), page.defaultEntryIndex());
        assertEquals(Optional.of(NEXT_WEEK_ALEX), page.getEntryAt(5));
    }

    @Test
    public void ofRange_null_throwsNullPointerException() {
        LocalDate date = TODAY;
        assertThrows(NullPointerException.class, () -> AppointmentPage.ofRange(null, date, ALL, NOW));
        assertThrows(NullPointerException.class, () -> AppointmentPage.ofRange(date, null, ALL, NOW));
        assertThrows(NullPointerException.class, () -> AppointmentPage.ofRange(date, date, null, NOW));
        assertThrows(NullPointerException.class, () -> AppointmentPage.ofRange(date, date, ALL, null));
    }

    @Test
    public void ofRange_acrossWeeks_headingOnlyForDaysWithAppointments() {
        AppointmentPage page = AppointmentPage.ofRange(LocalDate.of(2026, 10, 11), LocalDate.of(2026, 10, 23), ALL,
                NOW);
        List<AppointmentPage.Row> expected = List.of(
                new DayHeading(LocalDate.of(2026, 10, 11), 1),
                new Entry(1, LAST_WEEK_BEN),
                new DayHeading(LocalDate.of(2026, 10, 12), 1),
                new Entry(2, MONDAY_ALEX),
                new DayHeading(TODAY, 2),
                new Entry(3, TODAY_BEN),
                new Entry(4, TODAY_ALEX),
                new DayHeading(LocalDate.of(2026, 10, 23), 1),
                new Entry(5, NEXT_WEEK_ALEX));
        assertEquals(expected, page.getRows());
        assertEquals(5, page.getEntryCount());
    }

    @Test
    public void ofRange_bothEndsIncluded() {
        // a one-day range
        AppointmentPage page = AppointmentPage.ofRange(TODAY, TODAY, ALL, NOW);
        assertEquals(List.of(new DayHeading(TODAY, 2), new Entry(1, TODAY_BEN), new Entry(2, TODAY_ALEX)),
                page.getRows());
        // a range with no end
        page = AppointmentPage.ofRange(TODAY, LocalDate.MAX, ALL, NOW);
        assertEquals(3, page.getEntryCount());
    }

    @Test
    public void ofRange_noAppointments_emptyPage() {
        AppointmentPage page = AppointmentPage.ofRange(LocalDate.of(2027, 1, 1), LocalDate.of(2027, 1, 31), ALL, NOW);
        assertEquals(List.of(), page.getRows());
        assertEquals(0, page.getEntryCount());
        assertEquals(OptionalInt.empty(), page.firstEntryIndex());
        assertEquals(OptionalInt.empty(), page.defaultEntryIndex());
        assertEquals(0, page.headingIndexOf(0));
    }

    @Test
    public void defaultEntryIndex_rangeSpanningToday_firstFromToday() {
        AppointmentPage page = AppointmentPage.ofRange(LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31), ALL, NOW);
        // the rows are Sun 11 (Ben), Mon 12 (Alex), today (Ben, Alex) and Fri 23 (Alex)
        assertEquals(OptionalInt.of(5), page.defaultEntryIndex());
        // a page whose days all come after today starts at its first appointment
        page = AppointmentPage.ofRange(LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31), ALL,
                LocalDate.of(2026, 10, 1).atStartOfDay());
        assertEquals(OptionalInt.of(1), page.defaultEntryIndex());
    }

    @Test
    public void getRows_today_noTimeRightAboveNextAppointment() {
        Patient carl = new PatientBuilder().withId("S3456789C").withName("Carl")
                .withAppointments("2026-10-16 14:00").build();
        PatientAppointment todayCarl = entry(carl, "2026-10-16 14:00");
        // at 10:00, Alex's 09:30 has passed, so Ben's appointment with no time comes after it, above Carl's
        AppointmentPage page = AppointmentPage.ofRange(TODAY, TODAY, List.of(todayCarl, TODAY_BEN, TODAY_ALEX),
                TODAY.atTime(10, 0));
        assertEquals(List.of(new DayHeading(TODAY, 3), new Entry(1, TODAY_ALEX), new Entry(2, TODAY_BEN),
                new Entry(3, todayCarl)), page.getRows());
        assertEquals(OptionalInt.of(2), page.defaultEntryIndex());
    }

    @Test
    public void getRows_otherDays_noTimeFirst() {
        // on Saturday, today's appointments are all a day old, so Ben's with no time comes first again
        AppointmentPage page = AppointmentPage.ofRange(TODAY, TODAY, ALL, TODAY.plusDays(1).atTime(10, 0));
        assertEquals(List.of(new DayHeading(TODAY, 2), new Entry(1, TODAY_BEN), new Entry(2, TODAY_ALEX)),
                page.getRows());
    }

    @Test
    public void headingIndexOf_entriesAndHeadings() {
        AppointmentPage page = AppointmentPage.ofWeek(THIS_WEEK, ALL, NOW);
        assertEquals(0, page.headingIndexOf(1));
        assertEquals(5, page.headingIndexOf(7));
        assertEquals(5, page.headingIndexOf(5));
        assertEquals(0, page.headingIndexOf(-1));
        // past the end, the last row's heading
        assertEquals(9, page.headingIndexOf(100));
    }

    private static PatientAppointment entry(Patient patient, String appointment) {
        return new PatientAppointment(patient, new Appointment(appointment));
    }
}
