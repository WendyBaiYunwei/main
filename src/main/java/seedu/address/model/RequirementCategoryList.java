package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import seedu.address.commons.util.InvalidationListenerManager;
import seedu.address.model.requirementCategory.RequirementCategory;
import seedu.address.model.requirementCategory.UniqueRequirementCategoryList;

/**
 * Wraps all data at the requirementCategory-list level
 * Duplicates are not allowed (by .isSameRequirementCategory comparison)
 */

public class RequirementCategoryList implements ReadOnlyRequirementCategoryList {

    private final UniqueRequirementCategoryList requirementCategories;
    private final InvalidationListenerManager invalidationListenerManager = new InvalidationListenerManager();

    public RequirementCategoryList() {
        requirementCategories = new UniqueRequirementCategoryList();
    }

    /**
     * @param toBeCopied
     */
    public RequirementCategoryList(ReadOnlyRequirementCategoryList toBeCopied) {
        requirementCategories = new UniqueRequirementCategoryList();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the requirementCategory list with {@code requirementCategory}.
     * {@code requirementCategory} must not contain duplicate requirementCategory.
     */
    public void setRequirementCategories(List<RequirementCategory> requirementCategories) {
        this.requirementCategories.setRequirementCategories(requirementCategories);
    }

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyRequirementCategoryList newData) {
        requireNonNull(newData);
        this.setRequirementCategories(newData.getRequirementCategoryList());
    }

    //// planner-level operations

    /**
     * Returns true if an requirementCategory with the same identity as {@code requirementCategory} exists in the
     * requirementCategory.
     */
    public boolean hasRequirementCategory(RequirementCategory requirementCategory) {
        requireNonNull(requirementCategory);
        return requirementCategories.contains(requirementCategory);
    }

    /**
     * Adds a requirementCategory to the requirementCategoryList.
     * The requirementCategory must not already exist in the requirementCategoryList.
     */
    public void addRequirementCategory(RequirementCategory p) {
        requirementCategories.add(p);
    }

    /**
     * Replaces the given requirementCategory {@code target} in the list with {@code editedRequirementCategory}.
     * {@code target} must exist in the requirementCategory list.
     * The identity of {@code editedRequirementCategory} must not be the same as another existing requirementCategory
     * in the
     * requirementCategory list.
     */
    public void setRequirementCategory(RequirementCategory target, RequirementCategory editedRequirementCategory) {
        requireNonNull(editedRequirementCategory);

        requirementCategories.setRequirementCategory(target, editedRequirementCategory);
    }

    /**
     * Removes {@code key} from this {@code RequirementCategoryList}.
     * {@code key} must exist in the requirementCategory list.
     */
    public void removeRequirementCategory(RequirementCategory key) {
        requirementCategories.remove(key);
    }

    //// util methods

    @Override
    public String toString() {
        return requirementCategories.asUnmodifiableObservableList().size() + " requirementCategory";
    }

    public ObservableList<RequirementCategory> getRequirementCategoryList() {
        return requirementCategories.asUnmodifiableObservableList();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof RequirementCategoryList// instanceof handles nulls
                && requirementCategories.equals(((RequirementCategoryList) other).requirementCategories));
    }

    @Override
    public int hashCode() {
        return requirementCategories.hashCode();
    }

    @Override public void addListener(InvalidationListener listener) {
        invalidationListenerManager.addListener(listener);
    }

    @Override public void removeListener(InvalidationListener listener) {
        invalidationListenerManager.removeListener(listener);
    }

    /**
     * Notifies listeners that the address book has been modified.
     */
    protected void indicateModified() {
        invalidationListenerManager.callListeners(this);
    }
}
