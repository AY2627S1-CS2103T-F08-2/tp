package seedu.address.model;

import java.util.Optional;
import java.util.function.Predicate;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientAppointment;
import seedu.address.model.patient.PatientId;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluates to true */
    Predicate<Patient> PREDICATE_SHOW_ALL_PATIENTS = unused -> true;

    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Replaces all the data with the data in {@code laiNurse}.
     */
    void setLaiNurse(ReadOnlyLaiNurse laiNurse);

    /**
     * Returns the LaiNurse
     */
    ReadOnlyLaiNurse getLaiNurse();

    /**
     * Returns true if a patient with the same identity as {@code patient} exists in LaiNurse.
     */
    boolean hasPatient(Patient patient);

    /**
     * Returns the patient with the given {@code id}, or {@code Optional#empty()} if there is none.
     * Searches all patients in LaiNurse, not just those in the filtered patient list.
     */
    Optional<Patient> findPatientById(PatientId id);

    /**
     * Deletes the given patient.
     * The patient must exist in LaiNurse.
     */
    void deletePatient(Patient target);

    /**
     * Adds the given patient.
     * {@code patient} must not already exist in LaiNurse.
     */
    void addPatient(Patient patient);

    /**
     * Replaces the given patient {@code target} with {@code editedPatient}.
     * {@code target} must exist in LaiNurse.
     * The patient identity of {@code editedPatient} must not be the same as another existing patient
     * in LaiNurse.
     */
    void setPatient(Patient target, Patient editedPatient);

    /**
     * Returns an unmodifiable view of the filtered patient list, in name order, as
     * {@link seedu.address.model.patient.PatientNameComparator#PATIENT_NAME_ORDER} gives.
     */
    ObservableList<Patient> getFilteredPatientList();

    /**
     * Updates the filter of the filtered patient list to filter by the given {@code predicate}.
     *
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredPatientList(Predicate<Patient> predicate);

    /**
     * Returns an unmodifiable list of every appointment of every patient, in time order. It follows the patients,
     * so it changes when a patient is added, edited or deleted, but not when the filtered patient list changes.
     */
    ObservableList<PatientAppointment> getAppointmentList();
}
