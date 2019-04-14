package pwe.planner.logic.commands;

import static pwe.planner.logic.commands.CommandTestUtil.assertCommandSuccess;
import static pwe.planner.testutil.TypicalDegreePlanners.getTypicalDegreePlannerList;
import static pwe.planner.testutil.TypicalModules.getTypicalModuleList;
import static pwe.planner.testutil.TypicalRequirementCategories.getTypicalRequirementCategoriesList;

import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import pwe.planner.commons.exceptions.IllegalValueException;
import pwe.planner.logic.CommandHistory;
import pwe.planner.model.Model;
import pwe.planner.model.ModelManager;
import pwe.planner.model.UserPrefs;
import pwe.planner.model.planner.DegreePlanner;
import pwe.planner.storage.JsonSerializableApplication;

/**
 * Contains integration tests (interaction with the Model) and unit tests for PlannerListCommand.
 */
public class PlannerListCommandTest {
    private Model model;
    private Model expectedModel;
    private CommandHistory commandHistory = new CommandHistory();

    @Before
    public void setUp() throws IllegalValueException {
        model = new ModelManager(new JsonSerializableApplication(getTypicalModuleList(), getTypicalDegreePlannerList(),
                getTypicalRequirementCategoriesList()).toModelType(), new UserPrefs());
        expectedModel = new ModelManager(model.getApplication(), new UserPrefs());
    }

    @Test
    public void execute_plannerListIsNotFiltered_showsSameList() {
        String degreePlannerListContent = model.getApplication().getDegreePlannerList().stream()
                .map(DegreePlanner::toString)
                .collect(Collectors.joining("\n"));
        String expectedMessage =
                String.format(PlannerListCommand.MESSAGE_SUCCESS, degreePlannerListContent);
        assertCommandSuccess(new PlannerListCommand(), model, commandHistory, expectedMessage, expectedModel);
    }
}
