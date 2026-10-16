package seedu.address.logic;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.IdCompleter;
import seedu.address.logic.parser.LaiNurseParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientAppointment;
import seedu.address.model.patient.PatientId;
import seedu.address.storage.Storage;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    public static final String FILE_OPS_ERROR_FORMAT = "Could not save data due to the following error: %s";

    public static final String FILE_OPS_PERMISSION_ERROR_FORMAT =
            "Could not save data to file %s due to insufficient permissions to write to the file or the folder.";

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;
    private final LaiNurseParser laiNurseParser;

    /**
     * Constructs a {@code LogicManager} with the given {@code Model} and {@code Storage}.
     */
    public LogicManager(Model model, Storage storage) {
        this.model = model;
        this.storage = storage;
        laiNurseParser = new LaiNurseParser();
    }

    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");

        CommandResult commandResult;
        Command command = laiNurseParser.parseCommand(commandText);
        commandResult = command.execute(model);
        // Each command declares the view it shows, so a command's own result never has to name it
        commandResult = command.getView().map(commandResult::withView).orElse(commandResult);

        try {
            storage.saveLaiNurse(model.getLaiNurse());
        } catch (AccessDeniedException e) {
            throw new CommandException(String.format(FILE_OPS_PERMISSION_ERROR_FORMAT, e.getMessage()), e);
        } catch (IOException ioe) {
            throw new CommandException(String.format(FILE_OPS_ERROR_FORMAT, ioe.getMessage()), ioe);
        }

        return commandResult;
    }

    @Override
    public ObservableList<Patient> getFilteredPatientList() {
        return model.getFilteredPatientList();
    }

    @Override
    public ObservableList<Patient> getPatientList() {
        return model.getLaiNurse().getPatientList();
    }

    @Override
    public ObservableList<PatientAppointment> getAppointmentList() {
        return model.getAppointmentList();
    }

    @Override
    public List<String> getCompletions(String commandText) {
        List<PatientId> ids = Stream.concat(getFilteredPatientList().stream(), getPatientList().stream())
                .map(Patient::getId)
                .distinct()
                .toList();
        return IdCompleter.complete(commandText, ids);
    }

    @Override
    public GuiSettings getGuiSettings() {
        return model.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        model.setGuiSettings(guiSettings);
    }
}
