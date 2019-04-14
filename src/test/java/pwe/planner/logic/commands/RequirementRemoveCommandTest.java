package pwe.planner.logic.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static pwe.planner.logic.commands.CommandTestUtil.assertCommandFailure;
import static pwe.planner.logic.commands.CommandTestUtil.assertCommandSuccess;
import static pwe.planner.testutil.TypicalDegreePlanners.getTypicalDegreePlannerList;
import static pwe.planner.testutil.TypicalModules.getTypicalModuleList;
import static pwe.planner.testutil.TypicalRequirementCategories.getTypicalRequirementCategoriesList;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import pwe.planner.commons.exceptions.IllegalValueException;
import pwe.planner.commons.util.StringUtil;
import pwe.planner.logic.CommandHistory;
import pwe.planner.model.Model;
import pwe.planner.model.ModelManager;
import pwe.planner.model.UserPrefs;
import pwe.planner.model.module.Code;
import pwe.planner.model.module.Name;
import pwe.planner.model.requirement.RequirementCategory;
import pwe.planner.storage.JsonSerializableApplication;

public class RequirementRemoveCommandTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private CommandHistory commandHistory = new CommandHistory();
    private Model model;

    @Before
    public void setUp() throws IllegalValueException {
        model = new ModelManager(new JsonSerializableApplication(getTypicalModuleList(), getTypicalDegreePlannerList(),
                getTypicalRequirementCategoriesList()).toModelType(), new UserPrefs());
    }

    @Test
    public void constructor_nullInputs_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        new RequirementRemoveCommand(null);
    }

    @Test
    public void execute_nonExistentCode_throwsCommandException() {
        Set<Code> validCodeSet = Set.of(new Code("CS9999"));
        String formattedCodeString = StringUtil.joinStreamAsString(validCodeSet.stream().sorted());
        assertCommandFailure(new RequirementRemoveCommand(validCodeSet), model, commandHistory,
                String.format(RequirementRemoveCommand.MESSAGE_NONEXISTENT_CODE, formattedCodeString));

        //case insensitive checks
        Set<Code> validCodeSetCaseInsensitive = Set.of(new Code("cs9999"));
        formattedCodeString = StringUtil.joinStreamAsString(validCodeSetCaseInsensitive.stream().sorted());
        assertCommandFailure(new RequirementRemoveCommand(validCodeSet), model, commandHistory,
                String.format(RequirementRemoveCommand.MESSAGE_NONEXISTENT_CODE, formattedCodeString));
    }

    @Test
    public void execute_nonExistentCodes_throwsCommandException() {
        Set<Code> invalidCodeSet = Set.of(new Code("CS9999"));
        Set<Code> validCodeSet = Set.of(new Code("CS2100"), new Code("CS9999"));
        String formattedCodeString = StringUtil.joinStreamAsString(invalidCodeSet.stream().sorted());
        assertCommandFailure(new RequirementRemoveCommand(validCodeSet), model, commandHistory,
                String.format(RequirementRemoveCommand.MESSAGE_NONEXISTENT_CODE, formattedCodeString));
    }

    @Test
    public void execute_codeNotInAnyRequirementCategory_throwsCommandException() {
        Set<Code> validCodeSet = Set.of(new Code("CS1010"));
        String formattedCodeString = StringUtil.joinStreamAsString(validCodeSet.stream().sorted());
        assertCommandFailure(new RequirementRemoveCommand(validCodeSet), model, commandHistory,
                String.format(RequirementRemoveCommand.MESSAGE_CODE_NOT_IN_ANY_REQUIREMENT_CATEGORY,
                        formattedCodeString));

        //case insensitive checks
        Set<Code> validCodeSetCaseInsensitive = Set.of(new Code("cs1010"));
        formattedCodeString = StringUtil.joinStreamAsString(validCodeSetCaseInsensitive.stream().sorted());
        assertCommandFailure(new RequirementRemoveCommand(validCodeSetCaseInsensitive), model, commandHistory,
                String.format(RequirementRemoveCommand.MESSAGE_CODE_NOT_IN_ANY_REQUIREMENT_CATEGORY,
                        formattedCodeString));
    }

    @Test
    public void execute_codesNotInAnyRequirementCategory_throwsCommandException() {
        Set<Code> invalidCodeSet = Set.of(new Code("CS1010"));
        Set<Code> validCodeSet = Set.of(new Code("CS2100"), new Code("CS1010"));
        String formattedCodeString = StringUtil.joinStreamAsString(invalidCodeSet.stream().sorted());
        assertCommandFailure(new RequirementRemoveCommand(validCodeSet), model, commandHistory,
                String.format(RequirementRemoveCommand.MESSAGE_CODE_NOT_IN_ANY_REQUIREMENT_CATEGORY,
                        formattedCodeString));
    }

    @Test
    public void execute_removeCodeFromRequirementCategory_success() {
        Set<Code> validCodeSet = new HashSet<>();
        Name requirementCategoryName = new Name("Computing Foundation");
        RequirementCategory currentRequirementCategory = model.getRequirementCategory(requirementCategoryName);
        RequirementCategory editedRequirementCategory =
                new RequirementCategory(requirementCategoryName, currentRequirementCategory.getCredits(), validCodeSet);

        Model expectedModel = new ModelManager(model.getApplication(), new UserPrefs());
        expectedModel.setRequirementCategory(currentRequirementCategory, editedRequirementCategory);
        expectedModel.commitApplication();

        validCodeSet.add(new Code("CS2100"));
        String formattedCodeString = StringUtil.joinStreamAsString(validCodeSet.stream().sorted());
        assertCommandSuccess(new RequirementRemoveCommand(validCodeSet), model, commandHistory,
                String.format(RequirementRemoveCommand.MESSAGE_SUCCESS, formattedCodeString),
                expectedModel);
    }

    @Test
    public void execute_removeCodeFromRequirementCategoryCaseInsensitive_success() {
        Set<Code> validCodeSet = new HashSet<>();
        Name requirementCategoryName = new Name("COMPUting FOundAtIon");
        RequirementCategory currentRequirementCategory = model.getRequirementCategory(requirementCategoryName);
        RequirementCategory editedRequirementCategory = new RequirementCategory(currentRequirementCategory.getName(),
                currentRequirementCategory.getCredits(), validCodeSet);

        Model expectedModel = new ModelManager(model.getApplication(), new UserPrefs());
        expectedModel.setRequirementCategory(currentRequirementCategory, editedRequirementCategory);
        expectedModel.commitApplication();

        validCodeSet.add(new Code("cs2100"));
        String formattedCodeString = StringUtil.joinStreamAsString(validCodeSet.stream().sorted());
        assertCommandSuccess(new RequirementRemoveCommand(validCodeSet), model, commandHistory,
                String.format(RequirementRemoveCommand.MESSAGE_SUCCESS, formattedCodeString),
                expectedModel);
    }

    @Test
    public void execute_removeMultipleCodeFromRequirementCategory_success() {
        Set<Code> validCodeSet = new HashSet<>();
        Name requirementCategoryNameSource1 = new Name("Computing Foundation");
        Name requirementCategoryNameSource2 = new Name("Mathematics");

        RequirementCategory currentRequirementCategory1 = model.getRequirementCategory(requirementCategoryNameSource1);
        RequirementCategory currentRequirementCategory2 = model.getRequirementCategory(requirementCategoryNameSource2);

        RequirementCategory editedRequirementCategory1 = new RequirementCategory(requirementCategoryNameSource1,
                currentRequirementCategory1.getCredits(), validCodeSet);
        RequirementCategory editedRequirementCategory2 = new RequirementCategory(requirementCategoryNameSource2,
                currentRequirementCategory2.getCredits(), validCodeSet);

        Model expectedModel = new ModelManager(model.getApplication(), new UserPrefs());
        expectedModel.setRequirementCategory(currentRequirementCategory1, editedRequirementCategory1);
        expectedModel.setRequirementCategory(currentRequirementCategory2, editedRequirementCategory2);
        expectedModel.commitApplication();

        validCodeSet.addAll(Set.of(new Code("CS2100"), new Code("CS1231")));
        String formattedCodeString = StringUtil.joinStreamAsString(validCodeSet.stream().sorted());
        assertCommandSuccess(new RequirementRemoveCommand(validCodeSet), model, commandHistory,
                String.format(RequirementRemoveCommand.MESSAGE_SUCCESS, formattedCodeString),
                expectedModel);

        // undo -> reverts application back to previous state
        expectedModel.undoApplication();
        assertCommandSuccess(new UndoCommand(), model, commandHistory, UndoCommand.MESSAGE_SUCCESS, expectedModel);

        // redo -> reverts application back to previous state before undo
        expectedModel.redoApplication();
        assertCommandSuccess(new RedoCommand(), model, commandHistory, RedoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_equals() {
        Set<Code> validCodeSet = Set.of(new Code("CS2100"));
        Set<Code> codeSetToCompare = Set.of(new Code("CS1010"));

        RequirementRemoveCommand commandBaseline = new RequirementRemoveCommand(validCodeSet);
        RequirementRemoveCommand commandToCompare = new RequirementRemoveCommand(codeSetToCompare);

        //same object -> returns true
        assertTrue(commandBaseline.equals(commandBaseline));

        //different objects, same values -> returns true
        RequirementRemoveCommand commandBaselineCopy = new RequirementRemoveCommand(validCodeSet);
        assertTrue(commandBaseline.equals(commandBaselineCopy));

        //different objects -> returns false
        assertFalse(commandBaseline.equals(commandToCompare));

        //different objects -> returns false
        assertFalse(commandBaseline.equals(1));
    }
}
