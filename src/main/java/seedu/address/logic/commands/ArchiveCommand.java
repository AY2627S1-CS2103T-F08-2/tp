package seedu.address.logic.commands;

import seedu.address.model.patient.PatientId;

/**
 * Archives a patient, such as one who has been discharged. The patient keeps every detail, but is hidden unless the
 * patient list or the appointment view shows archived patients, and their appointments do not clash.
 */
public class ArchiveCommand extends ArchiveStatusCommand {

    public static final String COMMAND_WORD = "archive";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Archives the patient with the given ID, such as one "
            + "who has been discharged. An archived patient keeps every detail, and every command that takes their ID "
            + "still works on them, but they are hidden unless you give --all or --archived, and their appointments "
            + "do not clash.\n"
            + "Parameters: ID\n"
            + "Example: " + COMMAND_WORD + " S1234567A";

    public static final String MESSAGE_ARCHIVE_SUCCESS = "Archived %1$s (%2$s). To see archived patients, "
            + "use 'view --all' or 'view --archived'.";
    public static final String MESSAGE_ALREADY_ARCHIVED = "%1$s (%2$s) is already archived.";

    /**
     * Creates a command to archive the patient with {@code targetId}.
     */
    public ArchiveCommand(PatientId targetId) {
        super(targetId, true, COMMAND_WORD, MESSAGE_ARCHIVE_SUCCESS, MESSAGE_ALREADY_ARCHIVED);
    }
}
