package pwe.planner.model.planner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import pwe.planner.testutil.DegreePlannerBuilder;

public class SemesterContainsKeywordPredicateTest {
    @Test
    public void equals() {
        String firstPredicateKeyword = "1";
        String secondPredicateKeyword = "2";

        SemesterContainsKeywordPredicate<DegreePlanner> firstPredicate =
                new SemesterContainsKeywordPredicate<>(firstPredicateKeyword);
        SemesterContainsKeywordPredicate<DegreePlanner> secondPredicate =
                new SemesterContainsKeywordPredicate<>(secondPredicateKeyword);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        SemesterContainsKeywordPredicate<DegreePlanner> firstPredicateCopy =
                new SemesterContainsKeywordPredicate<>(firstPredicateKeyword);
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // different module -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void test_semesterContainsKeywords_returnsTrue() {
        // One keyword
        SemesterContainsKeywordPredicate<DegreePlanner> predicate =
                new SemesterContainsKeywordPredicate<>("1");
        assertTrue(predicate.test(new DegreePlannerBuilder().withSemester("1").build()));

    }

    @Test
    public void test_semesterDoesNotContainKeywords_returnsFalse() {
        // Non-matching keyword
        SemesterContainsKeywordPredicate<DegreePlanner> predicate = new SemesterContainsKeywordPredicate<>("4");
        assertFalse(predicate.test(new DegreePlannerBuilder().withSemester("1").build()));
    }
}
