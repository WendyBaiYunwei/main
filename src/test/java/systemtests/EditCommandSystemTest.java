package systemtests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
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
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_MODULES;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_MODULE;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_MODULE;
import static seedu.address.testutil.TypicalModules.AMY;
import static seedu.address.testutil.TypicalModules.BOB;
import static seedu.address.testutil.TypicalModules.KEYWORD_MATCHING_MEIER;

import org.junit.Test;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditCommand;
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

public class EditCommandSystemTest extends AddressBookSystemTest {

    @Test
    public void edit() {
        Model model = getModel();

        /* ----------------- Performing edit operation while an unfiltered list is being shown ---------------------- */

        /* Case: edit all fields, command with leading spaces, trailing spaces and multiple spaces between each field
         * -> edited
         */
        Index index = INDEX_FIRST_MODULE;
        String command = " " + EditCommand.COMMAND_WORD + "  " + index.getOneBased() + "  " + NAME_DESC_BOB + "  "
                + CREDITS_DESC_BOB + " " + CODE_DESC_BOB + " " + TAG_DESC_HUSBAND + " ";
        Module editedModule = new ModuleBuilder(BOB).withTags(VALID_TAG_HUSBAND).build();
        assertCommandSuccess(command, index, editedModule);

        /* Case: undo editing the last module in the list -> last module restored */
        command = UndoCommand.COMMAND_WORD;
        String expectedResultMessage = UndoCommand.MESSAGE_SUCCESS;
        assertCommandSuccess(command, model, expectedResultMessage);

        /* Case: redo editing the last module in the list -> last module edited again */
        command = RedoCommand.COMMAND_WORD;
        expectedResultMessage = RedoCommand.MESSAGE_SUCCESS;
        model.setModule(getModel().getFilteredModuleList().get(INDEX_FIRST_MODULE.getZeroBased()), editedModule);
        assertCommandSuccess(command, model, expectedResultMessage);

        /* Case: edit a module with new values same as existing values -> edited */
        command = EditCommand.COMMAND_WORD + " " + index.getOneBased() + NAME_DESC_BOB + CREDITS_DESC_BOB
                + CODE_DESC_BOB + TAG_DESC_FRIEND + TAG_DESC_HUSBAND;
        assertCommandSuccess(command, index, BOB);

        /* Case: edit a module with new values same as another module's values but with different name -> rejected */
        assertTrue(getModel().getAddressBook().getModuleList().contains(BOB));
        index = INDEX_SECOND_MODULE;
        assertNotEquals(getModel().getFilteredModuleList().get(index.getZeroBased()), BOB);
        command = EditCommand.COMMAND_WORD + " " + index.getOneBased() + NAME_DESC_AMY + CREDITS_DESC_BOB
                + CODE_DESC_BOB + TAG_DESC_FRIEND + TAG_DESC_HUSBAND;
        assertCommandFailure(command, EditCommand.MESSAGE_DUPLICATE_MODULE);

        /* Case: edit a module with new values same as another module's values but with different credits -> rejected */
        index = INDEX_SECOND_MODULE;
        command = EditCommand.COMMAND_WORD + " " + index.getOneBased() + NAME_DESC_BOB + CREDITS_DESC_AMY
                + CODE_DESC_BOB + TAG_DESC_FRIEND + TAG_DESC_HUSBAND;
        assertCommandFailure(command, EditCommand.MESSAGE_DUPLICATE_MODULE);

        /* Case: clear tags -> cleared */
        index = INDEX_FIRST_MODULE;
        command = EditCommand.COMMAND_WORD + " " + index.getOneBased() + " " + PREFIX_TAG.getPrefix();
        Module moduleToEdit = getModel().getFilteredModuleList().get(index.getZeroBased());
        editedModule = new ModuleBuilder(moduleToEdit).withTags().build();
        assertCommandSuccess(command, index, editedModule);

        /* ------------------ Performing edit operation while a filtered list is being shown ------------------------ */

        /* Case: filtered module list, edit index within bounds of address book and module list -> edited */
        showModulesWithName(KEYWORD_MATCHING_MEIER);
        index = INDEX_FIRST_MODULE;
        assertTrue(index.getZeroBased() < getModel().getFilteredModuleList().size());
        command = EditCommand.COMMAND_WORD + " " + index.getOneBased() + " " + NAME_DESC_BOB;
        moduleToEdit = getModel().getFilteredModuleList().get(index.getZeroBased());
        editedModule = new ModuleBuilder(moduleToEdit).withName(VALID_NAME_BOB).build();
        assertCommandSuccess(command, index, editedModule);

        /* Case: filtered module list, edit index within bounds of address book but out of bounds of module list
         * -> rejected
         */
        showModulesWithName(KEYWORD_MATCHING_MEIER);
        int invalidIndex = getModel().getAddressBook().getModuleList().size();
        assertCommandFailure(EditCommand.COMMAND_WORD + " " + invalidIndex + NAME_DESC_BOB,
                Messages.MESSAGE_INVALID_MODULE_DISPLAYED_INDEX);

        /* --------------------- Performing edit operation while a module card is selected -------------------------- */

        /* Case: selects first card in the module list, edit a module -> edited, card selection remains unchanged but
         * browser url changes
         */
        showAllModules();
        index = INDEX_FIRST_MODULE;
        selectModule(index);
        command = EditCommand.COMMAND_WORD + " " + index.getOneBased() + NAME_DESC_AMY + CREDITS_DESC_AMY
                + CODE_DESC_AMY + TAG_DESC_FRIEND;
        // this can be misleading: card selection actually remains unchanged but the
        // browser's url is updated to reflect the new module's name
        assertCommandSuccess(command, index, AMY, index);

        /* --------------------------------- Performing invalid edit operation -------------------------------------- */

        /* Case: invalid index (0) -> rejected */
        assertCommandFailure(EditCommand.COMMAND_WORD + " 0" + NAME_DESC_BOB,
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));

