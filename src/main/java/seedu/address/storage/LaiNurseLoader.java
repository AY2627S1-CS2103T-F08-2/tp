package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.util.StringUtil;
import seedu.address.model.LaiNurse;
import seedu.address.model.ReadOnlyLaiNurse;
import seedu.address.model.util.SampleDataUtil;

/**
 * Loads the LaiNurse data that the app starts with from a {@link Storage}, falling back to the backup when the data
 * file cannot be loaded.
 * <ul>
 * <li>If the data file loads, its data is used.</li>
 * <li>If the data file is missing, the sample data is used, even if there is a backup.</li>
 * <li>If the data file cannot be loaded, it is copied to the corrupt copy, and the backup's data is used, or no data
 * if the backup is missing or cannot be loaded either.</li>
 * </ul>
 */
public class LaiNurseLoader {

    public static final String MESSAGE_BACKUP_LOADED = "%1$s could not be loaded, so LaiNurse loaded its backup, %2$s,"
            + " which holds the data it last saved.";
    public static final String MESSAGE_BACKUP_NOT_LOADED = "%1$s could not be loaded, and neither could its backup,"
            + " %2$s, so LaiNurse started with no data. The next command that succeeds saves this empty data.";
    public static final String MESSAGE_BACKUP_MISSING = "%1$s could not be loaded, and there is no backup at %2$s,"
            + " so LaiNurse started with no data. The next command that succeeds saves this empty data.";
    public static final String MESSAGE_CORRUPT_FILE_KEPT = "A copy of the file that could not be loaded is kept at"
            + " %1$s. To restore it, close LaiNurse, fix the copy, and copy it over %2$s.";
    public static final String MESSAGE_CORRUPT_FILE_NOT_KEPT = "The file that could not be loaded could not be copied"
            + " to %1$s. To keep it, close LaiNurse now and copy it somewhere else, as the next command that succeeds"
            + " overwrites it.";

    private static final Logger logger = LogsCenter.getLogger(LaiNurseLoader.class);

    /**
     * The data that the app starts with, and the message that tells the user where it came from, if it did not come
     * from the data file as usual.
     */
    public record Result(ReadOnlyLaiNurse data, Optional<String> message) {
        /**
         * Creates a {@code Result} with the given data and message.
         */
        public Result {
            requireNonNull(data);
            requireNonNull(message);
        }
    }

    private LaiNurseLoader() {}

    /**
     * Returns the data that the app starts with, as read from {@code storage}.
     */
    public static Result load(Storage storage) {
        requireNonNull(storage);
        logger.info("Using data file : " + storage.getLaiNurseFilePath());

        try {
            Optional<ReadOnlyLaiNurse> laiNurseOptional = storage.readLaiNurse();
            if (laiNurseOptional.isEmpty()) {
                logger.info("Creating a new data file " + storage.getLaiNurseFilePath()
                        + " populated with sample data.");
            }
            return new Result(laiNurseOptional.orElseGet(SampleDataUtil::getSampleLaiNurse), Optional.empty());
        } catch (DataLoadingException e) {
            logger.warning("Data file at " + storage.getLaiNurseFilePath() + " could not be loaded.");
        }

        String corruptFileMessage = keepCorruptFile(storage);
        try {
            Optional<ReadOnlyLaiNurse> backupOptional = storage.readLaiNurseBackup();
            if (backupOptional.isPresent()) {
                logger.warning("Loaded the backup at " + storage.getLaiNurseBackupFilePath() + " instead.");
                return new Result(backupOptional.get(), Optional.of(String.format(MESSAGE_BACKUP_LOADED,
                        storage.getLaiNurseFilePath(), storage.getLaiNurseBackupFilePath())
                        + "\n" + corruptFileMessage));
            }
            logger.warning("No backup at " + storage.getLaiNurseBackupFilePath()
                    + ". Will be starting with an empty patient list.");
            return new Result(new LaiNurse(), Optional.of(String.format(MESSAGE_BACKUP_MISSING,
                    storage.getLaiNurseFilePath(), storage.getLaiNurseBackupFilePath()) + "\n" + corruptFileMessage));
        } catch (DataLoadingException e) {
            logger.warning("Backup at " + storage.getLaiNurseBackupFilePath() + " could not be loaded either."
                    + " Will be starting with an empty patient list.");
            return new Result(new LaiNurse(), Optional.of(String.format(MESSAGE_BACKUP_NOT_LOADED,
                    storage.getLaiNurseFilePath(), storage.getLaiNurseBackupFilePath()) + "\n" + corruptFileMessage));
        }
    }

    /**
     * Copies the data file that could not be loaded to the corrupt copy, and returns the message that tells the user
     * whether it was kept.
     */
    private static String keepCorruptFile(Storage storage) {
        try {
            storage.keepCorruptLaiNurseFile();
            logger.info("Copied the data file to " + storage.getLaiNurseCorruptFilePath());
            return String.format(MESSAGE_CORRUPT_FILE_KEPT,
                    storage.getLaiNurseCorruptFilePath(), storage.getLaiNurseFilePath());
        } catch (IOException e) {
            logger.warning("Failed to copy the data file to " + storage.getLaiNurseCorruptFilePath() + " : "
                    + StringUtil.getDetails(e));
            return String.format(MESSAGE_CORRUPT_FILE_NOT_KEPT, storage.getLaiNurseCorruptFilePath());
        }
    }

}
