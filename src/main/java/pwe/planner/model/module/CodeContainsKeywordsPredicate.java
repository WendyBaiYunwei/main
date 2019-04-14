package pwe.planner.model.module;

import static java.util.Objects.requireNonNull;
import static pwe.planner.logic.parser.ParserUtil.parseKeyword;

/**
 * Tests that a {@code Module}'s {@code Code} matches the keyword given.
 */
public class CodeContainsKeywordsPredicate<T> implements KeywordsPredicate<T> {
    private final String keyword;

    public CodeContainsKeywordsPredicate(String keyword) {
        requireNonNull(keyword);

        this.keyword = keyword;
    }

    @Override
    public boolean test(T object) {
        requireNonNull(object);
        Module module = (Module) object;

        String moduleCode = module.getCode().toString();
        return parseKeyword(keyword, moduleCode);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof CodeContainsKeywordsPredicate // instanceof handles nulls
                && keyword.equals(((CodeContainsKeywordsPredicate) other).keyword)); // state check
    }

}
