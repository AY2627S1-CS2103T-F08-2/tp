package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.LaiNurse;
import seedu.address.testutil.TypicalPatients;

public class JsonSerializableLaiNurseTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableLaiNurseTest");
    private static final Path TYPICAL_PATIENTS_FILE = TEST_DATA_FOLDER.resolve("typicalPatientsLaiNurse.json");
    private static final Path INVALID_PATIENT_FILE = TEST_DATA_FOLDER.resolve("invalidPatientLaiNurse.json");
    private static final Path DUPLICATE_PATIENT_FILE = TEST_DATA_FOLDER.resolve("duplicatePatientLaiNurse.json");

    @Test
    public void toModelType_typicalPatientsFile_success() throws Exception {
        JsonSerializableLaiNurse dataFromFile = JsonUtil.readJsonFile(TYPICAL_PATIENTS_FILE,
                JsonSerializableLaiNurse.class).get();
        LaiNurse laiNurseFromFile = dataFromFile.toModelType();
        LaiNurse typicalPatientsLaiNurse = TypicalPatients.getTypicalLaiNurse();
        assertEquals(laiNurseFromFile, typicalPatientsLaiNurse);
    }

    @Test
    public void toModelType_invalidPatientFile_throwsIllegalValueException() throws Exception {
        JsonSerializableLaiNurse dataFromFile = JsonUtil.readJsonFile(INVALID_PATIENT_FILE,
                JsonSerializableLaiNurse.class).get();
        assertThrows(IllegalValueException.class, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicatePatients_throwsIllegalValueException() throws Exception {
        JsonSerializableLaiNurse dataFromFile = JsonUtil.readJsonFile(DUPLICATE_PATIENT_FILE,
                JsonSerializableLaiNurse.class).get();
        assertThrows(IllegalValueException.class, JsonSerializableLaiNurse.MESSAGE_DUPLICATE_PATIENT,
                dataFromFile::toModelType);
    }

}
