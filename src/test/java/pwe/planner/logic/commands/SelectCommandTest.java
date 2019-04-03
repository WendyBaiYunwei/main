package pwe.planner.logic.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static pwe.planner.commons.core.Messages.MESSAGE_INVALID_MODULE_DISPLAYED_INDEX;
import static pwe.planner.logic.commands.CommandTestUtil.assertCommandFailure;
import static pwe.planner.logic.commands.CommandTestUtil.assertCommandSuccess;
import static pwe.planner.logic.commands.CommandTestUtil.showModuleAtIndex;
import static pwe.planner.testutil.TypicalDegreePlanners.getTypicalDegreePlannerList;
import static pwe.planner.testutil.TypicalIndexes.INDEX_FIRST_MODULE;
import static pwe.planner.testutil.TypicalIndexes.INDEX_SECOND_MODULE;
import static pwe.planner.testutil.TypicalIndexes.INDEX_THIRD_MODULE;
import static pwe.planner.testutil.TypicalModules.getTypicalModuleList;
import static pwe.planner.testutil.TypicalRequirementCategories.getTypicalRequirementCategoriesList;

import org.junit.Test;

import pwe.planner.commons.core.index.Index;
import pwe.planner.commons.exceptions.IllegalValueException;
import pwe.planner.logic.CommandHistory;
import pwe.planner.model.Model;
import pwe.planner.model.ModelManager;
import pwe.planner.model.UserPrefs;
import pwe.planner.storage.JsonSerializableApplication;

/**
 * Contains integration tests (interaction with the Model) for {@code SelectCommand}.
 */
public class SelectCommandTest {
    //ToDo: Implement getTypicalDegreePlannerList for DegreePlannerList and update the codes below
    private Model model =
            new ModelManager(
                    new JsonSerializableApplication(getTypicalModuleList(), getTypicalDegreePlannerList(),
                            getTypicalRequirementCategoriesList()).toModelType(), new UserPrefs());
    private Model expectedModel =
            new ModelManager(
                    new JsonSerializableApplication(getTypicalModuleList(), getTypicalDegreePlannerList(),
                            getTypicalRequirementCategoriesList()).toModelType(), new UserPrefs());
    private CommandHistory commandHistory = new CommandHistory();

    public SelectCommandTest() throws IllegalValueException {}

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

        assertExecutionFailure(outOfBoundsIndex, MESSAGE_INVALID_MODULE_DISPLAYED_INDEX);
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
        // ensures that outOfBoundIndex is still in bounds of application list
        assertTrue(outOfBoundsIndex.getZeroBased() < model.getApplication().getModuleList().size());

        assertExecutionFailure(outOfBoundsIndex, MESSAGE_INVALID_MODULE_DISPLAYED_INDEX);
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
