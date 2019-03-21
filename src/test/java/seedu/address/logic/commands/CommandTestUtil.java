package seedu.address.logic.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CREDITS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.module.Module;
import seedu.address.model.module.NameContainsKeywordsPredicate;
import seedu.address.testutil.EditModuleDescriptorBuilder;

/**
 * Contains helper methods for testing commands.
 */
public class CommandTestUtil {

    public static final String VALID_NAME_AMY = "Amy Bee";
    public static final String VALID_NAME_BOB = "Bob Choo";
    public static final String VALID_CREDITS_AMY = "0";
    public static final String VALID_CREDITS_BOB = "999";
    public static final String VALID_CODE_AMY = "AAA0000A";
    public static final String VALID_CODE_BOB = "BBB1111B";
    public static final String VALID_TAG_HUSBAND = "husband";
    public static final String VALID_TAG_FRIEND = "friend";

    public static final String NAME_DESC_AMY = " " + PREFIX_NAME + VALID_NAME_AMY;
    public static final String NAME_DESC_BOB = " " + PREFIX_NAME + VALID_NAME_BOB;
    public static final String CREDITS_DESC_AMY = " " + PREFIX_CREDITS + VALID_CREDITS_AMY;
    public static final String CREDITS_DESC_BOB = " " + PREFIX_CREDITS + VALID_CREDITS_BOB;
    public static final String CODE_DESC_AMY = " " + PREFIX_CODE + VALID_CODE_AMY;
    public static final String CODE_DESC_BOB = " " + PREFIX_CODE + VALID_CODE_BOB;
    public static final String TAG_DESC_FRIEND = " " + PREFIX_TAG + VALID_TAG_FRIEND;
    public static final String TAG_DESC_HUSBAND = " " + PREFIX_TAG + VALID_TAG_HUSBAND;

    public static final String INVALID_NAME_DESC = " " + PREFIX_NAME + "Jame§"; // '§' not allowed in names
    public static final String INVALID_CREDITS_DESC = " " + PREFIX_CREDITS + "1a"; // 'a' not allowed in credits
    public static final String INVALID_CODE_DESC = " " + PREFIX_CODE; // empty string not allowed for codes
    public static final String INVALID_TAG_DESC = " " + PREFIX_TAG + "hubby*"; // '*' not allowed in tags

    public static final String PREAMBLE_WHITESPACE = "\t  \r  \n";
    public static final String PREAMBLE_NON_EMPTY = "NonEmptyPreamble";

    public static final EditCommand.EditModuleDescriptor DESC_AMY;
    public static final EditCommand.EditModuleDescriptor DESC_BOB;

    static {
        DESC_AMY = new EditModuleDescriptorBuilder().withName(VALID_NAME_AMY)
                .withCredits(VALID_CREDITS_AMY).withCode(VALID_CODE_AMY)
                .withTags(VALID_TAG_FRIEND).build();
        DESC_BOB = new EditModuleDescriptorBuilder().withName(VALID_NAME_BOB)
                .withCredits(VALID_CREDITS_BOB).withCode(VALID_CODE_BOB)
                .withTags(VALID_TAG_HUSBAND, VALID_TAG_FRIEND).build();
    }

    /**
     * Executes the given {@code command}, confirms that <br>
     * - the returned {@link CommandResult} matches {@code expectedCommandResult} <br>
     * - the {@code actualModel} matches {@code expectedModel} <br>
     * - the {@code actualCommandHistory} remains unchanged.
     */
    public static void assertCommandSuccess(Command command, Model actualModel, CommandHistory actualCommandHistory,
            CommandResult expectedCommandResult, Model expectedModel) {
        CommandHistory expectedCommandHistory = new CommandHistory(actualCommandHistory);
        try {
            CommandResult result = command.execute(actualModel, actualCommandHistory);
            assertEquals(expectedCommandResult, result);
            assertEquals(expectedModel, actualModel);
            assertEquals(expectedCommandHistory, actualCommandHistory);
        } catch (CommandException ce) {
            throw new AssertionError("Execution of command should not fail.", ce);
        }
    }

    /**
     * Convenience wrapper to {@link #assertCommandSuccess(Command, Model, CommandHistory, CommandResult, Model)}
     * that takes a string {@code expectedMessage}.
     */
    public static void assertCommandSuccess(Command command, Model actualModel, CommandHistory actualCommandHistory,
            String expectedMessage, Model expectedModel) {
        CommandResult expectedCommandResult = new CommandResult(expectedMessage);
        assertCommandSuccess(command, actualModel, actualCommandHistory, expectedCommandResult, expectedModel);
    }

    /**
     * Executes the given {@code command}, confirms that <br>
     * - a {@code CommandException} is thrown <br>
     * - the CommandException message matches {@code expectedMessage} <br>
     * - the address book, filtered module list and selected module in {@code actualModel} remain unchanged <br>
     * - {@code actualCommandHistory} remains unchanged.
     */
    public static void assertCommandFailure(Command command, Model actualModel, CommandHistory actualCommandHistory,
            String expectedMessage) {
        // we are unable to defensively copy the model for comparison later, so we can
        // only do so by copying its components.
        AddressBook expectedAddressBook = new AddressBook(actualModel.getAddressBook());
        List<Module> expectedFilteredList = new ArrayList<>(actualModel.getFilteredModuleList());
        Module expectedSelectedModule = actualModel.getSelectedModule();

        CommandHistory expectedCommandHistory = new CommandHistory(actualCommandHistory);

        try {
            command.execute(actualModel, actualCommandHistory);
            throw new AssertionError("The expected CommandException was not thrown.");
        } catch (CommandException e) {
            assertEquals(expectedMessage, e.getMessage());
            assertEquals(expectedAddressBook, actualModel.getAddressBook());
            assertEquals(expectedFilteredList, actualModel.getFilteredModuleList());
            assertEquals(expectedSelectedModule, actualModel.getSelectedModule());
            assertEquals(expectedCommandHistory, actualCommandHistory);
        }
    }

    /**
     * Updates {@code model}'s filtered list to show only the module at the given {@code targetIndex} in the
     * {@code model}'s address book.
     */
    public static void showModuleAtIndex(Model model, Index targetIndex) {
        assertTrue(targetIndex.getZeroBased() < model.getFilteredModuleList().size());

        Module module = model.getFilteredModuleList().get(targetIndex.getZeroBased());
        final String[] splitName = module.getName().fullName.split("\\s+");
        model.updateFilteredModuleList(new NameContainsKeywordsPredicate(Arrays.asList(splitName[0])));

        assertEquals(1, model.getFilteredModuleList().size());
    }

    /**
     * Deletes the first module in {@code model}'s filtered list from {@code model}'s address book.
     */
    public static void deleteFirstModule(Model model) {
        Module firstModule = model.getFilteredModuleList().get(0);
        model.deleteModule(firstModule);
        model.commitAddressBook();
    }

}
