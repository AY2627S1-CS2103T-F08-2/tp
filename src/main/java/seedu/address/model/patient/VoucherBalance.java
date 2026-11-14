package seedu.address.model.patient;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * A patient's balance of unused vouchers. Every voucher has the same value, whatever that value is, and a voucher
 * count is a number of vouchers, not an amount of anything.
 * <p>
 * The balance earns a daily interest of 10%: on every day after the day of {@code interestDate}, the patient's
 * unused voucher count is multiplied by 1.1, so a count may be any number of vouchers, not a whole one. The balance
 * records the last date up to which its interest has been applied, so that the interest is applied lazily, when the
 * balance is read or changed, instead of by a task that runs at midnight.
 * <p>
 * Guarantees: the count is a finite number of at least 0, and {@code interestDate} is not null.
 */
public class VoucherBalance {

    /** The factor that an unused voucher count grows by on each day of interest, a daily interest of 10%. */
    public static final double DAILY_INTEREST_MULTIPLIER = 1.1;

    /** Message for a voucher count read from the data file that is not a finite number of at least 0. */
    public static final String MESSAGE_COUNT_CONSTRAINTS = "A patient's voucher count must be a number of at least 0.";

    /** Message for a voucher interest date read from the data file that is not a date. */
    public static final String MESSAGE_DATE_CONSTRAINTS =
            "A patient's voucher interest date must be a real date in the format yyyy-MM-dd.";

    /**
     * The balance of a patient who holds no vouchers. Its interest date never matters, because a count of 0 earns
     * no interest, so every balance of 0 vouchers behaves the same.
     */
    public static final VoucherBalance ZERO = new VoucherBalance(0, LocalDate.EPOCH);

    /**
     * How far a redeemed count may fall below the whole number that was redeemed, and still count as having
     * redeemed all of it. Multiplying by {@link #DAILY_INTEREST_MULTIPLIER} rounds the count, so a balance of
     * exactly 11 vouchers can be held as 11.000000000000002 or a hair below, and the hair below must not fail a
     * redemption of all 11, nor stay behind as a negative count.
     */
    private static final double REDEEM_TOLERANCE = 1e-9;

    /** The number of decimal places that a voucher count shows, as interest makes it a fraction. */
    private static final int DISPLAY_DECIMAL_PLACES = 2;

    private final double count;
    private final LocalDate interestDate;

    /**
     * Constructs a {@code VoucherBalance} of {@code count} vouchers, whose interest has been applied up to
     * {@code interestDate}, not including that day's own interest.
     *
     * @throws IllegalArgumentException if {@code count} is not a finite number of at least 0.
     */
    public VoucherBalance(double count, LocalDate interestDate) {
        requireNonNull(interestDate);
        checkArgument(isValidCount(count), MESSAGE_COUNT_CONSTRAINTS);
        this.count = count;
        this.interestDate = interestDate;
    }

    /**
     * Returns true if {@code count} can be a voucher count: a finite number of at least 0.
     */
    public static boolean isValidCount(double count) {
        return Double.isFinite(count) && count >= 0;
    }

    public double getCount() {
        return count;
    }

    public LocalDate getInterestDate() {
        return interestDate;
    }

    /**
     * Returns the balance as of {@code today}, with the interest of every day after {@code interestDate} applied.
     * A balance of no vouchers never grows, and a {@code today} that is not after the interest date, such as one
     * before it in a hand-edited data file, applies no interest either.
     */
    public VoucherBalance withInterestAppliedUntil(LocalDate today) {
        requireNonNull(today);
        if (count == 0 || !today.isAfter(interestDate)) {
            return this;
        }
        long daysElapsed = ChronoUnit.DAYS.between(interestDate, today);
        double grownCount = count * Math.pow(DAILY_INTEREST_MULTIPLIER, daysElapsed);
        // A count grown past every double, from an interest date far in the past, stays the largest finite count
        double clampedCount = Math.min(grownCount, Double.MAX_VALUE);
        return new VoucherBalance(clampedCount, today);
    }

    /**
     * Returns the balance as of {@code today} after the patient is given {@code number} more vouchers.
     *
     * @throws IllegalArgumentException if {@code number} is not a positive whole number.
     */
    public VoucherBalance added(long number, LocalDate today) {
        requireNonNull(today);
        checkArgument(number > 0, VoucherNumber.MESSAGE_CONSTRAINTS);
        VoucherBalance current = withInterestAppliedUntil(today);
        return new VoucherBalance(current.count + number, today);
    }

    /**
     * Returns true if this balance holds at least {@code number} vouchers to redeem, where a count a hair below
     * {@code number} through rounding, such as 11 vouchers held as 10.999999999999998, still counts as holding it.
     */
    public boolean canRedeem(long number) {
        return count + REDEEM_TOLERANCE >= number;
    }

    /**
     * Returns the balance as of {@code today} after {@code number} of the patient's vouchers are redeemed, so that
     * they leave the balance and the unused vouchers left keep earning interest. Redeeming every voucher the
     * patient holds gives {@link #ZERO}: the rounded count can leave a hair of a voucher, which is none.
     *
     * @throws IllegalArgumentException if {@code number} is not a positive whole number.
     */
    public VoucherBalance redeemed(long number, LocalDate today) {
        requireNonNull(today);
        checkArgument(number > 0, VoucherNumber.MESSAGE_CONSTRAINTS);
        VoucherBalance current = withInterestAppliedUntil(today);
        double remaining = current.count - number;
        if (Math.abs(remaining) < REDEEM_TOLERANCE) {
            return ZERO;
        }
        return new VoucherBalance(remaining, today);
    }

    /**
     * Returns {@code count} as users see it: rounded to {@value #DISPLAY_DECIMAL_PLACES} decimal places, with
     * trailing zeros and the decimal point left out, such as {@code 5}, {@code 11.2} or {@code 13.31}.
     */
    public static String formatCount(double count) {
        return BigDecimal.valueOf(count)
                .setScale(DISPLAY_DECIMAL_PLACES, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof VoucherBalance otherBalance)) {
            return false;
        }

        return count == otherBalance.count && interestDate.equals(otherBalance.interestDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(count, interestDate);
    }

    @Override
    public String toString() {
        if (count == 0) {
            return "0 vouchers";
        }
        return formatCount(count) + " vouchers as of " + interestDate;
    }

}
