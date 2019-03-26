package seedu.address.model.planner;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import seedu.address.model.module.Code;
import seedu.address.model.planner.exceptions.DegreePlannerNotFoundException;
import seedu.address.model.planner.exceptions.DuplicateDegreePlannerException;

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
     * Returns true if the list contains an equivalent planner module as the given argument.
     */
    public boolean contains(DegreePlanner toCheck) {
        requireNonNull(toCheck);
        return internalList.stream().anyMatch(toCheck::isSameDegreePlanner);
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
     * Returns location of the degree planner to add to in the internalList.
     */
    public int location(DegreePlanner toCheck) {
        requireNonNull(toCheck);
        for (int i = 0; i < internalList.size(); i++) {
            if (toCheck.getYear().equals(internalList.get(i).getYear())
                    && toCheck.getSemester().equals(internalList.get(i).getSemester())) {
                return i;
            }
        }
        return -1; // Year and Semester input are guaranteed to be valid through previous checks.
        // Meanwhile, internalList also has the matching year and semester. Thus this line is a dummy line for syntax
        // purpose.
    }

    /**
     * Adds the new module to the particular degree planner with matching year and semester
     */
    public void addModules(DegreePlanner toAdd) {
        requireNonNull(toAdd);
        int location = location(toAdd);
        Set<Code> inputList = new HashSet<>();
        inputList.addAll(toAdd.getCodes());
        Set<Code> currentDegreePlanner = internalList.get(location).getCodes();
        inputList.addAll(currentDegreePlanner);
        DegreePlanner edited = new DegreePlanner(toAdd.getYear(), toAdd.getSemester(), inputList);
        setDegreePlanner(internalList.get(location), edited);
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
    /**
     * Returns true if the modules to add are contained inside the entire degree planner.
     */
    public boolean containsModules(DegreePlanner toCheck) {
        Set<Code> modulesToCheck = toCheck.getCodes();

        requireNonNull(toCheck);
        for (int i = 0; i < internalList.size(); i++) {
            for (Code currentCode : internalList.get(i).getCodes()) {
                for (Code codeToAdd : modulesToCheck) {
                    if (currentCode.equals(codeToAdd)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
