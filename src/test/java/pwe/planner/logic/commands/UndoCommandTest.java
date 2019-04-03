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

public class UndoCommandTest {

    //ToDo: Implement getTypicalDegreePlannerList for DegreePlannerList and update the codes below
    private final Model model =
            new ModelManager(new JsonSerializableApplication(getTypicalModuleList(), getTypicalDegreePlannerList(),
                    getTypicalRequirementCategoriesList()).toModelType(), new UserPrefs());
    private final Model expectedModel =
            new ModelManager(new JsonSerializableApplication(getTypicalModuleList(), getTypicalDegreePlannerList(),
                    getTypicalRequirementCategoriesList()).toModelType(), new UserPrefs());
    private final CommandHistory commandHistory = new CommandHistory();

    public UndoCommandTest() throws IllegalValueException {}

    @Before
    public void setUp() {
        // set up of models' undo/redo history
        deleteFirstModule(model);
        deleteFirstModule(model);

        deleteFirstModule(expectedModel);
        deleteFirstModule(expectedModel);
    }

    @Test
    public void execute() {
        // multiple undoable states in model
        expectedModel.undoApplication();
        assertCommandSuccess(new UndoCommand(), model, commandHistory, UndoCommand.MESSAGE_SUCCESS, expectedModel);

        // single undoable state in model
        expectedModel.undoApplication();
        assertCommandSuccess(new UndoCommand(), model, commandHistory, UndoCommand.MESSAGE_SUCCESS, expectedModel);

        // no undoable states in model
        assertCommandFailure(new UndoCommand(), model, commandHistory, UndoCommand.MESSAGE_FAILURE);
    }
}
