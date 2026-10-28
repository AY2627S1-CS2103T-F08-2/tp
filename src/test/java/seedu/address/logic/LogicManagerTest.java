package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.logic.Messages.MESSAGE_PATIENT_NOT_FOUND;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.ID_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPatients.AMY;
import static seedu.address.testutil.TypicalPatients.BOB;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.CommandResult.AppointmentRange;
import seedu.address.logic.commands.CommandResult.Navigation;
import seedu.address.logic.commands.CommandResult.View;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.Completion;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyLaiNurse;
import seedu.address.model.UserPrefs;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientAppointment;
import seedu.address.model.patient.PatientId;
import seedu.address.storage.JsonLaiNurseStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PatientBuilder;

public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy IO exception");
    private static final IOException DUMMY_AD_EXCEPTION = new AccessDeniedException("dummy access denied exception");

    @TempDir
    public Path temporaryFolder;

    private Model model = new ModelManager();
    private Logic logic;

    @BeforeEach
    public void setUp() {
        JsonLaiNurseStorage laiNurseStorage =
                new JsonLaiNurseStorage(temporaryFolder.resolve("laiNurse.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(laiNurseStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand, MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteCommand = "delete T9999999Z";
        assertCommandException(deleteCommand, String.format(MESSAGE_PATIENT_NOT_FOUND, "T9999999Z"));
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD;
        assertCommandSuccess(listCommand, ListCommand.MESSAGE_SUCCESS, model);
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_IO_EXCEPTION, String.format(
                LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_storageThrowsAdException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_AD_EXCEPTION, String.format(
                LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, DUMMY_AD_EXCEPTION.getMessage()));
    }

    @Test
    public void showAllPatients_filteredList_showsAllWithoutSaving() {
        model.addPatient(BOB);
        model.addPatient(AMY);
        model.updateFilteredPatientList(patient -> patient.isSamePatient(BOB));

        logic.showAllPatients();

        assertEquals(List.of(AMY, BOB), logic.getFilteredPatientList()); // in name order
        // no command ran, so nothing was saved
        assertFalse(Files.exists(temporaryFolder.resolve("laiNurse.json")));
    }

    @Test
    public void getFilteredPatientList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredPatientList().remove(0));
    }

    @Test
    public void getPatientList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getPatientList().remove(0));
    }

    @Test
    public void execute_eachCommand_resultNamesTheViewItShows() throws Exception {
        // commands that list or change patients show the patient view
        assertEquals(Optional.of(View.PATIENTS), logic.execute(ListCommand.COMMAND_WORD).getView());
        assertEquals(Optional.of(View.PATIENTS), logic.execute("find --name Amy").getView());
        // a command word that ends in "appt" shows the appointment view
        assertEquals(Optional.of(View.APPOINTMENTS), logic.execute("viewappt 2026-10-06").getView());
        // other commands keep the view that is shown
        assertEquals(Optional.empty(), logic.execute("help").getView());
        assertEquals(Optional.empty(), logic.execute("clear").getView());
    }

    @Test
    public void checkNavigation_entryShown_success() {
        Patient patient = new PatientBuilder(AMY).withAppointments("2026-10-06").build();
        model.addPatient(patient);
        PatientAppointment entry = new PatientAppointment(patient, patient.getSchedule().asList().getFirst());
        CommandResult result = new CommandResult("feedback");
        CommandResult rangeResult = new CommandResult("feedback",
                new AppointmentRange(LocalDate.of(2026, 10, 6), LocalDate.of(2026, 10, 6)));

        // no navigation, or no entry to select
        LogicManager.checkNavigation(result, model);
        LogicManager.checkNavigation(result.withNavigation(Navigation.toView(View.APPOINTMENTS)), model);
        // a patient in the filtered list
        LogicManager.checkNavigation(result.withNavigation(Navigation.toPatient(patient.getId())), model);
        // an appointment with no range, and one in the range
        LogicManager.checkNavigation(result.withNavigation(Navigation.toAppointment(entry)), model);
        LogicManager.checkNavigation(rangeResult.withNavigation(Navigation.toAppointment(entry)), model);
    }

    @Test
    public void checkNavigation_patientFilteredOut_throwsIllegalStateException() {
        Patient patient = new PatientBuilder(AMY).build();
        model.addPatient(patient);
        model.updateFilteredPatientList(unused -> false);
        CommandResult result = new CommandResult("feedback").withNavigation(Navigation.toPatient(patient.getId()));

        assertThrows(IllegalStateException.class, () -> LogicManager.checkNavigation(result, model));
    }

    @Test
    public void checkNavigation_appointmentOutsideRange_throwsIllegalStateException() {
        Patient patient = new PatientBuilder(AMY).withAppointments("2026-10-06").build();
        model.addPatient(patient);
        PatientAppointment entry = new PatientAppointment(patient, patient.getSchedule().asList().getFirst());
        CommandResult result = new CommandResult("feedback",
                new AppointmentRange(LocalDate.of(2026, 10, 7), LocalDate.of(2026, 10, 31)))
                .withNavigation(Navigation.toAppointment(entry));

        assertThrows(IllegalStateException.class, () -> LogicManager.checkNavigation(result, model));
    }

    @Test
    public void checkNavigation_rangeOfOnePatient_checksPatient() {
        Patient patient = new PatientBuilder(AMY).withAppointments("2026-10-06").build();
        model.addPatient(patient);
        PatientAppointment entry = new PatientAppointment(patient, patient.getSchedule().asList().getFirst());
        LocalDate start = LocalDate.of(2026, 10, 1);
        LocalDate end = LocalDate.of(2026, 10, 31);
        Navigation navigation = Navigation.toAppointment(entry);

        // the range's patient
        LogicManager.checkNavigation(new CommandResult("feedback",
                new AppointmentRange(start, end, patient.getId())).withNavigation(navigation), model);
        // another patient's range, although the date is in it
        CommandResult otherPatient = new CommandResult("feedback",
                new AppointmentRange(start, end, new PatientId("S2222222B"))).withNavigation(navigation);
        assertThrows(IllegalStateException.class, () -> LogicManager.checkNavigation(otherPatient, model));
    }

    @Test
    public void checkNavigation_appointmentNotInModel_throwsIllegalStateException() {
        Patient patient = new PatientBuilder(AMY).withAppointments("2026-10-06").build();
        PatientAppointment entry = new PatientAppointment(patient, patient.getSchedule().asList().getFirst());
        CommandResult result = new CommandResult("feedback").withNavigation(Navigation.toAppointment(entry));

        assertThrows(IllegalStateException.class, () -> LogicManager.checkNavigation(result, model));
    }

    @Test
    public void getAppointmentList_followsModel() {
        Patient patient = new PatientBuilder(AMY).withAppointments("2026-10-06").build();
        model.addPatient(patient);
        assertEquals(List.of(new PatientAppointment(patient, patient.getSchedule().asList().getFirst())),
                logic.getAppointmentList());
        assertThrows(UnsupportedOperationException.class, () -> logic.getAppointmentList().remove(0));
    }

    @Test
    public void getPatientList_filteredList_stillHasAllPatients() throws Exception {
        Patient expectedPatient = new PatientBuilder(AMY).build();
        model.addPatient(expectedPatient);
        model.updateFilteredPatientList(patient -> false);

        assertEquals(0, logic.getFilteredPatientList().size());
        assertEquals(List.of(expectedPatient), logic.getPatientList());
    }

    @Test
    public void getCompletions_filteredList_listsDisplayedPatientsFirst() {
        model.addPatient(new PatientBuilder().withId("S1000001A").withName("Alpha").build());
        model.addPatient(new PatientBuilder().withId("S1000002B").withName("Bravo").build());
        model.addPatient(new PatientBuilder().withId("S1000003C").withName("Charlie").build());
        model.updateFilteredPatientList(patient -> patient.getId().value.equals("S1000003C"));

        assertEquals(List.of("view S1000003C", "view S1000001A", "view S1000002B"),
                logic.getCompletions("view S1").stream().map(Completion::text).toList());
        assertEquals(List.of("S1000003C", "S1000001A", "S1000002B"),
                logic.getCompletions("viewappt --id s").stream().map(Completion::word).toList());
        assertEquals(List.of(), logic.getCompletions("list"));
    }

    /**
     * Executes the command and confirms that
     * - no exceptions are thrown <br>
     * - the feedback message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage,
            Model expectedModel) throws CommandException, ParseException {
        CommandResult result = logic.execute(inputCommand);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(expectedModel, model);
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertParseException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage) {
        Model expectedModel = new ModelManager(model.getLaiNurse(), new UserPrefs());
        assertCommandFailure(inputCommand, expectedException, expectedMessage, expectedModel);
    }

    /**
     * Executes the command and confirms that
     * - the {@code expectedException} is thrown <br>
     * - the resulting error message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandSuccess(String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage, Model expectedModel) {
        assertThrows(expectedException, expectedMessage, () -> logic.execute(inputCommand));
        assertEquals(expectedModel, model);
    }

    /**
     * Tests the Logic component's handling of an {@code IOException} thrown by the Storage component.
     *
     * @param e the exception to be thrown by the Storage component
     * @param expectedMessage the message expected inside exception thrown by the Logic component
     */
    private void assertCommandFailureForExceptionFromStorage(IOException e, String expectedMessage) {
        Path prefPath = temporaryFolder.resolve("ExceptionUserPrefs.json");

        // Inject LogicManager with a JsonLaiNurseStorage that throws the IOException e when saving
        JsonLaiNurseStorage laiNurseStorage = new JsonLaiNurseStorage(prefPath) {
            @Override
            public void saveLaiNurse(ReadOnlyLaiNurse laiNurse) throws IOException {
                throw e;
            }
        };

        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("ExceptionUserPrefs.json"));
        StorageManager storage = new StorageManager(laiNurseStorage, userPrefsStorage);

        logic = new LogicManager(model, storage);

        // Triggers the saveLaiNurse method by executing an add command
        String addCommand = AddCommand.COMMAND_WORD + ID_DESC_AMY + NAME_DESC_AMY + PHONE_DESC_AMY
                + ADDRESS_DESC_AMY;
        Patient expectedPatient = new PatientBuilder(AMY).build();
        ModelManager expectedModel = new ModelManager();
        expectedModel.addPatient(expectedPatient);
        assertCommandFailure(addCommand, CommandException.class, expectedMessage, expectedModel);
    }
}
