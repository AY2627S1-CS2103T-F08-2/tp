package seedu.address.model.patient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PatientBuilder;

public class PatientNameComparatorTest {

    private final PatientNameComparator comparator = PatientNameComparator.PATIENT_NAME_ORDER;

    @Test
    public void compare_differentNames_ordersByNameIgnoringCase() {
        Patient alice = patient("S00000001A", "alice Tan");
        Patient bob = patient("S00000002A", "Bob Ong");
        Patient zac = patient("S00000003A", "Zac Lim");

        assertTrue(comparator.compare(alice, bob) < 0);
        assertTrue(comparator.compare(bob, zac) < 0);
        assertTrue(comparator.compare(zac, alice) > 0);
    }

    @Test
    public void compare_sameNameIgnoringCase_breaksTieById() {
        Patient first = patient("S00000001A", "alice Tan");
        Patient second = patient("S00000002A", "ALICE TAN");
        Patient twin = patient("S00000001A", "alice Tan");

        assertTrue(comparator.compare(first, second) < 0);
        assertTrue(comparator.compare(second, first) > 0);
        assertEquals(0, comparator.compare(first, twin));
    }

    /** Returns a patient with the given ID and name, with the other details filled in. */
    private static Patient patient(String id, String name) {
        return new PatientBuilder().withId(id).withName(name)
                .withPhone("90000000").withAddress("Blk 1 Clementi Rd").build();
    }
}
