package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPatients.ALICE;
import static seedu.address.testutil.TypicalPatients.BENSON;
import static seedu.address.testutil.TypicalPatients.CARL;
import static seedu.address.testutil.TypicalPatients.DANIEL;
import static seedu.address.testutil.TypicalPatients.ELLE;
import static seedu.address.testutil.TypicalPatients.FIONA;
import static seedu.address.testutil.TypicalPatients.getTypicalLaiNurse;
import static seedu.address.testutil.TypicalPatients.getTypicalPatients;

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

/**
 * Tests find commands from user input through patient-list filtering.
 */
public class FindCommandFlowTest {
    private final LaiNurseParser parser = new LaiNurseParser();
    private final Model model = new ModelManager(getTypicalLaiNurse(), new UserPrefs());

    @Test
    public void execute_multipleCriteria_uniquePatientsInOriginalOrder() throws Exception {
        CommandResult result = execute("find --id T --name Alice --address Jurong");
        assertEquals(List.of(ALICE, ELLE, FIONA), model.getFilteredPatientList());
        assertEquals(String.format(Messages.MESSAGE_PATIENTS_LISTED_OVERVIEW, 3), result.getFeedbackToUser());
        assertEquals(getTypicalPatients(), model.getLaiNurse().getPatientList());
    }

    @Test
    public void execute_successiveSearches_replacePreviousFilters() throws Exception {
        execute("viewappt 2026-10-05");
        assertEquals(List.of(BENSON), model.getFilteredPatientList());

        execute("find --name \"ali\"");
        assertEquals(List.of(ALICE), model.getFilteredPatientList());

        execute("find --address “wall”");
        assertEquals(List.of(CARL), model.getFilteredPatientList());

        execute("viewappt --from 2026-10-01 --to 2026-10-31");
        assertEquals(List.of(BENSON, DANIEL), model.getFilteredPatientList());
    }

    @Test
    public void execute_invalidCommands_preservePatientsAndCurrentFilter() throws Exception {
        execute("find --id T");
        LaiNurse expectedLaiNurse = new LaiNurse(model.getLaiNurse());
        List<Patient> expectedFilteredPatients = List.copyOf(model.getFilteredPatientList());

        for (String command : List.of("find", "find John", "find --name \"\"", "find --name Mary --unknown value",
                "find --id 123 --id 456", "find --address “Marina Bay\"", "find --next-appointment 2026-10")) {
            assertThrows(ParseException.class, () -> execute(command));
            assertEquals(expectedLaiNurse, model.getLaiNurse());
            assertEquals(expectedFilteredPatients, model.getFilteredPatientList());
        }
    }

    @Test
    public void execute_listAfterNoMatches_restoresAllPatients() throws Exception {
        CommandResult result = execute("find --name Nobody");
        assertEquals(List.of(), model.getFilteredPatientList());
        assertEquals(String.format(Messages.MESSAGE_PATIENTS_LISTED_OVERVIEW, 0), result.getFeedbackToUser());

        execute("list");
        assertEquals(getTypicalPatients(), model.getFilteredPatientList());
        assertEquals(getTypicalPatients(), model.getLaiNurse().getPatientList());
    }

    private CommandResult execute(String command) throws Exception {
        return parser.parseCommand(command).execute(model);
    }
}
