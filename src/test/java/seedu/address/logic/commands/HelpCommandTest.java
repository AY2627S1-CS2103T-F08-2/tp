package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;

public class HelpCommandTest {

    private final Model model = new ModelManager();
    private final Model expectedModel = new ModelManager();

    @Test
    public void execute_helpNoArgs_showsAllCommandsSummary() {
        CommandResult expectedResult = new CommandResult(HelpCommand.SHOWING_HELP_SUMMARY);
        assertCommandSuccess(new HelpCommand(), model, expectedResult, expectedModel);
    }

    @Test
    public void execute_helpValidTargetAdd_showsAddUsage() {
        CommandResult expectedResult = new CommandResult(AddCommand.MESSAGE_USAGE);
        assertCommandSuccess(new HelpCommand("add"), model, expectedResult, expectedModel);
    }

    @Test
    public void execute_helpValidTargetDelete_showsDeleteUsage() {
        CommandResult expectedResult = new CommandResult(DeleteCommand.MESSAGE_USAGE);
        assertCommandSuccess(new HelpCommand("delete"), model, expectedResult, expectedModel);
    }

    @Test
    public void execute_helpValidTargetViewAppt_showsViewApptUsage() {
        CommandResult expectedResult = new CommandResult(ViewAppointmentCommand.MESSAGE_USAGE);
        assertCommandSuccess(new HelpCommand("viewappt"), model, expectedResult, expectedModel);
    }

    @Test
    public void execute_helpWindowFlag_opensHelpWindow() throws Exception {
        CommandResult result = new HelpCommand("window").execute(model);
        assertTrue(result.isShowHelp());
        assertFalse(result.isExit());
        assertEquals(HelpCommand.SHOWING_HELP_MESSAGE, result.getFeedbackToUser());
    }

    @Test
    public void execute_helpUnknownTarget_success() {
        String invalidTarget = "nonexistent";
        String expectedMessage = String.format(HelpCommand.MESSAGE_UNKNOWN_HELP_TARGET, invalidTarget);
        CommandResult expectedCommandResult = new CommandResult(expectedMessage);
        assertCommandSuccess(new HelpCommand(invalidTarget), model, expectedCommandResult, model);
    }

    @Test
    public void execute_allCommandsPresentInSummary() {
        for (String commandWord : HelpCommand.COMMAND_USAGE_MAP.keySet()) {
            assertTrue(HelpCommand.SHOWING_HELP_SUMMARY.contains(commandWord));
        }
    }

    @Test
    public void equals() {
        HelpCommand defaultHelp = new HelpCommand();
        HelpCommand helpDelete1 = new HelpCommand("delete");
        HelpCommand helpDelete2 = new HelpCommand("delete");
        HelpCommand helpAdd = new HelpCommand("add");
        HelpCommand helpWindow = new HelpCommand("window");

        // Same object -> true
        assertTrue(defaultHelp.equals(defaultHelp));
        assertTrue(helpDelete1.equals(helpDelete1));

        // Same values -> true
        assertTrue(helpDelete1.equals(helpDelete2));

        // Different types -> false
        assertFalse(defaultHelp.equals(1));

        // Null -> false
        assertFalse(defaultHelp.equals(null));

        // Different targets -> false
        assertFalse(helpDelete1.equals(helpAdd));
        assertFalse(helpDelete1.equals(helpWindow));
        assertFalse(defaultHelp.equals(helpDelete1));
    }
}
