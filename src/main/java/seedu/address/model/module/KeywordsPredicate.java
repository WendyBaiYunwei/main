package seedu.address.model.module;

import java.util.function.Predicate;

/**
 * The abstract class for find keyword predicate
 */
public abstract class KeywordsPredicate implements Predicate<Module> {
    public abstract boolean test(Module module);
}
