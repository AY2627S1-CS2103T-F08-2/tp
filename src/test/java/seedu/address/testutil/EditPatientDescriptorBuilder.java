package seedu.address.testutil;

import seedu.address.logic.commands.EditCommand.EditPatientDescriptor;
import seedu.address.model.patient.Address;
import seedu.address.model.patient.MedicalHistory;
import seedu.address.model.patient.Name;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.Phone;
import seedu.address.model.util.SampleDataUtil;

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
        patient.getMedicalHistory().ifPresent(descriptor::setMedicalHistory);
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
     * Sets the {@code MedicalHistory} of the {@code EditPatientDescriptor} that we are building.
     */
    public EditPatientDescriptorBuilder withMedicalHistory(String medicalHistory) {
        descriptor.setMedicalHistory(new MedicalHistory(medicalHistory));
        return this;
    }

    /**
     * Sets the appointments to add of the {@code EditPatientDescriptor} that we are building.
     */
    public EditPatientDescriptorBuilder withAppointmentsToAdd(String... appointments) {
        descriptor.setAppointmentsToAdd(SampleDataUtil.getSchedule(appointments));
        return this;
    }

    /**
     * Sets the appointments to remove of the {@code EditPatientDescriptor} that we are building.
     */
    public EditPatientDescriptorBuilder withAppointmentsToRemove(String... appointments) {
        descriptor.setAppointmentsToRemove(SampleDataUtil.getSchedule(appointments));
        return this;
    }

    public EditPatientDescriptor build() {
        return descriptor;
    }
}
