package seedu.address.model.module;

import java.util.List;

import seedu.address.logic.parser.ParserUtil;

/**
 * Tests that a {@code Module}'s {@code Code} matches any of the keywords given.
 */
public class CodeContainsKeywordsPredicate implements KeywordsPredicate {
    private final List<String> keywords;

    public CodeContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean test(Module module) {
        String moduleCode = module.getCode().toString();
        return keywords.stream()
                .anyMatch(keyword -> ParserUtil.parseKeyword(keyword, moduleCode));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof CodeContainsKeywordsPredicate // instanceof handles nulls
                && keywords.equals(((CodeContainsKeywordsPredicate) other).keywords)); // state check
    }

}
