package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.CommandResult.AppointmentRange;
import seedu.address.logic.commands.CommandResult.View;
import seedu.address.model.patient.PatientId;

public class CommandResultTest {

    private static final LocalDate START = LocalDate.of(2026, 10, 1);
    private static final LocalDate END = LocalDate.of(2026, 10, 31);
    private static final AppointmentRange RANGE = new AppointmentRange(START, END);

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
    public void appointmentRange_heldAndCompared() {
        CommandResult rangeResult = new CommandResult("feedback", RANGE);
        assertEquals(Optional.of(RANGE), rangeResult.getAppointmentRange());

        // a plain result shows no range
        assertEquals(Optional.empty(), new CommandResult("feedback").getAppointmentRange());

        assertTrue(rangeResult.equals(new CommandResult("feedback", new AppointmentRange(START, END))));
        assertEquals(rangeResult.hashCode(),
                new CommandResult("feedback", new AppointmentRange(START, END)).hashCode());
        assertFalse(rangeResult.equals(new CommandResult("feedback")));
        assertFalse(rangeResult.equals(new CommandResult("feedback", new AppointmentRange(START, START))));
        assertNotEquals(rangeResult.hashCode(), new CommandResult("feedback").hashCode());
        assertThrows(NullPointerException.class, () -> new CommandResult("feedback", (AppointmentRange) null));
    }

    @Test
    public void appointmentRangeContains_boundariesIncluded() {
        assertFalse(RANGE.contains(START.minusDays(1)));
        assertTrue(RANGE.contains(START));
        assertTrue(RANGE.contains(END));
        assertFalse(RANGE.contains(END.plusDays(1)));
        // a range with no end
        assertTrue(new AppointmentRange(START, LocalDate.MAX).contains(LocalDate.of(9999, 12, 31)));
        assertThrows(NullPointerException.class, () -> RANGE.contains(null));
    }

    @Test
    public void appointmentRangeConstructor_invalid_throws() {
        assertThrows(NullPointerException.class, () -> new AppointmentRange(null, END));
        assertThrows(NullPointerException.class, () -> new AppointmentRange(START, null));
        assertThrows(IllegalArgumentException.class, () -> new AppointmentRange(END, START));
    }

    @Test
    public void appointmentRange_withPatient() {
        AppointmentRange patientRange = new AppointmentRange(START, END, new PatientId("S1234567A"));

        // The two-arg range is the same as a three-arg range with no patient
        assertEquals(new AppointmentRange(START, END, null), RANGE);
        assertEquals(patientRange, new AppointmentRange(START, END, new PatientId("S1234567A")));
        assertFalse(patientRange.equals(RANGE));
        assertFalse(patientRange.equals(new AppointmentRange(START, END, new PatientId("S2345678B"))));
        assertEquals(new PatientId("S1234567A"), patientRange.patientId());
    }

    @Test
    public void toStringMethod() {
        CommandResult commandResult = new CommandResult("feedback");
        String expected = CommandResult.class.getCanonicalName()
                + "{feedbackToUser=feedback, showHelp=false, exit=false, patientToView=null, view=null, "
                + "appointmentRange=null}";
        assertEquals(expected, commandResult.toString());

        CommandResult viewResult = new CommandResult("feedback", new PatientId("S1234567A")).withView(View.PATIENTS);
        String expectedView = CommandResult.class.getCanonicalName()
                + "{feedbackToUser=feedback, showHelp=false, exit=false, patientToView=S1234567A, view=PATIENTS, "
                + "appointmentRange=null}";
        assertEquals(expectedView, viewResult.toString());

        CommandResult appointmentResult = new CommandResult("feedback", RANGE);
        String expectedAppointment = CommandResult.class.getCanonicalName()
                + "{feedbackToUser=feedback, showHelp=false, exit=false, patientToView=null, view=null, "
                + "appointmentRange=" + RANGE + "}";
        assertEquals(expectedAppointment, appointmentResult.toString());
    }
}
