package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_INDEX;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Appointment;
import seedu.address.model.person.MedicalHistory;
import seedu.address.model.person.Name;
import seedu.address.model.person.PatientId;
import seedu.address.model.person.Phone;

public class ParserUtilTest {
    private static final String INVALID_ID = "S1234-567";
    private static final String INVALID_NAME = " ";
    private static final String INVALID_PHONE = "+65abc";
    private static final String INVALID_ADDRESS = " ";
    private static final String INVALID_APPOINTMENT_1 = "2026/06/07";
    private static final String INVALID_APPOINTMENT_2 = "2026-06-07 1807";

    private static final String VALID_ID = "S1234567A";
    private static final String VALID_NAME = "Rachel Walker";
    private static final String VALID_PHONE = "123456";
    private static final String VALID_ADDRESS = "123 Main Street #0505";
    private static final String VALID_MEDICAL_HISTORY = "dementia";
    private static final String VALID_APPOINTMENT_WITHOUT_TIME = "2026-06-07";
    private static final String VALID_APPOINTMENT_WITH_TIME = "2026-06-07 18:07";

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
    public void parseMedicalHistory_validValueWithoutWhitespace_returnsMedicalHistory() throws Exception {
        MedicalHistory expectedMedicalHistory = new MedicalHistory(VALID_MEDICAL_HISTORY);
        assertEquals(expectedMedicalHistory, ParserUtil.parseMedicalHistory(VALID_MEDICAL_HISTORY));
    }

    @Test
    public void parseMedicalHistory_validValueWithWhitespace_returnsTrimmedMedicalHistory() throws Exception {
        String medicalHistoryWithWhitespace = WHITESPACE + VALID_MEDICAL_HISTORY + WHITESPACE;
        MedicalHistory expectedMedicalHistory = new MedicalHistory(VALID_MEDICAL_HISTORY);
        assertEquals(expectedMedicalHistory, ParserUtil.parseMedicalHistory(medicalHistoryWithWhitespace));
    }

    @Test
    public void parseAppointment_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseAppointment((String) null));
    }

    @Test
    public void parseAppointment_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseAppointment(INVALID_APPOINTMENT_1));
        assertThrows(ParseException.class, () -> ParserUtil.parseAppointment(INVALID_APPOINTMENT_2));
    }

    @Test
    public void parseAppointment_validValueWithoutTime_returnsAppointment() throws Exception {
        Appointment expectedAppointment = new Appointment(VALID_APPOINTMENT_WITHOUT_TIME);
        assertEquals(expectedAppointment, ParserUtil.parseAppointment(VALID_APPOINTMENT_WITHOUT_TIME));
    }

    @Test
    public void parseAppointment_validValueWithTime_returnsAppointment() throws Exception {
        Appointment expectedAppointment = new Appointment(VALID_APPOINTMENT_WITH_TIME);
        assertEquals(expectedAppointment, ParserUtil.parseAppointment(VALID_APPOINTMENT_WITH_TIME));
    }

    @Test
    public void parseAppointment_validValueWithoutTimeWithWhitespace_returnsTrimmedAppointment() throws Exception {
        String appointmentWithWhitespace = WHITESPACE + VALID_APPOINTMENT_WITHOUT_TIME + WHITESPACE;
        Appointment expectedAppointment = new Appointment(VALID_APPOINTMENT_WITHOUT_TIME);
        assertEquals(expectedAppointment, ParserUtil.parseAppointment(appointmentWithWhitespace));
    }

    @Test
    public void parseAppointment_validValueWithTimeWithExtraWhitespace_returnsTrimmedAppointment() throws Exception {
        String appointmentWithExtraWhitespace = WHITESPACE + VALID_APPOINTMENT_WITH_TIME + WHITESPACE;
        Appointment expectedAppointment = new Appointment(VALID_APPOINTMENT_WITH_TIME);
        assertEquals(expectedAppointment, ParserUtil.parseAppointment(appointmentWithExtraWhitespace));
    }
}
