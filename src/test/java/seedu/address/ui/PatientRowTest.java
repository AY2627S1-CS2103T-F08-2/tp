package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.control.Label;
import seedu.address.model.patient.Patient;
import seedu.address.testutil.PatientBuilder;

public class PatientRowTest {

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
    public void show_anotherPatient_replacesWhatTheRowShowed() throws Exception {
        onFxThread(() -> {
            Patient withTodayAppointment = new PatientBuilder().withId("S1234567A").withName("Alex Yeoh")
                    .withAppointments(LocalDate.now() + " 23:59").build();
            Patient withoutAppointment = new PatientBuilder().withId("S2345678B").withName("Bernice Yu").build();
            PatientRow row = new PatientRow();

            row.show(withTodayAppointment, 1);
            assertEquals("1.", label(row, "#index").getText());
            assertEquals("ALEX YEOH", label(row, "#name").getText());
            Label nextAppointment = label(row, "#nextAppointment");
            assertTrue(nextAppointment.isVisible());
            assertTrue(nextAppointment.getStyleClass().contains("patient-row-appointment-today"));

            row.show(withoutAppointment, 2);
            assertEquals("2.", label(row, "#index").getText());
            assertEquals("BERNICE YU", label(row, "#name").getText());
            assertEquals("S2345678B", label(row, "#patientId").getText());
            assertFalse(nextAppointment.isVisible());
            assertFalse(nextAppointment.isManaged());
            assertFalse(nextAppointment.getStyleClass().contains("patient-row-appointment-today"));

            // a later appointment is shown again, without today's highlight
            row.show(new PatientBuilder().withAppointments(LocalDate.now().plusDays(2) + " 10:00").build(), 3);
            assertTrue(nextAppointment.isVisible());
            assertTrue(nextAppointment.isManaged());
            assertFalse(nextAppointment.getStyleClass().contains("patient-row-appointment-today"));
        });
    }

    private static Label label(PatientRow row, String selector) {
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
