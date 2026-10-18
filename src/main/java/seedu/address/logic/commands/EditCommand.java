package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADD_APPOINTMENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MEDICAL_HISTORY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMOVE_APPOINTMENT;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PATIENTS;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.patient.Address;
import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.AppointmentSchedule;
import seedu.address.model.patient.MedicalHistory;
import seedu.address.model.patient.Name;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientId;
import seedu.address.model.patient.Phone;

/**
 * Edits the details of an existing patient in LaiNurse.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the patient with the given ID. "
            + "Existing values will be overwritten by the input values. The ID itself cannot be edited.\n"
            + "Appointments are removed first, then added, so one command can move an appointment.\n"
            + "Parameters: ID "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_ADDRESS + "ADDRESS] "
            + "[" + PREFIX_MEDICAL_HISTORY + "MEDICAL_HISTORY] "
            + "[" + PREFIX_ADD_APPOINTMENT + "DATE_OR_DATETIME]... "
            + "[" + PREFIX_REMOVE_APPOINTMENT + "DATE_OR_DATETIME]...\n"
            + "Example: " + COMMAND_WORD + " T0123456B "
            + PREFIX_PHONE + "91234567 "
            + PREFIX_REMOVE_APPOINTMENT + "2026-10-20 09:30 "
            + PREFIX_ADD_APPOINTMENT + "2026-10-22 14:00";

    public static final String MESSAGE_EDIT_PATIENT_SUCCESS = "Edited patient: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_APPOINTMENT_NOT_FOUND = "Patient %1$s has no appointment on %2$s.";

    private final PatientId targetId;
    private final EditPatientDescriptor editPatientDescriptor;

    /**
     * @param targetId ID of the patient to edit
     * @param editPatientDescriptor details to edit the patient with
     */
    public EditCommand(PatientId targetId, EditPatientDescriptor editPatientDescriptor) {
        requireNonNull(targetId);
        requireNonNull(editPatientDescriptor);

        this.targetId = targetId;
        this.editPatientDescriptor = new EditPatientDescriptor(editPatientDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Patient> allPatients = model.getLaiNurse().getPatientList();

        Patient patientToEdit = allPatients.stream().filter(patient -> patient.getId().equals(targetId))
                .findFirst().orElseThrow(() -> new CommandException(Messages.MESSAGE_INVALID_PATIENT_ID));
        Patient editedPatient = createEditedPatient(patientToEdit, editPatientDescriptor);

        model.setPatient(patientToEdit, editedPatient);
        model.updateFilteredPatientList(PREDICATE_SHOW_ALL_PATIENTS);
        return new CommandResult(String.format(MESSAGE_EDIT_PATIENT_SUCCESS, Messages.format(editedPatient)));
    }

    /**
     * Creates and returns a {@code Patient} with the details of {@code patientToEdit}
     * edited with {@code editPatientDescriptor}. The patient ID is never changed.
     * Appointments to remove are removed before appointments to add are added.
     *
     * @throws CommandException if an appointment to remove is not one of the patient's appointments.
     */
    private static Patient createEditedPatient(Patient patientToEdit, EditPatientDescriptor editPatientDescriptor)
            throws CommandException {
        assert patientToEdit != null;

        Name updatedName = editPatientDescriptor.getName().orElse(patientToEdit.getName());
        Phone updatedPhone = editPatientDescriptor.getPhone().orElse(patientToEdit.getPhone());
        Address updatedAddress = editPatientDescriptor.getAddress().orElse(patientToEdit.getAddress());
        Optional<MedicalHistory> updatedMedicalHistory = editPatientDescriptor.getMedicalHistory()
                .or(patientToEdit::getMedicalHistory);

        AppointmentSchedule updatedSchedule = patientToEdit.getSchedule();
        for (Appointment appointment : editPatientDescriptor.getAppointmentsToRemove().asList()) {
            if (!updatedSchedule.contains(appointment)) {
                throw new CommandException(
                        String.format(MESSAGE_APPOINTMENT_NOT_FOUND, patientToEdit.getId(), appointment));
            }
            updatedSchedule = updatedSchedule.remove(appointment);
        }
        for (Appointment appointment : editPatientDescriptor.getAppointmentsToAdd().asList()) {
            updatedSchedule = updatedSchedule.add(appointment);
        }

        // edit has no option for notes yet, so they stay as they are
        return new Patient(patientToEdit.getId(), updatedName, updatedPhone, updatedAddress,
                updatedMedicalHistory, patientToEdit.getNotes(), updatedSchedule);
    }

    @Override
    public Optional<CommandResult.View> getView() {
        return Optional.of(CommandResult.View.PATIENTS);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand otherEditCommand)) {
            return false;
        }

        return targetId.equals(otherEditCommand.targetId)
                && editPatientDescriptor.equals(otherEditCommand.editPatientDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetId", targetId)
                .add("editPatientDescriptor", editPatientDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit the patient with. Each non-empty field value will replace the
     * corresponding field value of the patient.
     */
    public static class EditPatientDescriptor {
        private Name name;
        private Phone phone;
        private Address address;
        private MedicalHistory medicalHistory;
        private AppointmentSchedule appointmentsToAdd = AppointmentSchedule.empty();
        private AppointmentSchedule appointmentsToRemove = AppointmentSchedule.empty();

        public EditPatientDescriptor() {}

        /**
         * Copy constructor.
         */
        public EditPatientDescriptor(EditPatientDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setAddress(toCopy.address);
            setMedicalHistory(toCopy.medicalHistory);
            setAppointmentsToAdd(toCopy.appointmentsToAdd);
            setAppointmentsToRemove(toCopy.appointmentsToRemove);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, phone, address, medicalHistory)
                    || !appointmentsToAdd.isEmpty() || !appointmentsToRemove.isEmpty();
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public Optional<Address> getAddress() {
            return Optional.ofNullable(address);
        }

        public void setMedicalHistory(MedicalHistory medicalHistory) {
            this.medicalHistory = medicalHistory;
        }

        public Optional<MedicalHistory> getMedicalHistory() {
            return Optional.ofNullable(medicalHistory);
        }

        /**
         * Sets the appointments to add. Must not be null; use {@link AppointmentSchedule#empty()} for none.
         */
        public void setAppointmentsToAdd(AppointmentSchedule appointmentsToAdd) {
            this.appointmentsToAdd = requireNonNull(appointmentsToAdd);
        }

        /**
         * Returns the appointments to add, which is empty if none were given.
         */
        public AppointmentSchedule getAppointmentsToAdd() {
            return appointmentsToAdd;
        }

        /**
         * Sets the appointments to remove. Must not be null; use {@link AppointmentSchedule#empty()} for none.
         */
        public void setAppointmentsToRemove(AppointmentSchedule appointmentsToRemove) {
            this.appointmentsToRemove = requireNonNull(appointmentsToRemove);
        }

        /**
         * Returns the appointments to remove, which is empty if none were given.
         */
        public AppointmentSchedule getAppointmentsToRemove() {
            return appointmentsToRemove;
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditPatientDescriptor otherEditPatientDescriptor)) {
                return false;
            }

            return Objects.equals(name, otherEditPatientDescriptor.name)
                    && Objects.equals(phone, otherEditPatientDescriptor.phone)
                    && Objects.equals(address, otherEditPatientDescriptor.address)
                    && Objects.equals(medicalHistory, otherEditPatientDescriptor.medicalHistory)
                    && appointmentsToAdd.equals(otherEditPatientDescriptor.appointmentsToAdd)
                    && appointmentsToRemove.equals(otherEditPatientDescriptor.appointmentsToRemove);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("phone", phone)
                    .add("address", address)
                    .add("medicalHistory", medicalHistory)
                    .add("appointmentsToAdd", appointmentsToAdd)
                    .add("appointmentsToRemove", appointmentsToRemove)
                    .toString();
        }
    }
}
