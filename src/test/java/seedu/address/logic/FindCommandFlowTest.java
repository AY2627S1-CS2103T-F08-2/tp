package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPatients.ALICE;
import static seedu.address.testutil.TypicalPatients.BENSON;
import static seedu.address.testutil.TypicalPatients.CARL;
import static seedu.address.testutil.TypicalPatients.DANIEL;
import static seedu.address.testutil.TypicalPatients.ELLE;
import static seedu.address.testutil.TypicalPatients.FIONA;
import static seedu.address.testutil.TypicalPatients.GEORGE;
import static seedu.address.testutil.TypicalPatients.getTypicalLaiNurse;
import static seedu.address.testutil.TypicalPatients.getTypicalPatients;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.parser.LaiNurseParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.LaiNurse;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientAppointment;
import seedu.address.testutil.PatientBuilder;

/**
 * Tests find commands from user input through patient-list filtering.
 */
public class FindCommandFlowTest {
    private final LaiNurseParser parser = new LaiNurseParser();
    private final Model model = new ModelManager(getTypicalLaiNurse(), new UserPrefs());

    @Test
    public void execute_multipleCriteria_uniquePatientsRankedByMatchingFields() throws Exception {
        List<PatientAppointment> appointments = List.copyOf(model.getAppointmentList());
        CommandResult result = execute("find --id T --name Fiona --address tokyo");
        assertEquals(List.of(FIONA, ELLE), model.getFilteredPatientList());
        assertEquals(String.format(Messages.MESSAGE_PATIENTS_LISTED_OVERVIEW, 2), result.getFeedbackToUser());
        assertEquals(getTypicalPatients(), model.getLaiNurse().getPatientList());
        assertEquals(appointments, model.getAppointmentList());

        execute("find --address tokyo --name Fiona --id T");
        assertEquals(List.of(FIONA, ELLE), model.getFilteredPatientList());
    }

    @Test
    public void execute_equalMatchCounts_preservesOriginalOrderWithinEachGroup() throws Exception {
        executeRankedSearch();
        assertEquals(getTypicalPatients(), model.getLaiNurse().getPatientList());
    }

    @Test
    public void execute_singleFieldAfterRankedSearch_restoresOriginalOrderForTies() throws Exception {
        executeRankedSearch();
        execute("find --address street");
        assertEquals(List.of(CARL, DANIEL, GEORGE), model.getFilteredPatientList());
    }

    @Test
    public void execute_notesAndName_uniquePatientsInOriginalOrder() throws Exception {
        Patient aliceWithNotes = new PatientBuilder(ALICE).withNotes("Enjoys gardening").build();
        Patient carlWithNotes = new PatientBuilder(CARL).withNotes("Walks every morning").build();
        model.setPatient(ALICE, aliceWithNotes);
        model.setPatient(CARL, carlWithNotes);
        LaiNurse expectedLaiNurse = new LaiNurse(model.getLaiNurse());

        execute("find --notes \"ENJOYS   GARDEN\"");
        assertEquals(List.of(aliceWithNotes), model.getFilteredPatientList());

        execute("find --notes morning --name Alice");
        assertEquals(List.of(aliceWithNotes, carlWithNotes), model.getFilteredPatientList());

        execute("find --notes gardening --name Alice");
        assertEquals(List.of(aliceWithNotes), model.getFilteredPatientList());

        execute("find --notes swimming");
        assertEquals(List.of(), model.getFilteredPatientList());
        assertEquals(expectedLaiNurse, model.getLaiNurse());
    }

    @Test
    public void execute_successiveSearches_replacePreviousFilters() throws Exception {
        execute("viewappt 2026-10-05");
        assertEquals(List.of(BENSON), model.getFilteredPatientList());

        execute("find --id T --name Fiona");
        assertEquals(List.of(FIONA, ELLE), model.getFilteredPatientList());

        execute("find --id T");
        assertEquals(List.of(ELLE, FIONA), model.getFilteredPatientList());

        execute("find --name \"ali\"");
        assertEquals(List.of(ALICE), model.getFilteredPatientList());

        execute("find --address “wall”");
        assertEquals(List.of(CARL), model.getFilteredPatientList());

        execute("viewappt --from 2026-10-01 --to 2026-10-31");
        assertEquals(List.of(BENSON, DANIEL), model.getFilteredPatientList());
    }

