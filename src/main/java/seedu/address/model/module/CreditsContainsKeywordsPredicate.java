package seedu.address.model.module;

import java.util.List;

import seedu.address.commons.util.StringUtil;

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
        return keywords.stream()
                .anyMatch(keyword -> StringUtil.containsWordIgnoreCase(module.getCredits().toString(), keyword));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof CreditsContainsKeywordsPredicate // instanceof handles nulls
                && keywords.equals(((CreditsContainsKeywordsPredicate) other).keywords)); // state check
    }

}
