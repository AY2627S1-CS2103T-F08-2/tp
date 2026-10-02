package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.Locale;

/**
 * Normalizes the text stored in {@code Person} fields, so that values differing only in case or spacing are equal.
 */
final class TextNormalizer {

    private TextNormalizer() {} // prevents instantiation

    /**
     * Returns {@code s} trimmed, with each run of whitespace collapsed into a single space,
     * and converted to upper case.
     *   <br>examples:<pre>
     *       normalize("  tan  ah\tkow ") == "TAN AH KOW"
     *       normalize("   ") == ""
     *       </pre>
     * @throws NullPointerException if {@code s} is null.
     */
    static String normalize(String s) {
        requireNonNull(s);
        return s.trim().replaceAll("\\s+", " ").toUpperCase(Locale.ROOT);
    }
}
