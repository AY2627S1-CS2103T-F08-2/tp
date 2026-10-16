package seedu.address.ui;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.AppointmentSchedule;

/**
 * The appointments that the patient details list: every upcoming appointment, in time order, and the latest
 * past appointments, latest first. An appointment on today's date counts as upcoming.
 * Guarantees: immutable.
 */
public final class DetailAppointments {

    /** The number of past appointments that the details list, besides a marked one. */
    public static final int PAST_SHOWN = 2;

    private final List<Appointment> upcoming;
    private final List<Appointment> pastShown;
    private final int pastCount;
    private final boolean isOnlyLatestPastShown;

    private DetailAppointments(List<Appointment> upcoming, List<Appointment> pastShown, int pastCount,
            boolean isOnlyLatestPastShown) {
        this.upcoming = upcoming;
        this.pastShown = pastShown;
        this.pastCount = pastCount;
        this.isOnlyLatestPastShown = isOnlyLatestPastShown;
    }

    /**
     * Splits {@code schedule} into the appointments to list on {@code today}. A {@code marked} past appointment,
     * such as the one selected in the appointment view, is listed even if it is older than the latest ones.
     */
    public static DetailAppointments of(AppointmentSchedule schedule, LocalDate today,
            Optional<Appointment> marked) {
        requireAllNonNull(schedule, today, marked);
        List<Appointment> all = schedule.asList();
        List<Appointment> upcoming = all.stream()
                .filter(appointment -> !AppointmentFormatter.isPast(appointment, today))
                .sorted()
                .toList();
        List<Appointment> pastLatestFirst = all.stream()
                .filter(appointment -> AppointmentFormatter.isPast(appointment, today))
                .sorted(Comparator.reverseOrder())
                .toList();
        List<Appointment> pastShown = IntStream.range(0, pastLatestFirst.size())
                .filter(i -> i < PAST_SHOWN || marked.equals(Optional.of(pastLatestFirst.get(i))))
                .mapToObj(pastLatestFirst::get)
                .toList();
        boolean isOnlyLatestPastShown = pastShown.equals(
                pastLatestFirst.subList(0, Math.min(PAST_SHOWN, pastLatestFirst.size())));
        return new DetailAppointments(upcoming, pastShown, pastLatestFirst.size(), isOnlyLatestPastShown);
    }

    /**
     * Returns the upcoming appointments, in time order.
     */
    public List<Appointment> getUpcoming() {
        return upcoming;
    }

    /**
     * Returns the past appointments to list, latest first.
     */
    public List<Appointment> getPastShown() {
        return pastShown;
    }

    /**
     * Returns the number of past appointments, including those not listed.
     */
    public int getPastCount() {
        return pastCount;
    }

    /**
     * Returns the note beside the "Appointments" heading, such as "3 upcoming" or "None upcoming".
     */
    public String describeUpcoming() {
        return upcoming.isEmpty() ? "None upcoming" : upcoming.size() + " upcoming";
    }

    /**
     * Returns the note beside the "Past" heading, such as "1 of 1" when every past appointment is listed,
     * or "last 2 of 4, latest first" when only some are.
     */
    public String describePast() {
        if (pastShown.size() == pastCount) {
            return pastCount + " of " + pastCount;
        }
        return (isOnlyLatestPastShown ? "last " : "") + pastShown.size() + " of " + pastCount + ", latest first";
    }
}
