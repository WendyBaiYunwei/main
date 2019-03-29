package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import seedu.address.commons.util.InvalidationListenerManager;
import seedu.address.model.module.Code;
import seedu.address.model.module.Module;
import seedu.address.model.module.Name;
import seedu.address.model.module.UniqueModuleList;
import seedu.address.model.planner.DegreePlanner;
import seedu.address.model.planner.UniqueDegreePlannerList;
import seedu.address.model.requirement.RequirementCategory;
import seedu.address.model.requirement.UniqueRequirementCategoryList;

/**
 * Wraps all data at the address-book level
 * Duplicates are not allowed (by .isSameModule comparison)
 */
public class AddressBook implements ReadOnlyAddressBook {

    private final UniqueModuleList modules;
    private final UniqueDegreePlannerList degreePlanners;
    private final UniqueRequirementCategoryList requirementCategories;
    private final InvalidationListenerManager invalidationListenerManager = new InvalidationListenerManager();

    /*
     * The 'unusual' code block below is an non-static initialization block, sometimes used to avoid duplication
     * between constructors. See https://docs.oracle.com/javase/tutorial/java/javaOO/initial.html
     *
     * Note that non-static init blocks are not recommended to use. There are other ways to avoid duplication
     *   among constructors.
     */
    {
        modules = new UniqueModuleList();
        degreePlanners = new UniqueDegreePlannerList();
        requirementCategories = new UniqueRequirementCategoryList();
    }

    public AddressBook() {}

    /**
     * Creates an AddressBook using the Modules in the {@code toBeCopied}
     */
    public AddressBook(ReadOnlyAddressBook toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);
        setModules(newData.getModuleList());
        setDegreePlanners(newData.getDegreePlannerList());
        setRequirementCategories(newData.getRequirementCategoryList());
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the module list with {@code modules}.
     * {@code modules} must not contain duplicate modules.
     */
    public void setModules(List<Module> modules) {
        this.modules.setModules(modules);
        indicateModified();
    }

    /**
     * Replaces the contents of the degree planner list with {@code degreePlanners}.
     * {@code degreePlanners} must not contain duplicate degree planner.
     */
    public void setDegreePlanners(List<DegreePlanner> degreePlanners) {
        this.degreePlanners.setDegreePlanners(degreePlanners);
        indicateModified();
    }

    /**
     * Replaces the contents of the requirement list with {@code requirement}.
     * {@code requirement} must not contain duplicate requirement.
     */
    public void setRequirementCategories(List<RequirementCategory> requirementCategories) {
        this.requirementCategories.setRequirementCategories(requirementCategories);
        indicateModified();
    }

    //// module-level operations

    /**
     * Returns true if a module with the same identity as {@code module} exists in the address book.
     */
    public boolean hasModule(Module module) {
        requireNonNull(module);
        return modules.contains(module);
    }

    /**
     * Returns true if a {@code Module} with the specified {@code Code} exists in the address book.
     */
    public boolean hasModuleCode(Code code) {
        requireNonNull(code);
        return modules.asUnmodifiableObservableList().stream().anyMatch((module) -> module.getCode().equals(code));
    }

    /**
     * Adds a module to the address book.
     * The module must not already exist in the address book.
     */
    public void addModule(Module moduleToAdd) {
        modules.add(moduleToAdd);
        indicateModified();
    }

    /**
     * Replaces the given module {@code target} in the list with {@code editedModule}.
     * {@code target} must exist in the address book.
     * The module identity of {@code editedModule} must not be the same as another existing module in the address book.
     */
    public void setModule(Module target, Module editedModule) {
        requireNonNull(editedModule);

        modules.setModule(target, editedModule);
    }

    /**
     * Replaces the given module {@code target} in the list with {@code editedModule}.
     * {@code target} must exist in the address book.
     * The module identity of {@code editedModule} must not be the same as another existing module in the address book.
     */
    public void editModule(Module target, Module editedModule) {
        requireNonNull(editedModule);

        modules.setModule(target, editedModule);
        if (!target.getCode().equals(editedModule.getCode())) {
            cascadeEditedCodeInDegreePlanners(target.getCode(), editedModule.getCode());
            cascadeEditedCodeInRequirementCategories(target.getCode(), editedModule.getCode());
        }

        indicateModified();
    }

