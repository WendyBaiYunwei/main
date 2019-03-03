package seedu.address.model;

import javafx.beans.Observable;
import javafx.collections.ObservableList;
import seedu.address.model.planner.DegreePlanner;

/**
 * Unmodifiable view of planner list
 */
public interface ReadOnlyDegreePlannerList extends Observable {

    /**
     * Returns an unmodifiable view of the degreePlanner list.
     * This list will not contain any duplicate degreePlanner.
     */
    ObservableList<DegreePlanner> getDegreePlannerList();

}
