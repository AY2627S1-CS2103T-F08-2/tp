package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PATIENTS;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPatients.ALICE;
import static seedu.address.testutil.TypicalPatients.BENSON;
import static seedu.address.testutil.TypicalPatients.DANIEL;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.NameContainsKeywordsPredicate;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientAppointment;
import seedu.address.testutil.LaiNurseBuilder;
import seedu.address.testutil.PatientBuilder;

public class ModelManagerTest {

    private static final Comparator<Patient> BY_NAME = Comparator.comparing(patient -> patient.getName().fullName);

    private static final String NAME_ALICE = "Alice Tan";
    private static final String NAME_BOB = "Bob Ong";
    private static final String NAME_ZAC = "Zac Lim";

    private static final Patient ALICE =
            new PatientBuilder().withId("S00000001A").withName(NAME_ALICE).build();
    private static final Patient BOB =
            new PatientBuilder().withId("S00000002A").withName(NAME_BOB).build();
    private static final Patient ZAC =
            new PatientBuilder().withId("S00000003A").withName(NAME_ZAC).build();

    /** Has the same name as {@link #ALICE}, so a sort by name alone cannot tell the two apart. */
    private static final Patient ALICE_LATER_ID =
            new PatientBuilder().withId("S00000002A").withName(NAME_ALICE).build();

    private ModelManager modelManager = new ModelManager();

    @Test
    public void constructor() {
        assertEquals(new UserPrefs(), modelManager.getUserPrefs());
        assertEquals(new GuiSettings(), modelManager.getGuiSettings());
        assertEquals(new LaiNurse(), new LaiNurse(modelManager.getLaiNurse()));
    }

    @Test
    public void constructor_validUserPrefs_copiesUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        modelManager = new ModelManager(new LaiNurse(), userPrefs);
        assertEquals(userPrefs, modelManager.getUserPrefs());

