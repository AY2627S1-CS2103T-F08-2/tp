package seedu.address.model;

import java.util.function.Predicate;

import seedu.address.model.patient.Patient;

/**
 * Which patients the patient list shows: those who are not archived, all of them, or only archived ones. A filter,
 * such as one that {@code find} sets, then chooses among them. {@code viewappt} also uses a scope, but only as part of
 * the filter of the appointment view, as it uses dates and a patient ID.
 */
public enum PatientScope {
    /** Patients who are not archived. */
    UNARCHIVED(patient -> !patient.isArchived()),
    /** All patients, with archived ones last. */
    ALL(patient -> true),
    /** Only archived patients. */
    ARCHIVED(Patient::isArchived);

    /** The scope at start, and the one that {@code view} and a second Esc go back to. */
    public static final PatientScope DEFAULT = UNARCHIVED;

    private final Predicate<Patient> includes;

    PatientScope(Predicate<Patient> includes) {
        this.includes = includes;
    }

    /**
     * Returns true if this scope shows {@code patient}.
     */
    public boolean includes(Patient patient) {
        return includes.test(patient);
    }
}
