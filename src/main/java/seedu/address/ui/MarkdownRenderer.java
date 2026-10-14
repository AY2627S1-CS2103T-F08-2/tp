package seedu.address.ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

/**
 * Parses markdown text into native JavaFX nodes without external dependencies.
 */
public class MarkdownRenderer {

    private static final String NUS_ORANGE = "#ef7c00";
    private static final String NUS_BLUE = "#003d7c";
    private static final String NUS_BLUE_BORDER = "#1f5f9e";

    // Matches links [text](url), <kbd>key</kbd>, **bold**, and `code`
    private static final Pattern INLINE_PATTERN =
            Pattern.compile("(\\[.*?\\]\\(.*?\\)|<kbd>.*?</kbd>|\\*\\*.*?\\*\\*|`.*?`)");

    /**
     * Renders the given markdown text string into a JavaFX {@code VBox} container.
     *
     * @param markdown The raw markdown content to parse and render.
     * @return A {@code VBox} containing the rendered JavaFX nodes.
     */
    public static VBox render(String markdown) {
        VBox container = new VBox();
        container.setSpacing(8);
        container.setPadding(new Insets(15));
        container.setFillWidth(true);

        String[] lines = markdown.split("\\r?\\n");
        boolean inFrontmatter = false;
        int frontmatterDashes = 0;

        for (String rawLine : lines) {
            String line = rawLine.trim();

            if (line.equals("---")) {
                frontmatterDashes++;
                inFrontmatter = frontmatterDashes < 2;
                continue;
            }
            if (inFrontmatter || line.startsWith("{:") || line.isEmpty()
                    || line.equalsIgnoreCase("Table of Contents")
                    || line.equalsIgnoreCase("* Table of Contents")) {
                continue;
            }

            if (line.matches("^</?div.*?>$") || line.matches("^</?span.*?>$")
                    || line.matches("^!\\[.*?\\]\\(.*?\\)$")) {
                continue;
            }

            line = line.replace(":information_source:", "ℹ️");

            // Headings
            if (line.startsWith("### ")) {
                Label h3 = new Label(cleanHtmlLineBreaks(line.substring(4)).replaceAll("`", ""));
                h3.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: "
                        + NUS_ORANGE + "; -fx-padding: 10 0 4 0;");
                container.getChildren().add(h3);
            } else if (line.startsWith("## ")) {
                Label h2 = new Label(cleanHtmlLineBreaks(line.substring(3)).replaceAll("`", ""));
                h2.setStyle("-fx-font-size: 19px; -fx-font-weight: bold; -fx-text-fill: "
                        + NUS_ORANGE + "; -fx-padding: 14 0 6 0;");
                container.getChildren().add(h2);
            } else if (line.startsWith("# ")) {
                Label h1 = new Label(cleanHtmlLineBreaks(line.substring(2)).replaceAll("`", ""));
                h1.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #ffffff; "
                        + "-fx-padding: 16 0 8 0;");
                container.getChildren().add(h1);
            } else if (line.matches("^-{3,}$")) {
                Separator separator = new Separator();
                separator.setStyle("-fx-padding: 8 0 8 0; -fx-opacity: 0.35;");
                container.getChildren().add(separator);
            } else if (line.matches("^[\\-\\s|:]+$") && line.contains("|")) {
                // Table header separator (e.g. --------|------------------)
                continue;
            } else if (line.contains("|") && !line.startsWith("#")) {
                // Table row
                Label tableLine = new Label(cleanHtmlLineBreaks(line.replaceAll("\\|", "   ")));
                tableLine.setStyle("-fx-font-family: monospace; -fx-text-fill: #ffffff; "
                        + "-fx-background-color: " + NUS_BLUE + "; -fx-padding: 5 10; "
                        + "-fx-border-color: " + NUS_BLUE_BORDER + "; -fx-background-radius: 4; "
                        + "-fx-border-radius: 4;");
                container.getChildren().add(tableLine);
            } else if (line.startsWith("* ") || line.startsWith("- ")) {
                String text = cleanHtmlLineBreaks(line.substring(2));
                TextFlow bulletFlow = buildInlineTextFlow("•  " + text);
                bulletFlow.setStyle("-fx-padding: 0 0 0 16;");
                container.getChildren().add(bulletFlow);
            } else if (line.matches("^\\d+\\.\\s+.*")) {
                container.getChildren().add(buildInlineTextFlow(cleanHtmlLineBreaks(line)));
            } else {
                container.getChildren().add(buildInlineTextFlow(cleanHtmlLineBreaks(line)));
            }
        }

        return container;
    }

    private static String cleanHtmlLineBreaks(String text) {
        return text.replaceAll("(?i)<br\\s*/?>", "\n");
    }

    private static TextFlow buildInlineTextFlow(String text) {
        TextFlow flow = new TextFlow();
        Matcher matcher = INLINE_PATTERN.matcher(text);
        int lastIdx = 0;

        while (matcher.find()) {
            if (matcher.start() > lastIdx) {
                Text plain = new Text(text.substring(lastIdx, matcher.start()));
                plain.setStyle("-fx-fill: #c9d1d9; -fx-font-size: 13px;");
                flow.getChildren().add(plain);
            }

            String match = matcher.group();
            if (match.startsWith("[") && match.contains("](") && match.endsWith(")")) {
                int splitIndex = match.indexOf("](");
                String label = match.substring(1, splitIndex);
                String target = match.substring(splitIndex + 2, match.length() - 1);

                if (target.startsWith("http://") || target.startsWith("https://")) {
                    Hyperlink link = new Hyperlink(label);
                    String defaultStyle = "-fx-text-fill: #58a6ff; -fx-padding: 0; "
                            + "-fx-border-color: transparent; -fx-font-size: 13px; -fx-underline: true;";
                    String copiedStyle = "-fx-text-fill: #ef7c00; -fx-padding: 0; " // vibrant success green
                            + "-fx-border-color: transparent; -fx-font-size: 13px; -fx-underline: false;";

                    link.setStyle(defaultStyle);

                    link.setOnAction(e -> {
                        Clipboard clipboard = Clipboard.getSystemClipboard();
                        ClipboardContent content = new ClipboardContent();
                        content.putString(target);
                        clipboard.setContent(content);

                        link.setText(label + " (Copied!)");
                        link.setStyle(copiedStyle);

                        PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
                        pause.setOnFinished(ev -> {
                            link.setText(label);
                            link.setStyle(defaultStyle);
                        });
                        pause.play();
                    });

                    flow.getChildren().add(link);
                } else {
                    Text internalLink = new Text(label);
                    internalLink.setStyle("-fx-fill: #79c0ff; -fx-font-weight: bold; -fx-font-size: 13px;");
                    flow.getChildren().add(internalLink);
                }
            } else if (match.startsWith("<kbd>") && match.endsWith("</kbd>")) {
                Text kbd = new Text(" [" + match.substring(5, match.length() - 6) + "] ");
                kbd.setStyle("-fx-fill: #79c0ff; -fx-font-family: monospace; -fx-font-size: 12px;");
                flow.getChildren().add(kbd);
            } else if (match.startsWith("**") && match.endsWith("**")) {
                Text bold = new Text(match.substring(2, match.length() - 2));
                bold.setStyle("-fx-fill: #ffffff; -fx-font-weight: bold; -fx-font-size: 13px;");
                flow.getChildren().add(bold);
            } else if (match.startsWith("`") && match.endsWith("`")) {
                Text code = new Text(" " + match.substring(1, match.length() - 1) + " ");
                code.setStyle("-fx-fill: " + NUS_ORANGE + "; -fx-font-family: monospace; "
                        + "-fx-font-size: 12px; -fx-font-weight: bold;");
                flow.getChildren().add(code);
            }

            lastIdx = matcher.end();
        }

        if (lastIdx < text.length()) {
            Text remaining = new Text(text.substring(lastIdx));
            remaining.setStyle("-fx-fill: #c9d1d9; -fx-font-size: 13px;");
            flow.getChildren().add(remaining);
        }

        return flow;
    }
}
