package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.control.Label;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientAppointment;
import seedu.address.testutil.PatientBuilder;

public class AppointmentRowTest {

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
    public void show_anotherAppointment_replacesWhatTheRowShowed() throws Exception {
        onFxThread(() -> {
            Patient alex = new PatientBuilder().withId("S1234567A").withName("Alex Yeoh")
                    .withAppointments(LocalDate.now().minusDays(1) + " 09:30").build();
            Patient bernice = new PatientBuilder().withId("S2345678B").withName("Bernice Yu")
                    .withAppointments(LocalDate.now().plusDays(1).toString()).build();
            AppointmentRow row = new AppointmentRow();

            // a passed appointment with a time, which clashes
            row.show(new AppointmentPage.Entry(1, entry(alex)), LocalDateTime.now(), true);
            assertEquals("1.", label(row, "#number").getText());
            assertEquals("ALEX YEOH", label(row, "#name").getText());
            assertTrue(label(row, "#time").getStyleClass().contains("appointment-entry-time"));
            assertTrue(row.getRoot().getStyleClass().contains("appointment-entry-passed"));
            assertTrue(row.getRoot().getStyleClass().contains("appointment-entry-clash"));

            // an upcoming appointment with no time, which does not clash
            row.show(new AppointmentPage.Entry(2, entry(bernice)), LocalDateTime.now(), false);
            assertEquals("2.", label(row, "#number").getText());
            assertEquals("BERNICE YU", label(row, "#name").getText());
            // no style of its own, so that it takes the color of its row
            assertTrue(label(row, "#time").getStyleClass().contains("appointment-entry-time"));
            assertFalse(row.getRoot().getStyleClass().contains("appointment-entry-passed"));
            assertFalse(row.getRoot().getStyleClass().contains("appointment-entry-clash"));
        });
    }

    private static PatientAppointment entry(Patient patient) {
        return new PatientAppointment(patient, patient.getSchedule().asList().getFirst());
    }

    private static Label label(AppointmentRow row, String selector) {
        return (Label) row.getRoot().lookup(selector);
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
