package pwe.planner.logic.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static pwe.planner.logic.commands.CommandTestUtil.assertCommandFailure;
import static pwe.planner.logic.commands.CommandTestUtil.assertCommandSuccess;
import static pwe.planner.testutil.TypicalDegreePlanners.getTypicalDegreePlannerList;
import static pwe.planner.testutil.TypicalModules.getTypicalModuleList;
import static pwe.planner.testutil.TypicalRequirementCategories.getTypicalRequirementCategoriesList;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

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
import pwe.planner.model.planner.DegreePlanner;
import pwe.planner.model.planner.Semester;
import pwe.planner.model.planner.Year;
import pwe.planner.storage.JsonSerializableApplication;

public class PlannerMoveCommandTest {

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
    public void execute_nonExistentCode_throwsCommandException() {
        Code nonExistentCode = new Code("CS9999");
        Year validYear = new Year("1");
        Semester validSemester = new Semester("2");
        assertCommandFailure(new PlannerMoveCommand(validYear, validSemester, nonExistentCode), model,
                commandHistory, String.format(PlannerMoveCommand.MESSAGE_NONEXISTENT_CODE, nonExistentCode));
    }

    @Test
    public void execute_nonExistentYear_throwsCommandException() {
        Code validCodeToMove = new Code("CS1010");
        Year nonExistentYear = new Year("4");
        Semester validSemester = new Semester("1");
        assertCommandFailure(new PlannerMoveCommand(nonExistentYear, validSemester, validCodeToMove), model,
                commandHistory,
                String.format(PlannerMoveCommand.MESSAGE_NONEXISTENT_DEGREE_PLANNER, nonExistentYear, validSemester));
    }

    @Test
    public void execute_nonExistentSemester_throwsCommandException() {
        Code validCodeToMove = new Code("CS1010");
        Year validYear = new Year("1");
        Semester nonExistentSemester = new Semester("4");
        assertCommandFailure(new PlannerMoveCommand(validYear, nonExistentSemester, validCodeToMove), model,
                commandHistory,
                String.format(PlannerMoveCommand.MESSAGE_NONEXISTENT_DEGREE_PLANNER, validYear, nonExistentSemester));
    }