        /* Case: invalid index (-1) -> rejected */
        assertCommandFailure(EditCommand.COMMAND_WORD + " -1" + NAME_DESC_BOB,
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));

        /* Case: invalid index (size + 1) -> rejected */
        invalidIndex = getModel().getFilteredModuleList().size() + 1;
        assertCommandFailure(EditCommand.COMMAND_WORD + " " + invalidIndex + NAME_DESC_BOB,
                Messages.MESSAGE_INVALID_MODULE_DISPLAYED_INDEX);

        /* Case: missing index -> rejected */
        assertCommandFailure(EditCommand.COMMAND_WORD + NAME_DESC_BOB,
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));

        /* Case: missing all fields -> rejected */
        assertCommandFailure(EditCommand.COMMAND_WORD + " " + INDEX_FIRST_MODULE.getOneBased(),
                EditCommand.MESSAGE_NOT_EDITED);

        /* Case: invalid name -> rejected */
        assertCommandFailure(EditCommand.COMMAND_WORD + " " + INDEX_FIRST_MODULE.getOneBased() + INVALID_NAME_DESC,
                Name.MESSAGE_CONSTRAINTS);

        /* Case: invalid credits -> rejected */
        assertCommandFailure(EditCommand.COMMAND_WORD + " " + INDEX_FIRST_MODULE.getOneBased() + INVALID_CREDITS_DESC,
                Credits.MESSAGE_CONSTRAINTS);

        /* Case: invalid code -> rejected */
        assertCommandFailure(EditCommand.COMMAND_WORD + " " + INDEX_FIRST_MODULE.getOneBased() + INVALID_CODE_DESC,
                Code.MESSAGE_CONSTRAINTS);

        /* Case: invalid tag -> rejected */
        assertCommandFailure(EditCommand.COMMAND_WORD + " " + INDEX_FIRST_MODULE.getOneBased() + INVALID_TAG_DESC,
                Tag.MESSAGE_CONSTRAINTS);

        /* Case: edit a module with new values same as another module's values -> rejected */
        executeCommand(ModuleUtil.getAddCommand(BOB));
        assertTrue(getModel().getAddressBook().getModuleList().contains(BOB));
        index = INDEX_FIRST_MODULE;
        assertFalse(getModel().getFilteredModuleList().get(index.getZeroBased()).equals(BOB));
        command = EditCommand.COMMAND_WORD + " " + index.getOneBased() + NAME_DESC_BOB + CREDITS_DESC_BOB
                + CODE_DESC_BOB + TAG_DESC_FRIEND + TAG_DESC_HUSBAND;
        assertCommandFailure(command, EditCommand.MESSAGE_DUPLICATE_MODULE);

        /* Case: edit a module with new values same as another module's values but with different tags -> rejected */
        command = EditCommand.COMMAND_WORD + " " + index.getOneBased() + NAME_DESC_BOB + CREDITS_DESC_BOB
                + CODE_DESC_BOB + TAG_DESC_HUSBAND;
        assertCommandFailure(command, EditCommand.MESSAGE_DUPLICATE_MODULE);
    }

    /**
     * Performs the same verification as {@code assertCommandSuccess(String, Index, Module, Index)} except that
     * the browser url and selected card remain unchanged.
     * @param toEdit the index of the current model's filtered list
     * @see EditCommandSystemTest#assertCommandSuccess(String, Index, Module, Index)
     */
    private void assertCommandSuccess(String command, Index toEdit, Module editedModule) {
        assertCommandSuccess(command, toEdit, editedModule, null);
    }

    /**
     * Performs the same verification as {@code assertCommandSuccess(String, Model, String, Index)} and in addition,<br>
     * 1. Asserts that result display box displays the success message of executing {@code EditCommand}.<br>
     * 2. Asserts that the model related components are updated to reflect the module at index {@code toEdit} being
     * updated to values specified {@code editedModule}.<br>
     * @param toEdit the index of the current model's filtered list.
     * @see EditCommandSystemTest#assertCommandSuccess(String, Model, String, Index)
     */
    private void assertCommandSuccess(String command, Index toEdit, Module editedModule,
            Index expectedSelectedCardIndex) {
        Model expectedModel = getModel();
        expectedModel.setModule(expectedModel.getFilteredModuleList().get(toEdit.getZeroBased()), editedModule);
        expectedModel.updateFilteredModuleList(PREDICATE_SHOW_ALL_MODULES);

        assertCommandSuccess(command, expectedModel,
                String.format(EditCommand.MESSAGE_EDIT_MODULE_SUCCESS, editedModule), expectedSelectedCardIndex);
    }

    /**
     * Performs the same verification as {@code assertCommandSuccess(String, Model, String, Index)} except that the
     * browser url and selected card remain unchanged.
     * @see EditCommandSystemTest#assertCommandSuccess(String, Model, String, Index)
     */
    private void assertCommandSuccess(String command, Model expectedModel, String expectedResultMessage) {
        assertCommandSuccess(command, expectedModel, expectedResultMessage, null);
    }

    /**
     * Executes {@code command} and in addition,<br>
     * 1. Asserts that the command box displays an empty string.<br>
     * 2. Asserts that the result display box displays {@code expectedResultMessage}.<br>
     * 3. Asserts that the browser url and selected card update accordingly depending on the card at
     * {@code expectedSelectedCardIndex}.<br>
     * 4. Asserts that the status bar's sync status changes.<br>
     * 5. Asserts that the command box has the default style class.<br>
     * Verifications 1 and 2 are performed by
     * {@code AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.<br>
     * @see AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)
     * @see AddressBookSystemTest#assertSelectedCardChanged(Index)
     */
    private void assertCommandSuccess(String command, Model expectedModel, String expectedResultMessage,
            Index expectedSelectedCardIndex) {
        executeCommand(command);
        expectedModel.updateFilteredModuleList(PREDICATE_SHOW_ALL_MODULES);
        assertApplicationDisplaysExpected("", expectedResultMessage, expectedModel);
        assertCommandBoxShowsDefaultStyle();
        if (expectedSelectedCardIndex != null) {
            assertSelectedCardChanged(expectedSelectedCardIndex);
        } else {
            assertSelectedCardUnchanged();
        }
        assertStatusBarUnchangedExceptSyncStatus();
    }

    /**
     * Executes {@code command} and in addition,<br>
     * 1. Asserts that the command box displays {@code command}.<br>
     * 2. Asserts that result display box displays {@code expectedResultMessage}.<br>
     * 3. Asserts that the browser url, selected card and status bar remain unchanged.<br>
     * 4. Asserts that the command box has the error style.<br>
     * Verifications 1 and 2 are performed by
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
