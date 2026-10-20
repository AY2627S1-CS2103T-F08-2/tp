package seedu.address.model.patient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PatientBuilder;

public class PatientNameComparatorTest {

    private final PatientNameComparator comparator = PatientNameComparator.PATIENT_NAME_ORDER;

    @Test
    public void compare_nullPatient_throwsNullPointerException() {
        Patient alice = new PatientBuilder().withId("S00000001A").withName("Alice Tan").build();

        assertThrows(NullPointerException.class, () -> comparator.compare(alice, null));
        assertThrows(NullPointerException.class, () -> comparator.compare(null, alice));
        assertThrows(NullPointerException.class, () -> comparator.compare(null, null));
    }

    @Test
    public void compare_differentNames_ordersByNameIgnoringCase() {
        Patient alice = new PatientBuilder().withId("S00000001A").withName("Alice Tan").build();
        Patient bob = new PatientBuilder().withId("S00000002A").withName("Bob Ong").build();
        Patient zac = new PatientBuilder().withId("S00000003A").withName("Zac Lim").build();

        assertTrue(comparator.compare(alice, bob) < 0);
        assertTrue(comparator.compare(bob, zac) < 0);
        assertTrue(comparator.compare(zac, alice) > 0);
    }

    @Test
    public void compare_sameNameIgnoringCase_breaksTieById() {
        Patient first = new PatientBuilder().withId("S00000001A").withName("Alice Tan").build();
        Patient second = new PatientBuilder().withId("S00000002A").withName("ALICE TAN").build();
        Patient twin = new PatientBuilder().withId("S00000001A").withName("Alice Tan").build();

        assertTrue(comparator.compare(first, second) < 0);
        assertTrue(comparator.compare(second, first) > 0);
        assertEquals(0, comparator.compare(first, twin));
    }
}
