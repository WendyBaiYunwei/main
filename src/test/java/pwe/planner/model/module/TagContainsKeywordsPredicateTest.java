package pwe.planner.model.module;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import pwe.planner.testutil.ModuleBuilder;

public class TagContainsKeywordsPredicateTest {

    @Test
    public void equals() {
        String firstPredicateKeyword = "first";
        String secondPredicateKeyword = "second";

        TagContainsKeywordsPredicate<Module> firstPredicate = new TagContainsKeywordsPredicate<>(
                firstPredicateKeyword);
        TagContainsKeywordsPredicate<Module> secondPredicate = new TagContainsKeywordsPredicate<>(
                secondPredicateKeyword);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        TagContainsKeywordsPredicate<Module> firstPredicateCopy = new TagContainsKeywordsPredicate<>(
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
        TagContainsKeywordsPredicate<Module> predicate = new TagContainsKeywordsPredicate<>("tag");
        assertTrue(predicate.test(new ModuleBuilder().withTags("tag").build()));

        // Mixed-case keywords
        predicate = new TagContainsKeywordsPredicate<>("tAg");
        assertTrue(predicate.test(new ModuleBuilder().withTags("tag").build()));
    }

    @Test
    public void test_nameDoesNotContainKeywords_returnsFalse() {


        // Non-matching keyword
        TagContainsKeywordsPredicate<Module> predicate = new TagContainsKeywordsPredicate<>("notExisting");
        assertFalse(predicate.test(new ModuleBuilder().withTags("tags").build()));

        // Keywords match credits but does not match tag
        predicate = new TagContainsKeywordsPredicate<>("123");
        assertFalse(predicate.test(new ModuleBuilder().withTags("Alice").withCredits("123")
                .withCode("CS1010").withTags("TAG").build()));
    }
}
