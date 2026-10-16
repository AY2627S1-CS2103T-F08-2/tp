package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ID_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ID_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_MEDICAL_HISTORY_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPatientAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PATIENT;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PATIENT;
import static seedu.address.testutil.TypicalPatients.BENSON;
import static seedu.address.testutil.TypicalPatients.getTypicalLaiNurse;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditCommand.EditPatientDescriptor;
import seedu.address.model.LaiNurse;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientId;
import seedu.address.testutil.EditPatientDescriptorBuilder;
import seedu.address.testutil.PatientBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for EditCommand.
 */
public class EditCommandTest {

    private Model model = new ModelManager(getTypicalLaiNurse(), new UserPrefs());

    @Test
    public void execute_allFieldsSpecified_success() {
        // The descriptor copies no appointments and the ID cannot be edited, so these are kept from the first patient
        Patient firstPatient = model.getFilteredPatientList().get(INDEX_FIRST_PATIENT.getZeroBased());
        Patient editedPatient = new PatientBuilder(firstPatient).withName(VALID_NAME_BOB)
                .withPhone(VALID_PHONE_BOB).withAddress(VALID_ADDRESS_BOB).build();
        EditPatientDescriptor descriptor = new EditPatientDescriptorBuilder(editedPatient).build();
        EditCommand editCommand = new EditCommand(firstPatient.getId(), descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PATIENT_SUCCESS,
                Messages.format(editedPatient));

        Model expectedModel = new ModelManager(new LaiNurse(model.getLaiNurse()), new UserPrefs());
        expectedModel.setPatient(firstPatient, editedPatient);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_someFieldsSpecified_success() {
        Patient lastPatient = model.getFilteredPatientList().get(model.getFilteredPatientList().size() - 1);

        PatientBuilder patientInList = new PatientBuilder(lastPatient);
        Patient editedPatient = patientInList.withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB).build();

        EditPatientDescriptor descriptor = new EditPatientDescriptorBuilder().withName(VALID_NAME_BOB)
                .withPhone(VALID_PHONE_BOB).build();
        EditCommand editCommand = new EditCommand(lastPatient.getId(), descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PATIENT_SUCCESS,
                Messages.format(editedPatient));

        Model expectedModel = new ModelManager(new LaiNurse(model.getLaiNurse()), new UserPrefs());
        expectedModel.setPatient(lastPatient, editedPatient);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_medicalHistorySpecified_success() {
        // Only the medical history changes; the other fields are kept
        Patient firstPatient = model.getFilteredPatientList().get(INDEX_FIRST_PATIENT.getZeroBased());
        Patient editedPatient = new PatientBuilder(firstPatient).withMedicalHistory(VALID_MEDICAL_HISTORY_BOB).build();
        EditPatientDescriptor descriptor = new EditPatientDescriptorBuilder()
                .withMedicalHistory(VALID_MEDICAL_HISTORY_BOB).build();
        EditCommand editCommand = new EditCommand(firstPatient.getId(), descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PATIENT_SUCCESS,
                Messages.format(editedPatient));

        Model expectedModel = new ModelManager(new LaiNurse(model.getLaiNurse()), new UserPrefs());
        expectedModel.setPatient(firstPatient, editedPatient);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_addAppointments_success() {
        // BENSON already has an appointment on 2026-10-05 09:30
        EditPatientDescriptor descriptor = new EditPatientDescriptorBuilder()
                .withAppointmentsToAdd("2026-11-03", "2026-10-05 09:30").build();
        Patient editedPatient = new PatientBuilder(BENSON).withAppointments("2026-10-05 09:30", "2026-11-03").build();

        assertEditSuccess(BENSON, descriptor, editedPatient);
    }

    @Test
    public void execute_removeAppointment_success() {
        EditPatientDescriptor descriptor = new EditPatientDescriptorBuilder()
                .withAppointmentsToRemove("2026-10-05 09:30").build();
        Patient editedPatient = new PatientBuilder(BENSON).withAppointments().build();

        assertEditSuccess(BENSON, descriptor, editedPatient);
    }

    @Test
    public void execute_removeAndAddAppointment_movesAppointment() {
        EditPatientDescriptor descriptor = new EditPatientDescriptorBuilder()
                .withAppointmentsToRemove("2026-10-05 09:30").withAppointmentsToAdd("2026-10-07 14:00").build();
        Patient editedPatient = new PatientBuilder(BENSON).withAppointments("2026-10-07 14:00").build();

        assertEditSuccess(BENSON, descriptor, editedPatient);
    }

    @Test
    public void execute_removeAndAddSameAppointment_keepsAppointment() {
        // removals happen before additions, so the appointment is still there afterwards
        EditPatientDescriptor descriptor = new EditPatientDescriptorBuilder()
                .withAppointmentsToRemove("2026-10-05 09:30").withAppointmentsToAdd("2026-10-05 09:30").build();

        assertEditSuccess(BENSON, descriptor, BENSON);
    }

    @Test
    public void execute_removeMissingAppointment_failure() {
        // BENSON's appointment has a time, so a date-only appointment on the same day does not match
        EditPatientDescriptor descriptor = new EditPatientDescriptorBuilder()
                .withAppointmentsToRemove("2026-10-05").build();
        EditCommand editCommand = new EditCommand(BENSON.getId(), descriptor);

        assertCommandFailure(editCommand, model,
                String.format(EditCommand.MESSAGE_APPOINTMENT_NOT_FOUND, BENSON.getId(), "2026-10-05"));
    }

    @Test
    public void execute_noFieldSpecified_success() {
        Patient firstPatient = model.getFilteredPatientList().get(INDEX_FIRST_PATIENT.getZeroBased());
        EditCommand editCommand = new EditCommand(firstPatient.getId(), new EditPatientDescriptor());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PATIENT_SUCCESS,
                Messages.format(firstPatient));

        Model expectedModel = new ModelManager(new LaiNurse(model.getLaiNurse()), new UserPrefs());

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_patientHiddenByFilter_success() {
        // Patients are found by ID, so a patient hidden by a filter can still be edited,
        // and the list is reset to show every patient afterwards
        showPatientAtIndex(model, INDEX_FIRST_PATIENT);
        Patient secondPatient = model.getLaiNurse().getPatientList().get(INDEX_SECOND_PATIENT.getZeroBased());
        Patient editedPatient = new PatientBuilder(secondPatient).withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(secondPatient.getId(),
                new EditPatientDescriptorBuilder().withName(VALID_NAME_BOB).build());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PATIENT_SUCCESS,
                Messages.format(editedPatient));

        Model expectedModel = new ModelManager(new LaiNurse(model.getLaiNurse()), new UserPrefs());
        expectedModel.setPatient(secondPatient, editedPatient);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_sameDetailsAsOtherPatient_successAndKeepsId() {
        // Patients are identified by ID, which edit cannot change, so copying another patient's
        // name, phone, address and medical history does not create a duplicate
        Patient firstPatient = model.getFilteredPatientList().get(INDEX_FIRST_PATIENT.getZeroBased());
        Patient secondPatient = model.getFilteredPatientList().get(INDEX_SECOND_PATIENT.getZeroBased());
        EditPatientDescriptor descriptor = new EditPatientDescriptorBuilder(firstPatient).build();
        EditCommand editCommand = new EditCommand(secondPatient.getId(), descriptor);

        PatientBuilder editedPatientBuilder = new PatientBuilder(secondPatient)
                .withName(firstPatient.getName().fullName).withPhone(firstPatient.getPhone().value)
                .withAddress(firstPatient.getAddress().value);
        firstPatient.getMedicalHistory().ifPresent(history -> editedPatientBuilder.withMedicalHistory(history.value));
        Patient editedPatient = editedPatientBuilder.build();
        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PATIENT_SUCCESS,
                Messages.format(editedPatient));

        Model expectedModel = new ModelManager(new LaiNurse(model.getLaiNurse()), new UserPrefs());
        expectedModel.setPatient(secondPatient, editedPatient);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_idNotFound_failure() {
        // VALID_ID_AMY is not one of the typical patients
        EditPatientDescriptor descriptor = new EditPatientDescriptorBuilder().withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(new PatientId(VALID_ID_AMY), descriptor);

        assertCommandFailure(editCommand, model, Messages.MESSAGE_INVALID_PATIENT_ID);
    }

    /**
     * Asserts that editing {@code patientToEdit} with {@code descriptor} succeeds and gives {@code editedPatient}.
     */
    private void assertEditSuccess(Patient patientToEdit, EditPatientDescriptor descriptor, Patient editedPatient) {
        EditCommand editCommand = new EditCommand(patientToEdit.getId(), descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PATIENT_SUCCESS,
                Messages.format(editedPatient));

        Model expectedModel = new ModelManager(new LaiNurse(model.getLaiNurse()), new UserPrefs());
        expectedModel.setPatient(patientToEdit, editedPatient);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void equals() {
        PatientId firstId = new PatientId(VALID_ID_AMY);
        PatientId secondId = new PatientId(VALID_ID_BOB);
        final EditCommand standardCommand = new EditCommand(firstId, DESC_AMY);

        // same values -> returns true
        EditPatientDescriptor copyDescriptor = new EditPatientDescriptor(DESC_AMY);
        EditCommand commandWithSameValues = new EditCommand(firstId, copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different ID -> returns false
        assertFalse(standardCommand.equals(new EditCommand(secondId, DESC_AMY)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new EditCommand(firstId, DESC_BOB)));
    }

    @Test
    public void toStringMethod() {
        PatientId id = new PatientId(VALID_ID_AMY);
        EditPatientDescriptor editPatientDescriptor = new EditPatientDescriptor();
        EditCommand editCommand = new EditCommand(id, editPatientDescriptor);
        String expected = EditCommand.class.getCanonicalName() + "{targetId=" + id + ", editPatientDescriptor="
                + editPatientDescriptor + "}";
        assertEquals(expected, editCommand.toString());
    }

}
