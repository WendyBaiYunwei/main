package pwe.planner.model.module;

import java.util.List;

import pwe.planner.logic.parser.ParserUtil;

/**
 * Tests that a {@code Module}'s {@code Name} matches any of the keywords given.
 */
public class NameContainsKeywordsPredicate implements KeywordsPredicate {
    private final List<String> keywords;

    public NameContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean test(Module module) {
        String moduleName = module.getName().toString();
        return keywords.stream()
                .anyMatch(keyword -> ParserUtil.parseKeyword(keyword, moduleName));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof NameContainsKeywordsPredicate // instanceof handles nulls
                && keywords.equals(((NameContainsKeywordsPredicate) other).keywords)); // state check
    }

}
