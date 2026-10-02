package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_INDEX;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.MedicalHistory;
import seedu.address.model.person.Name;
import seedu.address.model.person.NextAppointment;
import seedu.address.model.person.PatientId;
import seedu.address.model.person.Phone;

public class ParserUtilTest {
    private static final String INVALID_ID = "S1234-567";
    private static final String INVALID_NAME = " ";
    private static final String INVALID_PHONE = "+65abc";
    private static final String INVALID_ADDRESS = " ";
    private static final String INVALID_MEDICAL_HISTORY = " ";
    private static final String INVALID_NEXT_APPOINTMENT = "2026-02-30";

    private static final String VALID_ID = "S1234567A";
    private static final String VALID_NAME = "Rachel Walker";
    private static final String VALID_PHONE = "123456";
    private static final String VALID_ADDRESS = "123 Main Street #0505";
    private static final String VALID_MEDICAL_HISTORY = "Asthma";
    private static final String VALID_NEXT_APPOINTMENT = "2026-10-20 10:00";

    private static final String WHITESPACE = " \t\r\n";

    @Test
    public void parseIndex_invalidInput_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseIndex("10 a"));
    }

    @Test
    public void parseIndex_outOfRangeInput_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_INDEX, ()
            -> ParserUtil.parseIndex(Long.toString(Integer.MAX_VALUE + 1)));
    }

    @Test
    public void parseIndex_validInput_success() throws Exception {
        // No whitespaces
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseIndex("1"));

        // Leading and trailing whitespaces
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseIndex("  1  "));
    }

    @Test
    public void parseId_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseId((String) null));
    }

    @Test
    public void parseId_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, PatientId.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseId(INVALID_ID));
    }

    @Test
    public void parseId_validValueWithoutWhitespace_returnsId() throws Exception {
        PatientId expectedId = new PatientId(VALID_ID);
        assertEquals(expectedId, ParserUtil.parseId(VALID_ID));
    }

    @Test
    public void parseId_validValueWithWhitespace_returnsTrimmedId() throws Exception {
        String idWithWhitespace = WHITESPACE + VALID_ID + WHITESPACE;
        PatientId expectedId = new PatientId(VALID_ID);
        assertEquals(expectedId, ParserUtil.parseId(idWithWhitespace));
    }

    @Test
    public void parseName_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseName((String) null));
    }

    @Test
    public void parseName_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseName(INVALID_NAME));
    }

    @Test
    public void parseName_validValueWithoutWhitespace_returnsName() throws Exception {
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(VALID_NAME));
    }

    @Test
    public void parseName_validValueWithWhitespace_returnsTrimmedName() throws Exception {
        String nameWithWhitespace = WHITESPACE + VALID_NAME + WHITESPACE;
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(nameWithWhitespace));
    }

    @Test
    public void parsePhone_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parsePhone((String) null));
    }

    @Test
    public void parsePhone_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parsePhone(INVALID_PHONE));
    }

    @Test
    public void parsePhone_validValueWithoutWhitespace_returnsPhone() throws Exception {
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, ParserUtil.parsePhone(VALID_PHONE));
    }

    @Test
    public void parsePhone_validValueWithWhitespace_returnsTrimmedPhone() throws Exception {
        String phoneWithWhitespace = WHITESPACE + VALID_PHONE + WHITESPACE;
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, ParserUtil.parsePhone(phoneWithWhitespace));
    }

    @Test
    public void parseAddress_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseAddress((String) null));
    }

    @Test
    public void parseAddress_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseAddress(INVALID_ADDRESS));
    }

    @Test
    public void parseAddress_validValueWithoutWhitespace_returnsAddress() throws Exception {
        Address expectedAddress = new Address(VALID_ADDRESS);
        assertEquals(expectedAddress, ParserUtil.parseAddress(VALID_ADDRESS));
    }

    @Test
    public void parseAddress_validValueWithWhitespace_returnsTrimmedAddress() throws Exception {
        String addressWithWhitespace = WHITESPACE + VALID_ADDRESS + WHITESPACE;
        Address expectedAddress = new Address(VALID_ADDRESS);
        assertEquals(expectedAddress, ParserUtil.parseAddress(addressWithWhitespace));
    }

    @Test
    public void parseMedicalHistory_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseMedicalHistory((String) null));
    }

    @Test
    public void parseMedicalHistory_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, MedicalHistory.MESSAGE_CONSTRAINTS, ()
            -> ParserUtil.parseMedicalHistory(INVALID_MEDICAL_HISTORY));
    }

    @Test
    public void parseMedicalHistory_validValueWithWhitespace_returnsTrimmedMedicalHistory() throws Exception {
        String medicalHistoryWithWhitespace = WHITESPACE + VALID_MEDICAL_HISTORY + WHITESPACE;
        MedicalHistory expectedMedicalHistory = new MedicalHistory(VALID_MEDICAL_HISTORY);
        assertEquals(expectedMedicalHistory, ParserUtil.parseMedicalHistory(medicalHistoryWithWhitespace));
    }

    @Test
    public void parseNextAppointment_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseNextAppointment((String) null));
    }

    @Test
    public void parseNextAppointment_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, NextAppointment.MESSAGE_CONSTRAINTS, ()
            -> ParserUtil.parseNextAppointment(INVALID_NEXT_APPOINTMENT));
    }

    @Test
    public void parseNextAppointment_validValueWithWhitespace_returnsTrimmedNextAppointment() throws Exception {
        String nextAppointmentWithWhitespace = WHITESPACE + VALID_NEXT_APPOINTMENT + WHITESPACE;
        NextAppointment expectedNextAppointment = new NextAppointment(VALID_NEXT_APPOINTMENT);
        assertEquals(expectedNextAppointment, ParserUtil.parseNextAppointment(nextAppointmentWithWhitespace));
    }
}
