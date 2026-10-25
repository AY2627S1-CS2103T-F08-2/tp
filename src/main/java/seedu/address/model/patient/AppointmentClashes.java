package seedu.address.model.patient;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collection;
import java.util.List;

/**
 * Finds exact date/optional-time clashes independently of patient and appointment identity.
 * Callers supply all appointments, including those hidden by a view filter.
 */
public final class AppointmentClashes {

    private AppointmentClashes() {}

    /**
     * Returns the other appointments at the target's date/time, in the global display order.
     */
    public static List<PatientAppointment> find(Collection<PatientAppointment> appointments,
            PatientAppointment target) {
        requireAllNonNull(appointments, target);
        return appointments.stream()
                .filter(entry -> !entry.isSameEntry(target))
                .filter(entry -> entry.getAppointment().hasSameDateTime(target.getAppointment()))
                .sorted()
                .toList();
    }
}
