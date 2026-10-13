package seedu.address.commons.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses and serves command help directly from the bundled UserGuide.md.
 */
public class HelpBookUtil {

    public static final String USER_GUIDE_RESOURCE_PATH = "/docs/UserGuide.md";

    private static final Pattern COMMAND_HEADER_PATTERN =
            Pattern.compile("^###\\s+.*?:\\s*`([a-zA-Z]+)`", Pattern.CASE_INSENSITIVE);

    private static final Pattern FORMAT_PATTERN =
            Pattern.compile("^Format:\\s*`?([^`\r\n]+)`?", Pattern.CASE_INSENSITIVE);

    /**
     * Stores format and example commands for a single command word.
     */
    public static class CommandHelpInfo {
        private final String commandWord;
        private String format = "";
        private final List<String> examples = new ArrayList<>();

        public CommandHelpInfo(String commandWord) {
            this.commandWord = commandWord;
        }

        public String getCommandWord() {
            return commandWord;
        }

        public String getFormat() {
            return format;
        }

        public List<String> getExamples() {
            return Collections.unmodifiableList(examples);
        }

        /**
         * Formats output for targeted help.
         */
        public String toTargetedDisplay() {
            StringBuilder sb = new StringBuilder();
            sb.append("Command: ").append(commandWord).append("\n");
            if (!format.isBlank()) {
                sb.append("Format: ").append(format).append("\n\n");
            }
            if (!examples.isEmpty()) {
                sb.append("Examples:\n");
                for (String example : examples) {
                    sb.append("  • ").append(example).append("\n");
                }
            }
            return sb.toString().stripTrailing();
        }
    }

    private static final Map<String, CommandHelpInfo> COMMAND_MAP = new LinkedHashMap<>();
    private static boolean isLoaded = false;

    private static synchronized void loadGuideIfNeeded() {
        if (isLoaded) {
            return;
        }

        try (InputStream is = HelpBookUtil.class.getResourceAsStream(USER_GUIDE_RESOURCE_PATH)) {
            if (is == null) {
                isLoaded = true;
                return;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                CommandHelpInfo currentInfo = null;
                boolean inExamples = false;
                StringBuilder pendingExample = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    String trimmed = line.trim();

                    // Check for new command section: ### Header: `cmd`
                    Matcher headerMatcher = COMMAND_HEADER_PATTERN.matcher(trimmed);
                    if (headerMatcher.find()) {
                        flushExample(currentInfo, pendingExample);
                        String cmd = headerMatcher.group(1).toLowerCase();
                        currentInfo = new CommandHelpInfo(cmd);
                        COMMAND_MAP.put(cmd, currentInfo);
                        inExamples = false;
                        continue;
                    }

                    // Stop parsing at next major markdown chapter
                    if (trimmed.startsWith("## ")) {
                        flushExample(currentInfo, pendingExample);
                        currentInfo = null;
                        inExamples = false;
                        continue;
                    }

                    if (currentInfo == null) {
                        continue;
                    }

                    // Check for Format: ...
                    Matcher formatMatcher = FORMAT_PATTERN.matcher(trimmed);
                    if (formatMatcher.find()) {
                        flushExample(currentInfo, pendingExample);
                        currentInfo.format = formatMatcher.group(1).trim();
                        inExamples = false;
                        continue;
                    }

                    // Check for Examples: section
                    if (trimmed.equalsIgnoreCase("Examples:") || trimmed.startsWith("Examples")) {
                        flushExample(currentInfo, pendingExample);
                        inExamples = true;
                        continue;
                    }

                    if (inExamples) {
                        if (trimmed.startsWith("-") || trimmed.startsWith("*")) {
                            flushExample(currentInfo, pendingExample);
                            String text = trimmed.replaceAll("^[-*]\\s*", "");
                            pendingExample.append(text);
                        } else if (trimmed.startsWith("###") || trimmed.startsWith("##")) {
                            flushExample(currentInfo, pendingExample);
                            inExamples = false;
                        } else if (!trimmed.isEmpty() && pendingExample.length() > 0) {
                            pendingExample.append(" ").append(trimmed);
                        }
                    }
                }
                flushExample(currentInfo, pendingExample);
            }
        } catch (Exception e) {
            // Keep empty map on read failure
        }

        isLoaded = true;
    }

    private static void flushExample(CommandHelpInfo info, StringBuilder pending) {
        if (info != null && pending.length() > 0) {
            String cleaned = pending.toString().replace("`", "").trim();
            if (!cleaned.isEmpty()) {
                info.examples.add(cleaned);
            }
            pending.setLength(0);
        }
    }

    public static String getBundledUserGuide() {
        try (InputStream is = HelpBookUtil.class.getResourceAsStream(USER_GUIDE_RESOURCE_PATH)) {
            if (is == null) {
                return "Offline User Guide is not available.";
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append(System.lineSeparator());
                }
                return sb.toString();
            }
        } catch (Exception e) {
            return "Unable to load User Guide: " + e.getMessage();
        }
    }

    public static String getAllCommandsSummary() {
        loadGuideIfNeeded();
        if (COMMAND_MAP.isEmpty()) {
            return "Available commands: add, clear, delete, edit, exit, find, help, list, view, viewappt";
        }

        StringBuilder sb = new StringBuilder("Available Commands:\n");
        for (CommandHelpInfo info : COMMAND_MAP.values()) {
            sb.append("• ").append(info.getCommandWord());
            if (!info.getFormat().isBlank()) {
                sb.append(" : ").append(info.getFormat());
            }
            sb.append("\n");
        }
        sb.append("\nType 'help --<command>' (e.g. 'help --delete') for detailed examples.");
        return sb.toString();
    }

    public static Optional<String> getSpecificCommandHelp(String commandWord) {
        loadGuideIfNeeded();
        CommandHelpInfo info = COMMAND_MAP.get(commandWord.toLowerCase());
        if (info == null) {
            return Optional.empty();
        }
        return Optional.of(info.toTargetedDisplay());
    }
}
