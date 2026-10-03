package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Locale;

import org.junit.jupiter.api.Test;

public class TextNormalizerTest {

    @Test
    public void normalize_nullInput_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> TextNormalizer.normalize(null));
    }

    @Test
    public void normalize_blankInput_returnsEmptyString() {
        assertEquals("", TextNormalizer.normalize("")); // Boundary value
        assertEquals("", TextNormalizer.normalize(" "));
        assertEquals("", TextNormalizer.normalize(" \t\r\n "));
    }

    @Test
    public void normalize_validInput_returnsNormalizedText() {
        // EP: already normalized
        assertEquals("TAN AH KOW", TextNormalizer.normalize("TAN AH KOW"));

        // EP: leading and trailing whitespace
        assertEquals("TAN", TextNormalizer.normalize("  TAN \t"));

        // EP: runs of whitespace inside the text, including tabs and newlines
        assertEquals("TAN AH KOW", TextNormalizer.normalize("TAN  AH\t\nKOW"));

        // EP: lower and mixed case
        assertEquals("TAN AH KOW", TextNormalizer.normalize("tan Ah kOW"));

        // EP: digits and symbols are unchanged
        assertEquals("BLK 12 #03-45, +65", TextNormalizer.normalize("blk 12 #03-45, +65"));

        // all of the above together
        assertEquals("TAN AH KOW", TextNormalizer.normalize("  tan  ah\tkow "));
    }

    @Test
    public void normalize_turkishDefaultLocale_usesLocaleIndependentUpperCase() {
        // In the Turkish locale, "i".toUpperCase() gives a dotted capital I, not "I".
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr"));
            assertEquals("ID", TextNormalizer.normalize("id"));
        } finally {
            Locale.setDefault(original);
        }
    }
}
