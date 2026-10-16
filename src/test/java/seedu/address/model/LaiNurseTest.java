package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPatients.ALICE;
import static seedu.address.testutil.TypicalPatients.getTypicalLaiNurse;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.exceptions.DuplicatePatientException;
import seedu.address.testutil.PatientBuilder;

public class LaiNurseTest {

    private final LaiNurse laiNurse = new LaiNurse();

    @Test
    public void constructor() {
        assertEquals(List.of(), laiNurse.getPatientList());
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> laiNurse.resetData(null));
    }

    @Test
    public void resetData_withValidReadOnlyLaiNurse_replacesData() {
        LaiNurse newData = getTypicalLaiNurse();
        laiNurse.resetData(newData);
        assertEquals(newData, laiNurse);
    }

    @Test
    public void resetData_withDuplicatePatients_throwsDuplicatePatientException() {
        // Two patients with the same identity fields
        Patient editedAlice = new PatientBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withMedicalHistory("Asthma")
                .build();
        List<Patient> newPatients = List.of(ALICE, editedAlice);
        LaiNurseStub newData = new LaiNurseStub(newPatients);

        assertThrows(DuplicatePatientException.class, () -> laiNurse.resetData(newData));
    }

    @Test
    public void hasPatient_nullPatient_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> laiNurse.hasPatient(null));
    }

    @Test
    public void hasPatient_patientNotInLaiNurse_returnsFalse() {
        assertFalse(laiNurse.hasPatient(ALICE));
    }

    @Test
    public void hasPatient_patientInLaiNurse_returnsTrue() {
        laiNurse.addPatient(ALICE);
        assertTrue(laiNurse.hasPatient(ALICE));
    }

    @Test
    public void hasPatient_patientWithSameIdentityFieldsInLaiNurse_returnsTrue() {
        laiNurse.addPatient(ALICE);
        Patient editedAlice = new PatientBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withMedicalHistory("Asthma")
                .build();
        assertTrue(laiNurse.hasPatient(editedAlice));
    }

    @Test
    public void findPatientById_nullId_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> laiNurse.findPatientById(null));
    }

    @Test
    public void findPatientById_idNotInLaiNurse_returnsEmpty() {
        assertEquals(Optional.empty(), laiNurse.findPatientById(ALICE.getId()));
    }

    @Test
    public void findPatientById_idInLaiNurse_returnsPatient() {
        laiNurse.addPatient(ALICE);
        assertEquals(Optional.of(ALICE), laiNurse.findPatientById(ALICE.getId()));
    }

    @Test
    public void getPatientList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> laiNurse.getPatientList().remove(0));
    }

    @Test
    public void toStringMethod() {
        String expected = LaiNurse.class.getCanonicalName() + "{patients=" + laiNurse.getPatientList() + "}";
        assertEquals(expected, laiNurse.toString());
    }

    /**
     * A stub ReadOnlyLaiNurse whose patients list can violate interface constraints.
     */
    private static class LaiNurseStub implements ReadOnlyLaiNurse {
        private final ObservableList<Patient> patients = FXCollections.observableArrayList();

        LaiNurseStub(Collection<Patient> patients) {
            this.patients.setAll(patients);
        }

        @Override
        public ObservableList<Patient> getPatientList() {
            return patients;
        }
    }

}
