package seedu.address.ui;

import javafx.scene.Node;
import javafx.scene.control.Label;

/**
 * Shows the mark before the time of an appointment: a {@link PinMark} if it is marked, or else a {@link ClashMark} if
 * it clashes. A marked appointment clashes only if a setting lets it (#216); its pin then shows the clash, as the
 * style sheet colors it red in a clashing row, in place of the boxed "!".
 */
final class TimeMark {

    /** The space between a mark and the time after it, in pixels. */
    static final double GAP = 4;

    private TimeMark() {}

    /**
     * Shows {@code pinMark} before the text of {@code time} if {@code isMarked}, or else {@code clashMark} if
     * {@code isClashing}, or no mark.
     */
    static void show(Label time, Node clashMark, Node pinMark, boolean isClashing, boolean isMarked) {
        time.setGraphic(isMarked ? pinMark : isClashing ? clashMark : null);
        time.setGraphicTextGap(GAP);
    }
}
