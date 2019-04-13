package pwe.planner.model.module;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import pwe.planner.testutil.ModuleBuilder;

public class CodeContainsKeywordsPredicateTest {
    @Test
    public void equals() {
        List<String> firstPredicateKeywordList = Collections.singletonList("first");
        List<String> secondPredicateKeywordList = Arrays.asList("first", "second");

        CodeContainsKeywordsPredicate<Module> firstPredicate =
                new CodeContainsKeywordsPredicate<>(firstPredicateKeywordList);
        CodeContainsKeywordsPredicate<Module> secondPredicate =
                new CodeContainsKeywordsPredicate<>(secondPredicateKeywordList);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        CodeContainsKeywordsPredicate<Module> firstPredicateCopy =
                new CodeContainsKeywordsPredicate<>(firstPredicateKeywordList);
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
                new CodeContainsKeywordsPredicate<>(Collections.singletonList("CS1010"));
        assertTrue(predicate.test(new ModuleBuilder().withCode("CS1010").build()));

        // Multiple keywords
        predicate = new CodeContainsKeywordsPredicate<>(Arrays.asList("CS1010", "CS1231"));
        assertTrue(predicate.test(new ModuleBuilder().withCode("CS1010").build()));
        assertTrue(predicate.test(new ModuleBuilder().withCode("CS1231").build()));

        // Mixed-case keywords
        predicate = new CodeContainsKeywordsPredicate<>(Arrays.asList("cS1010", "Cs1231"));
        assertTrue(predicate.test(new ModuleBuilder().withCode("CS1010").build()));
        assertTrue(predicate.test(new ModuleBuilder().withCode("CS1231").build()));
    }

    @Test
    public void test_nameDoesNotContainKeywords_returnsFalse() {
        // Zero keywords
        CodeContainsKeywordsPredicate<Module> predicate = new CodeContainsKeywordsPredicate<>(Collections.emptyList());
        assertFalse(predicate.test(new ModuleBuilder().withCode("CS1010").build()));

        // Non-matching keyword
        predicate = new CodeContainsKeywordsPredicate<>(Arrays.asList("CS1000"));
        assertFalse(predicate.test(new ModuleBuilder().withCode("CS1010").build()));

        // Keywords match credits and name, but does not match code
        predicate = new CodeContainsKeywordsPredicate<>(Arrays.asList("CS0000", "CS1111"));
        assertFalse(predicate.test(new ModuleBuilder().withName("Alice").withCredits("123")
                .withCode("CS1010").build()));
    }
}
