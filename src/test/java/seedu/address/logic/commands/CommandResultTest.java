package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPatients.AMY;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.CommandResult.AppointmentRange;
import seedu.address.logic.commands.CommandResult.Navigation;
import seedu.address.logic.commands.CommandResult.View;
import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientAppointment;
import seedu.address.model.patient.PatientId;
import seedu.address.testutil.PatientBuilder;

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
    public void navigationFactories_selectTheirEntry() {
        PatientId id = new PatientId("S1234567A");
        assertEquals(new Navigation(View.PATIENTS, Optional.empty(), Optional.empty()),
                Navigation.toView(View.PATIENTS));
        assertEquals(new Navigation(View.PATIENTS, Optional.of(id), Optional.empty()), Navigation.toPatient(id));
        PatientAppointment entry = appointmentOf("2026-10-06");
        assertEquals(new Navigation(View.APPOINTMENTS, Optional.empty(), Optional.of(entry)),
                Navigation.toAppointment(entry));
    }

    @Test
    public void navigationConstructor_entryOutsideItsView_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Navigation(View.APPOINTMENTS,
                Optional.of(new PatientId("S1234567A")), Optional.empty()));
        assertThrows(IllegalArgumentException.class, () -> new Navigation(View.PATIENTS,
                Optional.empty(), Optional.of(appointmentOf("2026-10-06"))));
        assertThrows(NullPointerException.class, () -> Navigation.toView(null));
        assertThrows(NullPointerException.class, () -> Navigation.toPatient(null));
        assertThrows(NullPointerException.class, () -> Navigation.toAppointment(null));
    }

    @Test
    public void withNavigation_setsNavigationAndView() {
        CommandResult rangeResult = new CommandResult("feedback", RANGE);
        Navigation navigation = Navigation.toAppointment(appointmentOf("2026-10-06"));
        CommandResult navigated = rangeResult.withNavigation(navigation);

        assertEquals(Optional.of(navigation), navigated.getNavigation());
        assertEquals(Optional.of(View.APPOINTMENTS), navigated.getView());
        assertEquals(Optional.of(RANGE), navigated.getAppointmentRange());
        // the original is unchanged
        assertEquals(Optional.empty(), rangeResult.getNavigation());
        assertEquals(Optional.empty(), new CommandResult("feedback").getNavigation());

        assertTrue(navigated.equals(rangeResult.withNavigation(
                Navigation.toAppointment(appointmentOf("2026-10-06")))));
        assertEquals(navigated.hashCode(), rangeResult.withNavigation(navigation).hashCode());
        assertFalse(navigated.equals(rangeResult.withView(View.APPOINTMENTS)));
        assertFalse(navigated.equals(rangeResult.withNavigation(Navigation.toView(View.APPOINTMENTS))));
        assertThrows(NullPointerException.class, () -> rangeResult.withNavigation(null));
    }

    @Test
    public void withViewAndNavigation_sameView_success() {
        CommandResult result = new CommandResult("feedback");
        Navigation navigation = Navigation.toView(View.PATIENTS);
        assertEquals(result.withNavigation(navigation), result.withNavigation(navigation).withView(View.PATIENTS));
        assertEquals(result.withNavigation(navigation), result.withView(View.PATIENTS).withNavigation(navigation));
    }

    @Test
    public void withViewAndNavigation_differentViews_throwsIllegalArgumentException() {
        CommandResult result = new CommandResult("feedback");
        assertThrows(IllegalArgumentException.class, () -> result
                .withNavigation(Navigation.toView(View.PATIENTS)).withView(View.APPOINTMENTS));
        assertThrows(IllegalArgumentException.class, () -> result
                .withView(View.APPOINTMENTS).withNavigation(Navigation.toView(View.PATIENTS)));
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
        assertThrows(NullPointerException.class, () -> RANGE.contains((LocalDate) null));
    }

    @Test
    public void appointmentRangeContainsEntry_checksDateAndPatient() {
        PatientAppointment amyInRange = appointmentOf("2026-10-06");

        // a range of every patient's appointments checks only the date
        assertTrue(RANGE.contains(amyInRange));
        assertFalse(RANGE.contains(appointmentOf("2026-11-06")));

        // a range of one patient's appointments also checks the patient
        assertTrue(new AppointmentRange(START, END, AMY.getId()).contains(amyInRange));
        assertFalse(new AppointmentRange(START, END, new PatientId("S2222222B")).contains(amyInRange));
        assertThrows(NullPointerException.class, () -> RANGE.contains((PatientAppointment) null));
    }

    @Test
    public void appointmentRangeConstructor_noPatient_rangeOfEveryPatient() {
        assertEquals(new AppointmentRange(START, END, null), RANGE);
        assertNull(RANGE.patientId());
        assertFalse(new AppointmentRange(START, END, AMY.getId()).equals(RANGE));
    }

    @Test
    public void appointmentRangeConstructor_invalid_throws() {
        assertThrows(NullPointerException.class, () -> new AppointmentRange(null, END));
        assertThrows(NullPointerException.class, () -> new AppointmentRange(START, null));
        assertThrows(IllegalArgumentException.class, () -> new AppointmentRange(END, START));
        assertThrows(IllegalArgumentException.class, () -> new AppointmentRange(END, START, AMY.getId()));
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
                + "appointmentRange=null, navigation=null}";
        assertEquals(expected, commandResult.toString());

        CommandResult viewResult = new CommandResult("feedback", new PatientId("S1234567A")).withView(View.PATIENTS);
        String expectedView = CommandResult.class.getCanonicalName()
                + "{feedbackToUser=feedback, showHelp=false, exit=false, patientToView=S1234567A, view=PATIENTS, "
                + "appointmentRange=null, navigation=null}";
        assertEquals(expectedView, viewResult.toString());

        CommandResult appointmentResult = new CommandResult("feedback", RANGE);
        String expectedAppointment = CommandResult.class.getCanonicalName()
                + "{feedbackToUser=feedback, showHelp=false, exit=false, patientToView=null, view=null, "
                + "appointmentRange=" + RANGE + ", navigation=null}";
        assertEquals(expectedAppointment, appointmentResult.toString());

        Navigation navigation = Navigation.toView(View.PATIENTS);
        CommandResult navigationResult = new CommandResult("feedback").withNavigation(navigation);
        String expectedNavigation = CommandResult.class.getCanonicalName()
                + "{feedbackToUser=feedback, showHelp=false, exit=false, patientToView=null, view=PATIENTS, "
                + "appointmentRange=null, navigation=" + navigation + "}";
        assertEquals(expectedNavigation, navigationResult.toString());
    }

    private static PatientAppointment appointmentOf(String appointment) {
        Patient patient = new PatientBuilder(AMY).withAppointments(appointment).build();
        return new PatientAppointment(patient, new Appointment(appointment));
    }
}
