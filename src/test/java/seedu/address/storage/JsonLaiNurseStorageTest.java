package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPatients.ALICE;
import static seedu.address.testutil.TypicalPatients.HOON;
import static seedu.address.testutil.TypicalPatients.IDA;
import static seedu.address.testutil.TypicalPatients.getTypicalLaiNurse;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.LaiNurse;
import seedu.address.model.ReadOnlyLaiNurse;

public class JsonLaiNurseStorageTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonLaiNurseStorageTest");

    @TempDir
    public Path testFolder;

    @Test
    public void readLaiNurse_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> readLaiNurse(null));
    }

    private java.util.Optional<ReadOnlyLaiNurse> readLaiNurse(String filePath) throws Exception {
        return new JsonLaiNurseStorage(Paths.get(filePath)).readLaiNurse(addToTestDataPathIfNotNull(filePath));
    }

    private Path addToTestDataPathIfNotNull(String prefsFileInTestDataFolder) {
        return prefsFileInTestDataFolder != null
                ? TEST_DATA_FOLDER.resolve(prefsFileInTestDataFolder)
                : null;
    }

    @Test
    public void read_missingFile_emptyResult() throws Exception {
        assertFalse(readLaiNurse("NonExistentFile.json").isPresent());
    }

    @Test
    public void read_notJsonFormat_exceptionThrown() {
        assertThrows(DataLoadingException.class, () -> readLaiNurse("notJsonFormatLaiNurse.json"));
    }

    @Test
    public void readLaiNurse_invalidPatientLaiNurse_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readLaiNurse("invalidPatientLaiNurse.json"));
    }

    @Test
    public void readLaiNurse_invalidAndValidPatientLaiNurse_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readLaiNurse("invalidAndValidPatientLaiNurse.json"));
    }

    @Test
    public void readAndSaveLaiNurse_allInOrder_success() throws Exception {
        Path filePath = testFolder.resolve("TempLaiNurse.json");
        LaiNurse original = getTypicalLaiNurse();
        JsonLaiNurseStorage jsonLaiNurseStorage = new JsonLaiNurseStorage(filePath);

        // Save in new file and read back
        jsonLaiNurseStorage.saveLaiNurse(original, filePath);
        ReadOnlyLaiNurse readBack = jsonLaiNurseStorage.readLaiNurse(filePath).get();
        assertEquals(original, new LaiNurse(readBack));

        // Modify data, overwrite existing file, and read back
        original.addPatient(HOON);
        original.removePatient(ALICE);
        jsonLaiNurseStorage.saveLaiNurse(original, filePath);
        readBack = jsonLaiNurseStorage.readLaiNurse(filePath).get();
        assertEquals(original, new LaiNurse(readBack));

        // Save and read without specifying file path
        original.addPatient(IDA);
        jsonLaiNurseStorage.saveLaiNurse(original); // file path not specified
        readBack = jsonLaiNurseStorage.readLaiNurse().get(); // file path not specified
        assertEquals(original, new LaiNurse(readBack));

    }

    @Test
    public void saveLaiNurse_nullLaiNurse_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveLaiNurse(null, "SomeFile.json"));
    }

    /**
     * Saves {@code laiNurse} at the specified {@code filePath}.
     */
    private void saveLaiNurse(ReadOnlyLaiNurse laiNurse, String filePath) {
        try {
            new JsonLaiNurseStorage(Paths.get(filePath))
                    .saveLaiNurse(laiNurse, addToTestDataPathIfNotNull(filePath));
        } catch (IOException ioe) {
            throw new AssertionError("There should not be an error writing to the file.", ioe);
        }
    }

    @Test
    public void saveLaiNurse_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveLaiNurse(new LaiNurse(), null));
    }
}
