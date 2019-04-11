package pwe.planner.logic.commands;

import static pwe.planner.logic.commands.CommandTestUtil.assertCommandSuccess;
import static pwe.planner.testutil.TypicalDegreePlanners.getTypicalDegreePlannerList;
import static pwe.planner.testutil.TypicalModules.getTypicalModuleList;
import static pwe.planner.testutil.TypicalRequirementCategories.getTypicalRequirementCategoriesList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.platform.commons.util.StringUtils;

import pwe.planner.commons.exceptions.IllegalValueException;
import pwe.planner.logic.CommandHistory;
import pwe.planner.logic.commands.exceptions.CommandException;
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
        model = new ModelManager(
                new JsonSerializableApplication(getTypicalModuleList(), getTypicalDegreePlannerList(),
                        getTypicalRequirementCategoriesList()).toModelType(), new UserPrefs());
    }

    @Test
    public void execute_modulesWithoutMatchingTagsAndCredits_recommendedModulesFound() {
        Credits bestCredits = new Credits("3");
        Set<Tag> tagsToFind = new HashSet<>();
        Tag validTag = new Tag("nonexistent");
        tagsToFind.add(validTag);
        Code code = new Code("CS2101");
        Code code2 = new Code("CS2105");
        List<Code> recommendedCodes = new ArrayList<>();
        recommendedCodes.add(code);
        recommendedCodes.add(code2);
        String expectedMessage = String.format(PlannerSuggestCommand.MESSAGE_SUCCESS, "None", recommendedCodes, "None",
                "None");

        assertCommandSuccess(new PlannerSuggestCommand(bestCredits, tagsToFind), model, commandHistory,
                expectedMessage, model);
    }

    @Test
    public void execute_modulesWithMatchingCredits_recommendedModulesFound() {
        Credits bestCredits = new Credits("4");
        Set<Tag> tagsToFind = new HashSet<>();
        Tag validTag = new Tag("nonexistent");
        tagsToFind.add(validTag);
        Code code = new Code("CS2101");
        Code code2 = new Code("CS2105");
        List<Code> recommendedCodes = new ArrayList<>();
        recommendedCodes.add(code);
        recommendedCodes.add(code2);
        List<Code> codesWithMatchingCredits = new ArrayList<>();
        codesWithMatchingCredits.add(code);

        String expectedMessage = String.format(PlannerSuggestCommand.MESSAGE_SUCCESS, "None", recommendedCodes, "None",
                codesWithMatchingCredits);

        assertCommandSuccess(new PlannerSuggestCommand(bestCredits, tagsToFind), model, commandHistory,
                expectedMessage, model);
    }
}
