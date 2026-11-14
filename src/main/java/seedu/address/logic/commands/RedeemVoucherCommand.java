package seedu.address.logic.commands;

import java.time.LocalDate;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientId;
import seedu.address.model.patient.VoucherBalance;
import seedu.address.model.patient.VoucherNumber;

/**
 * Redeems a number of a patient's unused vouchers, such as when they pay for something with them. The patient's
 * unused voucher count first grows by the interest of every day since it last changed, and the redeemed vouchers
 * then leave it. A patient can redeem only vouchers that they hold.
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

    private static final Logger logger = LogsCenter.getLogger(RedeemVoucherCommand.class);

    /**
     * Creates a command to redeem {@code voucherNumber} of the unused vouchers of the patient with {@code targetId}.
     */
    public RedeemVoucherCommand(PatientId targetId, VoucherNumber voucherNumber) {
        super(targetId, voucherNumber);
    }

    @Override
    protected VoucherBalance applyTo(Patient patient, VoucherBalance balance, long voucherCount, LocalDate today)
            throws CommandException {
        VoucherBalance current = balance.withInterestAppliedUntil(today);
        if (!current.canRedeem(voucherCount)) {
            logger.fine("Rejected " + COMMAND_WORD + ": the patient holds fewer vouchers than are redeemed.");
            String held = Messages.formatCount(current.getCount(), "voucher");
            String requested = Messages.formatCount(voucherCount, "voucher");
            throw new CommandException(
                    String.format(MESSAGE_INSUFFICIENT, patient.getName(), patient.getId(), held, requested));
        }
        return current.redeemed(voucherCount, today);
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
