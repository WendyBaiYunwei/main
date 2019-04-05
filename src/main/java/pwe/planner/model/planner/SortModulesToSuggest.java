package pwe.planner.model.planner;
import java.util.Comparator;

/**
 * Sorts ModuleToSuggest according to number of matching tags first.
 * Sorts according to the lowest credits difference if tie.
 */
public class SortModulesToSuggest implements Comparator<ModuleToSuggest> {
    public int compare(ModuleToSuggest a, ModuleToSuggest b) {
        if (a.getNumberOfMatchingTags() == b.getNumberOfMatchingTags()) {
            return a.getCreditDifference() - b.getCreditDifference();
        }
        return b.getNumberOfMatchingTags() - a.getNumberOfMatchingTags();
    }
}
