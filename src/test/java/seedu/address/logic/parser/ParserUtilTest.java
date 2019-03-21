package seedu.address.logic.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_INDEX;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_MODULE;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.module.Code;
import seedu.address.model.module.Credits;
import seedu.address.model.module.Name;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.Assert;

public class ParserUtilTest {
    private static final String INVALID_NAME = "Rächel";
    private static final String INVALID_CREDITS = "+651234";
    private static final String INVALID_CODE = " ";
    private static final String INVALID_TAG = "#friend";

    private static final String VALID_NAME = "Rachel Walker";
    private static final String VALID_CREDITS = "246";
    private static final String VALID_CODE = "MA1301";
    private static final String VALID_TAG_1 = "friend";
    private static final String VALID_TAG_2 = "neighbour";

    private static final String WHITESPACE = " \t\r\n";

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void parseIndex_invalidInput_throwsParseException() throws Exception {
        thrown.expect(ParseException.class);
        ParserUtil.parseIndex("10 a");
    }

    @Test
    public void parseIndex_outOfRangeInput_throwsParseException() throws Exception {
        thrown.expect(ParseException.class);
        thrown.expectMessage(MESSAGE_INVALID_INDEX);
        ParserUtil.parseIndex(Long.toString(Integer.MAX_VALUE + 1));
    }

    @Test
    public void parseIndex_validInput_success() throws Exception {
        // No whitespaces
        assertEquals(INDEX_FIRST_MODULE, ParserUtil.parseIndex("1"));

        // Leading and trailing whitespaces
        assertEquals(INDEX_FIRST_MODULE, ParserUtil.parseIndex("  1  "));
    }

    @Test
    public void parseName_null_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> ParserUtil.parseName((String) null));
    }

    @Test
    public void parseName_invalidValue_throwsParseException() {
        Assert.assertThrows(ParseException.class, () -> ParserUtil.parseName(INVALID_NAME));
    }

    @Test
    public void parseName_validValueWithoutWhitespace_returnsName() throws Exception {
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(VALID_NAME));
    }

    @Test
    public void parseName_validValueWithWhitespace_returnsTrimmedName() throws Exception {
        String nameWithWhitespace = WHITESPACE + VALID_NAME + WHITESPACE;
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(nameWithWhitespace));
    }

    @Test
    public void parseCredits_null_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> ParserUtil.parseCredits((String) null));
    }

    @Test
    public void parseCredits_invalidValue_throwsParseException() {
        Assert.assertThrows(ParseException.class, () -> ParserUtil.parseCredits(INVALID_CREDITS));
    }

    @Test
    public void parseCredits_validValueWithoutWhitespace_returnsCredits() throws Exception {
        Credits expectedCredits = new Credits(VALID_CREDITS);
        assertEquals(expectedCredits, ParserUtil.parseCredits(VALID_CREDITS));
    }

    @Test
    public void parseCredits_validValueWithWhitespace_returnsTrimmedCredits() throws Exception {
        String creditsWithWhitespace = WHITESPACE + VALID_CREDITS + WHITESPACE;
        Credits expectedCredits = new Credits(VALID_CREDITS);
        assertEquals(expectedCredits, ParserUtil.parseCredits(creditsWithWhitespace));
    }

    @Test
    public void parseCode_null_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> ParserUtil.parseCode((String) null));
    }

    @Test
    public void parseCode_invalidValue_throwsParseException() {
        Assert.assertThrows(ParseException.class, () -> ParserUtil.parseCode(INVALID_CODE));
    }

    @Test
    public void parseCode_validValueWithoutWhitespace_returnsCode() throws Exception {
        Code expectedCode = new Code(VALID_CODE);
        assertEquals(expectedCode, ParserUtil.parseCode(VALID_CODE));
    }

    @Test
    public void parseCode_validValueWithWhitespace_returnsTrimmedCode() throws Exception {
        String codeWithWhitespace = WHITESPACE + VALID_CODE + WHITESPACE;
        Code expectedCode = new Code(VALID_CODE);
        assertEquals(expectedCode, ParserUtil.parseCode(codeWithWhitespace));
    }

    @Test
    public void parseTag_null_throwsNullPointerException() throws Exception {
        thrown.expect(NullPointerException.class);
        ParserUtil.parseTag(null);
    }

    @Test
    public void parseTag_invalidValue_throwsParseException() throws Exception {
        thrown.expect(ParseException.class);
        ParserUtil.parseTag(INVALID_TAG);
    }

    @Test
    public void parseTag_validValueWithoutWhitespace_returnsTag() throws Exception {
        Tag expectedTag = new Tag(VALID_TAG_1);
        assertEquals(expectedTag, ParserUtil.parseTag(VALID_TAG_1));
    }

    @Test
    public void parseTag_validValueWithWhitespace_returnsTrimmedTag() throws Exception {
        String tagWithWhitespace = WHITESPACE + VALID_TAG_1 + WHITESPACE;
        Tag expectedTag = new Tag(VALID_TAG_1);
        assertEquals(expectedTag, ParserUtil.parseTag(tagWithWhitespace));
    }

    @Test
    public void parseTags_null_throwsNullPointerException() throws Exception {
        thrown.expect(NullPointerException.class);
        ParserUtil.parseTags(null);
    }

    @Test
    public void parseTags_collectionWithInvalidTags_throwsParseException() throws Exception {
        thrown.expect(ParseException.class);
        ParserUtil.parseTags(Arrays.asList(VALID_TAG_1, INVALID_TAG));
    }

    @Test
    public void parseTags_emptyCollection_returnsEmptySet() throws Exception {
        assertTrue(ParserUtil.parseTags(Collections.emptyList()).isEmpty());
    }

    @Test
    public void parseTags_collectionWithValidTags_returnsTagSet() throws Exception {
        Set<Tag> actualTagSet = ParserUtil.parseTags(Arrays.asList(VALID_TAG_1, VALID_TAG_2));
        Set<Tag> expectedTagSet = new HashSet<Tag>(Arrays.asList(new Tag(VALID_TAG_1), new Tag(VALID_TAG_2)));

        assertEquals(expectedTagSet, actualTagSet);
    }
}
