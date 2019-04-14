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

public class RequirementAddCommandTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private CommandHistory commandHistory = new CommandHistory();
    private Model model;

    @Before
    public void setUp() throws IllegalValueException {
        model = new ModelManager(
                new JsonSerializableApplication(getTypicalModuleList(), getTypicalDegreePlannerList(),
                        getTypicalRequirementCategoriesList()).toModelType(), new UserPrefs());
    }

    @Test
    public void constructor_nullModule_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        new RequirementAddCommand(null, null);
    }

    @Test
    public void execute_nonExistentRequirementCategory_throwsCommandException() {
        Set<Code> validCodeSet = new HashSet<>();
        Name nonExistentRequirementCategoryName = new Name("DOES NOT EXIST");
        assertCommandFailure(new RequirementAddCommand(nonExistentRequirementCategoryName, validCodeSet),
                model, commandHistory, String.format(RequirementAddCommand.MESSAGE_NONEXISTENT_REQUIREMENT_CATEGORY,
                        nonExistentRequirementCategoryName));
    }

    @Test
    public void execute_nonExistentCode_throwsCommandException() {
        Set<Code> validCodeSet = Set.of(new Code("CS2010"));
        Name requirementCategoryName = new Name("Computing Foundation");
        String formattedCodeString = StringUtil.joinStreamAsString(validCodeSet.stream().sorted());
        assertCommandFailure(new RequirementAddCommand(requirementCategoryName, validCodeSet), model, commandHistory,
                String.format(RequirementAddCommand.MESSAGE_NONEXISTENT_CODE, formattedCodeString));

        //case insensitive checks
        requirementCategoryName = new Name("comPUTING FOUNDATion");
        assertCommandFailure(new RequirementAddCommand(requirementCategoryName, validCodeSet), model, commandHistory,
                String.format(RequirementAddCommand.MESSAGE_NONEXISTENT_CODE, formattedCodeString));
    }

    @Test
    public void execute_duplicateCode_throwsCommandException() {
        Set<Code> validCodeSet = Set.of(new Code("CS2100"));
        Name requirementCategoryName = new Name("Computing Foundation");
        assertCommandFailure(new RequirementAddCommand(requirementCategoryName, validCodeSet), model, commandHistory,
                String.format(RequirementAddCommand.MESSAGE_DUPLICATE_CODE, requirementCategoryName));

        //case insensitive checks
        Set<Code> validCodeSetCaseInsensitive = Set.of(new Code("CS2100"));
        Name requirementCategoryNameInsensitive = new Name("COMPUting FOundAtIon");
        assertCommandFailure(new RequirementAddCommand(requirementCategoryNameInsensitive, validCodeSetCaseInsensitive),
                model, commandHistory, String.format(RequirementAddCommand.MESSAGE_DUPLICATE_CODE,
                        requirementCategoryName));
    }

    @Test
    public void execute_existingCode_throwsCommandException() {
        Set<Code> validCodeSet = Set.of(new Code("CS2100"));
        Name requirementCategoryName = new Name("Computing Breadth");
        assertCommandFailure(new RequirementAddCommand(requirementCategoryName, validCodeSet), model, commandHistory,
                RequirementAddCommand.MESSAGE_EXISTING_CODE);

        //case insensitive checks
        Set<Code> validCodeSetCaseInsensitive = Set.of(new Code("CS2100"));
        requirementCategoryName = new Name("COMPUting BREAdth");
        assertCommandFailure(new RequirementAddCommand(requirementCategoryName, validCodeSetCaseInsensitive),
                model, commandHistory, String.format(RequirementAddCommand.MESSAGE_EXISTING_CODE,
                        requirementCategoryName));
    }

    @Test
    public void execute_addModuleToRequirementCategory_success() {
        Set<Code> validCodeSet = Set.of(new Code("CS2040C"));
        Set<Code> codeSetToCheck = Set.of(new Code("CS2040C"), new Code("CS2100"));
        Name requirementCategoryName = new Name("Computing Foundation");
        RequirementCategory currentRequirementCategory = model.getRequirementCategory(requirementCategoryName);
        RequirementCategory editedRequirementCategory = new RequirementCategory(requirementCategoryName,
                currentRequirementCategory.getCredits(), codeSetToCheck);

        Model expectedModel = new ModelManager(model.getApplication(), new UserPrefs());
        expectedModel.setRequirementCategory(currentRequirementCategory, editedRequirementCategory);
        expectedModel.commitApplication();

        String formattedCodeString = StringUtil.joinStreamAsString(validCodeSet.stream().sorted());

        assertCommandSuccess(new RequirementAddCommand(requirementCategoryName, validCodeSet), model, commandHistory,
                String.format(RequirementAddCommand.MESSAGE_SUCCESS, formattedCodeString, requirementCategoryName),
                expectedModel);

        // undo -> reverts application back to previous state
        expectedModel.undoApplication();
        assertCommandSuccess(new UndoCommand(), model, commandHistory, UndoCommand.MESSAGE_SUCCESS, expectedModel);

        // redo -> reverts application back to previous state before undo
        expectedModel.redoApplication();
        assertCommandSuccess(new RedoCommand(), model, commandHistory, RedoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_addModuleToRequirementCategoryCaseInsensitive_success() {
        Set<Code> validCodeSet = Set.of(new Code("CS2102"), new Code("CS2040C"));
        Set<Code> codeSetToCheck = Set.of(new Code("CS2040C"), new Code("CS2100"), new Code("CS2102"));
        Name requirementCategoryName = new Name("CoMpUtInG FoundatioN");
        RequirementCategory currentRequirementCategory = model.getRequirementCategory(requirementCategoryName);
        RequirementCategory editedRequirementCategory = new RequirementCategory(currentRequirementCategory.getName(),
                currentRequirementCategory.getCredits(), codeSetToCheck);

        Model expectedModel = new ModelManager(model.getApplication(), new UserPrefs());
        expectedModel.setRequirementCategory(currentRequirementCategory, editedRequirementCategory);
        expectedModel.commitApplication();

        String formattedCodeString = StringUtil.joinStreamAsString(validCodeSet.stream().sorted());

        assertCommandSuccess(new RequirementAddCommand(requirementCategoryName, validCodeSet), model, commandHistory,
                String.format(RequirementAddCommand.MESSAGE_SUCCESS, formattedCodeString,
                        currentRequirementCategory.getName()), expectedModel);

    }

    @Test
    public void execute_equals() {
        Set<Code> validCodeSet = Set.of(new Code("CS2100"));
        Name requirementCategoryName = new Name("Computing Foundation");

        Set<Code> codeSetToCompare = Set.of(new Code("CS1010"));
        Name requirementCategoryNameToCompare = new Name("Mathematics");

        RequirementAddCommand commandBaseline = new RequirementAddCommand(requirementCategoryName, validCodeSet);
        RequirementAddCommand commandToCompare =
                new RequirementAddCommand(requirementCategoryNameToCompare, codeSetToCompare);

        //same object -> returns true
        assertTrue(commandBaseline.equals(commandBaseline));

        //different objects, same values -> returns true
        RequirementAddCommand commandBaselineCopy = new RequirementAddCommand(requirementCategoryName, validCodeSet);
        assertTrue(commandBaseline.equals(commandBaselineCopy));

        //different objects -> returns false
        assertFalse(commandBaseline.equals(commandToCompare));

        //different objects -> returns false
        assertFalse(commandBaseline.equals(1));
    }

}