    @Test
    public void execute_invalidCommands_preservePatientsAndCurrentFilter() throws Exception {
        execute("find --id T --name Fiona");
        LaiNurse expectedLaiNurse = new LaiNurse(model.getLaiNurse());
        List<Patient> expectedFilteredPatients = List.copyOf(model.getFilteredPatientList());

        for (String command : List.of("find", "find John", "find --name \"\"", "find --name Mary --unknown value",
                "find --id 123 --id 456", "find --address “Marina Bay\"", "find --next-appointment 2026-10",
                "find --notes", "find --notes \"\"", "find --notes \"gardening",
                "find --notes garden --notes walk")) {
            assertThrows(ParseException.class, () -> execute(command));
            assertEquals(expectedLaiNurse, model.getLaiNurse());
            assertEquals(expectedFilteredPatients, model.getFilteredPatientList());
        }
    }

    @Test
    public void execute_listAfterNoMatches_restoresAllPatients() throws Exception {
        executeRankedSearch();
        CommandResult result = execute("find --name Nobody");
        assertEquals(List.of(), model.getFilteredPatientList());
        assertEquals(String.format(Messages.MESSAGE_PATIENTS_LISTED_OVERVIEW, 0), result.getFeedbackToUser());

        execute("list");
        assertEquals(getTypicalPatients(), model.getFilteredPatientList());
        assertEquals(getTypicalPatients(), model.getLaiNurse().getPatientList());
    }

    @Test
    public void execute_listAfterRankedSearch_restoresAllPatientsInOriginalOrder() throws Exception {
        executeRankedSearch();
        execute("list");
        assertEquals(getTypicalPatients(), model.getFilteredPatientList());
    }

    @Test
    public void execute_addAfterRankedSearch_restoresAllPatientsInOriginalOrder() throws Exception {
        executeRankedSearch();
        execute("add --id NEW123 --name New Patient --phone 12345678 --address New Street");

        Patient addedPatient = new PatientBuilder().withId("NEW123").withName("New Patient")
                .withPhone("12345678").withAddress("New Street").build();
        List<Patient> expectedPatients = new ArrayList<>(getTypicalPatients());
        expectedPatients.add(addedPatient);
        assertEquals(expectedPatients, model.getFilteredPatientList());
        assertEquals(expectedPatients, model.getLaiNurse().getPatientList());
    }

    @Test
    public void execute_editAfterRankedSearch_restoresAllPatientsInOriginalOrder() throws Exception {
        executeRankedSearch();
        execute("edit S1234567A --name Alice Updated");

        Patient editedPatient = new PatientBuilder(ALICE).withName("Alice Updated").build();
        List<Patient> expectedPatients = getTypicalPatients();
        expectedPatients.set(0, editedPatient);
        assertEquals(expectedPatients, model.getFilteredPatientList());
        assertEquals(expectedPatients, model.getLaiNurse().getPatientList());
    }

    @Test
    public void execute_viewAppointmentAfterRankedSearch_restoresOriginalPatientOrder() throws Exception {
        executeRankedSearch();
        execute("viewappt --from 2026-10-01 --to 2026-10-31");
        assertEquals(List.of(BENSON, DANIEL), model.getFilteredPatientList());
    }

    @Test
    public void execute_patientReplacementsAndDeletion_keepRankedResultsCurrent() throws Exception {
        execute("find --name Fiona --address tokyo");
        assertEquals(List.of(FIONA), model.getFilteredPatientList());

        // Elle enters the results with the same score as Fiona, but has an earlier original position.
        Patient editedElle = new PatientBuilder(ELLE).withName("Fiona Elle").withAddress("Tokyo Town").build();
        model.setPatient(ELLE, editedElle);
        assertEquals(List.of(editedElle, FIONA), model.getFilteredPatientList());

        Patient partialMatch = new PatientBuilder(editedElle).withAddress("Elsewhere").build();
        model.setPatient(editedElle, partialMatch);
        assertEquals(List.of(FIONA, partialMatch), model.getFilteredPatientList());

        model.deletePatient(FIONA);
        assertEquals(List.of(partialMatch), model.getFilteredPatientList());
        model.setPatient(partialMatch, ELLE);
        assertEquals(List.of(), model.getFilteredPatientList());
    }

    private void executeRankedSearch() throws Exception {
        execute("find --id S --name Meier --address street");
        assertEquals(List.of(DANIEL, BENSON, CARL, ALICE, GEORGE), model.getFilteredPatientList());
    }

    private CommandResult execute(String command) throws Exception {
        return parser.parseCommand(command).execute(model);
    }
}
