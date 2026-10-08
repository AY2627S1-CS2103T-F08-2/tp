package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

/**
 * Represents a Person's remark in the address book, as free-form notes.
 * An empty remark means the person has no remark.
 * Guarantees: immutable; is always valid
 */
public class Remark {

    public final String value;

    /**
     * Constructs a {@code Remark}.
     *
     * @param remark Any text; leading and trailing whitespace is removed.
     */
    public Remark(String remark) {
        requireNonNull(remark);
        value = remark.trim();
    }

    /**
     * Returns true if this remark has no text.
     */
    public boolean isEmpty() {
        return value.isEmpty();
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Remark otherRemark)) {
            return false;
        }

        return value.equals(otherRemark.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
