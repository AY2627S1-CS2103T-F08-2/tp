package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.APPOINTMENT_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.ID_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.ID_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.MEDICAL_HISTORY_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_APPOINTMENT_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_MEDICAL_HISTORY_BOB;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MEDICAL_HISTORY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTES;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPatients.AMY;
import static seedu.address.testutil.TypicalPatients.BOB;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.patient.Address;
import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.MedicalHistory;
import seedu.address.model.patient.Name;
import seedu.address.model.patient.Notes;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientId;
import seedu.address.model.patient.Phone;
import seedu.address.testutil.PatientBuilder;
import seedu.address.testutil.PatientUtil;

/**
 * Tests option boundaries and validation for add commands.
 */
public class AddCommandParserValidationTest {
    private static final String REQUIRED_ARGUMENTS = ID_DESC_AMY + NAME_DESC_AMY + PHONE_DESC_AMY + ADDRESS_DESC_AMY;
    private static final String INVALID_FORMAT_MESSAGE =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);

    private final AddCommandParser parser = new AddCommandParser();
    private final LaiNurseParser commandParser = new LaiNurseParser();

    @Test
    public void parse_optionalFieldCombinationsInDifferentOrders_success() {
        assertParseSuccess(parser, ADDRESS_DESC_AMY + PHONE_DESC_AMY + NAME_DESC_AMY + ID_DESC_AMY,
                new AddCommand(AMY));

        Patient historyOnly = new PatientBuilder(AMY).withMedicalHistory(VALID_MEDICAL_HISTORY_BOB).build();
        assertParseSuccess(parser, MEDICAL_HISTORY_DESC_BOB + PHONE_DESC_AMY + ADDRESS_DESC_AMY
                + ID_DESC_AMY + NAME_DESC_AMY, new AddCommand(historyOnly));

        Patient appointmentOnly = new PatientBuilder(AMY).withAppointments(VALID_APPOINTMENT_BOB).build();
        assertParseSuccess(parser, NAME_DESC_AMY + APPOINTMENT_DESC_BOB + ADDRESS_DESC_AMY
                + ID_DESC_AMY + PHONE_DESC_AMY, new AddCommand(appointmentOnly));

        assertParseSuccess(parser, APPOINTMENT_DESC_BOB + ADDRESS_DESC_BOB + NAME_DESC_BOB
                + MEDICAL_HISTORY_DESC_BOB + PHONE_DESC_BOB + ID_DESC_BOB, new AddCommand(BOB));
    }

    @Test
    public void parse_notesInDifferentOrders_success() {
        Patient notesOnly = new PatientBuilder(AMY).withNotes("Enjoys gardening").build();
        assertParseSuccess(parser, " --notes Enjoys   gardening" + REQUIRED_ARGUMENTS, new AddCommand(notesOnly));
        assertParseSuccess(parser, REQUIRED_ARGUMENTS + "\t--notes\tEnjoys\t gardening", new AddCommand(notesOnly));

        Patient allOptionalFields = new PatientBuilder(BOB).withNotes("Dislikes loud noises").build();
        assertParseSuccess(parser, PatientUtil.getPatientDetails(allOptionalFields), new AddCommand(allOptionalFields));
    }

    @Test
    public void parse_repeatedNotes_failure() {
        for (String repeatedNotes : List.of(" --notes First --notes Second", " --notes First --notes",
                " --notes --notes Second")) {
            assertParseFailure(parser, REQUIRED_ARGUMENTS + repeatedNotes,
                    Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NOTES));
        }
    }

    @Test
    public void parse_dateOnlyAppointment_success() {
        Patient expectedPatient = new PatientBuilder(AMY).withAppointments("2026-09-20").build();
        assertParseSuccess(parser, REQUIRED_ARGUMENTS + " --appt 2026-09-20",
                new AddCommand(expectedPatient));
    }

    @Test
    public void parse_tabSeparatedOptions_success() {
        String arguments = "\t--medical-history\tdementia\t--phone\t22222222\t--id\tS2222222B"
                + "\t--address\tBlock 123, Bobby Street 3\t--appt\t2026-11-30 09:00\t--name\tBob Choo";
        assertParseSuccess(parser, arguments, new AddCommand(BOB));
    }

    @Test
    public void parse_noLeadingWhitespace_success() {
        assertParseSuccess(parser, PatientUtil.getPatientDetails(AMY).trim(), new AddCommand(AMY));
    }

    @Test
    public void parseCommand_emptyFinalValue_failure() {
        assertCommandParseFailure(NAME_DESC_AMY + PHONE_DESC_AMY + ADDRESS_DESC_AMY + " --id",
                PatientId.MESSAGE_CONSTRAINTS);
        assertCommandParseFailure(ID_DESC_AMY + PHONE_DESC_AMY + ADDRESS_DESC_AMY + " --name",
                Name.MESSAGE_CONSTRAINTS);
        assertCommandParseFailure(ID_DESC_AMY + NAME_DESC_AMY + ADDRESS_DESC_AMY + " --phone",
                Phone.MESSAGE_CONSTRAINTS);
        assertCommandParseFailure(ID_DESC_AMY + NAME_DESC_AMY + PHONE_DESC_AMY + " --address",
                Address.MESSAGE_CONSTRAINTS);
        assertCommandParseFailure(REQUIRED_ARGUMENTS + " --medical-history", MedicalHistory.MESSAGE_CONSTRAINTS);
        assertCommandParseFailure(REQUIRED_ARGUMENTS + " --notes", Notes.MESSAGE_CONSTRAINTS);
        assertCommandParseFailure(REQUIRED_ARGUMENTS + " --appt", Appointment.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_emptyOptionalValueBetweenOptions_failure() {
        assertParseFailure(parser, " --medical-history" + REQUIRED_ARGUMENTS, MedicalHistory.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " --notes \t " + REQUIRED_ARGUMENTS, Notes.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " --appt" + REQUIRED_ARGUMENTS, Appointment.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_unknownOptions_failure() {
        for (String option : List.of("--medical-histroy dementia", "--name=Bob", "--unknown value", "--",
                "--number 91234567")) {
            assertParseFailure(parser, REQUIRED_ARGUMENTS + " " + option, INVALID_FORMAT_MESSAGE);
        }

        assertParseFailure(parser, ID_DESC_AMY + NAME_DESC_AMY + " --unknown value" + PHONE_DESC_AMY
                + ADDRESS_DESC_AMY, INVALID_FORMAT_MESSAGE);
        assertParseFailure(parser, REQUIRED_ARGUMENTS + MEDICAL_HISTORY_DESC_BOB + "\t--unknown\tvalue",
                INVALID_FORMAT_MESSAGE);
    }

    @Test
    public void parseCommand_repeatedFinalOption_failure() {
        String arguments = REQUIRED_ARGUMENTS + MEDICAL_HISTORY_DESC_BOB + APPOINTMENT_DESC_BOB;
        // Appointments may repeat, so a repeated empty --appt is reported as an invalid appointment instead
        for (Prefix prefix : List.of(PREFIX_ID, PREFIX_NAME, PREFIX_PHONE, PREFIX_ADDRESS,
                PREFIX_MEDICAL_HISTORY)) {
            assertCommandParseFailure(arguments + " " + prefix.getPrefix().trim(),
                    Messages.getErrorMessageForDuplicatePrefixes(prefix));
        }
    }

    @Test
    public void parse_embeddedDoubleHyphenText_success() {
        Patient expectedPatient = new PatientBuilder(AMY).withName("Anne--Marie")
                .withAddress("Block--A, West Street").withMedicalHistory("Follow-up--stable").build();
        assertParseSuccess(parser, " " + PatientUtil.getPatientDetails(expectedPatient),
                new AddCommand(expectedPatient));
    }

    @Test
    public void parse_invalidAppointment_failure() {
        for (String appointment : List.of("2026-02-30", "2026-11-30 24:00", "2026/11/30")) {
            assertParseFailure(parser, REQUIRED_ARGUMENTS + " --appt " + appointment,
                    Appointment.MESSAGE_CONSTRAINTS);
        }
    }

    @Test
    public void parse_missingRequiredFieldBeforeDuplicateCheck_failure() {
        assertParseFailure(parser, NAME_DESC_AMY + NAME_DESC_AMY + PHONE_DESC_AMY + ADDRESS_DESC_AMY,
                INVALID_FORMAT_MESSAGE);
    }

    private void assertCommandParseFailure(String arguments, String expectedMessage) {
        String command = AddCommand.COMMAND_WORD + arguments + " \t";
        assertThrows(ParseException.class, expectedMessage, () -> commandParser.parseCommand(command));
    }
}
