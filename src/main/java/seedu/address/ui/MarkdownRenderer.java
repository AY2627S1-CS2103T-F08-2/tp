package seedu.address.ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Parses markdown text into native JavaFX nodes without external dependencies.
 */
public class MarkdownRenderer {

    // NUS Theme Palette
    private static final String NUS_ORANGE = "#ef7c00";
    private static final String NUS_BLUE = "#003d7c";
    private static final String NUS_BLUE_BORDER = "#1f5f9e"; // Lightened tint for borders/badges

    // Regex to capture **bold**, `code`, and <kbd>key</kbd>
    private static final Pattern INLINE_PATTERN =
            Pattern.compile("(<kbd>.*?</kbd>|\\*\\*.*?\\*\\*|`.*?`)");

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

            // Skip Jekyll frontmatter (--- at top)
            if (line.equals("---")) {
                frontmatterDashes++;
                inFrontmatter = frontmatterDashes < 2;
                continue;
            }
            if (inFrontmatter || line.startsWith("{:") || line.isEmpty()) {
                continue;
            }

            // Skip raw HTML block wrappers and alerts
            if (line.matches("^</?div.*?>$") || line.matches("^</?span.*?>$")) {
                continue;
            }

            // Skip raw image tags (e.g. ![Ui](images/Ui.png))
            if (line.matches("^!\\[.*?\\]\\(.*?\\)$")) {
                continue;
            }

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
                // Horizontal divider with NUS Orange accent tint
                Separator separator = new Separator();
                separator.setStyle("-fx-padding: 8 0 8 0; -fx-opacity: 0.35;");
                container.getChildren().add(separator);
            } else if (line.startsWith("* ") || line.startsWith("- ")) {
                // Bullet point with NUS Orange bullet dot
                String text = cleanHtmlLineBreaks(line.substring(2));
                TextFlow bulletFlow = buildInlineTextFlow("•  " + text);
                bulletFlow.setStyle("-fx-padding: 0 0 0 16;");
                container.getChildren().add(bulletFlow);
            } else if (line.matches("^\\d+\\.\\s+.*")) {
                // Numbered list item
                container.getChildren().add(buildInlineTextFlow(cleanHtmlLineBreaks(line)));
            } else if (line.startsWith("|") && line.endsWith("|")) {
                // Table row in styled badge using NUS Blue background
                if (!line.contains("---")) {
                    Label tableLine = new Label(cleanHtmlLineBreaks(line.replaceAll("\\|", "   ")));
                    tableLine.setStyle("-fx-font-family: monospace; -fx-text-fill: #ffffff; "
                            + "-fx-background-color: " + NUS_BLUE + "; -fx-padding: 5 10; "
                            + "-fx-border-color: " + NUS_BLUE_BORDER + "; -fx-background-radius: 4; "
                            + "-fx-border-radius: 4;");
                    container.getChildren().add(tableLine);
                }
            } else {
                // Standard paragraph
                container.getChildren().add(buildInlineTextFlow(cleanHtmlLineBreaks(line)));
            }
        }

        return container;
    }

    /**
     * Converts HTML line breaks into real newline characters.
     */
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
            if (match.startsWith("**") && match.endsWith("**")) {
                // Bold text
                Text bold = new Text(match.substring(2, match.length() - 2));
                bold.setStyle("-fx-fill: #ffffff; -fx-font-weight: bold; -fx-font-size: 13px;");
                flow.getChildren().add(bold);
            } else if (match.startsWith("`") && match.endsWith("`")) {
                // Inline code tag in NUS Orange
                Text code = new Text(" " + match.substring(1, match.length() - 1) + " ");
                code.setStyle("-fx-fill: " + NUS_ORANGE + "; -fx-font-family: monospace; "
                        + "-fx-font-size: 12px; -fx-font-weight: bold;");
                flow.getChildren().add(code);
            } else if (match.startsWith("<kbd>") && match.endsWith("</kbd>")) {
                // Keyboard shortcut in light blue accent
                Text kbd = new Text(" [" + match.substring(5, match.length() - 6) + "] ");
                kbd.setStyle("-fx-fill: #79c0ff; -fx-font-family: monospace; -fx-font-size: 12px;");
                flow.getChildren().add(kbd);
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
