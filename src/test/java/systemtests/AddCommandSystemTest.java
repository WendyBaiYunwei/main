package systemtests;

import static pwe.planner.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static pwe.planner.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;
import static pwe.planner.logic.commands.CommandTestUtil.CODE_DESC_AMY;
import static pwe.planner.logic.commands.CommandTestUtil.CODE_DESC_BOB;
import static pwe.planner.logic.commands.CommandTestUtil.CREDITS_DESC_AMY;
import static pwe.planner.logic.commands.CommandTestUtil.CREDITS_DESC_BOB;
import static pwe.planner.logic.commands.CommandTestUtil.INVALID_CODE_DESC;
import static pwe.planner.logic.commands.CommandTestUtil.INVALID_COREQUISITE_DESC;
import static pwe.planner.logic.commands.CommandTestUtil.INVALID_CREDITS_DESC;
import static pwe.planner.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static pwe.planner.logic.commands.CommandTestUtil.INVALID_SEMESTER_DESC_FIVE;
import static pwe.planner.logic.commands.CommandTestUtil.INVALID_TAG_DESC;
import static pwe.planner.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static pwe.planner.logic.commands.CommandTestUtil.NAME_DESC_BOB;
import static pwe.planner.logic.commands.CommandTestUtil.SEMESTERS_DESC_AMY;
import static pwe.planner.logic.commands.CommandTestUtil.SEMESTERS_DESC_BOB;
import static pwe.planner.logic.commands.CommandTestUtil.TAG_DESC_FRIEND;
import static pwe.planner.logic.commands.CommandTestUtil.TAG_DESC_HUSBAND;
import static pwe.planner.logic.commands.CommandTestUtil.VALID_CREDITS_BOB;
import static pwe.planner.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static pwe.planner.logic.commands.CommandTestUtil.VALID_SEMESTER_AMY_TWO;
import static pwe.planner.logic.commands.CommandTestUtil.VALID_SEMESTER_BOB_FOUR;
import static pwe.planner.logic.commands.CommandTestUtil.VALID_SEMESTER_BOB_THREE;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_TAG;
import static pwe.planner.testutil.TypicalModules.ALICE;
import static pwe.planner.testutil.TypicalModules.AMY;
import static pwe.planner.testutil.TypicalModules.BOB;
import static pwe.planner.testutil.TypicalModules.CARL;
import static pwe.planner.testutil.TypicalModules.HOON;
import static pwe.planner.testutil.TypicalModules.IDA;
import static pwe.planner.testutil.TypicalModules.KEYWORD_MATCHING_MEIER;

import org.junit.Test;

import pwe.planner.commons.core.index.Index;
import pwe.planner.logic.commands.AddCommand;
import pwe.planner.logic.commands.RedoCommand;
import pwe.planner.logic.commands.UndoCommand;
import pwe.planner.model.Model;
import pwe.planner.model.module.Code;
import pwe.planner.model.module.Credits;
import pwe.planner.model.module.Module;
import pwe.planner.model.module.Name;
import pwe.planner.model.planner.Semester;
import pwe.planner.model.tag.Tag;
import pwe.planner.testutil.ModuleBuilder;
import pwe.planner.testutil.ModuleUtil;

public class AddCommandSystemTest extends ApplicationSystemTest {

