package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static seedu.address.testutil.TypicalPatients.getTypicalLaiNurse;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.GuiSettings;
import seedu.address.model.LaiNurse;
import seedu.address.model.ReadOnlyLaiNurse;
import seedu.address.model.UserPrefs;

public class StorageManagerTest {

    @TempDir
    public Path testFolder;

    private StorageManager storageManager;

    @BeforeEach
    public void setUp() {
        JsonLaiNurseStorage laiNurseStorage = new JsonLaiNurseStorage(getTempFilePath("lainurse"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(getTempFilePath("prefs"));
        storageManager = new StorageManager(laiNurseStorage, userPrefsStorage);
    }

    private Path getTempFilePath(String fileName) {
        return testFolder.resolve(fileName);
    }

    @Test
    public void prefsReadSave() throws Exception {
        /*
         * Note: This is an integration test that verifies the StorageManager is properly wired to the
         * {@link JsonUserPrefsStorage} class.
         * More extensive testing of UserPref saving/reading is done in {@link JsonUserPrefsStorageTest} class.
         */
        UserPrefs original = new UserPrefs();
        original.setGuiSettings(new GuiSettings(300, 600, 4, 6));
        storageManager.saveUserPrefs(original);
        UserPrefs retrieved = storageManager.readUserPrefs().get();
        assertEquals(original, retrieved);
    }

    @Test
    public void laiNurseReadSave() throws Exception {
        /*
         * Note: This is an integration test that verifies the StorageManager is properly wired to the
         * {@link JsonLaiNurseStorage} class.
         * More extensive testing of UserPref saving/reading is done in {@link JsonLaiNurseStorageTest} class.
         */
        LaiNurse original = getTypicalLaiNurse();
        storageManager.saveLaiNurse(original);
        ReadOnlyLaiNurse retrieved = storageManager.readLaiNurse().get();
        assertEquals(original, new LaiNurse(retrieved));
    }

    @Test
    public void getLaiNurseFilePath() {
        assertNotNull(storageManager.getLaiNurseFilePath());
    }

}
