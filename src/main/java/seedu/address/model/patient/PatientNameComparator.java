package seedu.address.model.patient;

import java.util.Comparator;

/**
 * Compares patients for the patient view: by name, ignoring case, then by ID, so that patients with the same
 * name keep one fixed order.
 * Guarantees: immutable; stateless, so one shared instance is enough.
 */
public class PatientNameComparator implements Comparator<Patient> {

    /** The one order that the patient view uses. */
    public static final PatientNameComparator PATIENT_NAME_ORDER = new PatientNameComparator();

    private PatientNameComparator() {}

    @Override
    public int compare(Patient first, Patient second) {
        int byName = String.CASE_INSENSITIVE_ORDER.compare(
                first.getName().fullName, second.getName().fullName);
        if (byName != 0) {
            return byName;
        }
        return first.getId().value.compareTo(second.getId().value);
    }
}
