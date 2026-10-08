package seedu.address.model.patient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ID_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPatients.ALICE;
import static seedu.address.testutil.TypicalPatients.BENSON;
import static seedu.address.testutil.TypicalPatients.BOB;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PatientBuilder;

public class PatientTest {

    private static final String VALID_MEDICAL_HISTORY = "Asthma";
    private static final String VALID_NEXT_APPOINTMENT = "2026-10-20 10:00";

    @Test
    public void constructor_nullField_throwsNullPointerException() {
        PatientId id = new PatientId("S1234567A");
        Name name = new Name("Alice");
        Phone phone = new Phone("94351253");
        Address address = new Address("Jurong West");

        assertThrows(NullPointerException.class, () ->
                new Patient(null, name, phone, address, Optional.empty(), Optional.empty()));
        assertThrows(NullPointerException.class, () ->
                new Patient(id, null, phone, address, Optional.empty(), Optional.empty()));
        assertThrows(NullPointerException.class, () ->
                new Patient(id, name, null, address, Optional.empty(), Optional.empty()));
        assertThrows(NullPointerException.class, () ->
                new Patient(id, name, phone, null, Optional.empty(), Optional.empty()));
        assertThrows(NullPointerException.class, () ->
                new Patient(id, name, phone, address, null, Optional.empty()));
        assertThrows(NullPointerException.class, () ->
                new Patient(id, name, phone, address, Optional.empty(), null));
    }

    @Test
    public void getOptionalFields_notGiven_returnsEmpty() {
        Patient patient = new PatientBuilder().build();
        assertEquals(Optional.empty(), patient.getMedicalHistory());
        assertEquals(Optional.empty(), patient.getNextAppointment());
    }

    @Test
    public void getOptionalFields_given_returnsValue() {
        Patient patient = new PatientBuilder().withMedicalHistory(VALID_MEDICAL_HISTORY)
                .withNextAppointment(VALID_NEXT_APPOINTMENT).build();
        assertEquals(Optional.of(new MedicalHistory(VALID_MEDICAL_HISTORY)), patient.getMedicalHistory());
        assertEquals(Optional.of(new NextAppointment(VALID_NEXT_APPOINTMENT)), patient.getNextAppointment());
    }

    @Test
    public void isSamePatient() {
        // same object -> returns true
        assertTrue(ALICE.isSamePatient(ALICE));

        // null -> returns false
        assertFalse(ALICE.isSamePatient(null));

        // same id, all other attributes different -> returns true
        Patient editedAlice = new PatientBuilder(ALICE).withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB)
                .withAddress(VALID_ADDRESS_BOB).withMedicalHistory(VALID_MEDICAL_HISTORY)
                .withNextAppointment(VALID_NEXT_APPOINTMENT).build();
        assertTrue(ALICE.isSamePatient(editedAlice));

        // different id, all other attributes same -> returns false
        editedAlice = new PatientBuilder(ALICE).withId(VALID_ID_BOB).build();
        assertFalse(ALICE.isSamePatient(editedAlice));

        // id differs in case, all other attributes same -> returns true
        Patient editedBob = new PatientBuilder(BOB).withId(VALID_ID_BOB.toLowerCase()).build();
        assertTrue(BOB.isSamePatient(editedBob));

        // id has surrounding spaces, all other attributes same -> returns true
        editedBob = new PatientBuilder(BOB).withId(" " + VALID_ID_BOB + " ").build();
        assertTrue(BOB.isSamePatient(editedBob));

        // same name, different id -> returns false
        editedBob = new PatientBuilder(BOB).withId("S9999999Z").build();
        assertFalse(BOB.isSamePatient(editedBob));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Patient aliceCopy = new PatientBuilder(ALICE).build();
        assertTrue(ALICE.equals(aliceCopy));

        // same object -> returns true
        assertTrue(ALICE.equals(ALICE));

        // null -> returns false
        assertFalse(ALICE.equals(null));

        // different type -> returns false
        assertFalse(ALICE.equals(5));

        // different patient -> returns false
        assertFalse(ALICE.equals(BOB));

        // different id -> returns false
        Patient editedAlice = new PatientBuilder(ALICE).withId(VALID_ID_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different name -> returns false
        editedAlice = new PatientBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different phone -> returns false
        editedAlice = new PatientBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different address -> returns false
        editedAlice = new PatientBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different medical history -> returns false
        editedAlice = new PatientBuilder(ALICE).withMedicalHistory(VALID_MEDICAL_HISTORY).build();
        assertFalse(ALICE.equals(editedAlice));

        // different next appointment -> returns false
        editedAlice = new PatientBuilder(ALICE).withNextAppointment(VALID_NEXT_APPOINTMENT).build();
        assertFalse(ALICE.equals(editedAlice));

        // optional field present vs absent -> returns false
        assertFalse(new PatientBuilder().build().equals(
                new PatientBuilder().withMedicalHistory(VALID_MEDICAL_HISTORY).build()));
    }

    @Test
    public void hashCode_equalPatients_equalHashCodes() {
        assertEquals(BENSON.hashCode(), new PatientBuilder(BENSON).build().hashCode());
    }

    @Test
    public void toStringMethod() {
        String expected = Patient.class.getCanonicalName() + "{id=" + BENSON.getId() + ", name=" + BENSON.getName()
                + ", phone=" + BENSON.getPhone() + ", address=" + BENSON.getAddress()
                + ", medicalHistory=" + BENSON.getMedicalHistory().get()
                + ", nextAppointment=" + BENSON.getNextAppointment().get() + "}";
        assertEquals(expected, BENSON.toString());
    }
}
