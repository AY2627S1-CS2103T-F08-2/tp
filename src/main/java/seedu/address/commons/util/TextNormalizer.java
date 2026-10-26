package seedu.address.commons.util;

import static java.util.Objects.requireNonNull;

import java.util.Locale;

/**
 * Normalizes text so that values differing only in case or spacing are equal.
 */
public final class TextNormalizer {

    private TextNormalizer() {} // prevents instantiation

    /**
     * Returns {@code s} with every horizontal whitespace character replaced by a space, trimmed,
     * with each run of whitespace collapsed into a single space, and converted to upper case.
     *   <br>examples:<pre>
     *       normalize("  tan  ah\tkow ") == "TAN AH KOW"
     *       normalize("tan ah kow") == "TAN AH KOW"   (a non-breaking space between "tan" and "ah")
     *       normalize("   ") == ""
     *       </pre>
     * @throws NullPointerException if {@code s} is null.
     */
    public static String normalize(String s) {
        requireNonNull(s);
        return s.replaceAll("\\h", " ").trim().replaceAll("\\s+", " ").toUpperCase(Locale.ROOT);
    }
}
