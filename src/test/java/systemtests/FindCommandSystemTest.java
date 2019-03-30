package systemtests;

import static org.junit.Assert.assertFalse;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.commons.core.Messages.MESSAGE_MODULES_LISTED_OVERVIEW;
import static seedu.address.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.parser.CliSyntax.OPERATOR_AND;
import static seedu.address.logic.parser.CliSyntax.OPERATOR_LEFT_BRACKET;
import static seedu.address.logic.parser.CliSyntax.OPERATOR_OR;
import static seedu.address.logic.parser.CliSyntax.OPERATOR_RIGHT_BRACKET;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CREDITS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.testutil.TypicalModules.BENSON;
import static seedu.address.testutil.TypicalModules.CARL;
import static seedu.address.testutil.TypicalModules.DANIEL;
import static seedu.address.testutil.TypicalModules.KEYWORD_MATCHING_MEIER;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.RedoCommand;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.model.Model;
import seedu.address.model.tag.Tag;

public class FindCommandSystemTest extends AddressBookSystemTest {

    @Test
    public void find() {
        /* Case: find multiple modules in address book, command with leading spaces and trailing spaces
         * -> 2 modules found
         */
        String command = "   " + FindCommand.COMMAND_WORD + " " + KEYWORD_MATCHING_MEIER + "   ";
        Model expectedModel = getModel();
        ModelHelper.setFilteredList(expectedModel, BENSON, DANIEL); // first names of Benson and Daniel are "Meier"
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: repeat previous find command where module list is displaying the modules we are finding
         * -> 2 modules found
         */
        command = FindCommand.COMMAND_WORD + " " + KEYWORD_MATCHING_MEIER;
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: find module where module list is not displaying the module we are finding -> 1 module found */
        command = FindCommand.COMMAND_WORD + " " + PREFIX_NAME + "Carl";
        ModelHelper.setFilteredList(expectedModel, CARL);
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: find multiple modules in address book, 2 keywords -> 2 modules found */
        command = FindCommand.COMMAND_WORD + " " + PREFIX_NAME + "Benson " + OPERATOR_OR + " "
                + PREFIX_NAME + "Daniel";
        ModelHelper.setFilteredList(expectedModel, BENSON, DANIEL);
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: find multiple modules in address book, 2 keywords in reversed order -> 2 modules found */
        command = FindCommand.COMMAND_WORD + " " + PREFIX_NAME + "Daniel " + OPERATOR_OR + " "
                + PREFIX_NAME + "Benson";
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: find multiple modules in address book, 2 keywords with 1 repeat -> 2 modules found */
        command = FindCommand.COMMAND_WORD + " " + PREFIX_NAME + "Daniel " + OPERATOR_OR + " "
                + PREFIX_NAME + "Benson " + OPERATOR_OR + " " + PREFIX_NAME + "Daniel";
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: find multiple modules in address book, 2 matching keywords and 1 non-matching keyword
         * -> 2 modules found
         */
        command = FindCommand.COMMAND_WORD + " " + PREFIX_NAME + "Daniel " + OPERATOR_OR + " "
                + PREFIX_NAME + "Benson " + OPERATOR_OR + " " + PREFIX_NAME + "NonMatchingKeyWord";
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: undo previous find command -> rejected */
        command = UndoCommand.COMMAND_WORD;
        String expectedResultMessage = UndoCommand.MESSAGE_FAILURE;
        assertCommandFailure(command, expectedResultMessage);

        /* Case: redo previous find command -> rejected */
        command = RedoCommand.COMMAND_WORD;
        expectedResultMessage = RedoCommand.MESSAGE_FAILURE;
        assertCommandFailure(command, expectedResultMessage);

        /* Case: find same modules in address book after deleting 1 of them -> 1 module found */
        executeCommand(DeleteCommand.COMMAND_WORD + " 1");
        assertFalse(getModel().getAddressBook().getModuleList().contains(BENSON));
        command = FindCommand.COMMAND_WORD + " " + KEYWORD_MATCHING_MEIER;
        expectedModel = getModel();
        ModelHelper.setFilteredList(expectedModel, DANIEL);
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: find module in address book, keyword is same as name but of different case -> 1 module found */
        command = FindCommand.COMMAND_WORD + " " + PREFIX_NAME + "MeIeR";
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: find module in address book, keyword is substring of name -> 0 modules found */
        command = FindCommand.COMMAND_WORD + " " + PREFIX_NAME + "Mei";
        ModelHelper.setFilteredList(expectedModel);
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: find module in address book, name is substring of keyword -> 0 modules found */
        command = FindCommand.COMMAND_WORD + " " + PREFIX_NAME + "Meiers";
        ModelHelper.setFilteredList(expectedModel);
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: find module not in address book -> 0 modules found */
        command = FindCommand.COMMAND_WORD + " " + PREFIX_NAME + "Mark";
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: find credits of module in address book -> 0 modules found */
        command = FindCommand.COMMAND_WORD + " " + PREFIX_NAME + DANIEL.getCredits().value;
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: find credits of module in address book with PREFIX_CREDITS -> 1 modules found */
        command = FindCommand.COMMAND_WORD + " " + PREFIX_CREDITS + DANIEL.getCredits().value;
        ModelHelper.setFilteredList(expectedModel, DANIEL);
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: find credits of module not in address book with PREFIX_CREDITS -> 0 modules found */
        command = FindCommand.COMMAND_WORD + " " + PREFIX_CREDITS + "963";
        ModelHelper.setFilteredList(expectedModel);
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: find module in address book, credits is substring of keyword -> 0 modules found */
        command = FindCommand.COMMAND_WORD + " " + PREFIX_CREDITS + "999";
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: find code of module in address book -> 0 modules found */
        command = FindCommand.COMMAND_WORD + " " + PREFIX_NAME + DANIEL.getCode().value;
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        // TODO: Update the test case again after proper attribute is given in TypicalModules
        /* Case: find code of module in address book with correct PREFIX -> 1 module found */
        command = FindCommand.COMMAND_WORD + " " + PREFIX_CODE + DANIEL.getCode().value;
        ModelHelper.setFilteredList(expectedModel, DANIEL);
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: find non-existent module in address book with PREFIX_CODE -> 0 modules found */
        command = FindCommand.COMMAND_WORD + " " + PREFIX_CODE + "AAA1234Z";
        ModelHelper.setFilteredList(expectedModel);
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: find module in address book, code is substring of keyword -> 0 modules found */
        command = FindCommand.COMMAND_WORD + " " + PREFIX_CODE + "FS4205"; // valid partial code derived from IFS4205
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: find tags of module in address book -> 0 modules found */
        List<Tag> tags = new ArrayList<>(DANIEL.getTags());
        command = FindCommand.COMMAND_WORD + " " + PREFIX_NAME + tags.get(0).tagName;
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: find while a module is selected -> selected card deselected */
        showAllModules();
        selectModule(Index.fromOneBased(1));
        assertFalse(getModuleListPanel().getHandleToSelectedCard().getName().equals(DANIEL.getName().fullName));
        command = FindCommand.COMMAND_WORD + " " + PREFIX_NAME + "Daniel";
        ModelHelper.setFilteredList(expectedModel, DANIEL);
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardDeselected();

        /* Case: find module in empty address book -> 0 modules found */
        deleteAllModules();
        command = FindCommand.COMMAND_WORD + " " + KEYWORD_MATCHING_MEIER;
        expectedModel = getModel();
        ModelHelper.setFilteredList(expectedModel, DANIEL);
        assertCommandSuccess(command, expectedModel);
        assertSelectedCardUnchanged();

        /* Case: mixed case command word -> rejected */
        command = "FiNd " + PREFIX_NAME + "Meier";
        assertCommandFailure(command, MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void multiFind() {
        /* Case: find module with name daniel, code 'CS1231' and credits '2'-> 3 modules return */
        String command =
                FindCommand.COMMAND_WORD + " " + PREFIX_NAME + "Daniel " + OPERATOR_OR + " " + PREFIX_CODE + "CS1231 "
                        + OPERATOR_OR + " " + PREFIX_CREDITS + "2";
        Model expectedModel = getModel();
        ModelHelper.setFilteredList(expectedModel, DANIEL, BENSON, CARL);
        assertCommandSuccess(command, expectedModel);

        /* Case: find module with name, code and credits in different order -> 3 modules return */
        command = FindCommand.COMMAND_WORD + " " + PREFIX_CODE + "CS1231 " + OPERATOR_OR + " "
                + PREFIX_CREDITS + "2 " + OPERATOR_OR + " " + PREFIX_NAME + "Daniel ";
        assertCommandSuccess(command, expectedModel);

        /* Case: find module with name daniel, credits '95352563' and invalid code -> 2 modules return */
        command = FindCommand.COMMAND_WORD + " " + PREFIX_NAME + "Daniel " + OPERATOR_OR + " "
                + PREFIX_CODE + "AZ0000 " + OPERATOR_OR + " " + PREFIX_CREDITS + "2";
        expectedModel = getModel();
        ModelHelper.setFilteredList(expectedModel, DANIEL, CARL);
        assertCommandSuccess(command, expectedModel);

        /* Case: find module with valid name, code and non-existent credits -> 2 modules return */
        command = FindCommand.COMMAND_WORD + " " + PREFIX_NAME + "Daniel " + OPERATOR_OR + " "
                + PREFIX_CODE + "CS1231 " + OPERATOR_OR + " " + PREFIX_CREDITS + "968";
        expectedModel = getModel();
        ModelHelper.setFilteredList(expectedModel, DANIEL, BENSON);
        assertCommandSuccess(command, expectedModel);

        /* Case: find module with valid name but non-existent code and credits -> 1 modules return */
        command = FindCommand.COMMAND_WORD + " " + PREFIX_NAME + "Daniel " + OPERATOR_OR + " "
                + PREFIX_CODE + "FAS1234 " + OPERATOR_OR + " " + PREFIX_CREDITS + "999";
        expectedModel = getModel();
        ModelHelper.setFilteredList(expectedModel, DANIEL);
        assertCommandSuccess(command, expectedModel);

        /* Case: find module with non-existent name, code and credits -> 0 modules return */
        command = FindCommand.COMMAND_WORD + " " + PREFIX_NAME + "Programmmmming " + OPERATOR_OR + " "
                + PREFIX_CODE + "FAS1234 " + OPERATOR_OR + " " + PREFIX_CREDITS + "999";
        expectedModel = getModel();
        ModelHelper.setFilteredList(expectedModel);
        assertCommandSuccess(command, expectedModel);
    }

    @Test
    public void multiBooleanAndFind() {

        /* Case: Find module name which contain both Daniel and Meier -> Return exactly 1 module
          e.g. find ( name/Daniel && name/Meier )
        */
        String command = FindCommand.COMMAND_WORD + " ( " + PREFIX_NAME + "Daniel " + OPERATOR_AND + " "
                + PREFIX_NAME + "Meier )";
        Model expectedModel = getModel();
        ModelHelper.setFilteredList(expectedModel, DANIEL);
        assertCommandSuccess(command, expectedModel);

        /* Case: Find module name which contain both Daniel and Meier -> Return exactly 1 module
          e.g. find name/Daniel && name/Meier
        */
        command = FindCommand.COMMAND_WORD + " " + PREFIX_NAME + "Daniel " + OPERATOR_AND + " "
                + PREFIX_NAME + "Meier";
        expectedModel = getModel();
        assertCommandSuccess(command, expectedModel);

        /* Case: Find module code which contain CS1231 and CS2100 -> Return exactly 0 module */
        command = FindCommand.COMMAND_WORD + " " + PREFIX_CODE + "CS2100 " + OPERATOR_AND + " " + PREFIX_CODE
                + "CS1231";
        expectedModel = getModel();
        ModelHelper.setFilteredList(expectedModel);
        assertCommandSuccess(command, expectedModel);

        /* Case: Find module credits that are 4 and 0 -> Return exactly 0 module */
        command = FindCommand.COMMAND_WORD + " " + PREFIX_CREDITS + "4 " + OPERATOR_AND + " " + PREFIX_CREDITS
                + "0";
        expectedModel = getModel();
        ModelHelper.setFilteredList(expectedModel);
        assertCommandSuccess(command, expectedModel);
    }

    @Test
    public void complexMultiFind() {
        /*  find code/CS2102 || ( name/Daniel && name/Meier )  -> Return 2 modules */
        String command =
                FindCommand.COMMAND_WORD + " " + PREFIX_CODE + "CS2040C" + " " + OPERATOR_OR + " "
                        + OPERATOR_LEFT_BRACKET + PREFIX_NAME + "Daniel " + OPERATOR_AND + " "
                        + PREFIX_NAME + "Meier " + OPERATOR_RIGHT_BRACKET;
        Model expectedModel = getModel();
        ModelHelper.setFilteredList(expectedModel, DANIEL, CARL);
        assertCommandSuccess(command, expectedModel);
    }

    @Test
    public void negativeTest() {
        // invalid operator
        String command = FindCommand.COMMAND_WORD + " name/Programming !! code/CS1231";
        assertCommandFailure(command, String.format(FindCommand.MESSAGE_INVALID_EXPRESSION, FindCommand.MESSAGE_USAGE));
        // invalid operator
        command = FindCommand.COMMAND_WORD + " name/Programming ## code/CS1231";
        assertCommandFailure(command, String.format(FindCommand.MESSAGE_INVALID_EXPRESSION, FindCommand.MESSAGE_USAGE));
        // valid + invalid prefix
        command = FindCommand.COMMAND_WORD + " name/Programming " + OPERATOR_OR + " nonExisting/CS1231";
        assertCommandFailure(command, String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        // single invalid prefix
        command = FindCommand.COMMAND_WORD + " nonExisting/CS1231";
        assertCommandFailure(command, String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        // single invalid prefix with multiple white space
        command = FindCommand.COMMAND_WORD + "                          nonExisting/CS1231                ";
        assertCommandFailure(command, String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));


    }

    /**
     * Executes {@code command} and verifies that the command box displays an empty string, the result display
     * box displays {@code Messages#MESSAGE_MODULES_LISTED_OVERVIEW} with the number of modules in the filtered list,
     * and the model related components equal to {@code expectedModel}.
     * These verifications are done by
     * {@code AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.<br>
     * Also verifies that the status bar remains unchanged, and the command box has the default style class, and the
     * selected card updated accordingly, depending on {@code cardStatus}.
     * @see AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)
     */
    private void assertCommandSuccess(String command, Model expectedModel) {
        String expectedResultMessage = String.format(
                MESSAGE_MODULES_LISTED_OVERVIEW, expectedModel.getFilteredModuleList().size());

        executeCommand(command);
        assertApplicationDisplaysExpected("", expectedResultMessage, expectedModel);
        assertCommandBoxShowsDefaultStyle();
        assertStatusBarUnchanged();
    }

    /**
     * Executes {@code command} and verifies that the command box displays {@code command}, the result display
     * box displays {@code expectedResultMessage} and the model related components equal to the current model.
     * These verifications are done by
     * {@code AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.<br>
     * Also verifies that the browser url, selected card and status bar remain unchanged, and the command box has the
     * error style.
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
