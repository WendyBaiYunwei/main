package pwe.planner.model.util;

import java.util.HashSet;
import java.util.Set;

import pwe.planner.model.module.Code;
import pwe.planner.model.requirement.RequirementCategory;

/**
 * A utility class for RequirementCategory.
 */
public class RequirementCategoryUtil {

    /**
     * Returns a new requirement category object with the specified code {@code toMove} removed from the
     * requirement category provided {@code reqCat}
     */
    public static RequirementCategory getRequirementCategoryWithCodeRemoved(RequirementCategory reqCat, Code toMove) {
        Set<Code> newCodeSet = new HashSet<>(reqCat.getCodeSet());
        newCodeSet.remove(toMove);
        RequirementCategory requirementCategory = new RequirementCategory(reqCat.getName(), reqCat.getCredits(),
                newCodeSet);

        return requirementCategory;
    }

    /**
     * Returns a new requirement category object with the specified code set {@code toMove} removed from the
     * requirement category provided {@code reqCat}
     */
    public static RequirementCategory getRequirementCategoryWithCodesRemoved(RequirementCategory reqCat,
            Set<Code> setToMove) {
        Set<Code> newCodeSet = new HashSet<>(reqCat.getCodeSet());
        newCodeSet.removeAll(setToMove);
        RequirementCategory requirementCategory = new RequirementCategory(reqCat.getName(), reqCat.getCredits(),
                newCodeSet);

        return requirementCategory;
    }

    /**
     * Returns a new requirement category object with the specified code {@code toMove} added from the
     * requirement category provided {@code reqCat}
     */
    public static RequirementCategory getRequirementCategoryWithCodeAdded(RequirementCategory reqCat, Code toMove) {
        Set<Code> newCodeSet = new HashSet<>(reqCat.getCodeSet());
        newCodeSet.add(toMove);
        RequirementCategory requirementCategory = new RequirementCategory(reqCat.getName(), reqCat.getCredits(),
                newCodeSet);

        return requirementCategory;
    }

    /**
     * Returns a new requirement category object with the specified code set {@code toMove} added from the
     * requirement category provided {@code reqCat}
     */
    public static RequirementCategory getRequirementCategoryWithCodesAdded(RequirementCategory reqCat,
            Set<Code> setToMove) {
        Set<Code> newCodeSet = new HashSet<>(reqCat.getCodeSet());
        newCodeSet.addAll(setToMove);
        RequirementCategory requirementCategory = new RequirementCategory(reqCat.getName(), reqCat.getCredits(),
                newCodeSet);

        return requirementCategory;
    }

}
