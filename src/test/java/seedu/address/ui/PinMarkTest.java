package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

public class PinMarkTest {

    private static final Color STYLE_SHEET_COLOR = Color.web("#d5dadc");

    @BeforeAll
    static void initJavaFxToolkit() {
        try {
            Platform.startup(() -> { });
        } catch (IllegalStateException alreadyRunning) {
            // Another UI test initialized the toolkit
        }
        Platform.setImplicitExit(false);
    }

    @Test
    public void create_sameSquareAsClashMark_pinInItsMiddle() throws Exception {
        onFxThread(() -> {
            Region pin = PinMark.create();
            Region clash = ClashMark.create();
            styleAndLayout(new StackPane(pin, clash));
            assertEquals(clash.getWidth(), pin.getWidth());
            assertEquals(clash.getHeight(), pin.getHeight());
            Bounds shape = pin.lookup(".pin-mark-shape").getBoundsInParent();
            // about as wide as the clash mark's box, and in the middle of the square
            assertTrue(shape.getWidth() > 13 && shape.getWidth() < 16, shape.toString());
            assertEquals(pin.getWidth() / 2, shape.getCenterX(), 0.5);
            assertEquals(pin.getHeight() / 2, shape.getCenterY(), 0.5);
        });
    }

    @Test
    public void setColor_colorThenNull_colorsThePinThenGoesBackToTheStyleSheet() throws Exception {
        onFxThread(() -> {
            Region pin = PinMark.create();
            StackPane root = new StackPane(pin);
            styleAndLayout(root);
            Shape shape = (Shape) pin.lookup(".pin-mark-shape");
            assertEquals(STYLE_SHEET_COLOR, shape.getFill());

            PinMark.setColor(pin, Color.RED);
            root.applyCss();
            assertEquals(Color.RED, shape.getFill());

            PinMark.setColor(pin, null);
            root.applyCss();
            assertEquals(STYLE_SHEET_COLOR, shape.getFill());
        });
    }

    private static void styleAndLayout(StackPane root) {
        Scene scene = new Scene(root, 100, 100);
        scene.getStylesheets().add(PinMarkTest.class.getResource("/view/DarkTheme.css").toExternalForm());
        root.applyCss();
        root.layout();
    }

    private static void onFxThread(Runnable action) throws Exception {
        CompletableFuture<Void> done = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                action.run();
                done.complete(null);
            } catch (Throwable t) {
                done.completeExceptionally(t);
            }
        });
        done.get(10, TimeUnit.SECONDS);
    }
}
