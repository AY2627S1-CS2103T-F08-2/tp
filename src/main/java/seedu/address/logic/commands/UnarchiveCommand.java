package seedu.address.logic.commands;

import seedu.address.model.patient.PatientId;

/**
 * Unarchives a patient, so that they show again with the other patients, and their appointments can clash again.
 */
public class UnarchiveCommand extends ArchiveStatusCommand {

    public static final String COMMAND_WORD = "unarchive";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Unarchives the patient with the given ID, so that "
            + "they show again with the other patients.\n"
            + "Parameters: ID\n"
            + "Example: " + COMMAND_WORD + " S1234567A";

    public static final String MESSAGE_UNARCHIVE_SUCCESS = "Unarchived %1$s (%2$s).";
    public static final String MESSAGE_NOT_ARCHIVED = "%1$s (%2$s) is not archived.";

    /**
     * Creates a command to unarchive the patient with {@code targetId}.
     */
    public UnarchiveCommand(PatientId targetId) {
        super(targetId, false, COMMAND_WORD, MESSAGE_UNARCHIVE_SUCCESS, MESSAGE_NOT_ARCHIVED);
    }
}
