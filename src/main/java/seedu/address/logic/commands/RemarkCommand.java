package seedu.address.logic.commands;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.*;
import seedu.address.model.tag.Tag;

import java.util.*;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.*;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

/**
 * Edits the details of an existing person in the address book.
 */
public class RemarkCommand extends Command {

    public static final String COMMAND_WORD = "remark";

//    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the person identified "
//            + "by the index number used in the displayed person list. "
//            + "Existing values will be overwritten by the input values.\n"
//            + "Parameters: INDEX (must be a positive integer) "
//            + "[" + PREFIX_NAME + "NAME] "
//            + "[" + PREFIX_PHONE + "PHONE] "
//            + "[" + PREFIX_EMAIL + "EMAIL] "
//            + "[" + PREFIX_ADDRESS + "ADDRESS] "
//            + "[" + PREFIX_TAG + "TAG]...\n"
//            + "Example: " + COMMAND_WORD + " 1 "
//            + PREFIX_PHONE + "91234567 "
//            + PREFIX_EMAIL + "johndoe@example.com";
//
//    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited person: %1$s";
//    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
//    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book.";
//
//    private final Index index;
//    private final EditPersonDescriptor editPersonDescriptor;
//
//    /**
//     * @param index of the person in the filtered person list to edit
//     * @param editPersonDescriptor details to edit the person with
//     */
//    public RemarkCommand(Index index, EditPersonDescriptor editPersonDescriptor) {
//        requireNonNull(index);
//        requireNonNull(editPersonDescriptor);
//
//        this.index = index;
//        this.editPersonDescriptor = new EditPersonDescriptor(editPersonDescriptor);
//    }

    @Override
    public CommandResult execute(Model model) {
        return new CommandResult("Hello from remark");
    }

    @Override
    public String toString() {
        return "meow";
    }
}
