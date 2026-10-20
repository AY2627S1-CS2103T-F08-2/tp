package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import seedu.address.model.patient.Patient;
import seedu.address.testutil.PatientBuilder;

public class MessagesTest {

    @Test
    public void format_patientWithNotes_includesNormalizedNotes() {
        Patient patient = new PatientBuilder().withMedicalHistory("Asthma").withNotes("Enjoys gardening")
                .withAppointments("2026-11-30").build();
        assertEquals("AMY BEE; ID: S9876543Z; Phone: 85355255; Address: 123, JURONG WEST AVE 6, #08-111"
                + "; Medical History: ASTHMA; Notes: ENJOYS GARDENING; Appointments: 2026-11-30",
                Messages.format(patient));
    }

    @Test
    public void format_patientWithoutNotes_omitsNotes() {
        assertEquals("AMY BEE; ID: S9876543Z; Phone: 85355255; Address: 123, JURONG WEST AVE 6, #08-111",
                Messages.format(new PatientBuilder().build()));
    }
}
