package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.LaiNurse;
import seedu.address.testutil.PatientBuilder;
import seedu.address.testutil.TypicalPatients;

public class JsonSerializableLaiNurseTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableLaiNurseTest");
    private static final Path TYPICAL_PATIENTS_FILE = TEST_DATA_FOLDER.resolve("typicalPatientsLaiNurse.json");
    private static final Path INVALID_PATIENT_FILE = TEST_DATA_FOLDER.resolve("invalidPatientLaiNurse.json");
    private static final Path DUPLICATE_PATIENT_FILE = TEST_DATA_FOLDER.resolve("duplicatePatientLaiNurse.json");
    private static final Path NOTES_AND_DETAILS_FILE = TEST_DATA_FOLDER.resolve("notesAndDetailsLaiNurse.json");

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

    @Test
    public void toModelType_notesAndDetailsFile_success() throws Exception {
        JsonSerializableLaiNurse dataFromFile = JsonUtil.readJsonFile(NOTES_AND_DETAILS_FILE,
                JsonSerializableLaiNurse.class).get();
        assertEquals(getNotesAndDetailsLaiNurse(), dataFromFile.toModelType());
    }

    @Test
    public void constructor_notesAndDetails_savedWithAppointmentsAsObjects() throws Exception {
        String json = JsonUtil.toJsonString(new JsonSerializableLaiNurse(getNotesAndDetailsLaiNurse()));
        assertTrue(json.contains("\"notes\" : \"DAUGHTER MEI LING (91234567) IS THE MAIN CONTACT\""));
        assertTrue(json.contains("\"dateTime\" : \"2026-10-05 09:30\""));
        assertTrue(json.contains("\"details\" : \"INSULIN REVIEW\""));
        // an appointment read from a plain string is saved as an object, with no details
        assertTrue(json.contains("\"dateTime\" : \"2026-10-06\""));
    }

    /**
     * Returns the patients in {@code notesAndDetailsLaiNurse.json}.
     */
    private static LaiNurse getNotesAndDetailsLaiNurse() {
        LaiNurse laiNurse = new LaiNurse();
        laiNurse.addPatient(new PatientBuilder().withId("S1234567A").withName("Alex Yeoh").withPhone("87438807")
                .withAddress("Blk 30 Geylang Street 29, #06-40").withMedicalHistory("Type 2 diabetes")
                .withNotes("Daughter Mei Ling (91234567) is the main contact")
                .withAppointments("2026-11-02").withAppointment("2026-10-05 09:30", "Insulin review").build());
        laiNurse.addPatient(new PatientBuilder().withId("S2345678B").withName("Bernice Yu").withPhone("99272758")
                .withAddress("Blk 30 Lorong 3 Serangoon Gardens, #07-18")
                .withAppointments("2026-10-06", "2026-10-13 10:00").build());
        return laiNurse;
    }
}
