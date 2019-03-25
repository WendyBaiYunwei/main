package seedu.address.model;

import javafx.beans.Observable;
import javafx.collections.ObservableList;
import seedu.address.model.module.Module;
import seedu.address.model.planner.DegreePlanner;
import seedu.address.model.requirement.RequirementCategory;

/**
 * Unmodifiable view of an address book
 */
public interface ReadOnlyAddressBook extends Observable {

    /**
     * Returns an unmodifiable view of the modules list.
     * This list will not contain any duplicate modules.
     */
    ObservableList<Module> getModuleList();

    /**
     * Returns an unmodifiable view of the degree planner list.
     * This list will not contain any duplicate degree planner.
     */
    ObservableList<DegreePlanner> getDegreePlannerList();

    /**
     * Returns an unmodifiable view of the requirementCategories list.
     * This list will not contain any duplicate requirementCategories.
     */
    ObservableList<RequirementCategory> getRequirementCategoryList();

}
