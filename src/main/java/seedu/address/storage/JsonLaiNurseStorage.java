package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.FileUtil;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.ReadOnlyLaiNurse;

/**
 * A class to access LaiNurse data stored as a JSON file on the hard disk.
 */
public class JsonLaiNurseStorage {

    private static final Logger logger = LogsCenter.getLogger(JsonLaiNurseStorage.class);

    private Path filePath;

    public JsonLaiNurseStorage(Path filePath) {
        this.filePath = filePath;
    }

    public Path getLaiNurseFilePath() {
        return filePath;
    }

    /**
     * Returns LaiNurse data as a {@link ReadOnlyLaiNurse}.
     * Returns {@code Optional.empty()} if storage file is not found.
     *
     * @throws DataLoadingException if loading the data from storage failed.
     */
    public Optional<ReadOnlyLaiNurse> readLaiNurse() throws DataLoadingException {
        return readLaiNurse(filePath);
    }

    /**
     * Similar to {@link #readLaiNurse()}.
     *
     * @param filePath location of the data. Cannot be null.
     * @throws DataLoadingException if loading the data from storage failed.
     */
    public Optional<ReadOnlyLaiNurse> readLaiNurse(Path filePath) throws DataLoadingException {
        requireNonNull(filePath);

        Optional<JsonSerializableLaiNurse> jsonLaiNurse = JsonUtil.readJsonFile(
                filePath, JsonSerializableLaiNurse.class);
        if (!jsonLaiNurse.isPresent()) {
            return Optional.empty();
        }

        try {
            return Optional.of(jsonLaiNurse.get().toModelType());
        } catch (IllegalValueException ive) {
            logger.info("Illegal values found in " + filePath + ": " + ive.getMessage());
            throw new DataLoadingException(ive);
        }
    }

    /**
     * Saves the given {@link ReadOnlyLaiNurse} to the storage.
     * @param laiNurse cannot be null.
     * @throws IOException if there was any problem writing to the file.
     */
    public void saveLaiNurse(ReadOnlyLaiNurse laiNurse) throws IOException {
        saveLaiNurse(laiNurse, filePath);
    }

    /**
     * Similar to {@link #saveLaiNurse(ReadOnlyLaiNurse)}.
     *
     * @param filePath location of the data. Cannot be null.
     */
    public void saveLaiNurse(ReadOnlyLaiNurse laiNurse, Path filePath) throws IOException {
        requireNonNull(laiNurse);
        requireNonNull(filePath);

        FileUtil.createIfMissing(filePath);
        JsonUtil.saveJsonFile(new JsonSerializableLaiNurse(laiNurse), filePath);
    }

}
