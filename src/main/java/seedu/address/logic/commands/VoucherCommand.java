package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.LocalDate;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.CommandResult.Navigation;
import seedu.address.logic.commands.CommandResult.View;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.patient.BirthdayBonus;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientId;
import seedu.address.model.patient.VoucherBalance;
import seedu.address.model.patient.VoucherNumber;

/**
 * Shares the lookup, the daily interest, the birthday bonus and the immutable update behind adding and redeeming
 * vouchers. Both commands work on any patient, archived ones included, and leave the patient unchanged when they
 * fail.
 */
abstract class VoucherCommand extends Command {

    private static final Logger logger = LogsCenter.getLogger(VoucherCommand.class);

    private final PatientId targetId;
    private final VoucherNumber voucherNumber;

    VoucherCommand(PatientId targetId, VoucherNumber voucherNumber) {
        requireAllNonNull(targetId, voucherNumber);
        this.targetId = targetId;
        this.voucherNumber = voucherNumber;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Patient patient = model.findPatientById(targetId).orElseThrow(() -> {
            logger.fine("Rejected " + getCommandWord() + ": no patient has the given ID.");
            return new CommandException(String.format(Messages.MESSAGE_PATIENT_NOT_FOUND, targetId));
        });

        // The interest of every day since the balance last changed is applied before the change is made, and on
        // the patient's birthday, each other patient with the same birthday doubles the vouchers they hold
        LocalDate today = LocalDate.now();
        double birthdayFactor = BirthdayBonus.voucherMultiplier(patient,
                model.getLaiNurse().getPatientList(), today);
        VoucherBalance updatedBalance = applyChange(patient, birthdayFactor, today);
        Patient updatedPatient = patient.withVoucherBalance(updatedBalance);
        model.setPatient(patient, updatedPatient);
        logger.fine(getCommandWord() + " " + voucherNumber + " for patient " + targetId + ".");

        // The balance the patient holds that day carries the birthday bonus, as their details show it
        String newBalance = Messages.formatCount(
                VoucherBalance.multipliedCount(updatedBalance.getCount(), birthdayFactor), "voucher");
        String resultMessage = String.format(successMessage(), Messages.formatCount(voucherNumber.value, "voucher"),
                patient.getName(), targetId, newBalance);
        CommandResult result = new CommandResult(resultMessage);
        // The patient is selected only if the list still shows them, as an archived one may be hidden
        boolean isShown = model.getFilteredPatientList().contains(updatedPatient);
        return isShown ? result.withNavigation(Navigation.toPatient(targetId)) : result.withSelectionCleared();
    }

    /**
     * Returns the patient's balance after the change, as of {@code today}, where {@code birthdayFactor} is the
     * factor that the vouchers the patient holds that day multiply by.
     *
     * @throws CommandException if the change cannot be made, such as redeeming more vouchers than the patient
     *         holds that day.
     */
    private VoucherBalance applyChange(Patient patient, double birthdayFactor, LocalDate today)
            throws CommandException {
        VoucherBalance current = patient.getVoucherBalance().withInterestAppliedUntil(today);
        if (!isRedeeming()) {
            return current.added(voucherNumber.value, today);
        }
        // The doubled vouchers of a birthday are spent from the balance they doubled, so a redemption of the
        // vouchers the patient holds that day takes a factor-th of the balance itself
        double balanceNeeded = voucherNumber.value / birthdayFactor;
        if (!current.canRedeem(balanceNeeded)) {
            logger.fine("Rejected " + getCommandWord() + ": the patient holds fewer vouchers than are redeemed.");
            String held = Messages.formatCount(
                    VoucherBalance.multipliedCount(current.getCount(), birthdayFactor), "voucher");
            throw new CommandException(String.format(RedeemVoucherCommand.MESSAGE_INSUFFICIENT, patient.getName(),
                    patient.getId(), held, Messages.formatCount(voucherNumber.value, "voucher")));
        }
        return current.redeemed(balanceNeeded, today);
    }

    /** Returns true if the command redeems vouchers from the patient, instead of giving them. */
    protected abstract boolean isRedeeming();

    /** Returns the command word, to name the command in log messages. */
    protected abstract String getCommandWord();

    /** Returns the success message, with a place for the number of vouchers, the patient and their new balance. */
    protected abstract String successMessage();

    @Override
    public Optional<View> getView() {
        return Optional.of(View.PATIENTS);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof VoucherCommand otherCommand)) {
            return false;
        }

        // An adding and a redeeming command for the same patient and number are different commands
        return otherCommand.getCommandWord().equals(getCommandWord())
                && targetId.equals(otherCommand.targetId)
                && voucherNumber.equals(otherCommand.voucherNumber);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetId", targetId)
                .add("voucherNumber", voucherNumber)
                .toString();
    }

}
