package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import seedu.address.model.LaiNurse;
import seedu.address.model.ModelManager;
import seedu.address.model.PatientScope;
import seedu.address.model.UserPrefs;
import seedu.address.model.patient.Patient;
import seedu.address.testutil.PatientBuilder;

public class ListHeadersTest {

    private static final double WIDTH = 480;

    /** Where the header counts end in this test, the same in both lists, with and without a scroll bar. */
    private final List<Double> countEnds = new ArrayList<>();

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
    public void patientList_countEndsWithNextAppointments_withAndWithoutScrollBar() throws Exception {
        for (int count : List.of(2, 40)) {
            onFxThread(() -> {
                ObservableList<Patient> patients = FXCollections.observableArrayList(patients(count));
                PatientListPanel panel = new PatientListPanel(patients, patients, FXCollections.observableArrayList(),
                        new SimpleObjectProperty<>(PatientScope.DEFAULT), new SimpleBooleanProperty(false),
                        id -> { }, () -> { });
                StackPane root = new StackPane(panel.getRoot());
                styleAndLayout(root);
                assertEquals(endX(root, ".patient-row-appointment"), endX(root, ".list-header-count"), 0.5);
                countEnds.add(endX(root, ".list-header-count"));
            });
        }
    }

    @Test
    public void appointmentList_countEndsWithDayCounts_withAndWithoutScrollBar() throws Exception {
        for (int count : List.of(1, 40)) {
            onFxThread(() -> {
                ModelManager model = new ModelManager(new LaiNurse(), new UserPrefs());
                patients(count).forEach(model::addPatient);
                Runnable onUpAtTop = () -> { };
                AppointmentListPanel panel = new AppointmentListPanel(model.getAppointmentList(), entry -> { },
                        onUpAtTop);
                StackPane root = new StackPane(panel.getRoot());
                styleAndLayout(root);
                assertEquals(endX(root, ".appointment-day-count"), endX(root, ".list-header-count"), 0.5);
                countEnds.add(endX(root, ".list-header-count"));
            });
        }
    }

    @Test
    public void bothLists_countsEndInTheSamePlace_withAndWithoutScrollBar() throws Exception {
        patientList_countEndsWithNextAppointments_withAndWithoutScrollBar();
        appointmentList_countEndsWithDayCounts_withAndWithoutScrollBar();
        assertEquals(4, countEnds.size());
        for (double end : countEnds) {
            assertEquals(countEnds.get(0), end, 0.5);
        }
    }

    /** Returns {@code count} patients with an appointment today, so that every list row shows something at its end. */
    private static List<Patient> patients(int count) {
        List<Patient> patients = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            patients.add(new PatientBuilder().withId(String.format("S%07dA", i)).withName("Patient " + i)
                    .withAppointments(LocalDate.now() + " 23:59").build());
        }
        return patients;
    }

    private static double endX(Parent root, String selector) {
        Node node = root.lookup(selector);
        return node.localToScene(node.getLayoutBounds()).getMaxX();
    }

    private static void styleAndLayout(StackPane root) {
        Scene scene = new Scene(root, WIDTH, 400);
        scene.getStylesheets().add(ListHeadersTest.class.getResource("/view/DarkTheme.css").toExternalForm());
        // Virtualized list rows, and the scroll bar, appear during layout and need their own CSS pass
        for (int i = 0; i < 3; i++) {
            root.applyCss();
            root.layout();
        }
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
