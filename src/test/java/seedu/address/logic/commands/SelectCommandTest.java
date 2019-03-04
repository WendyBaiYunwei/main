package seedu.address.logic.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showModuleAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_MODULE;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_MODULE;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_MODULE;
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

/**
 * Contains integration tests (interaction with the Model) for {@code SelectCommand}.
 */
public class SelectCommandTest {
    //ToDo: Implement getTypicalDegreePlannerList for DegreePlannerList and update the codes below
    private Model model =
            new ModelManager(getTypicalAddressBook(), new DegreePlannerList(), new RequirementCategoryList(),
                    new UserPrefs());
    private Model expectedModel =
            new ModelManager(getTypicalAddressBook(), new DegreePlannerList(), new RequirementCategoryList(),
                    new UserPrefs());
    private CommandHistory commandHistory = new CommandHistory();

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Index lastModuleIndex = Index.fromOneBased(model.getFilteredModuleList().size());

        assertExecutionSuccess(INDEX_FIRST_MODULE);
        assertExecutionSuccess(INDEX_THIRD_MODULE);
        assertExecutionSuccess(lastModuleIndex);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_failure() {
        Index outOfBoundsIndex = Index.fromOneBased(model.getFilteredModuleList().size() + 1);

        assertExecutionFailure(outOfBoundsIndex, Messages.MESSAGE_INVALID_MODULE_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showModuleAtIndex(model, INDEX_FIRST_MODULE);
        showModuleAtIndex(expectedModel, INDEX_FIRST_MODULE);

        assertExecutionSuccess(INDEX_FIRST_MODULE);
    }

    @Test
    public void execute_invalidIndexFilteredList_failure() {
        showModuleAtIndex(model, INDEX_FIRST_MODULE);
        showModuleAtIndex(expectedModel, INDEX_FIRST_MODULE);

        Index outOfBoundsIndex = INDEX_SECOND_MODULE;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundsIndex.getZeroBased() < model.getAddressBook().getModuleList().size());

        assertExecutionFailure(outOfBoundsIndex, Messages.MESSAGE_INVALID_MODULE_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        SelectCommand selectFirstCommand = new SelectCommand(INDEX_FIRST_MODULE);
        SelectCommand selectSecondCommand = new SelectCommand(INDEX_SECOND_MODULE);

        // same object -> returns true
        assertTrue(selectFirstCommand.equals(selectFirstCommand));

        // same values -> returns true
        SelectCommand selectFirstCommandCopy = new SelectCommand(INDEX_FIRST_MODULE);
        assertTrue(selectFirstCommand.equals(selectFirstCommandCopy));

        // different types -> returns false
        assertFalse(selectFirstCommand.equals(1));

        // null -> returns false
        assertFalse(selectFirstCommand.equals(null));

        // different module -> returns false
        assertFalse(selectFirstCommand.equals(selectSecondCommand));
    }

    /**
     * Executes a {@code SelectCommand} with the given {@code index},
     * and checks that the model's selected module is set to the module at {@code index} in the filtered module list.
     */
    private void assertExecutionSuccess(Index index) {
        SelectCommand selectCommand = new SelectCommand(index);
        String expectedMessage = String.format(SelectCommand.MESSAGE_SELECT_MODULE_SUCCESS, index.getOneBased());
        expectedModel.setSelectedModule(model.getFilteredModuleList().get(index.getZeroBased()));

        assertCommandSuccess(selectCommand, model, commandHistory, expectedMessage, expectedModel);
    }

    /**
     * Executes a {@code SelectCommand} with the given {@code index}, and checks that a {@code CommandException}
     * is thrown with the {@code expectedMessage}.
     */
    private void assertExecutionFailure(Index index, String expectedMessage) {
        SelectCommand selectCommand = new SelectCommand(index);
        assertCommandFailure(selectCommand, model, commandHistory, expectedMessage);
    }
}
