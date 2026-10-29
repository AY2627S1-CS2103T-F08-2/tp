package seedu.address.ui;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

/**
 * Creates the mark that shows before the time of a marked appointment: a pushpin, drawn on a 16-pixel square, so that
 * it looks the same whatever the font. A pin says only that the nurse marked the appointment, which they may use for a
 * visit done, billed or to follow up. A marked appointment never clashes, so it never needs a {@link ClashMark} too.
 * The pin takes the same square as a {@link ClashMark}, and about as much of it as the clash mark's box.
 */
final class PinMark {

    private static final double SIZE = 16;
    /** The tilt of the pin, clockwise from upright, so that its needle points to the bottom left. */
    private static final double TILT = 45;
    /** An upright pushpin in the 16-pixel square: a cap, a body, a flange and a needle. */
    private static final String PIN = "M5.5 1.5 H10.5 Q11 1.5 11 2 V3 Q11 3.5 10.5 3.5 H9.8 L10.3 7.5 H12.5 V9"
            + " H8.6 L8 14.5 L7.4 9 H3.5 V7.5 H5.7 L6.2 3.5 H5.5 Q5 3.5 5 3 V2 Q5 1.5 5.5 1.5 Z";
    /**
     * How much the tilted pin is enlarged, so that it is about 14 pixels across, as the clash mark's box is 16. Tilted,
     * the pin is 11 pixels across, and its middle is 1 pixel up and right of the square's middle, so enlarging it
     * moves its middle by this much too, which {@link #create} moves back.
     */
    private static final double SCALE = 1.25;
    private static final String STYLE_CLASS = "pin-mark";
    private static final String SHAPE_STYLE_CLASS = "pin-mark-shape";

    private PinMark() {}

    /**
     * Returns a new pin mark, in the color that the style sheet gives it.
     */
    static Region create() {
        SVGPath pin = new SVGPath();
        pin.setContent(PIN);
        pin.setRotate(TILT);
        pin.setScaleX(SCALE);
        pin.setScaleY(SCALE);
        // Back to the middle of the square, from 1 pixel up and right, enlarged
        pin.setTranslateX(-SCALE);
        pin.setTranslateY(SCALE);
        pin.getStyleClass().add(SHAPE_STYLE_CLASS);

        Pane mark = new Pane(pin);
        mark.getStyleClass().add(STYLE_CLASS);
        mark.setMinSize(SIZE, SIZE);
        mark.setPrefSize(SIZE, SIZE);
        mark.setMaxSize(SIZE, SIZE);
        mark.setAccessibleText("Marked");
        return mark;
    }

    /**
     * Colors the pin of {@code mark}, which {@link #create} made, {@code color}, such as red, or the color that the
     * style sheet gives it if {@code color} is null.
     */
    static void setColor(Region mark, Color color) {
        Node shape = mark.lookup("." + SHAPE_STYLE_CLASS);
        assert shape != null : "The mark must be one that create() made.";
        // An inline style wins over the style sheet, which colors the pin by the row it is in
        shape.setStyle(color == null ? "" : "-fx-fill: " + toWeb(color) + ";");
    }

    private static String toWeb(Color color) {
        return String.format("rgba(%d, %d, %d, %s)", Math.round(color.getRed() * 255),
                Math.round(color.getGreen() * 255), Math.round(color.getBlue() * 255), color.getOpacity());
    }
}
