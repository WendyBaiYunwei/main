package pwe.planner.model.requirement;

import static java.util.Objects.requireNonNull;
import static pwe.planner.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Iterator;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import pwe.planner.commons.util.StringUtil;
import pwe.planner.model.module.Name;
import pwe.planner.model.requirement.exceptions.DuplicateRequirementCategoryException;
import pwe.planner.model.requirement.exceptions.RequirementCategoryNotFoundException;

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
     * Returns true if the list contains an equivalent Name as the given argument.
     */
    public boolean contains(Name toCheck) {
        requireNonNull(toCheck);

        return internalList.stream().map(RequirementCategory::getName).anyMatch(toCheck::equals);
    }

    /**
     * Returns true if the list contains an equivalent RequirementCategory as the given argument.
     */
    public boolean contains(RequirementCategory toCheck) {
        requireNonNull(toCheck);

        return internalList.stream().anyMatch(toCheck::isSameRequirementCategory);
    }

    /**
     * Returns a RequirementCategory object of the requirementCategory in the internalList.
     */
    public RequirementCategory getRequirementCategory(Name toCheck) {
        requireNonNull(toCheck);

        return internalList.stream()
                .filter(requirementCategory -> StringUtil
                        .compareEqualsIgnoreCase(requirementCategory.getName().toString(), toCheck.toString()))
                .findFirst()
                .orElse(null);
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
