package seedu.address.testutil;

import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADD_MEDICAL_HISTORY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADD_NOTES;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MEDICAL_HISTORY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTES;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;

import java.util.Optional;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.EditCommand.EditPatientDescriptor;
import seedu.address.logic.commands.OptionalFieldEdit;
import seedu.address.logic.parser.Prefix;
import seedu.address.model.patient.Patient;

/**
 * A utility class for Patient.
 */
public class PatientUtil {

    /**
     * Returns an add command string for adding the {@code patient}.
     */
    public static String getAddCommand(Patient patient) {
        return AddCommand.COMMAND_WORD + " " + getPatientDetails(patient);
    }

    /**
     * Returns the part of command string for the given {@code patient}'s details.
     * Appointments are not part of the add command; use {@code addappt ...} to set them.
     */
    public static String getPatientDetails(Patient patient) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX_ID + patient.getId().value + " ");
        sb.append(PREFIX_NAME + patient.getName().fullName + " ");
        sb.append(PREFIX_PHONE + patient.getPhone().value + " ");
        sb.append(PREFIX_ADDRESS + patient.getAddress().value + " ");
        if (patient.getMedicalHistory().isPresent()) {
            sb.append(PREFIX_MEDICAL_HISTORY + patient.getMedicalHistory().get().value + " ");
        }
        patient.getNotes().ifPresent(notes -> sb.append(PREFIX_NOTES).append(notes.value).append(" "));
        return sb.toString();
    }

    /**
     * Returns the part of command string for the given {@code EditPatientDescriptor}'s details.
     * Appointments are not editable via {@code edit}; use {@code addappt} or {@code deleteappt} instead.
     */
    public static String getEditPatientDescriptorDetails(EditPatientDescriptor descriptor) {
        StringBuilder sb = new StringBuilder();
        descriptor.getName().ifPresent(name -> sb.append(PREFIX_NAME).append(name.fullName).append(" "));
        descriptor.getPhone().ifPresent(phone -> sb.append(PREFIX_PHONE).append(phone.value).append(" "));
        descriptor.getAddress().ifPresent(address -> sb.append(PREFIX_ADDRESS).append(address.value).append(" "));
        descriptor.getMedicalHistory().ifPresent(edit ->
                sb.append(getFieldEditDetails(edit, PREFIX_MEDICAL_HISTORY, PREFIX_ADD_MEDICAL_HISTORY)));
        descriptor.getNotes().ifPresent(edit -> sb.append(getFieldEditDetails(edit, PREFIX_NOTES, PREFIX_ADD_NOTES)));
        return sb.toString();
    }

    /**
     * Returns the part of command string that gives {@code edit}: {@code replacePrefix} with the new value, or with
     * no value to clear the field, or {@code appendPrefix} with the text to add.
     */
    private static <T> String getFieldEditDetails(OptionalFieldEdit<T> edit, Prefix replacePrefix,
            Prefix appendPrefix) {
        // On an empty field, a replacement and an addition both give their value, and clearing gives nothing
        Optional<T> value = edit.applyTo(Optional.empty(), text -> {
            throw new AssertionError("An empty field has nothing to add to.");
        });
        if (value.isEmpty()) {
            return replacePrefix.toString();
        }
        Prefix prefix = edit.equals(OptionalFieldEdit.replace(value.get())) ? replacePrefix : appendPrefix;
        return prefix + value.get().toString() + " ";
    }
}
