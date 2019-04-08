package systemtests;

import static pwe.planner.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;
import static pwe.planner.logic.commands.ClearCommand.MESSAGE_USAGE;
import static pwe.planner.model.util.InitialDataUtil.getInitialApplication;
import static pwe.planner.testutil.TypicalModules.KEYWORD_MATCHING_MEIER;

import org.junit.Test;

import pwe.planner.commons.core.Messages;
import pwe.planner.commons.core.index.Index;
import pwe.planner.logic.commands.ClearCommand;
import pwe.planner.logic.commands.RedoCommand;
import pwe.planner.logic.commands.UndoCommand;
import pwe.planner.model.Model;
import pwe.planner.model.ModelManager;

public class ClearCommandSystemTest extends ApplicationSystemTest {

    @Test
    public void clear() {
        final Model defaultModel = getModel();

        /* Case: clear non-empty application, command with leading spaces and trailing alphanumeric characters and
         * spaces -> failed
         */
        String command = ClearCommand.COMMAND_WORD + "      ab4";
        String expectedResultMessage = String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE);
        assertCommandFailure(command, expectedResultMessage);
        assertSelectedCardUnchanged();


        /*
         * Case: clear command with trailing spaces -> passed
         */
        assertCommandSuccess(ClearCommand.COMMAND_WORD + "              ");
        assertSelectedCardUnchanged();

        /* Case: undo clearing application -> original application restored */
        command = UndoCommand.COMMAND_WORD;
        expectedResultMessage = UndoCommand.MESSAGE_SUCCESS;
        assertCommandSuccess(command, expectedResultMessage, defaultModel);
        assertSelectedCardUnchanged();

        /* Case: redo clearing application -> cleared */
        Model expectedModel = new ModelManager(getInitialApplication());
        command = RedoCommand.COMMAND_WORD;
        expectedResultMessage = RedoCommand.MESSAGE_SUCCESS;
        assertCommandSuccess(command, expectedResultMessage, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: selects first card in module list and clears application -> cleared and no card selected */
        executeCommand(UndoCommand.COMMAND_WORD); // restores the original application
        selectModule(Index.fromOneBased(1));
        assertCommandSuccess(ClearCommand.COMMAND_WORD);
        assertSelectedCardDeselected();

        /* Case: filters the module list before clearing -> entire application cleared */
        executeCommand(UndoCommand.COMMAND_WORD); // restores the original application
        showModulesWithName(KEYWORD_MATCHING_MEIER);
        assertCommandSuccess(ClearCommand.COMMAND_WORD);
        assertSelectedCardUnchanged();

        /* Case: clear empty application -> cleared */
        assertCommandSuccess(ClearCommand.COMMAND_WORD);
        assertSelectedCardUnchanged();

        /* Case: mixed case command word -> rejected */
        assertCommandFailure("ClEaR", MESSAGE_UNKNOWN_COMMAND);
    }

    /**
     * Executes {@code command} and verifies that the command box displays an empty string, the result display
     * box displays {@code ClearCommand#MESSAGE_SUCCESS} and the model related components equal to an empty model.
     * These verifications are done by
     * {@code ApplicationSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.<br>
     * Also verifies that the command box has the default style class and the status bar's sync status changes.
     * @see ApplicationSystemTest#assertApplicationDisplaysExpected(String, String, Model)
     */
    private void assertCommandSuccess(String command) {
        Model expectedModel = new ModelManager(getInitialApplication());
        assertCommandSuccess(command, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    /**
     * Performs the same verification as {@code assertCommandSuccess(String)} except that the result box displays
     * {@code expectedResultMessage} and the model related components equal to {@code expectedModel}.
     * @see ClearCommandSystemTest#assertCommandSuccess(String)
     */
    private void assertCommandSuccess(String command, String expectedResultMessage, Model expectedModel) {
        executeCommand(command);
        assertApplicationDisplaysExpected("", expectedResultMessage, expectedModel);
        assertCommandBoxShowsDefaultStyle();
        assertStatusBarUnchangedExceptSyncStatus();
    }

    /**
     * Executes {@code command} and verifies that the command box displays {@code command}, the result display
     * box displays {@code expectedResultMessage} and the model related components equal to the current model.
     * These verifications are done by
     * {@code ApplicationSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.<br>
     * Also verifies that the browser url, selected card and status bar remain unchanged, and the command box has the
     * error style.
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
