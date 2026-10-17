package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.AppointmentSchedule;

public class DetailAppointmentsTest {

    /** Midnight today, so that every appointment today is still to come. */
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 10, 16, 0, 0);

    private static final Appointment FIRST_VISIT = new Appointment("2026-10-05 09:30");
    private static final Appointment SECOND_VISIT = new Appointment("2026-10-07");
    private static final Appointment THIRD_VISIT = new Appointment("2026-10-09 09:30");
    private static final Appointment YESTERDAY = new Appointment("2026-10-15 16:00");
    private static final Appointment TODAY_EARLY = new Appointment("2026-10-16 00:00");
    private static final Appointment NEXT_WEEK = new Appointment("2026-10-23 09:30");
    private static final Appointment NEXT_MONTH = new Appointment("2026-11-02");

    @Test
    public void of_null_throwsNullPointerException() {
        AppointmentSchedule schedule = AppointmentSchedule.empty();
        assertThrows(NullPointerException.class, () -> DetailAppointments.of(null, NOW, Optional.empty()));
        assertThrows(NullPointerException.class, () -> DetailAppointments.of(schedule, null, Optional.empty()));
        assertThrows(NullPointerException.class, () -> DetailAppointments.of(schedule, NOW, null));
    }

    @Test
    public void of_noAppointments_nothingListed() {
        DetailAppointments appointments = DetailAppointments.of(AppointmentSchedule.empty(), NOW,
                Optional.empty());
        assertEquals(List.of(), appointments.getUpcoming());
        assertEquals(List.of(), appointments.getPastShown());
        assertEquals(0, appointments.getPastCount());
        assertEquals("None upcoming", appointments.describeUpcoming());
    }

    @Test
    public void of_mixedAppointments_upcomingInTimeOrderAndLatestTwoPastFirst() {
        DetailAppointments appointments = DetailAppointments.of(schedule(NEXT_MONTH, FIRST_VISIT, TODAY_EARLY,
                THIRD_VISIT, NEXT_WEEK, SECOND_VISIT, YESTERDAY), NOW, Optional.empty());
        // an appointment earlier today still counts as upcoming, as only dates are compared
        assertEquals(List.of(TODAY_EARLY, NEXT_WEEK, NEXT_MONTH), appointments.getUpcoming());
        assertEquals(List.of(YESTERDAY, THIRD_VISIT), appointments.getPastShown());
        assertEquals(4, appointments.getPastCount());
        assertEquals("3 upcoming", appointments.describeUpcoming());
        assertEquals("last 2 of 4, latest first", appointments.describePast());
    }

    @Test
    public void of_fewPastAppointments_allListed() {
        DetailAppointments appointments = DetailAppointments.of(schedule(YESTERDAY, NEXT_WEEK), NOW,
                Optional.empty());
        assertEquals(List.of(YESTERDAY), appointments.getPastShown());
        assertEquals("1 of 1", appointments.describePast());
        assertEquals("1 upcoming", appointments.describeUpcoming());
    }

    @Test
    public void of_olderMarkedAppointment_alsoListed() {
        AppointmentSchedule schedule = schedule(FIRST_VISIT, SECOND_VISIT, THIRD_VISIT, YESTERDAY);
        DetailAppointments appointments = DetailAppointments.of(schedule, NOW, Optional.of(FIRST_VISIT));
        assertEquals(List.of(YESTERDAY, THIRD_VISIT, FIRST_VISIT), appointments.getPastShown());
        assertEquals("3 of 4, latest first", appointments.describePast());

        // a marked appointment that is listed anyway changes nothing
        appointments = DetailAppointments.of(schedule, NOW, Optional.of(YESTERDAY));
        assertEquals(List.of(YESTERDAY, THIRD_VISIT), appointments.getPastShown());
        assertEquals("last 2 of 4, latest first", appointments.describePast());
    }

    @Test
    public void of_earlierToday_listedAsPast() {
        Appointment todayNoTime = new Appointment("2026-10-16");
        Appointment todayLater = new Appointment("2026-10-16 14:00");
        DetailAppointments appointments = DetailAppointments.of(schedule(YESTERDAY, TODAY_EARLY, todayNoTime,
                todayLater), LocalDateTime.of(2026, 10, 16, 10, 0), Optional.empty());

        // an appointment today with no time has not passed
        assertEquals(List.of(todayNoTime, todayLater), appointments.getUpcoming());
        assertEquals(List.of(TODAY_EARLY, YESTERDAY), appointments.getPastShown());
        assertEquals(2, appointments.getPastCount());
    }

    private static AppointmentSchedule schedule(Appointment... appointments) {
        return new AppointmentSchedule(List.of(appointments));
    }
}
