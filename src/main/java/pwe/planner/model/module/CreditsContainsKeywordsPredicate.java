package pwe.planner.model.module;

import static java.util.Objects.requireNonNull;
import static pwe.planner.logic.parser.ParserUtil.parseKeyword;

import java.util.List;

/**
 * Tests that a {@code Module}'s {@code Credit} matches any of the keywords given.
 */
public class CreditsContainsKeywordsPredicate<T> implements KeywordsPredicate<T> {
    private final List<String> keywords;

    public CreditsContainsKeywordsPredicate(List<String> keywords) {
        requireNonNull(keywords);

        this.keywords = keywords;
    }

    @Override
    public boolean test(T object) {
        requireNonNull(object);
        Module module = (Module) object;

        String moduleCredits = module.getCredits().toString();
        return keywords.stream().anyMatch(keyword -> parseKeyword(keyword, moduleCredits));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof CreditsContainsKeywordsPredicate // instanceof handles nulls
                && keywords.equals(((CreditsContainsKeywordsPredicate) other).keywords)); // state check
    }

}
