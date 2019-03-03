package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.module.Module;
import seedu.address.model.module.exceptions.ModuleNotFoundException;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final VersionedAddressBook versionedAddressBook;
    private final VersionedDegreePlanner versionedDegreePlanner;
    private final UserPrefs userPrefs;
    private final FilteredList<Module> filteredModules;
    private final FilteredList<Module> filteredPlannerModules;
    private final SimpleObjectProperty<Module> selectedModule = new SimpleObjectProperty<>();

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyAddressBook degreePlanner, ReadOnlyUserPrefs userPrefs) {
        super();
        requireAllNonNull(addressBook, degreePlanner, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + "Initializing with degree planner: " + degreePlanner + " and user prefs " + userPrefs);

        versionedAddressBook = new VersionedAddressBook(addressBook);
        versionedDegreePlanner = new VersionedDegreePlanner(degreePlanner);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredModules = new FilteredList<>(versionedAddressBook.getModuleList());
        filteredModules.addListener(this::ensureSelectedModuleIsValid);
        filteredPlannerModules = new FilteredList<>(versionedDegreePlanner.getModuleList());
        filteredPlannerModules.addListener(this::ensureSelectedModuleIsValid);
    }

    public ModelManager() {
        this(new AddressBook(), new DegreePlanner(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        this.userPrefs.resetData(userPrefs);
    }

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    @Override
    public Path getAddressBookFilePath() {
        return userPrefs.getAddressBookFilePath();
    }

    @Override
    public Path getDegreePlannerFilePath() {
        return userPrefs.getDegreePlannerFilePath();
    }

    @Override
    public void setAddressBookFilePath(Path addressBookFilePath) {
        requireNonNull(addressBookFilePath);
        userPrefs.setAddressBookFilePath(addressBookFilePath);
    }

    @Override
    public void setDegreePlannerFilePath(Path degreePlannerFilePath) {
        requireNonNull(degreePlannerFilePath);
        userPrefs.setAddressBookFilePath(degreePlannerFilePath);
    }

    //=========== AddressBook ================================================================================

    @Override
    public void setAddressBook(ReadOnlyAddressBook addressBook) {
        versionedAddressBook.resetData(addressBook);
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return versionedAddressBook;
    }

    @Override
    public boolean hasModule(Module module) {
        requireNonNull(module);
        return versionedAddressBook.hasModule(module);
    }

    @Override
    public void deleteModule(Module target) {
        versionedAddressBook.removeModule(target);
    }

    @Override
    public void plannerDeleteModule(Module target) {

    }

    @Override
    public void addModule(Module module) {
        versionedAddressBook.addModule(module);
        updateFilteredModuleList(PREDICATE_SHOW_ALL_MODULES);
    }

    @Override
    public void setModule(Module target, Module editedModule) {
        requireAllNonNull(target, editedModule);

        versionedAddressBook.setModule(target, editedModule);
    }

    //=========== DegreePlanner ================================================================================

    @Override
    public void setDegreePlanner(ReadOnlyAddressBook degreePlanner) {
        versionedDegreePlanner.resetData(degreePlanner);
    }

    @Override
    public ReadOnlyAddressBook getDegreePlanner() {
        return versionedDegreePlanner;
    }

    @Override
    public boolean plannerHasModule(Module module) {
        requireNonNull(module);
        return versionedDegreePlanner.plannerHasModule(module);
    }

    @Override
    public void plannerAddModule(Module module) {
        versionedDegreePlanner.plannerAddModule(module);
        updateFilteredModuleList(PREDICATE_SHOW_ALL_MODULES);
    }

    @Override
    public void plannerSetModule(Module target, Module editedModule) {
        requireAllNonNull(target, editedModule);

        versionedDegreePlanner.plannerSetModule(target, editedModule);
    }

    //=========== Filtered Module List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Module} backed by the internal list of
     * {@code versionedAddressBook}
     */
    @Override
    public ObservableList<Module> getFilteredModuleList() {
        return filteredModules;
    }

    @Override
    public void updateFilteredModuleList(Predicate<Module> predicate) {
        requireNonNull(predicate);
        filteredModules.setPredicate(predicate);
    }

    //=========== Undo/Redo =================================================================================

    @Override
    public boolean canUndoAddressBook() {
        return versionedAddressBook.canUndo() && versionedDegreePlanner.canUndo();
    }

    @Override
    public boolean canUndoDegreePlanner() {
        return false;
    }

    @Override
    public boolean canRedoAddressBook() {
        return versionedAddressBook.canRedo() && versionedDegreePlanner.canRedo();
    }

    @Override
    public boolean canRedoDegreePlanner() {
        return false;
    }

    @Override
    public void undoAddressBook() {
        versionedAddressBook.undo();
        versionedDegreePlanner.undo();
    }

    @Override
    public void undoDegreePlanner() {

    }

    @Override
    public void redoAddressBook() {
        versionedAddressBook.redo();
        versionedDegreePlanner.redo();
    }

    @Override
    public void redoDegreePlanner() {

    }

    @Override
    public void commitAddressBook() {
        versionedAddressBook.commit();
        versionedDegreePlanner.commit();
    }

    @Override
    public void commitDegreePlanner() {

    }

    //=========== Selected module ===========================================================================

    @Override
    public ReadOnlyProperty<Module> selectedModuleProperty() {
        return selectedModule;
    }

    @Override
    public Module getSelectedModule() {
        return selectedModule.getValue();
    }

    @Override
    public void setSelectedModule(Module module) {
        if (module != null && !filteredModules.contains(module)) {
            throw new ModuleNotFoundException();
        }
        selectedModule.setValue(module);
    }

    /**
     * Ensures {@code selectedModule} is a valid module in {@code filteredModules}.
     */
    private void ensureSelectedModuleIsValid(ListChangeListener.Change<? extends Module> change) {
        while (change.next()) {
            if (selectedModule.getValue() == null) {
                // null is always a valid selected module, so we do not need to check that it is valid anymore.
                return;
            }

            boolean wasSelectedModuleReplaced = change.wasReplaced() && change.getAddedSize() == change.getRemovedSize()
                    && change.getRemoved().contains(selectedModule.getValue());
            if (wasSelectedModuleReplaced) {
                // Update selectedModule to its new value.
                int index = change.getRemoved().indexOf(selectedModule.getValue());
                selectedModule.setValue(change.getAddedSubList().get(index));
                continue;
            }

            boolean wasSelectedModuleRemoved = change.getRemoved().stream()
                    .anyMatch(removedModule -> selectedModule.getValue().isSameModule(removedModule));
            if (wasSelectedModuleRemoved) {
                // Select the module that came before it in the list,
                // or clear the selection if there is no such module.
                selectedModule.setValue(change.getFrom() > 0 ? change.getList().get(change.getFrom() - 1) : null);
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        // short circuit if same object
        if (obj == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(obj instanceof ModelManager)) {
            return false;
        }

        // state check
        ModelManager other = (ModelManager) obj;
        return versionedAddressBook.equals(other.versionedAddressBook)
                && versionedDegreePlanner.equals(other.versionedDegreePlanner)
                && userPrefs.equals(other.userPrefs)
                && filteredModules.equals(other.filteredModules)
                && Objects.equals(selectedModule.get(), other.selectedModule.get());
    }

}
