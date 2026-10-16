package seedu.address.testutil;

import seedu.address.model.LaiNurse;
import seedu.address.model.patient.Patient;

/**
 * A utility class to help with building LaiNurse objects.
 * Example usage: <br>
 *     {@code LaiNurse laiNurse = new LaiNurseBuilder().withPatient("John", "Doe").build();}
 */
public class LaiNurseBuilder {

    private LaiNurse laiNurse;

    public LaiNurseBuilder() {
        laiNurse = new LaiNurse();
    }

    public LaiNurseBuilder(LaiNurse laiNurse) {
        this.laiNurse = laiNurse;
    }

    /**
     * Adds a new {@code Patient} to the {@code LaiNurse} that we are building.
     */
    public LaiNurseBuilder withPatient(Patient patient) {
        laiNurse.addPatient(patient);
        return this;
    }

    public LaiNurse build() {
        return laiNurse;
    }
}
