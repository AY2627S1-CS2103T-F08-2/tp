package seedu.address.ui;

import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * Creates the mark that shows before the time of a clashing appointment: a boxed "!" in the clash color, so that a
 * clash does not show by color alone. The box and the "!" are drawn, not typed, on whole pixels of a 16-pixel square,
 * so that the "!" stays in the middle of the box whatever the font and the display scaling.
 */
final class ClashMark {

    private static final double SIZE = 16;
    private static final double BORDER_WIDTH = 1.5;
    private static final String STYLE_CLASS = "clash-mark";
    private static final String BOX_STYLE_CLASS = "clash-mark-box";
    private static final String PART_STYLE_CLASS = "clash-mark-part";

    private ClashMark() {}

    /**
     * Returns a new clash mark.
     */
    static Region create() {
        // The outline is centered on its stroke, so it starts half a stroke inside the square
        double inset = BORDER_WIDTH / 2;
        Rectangle box = new Rectangle(inset, inset, SIZE - BORDER_WIDTH, SIZE - BORDER_WIDTH);
        box.setArcWidth(5);
        box.setArcHeight(5);
        box.getStyleClass().add(BOX_STYLE_CLASS);
        // A bar two pixels wide on the middle column of the square, and a dot under it
        Rectangle bar = new Rectangle(7, 3, 2, 6);
        Circle dot = new Circle(8, 12, 1);
        bar.getStyleClass().add(PART_STYLE_CLASS);
        dot.getStyleClass().add(PART_STYLE_CLASS);

        Pane mark = new Pane(box, bar, dot);
        mark.getStyleClass().add(STYLE_CLASS);
        mark.setMinSize(SIZE, SIZE);
        mark.setPrefSize(SIZE, SIZE);
        mark.setMaxSize(SIZE, SIZE);
        mark.setAccessibleText("Clash");
        return mark;
    }
}
