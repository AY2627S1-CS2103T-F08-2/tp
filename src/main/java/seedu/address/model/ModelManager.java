package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
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
    /** Archived patients come last, whatever else orders the list. */
    private static final Comparator<Patient> ARCHIVED_LAST = Comparator.comparing(Patient::isArchived);

    private final LaiNurse laiNurse;
    private final UserPrefs userPrefs;
    private final FilteredList<Patient> filteredPatients;
    private final SortedList<Patient> sortedPatients;
    private final ObservableList<PatientAppointment> appointments = FXCollections.observableArrayList();
    private final ObjectProperty<PatientScope> scope = new SimpleObjectProperty<>(PatientScope.DEFAULT);
    /** The filter of the patient list, which chooses among the patients in the scope. */
    private Predicate<Patient> filter = PREDICATE_SHOW_ALL_PATIENTS;

    /**
     * Initializes a ModelManager with the given laiNurse and userPrefs.
     */
    public ModelManager(ReadOnlyLaiNurse laiNurse, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(laiNurse, userPrefs);

        logger.fine("Initializing with LaiNurse: " + laiNurse + " and user prefs " + userPrefs);

        this.laiNurse = new LaiNurse(laiNurse);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredPatients = new FilteredList<>(new SortedList<>(this.laiNurse.getPatientList(),
                ARCHIVED_LAST.thenComparing(PatientNameComparator.PATIENT_NAME_ORDER)));
        sortedPatients = new SortedList<>(filteredPatients);
        applyFilter();
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
     * Returns an unmodifiable view of the filtered and ordered patients, backed by {@code laiNurse}.
     */
    @Override
    public ObservableList<Patient> getFilteredPatientList() {
        return sortedPatients;
    }

    @Override
    public void updateFilteredPatientList(Predicate<Patient> predicate) {
        updateFilteredPatientList(predicate, null);
    }

    @Override
    public void updateFilteredPatientList(Predicate<Patient> predicate, Comparator<Patient> comparator) {
        requireNonNull(predicate);
        filter = predicate;
        applyFilter();
        sortedPatients.setComparator(comparator == null ? null : ARCHIVED_LAST.thenComparing(comparator));
        logger.fine(() -> "Updated patient filter: " + sortedPatients.size() + " patients, "
                + (comparator == null ? "name order" : "custom order"));
    }

    @Override
    public boolean hasPatientFilter() {
        return filter != PREDICATE_SHOW_ALL_PATIENTS;
    }

    @Override
    public ObservableValue<PatientScope> getPatientListScope() {
        return scope;
    }

    @Override
    public void setPatientListScope(PatientScope scope) {
        requireNonNull(scope);
        this.scope.set(scope);
        applyFilter();
        logger.fine(() -> "Updated patient scope: " + scope);
    }

    @Override
    public void showInPatientList(Patient patient) {
        requireNonNull(patient);
        if (!scope.get().includes(patient)) {
            setPatientListScope(PatientScope.ALL);
        }
    }

    /**
     * Shows the patients in the scope that the filter keeps.
     */
    private void applyFilter() {
        PatientScope currentScope = scope.get();
        Predicate<Patient> currentFilter = filter;
        filteredPatients.setPredicate(patient -> currentScope.includes(patient) && currentFilter.test(patient));
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
                && sortedPatients.equals(otherModelManager.sortedPatients)
                && scope.get() == otherModelManager.scope.get();
    }

}
