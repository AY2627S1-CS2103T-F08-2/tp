package seedu.address.commons.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class LogsCenterTest {

    @TempDir
    public Path testFolder;

    @Test
    public void addFileHandler_writableFile_logsToFile() throws Exception {
        Logger logger = getIsolatedLogger("writable");
        Path logFile = testFolder.resolve("test.log");

        LogsCenter.addFileHandler(logger, logFile.toString());
        assertEquals(1, logger.getHandlers().length);
        Handler fileHandler = logger.getHandlers()[0];
        assertInstanceOf(FileHandler.class, fileHandler);

        logger.info("logged to the file");
        fileHandler.close(); // releases the file so that the temporary folder can be deleted
        logger.removeHandler(fileHandler);
        // With more than one file, FileHandler numbers them, as lainurse.log.0 is
        assertTrue(Files.readString(testFolder.resolve("test.log.0")).contains("logged to the file"));
    }

    @Test
    public void addFileHandler_fileCannotBeWritten_warnsWithoutThrowing() {
        Logger logger = getIsolatedLogger("unwritable");
        List<LogRecord> records = new ArrayList<>();
        Handler recorder = new Handler() {
            @Override
            public void publish(LogRecord logRecord) {
                records.add(logRecord);
            }

            @Override
            public void flush() {}

            @Override
            public void close() {}
        };
        logger.addHandler(recorder);
        // A log file in a folder that does not exist cannot be opened, like a read-only one
        String logFile = testFolder.resolve("missing").resolve("test.log").toString();

        LogsCenter.addFileHandler(logger, logFile);
        assertEquals(List.of(recorder), List.of(logger.getHandlers()));
        assertEquals(1, records.size());
        assertEquals(Level.WARNING, records.get(0).getLevel());
        assertTrue(records.get(0).getMessage().startsWith("Could not log to " + logFile));
        logger.removeHandler(recorder);
    }

    private static Logger getIsolatedLogger(String name) {
        Logger logger = Logger.getLogger(LogsCenterTest.class.getName() + "." + name);
        logger.setUseParentHandlers(false);
        return logger;
    }
}
