package pwe.planner.logic.commands;

import static pwe.planner.logic.commands.CommandTestUtil.assertCommandSuccess;
import static pwe.planner.testutil.TypicalDegreePlanners.getTypicalDegreePlannerList;
import static pwe.planner.testutil.TypicalModules.getTypicalModuleList;
import static pwe.planner.testutil.TypicalRequirementCategories.getTypicalRequirementCategoriesList;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import pwe.planner.commons.exceptions.IllegalValueException;
import pwe.planner.commons.util.StringUtil;
import pwe.planner.logic.CommandHistory;
import pwe.planner.model.Model;
import pwe.planner.model.ModelManager;
import pwe.planner.model.UserPrefs;
import pwe.planner.model.module.Code;
import pwe.planner.model.module.Credits;
import pwe.planner.model.tag.Tag;
import pwe.planner.storage.JsonSerializableApplication;

/**
 * Contains unit tests for PlannerSuggestCommand.
 */
public class PlannerSuggestCommandTest {

    private Model model;
    private CommandHistory commandHistory = new CommandHistory();

    @Before
    public void setUp() throws IllegalValueException {
        model = new ModelManager(new JsonSerializableApplication(getTypicalModuleList(), getTypicalDegreePlannerList(),
                        getTypicalRequirementCategoriesList()).toModelType(), new UserPrefs());
    }

    @Test
    public void execute_modulesWithoutMatchingTagsAndCredits_recommendedModulesFound() {
        Credits bestCredits = new Credits("3");
        Set<Tag> tagsToFind = Set.of(new Tag("nonexistent"));
        List<Code> recommendedCodes = List.of(new Code("CS2101"), new Code("CS2105"));

        String recommendedCodesString = StringUtil.joinStreamAsString(recommendedCodes.stream().sorted());

        String expectedMessage = String.format(PlannerSuggestCommand.MESSAGE_SUCCESS, recommendedCodesString, "None",
                "None");

        assertCommandSuccess(new PlannerSuggestCommand(bestCredits, tagsToFind), model, commandHistory,
                expectedMessage, model);
    }

    @Test
    public void execute_modulesWithMatchingCredits_recommendedModulesFound() {
        Credits bestCredits = new Credits("4");
        Set<Tag> tagsToFind = Set.of(new Tag("nonexistent"));
        List<Code> recommendedCodes = List.of(new Code("CS2101"), new Code("CS2105"));
        List<Code> codesWithMatchingCredits = List.of(new Code("CS2101"));

        String matchingCreditCodesString = StringUtil.joinStreamAsString(codesWithMatchingCredits.stream().sorted());

        String recommendedCodesString = StringUtil.joinStreamAsString(recommendedCodes.stream().sorted());

        String expectedMessage = String.format(PlannerSuggestCommand.MESSAGE_SUCCESS, recommendedCodesString, "None",
                matchingCreditCodesString);

        assertCommandSuccess(new PlannerSuggestCommand(bestCredits, tagsToFind), model, commandHistory,
                expectedMessage, model);
    }
}
