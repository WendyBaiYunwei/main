package seedu.address.model;

import java.nio.file.Path;
import java.util.function.Predicate;

import javafx.beans.property.ReadOnlyProperty;
import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.module.Module;
import seedu.address.model.planner.DegreePlanner;
import seedu.address.model.planner.DegreePlannerModule;
import seedu.address.model.requirement.RequirementCategory;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluate to true */
    Predicate<Module> PREDICATE_SHOW_ALL_MODULES = unused -> true;

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
     * Returns the user prefs' address book file path.
     */
    Path getAddressBookFilePath();

    /**
     * Sets the user prefs' address book file path.
     */
    void setAddressBookFilePath(Path addressBookFilePath);

    /**
     * Returns the user prefs' degreePlanner list file path.
     */
    Path getDegreePlannerListFilePath();

    /**
     * Sets the user prefs' degreePlanner list file path.
     */
    void setDegreePlannerListFilePath(Path degreePlannerListFilePath);

    /** Returns the AddressBook */
    ReadOnlyAddressBook getAddressBook();

    /**
     * Returns the user prefs' requirement list file path.
     */
    Path getRequirementCategoryListFilePath();

    /**
     * Sets the user prefs' degreePlanner list file path.
     */
    void setRequirementCategoryListFilePath(Path requirementCategoryListFilePath);

    /**
     * Replaces address book data with the data in {@code addressBook}.
     */
    void setAddressBook(ReadOnlyAddressBook addressBook);

    /**
     * Returns true if a module with the same identity as {@code module} exists in the address book.
     */
    boolean hasModule(Module module);

    /**
     * Deletes the given module.
     * The module must exist in the address book.
     */
    void deleteModule(Module target);

    /**
     * Adds the given module.
     * {@code module} must not already exist in the address book.
     */
    void addModule(Module module);

    /**
     * Replaces the given module {@code target} with {@code editedModule}.
     * {@code target} must exist in the address book.
     * The module identity of {@code editedModule} must not be the same as another existing module in the address book.
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
     * Returns true if the model has previous address book states to restore.
     */
    boolean canUndoAddressBook();

    /**
     * Returns true if the model has undone address book states to restore.
     */
    boolean canRedoAddressBook();

    /**
     * Restores the model's address book to its previous state.
     */
    void undoAddressBook();

    /**
     * Restores the model's address book to its previously undone state.
     */
    void redoAddressBook();

    /**
     * Saves the current address book state for undo/redo.
     */
    void commitAddressBook();

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

    /** Returns the DegreePlannerList */
    ReadOnlyDegreePlannerList getDegreePlannerList();

    /**
     * Returns true if a degreePlanner with the same identity as {@code degreePlanner} exists in the address book.
     */
    boolean hasDegreePlanner(DegreePlanner degreePlanner);

    boolean hasDegreePlannerModule(DegreePlannerModule plannerModule);

    /**
     * Deletes the given degreePlanner.
     * The degreePlanner must exist in the degreePlaner list.
     */
    void deleteDegreePlanner(DegreePlanner degreePlanner);

    /**
     * Adds the given degreePlanner.
     * {@code degreePlanner} must not already exist in the address book.
     */
    void addDegreePlanner(DegreePlanner degreePlanner);

    /**
     * Adds the given module to the degree planner.
     * {@code plannerModule} must not already exist in the address book.
     */
    void addDegreePlannerModule(DegreePlannerModule plannerModule);
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

    /**
     * Returns true if the model has previous degreePlanner list states to restore.
     */
    boolean canUndoDegreePlannerList();

    /**
     * Returns true if the model has undone degreePlanner list states to restore.
     */
    boolean canRedoDegreePlannerList();

    /**
     * Restores the model's degreePlanner list to its previous state.
     */
    void undoDegreePlannerList();

    /**
     * Restores the model's degreePlanner list to its previously undone state.
     */
    void redoDegreePlannerList();

    /**
     * Saves the current degreePlanner list for undo/redo.
     */
    void commitDegreePlannerList();


    /**
     * Returns the RequirementCategoryList
     */
    ReadOnlyRequirementCategoryList getRequirementCategoryList();

    /**
     * Returns true if a requirement with the code as {@code requirement} exists in the
     * requirement list.
     */
    boolean hasRequirementCategory(RequirementCategory requirementCategory);

    /**
     * Deletes the given requirement.
     * The requirement must exist in the address book.
     */
    void deleteRequirementCategory(RequirementCategory target);

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
     * Returns true if the model has previous requirement list states to restore.
     */
    boolean canUndoRequirementCategoryList();

    /**
     * Returns true if the model has undone requirement list states to restore.
     */
    boolean canRedoRequirementCategoryList();

    /**
     * Restores the model's requirement list to its previous state.
     */
    void undoRequirementCategoryList();

    /**
     * Restores the model's requirement list to its previously undone state.
     */
    void redoRequirementCategoryList();

    /**
     * Saves the current address book state for undo/redo.
     */
    void commitRequirementCategoryList();

}
