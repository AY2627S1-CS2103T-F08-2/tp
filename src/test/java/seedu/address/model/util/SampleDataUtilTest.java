package seedu.address.model.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.model.ReadOnlyLaiNurse;
import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.AppointmentDetails;

/**
 * Tests that the sample appointments are anchored to the week containing the given date. The anchors cover
 * the week boundaries (Monday and Sunday), and Sundays whose weeks cross a month and a year boundary. The
 * expected appointments are built in one place, {@code expectedAppointmentsFor}, by patient, so that a change
 * to the sample data only requires updating it.
 */
public class SampleDataUtilTest {

    @Test
    public void getSampleLaiNurse_mondayAnchor_appointmentsOnKnownDates() {
        // A Monday anchors the week to itself: Monday 2026-10-05 to Sunday 2026-10-11.
        assertSampleAppointments("2026-10-05", "2026-10-05");
    }

    @Test
    public void getSampleLaiNurse_sundayAnchor_appointmentsInSameWeekAsMonday() {
        // A Sunday must roll back to the Monday six days earlier (2026-10-05), not forward to the next one.
        assertSampleAppointments("2026-10-11", "2026-10-05");
    }

    @Test
    public void getSampleLaiNurse_midweekAnchor_appointmentsOnKnownDates() {
        // A midweek day belongs to the week that started on the Monday before it.
        assertSampleAppointments("2026-10-08", "2026-10-05");
    }

    @Test
    public void getSampleLaiNurse_sundayAcrossMonthBoundary_appointmentsInPreviousMonthWeek() {
        // Sunday 2026-11-01 belongs to a week whose Monday, 2026-10-26, is in the previous month.
        assertSampleAppointments("2026-11-01", "2026-10-26");
    }

    @Test
    public void getSampleLaiNurse_sundayAcrossYearBoundary_appointmentsInPreviousYearWeek() {
        // Sunday 2027-01-03 belongs to a week whose Monday, 2026-12-28, is in the previous year.
        assertSampleAppointments("2027-01-03", "2026-12-28");
    }

    /**
     * Asserts that the sample data anchored to {@code today} gives each patient the sample appointments of the
     * week starting on {@code monday}: three in that week, and the monthly blood test on the Monday four weeks later.
     */
    private static void assertSampleAppointments(String today, String monday) {
        ReadOnlyLaiNurse laiNurse = SampleDataUtil.getSampleLaiNurse(LocalDate.parse(today));
        Map<String, List<Appointment>> actualAppointments = laiNurse.getPatientList().stream()
                .collect(Collectors.toMap(patient -> patient.getId().value,
                        patient -> patient.getSchedule().asList()));
        assertEquals(expectedAppointmentsFor(LocalDate.parse(monday)), actualAppointments);
    }

    /**
     * Builds the sample appointments expected for the week starting on {@code monday}, by patient ID: the insulin
     * review at Monday 09:30 and the monthly blood test four weeks later for Alex Yeoh (S1234567A), an appointment
     * on the Tuesday for Bernice Yu (S2345678B), and the wound dressing at Wednesday 14:00 for Irfan Ibrahim
     * (G1234567E). The other three patients have none.
     */
    private static Map<String, List<Appointment>> expectedAppointmentsFor(LocalDate monday) {
        return Map.of(
                "S1234567A", List.of(
                        appointment(monday + " 09:30", "Insulin review"),
                        appointment(monday.plusWeeks(4).toString(), "Monthly blood test")),
                "S2345678B", List.of(new Appointment(monday.plusDays(1).toString())),
                "G1234567E", List.of(
                        appointment(monday.plusDays(2) + " 14:00", "Dress the wound on the left foot")),
                "T0123456C", List.of(),
                "S3456789D", List.of(),
                "S4567890F", List.of());
    }

    private static Appointment appointment(String dateTime, String details) {
        return new Appointment(dateTime, Optional.of(new AppointmentDetails(details)));
    }
}
