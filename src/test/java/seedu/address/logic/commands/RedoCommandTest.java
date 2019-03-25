package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.deleteFirstModule;
import static seedu.address.testutil.TypicalDegreePlanners.getTypicalDegreePlannerList;
import static seedu.address.testutil.TypicalModules.getTypicalModuleList;
import static seedu.address.testutil.TypicalRequirementCategories.getTypicalRequirementCategoriesList;

import org.junit.Before;
import org.junit.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.CommandHistory;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.storage.JsonSerializableAddressBook;

public class RedoCommandTest {
    //ToDo: Implement getTypicalDegreePlannerList for DegreePlannerList and update the codes below
    private final Model model =
            new ModelManager(
                    new JsonSerializableAddressBook(getTypicalModuleList(), getTypicalDegreePlannerList(),
                            getTypicalRequirementCategoriesList()).toModelType(), new UserPrefs());
    private final Model expectedModel =
            new ModelManager(
                    new JsonSerializableAddressBook(getTypicalModuleList(), getTypicalDegreePlannerList(),
                            getTypicalRequirementCategoriesList()).toModelType(), new UserPrefs());
    private final CommandHistory commandHistory = new CommandHistory();

    public RedoCommandTest() throws IllegalValueException {}

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
