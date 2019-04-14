package pwe.planner.model;

import static java.util.Objects.requireNonNull;
import static pwe.planner.commons.util.CollectionUtil.requireAllNonNull;

import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import pwe.planner.commons.core.GuiSettings;
import pwe.planner.commons.core.LogsCenter;
import pwe.planner.model.module.Code;
import pwe.planner.model.module.Module;
import pwe.planner.model.module.Name;
import pwe.planner.model.module.exceptions.ModuleNotFoundException;
import pwe.planner.model.planner.DegreePlanner;
import pwe.planner.model.requirement.RequirementCategory;

/**
 * Represents the in-memory model of the application data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final VersionedApplication versionedApplication;
    private final UserPrefs userPrefs;

    private final FilteredList<Module> filteredModules;
    private final FilteredList<DegreePlanner> filteredDegreePlanners;
    private final FilteredList<RequirementCategory> filteredRequirementCategory;

    private final SimpleObjectProperty<Module> selectedModule = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<RequirementCategory> selectedRequirementCategory = new SimpleObjectProperty<>();

    /**
     * Initializes a ModelManager with the given application and userPrefs.
     */
    public ModelManager(ReadOnlyApplication application, ReadOnlyUserPrefs userPrefs) {
        super();
        requireAllNonNull(application, userPrefs);

        logger.fine("Initializing with application: " + application + " and user prefs " + userPrefs);

        versionedApplication = new VersionedApplication(application);
        this.userPrefs = new UserPrefs(userPrefs);

        filteredModules = new FilteredList<>(versionedApplication.getModuleList());
        filteredDegreePlanners = new FilteredList<>((versionedApplication.getDegreePlannerList()));
        filteredRequirementCategory = new FilteredList<>(versionedApplication.getRequirementCategoryList());

        filteredModules.addListener(this::ensureSelectedModuleIsValid);
        filteredRequirementCategory.addListener(this::ensureSelectedRequirementCategoryIsValid);
    }

    public ModelManager() {
        this(new Application(), new UserPrefs());
    }

    public ModelManager(ReadOnlyApplication application) {
        this(application, new UserPrefs());
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
    public Path getModuleListFilePath() {
        return userPrefs.getModuleListFilePath();
    }

    @Override
    public void setModuleListFilePath(Path moduleListFilePath) {
        requireNonNull(moduleListFilePath);

        userPrefs.setModuleListFilePath(moduleListFilePath);
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

    //=========== Application ================================================================================

    @Override
    public void setApplication(ReadOnlyApplication application) {
        requireNonNull(application);

        versionedApplication.resetData(application);
    }

    @Override
    public void resetRequirement() {
        versionedApplication.resetRequirement();
    }

    @Override
    public void resetPlanner() {
        versionedApplication.resetPlanner();
    }

    @Override
    public ReadOnlyApplication getApplication() {
        return versionedApplication;
    }

    @Override
    public boolean hasModule(Module module) {
        requireNonNull(module);

        return versionedApplication.hasModule(module);
    }

    @Override
    public Module getModuleByCode(Code code) {
        requireNonNull(code);

        return versionedApplication.getModuleByCode(code);
    }

    @Override
    public boolean hasModuleCode(Code code) {
        requireNonNull(code);

        return versionedApplication.hasModuleCode(code);
    }

    @Override
    public void deleteModule(Module target) {
        requireNonNull(target);

        versionedApplication.removeModule(target);
    }

    @Override
    public void addModule(Module module) {
        requireNonNull(module);

        versionedApplication.addModule(module);
        updateFilteredModuleList(PREDICATE_SHOW_ALL_MODULES);
    }

    @Override
    public void editModule(Module target, Module editedModule) {
        requireAllNonNull(target, editedModule);

        versionedApplication.editModule(target, editedModule);
    }

    @Override
    public void setModule(Module target, Module editedModule) {
        requireAllNonNull(target, editedModule);

        versionedApplication.setModule(target, editedModule);
    }

    //=========== Filtered Module List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Module} backed by the internal list of
     * {@code versionedApplication}
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
    public boolean canUndoApplication() {
        return versionedApplication.canUndo();
    }

    @Override
    public boolean canRedoApplication() {
        return versionedApplication.canRedo();
    }

    @Override
    public void undoApplication() {
        versionedApplication.undo();
    }

    @Override
    public void redoApplication() {
        versionedApplication.redo();
    }

    @Override
    public void commitApplication() {
        versionedApplication.commit();
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
        assert change != null;

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
        return versionedApplication.equals(other.versionedApplication)
                && userPrefs.equals(other.userPrefs)
                && filteredModules.equals(other.filteredModules)
                && Objects.equals(selectedModule.get(), other.selectedModule.get())
                && Objects.equals(selectedRequirementCategory.get(), other.selectedRequirementCategory.get());
    }

    //=========== DegreePlannerList Methods =================================================================

    @Override
    public boolean hasDegreePlanner(DegreePlanner planner) {
        requireNonNull(planner);
        return versionedApplication.hasDegreePlanner(planner);
    }

    @Override
    public void deleteDegreePlanner(DegreePlanner target) {
        versionedApplication.removeDegreePlanner(target);
    }

    @Override
    public void addDegreePlanner(DegreePlanner degreePlanner) {
        versionedApplication.addDegreePlanner(degreePlanner);
    }

    @Override
    public DegreePlanner getDegreePlannerByCode(Code code) {
        requireNonNull(code);
        return versionedApplication.getDegreePlannerByCode(code);
    }

    @Override
    public void setDegreePlanner(DegreePlanner target, DegreePlanner editedDegreePlanner) {
        requireAllNonNull(target, editedDegreePlanner);

        versionedApplication.setDegreePlanner(target, editedDegreePlanner);
    }

    @Override
    public void moveModuleBetweenPlanner(DegreePlanner sourcePlanner, DegreePlanner destinationPlanner, Code code) {
        requireAllNonNull(sourcePlanner, destinationPlanner, code);

        versionedApplication.moveModuleBetweenPlanner(sourcePlanner, destinationPlanner, code);
    }

    @Override
    public ObservableList<DegreePlanner> getFilteredDegreePlannerList() {
        return filteredDegreePlanners;
    }

    @Override
    public void updateFilteredDegreePlannerList(Predicate<DegreePlanner> predicate) {
        requireNonNull(predicate);

        filteredDegreePlanners.setPredicate(predicate);
    }

    //=========== RequirementCategoryList Methods =================================================================

    @Override
    public boolean hasRequirementCategory(Name requirementCategoryName) {
        requireNonNull(requirementCategoryName);

        return versionedApplication.hasRequirementCategory(requirementCategoryName);
    }

    @Override
    public boolean hasRequirementCategory(RequirementCategory requirementCategory) {
        requireNonNull(requirementCategory);

        return versionedApplication.hasRequirementCategory(requirementCategory);
    }

    @Override
    public RequirementCategory getRequirementCategory(Name requirementCategoryName) {
        requireNonNull(requirementCategoryName);

        return versionedApplication.getRequirementCategory(requirementCategoryName);
    }

    @Override
    public void addRequirementCategory(RequirementCategory requirementCategory) {
        requireNonNull(requirementCategory);

        versionedApplication.addRequirementCategory(requirementCategory);
        updateFilteredRequirementCategoryList(PREDICATE_SHOW_ALL_REQUIREMENT_CATEGORIES);
    }

    @Override
    public void setRequirementCategory(RequirementCategory target, RequirementCategory editedRequirementCategory) {
        requireAllNonNull(target, editedRequirementCategory);

        versionedApplication.setRequirementCategory(target, editedRequirementCategory);
    }

    @Override
    public ObservableList<RequirementCategory> getFilteredRequirementCategoryList() {
        return filteredRequirementCategory;
    }

    @Override
    public void updateFilteredRequirementCategoryList(Predicate<RequirementCategory> predicate) {
        requireNonNull(predicate);

        filteredRequirementCategory.setPredicate(predicate);
    }

    @Override
    public ReadOnlyProperty<RequirementCategory> selectedRequirementCategoryProperty() {
        return selectedRequirementCategory;
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

    /**
     * Ensures {@code selectedRequirementCategory} is a valid module in {@code selectedRequirementCategory}.
     */
    private void ensureSelectedRequirementCategoryIsValid(
            ListChangeListener.Change<? extends RequirementCategory> change) {
        assert change != null;

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
