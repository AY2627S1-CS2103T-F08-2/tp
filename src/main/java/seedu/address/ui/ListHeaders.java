package seedu.address.ui;

import java.util.Optional;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.Region;

/**
 * Lines up the end of a list's header with the end of its rows, at the same place in every list, whether or not the
 * list shows a vertical scroll bar. So a count at the end of the header, such as "6", ends where the rows'
 * last column ends, and where the other list's count ends, so it stays in place as the user switches views.
 */
final class ListHeaders {

    private ListHeaders() {}

    /**
     * Keeps the end padding of {@code header} equal to the inset of {@code list}, plus the width of its vertical
     * scroll bar, plus {@code rowEndPadding}, the end padding of a row. Returns the room that each row must leave
     * empty at its end, with {@link #cellPadding}, so that its content ends there too: none while the scroll bar
     * shows, and the width of the scroll bar while it does not.
     */
    static ReadOnlyDoubleProperty alignEndWithRows(Region header, ListView<?> list, double rowEndPadding) {
        DoubleProperty rowGutter = new SimpleDoubleProperty();
        Runnable update = () -> {
            Optional<ScrollBar> scrollBar = findVerticalScrollBar(list);
            double scrollBarWidth = scrollBar.map(bar -> bar.prefWidth(-1)).orElse(0.0);
            boolean isScrollBarShown = scrollBar.map(Node::isVisible).orElse(false);
            rowGutter.set(isScrollBarShown ? 0 : scrollBarWidth);
            Insets padding = header.getPadding();
            header.setPadding(new Insets(padding.getTop(), list.snappedRightInset() + scrollBarWidth + rowEndPadding,
                    padding.getBottom(), padding.getLeft()));
        };
        list.insetsProperty().addListener((unused, oldInsets, insets) -> update.run());
        // The scroll bar exists only once the list has its skin
        list.skinProperty().addListener((unused, oldSkin, skin) -> watchScrollBar(list, update));
        watchScrollBar(list, update);
        update.run();
        return rowGutter;
    }

    /**
     * Returns the padding of a list cell that leaves {@code rowGutter} empty at the end of its row. Bound to a cell's
     * padding, it takes the place of the padding in the style sheet.
     */
    static ObjectBinding<Insets> cellPadding(ReadOnlyDoubleProperty rowGutter) {
        return Bindings.createObjectBinding(() -> new Insets(0, rowGutter.get(), 0, 0), rowGutter);
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
