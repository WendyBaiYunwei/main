package pwe.planner.model.module;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import pwe.planner.testutil.ModuleBuilder;

public class CreditsContainsKeywordsPredicateTest {
    //TODO: to update all test cases again after regex for `Credits` is updated
    @Test
    public void equals() {
        List<String> firstPredicateKeywordList = Collections.singletonList("1 2");
        List<String> secondPredicateKeywordList = Arrays.asList("1", "1");

        CreditsContainsKeywordsPredicate<Module> firstPredicate =
                new CreditsContainsKeywordsPredicate<>(firstPredicateKeywordList);
        CreditsContainsKeywordsPredicate<Module> secondPredicate =
                new CreditsContainsKeywordsPredicate<>(secondPredicateKeywordList);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        CreditsContainsKeywordsPredicate<Module> firstPredicateCopy =
                new CreditsContainsKeywordsPredicate<>(firstPredicateKeywordList);
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
                new CreditsContainsKeywordsPredicate<>(Collections.singletonList("444"));
        assertTrue(predicate.test(new ModuleBuilder().withCredits("444").build()));

        // Multiple keywords
        predicate = new CreditsContainsKeywordsPredicate<>(Arrays.asList("122", "444"));
        assertTrue(predicate.test(new ModuleBuilder().withCredits("122").build()));

        // Only one matching keyword
        predicate = new CreditsContainsKeywordsPredicate<>(Arrays.asList("999", "444"));
        assertTrue(predicate.test(new ModuleBuilder().withCredits("444").build()));
    }

    @Test
    public void test_nameDoesNotContainKeywords_returnsFalse() {
        // Zero keywords
        CreditsContainsKeywordsPredicate<Module> predicate = new CreditsContainsKeywordsPredicate<>(
                Collections.emptyList());
        assertFalse(predicate.test(new ModuleBuilder().withCredits("123").build()));

        // Non-matching keyword
        predicate = new CreditsContainsKeywordsPredicate<>(Arrays.asList("144"));
        assertFalse(predicate.test(new ModuleBuilder().withCredits("441").build()));
    }
}
