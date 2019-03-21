package systemtests;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.CODE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.CODE_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.CREDITS_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.CREDITS_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_CODE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_CREDITS_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_TAG_DESC;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CREDITS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.testutil.TypicalModules.ALICE;
import static seedu.address.testutil.TypicalModules.AMY;
import static seedu.address.testutil.TypicalModules.BOB;
import static seedu.address.testutil.TypicalModules.CARL;
import static seedu.address.testutil.TypicalModules.HOON;
import static seedu.address.testutil.TypicalModules.IDA;
import static seedu.address.testutil.TypicalModules.KEYWORD_MATCHING_MEIER;

import org.junit.Test;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.RedoCommand;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.model.Model;
import seedu.address.model.module.Code;
import seedu.address.model.module.Credits;
import seedu.address.model.module.Module;
import seedu.address.model.module.Name;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.ModuleBuilder;
import seedu.address.testutil.ModuleUtil;

public class AddCommandSystemTest extends AddressBookSystemTest {

    @Test
    public void add() {
        Model model = getModel();

        /* ------------------------ Perform add operations on the shown unfiltered list ----------------------------- */

        /* Case: add a module without tags to a non-empty address book, command with leading spaces and trailing spaces
         * -> added
         */
        Module toAdd = AMY;
        String command = "   " + AddCommand.COMMAND_WORD + "  " + NAME_DESC_AMY + "  " + CREDITS_DESC_AMY
                + "   " + CODE_DESC_AMY + "   " + TAG_DESC_FRIEND + " ";
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

        /* Case: add a module with all fields same as another module in the address book except name -> rejected */
        toAdd = new ModuleBuilder(AMY).withName(VALID_NAME_BOB).build();
        command = ModuleUtil.getAddCommand(toAdd);
        assertCommandFailure(command, AddCommand.MESSAGE_DUPLICATE_MODULE);

        /* Case: add a module with all fields same as another module in the address book except credits -> rejected */
        toAdd = new ModuleBuilder(AMY).withCredits(VALID_CREDITS_BOB).build();
        command = ModuleUtil.getAddCommand(toAdd);
        assertCommandFailure(command, AddCommand.MESSAGE_DUPLICATE_MODULE);

        /* Case: add to empty address book -> added */
        deleteAllModules();
        assertCommandSuccess(ALICE);

        /* Case: add a module with tags, command with parameters in random order -> added */
        toAdd = BOB;
        command = AddCommand.COMMAND_WORD + TAG_DESC_FRIEND + CREDITS_DESC_BOB + CODE_DESC_BOB + NAME_DESC_BOB
                + TAG_DESC_HUSBAND;
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
        assertCommandFailure(command, AddCommand.MESSAGE_DUPLICATE_MODULE);

        /* Case: add a duplicate module except with different tags -> rejected */
        command = ModuleUtil.getAddCommand(HOON) + " " + PREFIX_TAG.getPrefix() + "friends";
        assertCommandFailure(command, AddCommand.MESSAGE_DUPLICATE_MODULE);

        /* Case: missing name -> rejected */
        command = AddCommand.COMMAND_WORD + CREDITS_DESC_AMY + CODE_DESC_AMY;
        assertCommandFailure(command, String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));

        /* Case: missing credits -> rejected */
        command = AddCommand.COMMAND_WORD + NAME_DESC_AMY + CODE_DESC_AMY;
        assertCommandFailure(command, String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));

        /* Case: missing code -> rejected */
        command = AddCommand.COMMAND_WORD + NAME_DESC_AMY + CREDITS_DESC_AMY;
        assertCommandFailure(command, String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));

        /* Case: invalid keyword -> rejected */
        command = "adds " + ModuleUtil.getModuleDetails(toAdd);
        assertCommandFailure(command, Messages.MESSAGE_UNKNOWN_COMMAND);

        /* Case: invalid name -> rejected */
        command = AddCommand.COMMAND_WORD + INVALID_NAME_DESC + CREDITS_DESC_AMY + CODE_DESC_AMY;
        assertCommandFailure(command, Name.MESSAGE_CONSTRAINTS);

        /* Case: invalid credits -> rejected */
        command = AddCommand.COMMAND_WORD + NAME_DESC_AMY + INVALID_CREDITS_DESC + CODE_DESC_AMY;
        assertCommandFailure(command, Credits.MESSAGE_CONSTRAINTS);

        /* Case: invalid code -> rejected */
        command = AddCommand.COMMAND_WORD + NAME_DESC_AMY + CREDITS_DESC_AMY + INVALID_CODE_DESC;
        assertCommandFailure(command, Code.MESSAGE_CONSTRAINTS);

        /* Case: invalid tag -> rejected */
        command = AddCommand.COMMAND_WORD + NAME_DESC_AMY + CREDITS_DESC_AMY + CODE_DESC_AMY
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
     * {@code AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.<br>
     * @see AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)
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
     * {@code AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.<br>
     * @see AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)
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
