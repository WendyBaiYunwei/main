package seedu.address.model;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code RequirementCategoryList} that keeps track of its own history.
 */
public class VersionedRequirementCategoryList extends RequirementCategoryList {

    private final List<ReadOnlyRequirementCategoryList> requirementCategoryStateList;
    private int currentStatePointer;

    public VersionedRequirementCategoryList(ReadOnlyRequirementCategoryList initialState) {
        super(initialState);

        requirementCategoryStateList = new ArrayList<>();
        requirementCategoryStateList.add(new RequirementCategoryList(initialState));
        currentStatePointer = 0;
    }

    /**
     * Saves a copy of the current {@code RequirementCategoryList} state at the end of the state list.
     * Undone states are removed from the state list.
     */
    public void commit() {
        removeStatesAfterCurrentPointer();
        requirementCategoryStateList.add(new RequirementCategoryList(this));
        currentStatePointer++;
        indicateModified();
    }

    private void removeStatesAfterCurrentPointer() {
        requirementCategoryStateList.subList(currentStatePointer + 1, requirementCategoryStateList.size()).clear();
    }

    /**
     * Restores the requirement list to its previous state.
     */
    public void undo() {
        if (!canUndo()) {
            throw new VersionedRequirementCategoryList.NoUndoableStateException();
        }
        currentStatePointer--;
        resetData(requirementCategoryStateList.get(currentStatePointer));
    }

    /**
     * Restores the requirement list to its previously undone state.
     */
    public void redo() {
        if (!canRedo()) {
            throw new VersionedRequirementCategoryList.NoRedoableStateException();
        }
        currentStatePointer++;
        resetData(requirementCategoryStateList.get(currentStatePointer));
    }

    /**
     * Returns true if {@code undo()} has requirement states to undo.
     */
    public boolean canUndo() {
        return currentStatePointer > 0;
    }

    /**
     * Returns true if {@code redo()} has requirement states to redo.
     */
    public boolean canRedo() {
        return currentStatePointer < requirementCategoryStateList.size() - 1;
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof VersionedRequirementCategoryList)) {
            return false;
        }

        VersionedRequirementCategoryList otherVersionedRequirementList = (VersionedRequirementCategoryList) other;

        // state check
        return super.equals(otherVersionedRequirementList)
                && requirementCategoryStateList.equals(otherVersionedRequirementList.requirementCategoryStateList)
                && currentStatePointer == otherVersionedRequirementList.currentStatePointer;
    }

    /**
     * Thrown when trying to {@code undo()} but can't.
     */
    public static class NoUndoableStateException extends RuntimeException {
        private NoUndoableStateException() {
            super("Current state pointer at start of addressBookState list, unable to undo.");
        }
    }

    /**
     * Thrown when trying to {@code redo()} but can't.
     */
    public static class NoRedoableStateException extends RuntimeException {
        private NoRedoableStateException() {
            super("Current state pointer at end of addressBookState list, unable to redo.");
        }
    }
}
