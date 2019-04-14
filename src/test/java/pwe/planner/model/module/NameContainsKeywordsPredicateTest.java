package pwe.planner.model.module;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import pwe.planner.testutil.ModuleBuilder;

public class NameContainsKeywordsPredicateTest {

    @Test
    public void equals() {
        List<String> firstPredicateKeywordList = Collections.singletonList("first");
        List<String> secondPredicateKeywordList = Arrays.asList("first", "second");

        NameContainsKeywordsPredicate<Module> firstPredicate = new NameContainsKeywordsPredicate<>(
                firstPredicateKeywordList);
        NameContainsKeywordsPredicate<Module> secondPredicate = new NameContainsKeywordsPredicate<>(
                secondPredicateKeywordList);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        NameContainsKeywordsPredicate<Module> firstPredicateCopy = new NameContainsKeywordsPredicate<>(
                firstPredicateKeywordList);
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
                Collections.singletonList("Alice"));
        assertTrue(predicate.test(new ModuleBuilder().withName("Alice Bob").build()));

        // Multiple keywords
        predicate = new NameContainsKeywordsPredicate<>(Arrays.asList("Alice", "Bob"));
        assertTrue(predicate.test(new ModuleBuilder().withName("Alice Bob").build()));

        // Only one matching keyword
        predicate = new NameContainsKeywordsPredicate<>(Arrays.asList("Bob", "Carol"));
        assertTrue(predicate.test(new ModuleBuilder().withName("Alice Carol").build()));

        // Mixed-case keywords
        predicate = new NameContainsKeywordsPredicate<>(Arrays.asList("aLIce", "bOB"));
        assertTrue(predicate.test(new ModuleBuilder().withName("Alice Bob").build()));
    }

    @Test
    public void test_nameDoesNotContainKeywords_returnsFalse() {
        // Zero keywords
        NameContainsKeywordsPredicate<Module> predicate = new NameContainsKeywordsPredicate<>(
                Collections.emptyList());
        assertFalse(predicate.test(new ModuleBuilder().withName("Alice").build()));

        // Non-matching keyword
        predicate = new NameContainsKeywordsPredicate<>(Arrays.asList("Carol"));
        assertFalse(predicate.test(new ModuleBuilder().withName("Alice Bob").build()));

        // Keywords match credits and code, but does not match name
        predicate = new NameContainsKeywordsPredicate<>(Arrays.asList("12345", "Main", "Street"));
        assertFalse(predicate.test(new ModuleBuilder().withName("Alice").withCredits("123")
                .withCode("CS1010").build()));
    }
}
