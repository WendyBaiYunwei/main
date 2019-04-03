package pwe.planner.logic;

import java.nio.file.Path;

import javafx.beans.property.ReadOnlyProperty;
import javafx.collections.ObservableList;
import pwe.planner.commons.core.GuiSettings;
import pwe.planner.logic.commands.CommandResult;
import pwe.planner.logic.commands.exceptions.CommandException;
import pwe.planner.logic.parser.exceptions.ParseException;
import pwe.planner.model.Model;
import pwe.planner.model.ReadOnlyApplication;
import pwe.planner.model.module.Module;
import pwe.planner.model.planner.DegreePlanner;
import pwe.planner.model.requirement.RequirementCategory;

/**
 * API of the Logic component
 */
public interface Logic {
    /**
     * Executes the command and returns the result.
     *
     * @param commandText The command as entered by the user.
     * @return the result of the command execution.
     * @throws CommandException If an error occurs during command execution.
     * @throws ParseException   If an error occurs during parsing.
     */
    CommandResult execute(String commandText) throws CommandException, ParseException;

    /**
     * Returns the Application.
     *
     * @see Model#getApplication()
     */
    ReadOnlyApplication getApplication();

    /** Returns an unmodifiable view of the filtered list of modules */
    ObservableList<Module> getFilteredModuleList();

    /** Returns an unmodifiable view of the filtered degreePlanner list */
    ObservableList<DegreePlanner> getFilteredDegreePlannerList();

    /** Returns an unmodifiable view of the filtered list of modules */
    ObservableList<RequirementCategory> getFilteredRequirementCategoryList();

    /**
     * Returns an unmodifiable view of the list of commands entered by the user.
     * The list is ordered from the least recent command to the most recent command.
     */
    ObservableList<String> getHistory();

    /**
     * Returns the user prefs' module list file path.
     */
    Path getModuleListFilePath();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Set the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Selected module in the filtered module list.
     * null if no module is selected.
     *
     * @see Model#selectedModuleProperty()
     */
    ReadOnlyProperty<Module> selectedModuleProperty();

    /**
     * Sets the selected module in the filtered module list.
     *
     * @see Model#setSelectedModule(Module)
     */
    void setSelectedModule(Module module);

    /**
     * Selected module in the filtered module list.
     * null if no module is selected.
     *
     * @see Model#selectedModuleProperty()
     */
    ReadOnlyProperty<RequirementCategory> selectedRequirementCategoryProperty();

    /**
     * Sets the selected module in the filtered module list.
     *
     * @see Model#setSelectedModule(Module)
     */
    void setSelectedRequirementCategory(RequirementCategory requirementCategory);
}
