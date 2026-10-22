package seedu.address.logic;

import java.util.List;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientAppointment;

/**
 * API of the Logic component
 */
public interface Logic {
    /**
     * Executes the command and returns the result.
     * @param commandText The command as entered by the user.
     * @return the result of the command execution.
     * @throws CommandException If an error occurs during command execution.
     * @throws ParseException If an error occurs during parsing.
     */
    CommandResult execute(String commandText) throws CommandException, ParseException;

    /** Returns an unmodifiable view of the filtered list of patients */
    ObservableList<Patient> getFilteredPatientList();

    /**
     * Removes the filter of the patient list, so that it shows all patients in name order, as {@code list} does.
     * This runs no command, so there is no result to show, nothing is selected, and nothing is saved.
     */
    void showAllPatients();

    /** Returns an unmodifiable view of the list of all patients, whatever the current filter */
    ObservableList<Patient> getPatientList();

    /** Returns an unmodifiable view of every appointment of every patient, in time order */
    ObservableList<PatientAppointment> getAppointmentList();

    /**
     * Returns each way to complete the patient ID at the end of {@code commandText}, such as {@code view S12},
     * with the patients in the filtered list first. Returns an empty list if there is nothing to complete.
     */
    List<String> getCompletions(String commandText);

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Set the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);
}
