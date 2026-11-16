package seedu.address.model.patient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PatientBuilder;

public class BirthdayBonusTest {

    private static final LocalDate BIRTHDAY = LocalDate.parse("2026-11-16");
    private static final String SHARED_DOB = "1990-11-16";
    private static final String OTHER_DOB = "1990-11-17";

    /** Returns a patient with {@code id} whose date of birth is 16 Nov 1990, the birthday of these tests. */
    private static Patient bornOnTheBirthday(String id) {
        return new PatientBuilder().withId(id).withDateOfBirth(SHARED_DOB).build();
    }

    @Test
    public void voucherMultiplier_nullArguments_throwsNullPointerException() {
        Patient patient = bornOnTheBirthday("S1234567A");
        assertThrows(NullPointerException.class, () -> BirthdayBonus.voucherMultiplier(null, List.of(), BIRTHDAY));
        assertThrows(NullPointerException.class, () -> BirthdayBonus.voucherMultiplier(patient, null, BIRTHDAY));
        assertThrows(NullPointerException.class, () -> BirthdayBonus.voucherMultiplier(patient, List.of(), null));
    }

    @Test
    public void voucherMultiplier_withoutDateOfBirth_isOne() {
        Patient patient = new PatientBuilder().withId("S1234567A").build();
        Patient other = bornOnTheBirthday("S2345678B");
        assertEquals(1, BirthdayBonus.voucherMultiplier(patient, List.of(patient, other), BIRTHDAY));
    }

    @Test
    public void voucherMultiplier_onAnotherDay_isOne() {
        Patient patient = bornOnTheBirthday("S1234567A");
        Patient other = bornOnTheBirthday("S2345678B");
        assertEquals(1, BirthdayBonus.voucherMultiplier(patient, List.of(patient, other), BIRTHDAY.plusDays(1)));
        assertEquals(1, BirthdayBonus.voucherMultiplier(patient, List.of(patient, other), BIRTHDAY.minusDays(1)));
    }

    @Test
    public void voucherMultiplier_aloneOnTheirBirthday_isOne() {
        Patient patient = bornOnTheBirthday("S1234567A");
        assertEquals(1, BirthdayBonus.voucherMultiplier(patient, List.of(patient), BIRTHDAY));
    }

    @Test
    public void voucherMultiplier_oneOtherBirthday_doubles() {
        Patient patient = bornOnTheBirthday("S1234567A");
        Patient other = bornOnTheBirthday("S2345678B");
        assertEquals(2, BirthdayBonus.voucherMultiplier(patient, List.of(patient, other), BIRTHDAY));
        // the bonus belongs to each sharer: the other patient doubles too
        assertEquals(2, BirthdayBonus.voucherMultiplier(other, List.of(patient, other), BIRTHDAY));
    }

    @Test
    public void voucherMultiplier_eachOtherBirthday_doublesOnceMore() {
        Patient patient = bornOnTheBirthday("S1234567A");
        Patient other = bornOnTheBirthday("S2345678B");
        Patient third = bornOnTheBirthday("T0123456C");
        assertEquals(4, BirthdayBonus.voucherMultiplier(patient, List.of(patient, other, third), BIRTHDAY));
        assertEquals(4, BirthdayBonus.voucherMultiplier(third, List.of(patient, other, third), BIRTHDAY));
    }

    @Test
    public void voucherMultiplier_ignoresPatientsWithoutTheBirthday() {
        Patient patient = bornOnTheBirthday("S1234567A");
        Patient other = bornOnTheBirthday("S2345678B");
        // a different day, a different month, and a patient with no date of birth, are not birthday sharers
        Patient otherDay = new PatientBuilder().withId("T0123456C").withDateOfBirth(OTHER_DOB).build();
        Patient otherMonth = new PatientBuilder().withId("T0123456D").withDateOfBirth("1990-12-16").build();
        Patient noDob = new PatientBuilder().withId("T0123456E").build();
        assertEquals(2, BirthdayBonus.voucherMultiplier(patient,
                List.of(patient, other, otherDay, otherMonth, noDob), BIRTHDAY));
    }

    @Test
    public void voucherMultiplier_ignoresTheBirthYear() {
        Patient patient = bornOnTheBirthday("S1234567A");
        Patient older = new PatientBuilder().withId("S2345678B").withDateOfBirth("1930-11-16").build();
        assertEquals(2, BirthdayBonus.voucherMultiplier(patient, List.of(patient, older), BIRTHDAY));
    }

    @Test
    public void voucherMultiplier_archivedSharers_count() {
        Patient patient = bornOnTheBirthday("S1234567A");
        Patient archived = new PatientBuilder().withId("S2345678B").withDateOfBirth(SHARED_DOB)
                .withArchived(true).build();
        assertEquals(2, BirthdayBonus.voucherMultiplier(patient, List.of(patient, archived), BIRTHDAY));
    }

    @Test
    public void voucherMultiplier_feb29Birthday_isOnA29FebOnly() {
        Patient patient = new PatientBuilder().withId("S1234567A").withDateOfBirth("2000-02-29").build();
        Patient other = new PatientBuilder().withId("S2345678B").withDateOfBirth("2000-02-29").build();
        assertEquals(2, BirthdayBonus.voucherMultiplier(patient, List.of(patient, other),
                LocalDate.parse("2028-02-29")));
        assertEquals(1, BirthdayBonus.voucherMultiplier(patient, List.of(patient, other),
                LocalDate.parse("2027-02-28")));
    }

    @Test
    public void voucherMultiplier_moreBirthdaySharersThanADoubleCanHold_staysFinite() {
        // a thousand sharers hold a factor of 2 to the power of 1000, which is finite, and past 1024 of them,
        // where the power would overflow, the largest finite count caps it
        Patient[] thousand = new Patient[1001];
        for (int i = 0; i < thousand.length; i++) {
            thousand[i] = bornOnTheBirthday("S" + i);
        }
        double factorOfThousand = BirthdayBonus.voucherMultiplier(thousand[0], List.of(thousand), BIRTHDAY);
        assertEquals(Math.pow(2, 1000), factorOfThousand, factorOfThousand * 1e-12);

        Patient[] aThousandAndMore = new Patient[1100];
        for (int i = 0; i < aThousandAndMore.length; i++) {
            aThousandAndMore[i] = bornOnTheBirthday("T" + i);
        }
        double cappedFactor = BirthdayBonus.voucherMultiplier(aThousandAndMore[0], List.of(aThousandAndMore),
                BIRTHDAY);
        assertEquals(Double.MAX_VALUE, cappedFactor, 0);
    }

}
