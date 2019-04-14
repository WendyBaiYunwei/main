package pwe.planner.model.module;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import pwe.planner.testutil.ModuleBuilder;

public class NameContainsKeywordsPredicateTest {

    @Test
    public void equals() {
        String firstPredicateKeyword = "first";
        String secondPredicateKeyword = "second";

        NameContainsKeywordsPredicate<Module> firstPredicate = new NameContainsKeywordsPredicate<>(
                firstPredicateKeyword);
        NameContainsKeywordsPredicate<Module> secondPredicate = new NameContainsKeywordsPredicate<>(
                secondPredicateKeyword);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        NameContainsKeywordsPredicate<Module> firstPredicateCopy = new NameContainsKeywordsPredicate<>(
                firstPredicateKeyword);
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
        NameContainsKeywordsPredicate<Module> predicate = new NameContainsKeywordsPredicate<>(
                "Alice");
        assertTrue(predicate.test(new ModuleBuilder().withName("Alice Bob").build()));

        // exact keyword match
        predicate = new NameContainsKeywordsPredicate<>("Alice Bob");
        assertTrue(predicate.test(new ModuleBuilder().withName("Alice Bob").build()));

        // exact mixed case keyword
        predicate = new NameContainsKeywordsPredicate<>("AlIcE bOb");
        assertTrue(predicate.test(new ModuleBuilder().withName("Alice Bob").build()));

        // Mixed-case keyword
        predicate = new NameContainsKeywordsPredicate<>("aLIce");
        assertTrue(predicate.test(new ModuleBuilder().withName("Alice Bob").build()));
    }

    @Test
    public void test_nameDoesNotContainKeywords_returnsFalse() {
        // Non-matching keyword
        NameContainsKeywordsPredicate<Module> predicate = new NameContainsKeywordsPredicate<>("Carol");
        assertFalse(predicate.test(new ModuleBuilder().withName("Alice Bob").build()));

        // Keywords match credits, but does not match name
        predicate = new NameContainsKeywordsPredicate<>("123");
        assertFalse(predicate.test(new ModuleBuilder().withName("Alice").withCredits("123")
                .withCode("CS1010").build()));
    }
}
