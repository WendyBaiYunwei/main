package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import seedu.address.commons.util.InvalidationListenerManager;
import seedu.address.model.planner.DegreePlanner;
import seedu.address.model.planner.UniqueDegreePlannerList;

/**
 * Wraps all data at the planner-list level
 * Duplicates are not allowed (by .isSamePlanner comparison)
 */

public class DegreePlannerList implements ReadOnlyDegreePlannerList {

    private final UniqueDegreePlannerList degreePlanners;
    private final InvalidationListenerManager invalidationListenerManager = new InvalidationListenerManager();

    public DegreePlannerList() {
        degreePlanners = new UniqueDegreePlannerList();
    }

    /**
     * @param toBeCopied
     */
    public DegreePlannerList(ReadOnlyDegreePlannerList toBeCopied) {
        degreePlanners = new UniqueDegreePlannerList();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the planner list with {@code degreePlanners}.
     * {@code degreePlanners} must not contain duplicate degreePlanners.
     */
    public void setDegreePlanners(List<DegreePlanner> degreePlanners) {
        this.degreePlanners.setDegreePlanners(degreePlanners);
    }

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyDegreePlannerList newData) {
        requireNonNull(newData);
        this.setDegreePlanners(newData.getDegreePlannerList());
    }

    //// planner-level operations

    /**
     * Returns true if an degreePlanner with the same identity as {@code degreePlanner} exists in the degreePlanner.
     */
    public boolean hasDegreePlanner(DegreePlanner degreePlanner) {
        requireNonNull(degreePlanner);
        return degreePlanners.contains(degreePlanner);
    }

    /**
     * Returns true if a module with the same identity as {@code degreePlanner} exists in the particular degreePlanner.
     */
    public boolean hasDegreePlannerModules(DegreePlanner plannerModules) {
        requireNonNull(plannerModules);
        return degreePlanners.containsModules(plannerModules);
    }

    /**
     * Adds a degreePlanner to the degreePlanner list.
     * The degreePlanner must not already exist in the degreePlanner list.
     */
    public void addDegreePlanner(DegreePlanner p) {
        degreePlanners.add(p);
    }

    public void addDegreePlannerModule(DegreePlanner p) {
        degreePlanners.addModules(p);
    }

    /**
     * Replaces the given planner {@code target} in the list with {@code editedDegreePlanner}.
     * {@code target} must exist in the degreePlanner list.
     * The identity of {@code editedDegreePlanner} must not be the same as another existing degreePlanner in the
     * degreePlanner list.
     */
    public void setDegreePlanner(DegreePlanner target, DegreePlanner editedDegreePlanner) {
        requireNonNull(editedDegreePlanner);

        degreePlanners.setDegreePlanner(target, editedDegreePlanner);
    }

    /**
     * Removes {@code key} from this {@code DegreePlannerList}.
     * {@code key} must exist in the degreePlanner list.
     */
    public void removeDegreePlanner(DegreePlanner key) {
        degreePlanners.remove(key);
    }

    //// util methods

    @Override
    public String toString() {
        return degreePlanners.asUnmodifiableObservableList().size() + " degreePlanners";
    }

    public ObservableList<DegreePlanner> getDegreePlannerList() {
        return degreePlanners.asUnmodifiableObservableList();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DegreePlannerList// instanceof handles nulls
                && degreePlanners.equals(((DegreePlannerList) other).degreePlanners));
    }

    @Override
    public int hashCode() {
        return degreePlanners.hashCode();
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

    public boolean existingPlannerModules(DegreePlanner toAdd, Model model) {
        return degreePlanners.existingPlannerModules(toAdd, model);
    }

}
