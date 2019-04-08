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
    private Set<Code> codeList = new HashSet<>();

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
        codeList.clear();
        Name nonExistentRequirementCategoryName = new Name("DOES NOT EXIST");
        assertCommandFailure(new RequirementAddCommand(nonExistentRequirementCategoryName, codeList),
                model, commandHistory, String.format(RequirementAddCommand.MESSAGE_NONEXISTENT_REQUIREMENT_CATEGORY,
                        nonExistentRequirementCategoryName));
    }

    @Test
    public void execute_nonExistentCode_throwsCommandException() {
        codeList.clear();
        codeList.add(new Code("CS2010"));
        Name requirementCategoryName = new Name("Computing Foundation");
        assertCommandFailure(new RequirementAddCommand(requirementCategoryName, codeList), model, commandHistory,
                RequirementAddCommand.MESSAGE_NONEXISTENT_CODE);

        //case insensitive checks
        requirementCategoryName = new Name("comPUTING FOUNDATion");
        assertCommandFailure(new RequirementAddCommand(requirementCategoryName, codeList), model, commandHistory,
                String.format(RequirementAddCommand.MESSAGE_NONEXISTENT_CODE,
                        requirementCategoryName));
    }

    @Test
    public void execute_duplicateCode_throwsCommandException() {
        codeList.clear();
        codeList.add(new Code("CS2100"));
        Name requirementCategoryName = new Name("Computing Foundation");
        assertCommandFailure(new RequirementAddCommand(requirementCategoryName, codeList), model, commandHistory,
                String.format(RequirementAddCommand.MESSAGE_DUPLICATE_CODE,
                        requirementCategoryName));

        //case insensitive checks
        Name requirementCategoryNameInsensitive = new Name("COMPUting FOundAtIon");
        assertCommandFailure(new RequirementAddCommand(requirementCategoryNameInsensitive, codeList), model,
                commandHistory, String.format(RequirementAddCommand.MESSAGE_DUPLICATE_CODE,
                        requirementCategoryName));
    }

    @Test
    public void execute_existingCode_throwsCommandException() {
        codeList.clear();
        codeList.add(new Code("CS2100"));
        Name requirementCategoryName = new Name("Computing Breadth");
        assertCommandFailure(new RequirementAddCommand(requirementCategoryName, codeList), model, commandHistory,
                RequirementAddCommand.MESSAGE_EXISTING_CODE);

        //case insensitive checks
        requirementCategoryName = new Name("COMPUting BREAdth");
        assertCommandFailure(new RequirementAddCommand(requirementCategoryName, codeList), model, commandHistory,
                String.format(RequirementAddCommand.MESSAGE_EXISTING_CODE,
                        requirementCategoryName));
    }

    @Test
    public void execute_addModuleToRequirementCategory_success() {
        codeList.clear();
        Name requirementCategoryName = new Name("Computing Foundation");
        RequirementCategory currentRequirementCategory = model.getRequirementCategory(requirementCategoryName);
        RequirementCategory editedRequirementCategory =
                new RequirementCategory(requirementCategoryName, currentRequirementCategory.getCredits(), codeList);

        Model expectedModel = model;
        expectedModel.setRequirementCategory(currentRequirementCategory, editedRequirementCategory);
        expectedModel.commitApplication();

        assertCommandSuccess(new RequirementAddCommand(requirementCategoryName, codeList), model, commandHistory,
                String.format(RequirementAddCommand.MESSAGE_SUCCESS, requirementCategoryName, codeList),
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
        codeList.clear();
        Name requirementCategoryName = new Name("CoMpUtInG BreaDTH");
        RequirementCategory currentRequirementCategory = model.getRequirementCategory(requirementCategoryName);
        RequirementCategory editedRequirementCategory =
                new RequirementCategory(requirementCategoryName, currentRequirementCategory.getCredits(), codeList);

        Model expectedModel = model;
        expectedModel.setRequirementCategory(currentRequirementCategory, editedRequirementCategory);
        expectedModel.commitApplication();

        assertCommandSuccess(new RequirementAddCommand(requirementCategoryName, codeList), model, commandHistory,
                String.format(RequirementAddCommand.MESSAGE_SUCCESS, requirementCategoryName, codeList),
                expectedModel);

    }

    @Test
    public void execute_equals() {
        codeList.clear();
        codeList.add(new Code("CS2100"));
        Name requirementCategoryName = new Name("Computing Foundation");

        Set<Code> codeListToCompare = new HashSet<>();
        codeListToCompare.add(new Code("CS1010"));
        Name requirementCategoryNameToCompare = new Name("Mathematics");

        RequirementAddCommand commandBaseline = new RequirementAddCommand(requirementCategoryName, codeList);
        RequirementAddCommand commandToCompare =
                new RequirementAddCommand(requirementCategoryNameToCompare, codeListToCompare);

        //same object -> returns true
        assertTrue(commandBaseline.equals(commandBaseline));

        //different objects, same values -> returns true
        RequirementAddCommand commandBaselineCopy = new RequirementAddCommand(requirementCategoryName, codeList);
        assertTrue(commandBaseline.equals(commandBaselineCopy));

        //different objects -> returns false
        assertFalse(commandBaseline.equals(commandToCompare));

        //different objects -> returns false
        assertFalse(commandBaseline.equals(1));
    }

}
