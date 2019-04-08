package pwe.planner.logic.commands;

import static pwe.planner.logic.commands.ClearCommand.PLANNER;
import static pwe.planner.logic.commands.ClearCommand.REQUIREMENT;
import static pwe.planner.logic.commands.CommandTestUtil.assertCommandSuccess;
import static pwe.planner.model.util.InitialDataUtil.getInitialApplication;
import static pwe.planner.testutil.TypicalDegreePlanners.getTypicalDegreePlannerList;
import static pwe.planner.testutil.TypicalModules.getTypicalModuleList;
import static pwe.planner.testutil.TypicalRequirementCategories.getTypicalRequirementCategoriesList;

import org.junit.Test;

import pwe.planner.commons.exceptions.IllegalValueException;
import pwe.planner.logic.CommandHistory;
import pwe.planner.model.Model;
import pwe.planner.model.ModelManager;
import pwe.planner.model.UserPrefs;
import pwe.planner.storage.JsonSerializableApplication;
import pwe.planner.testutil.TypicalDegreePlanners;
import pwe.planner.testutil.TypicalModules;
import pwe.planner.testutil.TypicalRequirementCategories;

public class ClearCommandTest {

    private CommandHistory commandHistory = new CommandHistory();

    @Test
    public void execute_emptyapplication_success() {
        Model model = new ModelManager();
        Model expectedModel = new ModelManager();
        expectedModel.setApplication(getInitialApplication());
        expectedModel.commitApplication();

        assertCommandSuccess(new ClearCommand(), model, commandHistory,
                ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_clearRequirementCategoriesAndDegreePlanners() {
        Model model = new ModelManager();
        Model expectedModel = new ModelManager();
        // only module is empty
        TypicalDegreePlanners.getTypicalDegreePlanners().forEach(expectedModel::addDegreePlanner);
        TypicalRequirementCategories.getTypicalRequirementCategories().forEach(
                expectedModel::addRequirementCategory);
        TypicalModules.getTypicalModules().forEach(expectedModel::addModule);
        // actual model having all 3 sets of data
        TypicalDegreePlanners.getTypicalDegreePlanners().forEach(model::addDegreePlanner);
        TypicalRequirementCategories.getTypicalRequirementCategories().forEach(
                model::addRequirementCategory);
        TypicalModules.getTypicalModules().forEach(model::addModule);

        expectedModel.resetRequirement();
        expectedModel.commitApplication();

        assertCommandSuccess(new ClearCommand(REQUIREMENT), model, commandHistory,
                ClearCommand.MESSAGE_SUCCESS, expectedModel);

        expectedModel.resetPlanner();
        expectedModel.commitApplication();

        assertCommandSuccess(new ClearCommand(PLANNER), model, commandHistory,
                ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_nonEmptyapplication_success() throws IllegalValueException {
        Model model =
                new ModelManager(new JsonSerializableApplication(getTypicalModuleList(), getTypicalDegreePlannerList(),
                        getTypicalRequirementCategoriesList()).toModelType(), new UserPrefs());
        Model expectedModel =
                new ModelManager(new JsonSerializableApplication(getTypicalModuleList(), getTypicalDegreePlannerList(),
                        getTypicalRequirementCategoriesList()).toModelType(), new UserPrefs());
        expectedModel.setApplication(getInitialApplication());
        expectedModel.commitApplication();

        assertCommandSuccess(new ClearCommand(), model, commandHistory, ClearCommand.MESSAGE_SUCCESS,
                expectedModel);
    }

}
