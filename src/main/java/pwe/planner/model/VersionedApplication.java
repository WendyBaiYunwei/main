package pwe.planner.model;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code Application} that keeps track of its own history.
 */
public class VersionedApplication extends Application {

    private final List<ReadOnlyApplication> applicationStateList;
    private int currentStatePointer;

    public VersionedApplication(ReadOnlyApplication initialState) {
        super(initialState);
        requireNonNull(initialState);

        applicationStateList = new ArrayList<>();
        applicationStateList.add(new Application(initialState));
        currentStatePointer = 0;
    }

    /**
     * Saves a copy of the current {@code Application} state at the end of the state list.
     * Undone states are removed from the state list.
     */
    public void commit() {
        removeStatesAfterCurrentPointer();
        applicationStateList.add(new Application(this));
        currentStatePointer++;
        indicateModified();
    }

    private void removeStatesAfterCurrentPointer() {
        applicationStateList.subList(currentStatePointer + 1, applicationStateList.size()).clear();
    }

    /**
     * Restores the application to its previous state.
     */
    public void undo() {
        if (!canUndo()) {
            throw new NoUndoableStateException();
        }
        currentStatePointer--;
        resetData(applicationStateList.get(currentStatePointer));
    }

    /**
     * Restores the application to its previously undone state.
     */
    public void redo() {
        if (!canRedo()) {
            throw new NoRedoableStateException();
        }
        currentStatePointer++;
        resetData(applicationStateList.get(currentStatePointer));
    }

    /**
     * Returns true if {@code undo()} has application states to undo.
     */
    public boolean canUndo() {
        return currentStatePointer > 0;
    }

    /**
     * Returns true if {@code redo()} has application states to redo.
     */
    public boolean canRedo() {
        return currentStatePointer < applicationStateList.size() - 1;
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof VersionedApplication)) {
            return false;
        }

        VersionedApplication otherVersionedApplication = (VersionedApplication) other;

        // state check
        return super.equals(otherVersionedApplication)
                && applicationStateList.equals(otherVersionedApplication.applicationStateList)
                && currentStatePointer == otherVersionedApplication.currentStatePointer;
    }

    /**
     * Thrown when trying to {@code undo()} but can't.
     */
    public static class NoUndoableStateException extends RuntimeException {
        private NoUndoableStateException() {
            super("Current state pointer at start of applicationState list, unable to undo.");
        }
    }

    /**
     * Thrown when trying to {@code redo()} but can't.
     */
    public static class NoRedoableStateException extends RuntimeException {
        private NoRedoableStateException() {
            super("Current state pointer at end of applicationState list, unable to redo.");
        }
    }
}
