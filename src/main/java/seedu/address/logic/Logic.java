package seedu.address.logic;

import java.util.List;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.Completion;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.PatientScope;
import seedu.address.model.Settings;
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
     * Undoes one step of what the patient list shows, as Esc does: removes its filter, so that it shows every patient
     * in the scope in name order, or if it has no filter, goes back to the default scope, as {@code view} does.
     * This runs no command, so there is no result to show, nothing is selected, and nothing is saved.
     */
    void resetPatientList();

    /**
     * Returns the scope of the patient list: whether it shows archived patients.
     */
    ObservableValue<PatientScope> getPatientListScope();

    /**
     * Returns the settings that apply, which decide how marks, past clashes and archived patients behave (#216).
     */
    ObservableValue<Settings> getSettings();

    /**
     * Returns whether the patient list has a filter, such as one that {@code find} sets, even one that hides no one.
     */
    ObservableValue<Boolean> isPatientListFiltered();

    /** Returns an unmodifiable view of the list of all patients, whatever the current filter */
    ObservableList<Patient> getPatientList();

    /** Returns an unmodifiable view of every appointment of every patient, in time order */
    ObservableList<PatientAppointment> getAppointmentList();

    /**
     * Returns each way to complete the word that {@code commandText} ends with, such as the command word in
     * {@code dele}, the option in {@code edit S1234567A --ph} or the patient ID in {@code view S12}, with the patients
     * in the filtered list first, and archived patients last. Returns an empty list if there is nothing to complete.
     *
     * @see seedu.address.logic.parser.TabCompleter#complete(String, List)
     */
    List<Completion> getCompletions(String commandText);

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Set the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);
}