    /**
     * Cascades the edited module code by updating {@code UniqueDegreePlannerList} accordingly
     * @param codeToEdit module code to edit/find
     * @param editedCode module code to replace with
     */
    private void cascadeEditedCodeInDegreePlanners(Code codeToEdit, Code editedCode) {
        ObservableList<DegreePlanner> degreePlanners = getDegreePlannerList();

        for (DegreePlanner degreePlanner : degreePlanners) {
            if (degreePlanner.getCodes().contains(codeToEdit)) {
                Set<Code> editedDegreePlannerCodes = new HashSet<>(degreePlanner.getCodes());
                editedDegreePlannerCodes.remove(codeToEdit);
                editedDegreePlannerCodes.add(editedCode);

                DegreePlanner editedDegreePlanner = new DegreePlanner(
                        degreePlanner.getYear(),
                        degreePlanner.getSemester(),
                        editedDegreePlannerCodes
                );

                setDegreePlanner(degreePlanner, editedDegreePlanner);
            }
        }
    }

    /**
     * Cascades the edited module code by updating {@code UniqueRequirementCategoryList} accordingly
     * @param codeToEdit module code to edit/find
     * @param editedCode module code to replace with
     */
    private void cascadeEditedCodeInRequirementCategories(Code codeToEdit, Code editedCode) {
        ObservableList<RequirementCategory> requirementCategories = getRequirementCategoryList();

        for (RequirementCategory requirementCategory : requirementCategories) {
            if (requirementCategory.getCodeSet().contains(codeToEdit)) {
                Set<Code> editedCodes = new HashSet<>(requirementCategory.getCodeSet());
                editedCodes.remove(codeToEdit);
                editedCodes.add(editedCode);

                RequirementCategory editedRequirementCategory = new RequirementCategory(
                        requirementCategory.getName(),
                        requirementCategory.getCredits(),
                        editedCodes
                );

                setRequirementCategory(requirementCategory, editedRequirementCategory);
            }
        }
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * {@code key} must exist in the address book.
     */
    public void removeModule(Module key) {
        modules.remove(key);
        cascadeDeletedCodeInDegreePlanners(key.getCode());
        cascadeDeletedCodeInRequirementCategories(key.getCode());
        indicateModified();
    }

    /**
     * Cascades the deleted module code by removing it from {@code UniqueDegreePlannerList} accordingly
     * @param codeToDelete module code to delete
     */
    private void cascadeDeletedCodeInDegreePlanners(Code codeToDelete) {
        ObservableList<DegreePlanner> degreePlanners = getDegreePlannerList();
        for (DegreePlanner degreePlanner : degreePlanners) {
            if (degreePlanner.getCodes().contains(codeToDelete)) {
                Set<Code> editedCodes = new HashSet<>(degreePlanner.getCodes());
                editedCodes.remove(codeToDelete);

                DegreePlanner editedDegreePlanner = new DegreePlanner(
                        degreePlanner.getYear(),
                        degreePlanner.getSemester(),
                        editedCodes
                );

                setDegreePlanner(degreePlanner, editedDegreePlanner);
            }
        }
    }

    /**
     * Cascades the deleted module code by removing it from {@code UniqueRequirementCategoryList} accordingly
     * @param codeToDelete module code to delete
     */
    private void cascadeDeletedCodeInRequirementCategories(Code codeToDelete) {
        ObservableList<RequirementCategory> requirementCategories = getRequirementCategoryList();
        for (RequirementCategory requirementCategory : requirementCategories) {
            if (requirementCategory.getCodeSet().contains(codeToDelete)) {
                Set<Code> editedCodes = new HashSet<>(requirementCategory.getCodeSet());
                editedCodes.remove(codeToDelete);

                RequirementCategory editedRequirementCategory = new RequirementCategory(
                        requirementCategory.getName(),
                        requirementCategory.getCredits(),
                        editedCodes
                );

                setRequirementCategory(requirementCategory, editedRequirementCategory);
            }
        }
    }

    //// planner-level operations

    /**
     * Returns true if a degree planner with the same identity as {@code degreePlanner} exists in the
     * degree planner.
     */
    public boolean hasDegreePlanner(DegreePlanner degreePlanner) {
        requireNonNull(degreePlanner);
        return degreePlanners.contains(degreePlanner);
    }

    /**
     * Adds a degree planner to the degree planner list.
     * The degree planner must not already exist in the degree planner list.
     */
    public void addDegreePlanner(DegreePlanner degreePlanner) {
        degreePlanners.add(degreePlanner);
    }

    /**
     * Returns true if a {@code Module} with the specified {@code Code} exists in the application.
     */
    public boolean existingApplicationModules(Code plannerCode) {
        requireNonNull(plannerCode);
        return modules.asUnmodifiableObservableList().stream().anyMatch((module) ->
                module.getCode().equals(plannerCode));
    }

    /**
     * Returns true if a {@code Module} with the specified {@code Code} exists in the entire degree planner.
     */
    public boolean existingPlannerModules(Code plannerCode) {
        requireNonNull(plannerCode);
        Set<Code> existingCodes = new HashSet<>();

        Stream<DegreePlanner> stream = StreamSupport.stream(degreePlanners.spliterator(), false);
        Set<DegreePlanner> streamSet = stream.collect(Collectors.toSet());;
        for (DegreePlanner existingCode : streamSet) {
            existingCodes.addAll(existingCode.getCodes());
        }
        return existingCodes.stream().anyMatch((existingCode) -> existingCode.equals(plannerCode));
    }

    /**
     * Replaces the given degree planner {@code target} in the list with {@code editedDegreePlanner}.
     * {@code target} must exist in the degree planner list.
     * The identity of {@code editedDegreePlanner} must not be the same as another existing degree planner
     * in the degree planner list.
     */
    public void setDegreePlanner(DegreePlanner target, DegreePlanner editedDegreePlanner) {
        requireNonNull(editedDegreePlanner);
        degreePlanners.setDegreePlanner(target, editedDegreePlanner);
    }

    /**
     * Removes {@code key} from this {@code DegreePlannerList}.
     * {@code key} must exist in the degree planner list.
     */
    public void removeDegreePlanner(DegreePlanner key) {
        degreePlanners.remove(key);
    }

    /**
     * Gets the degree planner based on year and semester.
     */
    public DegreePlanner getDegreePlanner(DegreePlanner degreePlanner) {
        requireNonNull(degreePlanner);
        return degreePlanners.getDegreePlanner(degreePlanner);
    }

    //// requirement-level operations

    /**
     * Returns true if a requirement with the name as {@code requirement} exists in the
     * requirement list.
     */
    public boolean hasRequirementCategory(Name requirementCategoryName) {
        requireNonNull(requirementCategoryName);
        return requirementCategories.contains(requirementCategoryName);
    }

    /**
     * Returns true if a requirement object {@code requirement} exists in the
     * requirement list.
     */
    public boolean hasRequirementCategory(RequirementCategory requirementCategory) {
        requireNonNull(requirementCategory);
        return requirementCategories.contains(requirementCategory);
    }

    /**
     * Returns true if an requirement with the same identity as {@code requirement} exists in the
     * requirement.
     */
    public RequirementCategory getRequirementCategory(Name requirementCategoryName) {
        requireNonNull(requirementCategoryName);
        return requirementCategories.getRequirementCategory(requirementCategoryName);
    }

    /**
     * Adds a requirement to the requirementCategoryList.
     * The requirement must not already exist in the requirementCategoryList.
     */
    public void addRequirementCategory(RequirementCategory requirementCategory) {
        requirementCategories.add(requirementCategory);
    }

    /**
     * Replaces the given requirement {@code target} in the list with {@code editedRequirementCategory}.
     * {@code target} must exist in the requirement list.
     * The identity of {@code editedRequirementCategory} must not be the same as another existing requirement
     * in the
     * requirement list.
     */
    public void setRequirementCategory(RequirementCategory target, RequirementCategory editedRequirementCategory) {
        requireNonNull(editedRequirementCategory);

        requirementCategories.setRequirementCategory(target, editedRequirementCategory);
    }

    /**
     * Removes {@code key} from this {@code RequirementCategoryList}.
     * {@code key} must exist in the requirement list.
     */
    public void removeRequirementCategory(RequirementCategory key) {
        requirementCategories.remove(key);
    }

    //// listener methods

    @Override
    public void addListener(InvalidationListener listener) {
        invalidationListenerManager.addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        invalidationListenerManager.removeListener(listener);
    }

    /**
     * Notifies listeners that the address book has been modified.
     */
    protected void indicateModified() {
        invalidationListenerManager.callListeners(this);
    }

    //// util methods

    @Override
    public String toString() {
        return modules.asUnmodifiableObservableList().size() + " modules \n"
                + degreePlanners.asUnmodifiableObservableList().size() + " degree planners \n"
                + requirementCategories.asUnmodifiableObservableList().size() + " requirementCategories";
    }

    @Override
    public ObservableList<Module> getModuleList() {
        return modules.asUnmodifiableObservableList();
    }

    @Override
    public ObservableList<DegreePlanner> getDegreePlannerList() {
        return degreePlanners.asUnmodifiableObservableList();
    }

    @Override
    public ObservableList<RequirementCategory> getRequirementCategoryList() {
        return requirementCategories.asUnmodifiableObservableList();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof AddressBook // instanceof handles nulls
                && modules.equals(((AddressBook) other).modules)
                && degreePlanners.equals(((AddressBook) other).degreePlanners)
                && requirementCategories.equals(((AddressBook) other).requirementCategories));
    }

    @Override
    public int hashCode() {
        return Objects.hash(modules.hashCode(), degreePlanners.hashCode(), requirementCategories.hashCode());
    }
}
