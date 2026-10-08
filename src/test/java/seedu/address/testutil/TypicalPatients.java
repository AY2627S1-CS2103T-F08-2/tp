package seedu.address.testutil;

import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ID_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ID_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_MEDICAL_HISTORY_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NEXT_APPOINTMENT_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import seedu.address.model.AddressBook;
import seedu.address.model.patient.Patient;

/**
 * A utility class containing a list of {@code Patient} objects to be used in tests.
 */
public class TypicalPatients {

    public static final Patient ALICE = new PatientBuilder().withId("S1234567A").withName("Alice Pauline")
            .withAddress("123, Jurong West Ave 6, #08-111")
            .withPhone("94351253")
            .withMedicalHistory("Type 2 diabetes").build();
    public static final Patient BENSON = new PatientBuilder().withId("S2345678B").withName("Benson Meier")
            .withAddress("311, Clementi Ave 2, #02-25")
            .withPhone("98765432")
            .withMedicalHistory("Hypertension, allergic to penicillin")
            .withNextAppointment("2026-10-05 09:30").build();
    public static final Patient CARL = new PatientBuilder().withId("S3456789C").withName("Carl Kurz")
            .withPhone("95352563").withAddress("wall street").build();
    public static final Patient DANIEL = new PatientBuilder().withId("S4567890D").withName("Daniel Meier")
            .withPhone("87652533").withAddress("10th street").withNextAppointment("2026-10-06").build();
    public static final Patient ELLE = new PatientBuilder().withId("T0123456E").withName("Elle Meyer")
            .withPhone("9482224").withAddress("michegan ave").build();
    public static final Patient FIONA = new PatientBuilder().withId("T1234567F").withName("Fiona Kunz")
            .withPhone("9482427").withAddress("little tokyo").build();
    public static final Patient GEORGE = new PatientBuilder().withId("G1234567G").withName("George Best")
            .withPhone("9482442").withAddress("4th street").build();

    // Manually added
    public static final Patient HOON = new PatientBuilder().withId("S5678901H").withName("Hoon Meier")
            .withPhone("8482424").withAddress("little india").build();
    public static final Patient IDA = new PatientBuilder().withId("S6789012I").withName("Ida Mueller")
            .withPhone("8482131").withAddress("chicago ave").build();

    // Manually added - Patient's details found in {@code CommandTestUtil}
    public static final Patient AMY = new PatientBuilder().withId(VALID_ID_AMY).withName(VALID_NAME_AMY)
            .withPhone(VALID_PHONE_AMY).withAddress(VALID_ADDRESS_AMY).build();
    public static final Patient BOB = new PatientBuilder().withId(VALID_ID_BOB).withName(VALID_NAME_BOB)
            .withPhone(VALID_PHONE_BOB).withAddress(VALID_ADDRESS_BOB).withMedicalHistory(VALID_MEDICAL_HISTORY_BOB)
            .withNextAppointment(VALID_NEXT_APPOINTMENT_BOB).build();

    public static final String KEYWORD_MATCHING_MEIER = "Meier"; // A keyword that matches MEIER

    private TypicalPatients() {} // prevents instantiation

    /**
     * Returns an {@code AddressBook} with all the typical patients.
     */
    public static AddressBook getTypicalAddressBook() {
        AddressBook ab = new AddressBook();
        for (Patient patient : getTypicalPatients()) {
            ab.addPatient(patient);
        }
        return ab;
    }

    public static List<Patient> getTypicalPatients() {
        return new ArrayList<>(Arrays.asList(ALICE, BENSON, CARL, DANIEL, ELLE, FIONA, GEORGE));
    }
}
