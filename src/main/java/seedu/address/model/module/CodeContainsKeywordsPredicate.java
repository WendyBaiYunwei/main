package seedu.address.model.module;

import java.util.List;

import seedu.address.commons.util.StringUtil;

/**
 * Tests that a {@code Module}'s {@code Code} matches any of the keywords given.
 */
public class CodeContainsKeywordsPredicate extends KeywordsPredicate {
    private final List<String> keywords;

    public CodeContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean test(Module module) {
        return keywords.stream()
                .anyMatch(keyword -> StringUtil.containsWordIgnoreCase(module.getCode().toString(), keyword));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof CodeContainsKeywordsPredicate // instanceof handles nulls
                && keywords.equals(((CodeContainsKeywordsPredicate) other).keywords)); // state check
    }

}
