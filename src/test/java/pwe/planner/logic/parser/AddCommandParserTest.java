package pwe.planner.logic.parser;

import static pwe.planner.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static pwe.planner.logic.commands.CommandTestUtil.CODE_DESC_AMY;
import static pwe.planner.logic.commands.CommandTestUtil.CODE_DESC_BOB;
import static pwe.planner.logic.commands.CommandTestUtil.CREDITS_DESC_AMY;
import static pwe.planner.logic.commands.CommandTestUtil.CREDITS_DESC_BOB;
import static pwe.planner.logic.commands.CommandTestUtil.INVALID_CODE_DESC;
import static pwe.planner.logic.commands.CommandTestUtil.INVALID_CREDITS_DESC;
import static pwe.planner.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static pwe.planner.logic.commands.CommandTestUtil.INVALID_SEMESTER_DESC_ZERO;
import static pwe.planner.logic.commands.CommandTestUtil.INVALID_TAG_DESC;
import static pwe.planner.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static pwe.planner.logic.commands.CommandTestUtil.NAME_DESC_BOB;
import static pwe.planner.logic.commands.CommandTestUtil.PREAMBLE_NON_EMPTY;
import static pwe.planner.logic.commands.CommandTestUtil.PREAMBLE_WHITESPACE;
import static pwe.planner.logic.commands.CommandTestUtil.SEMESTERS_DESC_AMY;
import static pwe.planner.logic.commands.CommandTestUtil.SEMESTERS_DESC_BOB;
import static pwe.planner.logic.commands.CommandTestUtil.SEMESTER_DESC_BOB_FOUR;
import static pwe.planner.logic.commands.CommandTestUtil.SEMESTER_DESC_BOB_THREE;
import static pwe.planner.logic.commands.CommandTestUtil.TAG_DESC_FRIEND;
import static pwe.planner.logic.commands.CommandTestUtil.TAG_DESC_HUSBAND;
import static pwe.planner.logic.commands.CommandTestUtil.VALID_CODE_BOB;
import static pwe.planner.logic.commands.CommandTestUtil.VALID_CREDITS_BOB;
import static pwe.planner.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static pwe.planner.logic.commands.CommandTestUtil.VALID_SEMESTER_AMY_ONE;
import static pwe.planner.logic.commands.CommandTestUtil.VALID_SEMESTER_AMY_TWO;
import static pwe.planner.logic.commands.CommandTestUtil.VALID_SEMESTER_BOB_FOUR;
import static pwe.planner.logic.commands.CommandTestUtil.VALID_SEMESTER_BOB_THREE;
import static pwe.planner.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static pwe.planner.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static pwe.planner.logic.parser.CommandParserTestUtil.assertParseFailure;
import static pwe.planner.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static pwe.planner.testutil.TypicalModules.AMY;
import static pwe.planner.testutil.TypicalModules.BOB;

import org.junit.Test;

import pwe.planner.logic.commands.AddCommand;
import pwe.planner.model.module.Code;
import pwe.planner.model.module.Credits;
import pwe.planner.model.module.Module;
import pwe.planner.model.module.Name;
import pwe.planner.model.planner.Semester;
import pwe.planner.model.tag.Tag;
import pwe.planner.testutil.ModuleBuilder;

public class AddCommandParserTest {
    private AddCommandParser parser = new AddCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        Module expectedModule =
                new ModuleBuilder(BOB).withTags(VALID_TAG_FRIEND).build();

        String addCommand = PREAMBLE_WHITESPACE + CODE_DESC_BOB + NAME_DESC_BOB + CREDITS_DESC_BOB + SEMESTERS_DESC_BOB
                + TAG_DESC_FRIEND;
        // whitespace only preamble
        assertParseSuccess(parser, addCommand, new AddCommand(expectedModule));

        // multiple codes - last code accepted
        addCommand = CODE_DESC_AMY + CODE_DESC_BOB + NAME_DESC_BOB + CREDITS_DESC_BOB + SEMESTERS_DESC_BOB
                + TAG_DESC_FRIEND;
        assertParseSuccess(parser, addCommand, new AddCommand(expectedModule));

        // multiple names - last name accepted
        addCommand = CODE_DESC_BOB + NAME_DESC_AMY + NAME_DESC_BOB + CREDITS_DESC_BOB + SEMESTERS_DESC_BOB
                + TAG_DESC_FRIEND;
        assertParseSuccess(parser, addCommand, new AddCommand(expectedModule));

        // multiple credits - last credits accepted
        addCommand = CODE_DESC_BOB + NAME_DESC_BOB + CREDITS_DESC_AMY + CREDITS_DESC_BOB + SEMESTERS_DESC_BOB
                + TAG_DESC_FRIEND;
        assertParseSuccess(parser, addCommand, new AddCommand(expectedModule));

        // multiple semesters - all accepted
        addCommand = CODE_DESC_BOB + NAME_DESC_BOB + CREDITS_DESC_AMY + CREDITS_DESC_BOB + SEMESTERS_DESC_AMY
                + SEMESTERS_DESC_BOB;
        Module expectedModuleMultipleSemesters = new ModuleBuilder(BOB)
                .withSemesters(VALID_SEMESTER_AMY_ONE, VALID_SEMESTER_AMY_TWO, VALID_SEMESTER_BOB_THREE,
                        VALID_SEMESTER_BOB_FOUR)
                .withTags()
                .build();
        assertParseSuccess(parser, addCommand, new AddCommand(expectedModuleMultipleSemesters));

