package seedu.address.logic.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showModuleAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_MODULE;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_MODULE;
import static seedu.address.testutil.TypicalModules.getTypicalAddressBook;

import org.junit.Test;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.CommandHistory;
import seedu.address.model.DegreePlannerList;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.RequirementCategoryList;
import seedu.address.model.UserPrefs;
import seedu.address.model.module.Module;

/**
 * Contains integration tests (interaction with the Model, UndoCommand and RedoCommand) and unit tests for
 * {@code DeleteCommand}.
 */
public class DeleteCommandTest {
    //ToDo: Implement getTypicalDegreePlannerList for DegreePlannerList and update the codes below
    private Model model =
            new ModelManager(getTypicalAddressBook(), new DegreePlannerList(), new RequirementCategoryList(),
                    new UserPrefs());
    private CommandHistory commandHistory = new CommandHistory();

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Module moduleToDelete = model.getFilteredModuleList().get(INDEX_FIRST_MODULE.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_MODULE);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_MODULE_SUCCESS, moduleToDelete);

        ModelManager expectedModel =
                new ModelManager(model.getAddressBook(), model.getDegreePlannerList(),
                        model.getRequirementCategoryList(), new UserPrefs());
        expectedModel.deleteModule(moduleToDelete);
        expectedModel.commitAddressBook();

        assertCommandSuccess(deleteCommand, model, commandHistory, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredModuleList().size() + 1);
        DeleteCommand deleteCommand = new DeleteCommand(outOfBoundIndex);

        assertCommandFailure(deleteCommand, model, commandHistory, Messages.MESSAGE_INVALID_MODULE_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showModuleAtIndex(model, INDEX_FIRST_MODULE);

        Module moduleToDelete = model.getFilteredModuleList().get(INDEX_FIRST_MODULE.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_MODULE);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_MODULE_SUCCESS, moduleToDelete);

        Model expectedModel = new ModelManager(model.getAddressBook(), model.getDegreePlannerList(),
                model.getRequirementCategoryList(), new UserPrefs());
        expectedModel.deleteModule(moduleToDelete);
        expectedModel.commitAddressBook();
        showNoModule(expectedModel);

        assertCommandSuccess(deleteCommand, model, commandHistory, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showModuleAtIndex(model, INDEX_FIRST_MODULE);

        Index outOfBoundIndex = INDEX_SECOND_MODULE;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getModuleList().size());

        DeleteCommand deleteCommand = new DeleteCommand(outOfBoundIndex);

        assertCommandFailure(deleteCommand, model, commandHistory, Messages.MESSAGE_INVALID_MODULE_DISPLAYED_INDEX);
    }

    @Test
    public void executeUndoRedo_validIndexUnfilteredList_success() throws Exception {
        Module moduleToDelete = model.getFilteredModuleList().get(INDEX_FIRST_MODULE.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_MODULE);
        Model expectedModel = new ModelManager(model.getAddressBook(), model.getDegreePlannerList(),
                model.getRequirementCategoryList(), new UserPrefs());
        expectedModel.deleteModule(moduleToDelete);
        expectedModel.commitAddressBook();

        // delete -> first module deleted
        deleteCommand.execute(model, commandHistory);

        // undo -> reverts addressbook back to previous state and filtered module list to show all modules
        expectedModel.undoAddressBook();
        assertCommandSuccess(new UndoCommand(), model, commandHistory, UndoCommand.MESSAGE_SUCCESS, expectedModel);

        // redo -> same first module deleted again
        expectedModel.redoAddressBook();
        assertCommandSuccess(new RedoCommand(), model, commandHistory, RedoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void executeUndoRedo_invalidIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredModuleList().size() + 1);
        DeleteCommand deleteCommand = new DeleteCommand(outOfBoundIndex);

        // execution failed -> address book state not added into model
        assertCommandFailure(deleteCommand, model, commandHistory, Messages.MESSAGE_INVALID_MODULE_DISPLAYED_INDEX);

        // single address book state in model -> undoCommand and redoCommand fail
        assertCommandFailure(new UndoCommand(), model, commandHistory, UndoCommand.MESSAGE_FAILURE);
        assertCommandFailure(new RedoCommand(), model, commandHistory, RedoCommand.MESSAGE_FAILURE);
    }

    /**
     * 1. Deletes a {@code Module} from a filtered list.
     * 2. Undo the deletion.
     * 3. The unfiltered list should be shown now. Verify that the index of the previously deleted module in the
     * unfiltered list is different from the index at the filtered list.
     * 4. Redo the deletion. This ensures {@code RedoCommand} deletes the module object regardless of indexing.
     */
    @Test
    public void executeUndoRedo_validIndexFilteredList_sameModuleDeleted() throws Exception {
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_MODULE);

        Model expectedModel = new ModelManager(model.getAddressBook(), model.getDegreePlannerList(),
                model.getRequirementCategoryList(), new UserPrefs());

        showModuleAtIndex(model, INDEX_SECOND_MODULE);
        Module moduleToDelete = model.getFilteredModuleList().get(INDEX_FIRST_MODULE.getZeroBased());
        expectedModel.deleteModule(moduleToDelete);
        expectedModel.commitAddressBook();

        // delete -> deletes second module in unfiltered module list / first module in filtered module list
        deleteCommand.execute(model, commandHistory);

        // undo -> reverts addressbook back to previous state and filtered module list to show all modules
        expectedModel.undoAddressBook();
        assertCommandSuccess(new UndoCommand(), model, commandHistory, UndoCommand.MESSAGE_SUCCESS, expectedModel);

        assertNotEquals(moduleToDelete, model.getFilteredModuleList().get(INDEX_FIRST_MODULE.getZeroBased()));
        // redo -> deletes same second module in unfiltered module list
        expectedModel.redoAddressBook();
        assertCommandSuccess(new RedoCommand(), model, commandHistory, RedoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void equals() {
        DeleteCommand deleteFirstCommand = new DeleteCommand(INDEX_FIRST_MODULE);
        DeleteCommand deleteSecondCommand = new DeleteCommand(INDEX_SECOND_MODULE);

        // same object -> returns true
        assertTrue(deleteFirstCommand.equals(deleteFirstCommand));

        // same values -> returns true
        DeleteCommand deleteFirstCommandCopy = new DeleteCommand(INDEX_FIRST_MODULE);
        assertTrue(deleteFirstCommand.equals(deleteFirstCommandCopy));

        // different types -> returns false
        assertFalse(deleteFirstCommand.equals(1));

        // null -> returns false
        assertFalse(deleteFirstCommand.equals(null));

        // different module -> returns false
        assertFalse(deleteFirstCommand.equals(deleteSecondCommand));
    }

    /**
     * Updates {@code model}'s filtered list to show no one.
     */
    private void showNoModule(Model model) {
        model.updateFilteredModuleList(p -> false);

        assertTrue(model.getFilteredModuleList().isEmpty());
    }
}
