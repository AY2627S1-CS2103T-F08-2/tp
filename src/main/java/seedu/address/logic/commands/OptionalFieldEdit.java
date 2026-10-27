package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import seedu.address.commons.util.ToStringBuilder;

/**
 * A change to an optional text field, such as a patient's notes or an appointment's details: replace its value,
 * clear it, or add text to the end of it.
 *
 * @param <T> The type of the field, whose {@code toString()} returns its value.
 */
public final class OptionalFieldEdit<T> {

    /** Goes between the old value and the text added to its end. */
    public static final String APPEND_SEPARATOR = "; ";

    private enum Kind { REPLACE, CLEAR, APPEND }

    private final Kind kind;
    private final T value; // null when the edit clears the field

    private OptionalFieldEdit(Kind kind, T value) {
        this.kind = kind;
        this.value = value;
    }

    /**
     * Returns an edit that replaces the field's value with {@code value}.
     */
    public static <T> OptionalFieldEdit<T> replace(T value) {
        requireNonNull(value);
        return new OptionalFieldEdit<>(Kind.REPLACE, value);
    }

    /**
     * Returns an edit that clears the field.
     */
    public static <T> OptionalFieldEdit<T> clear() {
        return new OptionalFieldEdit<>(Kind.CLEAR, null);
    }

    /**
     * Returns an edit that adds {@code value} to the end of the field, after {@link #APPEND_SEPARATOR}, or sets the
     * field to {@code value} if it is empty.
     */
    public static <T> OptionalFieldEdit<T> append(T value) {
        requireNonNull(value);
        return new OptionalFieldEdit<>(Kind.APPEND, value);
    }

    /**
     * Returns the field after this edit, given its value {@code current} before it.
     *
     * @param create Creates a field from its value, to join the old value and the added text.
     */
    public Optional<T> applyTo(Optional<T> current, Function<String, T> create) {
        requireNonNull(current);
        requireNonNull(create);
        return switch (kind) {
            case REPLACE -> Optional.of(value);
            case CLEAR -> Optional.empty();
            case APPEND -> Optional.of(current.map(old -> create.apply(old + APPEND_SEPARATOR + value))
                    .orElse(value));
        };
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof OptionalFieldEdit<?> otherEdit)) {
            return false;
        }

        return kind == otherEdit.kind && Objects.equals(value, otherEdit.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kind, value);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("kind", kind)
                .add("value", value)
                .toString();
    }
}
