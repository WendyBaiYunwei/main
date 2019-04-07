package pwe.planner.logic.commands;

import static pwe.planner.logic.commands.CommandTestUtil.assertCommandSuccess;
import static pwe.planner.testutil.TypicalDegreePlanners.getTypicalDegreePlannerList;
import static pwe.planner.testutil.TypicalModules.getTypicalModuleList;
import static pwe.planner.testutil.TypicalRequirementCategories.getTypicalRequirementCategoriesList;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import pwe.planner.commons.exceptions.IllegalValueException;
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
        model = new ModelManager(
                new JsonSerializableApplication(getTypicalModuleList(), getTypicalDegreePlannerList(),
                        getTypicalRequirementCategoriesList()).toModelType(), new UserPrefs());
    }

    @Test
    public void execute_existentModulesWithMatchingTags_recommendedModulesFound() {
        Credits bestCredits = new Credits("3");
        Set<Tag> tagsToFind = new HashSet<>();
        Tag validTag = new Tag("owesMoney");
        tagsToFind.add(validTag);
        Set<Code> codesToSuggest = new HashSet<>();
        Code validCode = new Code("CS2101");
        codesToSuggest.add(validCode);
        String expectedMessage = String.format(PlannerSuggestCommand.MESSAGE_SUCCESS, "None ", codesToSuggest);
        assertCommandSuccess(new PlannerSuggestCommand(bestCredits, tagsToFind), model, commandHistory, expectedMessage, model);
    }

    @Test
    public void execute_nonexistentModulesWithMatchingTags_recommendedModulesNotFound() {
        Credits bestCredits = new Credits("3");
        Set<Tag> tagsToFind = new HashSet<>();
        Tag validTag = new Tag("nonexistent");
        tagsToFind.add(validTag);
        String expectedMessage = String.format(PlannerSuggestCommand.MESSAGE_SUCCESS, "None ", "None");
        assertCommandSuccess(new PlannerSuggestCommand(bestCredits, tagsToFind), model, commandHistory, expectedMessage, model);
    }
}
