package systemtests;

import static org.junit.Assert.assertTrue;
import static pwe.planner.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static pwe.planner.commons.core.Messages.MESSAGE_INVALID_MODULE_DISPLAYED_INDEX;
import static pwe.planner.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;
import static pwe.planner.logic.commands.DeleteCommand.MESSAGE_DELETE_MODULE_SUCCESS;
import static pwe.planner.testutil.TestUtil.getLastIndex;
import static pwe.planner.testutil.TestUtil.getMidIndex;
import static pwe.planner.testutil.TestUtil.getModule;
import static pwe.planner.testutil.TypicalIndexes.INDEX_FIRST_MODULE;
import static pwe.planner.testutil.TypicalModules.KEYWORD_MATCHING_MEIER;

import org.junit.Test;

import pwe.planner.commons.core.index.Index;
import pwe.planner.logic.commands.DeleteCommand;
import pwe.planner.logic.commands.RedoCommand;
import pwe.planner.logic.commands.UndoCommand;
import pwe.planner.model.Model;
import pwe.planner.model.module.Module;

public class DeleteCommandSystemTest extends ApplicationSystemTest {

    private static final String MESSAGE_INVALID_DELETE_COMMAND_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE);

    @Test
    public void delete() {
        /* ----------------- Performing delete operation while an unfiltered list is being shown -------------------- */

        /* Case: delete the first module in the list, command with leading spaces and trailing spaces -> deleted */
        Model expectedModel = getModel();
        String command = "     " + DeleteCommand.COMMAND_WORD + "      " + INDEX_FIRST_MODULE.getOneBased() + "       ";
        Module deletedModule = removeModule(expectedModel, INDEX_FIRST_MODULE);
        String expectedResultMessage = String.format(MESSAGE_DELETE_MODULE_SUCCESS, deletedModule);
        assertCommandSuccess(command, expectedModel, expectedResultMessage);

        /* Case: delete the last module in the list -> deleted */
        Model modelBeforeDeletingLast = getModel();
        Index lastModuleIndex = getLastIndex(modelBeforeDeletingLast);
        assertCommandSuccess(lastModuleIndex);

        /* Case: undo deleting the last module in the list -> last module restored */
        command = UndoCommand.COMMAND_WORD;
        expectedResultMessage = UndoCommand.MESSAGE_SUCCESS;
        assertCommandSuccess(command, modelBeforeDeletingLast, expectedResultMessage);

        /* Case: redo deleting the last module in the list -> last module deleted again */
        command = RedoCommand.COMMAND_WORD;
        removeModule(modelBeforeDeletingLast, lastModuleIndex);
        expectedResultMessage = RedoCommand.MESSAGE_SUCCESS;
        assertCommandSuccess(command, modelBeforeDeletingLast, expectedResultMessage);

        /* Case: delete the middle module in the list -> deleted */
        Index middleModuleIndex = getMidIndex(getModel());
        assertCommandSuccess(middleModuleIndex);

        /* ------------------ Performing delete operation while a filtered list is being shown ---------------------- */

        /* Case: filtered module list, delete index within bounds of application and module list -> deleted */
        showModulesWithName(KEYWORD_MATCHING_MEIER);
        Index index = INDEX_FIRST_MODULE;
        assertTrue(index.getZeroBased() < getModel().getFilteredModuleList().size());
        assertCommandSuccess(index);

        /* Case: filtered module list, delete index within bounds of application but out of bounds of module list
         * -> rejected
         */
        showModulesWithName(KEYWORD_MATCHING_MEIER);
        int invalidIndex = getModel().getApplication().getModuleList().size();
        command = DeleteCommand.COMMAND_WORD + " " + invalidIndex;
        assertCommandFailure(command, MESSAGE_INVALID_MODULE_DISPLAYED_INDEX);

        /* --------------------- Performing delete operation while a module card is selected ------------------------ */

        /* Case: delete the selected module -> module list panel selects the module before the deleted module */
        showAllModules();
        expectedModel = getModel();
        Index selectedIndex = getLastIndex(expectedModel);
        Index expectedIndex = Index.fromZeroBased(selectedIndex.getZeroBased() - 1);
        selectModule(selectedIndex);
        command = DeleteCommand.COMMAND_WORD + " " + selectedIndex.getOneBased();
        deletedModule = removeModule(expectedModel, selectedIndex);
        expectedModel.updateFilteredModuleList(Model.PREDICATE_SHOW_ALL_MODULES);
        expectedResultMessage = String.format(MESSAGE_DELETE_MODULE_SUCCESS, deletedModule);
        assertCommandSuccess(command, expectedModel, expectedResultMessage, expectedIndex);

        /* --------------------------------- Performing invalid delete operation ------------------------------------ */

        /* Case: invalid index (0) -> rejected */
        command = DeleteCommand.COMMAND_WORD + " 0";
        assertCommandFailure(command, MESSAGE_INVALID_DELETE_COMMAND_FORMAT);

        /* Case: invalid index (-1) -> rejected */
        command = DeleteCommand.COMMAND_WORD + " -1";
        assertCommandFailure(command, MESSAGE_INVALID_DELETE_COMMAND_FORMAT);

        /* Case: invalid index (size + 1) -> rejected */
        Index outOfBoundsIndex = Index.fromOneBased(
                getModel().getApplication().getModuleList().size() + 1);
        command = DeleteCommand.COMMAND_WORD + " " + outOfBoundsIndex.getOneBased();
        assertCommandFailure(command, MESSAGE_INVALID_MODULE_DISPLAYED_INDEX);

        /* Case: invalid arguments (alphabets) -> rejected */
        assertCommandFailure(DeleteCommand.COMMAND_WORD + " abc", MESSAGE_INVALID_DELETE_COMMAND_FORMAT);

        /* Case: invalid arguments (extra argument) -> rejected */
        assertCommandFailure(DeleteCommand.COMMAND_WORD + " 1 abc", MESSAGE_INVALID_DELETE_COMMAND_FORMAT);

        /* Case: mixed case command word -> rejected */
        assertCommandFailure("DelETE 1", MESSAGE_UNKNOWN_COMMAND);
    }

    /**
     * Removes the {@code Module} at the specified {@code index} in {@code model}'s application.
     *
     * @return the removed module
     */
    private Module removeModule(Model model, Index index) {
        Module targetModule = getModule(model, index);
        model.deleteModule(targetModule);
        return targetModule;
    }

    /**
     * Deletes the module at {@code toDelete} by creating a default {@code DeleteCommand} using {@code toDelete} and
     * performs the same verification as {@code assertCommandSuccess(String, Model, String)}.
     *
     * @see DeleteCommandSystemTest#assertCommandSuccess(String, Model, String)
     */
    private void assertCommandSuccess(Index toDelete) {
        Model expectedModel = getModel();
        Module deletedModule = removeModule(expectedModel, toDelete);
        String expectedResultMessage = String.format(MESSAGE_DELETE_MODULE_SUCCESS, deletedModule);

        assertCommandSuccess(
                DeleteCommand.COMMAND_WORD + " " + toDelete.getOneBased(), expectedModel, expectedResultMessage);
    }

    /**
     * Executes {@code command} and in addition,<br>
     * 1. Asserts that the command box displays an empty string.<br>
     * 2. Asserts that the result display box displays {@code expectedResultMessage}.<br>
     * 3. Asserts that the browser url and selected card remains unchanged.<br>
     * 4. Asserts that the status bar's sync status changes.<br>
     * 5. Asserts that the command box has the default style class.<br>
     * Verifications 1 and 2 are performed by
     * {@code ApplicationSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.
     *
     * @see ApplicationSystemTest#assertApplicationDisplaysExpected(String, String, Model)
     */
    private void assertCommandSuccess(String command, Model expectedModel, String expectedResultMessage) {
        assertCommandSuccess(command, expectedModel, expectedResultMessage, null);
    }

    /**
     * Performs the same verification as {@code assertCommandSuccess(String, Model, String)} except that the browser url
     * and selected card are expected to update accordingly depending on the card at {@code expectedSelectedCardIndex}.
     *
     * @see DeleteCommandSystemTest#assertCommandSuccess(String, Model, String)
     * @see ApplicationSystemTest#assertSelectedCardChanged(Index)
     */
    private void assertCommandSuccess(String command, Model expectedModel, String expectedResultMessage,
            Index expectedSelectedCardIndex) {
        executeCommand(command);
        assertApplicationDisplaysExpected("", expectedResultMessage, expectedModel);

        if (expectedSelectedCardIndex != null) {
            assertSelectedCardChanged(expectedSelectedCardIndex);
        } else {
            assertSelectedCardUnchanged();
        }

        assertCommandBoxShowsDefaultStyle();
        assertStatusBarUnchangedExceptSyncStatus();
    }

    /**
     * Executes {@code command} and in addition,<br>
     * 1. Asserts that the command box displays {@code command}.<br>
     * 2. Asserts that result display box displays {@code expectedResultMessage}.<br>
     * 3. Asserts that the browser url, selected card and status bar remain unchanged.<br>
     * 4. Asserts that the command box has the error style.<br>
     * Verifications 1 and 2 are performed by
     * {@code ApplicationSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.<br>
     *
     * @see ApplicationSystemTest#assertApplicationDisplaysExpected(String, String, Model)
     */
    private void assertCommandFailure(String command, String expectedResultMessage) {
        Model expectedModel = getModel();

        executeCommand(command);
        assertApplicationDisplaysExpected(command, expectedResultMessage, expectedModel);
        assertSelectedCardUnchanged();
        assertCommandBoxShowsErrorStyle();
        assertStatusBarUnchanged();
    }
}
