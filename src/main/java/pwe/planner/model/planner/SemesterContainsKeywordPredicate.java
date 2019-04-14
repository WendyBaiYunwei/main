package pwe.planner.model.planner;

import static java.util.Objects.requireNonNull;
import static pwe.planner.logic.parser.ParserUtil.parseKeyword;

import pwe.planner.model.module.KeywordsPredicate;

/**
 * Tests that a {@code DegreePlanner}'s {@code Semester} matches any of the keyword given.
 */
public class SemesterContainsKeywordPredicate<T> implements KeywordsPredicate<T> {
    private final String keyword;

    public SemesterContainsKeywordPredicate(String keyword) {
        requireNonNull(keyword);

        this.keyword = keyword;
    }

    @Override
    public boolean test(T object) {
        requireNonNull(object);
        DegreePlanner degreePlanner = (DegreePlanner) object;

        String semester = degreePlanner.getSemester().toString();
        return parseKeyword(keyword, semester);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof SemesterContainsKeywordPredicate // instanceof handles nulls
                && keyword.equals(((SemesterContainsKeywordPredicate) other).keyword)); // state check
    }
}