        // multiple tags - all accepted
        addCommand = CODE_DESC_BOB + NAME_DESC_BOB + CREDITS_DESC_AMY + CREDITS_DESC_BOB + SEMESTERS_DESC_BOB
                + TAG_DESC_FRIEND + TAG_DESC_HUSBAND;
        Module expectedModuleMultipleTags = new ModuleBuilder(BOB)
                .withTags(VALID_TAG_FRIEND, VALID_TAG_HUSBAND).build();
        assertParseSuccess(parser, addCommand, new AddCommand(expectedModuleMultipleTags));
    }

    @Test
    public void parse_optionalFieldsMissing_success() {
        // zero tags
        Module expectedModule = new ModuleBuilder(AMY).withTags().build();
        String addCommand = CODE_DESC_AMY + NAME_DESC_AMY + CREDITS_DESC_AMY + SEMESTERS_DESC_AMY;
        assertParseSuccess(parser, addCommand, new AddCommand(expectedModule));
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);

        // missing code prefix
        String addCommand = VALID_CODE_BOB + NAME_DESC_BOB + CREDITS_DESC_BOB + SEMESTERS_DESC_BOB;
        assertParseFailure(parser, addCommand, expectedMessage);

        // missing name prefix
        addCommand = VALID_NAME_BOB + CODE_DESC_BOB + CREDITS_DESC_BOB + SEMESTERS_DESC_BOB;
        assertParseFailure(parser, addCommand, expectedMessage);

        // missing credits prefix
        addCommand = VALID_CREDITS_BOB + CODE_DESC_BOB + NAME_DESC_BOB + SEMESTERS_DESC_BOB;
        assertParseFailure(parser, addCommand, expectedMessage);

        // missing semester prefix for first of the two semesters
        addCommand = VALID_SEMESTER_BOB_THREE + CODE_DESC_BOB + NAME_DESC_BOB + CREDITS_DESC_BOB
                + SEMESTER_DESC_BOB_FOUR;
        assertParseFailure(parser, addCommand, expectedMessage);

        // missing semester prefix for last of the two semesters
        addCommand = VALID_SEMESTER_BOB_FOUR + CODE_DESC_BOB + NAME_DESC_BOB + CREDITS_DESC_BOB
                + SEMESTER_DESC_BOB_THREE;
        assertParseFailure(parser, addCommand, expectedMessage);

        // all prefixes missing
        addCommand = VALID_CODE_BOB + VALID_NAME_BOB + VALID_CREDITS_BOB + VALID_SEMESTER_BOB_THREE
                + VALID_SEMESTER_BOB_FOUR;
        assertParseFailure(parser, addCommand, expectedMessage);
    }

    @Test
    public void parse_invalidValue_failure() {
        // invalid code
        String addCommand = INVALID_CODE_DESC + NAME_DESC_BOB + CREDITS_DESC_BOB + SEMESTERS_DESC_BOB
                + TAG_DESC_HUSBAND + TAG_DESC_FRIEND;
        assertParseFailure(parser, addCommand, Code.MESSAGE_CONSTRAINTS);

        // invalid name
        addCommand = CODE_DESC_BOB + INVALID_NAME_DESC + CREDITS_DESC_BOB + SEMESTERS_DESC_BOB
                + TAG_DESC_HUSBAND + TAG_DESC_FRIEND;
        assertParseFailure(parser, addCommand, Name.MESSAGE_CONSTRAINTS);

        // invalid credits
        addCommand = CODE_DESC_BOB + NAME_DESC_BOB + INVALID_CREDITS_DESC + SEMESTERS_DESC_BOB
                + TAG_DESC_HUSBAND + TAG_DESC_FRIEND;
        assertParseFailure(parser, addCommand, Credits.MESSAGE_CONSTRAINTS);

        // invalid semesters
        addCommand = CODE_DESC_BOB + NAME_DESC_BOB + CREDITS_DESC_BOB + INVALID_SEMESTER_DESC_ZERO
                + TAG_DESC_HUSBAND + TAG_DESC_FRIEND;
        assertParseFailure(parser, addCommand, Semester.MESSAGE_SEMESTER_CONSTRAINTS);

        // invalid tag
        addCommand = CODE_DESC_BOB + NAME_DESC_BOB + CREDITS_DESC_BOB + SEMESTERS_DESC_BOB
                + INVALID_TAG_DESC + TAG_DESC_FRIEND;
        assertParseFailure(parser, addCommand, Tag.MESSAGE_CONSTRAINTS);

        // two invalid values, only first invalid value reported
        addCommand = INVALID_CODE_DESC + INVALID_NAME_DESC + INVALID_CREDITS_DESC + SEMESTERS_DESC_BOB
                + INVALID_TAG_DESC + TAG_DESC_FRIEND;
        assertParseFailure(parser, addCommand, Code.MESSAGE_CONSTRAINTS);

        // non-empty preamble
        addCommand = PREAMBLE_NON_EMPTY + CODE_DESC_BOB + NAME_DESC_BOB + CREDITS_DESC_BOB + SEMESTERS_DESC_BOB
                + TAG_DESC_HUSBAND + TAG_DESC_FRIEND;
        assertParseFailure(parser, addCommand, String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
    }
}
