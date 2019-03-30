package seedu.address.model.module;

import java.util.List;

import seedu.address.logic.parser.ParserUtil;

/**
 * Tests that a {@code Module}'s {@code Credit} matches any of the keywords given.
 */
public class CreditsContainsKeywordsPredicate implements KeywordsPredicate {
    private final List<String> keywords;

    public CreditsContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean test(Module module) {
        String moduleCredits = module.getCredits().toString();
        return keywords.stream()
                .anyMatch(keyword -> ParserUtil.parseKeyword(keyword, moduleCredits));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof CreditsContainsKeywordsPredicate // instanceof handles nulls
                && keywords.equals(((CreditsContainsKeywordsPredicate) other).keywords)); // state check
    }

}
