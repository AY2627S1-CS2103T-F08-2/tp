package seedu.address.commons.util;

import static java.util.Objects.requireNonNull;

import java.util.Locale;

/**
 * Normalizes text so that values differing only in case or spacing are equal.
 */
public final class TextNormalizer {

    /**
     * Invisible characters that text pasted from a website can carry, and that change nothing in what is shown: the
     * zero-width space, the byte order mark (zero-width no-break space) and the word joiner. The zero-width
     * non-joiner (U+200C) and joiner (U+200D) are not among them, as Arabic, Persian and Urdu names and composite
     * emoji, such as a health worker, need them to show correctly.
     */
    private static final String INVISIBLE_CHARACTERS = "[\\u200B\\uFEFF\\u2060]";

    private TextNormalizer() {} // prevents instantiation

    /**
     * Returns {@code s} with every horizontal whitespace character replaced by a space, trimmed,
     * with each run of whitespace collapsed into a single space, and converted to upper case.
     * Invisible characters such as the zero-width space are kept, so that an ID, a phone number or a date that
     * carries one fails its check instead of silently matching another; free text uses {@link #normalizeText}.
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

    /**
     * Returns free text {@code s}, such as a name or an address, as {@link #normalize} does, but first without
     * invisible characters such as the zero-width space. So the value is what the user sees: text of invisible
     * characters only is blank, and "alex", a zero-width space and "smith" give "ALEXSMITH", which a search for
     * "alexsmith" finds.
     *
     * @throws NullPointerException if {@code s} is null.
     */
    public static String normalizeText(String s) {
        requireNonNull(s);
        return normalize(s.replaceAll(INVISIBLE_CHARACTERS, ""));
    }
}
