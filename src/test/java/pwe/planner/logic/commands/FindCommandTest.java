package pwe.planner.logic.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static pwe.planner.commons.core.Messages.MESSAGE_MODULES_LISTED_OVERVIEW;
import static pwe.planner.logic.commands.CommandTestUtil.assertCommandSuccess;
import static pwe.planner.testutil.TypicalDegreePlanners.getTypicalDegreePlannerList;
import static pwe.planner.testutil.TypicalModules.CARL;
import static pwe.planner.testutil.TypicalModules.ELLE;
import static pwe.planner.testutil.TypicalModules.FIONA;
import static pwe.planner.testutil.TypicalModules.getTypicalModuleList;
import static pwe.planner.testutil.TypicalRequirementCategories.getTypicalRequirementCategoriesList;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

import pwe.planner.commons.exceptions.IllegalValueException;
import pwe.planner.logic.CommandHistory;
import pwe.planner.model.Model;
import pwe.planner.model.ModelManager;
import pwe.planner.model.UserPrefs;
import pwe.planner.model.module.CodeContainsKeywordsPredicate;
import pwe.planner.model.module.CreditsContainsKeywordsPredicate;
import pwe.planner.model.module.Module;
import pwe.planner.model.module.NameContainsKeywordsPredicate;
import pwe.planner.storage.JsonSerializableApplication;

/**
 * Contains integration tests (interaction with the Model) for {@code FindCommand}.
 */
public class FindCommandTest {
    //ToDo: Implement getTypicalDegreePlannerList for DegreePlannerList and update the codes below
    private Model model = new ModelManager(
            new JsonSerializableApplication(getTypicalModuleList(), getTypicalDegreePlannerList(),
                    getTypicalRequirementCategoriesList()).toModelType(), new UserPrefs());
    private Model expectedModel = new ModelManager(
            new JsonSerializableApplication(getTypicalModuleList(), getTypicalDegreePlannerList(),
                    getTypicalRequirementCategoriesList()).toModelType(), new UserPrefs());
    private CommandHistory commandHistory = new CommandHistory();

    public FindCommandTest() throws IllegalValueException {}

    @Test
    public void equals() {
        NameContainsKeywordsPredicate<Module> firstPredicate =
                new NameContainsKeywordsPredicate<>("first");
        NameContainsKeywordsPredicate<Module> secondPredicate =
                new NameContainsKeywordsPredicate<>("second");

        FindCommand findFirstCommand = new FindCommand(firstPredicate);
        FindCommand findSecondCommand = new FindCommand(secondPredicate);

        // same object -> returns true
        assertTrue(findFirstCommand.equals(findFirstCommand));

        // same values -> returns true
        FindCommand findFirstCommandCopy = new FindCommand(firstPredicate);
        assertTrue(findFirstCommand.equals(findFirstCommandCopy));

        // different types -> returns false
        assertFalse(findFirstCommand.equals(1));

        // null -> returns false
        assertFalse(findFirstCommand.equals(null));

        // different module -> returns false
        assertFalse(findFirstCommand.equals(findSecondCommand));
    }

    @Test
    public void execute_oneKeywords_noModuleFound() {
        String expectedMessage = String.format(MESSAGE_MODULES_LISTED_OVERVIEW, 0);
        NameContainsKeywordsPredicate<Module> predicate = prepareNamePredicate("doNotExist");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredModuleList(predicate);
        assertCommandSuccess(command, model, commandHistory, expectedMessage, expectedModel);
        assertEquals(Collections.emptyList(), model.getFilteredModuleList());
    }

    @Test
    public void execute_nameKeyword_foundModule() {
        String expectedMessage = String.format(MESSAGE_MODULES_LISTED_OVERVIEW, 1);
        NameContainsKeywordsPredicate<Module> predicate = prepareNamePredicate("Kurz ");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredModuleList(predicate);
        assertCommandSuccess(command, model, commandHistory, expectedMessage, expectedModel);
        assertEquals(List.of(CARL), model.getFilteredModuleList());
    }

    @Test
    public void execute_codeKeyword_foundModule() {
        String expectedMessage = String.format(MESSAGE_MODULES_LISTED_OVERVIEW, 1);
        // TODO: update the module code after TypicalModule attribute are updated
        CodeContainsKeywordsPredicate<Module> predicate = prepareCodePredicate(" CS2101 ");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredModuleList(predicate);
        assertCommandSuccess(command, model, commandHistory, expectedMessage, expectedModel);
        assertEquals(List.of(ELLE), model.getFilteredModuleList());
    }

    @Test
    public void execute_creditKeyword_foundModule() {
        String expectedMessage = String.format(MESSAGE_MODULES_LISTED_OVERVIEW, 1);
        // TODO: update the module credits after TypicalModule attribute are updated
        CreditsContainsKeywordsPredicate<Module> predicate = prepareCreditsPredicate("5");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredModuleList(predicate);
        assertCommandSuccess(command, model, commandHistory, expectedMessage, expectedModel);
        assertEquals(List.of(FIONA), model.getFilteredModuleList());
    }

    /**
     * Parses {@code userInput} into a {@code NameContainsKeywordsPredicate}.
     */
    private NameContainsKeywordsPredicate<Module> prepareNamePredicate(String userInput) {
        return new NameContainsKeywordsPredicate<>(userInput);
    }

    /**
     * Parses {@code userInput} into a {@code CodeContainsKeywordsPredicate}.
     */
    private CodeContainsKeywordsPredicate<Module> prepareCodePredicate(String userInput) {
        return new CodeContainsKeywordsPredicate<>(userInput);
    }

    /**
     * Parses {@code userInput} into a {@code CreditsContainsKeywordsPredicate}.
     */
    private CreditsContainsKeywordsPredicate<Module> prepareCreditsPredicate(String userInput) {
        return new CreditsContainsKeywordsPredicate<>(userInput);
    }

}
