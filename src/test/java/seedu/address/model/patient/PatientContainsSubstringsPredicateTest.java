package seedu.address.model.patient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PatientBuilder;

public class PatientContainsSubstringsPredicateTest {

    private final Patient patient = new PatientBuilder().withId("T04123").withName("Mary Jane")
            .withPhone("+65 9876-5432").withAddress("123, Marina Bay, #01-02")
            .withMedicalHistory("Type 2 diabetes; allergy (penicillin)")
            .withNextAppointment("2026-10-15 09:30").build();

    @Test
    public void constructor_noCriteria_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> predicate(null, null, null, null, null, null));
    }

    @Test
    public void constructor_nullOptional_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new PatientContainsSubstringsPredicate(null,
                Optional.of("Mary"), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
    }

    @Test
    public void constructor_blankCriterion_throwsIllegalArgumentException() {
        for (String blank : List.of("", " \t\n ")) {
            assertThrows(IllegalArgumentException.class, () -> predicate(blank, null, null, null, null, null));
            assertThrows(IllegalArgumentException.class, () -> predicate(null, blank, null, null, null, null));
            assertThrows(IllegalArgumentException.class, () -> predicate(null, null, blank, null, null, null));
            assertThrows(IllegalArgumentException.class, () -> predicate(null, null, null, blank, null, null));
            assertThrows(IllegalArgumentException.class, () -> predicate(null, null, null, null, blank, null));
            assertThrows(IllegalArgumentException.class, () -> predicate(null, null, null, null, null, blank));
        }
    }

    @Test
    public void test_eachFieldContainsSubstring_returnsTrue() {
        List<PatientContainsSubstringsPredicate> predicates = List.of(
                predicate("041", null, null, null, null, null),
                predicate(null, "arY", null, null, null, null),
                predicate(null, null, "9876", null, null, null),
                predicate(null, null, null, "ina Bay", null, null),
                predicate(null, null, null, null, "diabet", null),
                predicate(null, null, null, null, null, "2026-10"));
        for (PatientContainsSubstringsPredicate predicate : predicates) {
            assertTrue(predicate.test(patient), predicate.toString());
        }
    }

    @Test
    public void test_onlyCorrespondingFieldIsChecked_returnsFalse() {
        assertFalse(predicate("Mary", null, null, null, null, null).test(patient));
        assertFalse(predicate(null, "Marina", null, null, null, null).test(patient));
        assertFalse(predicate(null, null, "2026", null, null, null).test(patient));
        assertFalse(predicate(null, null, null, "diabetes", null, null).test(patient));
        assertFalse(predicate(null, null, null, null, "09:30", null).test(patient));
        assertFalse(predicate(null, null, null, null, null, "9876").test(patient));
    }

    @Test
    public void test_caseAndWhitespaceDifferences_returnsTrue() {
        assertTrue(predicate(null, "  mArY\t  jAnE  ", null, null, null, null).test(patient));
        assertTrue(predicate(null, null, null, "  marina \n BAY ", null, null).test(patient));
    }

    @Test
    public void test_multiwordValueRequiresContiguousSubstring() {
        assertTrue(predicate(null, null, null, "Marina Bay", null, null).test(patient));
        assertFalse(predicate(null, null, null, "Bay Marina", null, null).test(patient));
        assertFalse(predicate(null, null, null, "Marina Street", null, null).test(patient));
    }

    @Test
    public void test_multipleCriteria_matchAnyField() {
        assertTrue(predicate(null, "Mary", null, "Jurong", null, null).test(patient));
        assertTrue(predicate(null, "John", null, "Marina", null, null).test(patient));
        assertFalse(predicate(null, "John", null, "Jurong", null, null).test(patient));
    }

    @Test
    public void test_missingOptionalFields_doNotMatch() {
        Patient withoutOptionalFields = new PatientBuilder().build();
        assertFalse(predicate(null, null, null, null, "diabetes", null).test(withoutOptionalFields));
        assertFalse(predicate(null, null, null, null, null, "2026").test(withoutOptionalFields));
        assertTrue(predicate(null, "Amy", null, null, "diabetes", "2026").test(withoutOptionalFields));
    }

    @Test
    public void test_phoneAndAppointmentMatchStoredText() {
        assertTrue(predicate(null, null, "9876-54", null, null, null).test(patient));
        assertFalse(predicate(null, null, "987654", null, null, null).test(patient));
        assertTrue(predicate(null, null, null, null, null, "09:30").test(patient));
        assertTrue(predicate(null, null, null, null, null, "15 09").test(patient));
        assertFalse(predicate(null, null, null, null, null, "2026-02-30").test(patient));

        Patient dateOnlyAppointment = new PatientBuilder().withNextAppointment("2026-10-15").build();
        assertTrue(predicate(null, null, null, null, null, "2026-10").test(dateOnlyAppointment));
        assertFalse(predicate(null, null, null, null, null, "09:30").test(dateOnlyAppointment));
    }

    @Test
    public void test_punctuationIsLiteral() {
        assertTrue(predicate(null, null, null, "#01-", null, null).test(patient));
        assertTrue(predicate(null, null, null, null, "(penicillin)", null).test(patient));
        assertFalse(predicate(null, null, null, null, ".*", null).test(patient));
        Patient literalPattern = new PatientBuilder().withMedicalHistory("Recorded .* literally").build();
        assertTrue(predicate(null, null, null, null, ".*", null).test(literalPattern));
    }

    @Test
    public void equals() {
        PatientContainsSubstringsPredicate firstPredicate = predicate("T04", "Mary Jane", "9876", "Marina Bay",
                "diabetes", "2026-10");
        PatientContainsSubstringsPredicate normalizedCopy = predicate("t04", " mary\t jane ", "9876", "marina bay",
                "DIABETES", "2026-10");
        assertTrue(firstPredicate.equals(firstPredicate));
        assertTrue(firstPredicate.equals(normalizedCopy));
        assertEquals(firstPredicate.hashCode(), normalizedCopy.hashCode());
        assertFalse(firstPredicate.equals(null));
        assertFalse(firstPredicate.equals(1));

        List<PatientContainsSubstringsPredicate> differentPredicates = List.of(
                predicate("S04", "Mary Jane", "9876", "Marina Bay", "diabetes", "2026-10"),
                predicate("T04", "John", "9876", "Marina Bay", "diabetes", "2026-10"),
                predicate("T04", "Mary Jane", "1234", "Marina Bay", "diabetes", "2026-10"),
                predicate("T04", "Mary Jane", "9876", "Jurong", "diabetes", "2026-10"),
                predicate("T04", "Mary Jane", "9876", "Marina Bay", "asthma", "2026-10"),
                predicate("T04", "Mary Jane", "9876", "Marina Bay", "diabetes", "2026-11"),
                predicate(null, "Mary Jane", "9876", "Marina Bay", "diabetes", "2026-10"));
        for (PatientContainsSubstringsPredicate differentPredicate : differentPredicates) {
            assertFalse(firstPredicate.equals(differentPredicate));
        }
    }

    @Test
    public void toStringMethod() {
        PatientContainsSubstringsPredicate predicate = predicate("123", "Mary", "9876", "Marina", "diabetes", "2026");
        String expected = PatientContainsSubstringsPredicate.class.getCanonicalName()
                + "{id=Optional[123], name=Optional[MARY], phone=Optional[9876], address=Optional[MARINA], "
                + "medicalHistory=Optional[DIABETES], nextAppointment=Optional[2026]}";
        assertEquals(expected, predicate.toString());
    }

    private PatientContainsSubstringsPredicate predicate(String id, String name, String phone, String address,
            String medicalHistory, String nextAppointment) {
        return new PatientContainsSubstringsPredicate(Optional.ofNullable(id), Optional.ofNullable(name),
                Optional.ofNullable(phone), Optional.ofNullable(address), Optional.ofNullable(medicalHistory),
                Optional.ofNullable(nextAppointment));
    }
}
