package seedu.address.model.patient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class VoucherBalanceTest {

    private static final LocalDate INTEREST_DATE = LocalDate.parse("2026-10-05");
    private static final LocalDate NEXT_DAY = INTEREST_DATE.plusDays(1);
    private static final LocalDate SECOND_DAY = INTEREST_DATE.plusDays(2);

    @Test
    public void constructor_invalidCount_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new VoucherBalance(-1, INTEREST_DATE));
        assertThrows(IllegalArgumentException.class, () -> new VoucherBalance(Double.NaN, INTEREST_DATE));
        assertThrows(IllegalArgumentException.class, () -> new VoucherBalance(Double.POSITIVE_INFINITY,
                INTEREST_DATE));
    }

    @Test
    public void constructor_nullDate_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new VoucherBalance(5, null));
    }

    @Test
    public void constructor_boundaryCounts_areAccepted() {
        // the smallest count is 0, and the largest is the largest finite double
        assertEquals(0, new VoucherBalance(0, INTEREST_DATE).getCount(), 0);
        assertEquals(Double.MAX_VALUE, new VoucherBalance(Double.MAX_VALUE, INTEREST_DATE).getCount(), 0);
    }

    @Test
    public void isValidCount() {
        assertTrue(VoucherBalance.isValidCount(0));
        assertTrue(VoucherBalance.isValidCount(5));
        assertTrue(VoucherBalance.isValidCount(0.5));

        assertFalse(VoucherBalance.isValidCount(-0.1));
        assertFalse(VoucherBalance.isValidCount(Double.NaN));
        assertFalse(VoucherBalance.isValidCount(Double.NEGATIVE_INFINITY));
        assertFalse(VoucherBalance.isValidCount(Double.POSITIVE_INFINITY));
    }

    @Test
    public void withInterestAppliedUntil_zeroCount_neverGrows() {
        VoucherBalance zero = new VoucherBalance(0, LocalDate.EPOCH);
        assertEquals(zero, zero.withInterestAppliedUntil(INTEREST_DATE));
        assertEquals(zero, zero.withInterestAppliedUntil(INTEREST_DATE.plusYears(100)));
    }

    @Test
    public void withInterestAppliedUntil_noDaysElapsed_returnsSame() {
        VoucherBalance balance = new VoucherBalance(10, INTEREST_DATE);
        assertEquals(balance, balance.withInterestAppliedUntil(INTEREST_DATE));
    }

    @Test
    public void withInterestAppliedUntil_earlierDate_returnsSame() {
        // a date before the interest date, as a hand-edited data file may hold, applies no interest
        VoucherBalance balance = new VoucherBalance(10, INTEREST_DATE);
        assertEquals(balance, balance.withInterestAppliedUntil(INTEREST_DATE.minusDays(1)));
    }

    @Test
    public void withInterestAppliedUntil_growsByOneTenthEachDay() {
        VoucherBalance balance = new VoucherBalance(10, INTEREST_DATE);

        VoucherBalance afterOneDay = balance.withInterestAppliedUntil(NEXT_DAY);
        assertEquals(11, afterOneDay.getCount(), 1e-9);
        assertEquals(NEXT_DAY, afterOneDay.getInterestDate());

        // the daily multiplication compounds: two days grow 10 vouchers to 12.1
        VoucherBalance afterTwoDays = balance.withInterestAppliedUntil(SECOND_DAY);
        assertEquals(12.1, afterTwoDays.getCount(), 1e-9);
        assertEquals(SECOND_DAY, afterTwoDays.getInterestDate());
    }

    @Test
    public void withInterestAppliedUntil_compoundsEachDay_notAdds() {
        // two days of interest grow the count by 1.21 times, not by twice a tenth
        VoucherBalance balance = new VoucherBalance(100, INTEREST_DATE);
        assertEquals(121, balance.withInterestAppliedUntil(SECOND_DAY).getCount(), 1e-9);
    }

    @Test
    public void withInterestAppliedUntil_acrossAYearBoundary_countsCalendarDays() {
        // a day is a day, also over New Year
        VoucherBalance balance = new VoucherBalance(10, LocalDate.parse("2026-12-31"));
        VoucherBalance nextYear = balance.withInterestAppliedUntil(LocalDate.parse("2027-01-01"));
        assertEquals(11, nextYear.getCount(), 1e-9);
        assertEquals(LocalDate.parse("2027-01-01"), nextYear.getInterestDate());
    }

    @Test
    public void withInterestAppliedUntil_acrossLeapDay_countsCalendarDays() {
        // 2028 is a leap year: Feb 28 grows by two days of interest by Mar 1, and Feb 29 is itself a date
        // the balance may be dated on
        VoucherBalance balance = new VoucherBalance(10, LocalDate.parse("2028-02-28"));
        assertEquals(12.1, balance.withInterestAppliedUntil(LocalDate.parse("2028-03-01")).getCount(), 1e-9);

        VoucherBalance leapDay = new VoucherBalance(10, LocalDate.parse("2028-02-29"));
        VoucherBalance afterLeapDay = leapDay.withInterestAppliedUntil(LocalDate.parse("2028-03-01"));
        assertEquals(11, afterLeapDay.getCount(), 1e-9);
    }

    @Test
    public void withInterestAppliedUntil_overAYear_growsByTheCompoundFactor() {
        // a year from 2026-10-05 is 365 days, and the count grows by 1.1 to the power of the days
        double expected = 10 * Math.pow(VoucherBalance.DAILY_INTEREST_MULTIPLIER, 365);
        VoucherBalance balance = new VoucherBalance(10, INTEREST_DATE);
        VoucherBalance afterAYear = balance.withInterestAppliedUntil(INTEREST_DATE.plusYears(1));
        assertEquals(expected, afterAYear.getCount(), expected * 1e-12);
        assertTrue(Double.isFinite(afterAYear.getCount()));
    }

    @Test
    public void withInterestAppliedUntil_beyondEveryDouble_clampsToTheLargestCount() {
        // a balance that grows past every double stays at the largest count, and never becomes infinite
        VoucherBalance balance = new VoucherBalance(Double.MAX_VALUE, LocalDate.parse("2026-01-01"));
        VoucherBalance nextDay = balance.withInterestAppliedUntil(LocalDate.parse("2026-01-02"));
        assertEquals(Double.MAX_VALUE, nextDay.getCount(), 0);
        assertEquals(LocalDate.parse("2026-01-02"), nextDay.getInterestDate());
    }

    @Test
    public void interestLeavesTheBalanceAppliedTo_unchanged() {
        VoucherBalance balance = new VoucherBalance(10, INTEREST_DATE);
        balance.withInterestAppliedUntil(SECOND_DAY);
        balance.added(5, SECOND_DAY);
        balance.redeemed(4, SECOND_DAY);
        assertEquals(10, balance.getCount(), 0);
        assertEquals(INTEREST_DATE, balance.getInterestDate());

        // the shared balance of no vouchers is never changed by an operation on it
        VoucherBalance.ZERO.added(5, NEXT_DAY);
        assertEquals(0, VoucherBalance.ZERO.getCount(), 0);
    }

    @Test
    public void withInterestAppliedUntil_interestDateFarPast_staysFinite() {
        VoucherBalance balance = new VoucherBalance(10, LocalDate.of(1, 1, 1));
        assertTrue(Double.isFinite(balance.withInterestAppliedUntil(LocalDate.now()).getCount()));
    }

    @Test
    public void added_appliesInterestFirst() {
        // 10 vouchers grow to 12.1 over two days, and the 5 new ones add to that, not to the original 10
        VoucherBalance more = new VoucherBalance(10, INTEREST_DATE).added(5, SECOND_DAY);
        assertEquals(17.1, more.getCount(), 1e-9);
        assertEquals(SECOND_DAY, more.getInterestDate());

        // a patient with no vouchers, or one whose day has not changed, simply gains the vouchers
        assertEquals(new VoucherBalance(5, NEXT_DAY), VoucherBalance.ZERO.added(5, NEXT_DAY));
        assertEquals(new VoucherBalance(15, INTEREST_DATE), new VoucherBalance(10, INTEREST_DATE).added(5,
                INTEREST_DATE));
    }

    @Test
    public void redeemed_appliesInterestFirst() {
        // 10 vouchers grow to 12.1 over two days, and 4 of those are redeemed
        VoucherBalance fewer = new VoucherBalance(10, INTEREST_DATE).redeemed(4, SECOND_DAY);
        assertEquals(8.1, fewer.getCount(), 1e-9);
        assertEquals(SECOND_DAY, fewer.getInterestDate());
    }

    @Test
    public void redeemed_allVouchers_givesZero() {
        VoucherBalance balance = new VoucherBalance(10, INTEREST_DATE);
        assertEquals(VoucherBalance.ZERO, balance.redeemed(10, INTEREST_DATE));
        assertEquals(VoucherBalance.ZERO, balance.redeemed(11, NEXT_DAY));
    }

    @Test
    public void redeemed_roundingResidue_givesZero() {
        // a count of exactly 11 vouchers can be held a hair above or below 11, by the rounding of the
        // daily multiplication; redeeming all 11 leaves no residue either way
        VoucherBalance balance = new VoucherBalance(10, INTEREST_DATE).withInterestAppliedUntil(NEXT_DAY);
        assertEquals(VoucherBalance.ZERO, balance.redeemed(11, NEXT_DAY));
    }

    @Test
    public void redeemed_leavesARemainderAboveAResidue() {
        // redeeming 10 of 11 vouchers held a hair above 11 leaves that hair, which is a real fraction
        VoucherBalance balance = new VoucherBalance(10, INTEREST_DATE).withInterestAppliedUntil(NEXT_DAY);
        VoucherBalance remainder = balance.redeemed(10, NEXT_DAY);
        assertNotEquals(VoucherBalance.ZERO, remainder);
        assertEquals(1, remainder.getCount(), 1e-9);
        assertEquals(NEXT_DAY, remainder.getInterestDate());
    }

    @Test
    public void added_nonPositiveNumber_throws() {
        for (long nonPositive : new long[] {0, -1, -5}) {
            assertThrows(IllegalArgumentException.class, VoucherNumber.MESSAGE_CONSTRAINTS, () ->
                    new VoucherBalance(10, INTEREST_DATE).added(nonPositive, INTEREST_DATE));
        }
    }

    @Test
    public void redeemed_nonPositiveNumber_throws() {
        // a negative number would otherwise grow the balance, by redeeming a negative amount of it
        for (long nonPositive : new long[] {0, -1, -5}) {
            assertThrows(IllegalArgumentException.class, VoucherNumber.MESSAGE_CONSTRAINTS, () ->
                    new VoucherBalance(10, INTEREST_DATE).redeemed(nonPositive, INTEREST_DATE));
        }
    }

    @Test
    public void nullDates_throwNullPointerException() {
        VoucherBalance balance = new VoucherBalance(10, INTEREST_DATE);
        assertThrows(NullPointerException.class, () -> balance.withInterestAppliedUntil(null));
        assertThrows(NullPointerException.class, () -> balance.added(5, null));
        assertThrows(NullPointerException.class, () -> balance.redeemed(5, null));
    }

    @Test
    public void added_largestNumberOfVouchers_staysFinite() {
        VoucherBalance balance = new VoucherBalance(1, INTEREST_DATE).added(Long.MAX_VALUE, INTEREST_DATE);
        assertTrue(Double.isFinite(balance.getCount()));
        assertEquals(INTEREST_DATE, balance.getInterestDate());
    }

    @Test
    public void redeemed_vouchersLeft_keepEarningInterest() {
        // the redeemed vouchers leave the balance, and the unused ones left grow by another tenth the next day
        VoucherBalance balance = new VoucherBalance(10, INTEREST_DATE).redeemed(4, NEXT_DAY);
        assertEquals(7, balance.getCount(), 1e-9);
        VoucherBalance nextDay = balance.withInterestAppliedUntil(SECOND_DAY);
        assertEquals(7.7, nextDay.getCount(), 1e-9);
    }

    @Test
    public void canRedeem() {
        VoucherBalance balance = new VoucherBalance(10, INTEREST_DATE);
        assertTrue(balance.canRedeem(1));
        assertTrue(balance.canRedeem(10));
        assertFalse(balance.canRedeem(11));

        // rounding may hold a count a hair below its whole number, which can still be redeemed whole
        double heldBelow = Math.nextDown(11.0);
        assertTrue(new VoucherBalance(heldBelow, INTEREST_DATE).canRedeem(11));
        assertFalse(new VoucherBalance(heldBelow, INTEREST_DATE).canRedeem(12));
    }

    @Test
    public void formatCount() {
        assertEquals("0", VoucherBalance.formatCount(0));
        assertEquals("5", VoucherBalance.formatCount(5));
        assertEquals("5.5", VoucherBalance.formatCount(5.5));
        assertEquals("11.2", VoucherBalance.formatCount(11.2));
        // a count grown to a hair above 13.31 by the daily multiplication still shows as 13.31
        assertEquals("13.31", VoucherBalance.formatCount(13.310000000000002));

        // the second decimal rounds half-up: 0.005 counts as 0.01, and 0.004 as none
        assertEquals("0.01", VoucherBalance.formatCount(0.005));
        assertEquals("0", VoucherBalance.formatCount(0.004));
        assertEquals("1.01", VoucherBalance.formatCount(1.005));
        // a large count is written out in full, with no exponent
        assertEquals("1000000000000000", VoucherBalance.formatCount(1e15));
    }

    @Test
    public void equals() {
        VoucherBalance balance = new VoucherBalance(10, INTEREST_DATE);

        // same values -> returns true
        assertEquals(balance, new VoucherBalance(10, INTEREST_DATE));

        // same object -> returns true
        assertEquals(balance, balance);

        // null -> returns false
        assertNotEquals(balance, null);

        // different type -> returns false
        assertNotEquals(balance, 5);

        // different count -> returns false
        assertNotEquals(balance, new VoucherBalance(10.1, INTEREST_DATE));

        // different interest date -> returns false
        assertNotEquals(balance, new VoucherBalance(10, NEXT_DAY));
    }

    @Test
    public void hashCode_equalBalances_equalHashCodes() {
        assertEquals(new VoucherBalance(10, INTEREST_DATE).hashCode(),
                new VoucherBalance(10, INTEREST_DATE).hashCode());
    }

    @Test
    public void toStringMethod() {
        assertEquals("0 vouchers", VoucherBalance.ZERO.toString());
        assertEquals("10 vouchers as of " + INTEREST_DATE, new VoucherBalance(10, INTEREST_DATE).toString());
        assertEquals("12.1 vouchers as of " + SECOND_DAY, new VoucherBalance(12.1, SECOND_DAY).toString());
    }

}
