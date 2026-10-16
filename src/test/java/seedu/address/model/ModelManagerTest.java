package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PATIENTS;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPatients.ALICE;
import static seedu.address.testutil.TypicalPatients.BENSON;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;
import seedu.address.model.patient.NameContainsKeywordsPredicate;
import seedu.address.testutil.LaiNurseBuilder;

public class ModelManagerTest {

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
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredPatientList().remove(0));
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
