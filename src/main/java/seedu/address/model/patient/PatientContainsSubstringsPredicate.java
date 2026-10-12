package seedu.address.model.patient;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import seedu.address.commons.util.TextNormalizer;
import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests whether any specified patient field contains its corresponding substring.
 * Matching ignores case and repeated whitespace.
 */
public final class PatientContainsSubstringsPredicate implements Predicate<Patient> {
    private final Optional<String> id;
    private final Optional<String> name;
    private final Optional<String> phone;
    private final Optional<String> address;
    private final Optional<String> medicalHistory;
    private final Optional<String> nextAppointment;

    /**
     * Creates a predicate with at least one non-blank search criterion.
     *
     * @throws NullPointerException if any optional is null.
     * @throws IllegalArgumentException if no criterion is supplied or any supplied criterion is blank.
     */
    public PatientContainsSubstringsPredicate(Optional<String> id, Optional<String> name, Optional<String> phone,
            Optional<String> address, Optional<String> medicalHistory, Optional<String> nextAppointment) {
        requireAllNonNull(id, name, phone, address, medicalHistory, nextAppointment);
        checkArgument(Stream.of(id, name, phone, address, medicalHistory, nextAppointment)
                .anyMatch(Optional::isPresent), "At least one search criterion must be supplied.");
        this.id = normalizeCriterion(id);
        this.name = normalizeCriterion(name);
        this.phone = normalizeCriterion(phone);
        this.address = normalizeCriterion(address);
        this.medicalHistory = normalizeCriterion(medicalHistory);
        this.nextAppointment = normalizeCriterion(nextAppointment);
    }

    @Override
    public boolean test(Patient patient) {
        requireNonNull(patient);
        return matches(id, patient.getId().value)
                || matches(name, patient.getName().fullName)
                || matches(phone, patient.getPhone().value)
                || matches(address, patient.getAddress().value)
                || patient.getMedicalHistory().map(history -> matches(medicalHistory, history.value)).orElse(false)
                || patient.getNextAppointment().map(appointment -> matches(nextAppointment, appointment.value))
                        .orElse(false);
    }

    private static Optional<String> normalizeCriterion(Optional<String> criterion) {
        return criterion.map(value -> {
            String normalizedValue = TextNormalizer.normalize(value);
            checkArgument(!normalizedValue.isEmpty(), "Search values must not be blank.");
            return normalizedValue;
        });
    }

    private static boolean matches(Optional<String> criterion, String fieldValue) {
        return criterion.map(fieldValue::contains).orElse(false);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof PatientContainsSubstringsPredicate otherPredicate)) {
            return false;
        }

        return id.equals(otherPredicate.id)
                && name.equals(otherPredicate.name)
                && phone.equals(otherPredicate.phone)
                && address.equals(otherPredicate.address)
                && medicalHistory.equals(otherPredicate.medicalHistory)
                && nextAppointment.equals(otherPredicate.nextAppointment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, phone, address, medicalHistory, nextAppointment);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("id", id)
                .add("name", name)
                .add("phone", phone)
                .add("address", address)
                .add("medicalHistory", medicalHistory)
                .add("nextAppointment", nextAppointment)
                .toString();
    }
}
