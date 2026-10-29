package seedu.address.model;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;

import javafx.beans.value.ObservableValue;
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
     * Returns the settings that apply (#216), as a value that the UI can observe: the user's, or while the settings
     * are not released ({@code FeatureFlags#isSettingsEnabled()}), the default ones.
     */
    ObservableValue<Settings> getSettings();

    /**
     * Replaces the user's settings with {@code settings}, which are saved with the other user preferences.
     */
    void setSettings(Settings settings);

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
     * Returns an unmodifiable, observable view of the filtered patient list in its current display order: archived
     * patients last, and otherwise name order, as
     * {@link seedu.address.model.patient.PatientNameComparator#PATIENT_NAME_ORDER} gives, unless the last filter
     * update gave a comparator. The list shows only the patients in the scope, and of those, the ones that the filter
     * keeps.
     */
    ObservableList<Patient> getFilteredPatientList();

    /**
     * Updates the filter using {@code predicate} and restores name order. The scope stays.
     *
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredPatientList(Predicate<Patient> predicate);

    /**
     * Updates the filter using {@code predicate} and orders the results using {@code comparator}, with archived
     * patients last. A null comparator restores name order. The scope stays, and the stored patient order is
     * unchanged.
     *
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredPatientList(Predicate<Patient> predicate, Comparator<Patient> comparator);

    /**
     * Returns true if the patient list has a filter, such as one that {@code find} sets, that may hide some patients
     * in the scope.
     */
    boolean hasPatientFilter();

    /**
     * Returns whether the patient list has a filter, as {@link #hasPatientFilter()} does, as a value that the UI can
     * observe, so that its header says that the list is filtered even when the filter hides no one.
     */
    ObservableValue<Boolean> isPatientListFiltered();

    /**
     * Returns the scope of the patient list: whether it shows archived patients. {@code view} and {@code find} set it.
     * The appointment view has no scope: {@code viewappt} filters it by whether patients are archived, as it does by
     * date and patient.
     */
    ObservableValue<PatientScope> getPatientListScope();

    /**
     * Sets the scope of the patient list. The filter stays.
     *
     * @throws NullPointerException if {@code scope} is null.
     */
    void setPatientListScope(PatientScope scope);

    /**
     * Makes the scope of the patient list show {@code patient}: if it does not, the scope becomes
     * {@link PatientScope#ALL}, as when a command goes to an archived patient.
     */
    void showInPatientList(Patient patient);

    /**
     * Returns an unmodifiable list of every appointment of every patient, in time order. It follows the patients,
     * so it changes when a patient is added, edited or deleted, but not when the filtered patient list changes.
     */
    ObservableList<PatientAppointment> getAppointmentList();
}