    @Test
    public void execute_validPlannerUnfilteredList_success() {
        Code validCodeToMove = new Code("CS1010");
        Year validYear = new Year("1");
        Semester validSemester = new Semester("2");
        DegreePlanner toFind = new DegreePlanner(validYear, validSemester, Collections.emptySet());
        DegreePlanner sourcePlannerToEdit = model.getDegreePlannerByCode(validCodeToMove);
        DegreePlanner destinationPlannerToEdit = model.getApplication().getDegreePlannerList().stream()
                .filter(toFind::isSameDegreePlanner)
                .findFirst()
                .orElse(null);

        PlannerMoveCommand plannerMoveCommand = new PlannerMoveCommand(validYear, validSemester, validCodeToMove);

        Model expectedModel = new ModelManager(model.getApplication(), new UserPrefs());

        expectedModel.moveModuleBetweenPlanner(sourcePlannerToEdit, destinationPlannerToEdit, validCodeToMove);

        Set<Code> corequisites = model.getModuleByCode(validCodeToMove).getCorequisites();
        String corequisitesMoved = corequisites.isEmpty()
                ? "None"
                : corequisites.stream().sorted().map(Code::toString).collect(Collectors.joining(", "));
        String expectedMessage = String.format(PlannerMoveCommand.MESSAGE_SUCCESS, validCodeToMove, validYear,
                validSemester, corequisitesMoved);

        expectedModel.commitApplication();

        // plannerMove -> module moved
        assertCommandSuccess(plannerMoveCommand, model, commandHistory, expectedMessage, expectedModel);

        // undo -> reverts application back to previous state and filtered module list to show all modules
        expectedModel.undoApplication();
        assertCommandSuccess(new UndoCommand(), model, commandHistory, UndoCommand.MESSAGE_SUCCESS, expectedModel);

        // redo -> same first module deleted again
        expectedModel.redoApplication();
        assertCommandSuccess(new RedoCommand(), model, commandHistory, RedoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_moveToSamePlannerUnfilteredList_success() {
        Code validCodeToMove = new Code("CS1010");
        Year validYear = new Year("1");
        Semester validSemester = new Semester("1");

        PlannerMoveCommand plannerMoveCommand = new PlannerMoveCommand(validYear, validSemester, validCodeToMove);
        Model expectedModel = new ModelManager(model.getApplication(), new UserPrefs());
        expectedModel.commitApplication();

        Set<Code> corequisites = model.getModuleByCode(validCodeToMove).getCorequisites();
        String corequisitesMoved = corequisites.isEmpty()
                ? "None"
                : corequisites.stream().sorted().map(Code::toString).collect(Collectors.joining(", "));
        String expectedMessage = String.format(PlannerMoveCommand.MESSAGE_SUCCESS, validCodeToMove, validYear,
                validSemester, corequisitesMoved);

        assertCommandSuccess(plannerMoveCommand, model, commandHistory, expectedMessage, expectedModel);

        // undo -> reverts application back to previous state and filtered module list to show all modules
        expectedModel.undoApplication();
        assertCommandSuccess(new UndoCommand(), model, commandHistory, UndoCommand.MESSAGE_SUCCESS, expectedModel);

        // redo -> same first module deleted again
        expectedModel.redoApplication();
        assertCommandSuccess(new RedoCommand(), model, commandHistory, RedoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_unavailableModuleInSemesterPlannerUnfilteredList_success() {
        // module not available in the semester
        Code unavailableValidCodeToMove = new Code("CS1010");
        Year validYear = new Year("1");
        Semester validSemester = new Semester("3");

        PlannerMoveCommand plannerMoveCommand =
                new PlannerMoveCommand(validYear, validSemester, unavailableValidCodeToMove);
        Model expectedModel = new ModelManager(model.getApplication(), new UserPrefs());
        expectedModel.commitApplication();

        String expectedMessage = String.format(PlannerMoveCommand.MESSAGE_UNAVAILABLE_SEMESTER, validSemester);

        assertCommandFailure(plannerMoveCommand, model, commandHistory, expectedMessage);
    }

    @Test
    public void execute_unavailableCorequisitesInSemesterPlannerUnfilteredList_success() {
        // module's corequisite(s) not available in the semester
        Code codeToMove = new Code("CS1231");
        Year validYear = new Year("1");
        Semester validSemester = new Semester("2");

        PlannerMoveCommand plannerMoveCommand =
                new PlannerMoveCommand(validYear, validSemester, codeToMove);
        Model expectedModel = new ModelManager(model.getApplication(), new UserPrefs());
        expectedModel.commitApplication();

        Set<Code> corequisites = model.getModuleByCode(codeToMove).getCorequisites();
        String corequisitesMoved = corequisites.isEmpty()
                ? "None"
                : corequisites.stream().sorted().map(Code::toString).collect(Collectors.joining(", "));

        String expectedMessage =
                String.format(PlannerMoveCommand.MESSAGE_UNAVAILABLE_COREQUISITES, corequisitesMoved, validSemester);

        assertCommandFailure(plannerMoveCommand, model, commandHistory, expectedMessage);
    }

    @Test
    public void executeUndoRedo_invalidPlannerUnfilteredList_success() {
        Code invalidCodeToMove = new Code("CS9999");
        Year validYear = new Year("1");
        Semester validSemester = new Semester("2");
        PlannerMoveCommand plannerMoveCommand = new PlannerMoveCommand(validYear, validSemester, invalidCodeToMove);

        // execution failed -> application state not added into model
        assertCommandFailure(plannerMoveCommand, model, commandHistory,
                String.format(PlannerMoveCommand.MESSAGE_NONEXISTENT_CODE, invalidCodeToMove));

        // single application state in model -> undoCommand and redoCommand fail
        assertCommandFailure(new UndoCommand(), model, commandHistory, UndoCommand.MESSAGE_FAILURE);
        assertCommandFailure(new RedoCommand(), model, commandHistory, RedoCommand.MESSAGE_FAILURE);
    }

    @Test
    public void equals() {
        Code firstCodeToMove = new Code("CS1111");
        Code secondCodeToMove = new Code("CS2222");
        Year firstValidYear = new Year("1");
        Year secondValidYear = new Year("1");
        Semester firstValidSemester = new Semester("1");
        Semester secondValidSemester = new Semester("2");

        PlannerMoveCommand plannerMoveFirstCommand =
                new PlannerMoveCommand(firstValidYear, firstValidSemester, firstCodeToMove);
        PlannerMoveCommand plannerMoveSecondCommand =
                new PlannerMoveCommand(secondValidYear, secondValidSemester, secondCodeToMove);

        // same object -> returns true
        assertTrue(plannerMoveFirstCommand.equals(plannerMoveFirstCommand));

        // same values -> returns true
        PlannerMoveCommand plannerMoveFirstCommandCopy =
                new PlannerMoveCommand(firstValidYear, firstValidSemester, firstCodeToMove);
        assertTrue(plannerMoveFirstCommand.equals(plannerMoveFirstCommandCopy));

        // different types -> returns false
        assertFalse(plannerMoveFirstCommand.equals(1));

        // different module -> returns false
        assertFalse(plannerMoveFirstCommand.equals(plannerMoveSecondCommand));
    }
}
