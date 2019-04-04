package pwe.planner.model.module;

import java.util.function.Predicate;

/**
 * {@inheritDoc}
 * This interface let us have a list of predicate of different module's attributes
 */
@FunctionalInterface
public interface KeywordsPredicate extends Predicate<Module> {
}
