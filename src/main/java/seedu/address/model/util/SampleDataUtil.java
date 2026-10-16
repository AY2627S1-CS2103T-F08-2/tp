package seedu.address.model.util;

import java.util.Arrays;
import java.util.Optional;

import seedu.address.model.LaiNurse;
import seedu.address.model.ReadOnlyLaiNurse;
import seedu.address.model.patient.Address;
import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.AppointmentSchedule;
import seedu.address.model.patient.MedicalHistory;
import seedu.address.model.patient.Name;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientId;
import seedu.address.model.patient.Phone;

/**
 * Contains utility methods for populating {@code LaiNurse} with sample data.
 */
public class SampleDataUtil {
    public static Patient[] getSamplePatients() {
        return new Patient[] {
            new Patient(new PatientId("S1234567A"), new Name("Alex Yeoh"), new Phone("87438807"),
                new Address("Blk 30 Geylang Street 29, #06-40"),
                Optional.of(new MedicalHistory("Type 2 diabetes")),
                getSchedule("2026-10-05 09:30", "2026-11-02")),
            new Patient(new PatientId("S2345678B"), new Name("Bernice Yu"), new Phone("99272758"),
                new Address("Blk 30 Lorong 3 Serangoon Gardens, #07-18"),
                Optional.of(new MedicalHistory("Hypertension, allergic to penicillin")),
                getSchedule("2026-10-06")),
            new Patient(new PatientId("T0123456C"), new Name("Charlotte Oliveiro"), new Phone("93210283"),
                new Address("Blk 11 Ang Mo Kio Street 74, #11-04"),
                Optional.empty(), AppointmentSchedule.empty()),
            new Patient(new PatientId("S3456789D"), new Name("David Li"), new Phone("91031282"),
                new Address("Blk 436 Serangoon Gardens Street 26, #16-43"),
                Optional.of(new MedicalHistory("Post-stroke rehabilitation")),
                AppointmentSchedule.empty()),
            new Patient(new PatientId("G1234567E"), new Name("Irfan Ibrahim"), new Phone("92492021"),
                new Address("Blk 47 Tampines Street 20, #17-35"),
                Optional.empty(),
                getSchedule("2026-10-07 14:00")),
            new Patient(new PatientId("S4567890F"), new Name("Roy Balakrishnan"), new Phone("92624417"),
                new Address("Blk 45 Aljunied Street 85, #11-31"),
                Optional.of(new MedicalHistory("Dementia")),
                AppointmentSchedule.empty())
        };
    }

    /**
     * Returns an {@code AppointmentSchedule} containing the given appointments.
     */
    public static AppointmentSchedule getSchedule(String... appointments) {
        return new AppointmentSchedule(Arrays.stream(appointments).map(Appointment::new).toList());
    }

    public static ReadOnlyLaiNurse getSampleLaiNurse() {
        LaiNurse sampleAb = new LaiNurse();
        for (Patient samplePatient : getSamplePatients()) {
            sampleAb.addPatient(samplePatient);
        }
        return sampleAb;
    }

}
