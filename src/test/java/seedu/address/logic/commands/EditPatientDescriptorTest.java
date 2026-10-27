package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_MEDICAL_HISTORY_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.EditCommand.EditPatientDescriptor;
import seedu.address.model.patient.Notes;
import seedu.address.testutil.EditPatientDescriptorBuilder;
import seedu.address.testutil.PatientBuilder;

public class EditPatientDescriptorTest {

    @Test
    public void equals() {
        // same values -> returns true
        EditPatientDescriptor descriptorWithSameValues = new EditPatientDescriptor(DESC_AMY);
        assertTrue(DESC_AMY.equals(descriptorWithSameValues));

        // same object -> returns true
        assertTrue(DESC_AMY.equals(DESC_AMY));

        // null -> returns false
        assertFalse(DESC_AMY.equals(null));

        // different types -> returns false
        assertFalse(DESC_AMY.equals(5));

        // different values -> returns false
        assertFalse(DESC_AMY.equals(DESC_BOB));

        // different name -> returns false
        EditPatientDescriptor editedAmy = new EditPatientDescriptorBuilder(DESC_AMY).withName(VALID_NAME_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different phone -> returns false
        editedAmy = new EditPatientDescriptorBuilder(DESC_AMY).withPhone(VALID_PHONE_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different address -> returns false
        editedAmy = new EditPatientDescriptorBuilder(DESC_AMY).withAddress(VALID_ADDRESS_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different medical history -> returns false
        editedAmy = new EditPatientDescriptorBuilder(DESC_AMY).withMedicalHistory(VALID_MEDICAL_HISTORY_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different notes -> returns false
        editedAmy = new EditPatientDescriptorBuilder(DESC_AMY).withNotes("Enjoys gardening").build();
        assertFalse(DESC_AMY.equals(editedAmy));
    }

    @Test
    public void notesOnlyDescriptor_isEditedAndCopied() {
        EditPatientDescriptor original = new EditPatientDescriptorBuilder().withNotes("Enjoys gardening").build();
        EditPatientDescriptor copy = new EditPatientDescriptor(original);
        assertTrue(copy.isAnyFieldEdited());
        assertEquals(original, copy);
        assertEquals(OptionalFieldEdit.replace(new Notes("Enjoys gardening")), copy.getNotes().orElseThrow());

        original.setNotes(OptionalFieldEdit.replace(new Notes("Walks every morning")));
        assertEquals(OptionalFieldEdit.replace(new Notes("Enjoys gardening")), copy.getNotes().orElseThrow());
        assertFalse(original.equals(copy));
    }

    @Test
    public void builderFromPatient_copiesNotes() {
        EditPatientDescriptor descriptor = new EditPatientDescriptorBuilder(
                new PatientBuilder().withNotes("Enjoys gardening").build()).build();
        assertEquals(OptionalFieldEdit.replace(new Notes("Enjoys gardening")), descriptor.getNotes().orElseThrow());
    }

    @Test
    public void toStringMethod() {
        EditPatientDescriptor editPatientDescriptor = new EditPatientDescriptor();
        String expected = EditPatientDescriptor.class.getCanonicalName() + "{name="
                + editPatientDescriptor.getName().orElse(null) + ", phone="
                + editPatientDescriptor.getPhone().orElse(null) + ", address="
                + editPatientDescriptor.getAddress().orElse(null) + ", medicalHistory="
                + editPatientDescriptor.getMedicalHistory().orElse(null) + ", notes="
                + editPatientDescriptor.getNotes().orElse(null) + "}";
        assertEquals(expected, editPatientDescriptor.toString());
    }
}
