package seedu.address.model;

import java.util.function.Predicate;

import javafx.collections.ObservableList;
import seedu.address.model.requirement.RequirementCategory;

/**
 * The API of the Model component.
 */
public interface RequirementCategoryModel {
    /**
     * {@code Predicate} that always evaluate to true
     */
    Predicate<RequirementCategory> PREDICATE_SHOW_ALL_REQUIREMENTCATEGORY = unused -> true;

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
     * {@code requirement} must not already exist in the requirement.
     */
    void addRequirementCategory(RequirementCategory requirementCategory);

    /**
     * Replaces the given requirement {@code target} with {@code editedRequirementCategory}.
     * {@code target} must exist in the requirement list.
     * The planner identity of {@code editedRequirementCategory} must not be the same as another existing
     * requirement in the
     * requirementCategoryList.
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
     * Saves the current requirement list state for undo/redo.
     */
    void commitRequirementCategoryList();
}
