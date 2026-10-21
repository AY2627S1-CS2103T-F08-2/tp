package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Labeled;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MenuBarHoverTest {

    private static final PseudoClass HOVERED = PseudoClass.getPseudoClass("hovered");

    private final List<KeyCode> keysReceived = new ArrayList<>();
    private MenuBar menuBar;
    private Button focusedNode;
    private Stage stage;

    /**
     * MenuBar is a JavaFX control, so its skin, which adds the key filter to the scene, needs the toolkit.
     */
    @BeforeAll
    static void initJavaFxToolkit() {
        try {
            Platform.startup(() -> { });
        } catch (IllegalStateException alreadyRunning) {
            // Toolkit was already initialized by another test class
        }
        // Otherwise the toolkit stops when a test hides the last window, and the next test cannot run
        Platform.setImplicitExit(false);
    }

    @BeforeEach
    public void setUp() throws Exception {
        onFxThread(() -> {
            menuBar = new MenuBar(new Menu("File"), new Menu("Help"));
            focusedNode = new Button("Patients");
            focusedNode.addEventFilter(KeyEvent.KEY_PRESSED, event -> keysReceived.add(event.getCode()));
            // JavaFX selects a hovered menu only in a focused window, so the test needs one. It is transparent,
            // so that it does not flash up while the tests run.
            stage = new Stage();
            stage.setOpacity(0);
            stage.setScene(new Scene(new VBox(menuBar, focusedNode)));
            stage.show();
            stage.requestFocus();
            focusedNode.requestFocus();
            MainWindow.ignoreHoverOfClosedMenus(menuBar);
        });
    }

    @AfterEach
    public void tearDown() throws Exception {
        onFxThread(() -> stage.hide());
    }

    @Test
    public void hoverOverClosedMenu_leftAndRightReachFocusedNode() throws Exception {
        assumeTrue(waitForFocusedWindow(), "the display gave the test window no focus");
        onFxThread(() -> {
            Node help = menuButton("Help");
            fireMouse(help, MouseEvent.MOUSE_ENTERED);
            pressKey(KeyCode.RIGHT);
            pressKey(KeyCode.LEFT);
            fireMouse(help, MouseEvent.MOUSE_EXITED);
            pressKey(KeyCode.RIGHT);
        });

        assertEquals(List.of(KeyCode.RIGHT, KeyCode.LEFT, KeyCode.RIGHT), keysReceived);
    }

    @Test
    public void hoverOverClosedMenu_marksOnlyThatMenuAsHovered() throws Exception {
        onFxThread(() -> {
            Node file = menuButton("File");
            Node help = menuButton("Help");

            fireMouse(help, MouseEvent.MOUSE_ENTERED);
            assertTrue(help.getPseudoClassStates().contains(HOVERED));
            assertFalse(file.getPseudoClassStates().contains(HOVERED));

            fireMouse(help, MouseEvent.MOUSE_EXITED);
            assertFalse(help.getPseudoClassStates().contains(HOVERED));
        });
    }

    private Node menuButton(String text) {
        return menuBar.lookupAll(".menu").stream()
                .filter(node -> node instanceof Labeled labeled && text.equals(labeled.getText()))
                .findFirst()
                .orElseThrow();
    }

    private void pressKey(KeyCode code) {
        Event.fireEvent(focusedNode, new KeyEvent(KeyEvent.KEY_PRESSED, "", "", code, false, false, false, false));
    }

    private static void fireMouse(Node target, EventType<MouseEvent> type) {
        Event.fireEvent(target, new MouseEvent(type, 5, 5, 5, 5, MouseButton.NONE, 0,
                false, false, false, false, false, false, false, false, false, false, null));
    }

    /**
     * Returns true once the test window has the focus, or false if it still has none after a few seconds. The
     * platform gives the focus some time after the window is shown.
     */
    private boolean waitForFocusedWindow() throws Exception {
        for (int i = 0; i < 50; i++) {
            boolean[] isFocused = new boolean[1];
            onFxThread(() -> isFocused[0] = stage.isFocused());
            if (isFocused[0]) {
                return true;
            }
            Thread.sleep(100);
        }
        return false;
    }

    /**
     * Runs {@code action} on the JavaFX thread, where the menu bar's skin must be created, and waits for it.
     * A failed assertion in {@code action} fails the test.
     */
    private static void onFxThread(Runnable action) throws Exception {
        CompletableFuture<Void> done = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                action.run();
                done.complete(null);
            } catch (Throwable throwable) {
                done.completeExceptionally(throwable);
            }
        });
        try {
            done.get(10, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof Error error) {
                throw error;
            }
            throw e;
        }
    }
}
