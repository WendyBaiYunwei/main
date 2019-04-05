package pwe.planner.model.planner;
import java.util.Comparator;

/**
 * Sorts ModuleToSuggest according to number of matching tags first.
 * Sorts according to the lowest credits difference if tie.
 */
public class SortModulesToSuggest implements Comparator<ModuleToSuggest> {
    public int compare(ModuleToSuggest moduleA, ModuleToSuggest moduleB) {
        if (moduleA.getNumberOfMatchingTags() == moduleB.getNumberOfMatchingTags()) {
            return moduleA.getCreditDifference() - moduleB.getCreditDifference();
        }
        return moduleB.getNumberOfMatchingTags() - moduleA.getNumberOfMatchingTags();
    }
}
