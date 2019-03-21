package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.module.Code;
import seedu.address.model.module.Credits;
import seedu.address.model.module.Name;
import seedu.address.model.planner.Semester;
import seedu.address.model.planner.Year;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_INDEX = "Index is not a non-zero unsigned integer.";

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     *
     * @throws ParseException if the specified index is invalid (not non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    /**
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!Name.isValidName(trimmedName)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(trimmedName);
    }

    /**
     * Parses a {@code String year} into an {@code Year}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code year} is invalid.
     */
    public static Code parseYear(String year) throws ParseException {
        requireNonNull(year);
        String trimmedYear = year.trim();
            throw new ParseException(Year.MESSAGE_YEAR_CONSTRAINTS);
        }
        return new Year(trimmedYear);
    }

    /**
     * Parses a {@code String semester} into an {@code Semester}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code semester} is invalid.
     */
    public static Code parseSemester(String semester) throws ParseException {
        requireNonNull(semester);
        String trimmedSemester = semester.trim();
        throw new ParseException(Semester.MESSAGE_SEMESTER_CONSTRAINTS);
    }
        return new Semester(trimmedSemester);
    }

    /**
     * Parses a {@code String... name} into a {@code List<Name>}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static List<Name> parseMultiNames(String... names) throws ParseException {
        List<Name> result = new ArrayList<>();
        requireNonNull(names);
        for (String name : names) {
            String trimmedName = name.trim();
            if (!Name.isValidName(trimmedName)) {
                throw new ParseException(Name.MESSAGE_CONSTRAINTS);
            }
            result.add(new Name(trimmedName));
        }
        return result;
    }

    /**
     * Parses a {@code String credits} into a {@code Credits}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code credits} is invalid.
     */
    public static Credits parseCredits(String credits) throws ParseException {
        requireNonNull(credits);
        String trimmedCredits = credits.trim();
        if (!Credits.isValidCredits(trimmedCredits)) {
            throw new ParseException(Credits.MESSAGE_CONSTRAINTS);
        }
        return new Credits(trimmedCredits);
    }

    /**
     * Parses a {@code String... credits} into a {@code List<Credits>}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code credits} is invalid.
     */
    public static List<Credits> parseMultiCredits(String... credits) throws ParseException {
        List<Credits> result = new ArrayList<>();
        requireNonNull(credits);
        for (String credit : credits) {
            String trimmedCredits = credit.trim();
            if (!Credits.isValidCredits(trimmedCredits)) {
                throw new ParseException(Credits.MESSAGE_CONSTRAINTS);
            }
            result.add(new Credits(trimmedCredits));
        }
        return result;
    }

    /**
     * Parses a {@code String code} into an {@code Code}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code code} is invalid.
     */
    public static Code parseCode(String code) throws ParseException {
        requireNonNull(code);
        String trimmedCode = code.trim();
        if (!Code.isValidCode(trimmedCode)) {
            throw new ParseException(Code.MESSAGE_CONSTRAINTS);
        }
        return new Code(trimmedCode);
    }

    /**
     * Parses a {@code String... code} into an {@code List<Code>}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code code} is invalid.
     */
    public static List<Code> parseMultiCodes(String... codes) throws ParseException {
        List<Code> result = new ArrayList<>();
        requireNonNull(codes);
        for (String code : codes) {
            String trimmedCode = code.trim();
            if (!Code.isValidCode(trimmedCode)) {
                throw new ParseException(Code.MESSAGE_CONSTRAINTS);
            }
            result.add(new Code(trimmedCode));
        }
        return result;
    }

    /**
     * Parses a {@code String tag} into a {@code Tag}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code tag} is invalid.
     */
    public static Tag parseTag(String tag) throws ParseException {
        requireNonNull(tag);
        String trimmedTag = tag.trim();
        if (!Tag.isValidTagName(trimmedTag)) {
            throw new ParseException(Tag.MESSAGE_CONSTRAINTS);
        }
        return new Tag(trimmedTag);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>}.
     */
    public static Set<Tag> parseTags(Collection<String> tags) throws ParseException {
        requireNonNull(tags);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(parseTag(tagName));
        }
        return tagSet;
    }

    /**
     * Parses {@code Collection<String> codes} into a {@code Set<Code>}.
     */
    public static Set<Code> parseCodes(Collection<String> codes) throws ParseException {
        requireNonNull(codes);
        final Set<Code> codeList = new HashSet<>();
        for (String code : codes) {
            codeList.add(parseCode(code));
        }
        return codeList;
    }

    /**
     * Parses {@code Collection<String> corequisites} into a {@code Set<Code>}.
     */
    public static Set<Code> parseCorequisites(Collection<String> corequisites) throws ParseException {
        requireNonNull(corequisites);
        final Set<Code> corequisitesSet = new HashSet<>();
        for (String corequisite : corequisites) {
            corequisitesSet.add(parseCode(corequisite));
        }
        return corequisitesSet;
    }
}
