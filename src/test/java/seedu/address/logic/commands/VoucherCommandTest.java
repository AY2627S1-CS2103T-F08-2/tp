package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPatients.BENSON;
import static seedu.address.testutil.TypicalPatients.CARL;
import static seedu.address.testutil.TypicalPatients.getTypicalLaiNurse;

import java.time.DateTimeException;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.CommandResult.Navigation;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientId;
import seedu.address.model.patient.VoucherBalance;
import seedu.address.model.patient.VoucherNumber;
import seedu.address.testutil.PatientBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code AddVoucherCommand} and
 * {@code RedeemVoucherCommand}. Every patient holds their vouchers from today, so that no interest is applied
 * in these tests; {@code VoucherBalanceTest} covers the interest itself.
 */
public class VoucherCommandTest {

    private static final VoucherNumber FIVE = new VoucherNumber("5");
    private static final VoucherNumber FOUR = new VoucherNumber("4");
    private static final VoucherNumber FIFTEEN = new VoucherNumber("15");

    private final Model model = new ModelManager(getTypicalLaiNurse(), new UserPrefs());

    /**
     * Returns BENSON, who holds 10 vouchers from today, so that the commands see them with no interest.
     */
    private static Patient bensonWithTenVouchers() {
        return BENSON.withVoucherBalance(new VoucherBalance(10, LocalDate.now()));
    }

    /**
     * Returns the count that {@code count} vouchers grow to over {@code daysElapsed} days of interest, at 10% a day.
     */
    private static double grownCount(double count, long daysElapsed) {
        return count * Math.pow(VoucherBalance.DAILY_INTEREST_MULTIPLIER, daysElapsed);
    }

