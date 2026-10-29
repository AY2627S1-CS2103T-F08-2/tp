package seedu.address.model.patient;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import seedu.address.model.Settings;

/**
 * Finds exact date and time clashes between undone appointments, independently of patient and appointment identity.
 * An appointment with no time never clashes, since its time is not known, and neither does an archived patient's.
 * Callers supply all appointments, including those hidden by a view filter.
 */
public final class AppointmentClashes {

    private AppointmentClashes() {}

    /**
     * Returns the other appointments that clash with the target, under the default settings: the undone appointments
     * of patients who are not archived at the target's date and time, in global display order, or none for a done
     * target, one with no time, or one of an archived patient.
     */
    public static List<PatientAppointment> find(Collection<PatientAppointment> appointments,
            PatientAppointment target) {
        return find(appointments, target, Settings.DEFAULT, LocalDateTime.now());
    }

    /**
     * Returns the other appointments that clash with the target at {@code now}, under {@code settings} (#216), in
     * global display order, or none if the target cannot clash. An appointment can clash if it has a time, and the
     * settings allow it: marked appointments, ones that have passed, and archived patients' each clash only if the
     * settings say so.
     */
    public static List<PatientAppointment> find(Collection<PatientAppointment> appointments,
            PatientAppointment target, Settings settings, LocalDateTime now) {
        requireAllNonNull(appointments, target, settings, now);
        if (!canClash(target, settings, now)) {
            return List.of();
        }
        return appointments.stream()
                .filter(entry -> canClash(entry, settings, now))
                .filter(entry -> !entry.isSameEntry(target))
                .filter(entry -> entry.getAppointment().hasSameDateTime(target.getAppointment()))
                .sorted()
                .toList();
    }

    /**
     * Returns true if {@code entry} can clash under {@code settings} at {@code now}: it has a time, and it is
     * unmarked, of a patient who is not archived, and still to come, unless the settings let it clash anyway. If past
     * appointments cannot clash, neither can marked ones, which count as past.
     */
    private static boolean canClash(PatientAppointment entry, Settings settings, LocalDateTime now) {
        Appointment appointment = entry.getAppointment();
        boolean isPast = appointment.hasPassed(now) || appointment.isDone();
        return appointment.getTime().isPresent()
                && (!appointment.isDone() || !settings.isMarkStoppingClashes())
                && (!entry.getPatient().isArchived() || settings.isArchivedClashing())
                && (!isPast || settings.isPastClashing());
    }
}
