package pwe.planner.model;

import javafx.beans.Observable;
import javafx.collections.ObservableList;
import pwe.planner.model.module.Module;
import pwe.planner.model.planner.DegreePlanner;
import pwe.planner.model.requirement.RequirementCategory;

/**
 * Unmodifiable view of an application
 */
public interface ReadOnlyApplication extends Observable {

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
