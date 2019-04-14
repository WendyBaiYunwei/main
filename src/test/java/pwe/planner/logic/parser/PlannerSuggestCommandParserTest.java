package pwe.planner.logic.parser;

import static pwe.planner.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static pwe.planner.logic.commands.CommandTestUtil.PREAMBLE_NON_EMPTY;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_CREDITS;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_TAG;
import static pwe.planner.logic.parser.CommandParserTestUtil.assertParseFailure;
import static pwe.planner.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Set;

import org.junit.Test;

import pwe.planner.logic.commands.PlannerSuggestCommand;
import pwe.planner.model.module.Credits;
import pwe.planner.model.tag.Tag;

public class PlannerSuggestCommandParserTest {
    private PlannerSuggestCommandParser parser = new PlannerSuggestCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        Credits bestCredits = new Credits("2");
        Set<Tag> tagsToFind = Set.of(new Tag("validName"), new Tag("anotherValidName"));

        // multiple tags - all tags accepted
        assertParseSuccess(parser, " " + PREFIX_CREDITS + "2 "
                + PREFIX_TAG + "validName " + PREFIX_TAG
                + "anotherValidName", new PlannerSuggestCommand(bestCredits, tagsToFind));
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, PlannerSuggestCommand.MESSAGE_USAGE);

        // missing credits prefix
        assertParseFailure(parser, " " + "3 " + PREFIX_TAG + "validTag", expectedMessage);
    }

    @Test
    public void parse_invalidValue_failure() {
        // invalid credits
        assertParseFailure(parser, " " + PREFIX_CREDITS + "-1 " + PREFIX_TAG + "validTag", Credits.MESSAGE_CONSTRAINTS);

        // non-empty preamble
        assertParseFailure(parser, PREAMBLE_NON_EMPTY + " " + PREFIX_CREDITS + "1 " + PREFIX_TAG + "validTag",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, PlannerSuggestCommand.MESSAGE_USAGE));
    }
}
