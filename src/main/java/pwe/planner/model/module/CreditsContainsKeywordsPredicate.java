package pwe.planner.model.module;

import static java.util.Objects.requireNonNull;
import static pwe.planner.logic.parser.ParserUtil.parseKeyword;

/**
 * Tests that a {@code Module}'s {@code Credit} matches keyword given.
 */
public class CreditsContainsKeywordsPredicate<T> implements KeywordsPredicate<T> {
    private final String keyword;

    public CreditsContainsKeywordsPredicate(String keyword) {
        requireNonNull(keyword);

        this.keyword = keyword;
    }

    @Override
    public boolean test(T object) {
        requireNonNull(object);
        Module module = (Module) object;

        String moduleCredits = module.getCredits().toString();
        return parseKeyword(keyword, moduleCredits);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof CreditsContainsKeywordsPredicate // instanceof handles nulls
                && keyword.equals(((CreditsContainsKeywordsPredicate) other).keyword)); // state check
    }

}
