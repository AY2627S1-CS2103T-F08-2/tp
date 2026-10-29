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
        assertEquals("", TextNormalizer.normalize("\u00A0")); // non-breaking space only
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

        // EP: horizontal spaces that \s does not match, pasted from formatted text
        assertEquals("TAN AH", TextNormalizer.normalize("tan\u00A0ah"));
        assertEquals("TAN AH", TextNormalizer.normalize("tan\u202Fah")); // narrow no-break space
        assertEquals("TAN AH", TextNormalizer.normalize("tan\u3000ah")); // ideographic space

        // EP: digits and symbols are unchanged
        assertEquals("BLK 12 #03-45, +65", TextNormalizer.normalize("blk 12 #03-45, +65"));

        // all of the above together
        assertEquals("TAN AH KOW", TextNormalizer.normalize("  tan  ah\tkow "));
    }

    @Test
    public void normalizeText_invisibleCharacters_removedButJoinersKept() {
        // EP: invisible characters only, which look blank (#218)
        assertEquals("", TextNormalizer.normalizeText("\u200B"));
        assertEquals("", TextNormalizer.normalizeText("\u200B\uFEFF\u2060 \u200B"));

        // EP: inside and around words, where they change nothing that is shown
        assertEquals("ALEXSMITH", TextNormalizer.normalizeText("Alex\u200BSmith"));
        assertEquals("TAN AH", TextNormalizer.normalizeText("\uFEFFtan \u2060ah\u200B"));

        // EP: the zero-width non-joiner, which Persian and Urdu words need, and the zero-width joiner, which
        // composite emoji such as the health worker need, are kept
        assertEquals("\u0645\u06CC\u200C\u062E\u0648\u0627\u0647\u0645",
                TextNormalizer.normalizeText("\u0645\u06CC\u200C\u062E\u0648\u0627\u0647\u0645"));
        assertEquals("NURSE \uD83D\uDC69\u200D\u2695\uFE0F",
                TextNormalizer.normalizeText("nurse \uD83D\uDC69\u200D\u2695\uFE0F"));

        // otherwise as normalize does
        assertEquals("TAN AH KOW", TextNormalizer.normalizeText("  tan\u00A0ah\tkow "));
        assertThrows(NullPointerException.class, () -> TextNormalizer.normalizeText(null));
    }

    @Test
    public void normalize_invisibleCharacters_kept() {
        // so that an ID, a phone number or a date with one fails its check (#206)
        assertEquals("S1234567A\u200B", TextNormalizer.normalize("s1234567a\u200B"));
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
