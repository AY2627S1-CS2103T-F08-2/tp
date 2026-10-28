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
import javafx.scene.shape.Shape;
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
                assertEquals(CLASH_COLOR, ((Shape) root.lookup(".clash-mark-part")).getFill());
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
            // the time is red with a red boxed "!" before it, and the day heading keeps its colour
            assertEquals(CLASH_COLOR, label(root, ".appointment-entry-time").getTextFill());
            assertEquals(CLASH_COLOR, ((Shape) root.lookup(".clash-mark-part")).getFill());
            assertTrue(root.lookupAll(".appointment-day-clash").isEmpty());
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
            assertTrue(root.lookupAll(".clash-mark").isEmpty());
            assertNotEquals(CLASH_COLOR, label(root, ".appointment-entry-time").getTextFill());
        });
    }

    private static Patient patient(String id, String dateTime) {
        return new PatientBuilder().withId(id).withAppointments(dateTime).build();
    }

    @Test
    public void completion_bothStatuses_visibleInRowsAndNarrowDetails() throws Exception {
        onFxThread(() -> {
            for (boolean done : List.of(false, true)) {
                Appointment appointment = new Appointment(LocalDate.now().minusDays(1).toString())
                        .withNumber(1).withDone(done);
                Patient patient = new PatientBuilder().withSchedule(new AppointmentSchedule(List.of(appointment)))
                        .build();
                AppointmentRow row = new AppointmentRow(new AppointmentPage.Entry(1, entry(patient)),
                        LocalDateTime.now());
                StackPane rowRoot = new StackPane(row.getRoot());
                styleAndLayout(rowRoot, 500);
                assertEquals(done ? "Done" : "Undone", label(rowRoot, ".appointment-entry-status").getText());
                assertEquals(AppointmentFormatter.NO_TIME, label(rowRoot, ".appointment-entry-no-time").getText());

                ModelManager model = model(patient);
                PatientDetailPanel details = new PatientDetailPanel(model.getLaiNurse().getPatientList(),
                        model.getAppointmentList(), "Choose a patient");
                details.show(patient.getId(), Optional.of(appointment));
                StackPane detailRoot = new StackPane(details.getRoot());
                styleAndLayout(detailRoot, 320);
                Label status = label(detailRoot, ".detail-appointment-status");
                assertEquals(done ? "Done" : "Undone", status.getText());
                assertTrue(status.isVisible());
                assertTrue(status.isWrapText());
                assertTrue(status.getBoundsInLocal().getWidth() >= status.prefWidth(-1));
            }
        });
    }

    @Test
    public void completion_hiddenOtherPatient_refreshesClashesAndLabelsInBothDirections() throws Exception {
        onFxThread(() -> {
            LocalDate today = LocalDate.now();
            Patient alex = patient("S1234567A", today + " 09:30");
            Patient ben = patient("S2345678B", today + " 09:30");
            ModelManager model = model(alex, ben);
            AppointmentListPanel list = new AppointmentListPanel(model.getAppointmentList(), ignored -> { }, () -> { });
            list.showRange(new AppointmentRange(today, today, alex.getId()));
            PatientDetailPanel details = new PatientDetailPanel(model.getLaiNurse().getPatientList(),
                    model.getAppointmentList(), "Choose a patient");
            details.show(alex.getId(), Optional.of(entry(alex).getAppointment()));
            StackPane listRoot = new StackPane(list.getRoot());
            StackPane detailRoot = new StackPane(details.getRoot());
            styleAndLayout(listRoot, 800);
            styleAndLayout(detailRoot, 320);
            assertEquals(CLASH_COLOR, label(listRoot, ".appointment-entry-time").getTextFill());

            Patient doneBen = withDone(ben, 1, true);
            model.setPatient(ben, doneBen);
            styleAndLayout(listRoot, 800);
            styleAndLayout(detailRoot, 320);
            assertTrue(listRoot.lookupAll(".clash-mark").isEmpty());
            assertNotEquals(CLASH_COLOR, label(listRoot, ".appointment-entry-time").getTextFill());
            assertTrue(detailRoot.lookupAll(".detail-appointment-clash").isEmpty());
            assertTrue(detailRoot.lookupAll(".clash-mark").isEmpty());

            model.setPatient(doneBen, ben);
            styleAndLayout(listRoot, 800);
            styleAndLayout(detailRoot, 320);
            assertEquals(CLASH_COLOR, label(listRoot, ".appointment-entry-time").getTextFill());
            assertFalse(detailRoot.lookupAll(".detail-appointment-clash").isEmpty());

            model.setPatient(alex, withDone(alex, 1, true));
            styleAndLayout(listRoot, 800);
            styleAndLayout(detailRoot, 320);
            assertEquals("Done", label(listRoot, ".appointment-entry-status").getText());
            assertEquals("Done", label(detailRoot, ".detail-appointment-status").getText());
            assertTrue(listRoot.lookupAll(".appointment-entry-clash").isEmpty());
            assertTrue(detailRoot.lookupAll(".detail-appointment-clash").isEmpty());
        });
    }

    @Test
    public void completion_patientNextAppointment_refreshesAndHidesWhenAllDone() throws Exception {
        onFxThread(() -> {
            LocalDate tomorrow = LocalDate.now().plusDays(1);
            Patient patient = new PatientBuilder().withAppointments(tomorrow.toString(),
                    tomorrow.plusDays(1).toString()).build();
            ModelManager model = model(patient);
            PatientListPanel panel = new PatientListPanel(model.getFilteredPatientList(),
                    model.getLaiNurse().getPatientList(), ignored -> { }, () -> { });
            StackPane root = new StackPane(panel.getRoot());
            styleAndLayout(root, 800);
            assertEquals("Tomorrow", label(root, ".patient-row-appointment").getText());
            Patient firstDone = withDone(patient, 1, true);
            model.setPatient(patient, firstDone);
            styleAndLayout(root, 800);
            assertEquals(AppointmentFormatter.formatShort(firstDone.getSchedule().findByNumber(2).orElseThrow(),
                    tomorrow.minusDays(1)), label(root, ".patient-row-appointment").getText());
            Patient allDone = withDone(firstDone, 2, true);
            model.setPatient(firstDone, allDone);
            styleAndLayout(root, 800);
            assertFalse(label(root, ".patient-row-appointment").isVisible());
            model.setPatient(allDone, patient);
            styleAndLayout(root, 800);
            assertEquals("Tomorrow", label(root, ".patient-row-appointment").getText());
            assertTrue(label(root, ".patient-row-appointment").isVisible());
        });
    }

    private static Patient withDone(Patient patient, int number, boolean done) {
        Appointment appointment = patient.getSchedule().findByNumber(number).orElseThrow();
        return new PatientBuilder(patient).withSchedule(patient.getSchedule().remove(appointment)
                .add(appointment.withDone(done))).build();
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
        if (root.getScene() == null) {
            Scene scene = new Scene(root, width, 600);
            scene.getStylesheets().add(AppointmentClashUiTest.class
                    .getResource("/view/DarkTheme.css").toExternalForm());
        }
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
