package pwe.planner.model.module;

import static java.util.Objects.requireNonNull;
import static pwe.planner.logic.parser.ParserUtil.parseKeyword;

import java.util.Set;

import pwe.planner.model.tag.Tag;

/**
 * Tests that a {@code Module}'s {@code Tag} matches keyword given.
 */
public class TagContainsKeywordsPredicate<T> implements KeywordsPredicate<T> {
    private final String keyword;

    public TagContainsKeywordsPredicate(String keyword) {
        requireNonNull(keyword);

        this.keyword = keyword;
    }

    @Override
    public boolean test(T object) {
        requireNonNull(object);
        Module module = (Module) object;

        Set<Tag> tags = module.getTags();
        return tags.stream().anyMatch(tag -> parseKeyword(keyword, tag.tagName));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof TagContainsKeywordsPredicate // instanceof handles nulls
                && keyword.equals(((TagContainsKeywordsPredicate) other).keyword)); // state check
    }

}
