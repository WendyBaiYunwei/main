package seedu.address.model.requirement;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.Model;
import seedu.address.model.module.Code;
import seedu.address.model.module.Module;
import seedu.address.model.requirement.exceptions.DuplicateRequirementCategoryException;
import seedu.address.model.requirement.exceptions.RequirementCategoryNotFoundException;

/**
 * A list of requirement that enforces uniqueness between its elements and does not allow nulls.
 * A code is considered unique by comparing using {@code RequirementCategory#isSameRequirementCategory
 * (RequirementCategory)}. As such,
 * adding and updating of requirement uses RequirementCategory#isSameRequirementCategory(RequirementCategory)
 * for equality so as to
 * ensure that the requirement being added or updated is unique in terms of identity in the
 * UniqueRequirementCategoryList.
 * However, the removal of a requirement uses RequirementCategory#equals(Object) so as to ensure that the
 * requirement with
 * exactly
 * the same fields will be removed.
 * <p>
 * Supports a minimal set of list operations.
 *
 * @see RequirementCategory#isSameRequirementCategory(RequirementCategory)
 */
public class UniqueRequirementCategoryList implements Iterable<RequirementCategory> {

    private final ObservableList<RequirementCategory> internalList = FXCollections.observableArrayList();
    private final ObservableList<RequirementCategory> internalUnmodifiableList =
            FXCollections.unmodifiableObservableList(internalList);

    /**
     * Returns true if the list contains an equivalent requirement as the given argument.
     */
    public boolean contains(RequirementCategory toCheck) {
        requireNonNull(toCheck);
        return internalList.stream().anyMatch(toCheck::isSameRequirementCategory);
    }

    /**
     * Returns location of the requirementCategory in the internalList.
     */
    public int location(RequirementCategory toCheck) {
        requireNonNull(toCheck);
        int location = 0;
        for (int i = 0; i < internalList.size(); i++) {
            if (toCheck.getName().equals(internalList.get(i).getName())) {
                location = i;
                i = internalList.size();
            }
        }
        return location;
    }

    /**
     * Adds a requirement to the list.
     * The requirement must not already exist in the list.
     */
    public void add(RequirementCategory toAdd) {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw new DuplicateRequirementCategoryException();
        }
        internalList.add(toAdd);
    }

    /**
     * Adds a module to a requirement category.
     * The module must not already exist in the requirement category.
     */
    public void addModuleToRequirementCategory(RequirementCategory toAdd) {
        requireNonNull(toAdd);
        int location = location(toAdd);
        Set<Code> currentListInRequirementCategory = internalList.get(location).getCodeList();
        Set<Code> inputList = toAdd.getCodeList();

        for (Code currentCode : currentListInRequirementCategory) {
            inputList.add(currentCode);
        }

        RequirementCategory edited =
                new RequirementCategory(internalList.get(location).getName(), internalList.get(location).getCredits(),
                        inputList);

        setRequirementCategory(internalList.get(location), edited);
    }

    /**
     * Returns true if the list contains an equivalent requirement as the given argument.
     */
    public boolean isModuleInRequirementCategory(RequirementCategory toCheck) {
        int location = location(toCheck);
        Set<Code> currentList = internalList.get(location).getCodeList();
        Set<Code> inputList = toCheck.getCodeList();

        for (Code existingCode : currentList) {
            for (Code newCode : inputList) {
                if (existingCode.value.equals(newCode.value)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if the list contains an equivalent requirement as the given argument.
     */
    //TODO Refine the method so that it uses the implementation in PR #91
    public boolean doesModuleExistInApplication(RequirementCategory toCheck, Model model) {
        ObservableList<Module> existingModules = model.getFilteredModuleList();
        Set<Code> inputList = toCheck.getCodeList();
        Set<Code> outputList = new HashSet<>();
        boolean checker = true;

        for (Module existingModule : existingModules) {
            for (Code newCode : inputList) {
                if (existingModule.getCode().value.equals(newCode.value)) {
                    outputList.add(newCode);
                }
            }
        }

        if (outputList.size() != inputList.size()) {
            checker = false;
        }

        return checker;
    }

    /**
     * Replaces the requirement {@code target} in the list with {@code editedRequirementCategory}.
     * {@code target} must exist in the list.
     * The requirement identity of {@code editedRequirementCategory} must not be the same as another existing
     * requirement
     * in the list.
     */
    public void setRequirementCategory(RequirementCategory target, RequirementCategory editedRequirementCategory) {
        requireAllNonNull(target, editedRequirementCategory);

        int index = internalList.indexOf(target);
        if (index == -1) {
            throw new RequirementCategoryNotFoundException();
        }

        if (!target.isSameRequirementCategory(editedRequirementCategory) && contains(editedRequirementCategory)) {
            throw new DuplicateRequirementCategoryException();
        }

        internalList.set(index, editedRequirementCategory);
    }

    /**
     * Removes the equivalent requirement from the list.
     * The requirement module must exist in the list.
     */
    public void remove(RequirementCategory toRemove) {
        requireNonNull(toRemove);
        if (!internalList.remove(toRemove)) {
            throw new RequirementCategoryNotFoundException();
        }
    }

    public void setRequirementCategories(UniqueRequirementCategoryList replacement) {
        requireNonNull(replacement);
        internalList.setAll(replacement.internalList);
    }

    /**
     * Replaces the contents of this list with {@code requirement}.
     * {@code requirement} must not contain duplicate requirement.
     */
    public void setRequirementCategories(List<RequirementCategory> requirementCategories) {
        requireAllNonNull(requirementCategories);
        if (!requirementCategoriesAreUnique(requirementCategories)) {
            throw new DuplicateRequirementCategoryException();
        }

        internalList.setAll(requirementCategories);
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<RequirementCategory> asUnmodifiableObservableList() {
        return internalUnmodifiableList;
    }

    @Override
    public Iterator<RequirementCategory> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof UniqueRequirementCategoryList // instanceof handles nulls
                && internalList.equals(((UniqueRequirementCategoryList) other).internalList));
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }

    /**
     * Returns true if {@code requirement} contains only unique requirement modules.
     */
    private boolean requirementCategoriesAreUnique(List<RequirementCategory> requirementCategories) {
        for (int i = 0; i < requirementCategories.size() - 1; i++) {
            for (int j = i + 1; j < requirementCategories.size(); j++) {
                if (requirementCategories.get(i).isSameRequirementCategory(requirementCategories.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }
}
