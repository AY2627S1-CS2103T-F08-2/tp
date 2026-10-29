package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.control.Label;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientAppointment;
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

            row.show(withTodayAppointment, 1, List.of());
            assertEquals("1.", label(row, "#index").getText());
            assertEquals("ALEX YEOH", label(row, "#name").getText());
            Label nextAppointment = label(row, "#nextAppointment");
            assertTrue(nextAppointment.isVisible());
            assertTrue(nextAppointment.getStyleClass().contains("patient-row-appointment-today"));

            row.show(withoutAppointment, 2, List.of());
            assertEquals("2.", label(row, "#index").getText());
            assertEquals("BERNICE YU", label(row, "#name").getText());
            assertEquals("S2345678B", label(row, "#patientId").getText());
            assertFalse(nextAppointment.isVisible());
            assertFalse(nextAppointment.isManaged());
            assertFalse(nextAppointment.getStyleClass().contains("patient-row-appointment-today"));

            // a later appointment is shown again, without today's highlight
            row.show(new PatientBuilder().withAppointments(LocalDate.now().plusDays(2) + " 10:00").build(), 3,
                    List.of());
            assertTrue(nextAppointment.isVisible());
            assertTrue(nextAppointment.isManaged());
            assertFalse(nextAppointment.getStyleClass().contains("patient-row-appointment-today"));
        });
    }

    @Test
    public void show_clashingNextAppointment_redEvenToday() throws Exception {
        onFxThread(() -> {
            String today = LocalDate.now() + " 23:59";
            Patient alex = new PatientBuilder().withId("S1234567A").withAppointments(today).build();
            Patient bernice = new PatientBuilder().withId("S2345678B").withAppointments(today).build();
            List<PatientAppointment> appointments = List.of(entry(alex), entry(bernice));
            PatientRow row = new PatientRow();
            Label nextAppointment = label(row, "#nextAppointment");

            row.show(alex, 1, appointments);
            assertTrue(nextAppointment.getStyleClass().contains("patient-row-appointment-clash"));
            assertTrue(nextAppointment.getStyleClass().contains("patient-row-appointment-today"));

            // without Bernice's appointment, Alex's no longer clashes
            row.show(alex, 1, List.of(entry(alex)));
            assertFalse(nextAppointment.getStyleClass().contains("patient-row-appointment-clash"));
        });
    }

    @Test
    public void show_archivedPatient_greyedOutUntilAnotherPatientShows() throws Exception {
        onFxThread(() -> {
            Patient archived = new PatientBuilder().withId("S1234567A").withArchived(true).build();
            PatientRow row = new PatientRow();

            row.show(archived, 1, List.of());
            assertTrue(row.getRoot().getStyleClass().contains("patient-row-archived"));

            // a reused row shows the next patient as it is
            row.show(new PatientBuilder().withId("S2345678B").build(), 2, List.of());
            assertFalse(row.getRoot().getStyleClass().contains("patient-row-archived"));
        });
    }

    private static PatientAppointment entry(Patient patient) {
        return new PatientAppointment(patient, patient.getSchedule().asList().getFirst());
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
