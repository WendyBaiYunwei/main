package pwe.planner.logic.commands;

import static pwe.planner.logic.commands.CommandTestUtil.assertCommandFailure;
import static pwe.planner.logic.commands.CommandTestUtil.assertCommandSuccess;
import static pwe.planner.testutil.TypicalIndexes.INDEX_FIRST_MODULE;

import org.junit.Before;
import org.junit.Test;

import pwe.planner.logic.CommandHistory;
import pwe.planner.model.Model;
import pwe.planner.model.ModelManager;
import pwe.planner.model.UserPrefs;
import pwe.planner.model.module.Module;
import pwe.planner.model.util.SampleDataUtil;
import pwe.planner.testutil.ModuleBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code AddCommand}.
 */
public class AddCommandIntegrationTest {

    private Model model;
    private CommandHistory commandHistory = new CommandHistory();

    @Before
    public void setUp() {
        model = new ModelManager(SampleDataUtil.getSampleApplication(), new UserPrefs());
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
        Module moduleInList = model.getApplication().getModuleList().get(INDEX_FIRST_MODULE.getZeroBased());
        assertCommandFailure(new AddCommand(moduleInList), model, commandHistory,
                String.format(AddCommand.MESSAGE_DUPLICATE_MODULE, moduleInList.getCode())
        );
    }

    @Test
    public void execute_nonExistentCorequisites_throwsCommandException() {
        String nonExistentCorequisite = "ZYX9876W";
        Module moduleWithOnlyInvalidCorequisites = new ModuleBuilder().withCorequisites(nonExistentCorequisite).build();
        String expectedMessage = String.format(AddCommand.MESSAGE_NON_EXISTENT_COREQUISITE,
                moduleWithOnlyInvalidCorequisites.getCode(), nonExistentCorequisite);
        assertCommandFailure(new AddCommand(moduleWithOnlyInvalidCorequisites), model, commandHistory, expectedMessage);
    }

    @Test
    public void execute_corequisitesExistsInDegreePlanner_throwsCommandException() {
        Module moduleInList = model.getApplication().getModuleList().get(INDEX_FIRST_MODULE.getZeroBased());
        Module moduleWithInvalidCorequisite = new ModuleBuilder()
                .withCorequisites(moduleInList.getCode().toString()).build();
        String expectedMessage = String.format(AddCommand.MESSAGE_EXISTING_COREQUISITES_IN_DEGREE_PLAN,
                moduleWithInvalidCorequisite.getCode(), moduleInList.getCode());
        assertCommandFailure(new AddCommand(moduleWithInvalidCorequisite), model, commandHistory, expectedMessage);
    }
}