    @Test
    public void constructor_nullFields_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddVoucherCommand(null, FIVE));
        assertThrows(NullPointerException.class, () -> new AddVoucherCommand(BENSON.getId(), null));
        assertThrows(NullPointerException.class, () -> new RedeemVoucherCommand(null, FIVE));
        assertThrows(NullPointerException.class, () -> new RedeemVoucherCommand(BENSON.getId(), null));
    }

    @Test
    public void execute_addVouchers_patientGainsThemAndIsSelected() {
        Patient benson = bensonWithTenVouchers();
        model.setPatient(BENSON, benson);
        Patient expectedBenson = benson.withVoucherBalance(new VoucherBalance(15, LocalDate.now()));
        Model expectedModel = new ModelManager(getTypicalLaiNurse(), new UserPrefs());
        expectedModel.setPatient(BENSON, expectedBenson);

        String expectedMessage = String.format(AddVoucherCommand.MESSAGE_ADD_SUCCESS,
                Messages.formatCount(5, "voucher"), BENSON.getName(), BENSON.getId(),
                Messages.formatCount(15, "voucher"));
        assertCommandSuccess(new AddVoucherCommand(BENSON.getId(), FIVE), model,
                new CommandResult(expectedMessage).withNavigation(Navigation.toPatient(BENSON.getId())),
                expectedModel);
        assertEquals(expectedBenson, model.findPatientById(BENSON.getId()).orElseThrow());
    }

    @Test
    public void execute_redeemVouchers_patientLosesThemAndIsSelected() {
        Patient benson = bensonWithTenVouchers();
        model.setPatient(BENSON, benson);
        Patient expectedBenson = benson.withVoucherBalance(new VoucherBalance(6, LocalDate.now()));
        Model expectedModel = new ModelManager(getTypicalLaiNurse(), new UserPrefs());
        expectedModel.setPatient(BENSON, expectedBenson);

        String expectedMessage = String.format(RedeemVoucherCommand.MESSAGE_REDEEM_SUCCESS,
                Messages.formatCount(4, "voucher"), BENSON.getName(), BENSON.getId(),
                Messages.formatCount(6, "voucher"));
        assertCommandSuccess(new RedeemVoucherCommand(BENSON.getId(), FOUR), model,
                new CommandResult(expectedMessage).withNavigation(Navigation.toPatient(BENSON.getId())),
                expectedModel);
        assertEquals(expectedBenson, model.findPatientById(BENSON.getId()).orElseThrow());
    }

    @Test
    public void execute_addToPatientWithoutVouchers_patientGainsThem() {
        Patient expectedCarl = CARL.withVoucherBalance(new VoucherBalance(5, LocalDate.now()));
        Model expectedModel = new ModelManager(getTypicalLaiNurse(), new UserPrefs());
        expectedModel.setPatient(CARL, expectedCarl);

        String expectedMessage = String.format(AddVoucherCommand.MESSAGE_ADD_SUCCESS,
                Messages.formatCount(5, "voucher"), CARL.getName(), CARL.getId(),
                Messages.formatCount(5, "voucher"));
        assertCommandSuccess(new AddVoucherCommand(CARL.getId(), FIVE), model,
                new CommandResult(expectedMessage).withNavigation(Navigation.toPatient(CARL.getId())),
                expectedModel);
    }

    @Test
    public void execute_redeemAllVouchers_balanceBecomesZero() {
        Patient benson = bensonWithTenVouchers();
        model.setPatient(BENSON, benson);
        Patient expectedBenson = benson.withVoucherBalance(VoucherBalance.ZERO);
        Model expectedModel = new ModelManager(getTypicalLaiNurse(), new UserPrefs());
        expectedModel.setPatient(BENSON, expectedBenson);

        String expectedMessage = String.format(RedeemVoucherCommand.MESSAGE_REDEEM_SUCCESS,
                Messages.formatCount(10, "voucher"), BENSON.getName(), BENSON.getId(),
                Messages.formatCount(0, "voucher"));
        assertCommandSuccess(new RedeemVoucherCommand(BENSON.getId(), new VoucherNumber("10")), model,
                new CommandResult(expectedMessage).withNavigation(Navigation.toPatient(BENSON.getId())),
                expectedModel);
        assertEquals(expectedBenson, model.findPatientById(BENSON.getId()).orElseThrow());
    }

    @Test
    public void execute_redeemSingleVoucher_messageIsSingular() {
        Patient benson = BENSON.withVoucherBalance(new VoucherBalance(1, LocalDate.now()));
        model.setPatient(BENSON, benson);
        Patient expectedBenson = benson.withVoucherBalance(VoucherBalance.ZERO);
        Model expectedModel = new ModelManager(getTypicalLaiNurse(), new UserPrefs());
        expectedModel.setPatient(BENSON, expectedBenson);

        String expectedMessage = String.format(RedeemVoucherCommand.MESSAGE_REDEEM_SUCCESS,
                Messages.formatCount(1, "voucher"), BENSON.getName(), BENSON.getId(),
                Messages.formatCount(0, "voucher"));
        assertCommandSuccess(new RedeemVoucherCommand(BENSON.getId(), new VoucherNumber("1")), model,
                new CommandResult(expectedMessage).withNavigation(Navigation.toPatient(BENSON.getId())),
                expectedModel);
    }

    @Test
    public void execute_redeemMoreThanHeld_throwsCommandException() {
        Patient benson = bensonWithTenVouchers();
        model.setPatient(BENSON, benson);

        String expectedMessage = String.format(RedeemVoucherCommand.MESSAGE_INSUFFICIENT, BENSON.getName(),
                BENSON.getId(), Messages.formatCount(10, "voucher"), Messages.formatCount(15, "voucher"));
        assertCommandFailure(new RedeemVoucherCommand(BENSON.getId(), FIFTEEN), model, expectedMessage);
        assertEquals(benson, model.findPatientById(BENSON.getId()).orElseThrow());
    }

    @Test
    public void execute_redeemWithoutVouchers_throwsCommandException() {
        String expectedMessage = String.format(RedeemVoucherCommand.MESSAGE_INSUFFICIENT, CARL.getName(),
                CARL.getId(), Messages.formatCount(0, "voucher"), Messages.formatCount(5, "voucher"));
        assertCommandFailure(new RedeemVoucherCommand(CARL.getId(), FIVE), model, expectedMessage);
    }

    @Test
    public void execute_unknownId_throwsCommandException() {
        PatientId unknownId = new PatientId("T9999999Z");
        assertCommandFailure(new AddVoucherCommand(unknownId, FIVE), model,
                String.format(Messages.MESSAGE_PATIENT_NOT_FOUND, unknownId));
        assertCommandFailure(new RedeemVoucherCommand(unknownId, FIVE), model,
                String.format(Messages.MESSAGE_PATIENT_NOT_FOUND, unknownId));
    }

    @Test
    public void execute_onArchivedPatient_changesThemAndClearsSelection() {
        Patient benson = bensonWithTenVouchers().withArchived(true);
        model.setPatient(BENSON, benson);
        Patient expectedBenson = benson.withVoucherBalance(new VoucherBalance(15, LocalDate.now()));
        Model expectedModel = new ModelManager(getTypicalLaiNurse(), new UserPrefs());
        expectedModel.setPatient(BENSON, expectedBenson);

        String expectedMessage = String.format(AddVoucherCommand.MESSAGE_ADD_SUCCESS,
                Messages.formatCount(5, "voucher"), BENSON.getName(), BENSON.getId(),
                Messages.formatCount(15, "voucher"));
        // the default scope hides archived patients, so they are not selected
        assertCommandSuccess(new AddVoucherCommand(BENSON.getId(), FIVE), model,
                new CommandResult(expectedMessage).withSelectionCleared(), expectedModel);
        assertFalse(model.getFilteredPatientList().contains(expectedBenson));
    }

    @Test
    public void execute_onShownPatient_selectsPatient() {
        Patient expectedCarl = CARL.withVoucherBalance(new VoucherBalance(5, LocalDate.now()));
        Model expectedModel = new ModelManager(getTypicalLaiNurse(), new UserPrefs());
        expectedModel.setPatient(CARL, expectedCarl);

        assertCommandSuccess(new AddVoucherCommand(CARL.getId(), FIVE), model,
                new CommandResult(String.format(AddVoucherCommand.MESSAGE_ADD_SUCCESS,
                        Messages.formatCount(5, "voucher"), CARL.getName(), CARL.getId(),
                        Messages.formatCount(5, "voucher")))
                        .withNavigation(Navigation.toPatient(CARL.getId())),
                expectedModel);
        assertTrue(model.getFilteredPatientList().contains(expectedCarl));
    }

    @Test
    public void execute_addVouchers_appliesInterestOfPastDaysFirst() {
        // BENSON's 10 vouchers were given two days ago, so they have grown to 12.1, and the 5 new ones add to that
        long daysElapsed = 2;
        Patient benson = BENSON.withVoucherBalance(
                new VoucherBalance(10, LocalDate.now().minusDays(daysElapsed)));
        model.setPatient(BENSON, benson);
        double grownCount = grownCount(10, daysElapsed);
        Patient expectedBenson = benson.withVoucherBalance(new VoucherBalance(grownCount + 5, LocalDate.now()));
        Model expectedModel = new ModelManager(getTypicalLaiNurse(), new UserPrefs());
        expectedModel.setPatient(BENSON, expectedBenson);

        String expectedMessage = String.format(AddVoucherCommand.MESSAGE_ADD_SUCCESS,
                Messages.formatCount(5, "voucher"), BENSON.getName(), BENSON.getId(),
                Messages.formatCount(grownCount + 5, "voucher"));
        assertCommandSuccess(new AddVoucherCommand(BENSON.getId(), FIVE), model,
                new CommandResult(expectedMessage).withNavigation(Navigation.toPatient(BENSON.getId())),
                expectedModel);
        assertEquals(17.1, expectedBenson.getVoucherBalance().getCount(), 1e-9);
    }

    @Test
    public void execute_redeemVouchers_appliesInterestOfPastDaysFirst() {
        // BENSON's 10 vouchers were given a day ago, so they have grown to 11, of which 4 are redeemed
        long daysElapsed = 1;
        Patient benson = BENSON.withVoucherBalance(
                new VoucherBalance(10, LocalDate.now().minusDays(daysElapsed)));
        model.setPatient(BENSON, benson);
        double grownCount = grownCount(10, daysElapsed);
        Patient expectedBenson = benson.withVoucherBalance(
                new VoucherBalance(grownCount - 4, LocalDate.now()));
        Model expectedModel = new ModelManager(getTypicalLaiNurse(), new UserPrefs());
        expectedModel.setPatient(BENSON, expectedBenson);

        String expectedMessage = String.format(RedeemVoucherCommand.MESSAGE_REDEEM_SUCCESS,
                Messages.formatCount(4, "voucher"), BENSON.getName(), BENSON.getId(),
                Messages.formatCount(grownCount - 4, "voucher"));
        assertCommandSuccess(new RedeemVoucherCommand(BENSON.getId(), FOUR), model,
                new CommandResult(expectedMessage).withNavigation(Navigation.toPatient(BENSON.getId())),
                expectedModel);
        assertEquals(7, expectedBenson.getVoucherBalance().getCount(), 1e-9);
    }

    @Test
    public void execute_redeemMoreThanGrownCount_throwsCommandException() {
        // interest grows the balance, but not past the vouchers the patient asks for
        long daysElapsed = 1;
        Patient benson = BENSON.withVoucherBalance(
                new VoucherBalance(10, LocalDate.now().minusDays(daysElapsed)));
        model.setPatient(BENSON, benson);

        String expectedMessage = String.format(RedeemVoucherCommand.MESSAGE_INSUFFICIENT, BENSON.getName(),
                BENSON.getId(), Messages.formatCount(grownCount(10, daysElapsed), "voucher"),
                Messages.formatCount(15, "voucher"));
        assertCommandFailure(new RedeemVoucherCommand(BENSON.getId(), FIFTEEN), model, expectedMessage);
    }

    /**
     * Returns the date of birth of a patient whose birthday is today, in some past year.
     */
    private static String birthdayToday() {
        LocalDate today = LocalDate.now();
        try {
            return LocalDate.of(1990, today.getMonth(), today.getDayOfMonth()).toString();
        } catch (DateTimeException february29) {
            return LocalDate.of(1992, today.getMonth(), today.getDayOfMonth()).toString();
        }
    }

    /**
     * Sets BENSON and CARL up as the only two patients born on this day, with Benson holding 10 vouchers from
     * today, so that Benson's vouchers double today.
     */
    private void setBirthdaySharersUp() {
        Patient benson = new PatientBuilder(BENSON).withDateOfBirth(birthdayToday())
                .withVoucherBalance(new VoucherBalance(10, LocalDate.now())).build();
        Patient carl = new PatientBuilder(CARL).withDateOfBirth(birthdayToday()).build();
        model.setPatient(BENSON, benson);
        model.setPatient(CARL, carl);
    }

    @Test
    public void execute_addVouchersOnBirthday_doublesTheBalanceThatDay() {
        // Benson and Carl share this birthday, so the 15 vouchers Benson holds after the adding show as 30
        setBirthdaySharersUp();
        Patient benson = model.findPatientById(BENSON.getId()).orElseThrow();
        Patient carl = model.findPatientById(CARL.getId()).orElseThrow();
        Patient expectedBenson = benson.withVoucherBalance(new VoucherBalance(15, LocalDate.now()));
        Model expectedModel = new ModelManager(getTypicalLaiNurse(), new UserPrefs());
        expectedModel.setPatient(BENSON, expectedBenson);
        expectedModel.setPatient(CARL, carl);

        String expectedMessage = String.format(AddVoucherCommand.MESSAGE_ADD_SUCCESS,
                Messages.formatCount(5, "voucher"), BENSON.getName(), BENSON.getId(),
                Messages.formatCount(30, "voucher"));
        assertCommandSuccess(new AddVoucherCommand(BENSON.getId(), FIVE), model,
                new CommandResult(expectedMessage).withNavigation(Navigation.toPatient(BENSON.getId())),
                expectedModel);
        // the doubling belongs to the day alone: the balance that is stored holds no doubled amount
        assertEquals(15, model.findPatientById(BENSON.getId()).orElseThrow().getVoucherBalance().getCount(), 0);
    }

    @Test
    public void execute_redeemOnBirthday_spendsTheDoubledVouchers() {
        // Benson's 10 vouchers hold as 20 today, and redeeming 6 of them takes 3 from the balance itself
        setBirthdaySharersUp();
        Patient benson = model.findPatientById(BENSON.getId()).orElseThrow();
        Patient carl = model.findPatientById(CARL.getId()).orElseThrow();
        Patient expectedBenson = benson.withVoucherBalance(new VoucherBalance(7, LocalDate.now()));
        Model expectedModel = new ModelManager(getTypicalLaiNurse(), new UserPrefs());
        expectedModel.setPatient(BENSON, expectedBenson);
        expectedModel.setPatient(CARL, carl);

        String expectedMessage = String.format(RedeemVoucherCommand.MESSAGE_REDEEM_SUCCESS,
                Messages.formatCount(6, "voucher"), BENSON.getName(), BENSON.getId(),
                Messages.formatCount(14, "voucher"));
        assertCommandSuccess(new RedeemVoucherCommand(BENSON.getId(), new VoucherNumber("6")), model,
                new CommandResult(expectedMessage).withNavigation(Navigation.toPatient(BENSON.getId())),
                expectedModel);
        assertEquals(7, model.findPatientById(BENSON.getId()).orElseThrow().getVoucherBalance().getCount(), 0);
    }

    @Test
    public void execute_redeemMoreThanTheDoubledBalance_throwsCommandException() {
        // the doubled balance of 20 covers 21 vouchers by none, and the error names the doubled balance
        setBirthdaySharersUp();

        String expectedMessage = String.format(RedeemVoucherCommand.MESSAGE_INSUFFICIENT, BENSON.getName(),
                BENSON.getId(), Messages.formatCount(20, "voucher"), Messages.formatCount(21, "voucher"));
        assertCommandFailure(new RedeemVoucherCommand(BENSON.getId(), new VoucherNumber("21")), model,
                expectedMessage);
    }

    @Test
    public void execute_onABirthdayWithoutSharers_theBalanceDoesNotDouble() {
        // Carl is the only patient born on this day, so no one doubles his vouchers
        Patient carl = new PatientBuilder(CARL).withDateOfBirth(birthdayToday()).build();
        model.setPatient(CARL, carl);
        Patient expectedCarl = carl.withVoucherBalance(new VoucherBalance(5, LocalDate.now()));
        Model expectedModel = new ModelManager(getTypicalLaiNurse(), new UserPrefs());
        expectedModel.setPatient(CARL, expectedCarl);

        String expectedMessage = String.format(AddVoucherCommand.MESSAGE_ADD_SUCCESS,
                Messages.formatCount(5, "voucher"), CARL.getName(), CARL.getId(),
                Messages.formatCount(5, "voucher"));
        assertCommandSuccess(new AddVoucherCommand(CARL.getId(), FIVE), model,
                new CommandResult(expectedMessage).withNavigation(Navigation.toPatient(CARL.getId())),
                expectedModel);
    }

    @Test
    public void equals() {
        AddVoucherCommand addFiveToBenson = new AddVoucherCommand(BENSON.getId(), FIVE);

        assertTrue(addFiveToBenson.equals(addFiveToBenson));
        assertTrue(addFiveToBenson.equals(new AddVoucherCommand(BENSON.getId(), new VoucherNumber("05"))));
        assertFalse(addFiveToBenson.equals(null));
        assertFalse(addFiveToBenson.equals(1));
        assertFalse(addFiveToBenson.equals(new AddVoucherCommand(CARL.getId(), FIVE)));
        assertFalse(addFiveToBenson.equals(new AddVoucherCommand(BENSON.getId(), new VoucherNumber("6"))));
        // adding and redeeming the same number from the same patient are different commands
        assertNotEquals(addFiveToBenson, new RedeemVoucherCommand(BENSON.getId(), FIVE));
    }

    @Test
    public void getView_patients() {
        assertEquals(CommandResult.View.PATIENTS, new AddVoucherCommand(BENSON.getId(), FIVE).getView().orElseThrow());
        assertEquals(CommandResult.View.PATIENTS,
                new RedeemVoucherCommand(BENSON.getId(), FIVE).getView().orElseThrow());
    }

    @Test
    public void toStringMethod() {
        assertEquals(AddVoucherCommand.class.getCanonicalName() + "{targetId=" + BENSON.getId()
                + ", voucherNumber=5}", new AddVoucherCommand(BENSON.getId(), FIVE).toString());
        assertEquals(RedeemVoucherCommand.class.getCanonicalName() + "{targetId=" + BENSON.getId()
                + ", voucherNumber=5}", new RedeemVoucherCommand(BENSON.getId(), FIVE).toString());
    }

}
