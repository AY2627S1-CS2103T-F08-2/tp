package seedu.address.model.patient;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collection;
import java.util.List;

/**
 * Finds exact date and time clashes between undone appointments, independently of patient and appointment identity.
 * An appointment with no time never clashes, since its time is not known, and neither does an archived patient's.
 * Callers supply all appointments, including those hidden by a view filter.
 */
public final class AppointmentClashes {

    private AppointmentClashes() {}

    /**
     * Returns the other undone appointments of patients who are not archived at the target's date and time, in
     * global display order, or none for a done target, one with no time, or one of an archived patient.
     */
    public static List<PatientAppointment> find(Collection<PatientAppointment> appointments,
            PatientAppointment target) {
        requireAllNonNull(appointments, target);
        if (!canClash(target)) {
            return List.of();
        }
        return appointments.stream()
                .filter(AppointmentClashes::canClash)
                .filter(entry -> !entry.isSameEntry(target))
                .filter(entry -> entry.getAppointment().hasSameDateTime(target.getAppointment()))
                .sorted()
                .toList();
    }

    /**
     * Returns true if {@code entry} can clash: it is undone, has a time, and is of a patient who is not archived.
     */
    private static boolean canClash(PatientAppointment entry) {
        return !entry.getAppointment().isDone() && entry.getAppointment().getTime().isPresent()
                && !entry.getPatient().isArchived();
    }
}
