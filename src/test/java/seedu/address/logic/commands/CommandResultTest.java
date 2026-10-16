package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPatients.BENSON;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.CommandResult.View;
import seedu.address.model.patient.PatientAppointment;
import seedu.address.model.patient.PatientId;

public class CommandResultTest {

    private static final LocalDate DATE = LocalDate.of(2026, 10, 5);
    private static final PatientAppointment BENSON_APPOINTMENT =
            new PatientAppointment(BENSON, BENSON.getSchedule().asList().get(0));

    @Test
    public void equals() {
        CommandResult commandResult = new CommandResult("feedback");

        // same values -> returns true
        assertTrue(commandResult.equals(new CommandResult("feedback")));
        assertTrue(commandResult.equals(new CommandResult("feedback", false, false)));

        // same object -> returns true
        assertTrue(commandResult.equals(commandResult));

        // null -> returns false
        assertFalse(commandResult.equals(null));

        // different types -> returns false
        assertFalse(commandResult.equals(0.5f));

        // different feedbackToUser value -> returns false
        assertFalse(commandResult.equals(new CommandResult("different")));

        // different showHelp value -> returns false
        assertFalse(commandResult.equals(new CommandResult("feedback", true, false)));

        // different exit value -> returns false
        assertFalse(commandResult.equals(new CommandResult("feedback", false, true)));

        // patient to view only in one -> returns false
        assertFalse(commandResult.equals(new CommandResult("feedback", new PatientId("S1234567A"))));

        // same patient to view -> returns true
        CommandResult viewResult = new CommandResult("feedback", new PatientId("S1234567A"));
        assertTrue(viewResult.equals(new CommandResult("feedback", new PatientId("S1234567A"))));

        // different patient to view -> returns false
        assertFalse(viewResult.equals(new CommandResult("feedback", new PatientId("S2345678B"))));
    }

    @Test
    public void hashcode() {
        CommandResult commandResult = new CommandResult("feedback");

        // same values -> returns same hashcode
        assertEquals(commandResult.hashCode(), new CommandResult("feedback").hashCode());

        // different feedbackToUser value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("different").hashCode());

        // different showHelp value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("feedback", true, false).hashCode());

        // different exit value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("feedback", false, true).hashCode());

        // same patient to view -> returns same hashcode
        assertEquals(new CommandResult("feedback", new PatientId("S1234567A")).hashCode(),
                new CommandResult("feedback", new PatientId("S1234567A")).hashCode());

        // different patient to view -> returns different hashcode
        assertNotEquals(new CommandResult("feedback", new PatientId("S1234567A")).hashCode(),
                new CommandResult("feedback", new PatientId("S2345678B")).hashCode());
    }

    @Test
    public void getPatientToView() {
        assertEquals(Optional.empty(), new CommandResult("feedback").getPatientToView());
        assertEquals(Optional.empty(), new CommandResult("feedback", true, false).getPatientToView());
        assertEquals(Optional.of(new PatientId("S1234567A")),
                new CommandResult("feedback", new PatientId("S1234567A")).getPatientToView());
    }

    @Test
    public void constructor_nullPatientToView_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new CommandResult("feedback", (PatientId) null));
    }

    @Test
    public void withView_setsViewAndKeepsOtherFields() {
        CommandResult viewResult = new CommandResult("feedback", new PatientId("S1234567A"));
        CommandResult withView = viewResult.withView(View.PATIENTS);

        assertEquals(Optional.of(View.PATIENTS), withView.getView());
        assertEquals(viewResult.getPatientToView(), withView.getPatientToView());
        assertEquals(viewResult.getFeedbackToUser(), withView.getFeedbackToUser());
        // the original is unchanged
        assertEquals(Optional.empty(), viewResult.getView());

        assertFalse(viewResult.equals(withView));
        assertFalse(withView.equals(viewResult.withView(View.APPOINTMENTS)));
        assertTrue(withView.equals(viewResult.withView(View.PATIENTS)));
        assertEquals(withView.hashCode(), viewResult.withView(View.PATIENTS).hashCode());
        assertThrows(NullPointerException.class, () -> viewResult.withView(null));
    }

    @Test
    public void appointmentToShow_dateAndOptionalSelection() {
        CommandResult dateOnly = new CommandResult("feedback", DATE, Optional.empty());
        assertEquals(Optional.of(DATE), dateOnly.getAppointmentDateToShow());
        assertEquals(Optional.empty(), dateOnly.getAppointmentToSelect());

        CommandResult withSelection = new CommandResult("feedback", DATE, Optional.of(BENSON_APPOINTMENT));
        assertEquals(Optional.of(BENSON_APPOINTMENT), withSelection.getAppointmentToSelect());

        // a plain result shows no date
        assertEquals(Optional.empty(), new CommandResult("feedback").getAppointmentDateToShow());
        assertEquals(Optional.empty(), new CommandResult("feedback").getAppointmentToSelect());

        assertTrue(dateOnly.equals(new CommandResult("feedback", DATE, Optional.empty())));
        assertFalse(dateOnly.equals(new CommandResult("feedback")));
        assertFalse(dateOnly.equals(new CommandResult("feedback", DATE.plusDays(1), Optional.empty())));
        assertFalse(dateOnly.equals(withSelection));
        assertNotEquals(dateOnly.hashCode(), withSelection.hashCode());
    }

    @Test
    public void constructor_nullAppointmentToShow_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new CommandResult("feedback", null, Optional.empty()));
        assertThrows(NullPointerException.class, () -> new CommandResult("feedback", DATE, null));
    }

    @Test
    public void toStringMethod() {
        CommandResult commandResult = new CommandResult("feedback");
        String expected = CommandResult.class.getCanonicalName()
                + "{feedbackToUser=feedback, showHelp=false, exit=false, patientToView=null, view=null, "
                + "appointmentDateToShow=null, appointmentToSelect=null}";
        assertEquals(expected, commandResult.toString());

        CommandResult viewResult = new CommandResult("feedback", new PatientId("S1234567A")).withView(View.PATIENTS);
        String expectedView = CommandResult.class.getCanonicalName()
                + "{feedbackToUser=feedback, showHelp=false, exit=false, patientToView=S1234567A, view=PATIENTS, "
                + "appointmentDateToShow=null, appointmentToSelect=null}";
        assertEquals(expectedView, viewResult.toString());

        CommandResult appointmentResult = new CommandResult("feedback", DATE, Optional.of(BENSON_APPOINTMENT));
        String expectedAppointment = CommandResult.class.getCanonicalName()
                + "{feedbackToUser=feedback, showHelp=false, exit=false, patientToView=null, view=null, "
                + "appointmentDateToShow=" + DATE + ", appointmentToSelect=" + BENSON_APPOINTMENT + "}";
        assertEquals(expectedAppointment, appointmentResult.toString());
    }
}
