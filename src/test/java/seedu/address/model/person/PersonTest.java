package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ID_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class PersonTest {

    private static final String VALID_MEDICAL_HISTORY = "Asthma";
    private static final String VALID_NEXT_APPOINTMENT = "2026-10-20 10:00";

    @Test
    public void constructor_nullField_throwsNullPointerException() {
        PatientId id = new PatientId("S1234567A");
        Name name = new Name("Alice");
        Phone phone = new Phone("94351253");
        Address address = new Address("Jurong West");

        assertThrows(NullPointerException.class, () ->
                new Person(null, name, phone, address, Optional.empty(), Optional.empty()));
        assertThrows(NullPointerException.class, () ->
                new Person(id, null, phone, address, Optional.empty(), Optional.empty()));
        assertThrows(NullPointerException.class, () ->
                new Person(id, name, null, address, Optional.empty(), Optional.empty()));
        assertThrows(NullPointerException.class, () ->
                new Person(id, name, phone, null, Optional.empty(), Optional.empty()));
        assertThrows(NullPointerException.class, () ->
                new Person(id, name, phone, address, null, Optional.empty()));
        assertThrows(NullPointerException.class, () ->
                new Person(id, name, phone, address, Optional.empty(), null));
    }

    @Test
    public void getOptionalFields_notGiven_returnsEmpty() {
        Person person = new PersonBuilder().build();
        assertEquals(Optional.empty(), person.getMedicalHistory());
        assertEquals(Optional.empty(), person.getNextAppointment());
    }

    @Test
    public void getOptionalFields_given_returnsValue() {
        Person person = new PersonBuilder().withMedicalHistory(VALID_MEDICAL_HISTORY)
                .withNextAppointment(VALID_NEXT_APPOINTMENT).build();
        assertEquals(Optional.of(new MedicalHistory(VALID_MEDICAL_HISTORY)), person.getMedicalHistory());
        assertEquals(Optional.of(new NextAppointment(VALID_NEXT_APPOINTMENT)), person.getNextAppointment());
    }

    @Test
    public void isSamePerson() {
        // same object -> returns true
        assertTrue(ALICE.isSamePerson(ALICE));

        // null -> returns false
        assertFalse(ALICE.isSamePerson(null));

        // same id, all other attributes different -> returns true
        Person editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB)
                .withAddress(VALID_ADDRESS_BOB).withMedicalHistory(VALID_MEDICAL_HISTORY)
                .withNextAppointment(VALID_NEXT_APPOINTMENT).build();
        assertTrue(ALICE.isSamePerson(editedAlice));

        // different id, all other attributes same -> returns false
        editedAlice = new PersonBuilder(ALICE).withId(VALID_ID_BOB).build();
        assertFalse(ALICE.isSamePerson(editedAlice));

        // id differs in case, all other attributes same -> returns true
        Person editedBob = new PersonBuilder(BOB).withId(VALID_ID_BOB.toLowerCase()).build();
        assertTrue(BOB.isSamePerson(editedBob));

        // id has surrounding spaces, all other attributes same -> returns true
        editedBob = new PersonBuilder(BOB).withId(" " + VALID_ID_BOB + " ").build();
        assertTrue(BOB.isSamePerson(editedBob));

        // same name, different id -> returns false
        editedBob = new PersonBuilder(BOB).withId("S9999999Z").build();
        assertFalse(BOB.isSamePerson(editedBob));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Person aliceCopy = new PersonBuilder(ALICE).build();
        assertTrue(ALICE.equals(aliceCopy));

        // same object -> returns true
        assertTrue(ALICE.equals(ALICE));

        // null -> returns false
        assertFalse(ALICE.equals(null));

        // different type -> returns false
        assertFalse(ALICE.equals(5));

        // different person -> returns false
        assertFalse(ALICE.equals(BOB));

        // different id -> returns false
        Person editedAlice = new PersonBuilder(ALICE).withId(VALID_ID_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different name -> returns false
        editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different phone -> returns false
        editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different address -> returns false
        editedAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different medical history -> returns false
        editedAlice = new PersonBuilder(ALICE).withMedicalHistory(VALID_MEDICAL_HISTORY).build();
        assertFalse(ALICE.equals(editedAlice));

        // different next appointment -> returns false
        editedAlice = new PersonBuilder(ALICE).withNextAppointment(VALID_NEXT_APPOINTMENT).build();
        assertFalse(ALICE.equals(editedAlice));

        // optional field present vs absent -> returns false
        assertFalse(new PersonBuilder().build().equals(
                new PersonBuilder().withMedicalHistory(VALID_MEDICAL_HISTORY).build()));
    }

    @Test
    public void hashCode_equalPersons_equalHashCodes() {
        assertEquals(BENSON.hashCode(), new PersonBuilder(BENSON).build().hashCode());
    }

    @Test
    public void toStringMethod() {
        String expected = Person.class.getCanonicalName() + "{id=" + BENSON.getId() + ", name=" + BENSON.getName()
                + ", phone=" + BENSON.getPhone() + ", address=" + BENSON.getAddress()
                + ", medicalHistory=" + BENSON.getMedicalHistory().get()
                + ", nextAppointment=" + BENSON.getNextAppointment().get()
                + ", remark=" + BENSON.getRemark() + "}";
        assertEquals(expected, BENSON.toString());
    }
}
