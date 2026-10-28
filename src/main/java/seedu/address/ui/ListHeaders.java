package seedu.address.ui;

import java.util.Optional;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.Region;

/**
 * Lines up the end of a list's header with the end of its rows, so that a count at the end of the header, such as
 * "6 patients", ends where the rows' last column ends. Both lists use it, so their headers line up in the same way.
 */
final class ListHeaders {

    private ListHeaders() {}

    /**
     * Keeps the end padding of {@code header} equal to where the content of each row of {@code list} ends: the
     * list's own inset, the width of its vertical scroll bar while it shows, and {@code rowEndPadding}, the end
     * padding of a row. The header has no scroll bar, so without this its end is one scroll bar further out.
     */
    static void alignEndWithRows(Region header, ListView<?> list, double rowEndPadding) {
        Runnable update = () -> {
            double scrollBarWidth = findVerticalScrollBar(list).filter(Node::isVisible)
                    .map(ScrollBar::getWidth).orElse(0.0);
            Insets padding = header.getPadding();
            header.setPadding(new Insets(padding.getTop(), list.snappedRightInset() + scrollBarWidth + rowEndPadding,
                    padding.getBottom(), padding.getLeft()));
        };
        list.insetsProperty().addListener((unused, oldInsets, insets) -> update.run());
        // The scroll bar exists only once the list has its skin
        list.skinProperty().addListener((unused, oldSkin, skin) -> watchScrollBar(list, update));
        watchScrollBar(list, update);
        update.run();
    }

    private static void watchScrollBar(ListView<?> list, Runnable update) {
        findVerticalScrollBar(list).ifPresent(scrollBar -> {
            scrollBar.visibleProperty().addListener((unused, wasVisible, isVisible) -> update.run());
            scrollBar.widthProperty().addListener((unused, oldWidth, width) -> update.run());
            update.run();
        });
    }

    private static Optional<ScrollBar> findVerticalScrollBar(ListView<?> list) {
        return list.lookupAll(".scroll-bar").stream()
                .filter(ScrollBar.class::isInstance)
                .map(ScrollBar.class::cast)
                .filter(scrollBar -> scrollBar.getOrientation() == Orientation.VERTICAL)
                .findFirst();
    }
}
