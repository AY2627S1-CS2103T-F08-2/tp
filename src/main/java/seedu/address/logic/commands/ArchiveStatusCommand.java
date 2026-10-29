package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.CommandResult.Navigation;
import seedu.address.logic.commands.CommandResult.View;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientId;

/**
 * Shares the validation and immutable update of whether a patient is archived.
 */
abstract class ArchiveStatusCommand extends Command {

    private static final Logger logger = LogsCenter.getLogger(ArchiveStatusCommand.class);

    private final PatientId targetId;
    private final boolean isArchiving;
    private final String commandWord;
    private final String successMessage;
    private final String alreadyInStatusMessage;

    ArchiveStatusCommand(PatientId targetId, boolean isArchiving, String commandWord, String successMessage,
            String alreadyInStatusMessage) {
        requireAllNonNull(targetId, commandWord, successMessage, alreadyInStatusMessage);
        this.targetId = targetId;
        this.isArchiving = isArchiving;
        this.commandWord = commandWord;
        this.successMessage = successMessage;
        this.alreadyInStatusMessage = alreadyInStatusMessage;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Patient patient = model.findPatientById(targetId).orElseThrow(() -> {
            logger.fine("Rejected " + commandWord + ": no patient has the given ID.");
            return new CommandException(String.format(Messages.MESSAGE_PATIENT_NOT_FOUND, targetId));
        });
        if (patient.isArchived() == isArchiving) {
            logger.fine("Rejected " + commandWord + ": the patient already has the requested status.");
            throw new CommandException(String.format(alreadyInStatusMessage, patient.getName(), targetId));
        }

        Patient updatedPatient = patient.withArchived(isArchiving);
        model.setPatient(patient, updatedPatient);
        logger.fine((isArchiving ? "Archived" : "Unarchived") + " patient " + targetId + ".");
        CommandResult result = new CommandResult(String.format(successMessage, updatedPatient.getName(), targetId));
        // The scope stays, so that archiving a patient hides them, and the patient stays selected only if it shows them
        boolean isShown = model.getFilteredPatientList().contains(updatedPatient);
        return isShown ? result.withNavigation(Navigation.toPatient(targetId)) : result.withSelectionCleared();
    }

    @Override
    public Optional<View> getView() {
        return Optional.of(View.PATIENTS);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ArchiveStatusCommand otherCommand)) {
            return false;
        }
        return isArchiving == otherCommand.isArchiving && targetId.equals(otherCommand.targetId);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetId", targetId)
                .add("isArchiving", isArchiving)
                .toString();
    }
}
