package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPatients.getTypicalAddressBook;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.commons.util.DateUtil;
import seedu.address.logic.commands.ViewAppointmentCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

public class ViewAppointmentCommandParserTest {

    private ViewAppointmentCommandParser parser = new ViewAppointmentCommandParser();
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void parse_validDateOnly_returnsCommand() throws Exception {
        LocalDate expectedDate = LocalDate.of(2026, 10, 15);
        ViewAppointmentCommand expectedCommand = new ViewAppointmentCommand(expectedDate);

        assertEquals(expectedCommand, parser.parse("2026-10-15"));
    }

    @Test
    public void parse_validDateWithWhitespace_returnsCommand() throws Exception {
        LocalDate expectedDate = LocalDate.of(2026, 10, 15);
        ViewAppointmentCommand expectedCommand = new ViewAppointmentCommand(expectedDate);

        assertEquals(expectedCommand, parser.parse("  2026-10-15  "));
    }

    @Test
    public void parse_invalidDate_throwsParseException() {
        String expectedMessage = DateUtil.MESSAGE_CONSTRAINTS;

        assertThrows(ParseException.class, expectedMessage, () -> parser.parse("2026/10/15"));
        assertThrows(ParseException.class, expectedMessage, () -> parser.parse("invalid"));
        assertThrows(ParseException.class, expectedMessage, () -> parser.parse("2026-13-01"));
        assertThrows(ParseException.class, expectedMessage, () -> parser.parse("2026-02-30"));
    }

    @Test
    public void parse_emptyString_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse(""));
    }

    @Test
    public void parse_multipleDates_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("2026-10-15 2026-10-20"));
    }
}
