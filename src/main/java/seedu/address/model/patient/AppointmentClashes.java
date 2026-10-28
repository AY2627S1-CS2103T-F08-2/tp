package seedu.address.model.patient;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collection;
import java.util.List;

/**
 * Finds exact date and time clashes between undone appointments, independently of patient and appointment identity.
 * An appointment with no time never clashes, since its time is not known.
 * Callers supply all appointments, including those hidden by a view filter.
 */
public final class AppointmentClashes {

    private AppointmentClashes() {}

    /**
     * Returns other undone appointments at the target's date and time in global display order, or none for a done
     * target or one with no time.
     */
    public static List<PatientAppointment> find(Collection<PatientAppointment> appointments,
            PatientAppointment target) {
        requireAllNonNull(appointments, target);
        if (target.getAppointment().isDone() || target.getAppointment().getTime().isEmpty()) {
            return List.of();
        }
        return appointments.stream()
                .filter(entry -> !entry.isSameEntry(target))
                .filter(entry -> !entry.getAppointment().isDone())
                .filter(entry -> entry.getAppointment().hasSameDateTime(target.getAppointment()))
                .sorted()
                .toList();
    }
}
