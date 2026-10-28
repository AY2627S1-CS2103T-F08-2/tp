package seedu.address.ui;

import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.SVGPath;

/**
 * Creates the mark that shows before the time of a marked appointment: a pushpin, drawn on a 16-pixel square, so that
 * it looks the same whatever the font. A pin says only that the nurse marked the appointment, which they may use for a
 * visit done, billed or to follow up. A marked appointment never clashes, so it never needs a {@link ClashMark} too.
 */
final class PinMark {

    private static final double SIZE = 16;
    /** The tilt of the pin, clockwise from upright, so that its needle points to the bottom left. */
    private static final double TILT = 45;
    /** An upright pushpin in the 16-pixel square: a cap, a body, a flange and a needle. */
    private static final String PIN = "M5.5 1.5 H10.5 Q11 1.5 11 2 V3 Q11 3.5 10.5 3.5 H9.8 L10.3 7.5 H12.5 V9"
            + " H8.6 L8 14.5 L7.4 9 H3.5 V7.5 H5.7 L6.2 3.5 H5.5 Q5 3.5 5 3 V2 Q5 1.5 5.5 1.5 Z";
    private static final String STYLE_CLASS = "pin-mark";
    private static final String SHAPE_STYLE_CLASS = "pin-mark-shape";

    private PinMark() {}

    /**
     * Returns a new pin mark.
     */
    static Region create() {
        SVGPath pin = new SVGPath();
        pin.setContent(PIN);
        pin.setRotate(TILT);
        pin.getStyleClass().add(SHAPE_STYLE_CLASS);

        Pane mark = new Pane(pin);
        mark.getStyleClass().add(STYLE_CLASS);
        mark.setMinSize(SIZE, SIZE);
        mark.setPrefSize(SIZE, SIZE);
        mark.setMaxSize(SIZE, SIZE);
        mark.setAccessibleText("Marked");
        return mark;
    }
}
