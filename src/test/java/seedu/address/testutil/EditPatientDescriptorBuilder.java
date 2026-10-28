package seedu.address.testutil;

import seedu.address.logic.commands.EditCommand.EditPatientDescriptor;
import seedu.address.logic.commands.OptionalFieldEdit;
import seedu.address.model.patient.Address;
import seedu.address.model.patient.MedicalHistory;
import seedu.address.model.patient.Name;
import seedu.address.model.patient.Notes;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.Phone;

/**
 * A utility class to help with building EditPatientDescriptor objects.
 */
public class EditPatientDescriptorBuilder {

    private EditPatientDescriptor descriptor;

    public EditPatientDescriptorBuilder() {
        descriptor = new EditPatientDescriptor();
    }

    public EditPatientDescriptorBuilder(EditPatientDescriptor descriptor) {
        this.descriptor = new EditPatientDescriptor(descriptor);
    }

    /**
     * Returns an {@code EditPatientDescriptor} with fields containing {@code patient}'s details
     */
    public EditPatientDescriptorBuilder(Patient patient) {
        descriptor = new EditPatientDescriptor();
        descriptor.setName(patient.getName());
        descriptor.setPhone(patient.getPhone());
        descriptor.setAddress(patient.getAddress());
        patient.getMedicalHistory().map(OptionalFieldEdit::replace).ifPresent(descriptor::setMedicalHistory);
        patient.getNotes().map(OptionalFieldEdit::replace).ifPresent(descriptor::setNotes);
    }

    /**
     * Sets the {@code Name} of the {@code EditPatientDescriptor} that we are building.
     */
    public EditPatientDescriptorBuilder withName(String name) {
        descriptor.setName(new Name(name));
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code EditPatientDescriptor} that we are building.
     */
    public EditPatientDescriptorBuilder withPhone(String phone) {
        descriptor.setPhone(new Phone(phone));
        return this;
    }

    /**
     * Sets the {@code Address} of the {@code EditPatientDescriptor} that we are building.
     */
    public EditPatientDescriptorBuilder withAddress(String address) {
        descriptor.setAddress(new Address(address));
        return this;
    }

    /**
     * Sets the {@code EditPatientDescriptor} that we are building to replace the medical history with
     * {@code medicalHistory}.
     */
    public EditPatientDescriptorBuilder withMedicalHistory(String medicalHistory) {
        descriptor.setMedicalHistory(OptionalFieldEdit.replace(new MedicalHistory(medicalHistory)));
        return this;
    }

    /**
     * Sets the {@code EditPatientDescriptor} that we are building to clear the medical history.
     */
    public EditPatientDescriptorBuilder withoutMedicalHistory() {
        descriptor.setMedicalHistory(OptionalFieldEdit.clear());
        return this;
    }

    /**
     * Sets the {@code EditPatientDescriptor} that we are building to add {@code medicalHistory} to the end of the
     * medical history.
     */
    public EditPatientDescriptorBuilder withMedicalHistoryAdded(String medicalHistory) {
        descriptor.setMedicalHistory(OptionalFieldEdit.append(new MedicalHistory(medicalHistory)));
        return this;
    }

    /**
     * Sets the {@code EditPatientDescriptor} that we are building to replace the notes with {@code notes}.
     */
    public EditPatientDescriptorBuilder withNotes(String notes) {
        descriptor.setNotes(OptionalFieldEdit.replace(new Notes(notes)));
        return this;
    }

    /**
     * Sets the {@code EditPatientDescriptor} that we are building to clear the notes.
     */
    public EditPatientDescriptorBuilder withoutNotes() {
        descriptor.setNotes(OptionalFieldEdit.clear());
        return this;
    }

    /**
     * Sets the {@code EditPatientDescriptor} that we are building to add {@code notes} to the end of the notes.
     */
    public EditPatientDescriptorBuilder withNotesAdded(String notes) {
        descriptor.setNotes(OptionalFieldEdit.append(new Notes(notes)));
        return this;
    }

    public EditPatientDescriptor build() {
        return descriptor;
    }
}
