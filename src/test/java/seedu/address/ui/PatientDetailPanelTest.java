package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import seedu.address.model.LaiNurse;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.patient.Patient;
import seedu.address.testutil.PatientBuilder;

public class PatientDetailPanelTest {

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
    public void show_samePatientAfterClear_startsAtTop() throws Exception {
        onFxThread(() -> {
            Patient alex = new PatientBuilder().withId("S1234567A").withName("Alex Yeoh").build();
            Patient bernice = new PatientBuilder().withId("S2345678B").withName("Bernice Yu").build();
            LaiNurse laiNurse = new LaiNurse();
            laiNurse.addPatient(alex);
            laiNurse.addPatient(bernice);
            ModelManager model = new ModelManager(laiNurse, new UserPrefs());
            PatientDetailPanel panel = new PatientDetailPanel(model.getLaiNurse().getPatientList(),
                    model.getAppointmentList(), "Choose a patient");
            ScrollPane details = (ScrollPane) panel.getRoot().lookup("#details");

            panel.show(alex.getId());
            details.setVvalue(1);
            // a change to the shown patient keeps the scroll position
            panel.refresh();
            assertEquals(1, details.getVvalue());

            // another patient starts at the top
            panel.show(bernice.getId());
            assertEquals(0, details.getVvalue());

            // the same patient shown again after the panel showed no patient, as after an empty search (#201)
            details.setVvalue(1);
            panel.clear();
            panel.show(bernice.getId());
            assertEquals(0, details.getVvalue());
        });
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
