package pwe.planner.model.planner;
import java.util.Comparator;

/**
 * Sorts ModuleToSuggest according to number of matching tags first.
 * Sorts according to the lowest credits difference if tie.
 */
public class SortModulesToSuggest implements Comparator<ModuleToSuggest> {
    /**
     * @param moduleA A valid moduleA to suggest
     * @param moduleB A valid moduleB to suggest
     * @return number of matching tags difference between two modules to suggest, or
     * credit difference between two modules if tie.
     */
    public int compare(ModuleToSuggest moduleA, ModuleToSuggest moduleB) {
        if (moduleA.getNumberOfMatchingTags() == moduleB.getNumberOfMatchingTags()) {
            return moduleA.getCreditDifference() - moduleB.getCreditDifference();
        }
        return moduleB.getNumberOfMatchingTags() - moduleA.getNumberOfMatchingTags();
    }
}
