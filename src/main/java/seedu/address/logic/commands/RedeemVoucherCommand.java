package seedu.address.logic.commands;

import seedu.address.model.patient.PatientId;
import seedu.address.model.patient.VoucherNumber;

/**
 * Redeems a number of a patient's unused vouchers, such as when they pay for something with them. The patient's
 * unused voucher count first grows by the interest of every day since it last changed, and the redeemed vouchers
 * then leave it. On the patient's birthday, the whole balance they hold that day is doubled once for each other
 * patient with the same birthday, and a redemption takes that doubling off the balance itself. A patient can
 * redeem only vouchers that they hold.
 */
public class RedeemVoucherCommand extends VoucherCommand {

    public static final String COMMAND_WORD = "redeemvoucher";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Redeems a number of the unused vouchers of the "
            + "patient with the given ID, which all have the same value. Every unused voucher grows by 10% interest "
            + "each day, so a balance can be a fraction, such as 11.2 vouchers.\n"
            + "Parameters: ID NUMBER_OF_VOUCHERS\n"
            + "Example: " + COMMAND_WORD + " S1234567A 2";

    public static final String MESSAGE_REDEEM_SUCCESS = "Redeemed %1$s from %2$s (%3$s). New balance: %4$s.";

    public static final String MESSAGE_INSUFFICIENT = "%1$s (%2$s) has only %3$s to redeem, which is fewer than %4$s.";

    /**
     * Creates a command to redeem {@code voucherNumber} of the unused vouchers of the patient with {@code targetId}.
     */
    public RedeemVoucherCommand(PatientId targetId, VoucherNumber voucherNumber) {
        super(targetId, voucherNumber);
    }

    @Override
    protected boolean isRedeeming() {
        return true;
    }

    @Override
    protected String getCommandWord() {
        return COMMAND_WORD;
    }

    @Override
    protected String successMessage() {
        return MESSAGE_REDEEM_SUCCESS;
    }

}
