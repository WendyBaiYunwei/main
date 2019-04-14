package pwe.planner.model.module;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import pwe.planner.testutil.ModuleBuilder;

public class CodeContainsKeywordsPredicateTest {
    @Test
    public void equals() {
        String firstPredicateKeyword = "first";
        String secondPredicateKeyword = "second";

        CodeContainsKeywordsPredicate<Module> firstPredicate =
                new CodeContainsKeywordsPredicate<>(firstPredicateKeyword);
        CodeContainsKeywordsPredicate<Module> secondPredicate =
                new CodeContainsKeywordsPredicate<>(secondPredicateKeyword);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        CodeContainsKeywordsPredicate<Module> firstPredicateCopy =
                new CodeContainsKeywordsPredicate<>(firstPredicateKeyword);
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different module -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void test_nameContainsKeywords_returnsTrue() {
        // One keyword
        CodeContainsKeywordsPredicate<Module> predicate =
                new CodeContainsKeywordsPredicate<>("CS1010");
        assertTrue(predicate.test(new ModuleBuilder().withCode("CS1010").build()));

        // Mixed-case keywords
        predicate = new CodeContainsKeywordsPredicate<>("cS1010");
        assertTrue(predicate.test(new ModuleBuilder().withCode("CS1010").build()));
    }

    @Test
    public void test_nameDoesNotContainKeywords_returnsFalse() {
        // Non-matching keyword
        CodeContainsKeywordsPredicate<Module> predicate = new CodeContainsKeywordsPredicate<>("CS1000");
        assertFalse(predicate.test(new ModuleBuilder().withCode("CS1010").build()));

        // Keywords match credits and name, but does not match code
        predicate = new CodeContainsKeywordsPredicate<>("CS0000");
        assertFalse(predicate.test(new ModuleBuilder().withName("Alice").withCredits("123")
                .withCode("CS1010").build()));
    }
}
