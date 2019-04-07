package pwe.planner.logic.parser;

import static pwe.planner.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static pwe.planner.logic.commands.CommandTestUtil.PREAMBLE_NON_EMPTY;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_CREDITS;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_TAG;
import static pwe.planner.logic.parser.CommandParserTestUtil.assertParseFailure;
import static pwe.planner.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static pwe.planner.testutil.TypicalDegreePlanners.getTypicalDegreePlannerList;
import static pwe.planner.testutil.TypicalModules.getTypicalModuleList;
import static pwe.planner.testutil.TypicalRequirementCategories.getTypicalRequirementCategoriesList;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import pwe.planner.commons.exceptions.IllegalValueException;
import pwe.planner.logic.commands.PlannerSuggestCommand;
import pwe.planner.model.Model;
import pwe.planner.model.ModelManager;
import pwe.planner.model.UserPrefs;
import pwe.planner.model.module.Credits;
import pwe.planner.model.tag.Tag;
import pwe.planner.storage.JsonSerializableApplication;

public class PlannerSuggestCommandParserTest {
    private PlannerSuggestCommandParser parser = new PlannerSuggestCommandParser();

    private Model model;

    @Before
    public void setUp() throws IllegalValueException {
        model = new ModelManager(
                new JsonSerializableApplication(getTypicalModuleList(), getTypicalDegreePlannerList(),
                        getTypicalRequirementCategoriesList()).toModelType(), new UserPrefs());
    }

    @Test
    public void parse_allFieldsPresent_success() {
        Credits bestCredits = new Credits("2");
        Tag tagToFind = new Tag("validName");
        Tag anotherTagToFind = new Tag("anotherValidName");
        Set<Tag> tagsToFind = new HashSet<>();
        tagsToFind.add(tagToFind);
        tagsToFind.add(anotherTagToFind);

        // multiple tags - all tags accepted
        assertParseSuccess(parser, " " + PREFIX_CREDITS + "2 "
                + PREFIX_TAG + "validName " + PREFIX_TAG
                + "anotherValidName", new PlannerSuggestCommand(bestCredits, tagsToFind));
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, PlannerSuggestCommand.MESSAGE_USAGE);

        // missing credits prefix
        assertParseFailure(parser, "3 " + PREFIX_TAG + "validTag", expectedMessage);

        // missing tag prefix
        assertParseFailure(parser, PREFIX_CREDITS + "3 " + "tag", expectedMessage);
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
