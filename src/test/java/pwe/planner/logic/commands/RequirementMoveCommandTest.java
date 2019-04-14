package pwe.planner.logic.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static pwe.planner.logic.commands.CommandTestUtil.assertCommandFailure;
import static pwe.planner.logic.commands.CommandTestUtil.assertCommandSuccess;
import static pwe.planner.testutil.TypicalDegreePlanners.getTypicalDegreePlannerList;
import static pwe.planner.testutil.TypicalModules.getTypicalModuleList;
import static pwe.planner.testutil.TypicalRequirementCategories.getTypicalRequirementCategoriesList;

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
import pwe.planner.model.util.RequirementCategoryUtil;
import pwe.planner.storage.JsonSerializableApplication;

public class RequirementMoveCommandTest {

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
        new RequirementMoveCommand(null, null);
    }

    @Test
    public void execute_nonExistentRequirementCategory_throwsCommandException() {
        Set<Code> validCodeSet = Set.of(new Code("CS1231"));
        Name nonExistentRequirementCategoryName = new Name("DOES NOT EXIST");

        RequirementMoveCommand requirementMoveCommand =
                new RequirementMoveCommand(nonExistentRequirementCategoryName, validCodeSet);

        String expectedMessage = String.format(RequirementMoveCommand.MESSAGE_NONEXISTENT_REQUIREMENT_CATEGORY,
                nonExistentRequirementCategoryName);

        assertCommandFailure(requirementMoveCommand, model, commandHistory, expectedMessage);
    }

    @Test
    public void execute_nonExistentCodeInApplication_throwsCommandException() {
        Set<Code> validCodeSet = Set.of(new Code("CS9999"));
        Name requirementCategoryName = new Name("Computing Breadth");

        RequirementMoveCommand requirementMoveCommand =
                new RequirementMoveCommand(requirementCategoryName, validCodeSet);

        String formattedCodeString = StringUtil.joinStreamAsString(validCodeSet.stream().sorted());

        String expectedMessage = String.format(RequirementMoveCommand.MESSAGE_NONEXISTENT_CODE, formattedCodeString);

        assertCommandFailure(requirementMoveCommand, model, commandHistory, expectedMessage);
    }

    @Test
    public void execute_codeNotInAnyRequirementCategory_throwsCommandException() {
        Set<Code> validCodeSet = Set.of(new Code("CS2040C"));
        Name requirementCategoryName = new Name("Computing Breadth");

        RequirementMoveCommand requirementMoveCommand =
                new RequirementMoveCommand(requirementCategoryName, validCodeSet);

        String formattedCodeString = StringUtil.joinStreamAsString(validCodeSet.stream().sorted());

        String expectedMessage =
                String.format(RequirementMoveCommand.MESSAGE_CODE_NOT_IN_ANY_REQUIREMENT_CATEGORY, formattedCodeString);

        assertCommandFailure(requirementMoveCommand, model, commandHistory, expectedMessage);
    }

    @Test
    public void execute_multiCodeWithNonExistentCodeInApplication_throwsCommandException() {
        Set<Code> validCodeSet = Set.of(new Code("CS9999"), new Code("CS2100"));
        Name requirementCategoryName = new Name("Computing Breadth");

        Set<Code> invalidCodeSet = Set.of(new Code("CS9999"));

        RequirementMoveCommand requirementMoveCommand =
                new RequirementMoveCommand(requirementCategoryName, validCodeSet);

        String formattedCodeString = StringUtil.joinStreamAsString(invalidCodeSet.stream().sorted());

        String expectedMessage =
                String.format(RequirementMoveCommand.MESSAGE_NONEXISTENT_CODE, formattedCodeString);

        assertCommandFailure(requirementMoveCommand, model, commandHistory, expectedMessage);
    }

    @Test
    public void execute_multiCodeWithCodeNotInAnyRequirementCategory_throwsCommandException() {
        Set<Code> validCodeSet = Set.of(new Code("CS2040C"), new Code("CS2100"));
        Name requirementCategoryName = new Name("Computing Breadth");

        Set<Code> invalidCodeSet = Set.of(new Code("CS2040C"));

        RequirementMoveCommand requirementMoveCommand =
                new RequirementMoveCommand(requirementCategoryName, validCodeSet);

        String formattedCodeString = StringUtil.joinStreamAsString(invalidCodeSet.stream().sorted());

        String expectedMessage =
                String.format(RequirementMoveCommand.MESSAGE_CODE_NOT_IN_ANY_REQUIREMENT_CATEGORY, formattedCodeString);

        assertCommandFailure(requirementMoveCommand, model, commandHistory, expectedMessage);
    }

    @Test
    public void execute_multiCodeAndCodeNotInAnyRequirementCategoryAndNonExistentRequirementCategory_throwsException() {
        Set<Code> validCodeSet = Set.of(new Code("CS2040C"), new Code("CS2100"));
        Name nonExistentRequirementCategoryName = new Name("DOES NOT EXIST");

        RequirementMoveCommand requirementMoveCommand =
                new RequirementMoveCommand(nonExistentRequirementCategoryName, validCodeSet);

        String expectedMessage = String.format(RequirementMoveCommand.MESSAGE_NONEXISTENT_REQUIREMENT_CATEGORY,
                nonExistentRequirementCategoryName);


        assertCommandFailure(requirementMoveCommand, model, commandHistory, expectedMessage);
    }

    @Test
    public void execute_multiCodeWithNonExistentCodeInAppAndNonExistentRequirementCategory_throwsCommandException() {
        Set<Code> validCodeSet = Set.of(new Code("CS9999"), new Code("CS2100"));
        Name nonExistentRequirementCategoryName = new Name("DOES NOT EXIST");

        RequirementMoveCommand requirementMoveCommand =
                new RequirementMoveCommand(nonExistentRequirementCategoryName, validCodeSet);

        String expectedMessage = String.format(RequirementMoveCommand.MESSAGE_NONEXISTENT_REQUIREMENT_CATEGORY,
                nonExistentRequirementCategoryName);

        assertCommandFailure(requirementMoveCommand, model, commandHistory, expectedMessage);
    }

    @Test
    public void execute_multiCodeWithCodeNotInAnyRequirementCategoryAndApplication_throwsCommandException() {
        Set<Code> validCodeSet = Set.of(new Code("CS2040C"), new Code("CS2100"), new Code("CS9999"));
        Name requirementCategoryName = new Name("Computing Breadth");

        Set<Code> invalidCodeSet = Set.of(new Code("CS9999"));

        RequirementMoveCommand requirementMoveCommand =
                new RequirementMoveCommand(requirementCategoryName, validCodeSet);

        String formattedCodeString = StringUtil.joinStreamAsString(invalidCodeSet.stream().sorted());

        String expectedMessage = String.format(RequirementMoveCommand.MESSAGE_NONEXISTENT_CODE, formattedCodeString);

        assertCommandFailure(requirementMoveCommand, model, commandHistory, expectedMessage);
    }

    @Test
    public void execute_multiCodeWithAllInvalid_throwsCommandException() {
        Set<Code> validCodeSet = Set.of(new Code("CS9999"), new Code("CS2040C"));
        Name requirementCategoryName = new Name("Computing 123");

        RequirementMoveCommand requirementMoveCommand =
                new RequirementMoveCommand(requirementCategoryName, validCodeSet);

        String expectedMessage = String.format(RequirementMoveCommand.MESSAGE_NONEXISTENT_REQUIREMENT_CATEGORY,
                requirementCategoryName);

        assertCommandFailure(requirementMoveCommand, model, commandHistory, expectedMessage);
    }

    @Test
    public void execute_moveCodeToOtherRequirementCategory_success() {
        Set<Code> validCodeSet = Set.of(new Code("CS2100"));

        Name sourceReqCatName = new Name("Computing Foundation");
        Name destinationReqCatName = new Name("Computing Breadth");

        RequirementCategory sourceReqCat = model.getRequirementCategory(sourceReqCatName);
        RequirementCategory destinationReqCat = model.getRequirementCategory(destinationReqCatName);

        RequirementCategory editedSourceReqCat = RequirementCategoryUtil
                .getRequirementCategoryWithCodesRemoved(sourceReqCat, validCodeSet);
        RequirementCategory editedDestinationReqCat = RequirementCategoryUtil
                .getRequirementCategoryWithCodesAdded(destinationReqCat, validCodeSet);

        Model expectedModel = new ModelManager(model.getApplication(), new UserPrefs());
        expectedModel.setRequirementCategory(sourceReqCat, editedSourceReqCat);
        expectedModel.setRequirementCategory(destinationReqCat, editedDestinationReqCat);
        expectedModel.commitApplication();

        String formattedCodeString = StringUtil.joinStreamAsString(validCodeSet.stream().sorted());

        RequirementMoveCommand requirementMoveCommand =
                new RequirementMoveCommand(destinationReqCatName, validCodeSet);

        String expectedMessage = String.format(RequirementMoveCommand.MESSAGE_SUCCESS, formattedCodeString,
                destinationReqCatName);

        assertCommandSuccess(requirementMoveCommand, model, commandHistory, expectedMessage , expectedModel);
    }

    @Test
    public void execute_moveCodeToSameRequirementCategory_success() {
        Set<Code> validCodeSet = Set.of(new Code("CS2100"));

        Name destinationReqCatName = new Name("Computing Foundation");

        Model expectedModel = model;

        String formattedCodeString = StringUtil.joinStreamAsString(validCodeSet.stream().sorted());

        RequirementMoveCommand requirementMoveCommand =
                new RequirementMoveCommand(destinationReqCatName, validCodeSet);

        String expectedMessage =
                String.format(RequirementMoveCommand.MESSAGE_SUCCESS, formattedCodeString, destinationReqCatName);

        assertCommandSuccess(requirementMoveCommand, model, commandHistory, expectedMessage, expectedModel);
    }

    @Test
    public void execute_moveCodesFromMultipleSource_success() {
        Set<Code> validCodeSet = Set.of(new Code("CS1231"), new Code("CS2100"));

        Set<Code> sourceCodeSet1 = Set.of(new Code("CS2100"));

        Set<Code> sourceCodeSet2 = Set.of(new Code("CS1231"));

        Name sourceReqCatName1 = new Name("Computing Foundation");
        Name sourceReqCatName2 = new Name("Mathematics");
        Name destinationReqCatName = new Name("Computing Breadth");

        RequirementCategory sourceReqCat1 = model.getRequirementCategory(sourceReqCatName1);
        RequirementCategory sourceReqCat2 = model.getRequirementCategory(sourceReqCatName2);
        RequirementCategory destinationReqCat = model.getRequirementCategory(destinationReqCatName);

        RequirementCategory editedSourceReqCat = RequirementCategoryUtil
                        .getRequirementCategoryWithCodesRemoved(sourceReqCat1, sourceCodeSet1);
        RequirementCategory editedSourceReqCat2 = RequirementCategoryUtil
                        .getRequirementCategoryWithCodesRemoved(sourceReqCat2, sourceCodeSet2);
        RequirementCategory editedDestinationReqCat = RequirementCategoryUtil
                        .getRequirementCategoryWithCodesAdded(destinationReqCat, validCodeSet);

        Model expectedModel = new ModelManager(model.getApplication(), new UserPrefs());
        expectedModel.setRequirementCategory(sourceReqCat1, editedSourceReqCat);
        expectedModel.setRequirementCategory(sourceReqCat2, editedSourceReqCat2);
        expectedModel.setRequirementCategory(destinationReqCat, editedDestinationReqCat);
        expectedModel.commitApplication();

        RequirementMoveCommand requirementMoveCommand =
                new RequirementMoveCommand(destinationReqCatName, validCodeSet);

        String formattedCodeString = StringUtil.joinStreamAsString(validCodeSet.stream().sorted());

        String expectedMessage =
                String.format(RequirementMoveCommand.MESSAGE_SUCCESS, formattedCodeString, destinationReqCatName);

        assertCommandSuccess(requirementMoveCommand, model, commandHistory, expectedMessage, expectedModel);
    }

    @Test
    public void execute_moveCodesWithSameSourceAndDestination_success() {
        Set<Code> validCodeSet = Set.of(new Code("CS1231"), new Code("CS2100"));

        Set<Code> sourceCodeSet1 = Set.of(new Code("CS2100"));

        Set<Code> sourceCodeSet2 = Set.of(new Code("CS1231"));

        Name sourceReqCatName1 = new Name("Computing Foundation");
        Name sourceReqCatName2 = new Name("Mathematics");
        Name destinationReqCatName = new Name("Computing Foundation");

        RequirementCategory sourceReqCat1 = model.getRequirementCategory(sourceReqCatName1);
        RequirementCategory sourceReqCat2 = model.getRequirementCategory(sourceReqCatName2);

        RequirementCategory editedSourceReqCat = RequirementCategoryUtil
                        .getRequirementCategoryWithCodesRemoved(sourceReqCat1, sourceCodeSet1);
        RequirementCategory editedSourceReqCat2 = RequirementCategoryUtil
                        .getRequirementCategoryWithCodesRemoved(sourceReqCat2, sourceCodeSet2);

        Model expectedModel = new ModelManager(model.getApplication(), new UserPrefs());
        expectedModel.setRequirementCategory(sourceReqCat1, editedSourceReqCat);
        expectedModel.setRequirementCategory(sourceReqCat2, editedSourceReqCat2);

        RequirementCategory destinationReqCat = expectedModel.getRequirementCategory(destinationReqCatName);
        RequirementCategory editedDestinationReqCat = RequirementCategoryUtil
                        .getRequirementCategoryWithCodesAdded(destinationReqCat, validCodeSet);
        expectedModel.setRequirementCategory(destinationReqCat, editedDestinationReqCat);
        expectedModel.commitApplication();

        RequirementMoveCommand requirementMoveCommand =
                new RequirementMoveCommand(destinationReqCatName, validCodeSet);

        String formattedCodeString = StringUtil.joinStreamAsString(validCodeSet.stream().sorted());

        String expectedMessage =
                String.format(RequirementMoveCommand.MESSAGE_SUCCESS, formattedCodeString, destinationReqCatName);

        assertCommandSuccess(requirementMoveCommand, model, commandHistory, expectedMessage, expectedModel);
    }

    @Test
    public void execute_equals() {
        Set<Code> validCodeSet = Set.of(new Code("CS2100"));
        Name requirementCategoryName = new Name("Computing Foundation");

        Set<Code> codeSetToCompare = Set.of(new Code("CS1010"));
        Name requirementCategoryNameToCompare = new Name("Mathematics");

        RequirementMoveCommand commandBaseline = new RequirementMoveCommand(requirementCategoryName, validCodeSet);
        RequirementMoveCommand commandToCompare =
                new RequirementMoveCommand(requirementCategoryNameToCompare, codeSetToCompare);

        //same object -> returns true
        assertTrue(commandBaseline.equals(commandBaseline));

        //different objects, same values -> returns true
        RequirementMoveCommand commandBaselineCopy = new RequirementMoveCommand(requirementCategoryName, validCodeSet);
        assertTrue(commandBaseline.equals(commandBaselineCopy));

        //different objects -> returns false
        assertFalse(commandBaseline.equals(commandToCompare));

        //different objects -> returns false
        assertFalse(commandBaseline.equals(1));
    }


}