    @Test
    public void add() {
        Model model = getModel();

        /* ------------------------ Perform add operations on the shown unfiltered list ----------------------------- */

        /* Case: add a module without tags to a non-empty application, command with leading spaces and trailing spaces
         * -> added
         */
        Module toAdd = AMY;
        String command = "   " + AddCommand.COMMAND_WORD + "  " + CODE_DESC_AMY + "   " + NAME_DESC_AMY + "  "
                + CREDITS_DESC_AMY + "  " + SEMESTERS_DESC_AMY + "   " + TAG_DESC_FRIEND + " ";
        assertCommandSuccess(command, toAdd);

        /* Case: undo adding Amy to the list -> Amy deleted */
        command = UndoCommand.COMMAND_WORD;
        String expectedResultMessage = UndoCommand.MESSAGE_SUCCESS;
        assertCommandSuccess(command, model, expectedResultMessage);

        /* Case: redo adding Amy to the list -> Amy added again */
        command = RedoCommand.COMMAND_WORD;
        model.addModule(toAdd);
        expectedResultMessage = RedoCommand.MESSAGE_SUCCESS;
        assertCommandSuccess(command, model, expectedResultMessage);

        /* Case: add a module with all fields same as another module in the application except name -> rejected */
        toAdd = new ModuleBuilder(AMY).withName(VALID_NAME_BOB).build();
        command = ModuleUtil.getAddCommand(toAdd);
        expectedResultMessage = String.format(AddCommand.MESSAGE_DUPLICATE_MODULE, toAdd.getCode());
        assertCommandFailure(command, expectedResultMessage);

        /* Case: add a module with all fields same as another module in the application except credits -> rejected */
        toAdd = new ModuleBuilder(AMY).withCredits(VALID_CREDITS_BOB).build();
        command = ModuleUtil.getAddCommand(toAdd);
        expectedResultMessage = String.format(AddCommand.MESSAGE_DUPLICATE_MODULE, toAdd.getCode());
        assertCommandFailure(command, expectedResultMessage);

        /* Case: add a module with all fields same as another module in the application except 1/2 semesters
         * -> rejected
         */
        toAdd = new ModuleBuilder(AMY).withSemesters(VALID_SEMESTER_AMY_TWO, VALID_SEMESTER_BOB_THREE).build();
        command = ModuleUtil.getAddCommand(toAdd);
        expectedResultMessage = String.format(AddCommand.MESSAGE_DUPLICATE_MODULE, toAdd.getCode());
        assertCommandFailure(command, expectedResultMessage);

        /* Case: add a module with all fields same as another module in the application except all semesters
         * -> rejected
         */
        toAdd = new ModuleBuilder(AMY).withSemesters(VALID_SEMESTER_BOB_THREE, VALID_SEMESTER_BOB_FOUR).build();
        command = ModuleUtil.getAddCommand(toAdd);
        expectedResultMessage = String.format(AddCommand.MESSAGE_DUPLICATE_MODULE, toAdd.getCode());
        assertCommandFailure(command, expectedResultMessage);

        /* Case: add to empty application -> added */
        deleteAllModules();
        assertCommandSuccess(ALICE);

        /* Case: add a module with tags, command with parameters in random order -> added */
        toAdd = BOB;
        command = AddCommand.COMMAND_WORD + TAG_DESC_FRIEND + CREDITS_DESC_BOB + CODE_DESC_BOB + SEMESTERS_DESC_BOB
                + NAME_DESC_BOB + TAG_DESC_HUSBAND;
        assertCommandSuccess(command, toAdd);

        /* Case: add a module, missing tags -> added */
        assertCommandSuccess(HOON);

        /* -------------------------- Perform add operation on the shown filtered list ------------------------------ */

        /* Case: filters the module list before adding -> added */
        showModulesWithName(KEYWORD_MATCHING_MEIER);
        assertCommandSuccess(IDA);

        /* ------------------------ Perform add operation while a module card is selected --------------------------- */

        /* Case: selects first card in the module list, add a module -> added, card selection remains unchanged */
        selectModule(Index.fromOneBased(1));
        assertCommandSuccess(CARL);

        /* ----------------------------------- Perform invalid add operations --------------------------------------- */

        /* Case: add a duplicate module -> rejected */
        command = ModuleUtil.getAddCommand(HOON);
        expectedResultMessage = String.format(AddCommand.MESSAGE_DUPLICATE_MODULE, HOON.getCode());
        assertCommandFailure(command, expectedResultMessage);

        /* Case: add a duplicate module except with different tags -> rejected */
        command = ModuleUtil.getAddCommand(HOON) + " " + PREFIX_TAG.getPrefix() + "friends";
        expectedResultMessage = String.format(AddCommand.MESSAGE_DUPLICATE_MODULE, HOON.getCode());
        assertCommandFailure(command, expectedResultMessage);

        /* Case: missing name -> rejected */
        command = AddCommand.COMMAND_WORD + CREDITS_DESC_AMY + CODE_DESC_AMY;
        expectedResultMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);
        assertCommandFailure(command, expectedResultMessage);

        /* Case: missing credits -> rejected */
        command = AddCommand.COMMAND_WORD + NAME_DESC_AMY + CODE_DESC_AMY;
        assertCommandFailure(command, expectedResultMessage);

        /* Case: missing code -> rejected */
        command = AddCommand.COMMAND_WORD + NAME_DESC_AMY + CREDITS_DESC_AMY;
        assertCommandFailure(command, expectedResultMessage);

        /* Case: invalid keyword -> rejected */
        command = "adds " + ModuleUtil.getModuleDetails(toAdd);
        assertCommandFailure(command, MESSAGE_UNKNOWN_COMMAND);

        /* Case: invalid code -> rejected */
        command = AddCommand.COMMAND_WORD + NAME_DESC_AMY + CREDITS_DESC_AMY + INVALID_CODE_DESC + SEMESTERS_DESC_AMY;
        assertCommandFailure(command, Code.MESSAGE_CONSTRAINTS);

