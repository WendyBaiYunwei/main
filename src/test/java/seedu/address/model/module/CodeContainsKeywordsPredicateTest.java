package seedu.address.model.module;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import seedu.address.testutil.ModuleBuilder;

public class CodeContainsKeywordsPredicateTest {
    @Test
    public void equals() {
        List<String> firstPredicateKeywordList = Collections.singletonList("first");
        List<String> secondPredicateKeywordList = Arrays.asList("first", "second");

        CodeContainsKeywordsPredicate firstPredicate =
                new CodeContainsKeywordsPredicate(firstPredicateKeywordList);
        CodeContainsKeywordsPredicate secondPredicate =
                new CodeContainsKeywordsPredicate(secondPredicateKeywordList);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        CodeContainsKeywordsPredicate firstPredicateCopy =
                new CodeContainsKeywordsPredicate(firstPredicateKeywordList);
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
        CodeContainsKeywordsPredicate predicate =
                new CodeContainsKeywordsPredicate(Collections.singletonList("Clementi"));
        assertTrue(predicate.test(new ModuleBuilder().withCode("Clementi Road").build()));

        // Multiple keywords
        predicate = new CodeContainsKeywordsPredicate(Arrays.asList("Buona", "Vista"));
        assertTrue(predicate.test(new ModuleBuilder().withCode("buona vista").build()));

        // Only one matching keyword
        predicate = new CodeContainsKeywordsPredicate(Arrays.asList("Kent", "Ridge"));
        assertTrue(predicate.test(new ModuleBuilder().withCode("Alice Ridge").build()));

        // Mixed-case keywords
        predicate = new CodeContainsKeywordsPredicate(Arrays.asList("kEnT", "rIdGe"));
        assertTrue(predicate.test(new ModuleBuilder().withCode("kent ridge").build()));
    }

    @Test
    public void test_nameDoesNotContainKeywords_returnsFalse() {
        // Zero keywords
        CodeContainsKeywordsPredicate predicate = new CodeContainsKeywordsPredicate(Collections.emptyList());
        assertFalse(predicate.test(new ModuleBuilder().withCode("Wonderland").build()));

        // Non-matching keyword
        predicate = new CodeContainsKeywordsPredicate(Arrays.asList("Clementi"));
        assertFalse(predicate.test(new ModuleBuilder().withCode("Ang Mo Kio").build()));

        // Keywords match credits and name, but does not match code
        predicate = new CodeContainsKeywordsPredicate(Arrays.asList("Alice", "12345"));
        assertFalse(predicate.test(new ModuleBuilder().withName("Alice").withCredits("12345")
                .withCode("Main Street").build()));
    }
}
