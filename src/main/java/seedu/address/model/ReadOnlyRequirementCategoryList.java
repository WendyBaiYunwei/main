package seedu.address.model;

import javafx.beans.Observable;
import javafx.collections.ObservableList;
import seedu.address.model.requirement.RequirementCategory;

/**
 * Unmodifiable view of requirement list
 */
public interface ReadOnlyRequirementCategoryList extends Observable {

    /**
     * Returns an unmodifiable view of the requirement list.
     * This list will not contain any duplicate requirement.
     */
    ObservableList<RequirementCategory> getRequirementCategoryList();

}
