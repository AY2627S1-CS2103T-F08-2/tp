package seedu.address.model.patient;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.LocalDate;
import java.util.Collection;

/**
 * The birthday bonus of the loyalty program: on a patient's birthday, every other patient with the same birthday
 * doubles the vouchers the patient holds that day, so a patient who shares a birthday with two others holds twice
 * twice as many on it. The bonus belongs to the day alone: the balance it multiplies keeps growing from its own
 * count, and no doubled amount is stored.
 */
public class BirthdayBonus {

    /**
     * Returns the factor by which the unused vouchers of {@code patient} multiply on {@code today}: one doubling
     * for each other patient in {@code allPatients} whose birthday is that day, archived patients included. A
     * patient without a date of birth has no birthday, and a day that is not their birthday multiplies by none.
     */
    public static double voucherMultiplier(Patient patient, Collection<Patient> allPatients, LocalDate today) {
        requireAllNonNull(patient, allPatients, today);
        if (patient.getDateOfBirth().filter(dateOfBirth -> dateOfBirth.isOn(today)).isEmpty()) {
            return 1;
        }
        long othersWithBirthday = allPatients.stream()
                .filter(other -> !other.getId().equals(patient.getId()))
                .filter(other -> other.getDateOfBirth().filter(dateOfBirth -> dateOfBirth.isOn(today)).isPresent())
                .count();
        // The largest finite count caps the factor, so that no number of birthday sharers can make it infinite
        return Math.min(Math.pow(2, othersWithBirthday), Double.MAX_VALUE);
    }

}
