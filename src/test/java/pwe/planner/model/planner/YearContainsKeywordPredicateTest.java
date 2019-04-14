package pwe.planner.model.planner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import pwe.planner.testutil.DegreePlannerBuilder;

public class YearContainsKeywordPredicateTest {
    @Test
    public void equals() {
        String firstPredicateKeyword = "1";
        String secondPredicateKeyword = "2";

        YearContainsKeywordPredicate<DegreePlanner> firstPredicate =
                new YearContainsKeywordPredicate<>(firstPredicateKeyword);
        YearContainsKeywordPredicate<DegreePlanner> secondPredicate =
                new YearContainsKeywordPredicate<>(secondPredicateKeyword);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        YearContainsKeywordPredicate<DegreePlanner> firstPredicateCopy =
                new YearContainsKeywordPredicate<>(firstPredicateKeyword);
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // different module -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void test_yearContainsKeywords_returnsTrue() {
        // One keyword
        YearContainsKeywordPredicate<DegreePlanner> predicate =
                new YearContainsKeywordPredicate<>("1");
        assertTrue(predicate.test(new DegreePlannerBuilder().withYear("1").build()));
    }

    @Test
    public void test_yearDoesNotContainKeywords_returnsFalse() {
        // Non-matching keyword
        YearContainsKeywordPredicate<DegreePlanner> predicate = new YearContainsKeywordPredicate<>("4");
        assertFalse(predicate.test(new DegreePlannerBuilder().withYear("1").build()));
    }
}
