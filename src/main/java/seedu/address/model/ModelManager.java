package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientAppointment;
import seedu.address.model.patient.PatientId;
import seedu.address.model.patient.PatientNameComparator;

/**
 * Represents the in-memory model of the LaiNurse data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final LaiNurse laiNurse;
    private final UserPrefs userPrefs;
    private final FilteredList<Patient> filteredPatients;
    private final ObservableList<PatientAppointment> appointments = FXCollections.observableArrayList();

    /**
     * Initializes a ModelManager with the given laiNurse and userPrefs.
     */
    public ModelManager(ReadOnlyLaiNurse laiNurse, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(laiNurse, userPrefs);

        logger.fine("Initializing with LaiNurse: " + laiNurse + " and user prefs " + userPrefs);

        this.laiNurse = new LaiNurse(laiNurse);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredPatients = new FilteredList<>(
                new SortedList<>(this.laiNurse.getPatientList(), PatientNameComparator.PATIENT_NAME_ORDER));
        this.laiNurse.getPatientList().addListener((ListChangeListener<Patient>) change -> refreshAppointments());
        refreshAppointments();
    }

    public ModelManager() {
        this(new LaiNurse(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    //=========== LaiNurse ================================================================================

    @Override
    public void setLaiNurse(ReadOnlyLaiNurse laiNurse) {
        this.laiNurse.resetData(laiNurse);
    }

    @Override
    public ReadOnlyLaiNurse getLaiNurse() {
        return laiNurse;
    }

    @Override
    public boolean hasPatient(Patient patient) {
        requireNonNull(patient);
        return laiNurse.hasPatient(patient);
    }

    @Override
    public Optional<Patient> findPatientById(PatientId id) {
        requireNonNull(id);
        return laiNurse.findPatientById(id);
    }

    @Override
    public void deletePatient(Patient target) {
        laiNurse.removePatient(target);
    }

    @Override
    public void addPatient(Patient patient) {
        laiNurse.addPatient(patient);
        updateFilteredPatientList(PREDICATE_SHOW_ALL_PATIENTS);
    }

    @Override
    public void setPatient(Patient target, Patient editedPatient) {
        requireAllNonNull(target, editedPatient);

        laiNurse.setPatient(target, editedPatient);
    }

    //=========== Filtered Patient List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Patient} backed by the internal list of
     * {@code laiNurse}
     */
    @Override
    public ObservableList<Patient> getFilteredPatientList() {
        return filteredPatients;
    }

    @Override
    public void updateFilteredPatientList(Predicate<Patient> predicate) {
        requireNonNull(predicate);
        filteredPatients.setPredicate(predicate);
    }

    //=========== Appointment List Accessors ==================================================================

    @Override
    public ObservableList<PatientAppointment> getAppointmentList() {
        return FXCollections.unmodifiableObservableList(appointments);
    }

    /**
     * Rebuilds the appointment list from the patients. A patient has few appointments, so rebuilding the whole
     * list on each change is fast enough, and simpler than following each change.
     */
    private void refreshAppointments() {
        appointments.setAll(laiNurse.getPatientList().stream()
                .flatMap(patient -> patient.getSchedule().asList().stream()
                        .map(appointment -> new PatientAppointment(patient, appointment)))
                .sorted()
                .toList());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager otherModelManager)) {
            return false;
        }

        return laiNurse.equals(otherModelManager.laiNurse)
                && userPrefs.equals(otherModelManager.userPrefs)
                && filteredPatients.equals(otherModelManager.filteredPatients);
    }

}
