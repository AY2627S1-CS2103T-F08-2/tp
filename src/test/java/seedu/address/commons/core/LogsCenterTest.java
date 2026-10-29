package seedu.address.commons.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class LogsCenterTest {

    @TempDir
    public Path temporaryFolder;

    @Test
    public void addFileHandler_writableFile_addsFileHandler() {
        Logger logger = Logger.getLogger("lainurse.test.writable");
        logger.setUseParentHandlers(false);

        assertTrue(LogsCenter.addFileHandler(logger, temporaryFolder.resolve("test.log").toString()));
        assertEquals(1, fileHandlersOf(logger).size());
        closeHandlers(logger);
    }

    @Test
    public void addFileHandler_readOnlyFile_warnsAndKeepsOtherHandlers() throws IOException {
        Logger logger = Logger.getLogger("lainurse.test.readonly");
        logger.setUseParentHandlers(false);
        List<LogRecord> records = new ArrayList<>();
        logger.addHandler(new Handler() {
            @Override
            public void publish(LogRecord record) {
                records.add(record);
            }

            @Override
            public void flush() {}

            @Override
            public void close() {}
        });
        // The handler writes the first file of the pattern, which is read-only here, as in #199
        String pattern = temporaryFolder.resolve("readonly.log").toString();
        File firstFile = Files.createFile(Path.of(pattern + ".0")).toFile();
        assertTrue(firstFile.setWritable(false));

        try {
            assertFalse(LogsCenter.addFileHandler(logger, pattern));
            assertTrue(fileHandlersOf(logger).isEmpty());
            assertEquals(1, records.size());
            assertTrue(records.get(0).getMessage().contains("logs to the console only"));
        } finally {
            firstFile.setWritable(true);
            closeHandlers(logger);
        }
    }

    @Test
    public void getLogger_afterSetUp_namedUnderBaseLogger() {
        assertEquals("lainurse.LogsCenterTest", LogsCenter.getLogger(LogsCenterTest.class).getName());
    }

    private static List<Handler> fileHandlersOf(Logger logger) {
        return List.of(logger.getHandlers()).stream().filter(FileHandler.class::isInstance).toList();
    }

    private static void closeHandlers(Logger logger) {
        for (Handler handler : logger.getHandlers()) {
            handler.close();
            logger.removeHandler(handler);
        }
    }
}
