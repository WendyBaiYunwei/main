package pwe.planner.logic.commands;

import static pwe.planner.logic.commands.CommandTestUtil.assertCommandFailure;
import static pwe.planner.logic.commands.CommandTestUtil.assertCommandSuccess;
import static pwe.planner.logic.commands.CommandTestUtil.deleteFirstModule;
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
import pwe.planner.storage.JsonSerializableApplication;

public class RedoCommandTest {
    //ToDo: Implement getTypicalDegreePlannerList for DegreePlannerList and update the codes below
    private final Model model =
            new ModelManager(
                    new JsonSerializableApplication(getTypicalModuleList(), getTypicalDegreePlannerList(),
                            getTypicalRequirementCategoriesList()).toModelType(), new UserPrefs());
    private final Model expectedModel =
            new ModelManager(
                    new JsonSerializableApplication(getTypicalModuleList(), getTypicalDegreePlannerList(),
                            getTypicalRequirementCategoriesList()).toModelType(), new UserPrefs());
    private final CommandHistory commandHistory = new CommandHistory();

    public RedoCommandTest() throws IllegalValueException {}

    @Before
    public void setUp() {
        // set up of both models' undo/redo history
        deleteFirstModule(model);
        deleteFirstModule(model);
        model.undoApplication();
        model.undoApplication();

        deleteFirstModule(expectedModel);
        deleteFirstModule(expectedModel);
        expectedModel.undoApplication();
        expectedModel.undoApplication();
    }

    @Test
    public void execute() {
        // multiple redoable states in model
        expectedModel.redoApplication();
        assertCommandSuccess(new RedoCommand(), model, commandHistory, RedoCommand.MESSAGE_SUCCESS, expectedModel);

        // single redoable state in model
        expectedModel.redoApplication();
        assertCommandSuccess(new RedoCommand(), model, commandHistory, RedoCommand.MESSAGE_SUCCESS, expectedModel);

        // no redoable state in model
        assertCommandFailure(new RedoCommand(), model, commandHistory, RedoCommand.MESSAGE_FAILURE);
    }
}
