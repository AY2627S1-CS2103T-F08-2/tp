package seedu.address.logic.commands;

import seedu.address.model.patient.PatientId;
import seedu.address.model.patient.VoucherNumber;

/**
 * Gives a patient a number of new vouchers. The patient's unused voucher count first grows by the interest of every
 * day since it last changed, and the new vouchers then add to it. On the patient's birthday, the whole balance
 * they hold that day is doubled once for each other patient with the same birthday.
 */
public class AddVoucherCommand extends VoucherCommand {

    public static final String COMMAND_WORD = "addvoucher";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Gives the patient with the given ID a number of new "
            + "vouchers, which all have the same value. Every unused voucher grows by 10% interest each day.\n"
            + "Parameters: ID NUMBER_OF_VOUCHERS\n"
            + "Example: " + COMMAND_WORD + " S1234567A 5";

    public static final String MESSAGE_ADD_SUCCESS = "Added %1$s to %2$s (%3$s). New balance: %4$s.";

    /**
     * Creates a command to give the patient with {@code targetId} {@code voucherNumber} more vouchers.
     */
    public AddVoucherCommand(PatientId targetId, VoucherNumber voucherNumber) {
        super(targetId, voucherNumber);
    }

    @Override
    protected boolean isRedeeming() {
        return false;
    }

    @Override
    protected String getCommandWord() {
        return COMMAND_WORD;
    }

    @Override
    protected String successMessage() {
        return MESSAGE_ADD_SUCCESS;
    }

}
