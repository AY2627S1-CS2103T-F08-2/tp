package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.PatientId;
import seedu.address.model.person.Person;
import seedu.address.model.person.Remark;

/**
 * Changes the remark of an existing person in the address book.
 */
public class RemarkCommand extends Command {

    public static final String COMMAND_WORD = "remark";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Edits the remark of the person with the given ID. "
            + "Existing remark will be overwritten by the input. Leave the remark empty to remove it.\n"
            + "Parameters: ID [REMARK]\n"
            + "Example: " + COMMAND_WORD + " T0123456B Likes to swim.";

    public static final String MESSAGE_ADD_REMARK_SUCCESS = "Added remark to person: %1$s";
    public static final String MESSAGE_DELETE_REMARK_SUCCESS = "Removed remark from person: %1$s";

    private final PatientId targetId;
    private final Remark remark;

    /**
     * Creates a RemarkCommand to set the remark of the person with the specified {@code targetId}.
     */
    public RemarkCommand(PatientId targetId, Remark remark) {
        requireAllNonNull(targetId, remark);
        this.targetId = targetId;
        this.remark = remark;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        List<Person> listAllPersons = model.getAddressBook().getPersonList();

        Person personToEdit = listAllPersons.stream().filter(person -> person.getId().equals(targetId))
                .findFirst().orElseThrow(() -> new CommandException(Messages.MESSAGE_INVALID_PERSON_ID));
        Person editedPerson = new Person(personToEdit.getId(), personToEdit.getName(), personToEdit.getPhone(),
                personToEdit.getAddress(), personToEdit.getMedicalHistory(), personToEdit.getNextAppointment(),
                remark);

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        return new CommandResult(generateSuccessMessage(editedPerson));
    }

    /**
     * Returns the success message for adding or removing the remark of {@code personToEdit}.
     */
    private String generateSuccessMessage(Person personToEdit) {
        String message = remark.isEmpty() ? MESSAGE_DELETE_REMARK_SUCCESS : MESSAGE_ADD_REMARK_SUCCESS;
        return String.format(message, Messages.format(personToEdit));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof RemarkCommand otherRemarkCommand)) {
            return false;
        }

        return targetId.equals(otherRemarkCommand.targetId)
                && remark.equals(otherRemarkCommand.remark);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetId", targetId)
                .add("remark", remark)
                .toString();
    }
}