        /* Case: invalid name -> rejected */
        command = AddCommand.COMMAND_WORD + INVALID_NAME_DESC + CREDITS_DESC_AMY + CODE_DESC_AMY + SEMESTERS_DESC_AMY;
        assertCommandFailure(command, Name.MESSAGE_CONSTRAINTS);

        /* Case: invalid credits -> rejected */
        command = AddCommand.COMMAND_WORD + NAME_DESC_AMY + INVALID_CREDITS_DESC + CODE_DESC_AMY + SEMESTERS_DESC_AMY;
        assertCommandFailure(command, Credits.MESSAGE_CONSTRAINTS);

        /* Case: invalid semester -> rejected */
        command = AddCommand.COMMAND_WORD + NAME_DESC_AMY + CREDITS_DESC_AMY + CODE_DESC_AMY
                + INVALID_SEMESTER_DESC_FIVE;
        assertCommandFailure(command, Semester.MESSAGE_SEMESTER_CONSTRAINTS);

        /* Case: invalid corequisite code -> rejected */
        command = AddCommand.COMMAND_WORD + NAME_DESC_AMY + CREDITS_DESC_AMY + CODE_DESC_AMY + SEMESTERS_DESC_AMY
                + INVALID_COREQUISITE_DESC;
        assertCommandFailure(command, Code.MESSAGE_CONSTRAINTS);

        /* Case: invalid tag -> rejected */
        command = AddCommand.COMMAND_WORD + NAME_DESC_AMY + CREDITS_DESC_AMY + CODE_DESC_AMY + SEMESTERS_DESC_AMY
                + INVALID_TAG_DESC;
        assertCommandFailure(command, Tag.MESSAGE_CONSTRAINTS);
    }

    /**
     * Executes the {@code AddCommand} that adds {@code toAdd} to the model and asserts that the,<br>
     * 1. Command box displays an empty string.<br>
     * 2. Command box has the default style class.<br>
     * 3. Result display box displays the success message of executing {@code AddCommand} with the details of
     * {@code toAdd}.<br>
     * 4. {@code Storage} and {@code ModuleListPanel} equal to the corresponding components in
     * the current model added with {@code toAdd}.<br>
     * 5. Browser url and selected card remain unchanged.<br>
     * 6. Status bar's sync status changes.<br>
     * Verifications 1, 3 and 4 are performed by
     * {@code ApplicationSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.<br>
     * @see ApplicationSystemTest#assertApplicationDisplaysExpected(String, String, Model)
     */
    private void assertCommandSuccess(Module toAdd) {
        assertCommandSuccess(ModuleUtil.getAddCommand(toAdd), toAdd);
    }

    /**
     * Performs the same verification as {@code assertCommandSuccess(Module)}. Executes {@code command}
     * instead.
     * @see AddCommandSystemTest#assertCommandSuccess(Module)
     */
    private void assertCommandSuccess(String command, Module toAdd) {
        Model expectedModel = getModel();
        expectedModel.addModule(toAdd);
        String expectedResultMessage = String.format(AddCommand.MESSAGE_SUCCESS, toAdd);

        assertCommandSuccess(command, expectedModel, expectedResultMessage);
    }

    /**
     * Performs the same verification as {@code assertCommandSuccess(String, Module)} except asserts that
     * the,<br>
     * 1. Result display box displays {@code expectedResultMessage}.<br>
     * 2. {@code Storage} and {@code ModuleListPanel} equal to the corresponding components in
     * {@code expectedModel}.<br>
     * @see AddCommandSystemTest#assertCommandSuccess(String, Module)
     */
    private void assertCommandSuccess(String command, Model expectedModel, String expectedResultMessage) {
        executeCommand(command);
        assertApplicationDisplaysExpected("", expectedResultMessage, expectedModel);
        assertSelectedCardUnchanged();
        assertCommandBoxShowsDefaultStyle();
        assertStatusBarUnchangedExceptSyncStatus();
    }

    /**
     * Executes {@code command} and asserts that the,<br>
     * 1. Command box displays {@code command}.<br>
     * 2. Command box has the error style class.<br>
     * 3. Result display box displays {@code expectedResultMessage}.<br>
     * 4. {@code Storage} and {@code ModuleListPanel} remain unchanged.<br>
     * 5. Browser url, selected card and status bar remain unchanged.<br>
     * Verifications 1, 3 and 4 are performed by
     * {@code ApplicationSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.<br>
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
