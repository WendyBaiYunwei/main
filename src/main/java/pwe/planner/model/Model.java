package pwe.planner.model;

import java.nio.file.Path;
import java.util.function.Predicate;

import javafx.beans.property.ReadOnlyProperty;
import javafx.collections.ObservableList;
import pwe.planner.commons.core.GuiSettings;
import pwe.planner.model.module.Code;
import pwe.planner.model.module.Module;
import pwe.planner.model.module.Name;
import pwe.planner.model.planner.DegreePlanner;
import pwe.planner.model.requirement.RequirementCategory;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluate to true */
    Predicate<Module> PREDICATE_SHOW_ALL_MODULES = unused -> true;

    /** {@code Predicate} that always evaluate to true */
    Predicate<RequirementCategory> PREDICATE_SHOW_ALL_REQUIREMENT_CATEGORIES = unused -> true;

    /** {@code Predicate} that always evaluate to true */
    Predicate<DegreePlanner> PREDICATE_SHOW_ALL_DEGREE_PLANNERS = unused -> true;

    /**
     * Replaces user prefs data with the data in {@code userPrefs}.
     */
    void setUserPrefs(ReadOnlyUserPrefs userPrefs);

    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Returns the user prefs' module list file path.
     */
    Path getModuleListFilePath();

    /**
     * Sets the user prefs' module list file path.
     */
    void setModuleListFilePath(Path moduleListFilePath);

    /**
     * Returns the user prefs' degreePlanner list file path.
     */
    Path getDegreePlannerListFilePath();

    /**
     * Sets the user prefs' degreePlanner list file path.
     */
    void setDegreePlannerListFilePath(Path degreePlannerListFilePath);

    /** Returns the Application */
    ReadOnlyApplication getApplication();

    /**
     * Returns the user prefs' requirement list file path.
     */
    Path getRequirementCategoryListFilePath();

    /**
     * Sets the user prefs' degreePlanner list file path.
     */
    void setRequirementCategoryListFilePath(Path requirementCategoryListFilePath);

    /**
     * Replaces application data with the data in {@code application}.
     */
    void setApplication(ReadOnlyApplication application);

    /**
     * Reset the Requirement Categories in {@code application}
     */
    void resetRequirement();

    /**
     * Reset the Degree Planner in {@code application}
     */
    void resetPlanner();

    /**
     * Returns true if a module with the same identity as {@code module} exists in the application.
     */
    boolean hasModule(Module module);

    /**
     * Returns a module object if a module with the same module code as {@code code} exists in the application.
     */
    Module getModuleByCode(Code code);

    /**
     * Returns true if a {@code Module} with the specified {@code Code} exists in the application.
     */
    boolean hasModuleCode(Code code);

    /**
     * Deletes the given module.
     * The module must exist in the application.
     */
    void deleteModule(Module target);

    /**
     * Adds the given module.
     * {@code module} must not already exist in the application.
     */
    void addModule(Module module);

    /**
     * Replaces the given module {@code target} with {@code editedModule}.
     * {@code target} must exist in the application.
     * The module identity of {@code editedModule} must not be the same as another existing module in the application.
     * This method also cascades changes to {@code DegreePlanner} and {@code RequirementCategory}.
     */
    void editModule(Module target, Module editedModule);

    /**
     * Replaces the given module {@code target} with {@code editedModule}.
     * {@code target} must exist in the application.
     * The module identity of {@code editedModule} must not be the same as another existing module in the application.
     */
    void setModule(Module target, Module editedModule);

    /** Returns an unmodifiable view of the filtered module list */
    ObservableList<Module> getFilteredModuleList();

    /**
     * Updates the filter of the filtered module list to filter by the given {@code predicate}.
     *
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredModuleList(Predicate<Module> predicate);

    /**
     * Returns true if the model has previous application states to restore.
     */
    boolean canUndoApplication();

    /**
     * Returns true if the model has undone application states to restore.
     */
    boolean canRedoApplication();

    /**
     * Restores the model's application to its previous state.
     */
    void undoApplication();

    /**
     * Restores the model's application to its previously undone state.
     */
    void redoApplication();

    /**
     * Saves the current application state for undo/redo.
     */
    void commitApplication();

    /**
     * Selected module in the filtered module list.
     * null if no module is selected.
     */
    ReadOnlyProperty<Module> selectedModuleProperty();

    /**
     * Returns the selected module in the filtered module list.
     * null if no module is selected.
     */
    Module getSelectedModule();

    /**
     * Sets the selected module in the filtered module list.
     */
    void setSelectedModule(Module module);

    ///// DegreePlanner Methods

    /**
     * Returns true if a degreePlanner with the same identity as {@code degreePlanner} exists in the application.
     */
    boolean hasDegreePlanner(DegreePlanner degreePlanner);

    /**
     * Return the degree planner which contains the given {@code code}, otherwise returns null.
     */
    DegreePlanner getDegreePlannerByCode(Code code);

    /**
     * Deletes the given degreePlanner.
     * The degreePlanner must exist in the degreePlaner list.
     */
    void deleteDegreePlanner(DegreePlanner degreePlanner);

    /**
     * Adds the given degreePlanner.
     * {@code degreePlanner} must not already exist in the application.
     */
    void addDegreePlanner(DegreePlanner degreePlanner);

    /**
     * Replaces the given degreePlanner {@code target} with {@code editedDegreePlanner}.
     * {@code target} must exist in the degreePlanner list.
     * The degreePlanner identity of {@code editedDegreePlanner} must not be the same as another existing
     * degreePlanner in the degreePlanner List.
     */
    void setDegreePlanner(DegreePlanner target, DegreePlanner editedDegreePlanner);

    /** Returns an unmodifiable view of the filtered degreePlanner list */
    ObservableList<DegreePlanner> getFilteredDegreePlannerList();

    /**
     * Updates the filter of the filtered degreePlanner list to filter by the given {@code predicate}.
     *
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredDegreePlannerList(Predicate<DegreePlanner> predicate);


    ///// RequirementCategory Methods

    /**
     * Returns true if a requirement with the name as {@code requirement} exists in the
     * requirement list.
     */
    boolean hasRequirementCategory(Name requirementCategoryName);

    /**
     * Returns true if a requirement with the name as {@code requirement} exists in the
     * requirement list.
     */
    boolean hasRequirementCategory(RequirementCategory requirementCategory);

    /**
     * Returns true if a requirement with the code as {@code requirement} exists in the
     * requirement list.
     */
    RequirementCategory getRequirementCategory(Name requirementCategoryName);

    /**
     * Adds the given requirement.
     * {@code requirement} must not already exist in the requirementCategoryList.
     */
    void addRequirementCategory(RequirementCategory requirementCategory);

    /**
     * Replaces the given requirement {@code target} with {@code editedRequirementCategory}.
     * {@code target} must exist in the requirement list.
     * The planner identity of {@code editedRequirementCategory} must not be the same as another existing
     * requirement in the
     * requirement list.
     */
    void setRequirementCategory(RequirementCategory target, RequirementCategory editedRequirementCategory);

    /**
     * Returns an unmodifiable view of the filtered requirement list
     */
    ObservableList<RequirementCategory> getFilteredRequirementCategoryList();

    /**
     * Updates the filter of the filtered requirement list to filter by the given {@code predicate}.
     *
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredRequirementCategoryList(Predicate<RequirementCategory> predicate);

    /**
     * Selected requirementCategory in the filtered requirementCategory list.
     * null if no requirementCategory is selected.
     */
    ReadOnlyProperty<RequirementCategory> selectedRequirementCategoryProperty();

    /**
     * Returns the selected requirementCategory in the filtered requirementCategory list.
     * null if no requirementCategory is selected.
     */
    RequirementCategory getSelectedRequirementCategory();

    /**
     * Sets the selected requirementCategory in the filtered requirementCategory list.
     */
    void setSelectedRequirementCategory(RequirementCategory requirementCategory);

}
