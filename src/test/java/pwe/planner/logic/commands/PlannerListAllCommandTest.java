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
import pwe.planner.model.module.Code;
import pwe.planner.model.planner.DegreePlanner;
import pwe.planner.storage.JsonSerializableApplication;

/**
 * Contains integration tests (interaction with the Model) and unit tests for PlannerListAllCommand.
 */
public class PlannerListAllCommandTest {
    private Model model;
    private Model expectedModel;
    private CommandHistory commandHistory = new CommandHistory();

    @Before
    public void setUp() throws IllegalValueException {
        //ToDo: Implement getTypicalDegreePlannerList for DegreePlannerList and update the codes below
        model = new ModelManager(
                new JsonSerializableApplication(getTypicalModuleList(), getTypicalDegreePlannerList(),
                        getTypicalRequirementCategoriesList())
                        .toModelType(), new UserPrefs());
        expectedModel = new ModelManager(model.getApplication(), new UserPrefs());
    }

    @Test
    public void execute_plannerListIsNotFiltered_showsSameList() {
        StringBuilder degreePlannerListContent = new StringBuilder();
        for (DegreePlanner degreePlanner : model.getFilteredDegreePlannerList()) {
            degreePlannerListContent
                    .append("Year: " + degreePlanner.getYear() + " Semester: " + degreePlanner.getSemester() + "\n");
            if (degreePlanner.getCodes().isEmpty()) {
                degreePlannerListContent.append("No module inside");
            } else {
                degreePlannerListContent
                        .append("Modules: " + degreePlanner.getCodes().stream().map(Code::toString).collect(
                                Collectors.joining(", ")));
            }
            degreePlannerListContent.append("\n\n");
        }
        String expectedMessage =
                String.format(PlannerListAllCommand.MESSAGE_SUCCESS, degreePlannerListContent.toString());
        assertCommandSuccess(new PlannerListAllCommand(), model, commandHistory, expectedMessage,
                expectedModel);
    }

}