        // Modifying userPrefs should not modify modelManager's userPrefs
        UserPrefs oldUserPrefs = new UserPrefs(userPrefs);
        userPrefs.setGuiSettings(new GuiSettings(5, 6, 7, 8));
        assertEquals(oldUserPrefs, modelManager.getUserPrefs());
    }

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setGuiSettings(null));
    }

    @Test
    public void setGuiSettings_validGuiSettings_setsGuiSettings() {
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4);
        modelManager.setGuiSettings(guiSettings);
        assertEquals(guiSettings, modelManager.getGuiSettings());
    }

    @Test
    public void hasPatient_nullPatient_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasPatient(null));
    }

    @Test
    public void hasPatient_patientNotInLaiNurse_returnsFalse() {
        assertFalse(modelManager.hasPatient(ALICE));
    }

    @Test
    public void hasPatient_patientInLaiNurse_returnsTrue() {
        modelManager.addPatient(ALICE);
        assertTrue(modelManager.hasPatient(ALICE));
    }

    @Test
    public void findPatientById_nullId_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.findPatientById(null));
    }

    @Test
    public void findPatientById_idNotInLaiNurse_returnsEmpty() {
        assertEquals(Optional.empty(), modelManager.findPatientById(ALICE.getId()));
    }

    @Test
    public void findPatientById_patientFilteredOut_stillReturnsPatient() {
        modelManager.addPatient(ALICE);
        modelManager.addPatient(BENSON);
        // Show only BENSON, so ALICE is hidden from the filtered list
        modelManager.updateFilteredPatientList(patient -> patient.equals(BENSON));
        assertFalse(modelManager.getFilteredPatientList().contains(ALICE));

        assertEquals(Optional.of(ALICE), modelManager.findPatientById(ALICE.getId()));
    }

    @Test
    public void getFilteredPatientList_modifyList_throwsUnsupportedOperationException() {
        modelManager.addPatient(ALICE);
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredPatientList().remove(0));
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredPatientList().add(BENSON));
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredPatientList().set(0, BENSON));
    }

    @Test
    public void updateFilteredPatientList_comparator_filtersAndOrdersOnlyDisplayedPatients() {
        modelManager = new ModelManager(new LaiNurseBuilder().withPatient(DANIEL).withPatient(BENSON)
                .withPatient(ALICE).build(), new UserPrefs());
        ObservableList<Patient> displayedPatients = modelManager.getFilteredPatientList();
        List<PatientAppointment> appointments = List.copyOf(modelManager.getAppointmentList());

        modelManager.updateFilteredPatientList(patient -> !patient.equals(DANIEL), BY_NAME);

        assertSame(displayedPatients, modelManager.getFilteredPatientList());
        assertEquals(List.of(ALICE, BENSON), displayedPatients);
        assertEquals(List.of(DANIEL, BENSON, ALICE), modelManager.getLaiNurse().getPatientList());
        assertEquals(appointments, modelManager.getAppointmentList());
    }

    @Test
    public void getFilteredPatientList_patientChanges_updatesSameObservableView() {
        modelManager = new ModelManager(new LaiNurseBuilder().withPatient(BENSON).withPatient(ALICE)
                .withPatient(DANIEL).build(), new UserPrefs());
        ObservableList<Patient> displayedPatients = modelManager.getFilteredPatientList();
        List<List<Patient>> observedLists = new ArrayList<>();
        displayedPatients.addListener((ListChangeListener<Patient>) change ->
                observedLists.add(List.copyOf(displayedPatients)));
        modelManager.updateFilteredPatientList(PREDICATE_SHOW_ALL_PATIENTS, BY_NAME);
        assertEquals(List.of(ALICE, BENSON, DANIEL), displayedPatients);

        Patient renamedDaniel = new PatientBuilder(DANIEL).withName("Aaron Meier").build();
        modelManager.setPatient(DANIEL, renamedDaniel);
        assertEquals(List.of(renamedDaniel, ALICE, BENSON), displayedPatients);
        assertEquals(List.of(renamedDaniel, ALICE, BENSON), observedLists.getLast());

        modelManager.deletePatient(ALICE);
        assertSame(displayedPatients, modelManager.getFilteredPatientList());
        assertEquals(List.of(renamedDaniel, BENSON), displayedPatients);
        assertEquals(List.of(renamedDaniel, BENSON), observedLists.getLast());

        modelManager.setLaiNurse(new LaiNurseBuilder().withPatient(BENSON).withPatient(ALICE).build());
        assertEquals(List.of(ALICE, BENSON), displayedPatients);
        assertEquals(List.of(ALICE, BENSON), observedLists.getLast());
    }

    @Test
    public void updateFilteredPatientList_noComparator_restoresNameOrder() {
        modelManager = new ModelManager(new LaiNurseBuilder().withPatient(BENSON).withPatient(ALICE).build(),
                new UserPrefs());
        modelManager.updateFilteredPatientList(PREDICATE_SHOW_ALL_PATIENTS, BY_NAME.reversed());
        assertEquals(List.of(BENSON, ALICE), modelManager.getFilteredPatientList());

        modelManager.updateFilteredPatientList(PREDICATE_SHOW_ALL_PATIENTS);
        assertEquals(List.of(ALICE, BENSON), modelManager.getFilteredPatientList());

        modelManager.updateFilteredPatientList(PREDICATE_SHOW_ALL_PATIENTS, BY_NAME.reversed());
        modelManager.updateFilteredPatientList(PREDICATE_SHOW_ALL_PATIENTS, null);
        assertEquals(List.of(ALICE, BENSON), modelManager.getFilteredPatientList());
    }

    @Test
    public void updateFilteredPatientList_nullPredicate_throwsWithoutChangingResults() {
        modelManager = new ModelManager(new LaiNurseBuilder().withPatient(BENSON).withPatient(ALICE).build(),
                new UserPrefs());
        modelManager.updateFilteredPatientList(PREDICATE_SHOW_ALL_PATIENTS, BY_NAME);

        assertThrows(NullPointerException.class, () -> modelManager.updateFilteredPatientList(null));
        assertThrows(NullPointerException.class, () -> modelManager.updateFilteredPatientList(null, null));
        assertThrows(NullPointerException.class, () ->
                modelManager.updateFilteredPatientList(null, BY_NAME.reversed()));
        assertEquals(List.of(ALICE, BENSON), modelManager.getFilteredPatientList());
    }

    @Test
    public void equals_differentDisplayOrder_returnsFalse() {
        LaiNurse laiNurse = new LaiNurseBuilder().withPatient(BENSON).withPatient(ALICE).build();
        modelManager = new ModelManager(laiNurse, new UserPrefs());
        ModelManager otherModelManager = new ModelManager(laiNurse, new UserPrefs());
        modelManager.updateFilteredPatientList(PREDICATE_SHOW_ALL_PATIENTS, BY_NAME.reversed());
        assertFalse(modelManager.equals(otherModelManager));

        otherModelManager.updateFilteredPatientList(PREDICATE_SHOW_ALL_PATIENTS, BY_NAME.reversed());
        assertEquals(modelManager, otherModelManager);
    }

    // The tests below assert the sorted order of the patient view, which is by name, not the order of addition.
    // Names are stored upper case, so the order of two names never depends on how they were typed in.

    @Test
    public void constructor_patientsOutOfOrder_showsPatientsSortedByName() {
        ModelManager model = new ModelManager(new LaiNurseBuilder()
                .withPatient(ZAC).withPatient(ALICE).withPatient(BOB).build(), new UserPrefs());

        assertEquals(List.of(ALICE, BOB, ZAC), model.getFilteredPatientList());
    }

    @Test
    public void constructor_sameName_ordersTieById() {
        ModelManager model = new ModelManager(new LaiNurseBuilder()
                .withPatient(ALICE_LATER_ID).withPatient(ALICE).build(), new UserPrefs());

        assertEquals(List.of(ALICE, ALICE_LATER_ID), model.getFilteredPatientList());
    }

    @Test
    public void addPatient_keepsListSortedByName() {
        ModelManager model = new ModelManager(new LaiNurseBuilder().withPatient(ZAC).build(), new UserPrefs());

        model.addPatient(ALICE);

        assertEquals(List.of(ALICE, ZAC), model.getFilteredPatientList());
    }

    @Test
    public void updateFilteredPatientList_filteredSubset_staysSortedByName() {
        ModelManager model = new ModelManager(new LaiNurseBuilder()
                .withPatient(ZAC).withPatient(ALICE).withPatient(BOB).build(), new UserPrefs());

        model.updateFilteredPatientList(new NameContainsKeywordsPredicate(List.of("lim", "tan")));

        assertEquals(List.of(ALICE, ZAC), model.getFilteredPatientList());
    }

    @Test
    public void setLaiNurse_newData_showsPatientsSortedByName() {
        modelManager = new ModelManager(new LaiNurseBuilder().withPatient(ZAC).withPatient(ALICE).build(),
                new UserPrefs());

        // The new data is out of order, so a sorted view proves that the order follows the new data
        modelManager.setLaiNurse(new LaiNurseBuilder().withPatient(ZAC).withPatient(BOB).build());

        assertEquals(List.of(BOB, ZAC), modelManager.getFilteredPatientList());
    }

    @Test
    public void setPatient_renamedPatient_movesToItsNameOrder() {
        ModelManager model = new ModelManager(new LaiNurseBuilder()
                .withPatient(ZAC).withPatient(BOB).withPatient(ALICE).build(), new UserPrefs());
        Patient renamedBob = new PatientBuilder(BOB).withName(NAME_ALICE).build();

        model.setPatient(BOB, renamedBob);

        assertEquals(List.of(ALICE, renamedBob, ZAC), model.getFilteredPatientList());
    }

    @Test
    public void getAppointmentList_followsPatientsInTimeOrder() {
        // Daniel's appointment is the day after Benson's, so adding Daniel first still lists Benson first
        modelManager = new ModelManager(new LaiNurseBuilder().withPatient(DANIEL).withPatient(ALICE).build(),
                new UserPrefs());
        modelManager.addPatient(BENSON);
        assertEquals(List.of(entry(BENSON, "2026-10-05 09:30"), entry(DANIEL, "2026-10-06")),
                modelManager.getAppointmentList());

        // filtering the patient list does not hide appointments
        modelManager.updateFilteredPatientList(patient -> patient.equals(ALICE));
        assertEquals(2, modelManager.getAppointmentList().size());

        Patient movedDaniel = new PatientBuilder(DANIEL).withAppointments("2026-10-01", "2026-10-31 10:00").build();
        modelManager.setPatient(DANIEL, movedDaniel);
        assertEquals(List.of(entry(movedDaniel, "2026-10-01"), entry(BENSON, "2026-10-05 09:30"),
                entry(movedDaniel, "2026-10-31 10:00")), modelManager.getAppointmentList());

        modelManager.deletePatient(BENSON);
        modelManager.setLaiNurse(new LaiNurseBuilder().withPatient(movedDaniel).build());
        assertEquals(List.of(entry(movedDaniel, "2026-10-01"), entry(movedDaniel, "2026-10-31 10:00")),
                modelManager.getAppointmentList());
    }

    @Test
    public void getAppointmentList_modifyList_throwsUnsupportedOperationException() {
        modelManager.addPatient(BENSON);
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getAppointmentList().remove(0));
    }

    private static PatientAppointment entry(Patient patient, String appointment) {
        return new PatientAppointment(patient, patient.getSchedule()
                .findAtDateTime(new Appointment(appointment)).getFirst());
    }

    @Test
    public void patientListScope_hidesOrShowsArchivedPatientsLast() {
        Patient archivedAlice = ALICE.withArchived(true);
        modelManager = new ModelManager(new LaiNurseBuilder().withPatient(archivedAlice).withPatient(ZAC)
                .withPatient(BOB).build(), new UserPrefs());
        assertEquals(PatientScope.DEFAULT, modelManager.getPatientListScope().getValue());
        assertEquals(List.of(BOB, ZAC), modelManager.getFilteredPatientList());

        // archived patients come last, also in the order of a filter
        modelManager.setPatientListScope(PatientScope.ALL);
        assertEquals(List.of(BOB, ZAC, archivedAlice), modelManager.getFilteredPatientList());
        modelManager.updateFilteredPatientList(patient -> !patient.equals(BOB), BY_NAME);
        assertEquals(List.of(ZAC, archivedAlice), modelManager.getFilteredPatientList());
        assertTrue(modelManager.hasPatientFilter());

        // a new scope keeps the filter
        modelManager.setPatientListScope(PatientScope.ARCHIVED);
        assertEquals(List.of(archivedAlice), modelManager.getFilteredPatientList());
        modelManager.updateFilteredPatientList(PREDICATE_SHOW_ALL_PATIENTS);
        assertFalse(modelManager.hasPatientFilter());
        assertFalse(modelManager.isPatientListFiltered().getValue());
        // a filter that keeps every patient is still a filter
        modelManager.updateFilteredPatientList(patient -> true);
        assertTrue(modelManager.isPatientListFiltered().getValue());
        assertThrows(NullPointerException.class, () -> modelManager.setPatientListScope(null));
    }

    @Test
    public void showInPatientList_hiddenPatient_showsAllPatients() {
        Patient archivedAlice = ALICE.withArchived(true);
        modelManager = new ModelManager(new LaiNurseBuilder().withPatient(archivedAlice).withPatient(BOB).build(),
                new UserPrefs());
        modelManager.showInPatientList(BOB);
        assertEquals(PatientScope.DEFAULT, modelManager.getPatientListScope().getValue());
        modelManager.showInPatientList(archivedAlice);
        assertEquals(PatientScope.ALL, modelManager.getPatientListScope().getValue());

        modelManager.setPatientListScope(PatientScope.ARCHIVED);
        modelManager.showInPatientList(BOB);
        assertEquals(PatientScope.ALL, modelManager.getPatientListScope().getValue());
    }

    @Test
    public void equals_differentScope_returnsFalse() {
        ModelManager other = new ModelManager();
        other.setPatientListScope(PatientScope.ALL);
        assertFalse(modelManager.equals(other));
    }

    @Test
    public void equals() {
        LaiNurse laiNurse = new LaiNurseBuilder().withPatient(ALICE).withPatient(BENSON).build();
        LaiNurse differentLaiNurse = new LaiNurse();
        UserPrefs userPrefs = new UserPrefs();

        // same values -> returns true
        modelManager = new ModelManager(laiNurse, userPrefs);
        ModelManager modelManagerCopy = new ModelManager(laiNurse, userPrefs);
        assertTrue(modelManager.equals(modelManagerCopy));

        // same object -> returns true
        assertTrue(modelManager.equals(modelManager));

        // null -> returns false
        assertFalse(modelManager.equals(null));

        // different types -> returns false
        assertFalse(modelManager.equals(5));

        // different laiNurse -> returns false
        assertFalse(modelManager.equals(new ModelManager(differentLaiNurse, userPrefs)));

        // different filteredList -> returns false
        String[] keywords = ALICE.getName().fullName.split("\\s+");
        modelManager.updateFilteredPatientList(new NameContainsKeywordsPredicate(List.of(keywords)));
        assertFalse(modelManager.equals(new ModelManager(laiNurse, userPrefs)));

        // resets modelManager to initial state for upcoming tests
        modelManager.updateFilteredPatientList(PREDICATE_SHOW_ALL_PATIENTS);

        // different userPrefs -> returns false
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        assertFalse(modelManager.equals(new ModelManager(laiNurse, differentUserPrefs)));
    }
}
