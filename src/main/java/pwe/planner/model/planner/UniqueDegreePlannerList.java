package pwe.planner.model.planner;

import static java.util.Objects.requireNonNull;
import static pwe.planner.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Iterator;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import pwe.planner.model.module.Code;
import pwe.planner.model.planner.exceptions.DegreePlannerNotFoundException;
import pwe.planner.model.planner.exceptions.DuplicateDegreePlannerException;

/**
 * A list of degreePlanners that enforces uniqueness between its elements and does not allow nulls.
 * A code is considered unique by comparing using {@code DegreePlanner#isSameDegreePlanner(DegreePlanner)}. As such,
 * adding and updating of degreePlanners uses DegreePlanner#isSameDegreePlanner(DegreePlanner) for equality so as to
 * ensure that the degreePlanner being added or updated is unique in terms of identity in the UniqueDegreePlannerList.
 * However, the removal of a module uses DegreePlanner#equals(Object) so as to ensure that the module with exactly
 * the same fields will be removed.
 * <p>
 * Supports a minimal set of list operations.
 *
 * @see DegreePlanner#isSameDegreePlanner(DegreePlanner)
 */
public class UniqueDegreePlannerList implements Iterable<DegreePlanner> {

    private final ObservableList<DegreePlanner> internalList = FXCollections.observableArrayList();
    private final ObservableList<DegreePlanner> internalUnmodifiableList =
            FXCollections.unmodifiableObservableList(internalList);

    /**
     * Returns true if the list contains an equivalent degree planner as the given argument.
     */
    public boolean contains(DegreePlanner toCheck) {
        requireNonNull(toCheck);

        return internalList.stream().anyMatch(toCheck::isSameDegreePlanner);
    }

    /**
     * Returns a DegreePlanner object of the degree planner in the internalList if degree planner contains the given
     * code.
     */
    public DegreePlanner getDegreePlannerByCode(Code toCheck) {
        requireNonNull(toCheck);

        return internalList.stream()
                .filter(degreePlanner -> degreePlanner.getCodes().contains(toCheck))
                .findFirst()
                .orElse(null);
    }

    /**
     * Adds a planner module to the list.
     * The planner module must not already exist in the list.
     */
    public void add(DegreePlanner toAdd) {
        requireNonNull(toAdd);

        if (contains(toAdd)) {
            throw new DuplicateDegreePlannerException();
        }
        internalList.add(toAdd);
    }

    /**
     * Replaces the degreePlanner {@code target} in the list with {@code editedDegreePlanner}.
     * {@code target} must exist in the list.
     * The planner module identity of {@code editedDegreePlanner} must not be the same as another existing degreePlanner
     * in the list.
     */
    public void setDegreePlanner(DegreePlanner target, DegreePlanner editedDegreePlanner) {
        requireAllNonNull(target, editedDegreePlanner);

        int index = internalList.indexOf(target);
        if (index == -1) {
            throw new DegreePlannerNotFoundException();
        }

        if (!target.isSameDegreePlanner(editedDegreePlanner) && contains(editedDegreePlanner)) {
            throw new DuplicateDegreePlannerException();
        }

        internalList.set(index, editedDegreePlanner);
    }

    /**
     * Removes the equivalent degreePlanner from the list.
     * The planner module must exist in the list.
     */
    public void remove(DegreePlanner toRemove) {
        requireNonNull(toRemove);

        if (!internalList.remove(toRemove)) {
            throw new DegreePlannerNotFoundException();
        }
    }

    public void setDegreePlanners(UniqueDegreePlannerList replacement) {
        requireNonNull(replacement);

        internalList.setAll(replacement.internalList);
    }

    /**
     * Replaces the contents of this list with {@code degreePlanners}.
     * {@code degreePlanners} must not contain duplicate degreePlanners.
     */
    public void setDegreePlanners(List<DegreePlanner> degreePlanners) {
        requireAllNonNull(degreePlanners);

        if (!degreePlannersAreUnique(degreePlanners)) {
            throw new DuplicateDegreePlannerException();
        }

        internalList.setAll(degreePlanners);
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<DegreePlanner> asUnmodifiableObservableList() {
        return internalUnmodifiableList;
    }

    @Override
    public Iterator<DegreePlanner> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof UniqueDegreePlannerList // instanceof handles nulls
                && internalList.equals(((UniqueDegreePlannerList) other).internalList));
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }

    /**
     * Returns true if {@code degreePlanners} contains only unique degreePlanner.
     */
    private boolean degreePlannersAreUnique(List<DegreePlanner> degreePlanners) {
        for (int i = 0; i < degreePlanners.size() - 1; i++) {
            for (int j = i + 1; j < degreePlanners.size(); j++) {
                if (degreePlanners.get(i).isSameDegreePlanner(degreePlanners.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }
}
