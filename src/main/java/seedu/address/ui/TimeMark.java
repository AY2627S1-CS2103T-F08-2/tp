package seedu.address.ui;

import javafx.scene.Node;
import javafx.scene.control.Label;

/**
 * Shows the mark before the time of an appointment: a {@link ClashMark} if it clashes, or else a {@link PinMark} if
 * it is marked. A marked appointment never clashes, so an appointment never needs both.
 */
final class TimeMark {

    /** The space between a mark and the time after it, in pixels. */
    static final double GAP = 4;

    private TimeMark() {}

    /**
     * Shows {@code clashMark} before the text of {@code time} if {@code isClashing}, or else {@code pinMark} if
     * {@code isMarked}, or no mark.
     */
    static void show(Label time, Node clashMark, Node pinMark, boolean isClashing, boolean isMarked) {
        time.setGraphic(isClashing ? clashMark : isMarked ? pinMark : null);
        time.setGraphicTextGap(GAP);
    }
}
