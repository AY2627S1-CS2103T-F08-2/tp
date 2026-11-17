package seedu.address.model.patient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class VoucherNumberTest {

    @Test
    public void constructor_invalidNumber_throwsIllegalArgumentException() {
        // not a number
        for (String invalid : new String[] {"", " ", "two", "1.5", "-1", "+1", "1,000", "#5"}) {
            assertThrows(IllegalArgumentException.class, VoucherNumber.MESSAGE_CONSTRAINTS, () ->
                    new VoucherNumber(invalid));
        }
        // zero vouchers cannot be given or redeemed
        for (String zero : new String[] {"0", "00", "000"}) {
            assertThrows(IllegalArgumentException.class, VoucherNumber.MESSAGE_CONSTRAINTS, () ->
                    new VoucherNumber(zero));
        }
        // a number that does not fit a long is too large, which is its own message
        String tooLarge = "99999999999999999999";
        assertThrows(IllegalArgumentException.class,
                String.format(VoucherNumber.MESSAGE_TOO_LARGE, tooLarge), () ->
                        new VoucherNumber(tooLarge));
    }

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new VoucherNumber(null));
    }

    @Test
    public void isValidNumber() {
        assertTrue(VoucherNumber.isValidNumber("1"));
        assertTrue(VoucherNumber.isValidNumber("5"));
        // leading zeros keep the number valid
        assertTrue(VoucherNumber.isValidNumber("05"));
        assertTrue(VoucherNumber.isValidNumber("9223372036854775807"));

        assertFalse(VoucherNumber.isValidNumber(""));
        assertFalse(VoucherNumber.isValidNumber(" "));
        assertFalse(VoucherNumber.isValidNumber("0"));
        assertFalse(VoucherNumber.isValidNumber("00"));
        assertFalse(VoucherNumber.isValidNumber("1.5"));
        assertFalse(VoucherNumber.isValidNumber("-1"));
        assertFalse(VoucherNumber.isValidNumber("1 5"));
    }

    @Test
    public void constructor_validNumber_holdsValue() {
        assertEquals(5, new VoucherNumber("5").value);
        assertEquals(5, new VoucherNumber("05").value);
        assertEquals(9223372036854775807L, new VoucherNumber("9223372036854775807").value);
    }

    @Test
    public void equals() {
        VoucherNumber five = new VoucherNumber("5");

        // same values -> returns true
        assertEquals(five, new VoucherNumber("5"));

        // the same number with leading zeros -> returns true
        assertEquals(five, new VoucherNumber("05"));

        // same object -> returns true
        assertEquals(five, five);

        // null -> returns false
        assertNotEquals(five, null);

        // different type -> returns false
        assertNotEquals(five, 5);

        // different number -> returns false
        assertNotEquals(five, new VoucherNumber("6"));
    }

    @Test
    public void hashCode_equalNumbers_equalHashCodes() {
        assertEquals(new VoucherNumber("5").hashCode(), new VoucherNumber("05").hashCode());
    }

    @Test
    public void toStringMethod() {
        assertEquals("5", new VoucherNumber("5").toString());
        assertEquals("5", new VoucherNumber("05").toString());
    }

}
