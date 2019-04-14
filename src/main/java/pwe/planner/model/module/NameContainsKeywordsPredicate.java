package pwe.planner.model.module;

import static java.util.Objects.requireNonNull;
import static pwe.planner.logic.parser.ParserUtil.parseKeyword;

/**
 * Tests that a {@code Module}'s {@code Name} matches the keyword given.
 */
public class NameContainsKeywordsPredicate<T> implements KeywordsPredicate<T> {
    private final String keyword;

    public NameContainsKeywordsPredicate(String keyword) {
        requireNonNull(keyword);

        this.keyword = keyword;
    }

    @Override
    public boolean test(T object) {
        requireNonNull(object);
        Module module = (Module) object;

        String moduleName = module.getName().toString();
        return parseKeyword(keyword, moduleName);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof NameContainsKeywordsPredicate // instanceof handles nulls
                && keyword.equals(((NameContainsKeywordsPredicate) other).keyword)); // state check
    }

}
