package pwe.planner.model.planner;

import pwe.planner.model.module.Code;

/**
 * Creates a ModuleToSuggest object for sorting.
 */
public class ModuleToSuggest {
    private int creditDifference;
    private int numberOfMatchingTags;
    private Code moduleCode;

    public ModuleToSuggest(int creditDifference, int numberOfMatchingTags, Code moduleCode) {
        this.creditDifference = creditDifference;
        this.numberOfMatchingTags = numberOfMatchingTags;
        this.moduleCode = moduleCode;
    }

    public int getCreditDifference() {
        return creditDifference;
    }

    public int getNumberOfMatchingTags() {
        return numberOfMatchingTags;
    }

    public Code getModuleCode() {
        return moduleCode;
    }
}
