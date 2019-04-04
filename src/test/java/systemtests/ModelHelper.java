package systemtests;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import pwe.planner.model.Model;
import pwe.planner.model.module.Module;

/**
 * Contains helper methods to set up {@code Model} for testing.
 */
public class ModelHelper {
    private static final Predicate<Module> PREDICATE_MATCHING_NO_MODULES = unused -> false;

    /**
     * Updates {@code model}'s filtered list to display only {@code toDisplay}.
     */
    public static void setFilteredList(Model model, List<Module> toDisplay) {
        Optional<Predicate<Module>> predicate =
                toDisplay.stream().map(ModelHelper::getPredicateMatching).reduce(Predicate::or);
        model.updateFilteredModuleList(predicate.orElse(PREDICATE_MATCHING_NO_MODULES));
    }

    /**
     * @see ModelHelper#setFilteredList(Model, List)
     */
    public static void setFilteredList(Model model, Module... toDisplay) {
        setFilteredList(model, Arrays.asList(toDisplay));
    }

    /**
     * Returns a predicate that evaluates to true if this {@code Module} equals to {@code other}.
     */
    private static Predicate<Module> getPredicateMatching(Module other) {
        return module -> module.equals(other);
    }
}
