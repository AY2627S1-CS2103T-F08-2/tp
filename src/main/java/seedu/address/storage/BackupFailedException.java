package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Signals that the data file was saved, but its backup could not be. The data is safe in the data file, so a caller
 * keeps the change that it saved, unlike after a save that failed altogether.
 */
public class BackupFailedException extends IOException {

    private final Path backupFilePath;

    /**
     * Constructs a {@code BackupFailedException} for the backup at {@code backupFilePath}, which could not be written
     * because of {@code cause}.
     */
    public BackupFailedException(Path backupFilePath, IOException cause) {
        super("Could not back up the data to " + backupFilePath + ": " + cause.getMessage(), requireNonNull(cause));
        this.backupFilePath = backupFilePath;
    }

    /**
     * Returns the path of the backup that could not be written.
     */
    public Path getBackupFilePath() {
        return backupFilePath;
    }
}
