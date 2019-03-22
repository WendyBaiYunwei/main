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
import seedu.address.model.module.Code;
import seedu.address.model.module.Module;
import seedu.address.model.module.exceptions.ModuleNotFoundException;
import seedu.address.model.planner.DegreePlanner;
import seedu.address.model.requirement.RequirementCategory;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final VersionedAddressBook versionedAddressBook;
    private final UserPrefs userPrefs;

    private final FilteredList<Module> filteredModules;
    private final FilteredList<RequirementCategory> filteredRequirementCategory;

    private final SimpleObjectProperty<Module> selectedModule = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<RequirementCategory> selectedRequirementCategory = new SimpleObjectProperty<>();

    private final VersionedDegreePlannerList versionedDegreePlannerList;
    private final FilteredList<DegreePlanner> filteredDegreePlanners;

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyDegreePlannerList degreePlannerList,
            ReadOnlyUserPrefs userPrefs) {
        super();
        requireAllNonNull(addressBook, degreePlannerList, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        versionedAddressBook = new VersionedAddressBook(addressBook);
        this.userPrefs = new UserPrefs(userPrefs);

        filteredModules = new FilteredList<>(versionedAddressBook.getModuleList());
        filteredRequirementCategory = new FilteredList<>(versionedAddressBook.getRequirementCategoryList());

        versionedDegreePlannerList = new VersionedDegreePlannerList(degreePlannerList);
        filteredDegreePlanners = new FilteredList<>((versionedDegreePlannerList.getDegreePlannerList()));

        filteredModules.addListener(this::ensureSelectedModuleIsValid);
        filteredRequirementCategory.addListener(this::ensureSelectedRequirementCategoryIsValid);
    }

    /**
     * ToDo: Add DegreePlannerList
     */
    public ModelManager() {
        this(new AddressBook(), new DegreePlannerList(), new UserPrefs());
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
    public void setAddressBookFilePath(Path addressBookFilePath) {
        requireNonNull(addressBookFilePath);
        userPrefs.setAddressBookFilePath(addressBookFilePath);
    }

    @Override
    public Path getDegreePlannerListFilePath() {
        return userPrefs.getDegreePlannerListFilePath();
    }

    @Override
    public void setDegreePlannerListFilePath(Path degreePlannerListFilePath) {
        requireNonNull(degreePlannerListFilePath);
        userPrefs.setDegreePlannerListFilePath(degreePlannerListFilePath);
    }

    @Override
    public Path getRequirementCategoryListFilePath() {
        return userPrefs.getRequirementCategoryListFilePath();
    }

    @Override
    public void setRequirementCategoryListFilePath(Path requirementCategoryListFilePath) {
        requireNonNull(requirementCategoryListFilePath);
        userPrefs.setDegreePlannerListFilePath(requirementCategoryListFilePath);
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
    public boolean hasModuleCode(Code code) {
        requireNonNull(code);
        return versionedAddressBook.hasModuleCode(code);
    }

    @Override
    public void deleteModule(Module target) {
        versionedAddressBook.removeModule(target);
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
        return versionedAddressBook.canUndo();
    }

    @Override
    public boolean canRedoAddressBook() {
        return versionedAddressBook.canRedo();
    }

    @Override
    public void undoAddressBook() {
        versionedAddressBook.undo();
    }

    @Override
    public void redoAddressBook() {
        versionedAddressBook.redo();
    }

    @Override
    public void commitAddressBook() {
        versionedAddressBook.commit();
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
                && userPrefs.equals(other.userPrefs)
                && filteredModules.equals(other.filteredModules)
                && Objects.equals(selectedModule.get(), other.selectedModule.get())
                && Objects.equals(selectedRequirementCategory.get(), other.selectedRequirementCategory.get());
    }

    //=========== DegreePlannerList Methods =================================================================

    @Override
    public ReadOnlyDegreePlannerList getDegreePlannerList() {
        return versionedDegreePlannerList;
    }

    @Override
    public boolean hasDegreePlanner(DegreePlanner planner) {
        requireNonNull(planner);
        return versionedDegreePlannerList.hasDegreePlanner(planner);
    }

    @Override
    public boolean hasDegreePlannerModules(DegreePlanner plannerModules) {
        requireNonNull(plannerModules);
        return versionedDegreePlannerList.hasDegreePlannerModules(plannerModules);
    }

    @Override public void deleteDegreePlanner(DegreePlanner target) {
        versionedDegreePlannerList.removeDegreePlanner(target);
    }

    @Override public void addDegreePlanner(DegreePlanner degreePlanner) {
        versionedDegreePlannerList.addDegreePlanner(degreePlanner);
    }

    @Override
    public void addDegreePlannerModules(DegreePlanner plannerModules) {
        versionedDegreePlannerList.addDegreePlannerModule(plannerModules);
    }

    @Override public void setDegreePlanner(DegreePlanner target, DegreePlanner editedDegreePlanner) {
        requireAllNonNull(target, editedDegreePlanner);

        versionedDegreePlannerList.setDegreePlanner(target, editedDegreePlanner);
    }

    @Override public ObservableList<DegreePlanner> getFilteredDegreePlannerList() {
        return filteredDegreePlanners;
    }

    @Override public void updateFilteredDegreePlannerList(Predicate<DegreePlanner> predicate) {
        requireNonNull(predicate);
        filteredDegreePlanners.setPredicate(predicate);
    }

    //=========== Undo/Redo =================================================================================
    @Override public boolean canUndoDegreePlannerList() {
        return versionedDegreePlannerList.canUndo();
    }

    @Override public boolean canRedoDegreePlannerList() {
        return versionedDegreePlannerList.canRedo();
    }

    @Override public void undoDegreePlannerList() {
        versionedDegreePlannerList.undo();
    }

    @Override public void redoDegreePlannerList() {
        versionedDegreePlannerList.redo();
    }

    @Override public void commitDegreePlannerList() {
        versionedDegreePlannerList.commit();
    }

    //=========== RequirementCategoryList Methods =================================================================

    @Override
    public boolean hasRequirementCategory(RequirementCategory requirementCategory) {
        requireNonNull(requirementCategory);
        return versionedAddressBook.hasRequirementCategory(requirementCategory);
    }

    @Override public void deleteRequirementCategory(RequirementCategory target) {
        versionedAddressBook.removeRequirementCategory(target);
    }

    @Override public void addRequirementCategory(RequirementCategory requirementCategory) {
        versionedAddressBook.addRequirementCategory(requirementCategory);
        updateFilteredRequirementCategoryList(PREDICATE_SHOW_ALL_REQUIREMENT_CATEGORIES);
    }

    @Override public void setRequirementCategory(RequirementCategory target,
            RequirementCategory editedRequirementCategory) {
        requireAllNonNull(target, editedRequirementCategory);

        versionedAddressBook.setRequirementCategory(target, editedRequirementCategory);
    }

    @Override public ObservableList<RequirementCategory> getFilteredRequirementCategoryList() {
        return filteredRequirementCategory;
    }

    @Override public void updateFilteredRequirementCategoryList(Predicate<RequirementCategory> predicate) {
        requireNonNull(predicate);
        filteredRequirementCategory.setPredicate(predicate);
    }

    @Override
    public ReadOnlyProperty<RequirementCategory> selectedRequirementCategoryProperty() {
        return selectedRequirementCategory;
    }

    @Override
    public void addModuleToRequirementCategory(RequirementCategory requirementCategoryModule) {
        versionedAddressBook.addModuleToRequirementCategory(requirementCategoryModule);
        updateFilteredRequirementCategoryList(PREDICATE_SHOW_ALL_REQUIREMENT_CATEGORIES);
    }

    @Override
    public boolean isModuleInRequirementCategory(RequirementCategory requirementCategory) {
        requireNonNull(requirementCategory);
        return versionedAddressBook.isModuleInRequirementCategory(requirementCategory);
    }

    @Override
    public boolean doesModuleExistInApplication(RequirementCategory requirementCategory, Model model) {
        requireNonNull(requirementCategory);
        return versionedAddressBook.doesModuleExistInApplication(requirementCategory, model);
    }

    @Override
    public RequirementCategory getSelectedRequirementCategory() {
        return selectedRequirementCategory.getValue();
    }

    @Override
    public void setSelectedRequirementCategory(RequirementCategory requirementCategory) {
        if (requirementCategory != null && !filteredRequirementCategory.contains(requirementCategory)) {
            throw new ModuleNotFoundException();
        }
        selectedRequirementCategory.setValue(requirementCategory);
    }

    public boolean existingPlannerModules(DegreePlanner toAdd, Model model) {
        requireNonNull(toAdd);
        return versionedDegreePlannerList.existingPlannerModules(toAdd, model);
    }

    /**
     * Ensures {@code selectedRequirementCategory} is a valid module in {@code selectedRequirementCategory}.
     */
    private void ensureSelectedRequirementCategoryIsValid(
            ListChangeListener.Change<? extends RequirementCategory> change) {
        while (change.next()) {
            if (selectedRequirementCategory.getValue() == null) {
                // null is always a valid selected module, so we do not need to check that it is valid anymore.
                return;
            }

            boolean wasSelectedRequirementCategoryReplaced =
                    change.wasReplaced() && change.getAddedSize() == change.getRemovedSize()
                            && change.getRemoved().contains(selectedRequirementCategory.getValue());
            if (wasSelectedRequirementCategoryReplaced) {
                // Update selectedModule to its new value.
                int index = change.getRemoved().indexOf(selectedRequirementCategory.getValue());
                selectedRequirementCategory.setValue(change.getAddedSubList().get(index));
                continue;
            }

            boolean wasSelectedRequirementCategoryRemoved = change.getRemoved().stream()
                    .anyMatch(removedRequirementCategory -> selectedRequirementCategory.getValue()
                            .isSameRequirementCategory(removedRequirementCategory));
            if (wasSelectedRequirementCategoryRemoved) {
                // Select the module that came before it in the list,
                // or clear the selection if there is no such module.
                selectedRequirementCategory
                        .setValue(change.getFrom() > 0 ? change.getList().get(change.getFrom() - 1) : null);
            }
        }
    }

}
