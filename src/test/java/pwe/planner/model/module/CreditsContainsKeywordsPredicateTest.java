package pwe.planner.model.module;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import pwe.planner.testutil.ModuleBuilder;

public class CreditsContainsKeywordsPredicateTest {
    //TODO: to update all test cases again after regex for `Credits` is updated
    @Test
    public void equals() {
        String firstPredicateKeyword = "1";
        String secondPredicateKeyword = "2";

        CreditsContainsKeywordsPredicate<Module> firstPredicate =
                new CreditsContainsKeywordsPredicate<>(firstPredicateKeyword);
        CreditsContainsKeywordsPredicate<Module> secondPredicate =
                new CreditsContainsKeywordsPredicate<>(secondPredicateKeyword);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        CreditsContainsKeywordsPredicate<Module> firstPredicateCopy =
                new CreditsContainsKeywordsPredicate<>(firstPredicateKeyword);
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
        CreditsContainsKeywordsPredicate<Module> predicate =
                new CreditsContainsKeywordsPredicate<>("444");
        assertTrue(predicate.test(new ModuleBuilder().withCredits("444").build()));
    }

    @Test
    public void test_nameDoesNotContainKeywords_returnsFalse() {
        // Non-matching keyword
        CreditsContainsKeywordsPredicate<Module> predicate = new CreditsContainsKeywordsPredicate<>("144");
        assertFalse(predicate.test(new ModuleBuilder().withCredits("441").build()));
    }
}
