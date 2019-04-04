package pwe.planner.logic.commands;

import static pwe.planner.logic.commands.CommandTestUtil.assertCommandFailure;
import static pwe.planner.logic.commands.CommandTestUtil.assertCommandSuccess;
import static pwe.planner.testutil.TypicalDegreePlanners.getTypicalDegreePlannerList;
import static pwe.planner.testutil.TypicalModules.getTypicalModuleList;
import static pwe.planner.testutil.TypicalRequirementCategories.getTypicalRequirementCategoriesList;

import org.junit.Before;
import org.junit.Test;

import pwe.planner.commons.exceptions.IllegalValueException;
import pwe.planner.logic.CommandHistory;
import pwe.planner.model.Model;
import pwe.planner.model.ModelManager;
import pwe.planner.model.UserPrefs;
import pwe.planner.model.module.Module;
import pwe.planner.storage.JsonSerializableApplication;
import pwe.planner.testutil.ModuleBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code AddCommand}.
 */
public class AddCommandIntegrationTest {

    private Model model;
    private CommandHistory commandHistory = new CommandHistory();

    @Before
    public void setUp() throws IllegalValueException {
        //ToDo: Implement getTypicalDegreePlannerList for DegreePlannerList and update the codes below
        model = new ModelManager(new JsonSerializableApplication(getTypicalModuleList(), getTypicalDegreePlannerList(),
                getTypicalRequirementCategoriesList()).toModelType(), new UserPrefs());
    }

    @Test
    public void execute_newModule_success() {
        Module validModule = new ModuleBuilder().build();
        Model expectedModel = new ModelManager(model.getApplication(), new UserPrefs());
        expectedModel.addModule(validModule);
        expectedModel.commitApplication();

        assertCommandSuccess(new AddCommand(validModule), model, commandHistory,
                String.format(AddCommand.MESSAGE_SUCCESS, validModule), expectedModel);
    }

    @Test
    public void execute_duplicateModule_throwsCommandException() {
        Module moduleInList = model.getApplication().getModuleList().get(0);
        assertCommandFailure(new AddCommand(moduleInList), model, commandHistory,
                String.format(AddCommand.MESSAGE_DUPLICATE_MODULE, moduleInList.getCode())
        );
    }

}
