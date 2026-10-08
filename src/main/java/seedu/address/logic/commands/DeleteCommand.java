package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.PatientId;
import seedu.address.model.person.Person;

/**
 * Deletes a person identified using its displayed ID from the address book.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the person with the given ID number.\n"
            + "Parameters: ID\n"
            + "Example: " + COMMAND_WORD + " T0123456B";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted person: %1$s";

    private final PatientId targetId;

    /**
     * Creates a DeleteCommand to delete the patient with the specified {@code targetId}.
     */
    public DeleteCommand(PatientId targetId) {
        requireNonNull(targetId);
        this.targetId = targetId;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> listAllPersons = model.getAddressBook().getPersonList();

        Person personToDelete = listAllPersons.stream().filter(person -> person.getId().equals(targetId))
                        .findFirst().orElseThrow(() -> new CommandException(Messages.MESSAGE_INVALID_PERSON_ID));
        model.deletePerson(personToDelete);
        return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(personToDelete)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteCommand otherDeleteCommand)) {
            return false;
        }

        return targetId.equals(otherDeleteCommand.targetId);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetId", targetId)
                .toString();
    }
}
