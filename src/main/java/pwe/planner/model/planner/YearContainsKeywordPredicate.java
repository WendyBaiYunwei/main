package pwe.planner.model.planner;

import static java.util.Objects.requireNonNull;
import static pwe.planner.logic.parser.ParserUtil.parseKeyword;

import pwe.planner.model.module.KeywordsPredicate;

/**
 * Tests that a {@code DegreePlanner}'s {@code Year} matches any of the keywords given.
 */
public class YearContainsKeywordPredicate<T> implements KeywordsPredicate<T> {
    private final String keyword;

    public YearContainsKeywordPredicate(String keyword) {
        requireNonNull(keyword);

        this.keyword = keyword;
    }

    @Override
    public boolean test(T object) {
        requireNonNull(object);
        DegreePlanner degreePlanner = (DegreePlanner) object;

        String year = degreePlanner.getYear().toString();
        return parseKeyword(keyword, year);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof YearContainsKeywordPredicate // instanceof handles nulls
                && keyword.equals(((YearContainsKeywordPredicate) other).keyword)); // state check
    }
}
