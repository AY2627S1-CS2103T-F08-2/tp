package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.TypicalPatients.AMY;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.CommandResult.View;
import seedu.address.logic.commands.EditCommand.EditPatientDescriptor;
import seedu.address.model.patient.PatientContainsSubstringsPredicate;

public class CommandTest {

    @Test
    public void getView_patientCommands_showPatients() {
        PatientContainsSubstringsPredicate predicate = new PatientContainsSubstringsPredicate(Optional.empty(),
                Optional.of("Amy"), Optional.empty(), Optional.empty(), Optional.empty());
        for (Command command : List.of(new AddCommand(AMY), new EditCommand(AMY.getId(), new EditPatientDescriptor()),
                new FindCommand(predicate), new ListCommand(), new ViewCommand(AMY.getId()))) {
            assertEquals(Optional.of(View.PATIENTS), command.getView(), command.toString());
        }
    }

    @Test
    public void getView_appointmentCommands_showAppointments() {
        assertEquals(Optional.of(View.APPOINTMENTS), new ViewAppointmentCommand(LocalDate.of(2026, 10, 16)).getView());
    }

    @Test
    public void getView_otherCommands_keepCurrentView() {
        for (Command command : List.of(new DeleteCommand(AMY.getId()), new ClearCommand(), new ExitCommand(),
                new HelpCommand(), new HelpCommand("add"))) {
            assertEquals(Optional.empty(), command.getView(), command.getClass().getSimpleName());
        }
    }
}
