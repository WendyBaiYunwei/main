package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.deleteFirstModule;
import static seedu.address.testutil.TypicalModules.getTypicalAddressBook;

import org.junit.Before;
import org.junit.Test;

import seedu.address.logic.CommandHistory;
import seedu.address.model.DegreePlannerList;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.RequirementCategoryList;
import seedu.address.model.UserPrefs;

public class RedoCommandTest {
    //ToDo: Implement getTypicalDegreePlannerList for DegreePlannerList and update the codes below
    private final Model model =
            new ModelManager(getTypicalAddressBook(), new DegreePlannerList(), new RequirementCategoryList(),
                    new UserPrefs());
    private final Model expectedModel =
            new ModelManager(getTypicalAddressBook(), new DegreePlannerList(), new RequirementCategoryList(),
                    new UserPrefs());
    private final CommandHistory commandHistory = new CommandHistory();

    @Before
    public void setUp() {
        // set up of both models' undo/redo history
        deleteFirstModule(model);
        deleteFirstModule(model);
        model.undoAddressBook();
        model.undoAddressBook();

        deleteFirstModule(expectedModel);
        deleteFirstModule(expectedModel);
        expectedModel.undoAddressBook();
        expectedModel.undoAddressBook();
    }

    @Test
    public void execute() {
        // multiple redoable states in model
        expectedModel.redoAddressBook();
        assertCommandSuccess(new RedoCommand(), model, commandHistory, RedoCommand.MESSAGE_SUCCESS, expectedModel);

        // single redoable state in model
        expectedModel.redoAddressBook();
        assertCommandSuccess(new RedoCommand(), model, commandHistory, RedoCommand.MESSAGE_SUCCESS, expectedModel);

        // no redoable state in model
        assertCommandFailure(new RedoCommand(), model, commandHistory, RedoCommand.MESSAGE_FAILURE);
    }
}
