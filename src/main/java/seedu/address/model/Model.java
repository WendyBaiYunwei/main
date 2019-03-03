package seedu.address.model;

import java.nio.file.Path;
import java.util.function.Predicate;

import javafx.beans.property.ReadOnlyProperty;
import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.module.Module;

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

    Path getDegreePlannerFilePath();

    /**
     * Sets the user prefs' address book file path.
     */
    void setAddressBookFilePath(Path addressBookFilePath);

    void setDegreePlannerFilePath(Path degreePlannerFilePathFilePath);

    /**
     * Replaces address book data with the data in {@code addressBook}.
     */
    void setAddressBook(ReadOnlyAddressBook addressBook);

    void setDegreePlanner(ReadOnlyAddressBook degreePlanner);

    /** Returns the AddressBook */
    ReadOnlyAddressBook getAddressBook();

    ReadOnlyAddressBook getDegreePlanner();

    /**
     * Returns true if a module with the same identity as {@code module} exists in the address book.
     */
    boolean hasModule(Module module);

    boolean plannerHasModule(Module module);
    /**
     * Deletes the given module.
     * The module must exist in the address book.
     */
    void deleteModule(Module target);
    void plannerDeleteModule(Module target);

    /**
     * Adds the given module.
     * {@code module} must not already exist in the address book.
     */
    void addModule(Module module);

    void plannerAddModule(Module module);

    /**
     * Replaces the given module {@code target} with {@code editedModule}.
     * {@code target} must exist in the address book.
     * The module identity of {@code editedModule} must not be the same as another existing module in the address book.
     */
    void setModule(Module target, Module editedModule);

    void plannerSetModule(Module target, Module editedModule);

    /** Returns an unmodifiable view of the filtered module list */
    ObservableList<Module> getFilteredModuleList();

    /**
     * Updates the filter of the filtered module list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredModuleList(Predicate<Module> predicate);

    /**
     * Returns true if the model has previous address book states to restore.
     */
    boolean canUndoAddressBook();

    boolean canUndoDegreePlanner();

    /**
     * Returns true if the model has undone address book states to restore.
     */
    boolean canRedoAddressBook();

    boolean canRedoDegreePlanner();
    /**
     * Restores the model's address book to its previous state.
     */
    void undoAddressBook();

    void undoDegreePlanner();

    /**
     * Restores the model's address book to its previously undone state.
     */
    void redoAddressBook();

    void redoDegreePlanner();

    /**
     * Saves the current address book state for undo/redo.
     */
    void commitAddressBook();
    void commitDegreePlanner();

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
}
