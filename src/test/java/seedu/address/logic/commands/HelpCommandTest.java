package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.commons.util.HelpBookUtil;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;

public class HelpCommandTest {

    private final Model model = new ModelManager();
    private final Model expectedModel = new ModelManager();

    @Test
    public void execute_help_showsAllCommandsSummary() {
        CommandResult expectedResult = new CommandResult(
                HelpBookUtil.getAllCommandsSummary(), false, false);
        assertCommandSuccess(new HelpCommand(), model, expectedResult, expectedModel);
    }

    @Test
    public void execute_helpValidTarget_showsSpecificHelp() {
        CommandResult expectedResult = new CommandResult(
                HelpBookUtil.getSpecificCommandHelp("delete").get(), false, false);
        assertCommandSuccess(new HelpCommand("delete"), model, expectedResult, expectedModel);
    }

    @Test
    public void execute_helpUnknownTarget_throwsCommandException() {
        String invalidTarget = "nonexistent";
        String expectedMessage = String.format(HelpCommand.MESSAGE_UNKNOWN_TARGET, invalidTarget);
        assertCommandFailure(new HelpCommand(invalidTarget), model, expectedMessage);
    }

    @Test
    public void equals() {
        HelpCommand defaultHelp = new HelpCommand();
        HelpCommand helpDelete1 = new HelpCommand("delete");
        HelpCommand helpDelete2 = new HelpCommand("delete");
        HelpCommand helpAdd = new HelpCommand("add");

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
        assertFalse(defaultHelp.equals(helpDelete1));
    }
}
