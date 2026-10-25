package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import seedu.address.logic.commands.CommandResult.AppointmentRange;
import seedu.address.model.LaiNurse;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.AppointmentSchedule;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientAppointment;
import seedu.address.testutil.PatientBuilder;

public class AppointmentClashUiTest {

    private static final Color CLASH_COLOR = Color.web("#ff8585");

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
    public void row_selectedPastClash_timeStaysRedAndNumbersAreDistinct() throws Exception {
        onFxThread(() -> {
            Patient patient = patient("S1234567A", LocalDate.now().minusDays(1) + " 09:30");
            AppointmentRow row = new AppointmentRow(new AppointmentPage.Entry(7, entry(patient)),
                    LocalDateTime.now(), true);
            ListCell<AppointmentPage.Entry> cell = new ListCell<>();
            cell.setGraphic(row.getRoot());
            cell.pseudoClassStateChanged(PseudoClass.getPseudoClass("filled"), true);
            cell.pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), true);
            StackPane root = new StackPane(cell);
            styleAndLayout(root, 800);
            assertEquals("7.", label(root, ".appointment-entry-number").getText());
            assertEquals("S1234567A #1", label(root, ".appointment-entry-id").getText());
            assertEquals(CLASH_COLOR, label(root, ".appointment-entry-time").getTextFill());
            assertNotEquals(CLASH_COLOR, label(root, ".appointment-entry-name").getTextFill());
        });
    }

    @Test
    public void details_todayAndPastClashes_redEvenWhenMarkedAndRefreshAfterOtherPatientChanges() throws Exception {
        onFxThread(() -> {
            for (String dateTime : List.of(LocalDate.now() + " 23:59", LocalDate.now().minusDays(1) + " 09:30")) {
                Patient alex = patient("S1234567A", dateTime);
                Patient ben = patient("S2345678B", dateTime);
                ModelManager model = model(alex, ben);
                PatientDetailPanel panel = new PatientDetailPanel(model.getLaiNurse().getPatientList(),
                        model.getAppointmentList(), "Choose a patient");
                panel.show(alex.getId(), Optional.of(entry(alex).getAppointment()));
                StackPane root = new StackPane(panel.getRoot());
                styleAndLayout(root, 320);
                assertTrue(label(root, ".detail-appointment-date").getText().startsWith("#1 · "));
                assertEquals(CLASH_COLOR, label(root, ".detail-appointment-date").getTextFill());
                assertEquals(CLASH_COLOR, label(root, ".detail-appointment-time").getTextFill());
                assertFalse(root.lookupAll(".detail-appointment-marked").isEmpty());
                assertTrue(label(root, ".detail-appointment-date").isWrapText());
                Patient movedBen = new PatientBuilder(ben).withAppointments(LocalDate.now().plusDays(2).toString())
                        .build();
                model.setPatient(ben, movedBen);
                root.applyCss();
                root.layout();
                assertTrue(root.lookupAll(".detail-appointment-clash").isEmpty());
                assertNotEquals(CLASH_COLOR, label(root, ".detail-appointment-time").getTextFill());
            }
        });
    }

    @Test
    public void list_filteredClash_redHeadingAndTimeAndSelectionFollowsNumberAfterMove() throws Exception {
        onFxThread(() -> {
            LocalDate today = LocalDate.now();
            Patient alex = patient("S1234567A", today + " 09:30");
            Patient ben = patient("S2345678B", today + " 09:30");
            ModelManager model = model(alex, ben);
            AtomicReference<Optional<PatientAppointment>> selected = new AtomicReference<>(Optional.empty());
            AppointmentListPanel panel = new AppointmentListPanel(model.getAppointmentList(), selected::set, () -> { });
            panel.showRange(new AppointmentRange(today, today, alex.getId()));
            StackPane root = new StackPane(panel.getRoot());
            styleAndLayout(root, 800);
            assertEquals(CLASH_COLOR, label(root, ".appointment-day-clash").getTextFill());
            assertEquals(CLASH_COLOR, label(root, ".appointment-entry-time").getTextFill());
            assertEquals("S1234567A #1", label(root, ".appointment-entry-id").getText());
            Patient moved = new PatientBuilder(alex).withSchedule(new AppointmentSchedule(List.of(
                    new Appointment(today + " 10:00").withNumber(1)))).build();
            model.setPatient(alex, moved);
            root.applyCss();
            root.layout();
            root.applyCss();
            root.layout();
            assertEquals("10:00", label(root, ".appointment-entry-time").getText());
            assertTrue(selected.get().orElseThrow().isSameEntry(entry(alex)));
            assertEquals("10:00", selected.get().orElseThrow().getAppointment().getTime().orElseThrow().toString());
            assertTrue(root.lookupAll(".appointment-day-clash").isEmpty());
            assertNotEquals(CLASH_COLOR, label(root, ".appointment-entry-time").getTextFill());
        });
    }

    private static Patient patient(String id, String dateTime) {
        return new PatientBuilder().withId(id).withAppointments(dateTime).build();
    }

    private static ModelManager model(Patient... patients) {
        LaiNurse data = new LaiNurse();
        for (Patient patient : patients) {
            data.addPatient(patient);
        }
        return new ModelManager(data, new UserPrefs());
    }

    private static PatientAppointment entry(Patient patient) {
        return new PatientAppointment(patient, patient.getSchedule().asList().getFirst());
    }

    private static Label label(Parent root, String selector) {
        return (Label) Optional.ofNullable(root.lookup(selector)).orElseThrow();
    }

    private static void styleAndLayout(StackPane root, double width) {
        Scene scene = new Scene(root, width, 600);
        scene.getStylesheets().add(AppointmentClashUiTest.class.getResource("/view/DarkTheme.css").toExternalForm());
        root.resize(width, 600);
        root.applyCss();
        root.layout();
        // Virtualized list rows are created during layout and need their own CSS pass.
        root.applyCss();
        root.layout();
    }

    private static void onFxThread(Runnable action) throws Exception {
        CompletableFuture<Void> done = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                action.run();
                done.complete(null);
            } catch (Throwable e) {
                done.completeExceptionally(e);
            }
        });
        done.get(10, TimeUnit.SECONDS);
    }
}
