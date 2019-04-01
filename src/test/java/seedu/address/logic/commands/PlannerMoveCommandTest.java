package seedu.address.logic.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalDegreePlanners.getTypicalDegreePlannerList;
import static seedu.address.testutil.TypicalModules.getTypicalModuleList;
import static seedu.address.testutil.TypicalRequirementCategories.getTypicalRequirementCategoriesList;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.CommandHistory;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.module.Code;
import seedu.address.model.planner.DegreePlanner;
import seedu.address.model.planner.Semester;
import seedu.address.model.planner.Year;
import seedu.address.storage.JsonSerializableAddressBook;

public class PlannerMoveCommandTest {

    @Rule public ExpectedException thrown = ExpectedException.none();

    private CommandHistory commandHistory = new CommandHistory();
    private Model model;

    @Before
    public void setUp() throws IllegalValueException {
        model = new ModelManager(
                new JsonSerializableAddressBook(getTypicalModuleList(), getTypicalDegreePlannerList(),
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
    public void execute_validPlannerUnfilteredList_success() throws Exception {
        Code validCodeToMove = new Code("CS1010");
        Year validYear = new Year("1");
        Semester validSemester = new Semester("2");
        DegreePlanner toFind = new DegreePlanner(validYear, validSemester, Collections.emptySet());
        DegreePlanner sourcePlannerToEdit = model.getDegreePlannerByCode(validCodeToMove);
        DegreePlanner destinationPlannerToEdit = model.getAddressBook().getDegreePlannerList().stream()
                .filter(toFind::isSameDegreePlanner)
                .findFirst()
                .orElse(null);

        PlannerMoveCommand plannerMoveCommand =
                new PlannerMoveCommand(validYear, validSemester, validCodeToMove);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        String expectedMessage =
                String.format(PlannerMoveCommand.MESSAGE_SUCCESS, validCodeToMove, validYear, validSemester);

        Set<Code> newSourceCodes = new HashSet<>(sourcePlannerToEdit.getCodes());
        newSourceCodes.remove(validCodeToMove);
        Set<Code> newDestinationCodes = new HashSet<>(destinationPlannerToEdit.getCodes());
        newDestinationCodes.add(validCodeToMove);

        DegreePlanner editedSourcePlanner =
                new DegreePlanner(sourcePlannerToEdit.getYear(), sourcePlannerToEdit.getSemester(), newSourceCodes);
        DegreePlanner editedDestinationPlanner =
                new DegreePlanner(destinationPlannerToEdit.getYear(), destinationPlannerToEdit.getSemester(),
                        newDestinationCodes);
        expectedModel.setDegreePlanner(sourcePlannerToEdit, editedSourcePlanner);
        expectedModel.setDegreePlanner(destinationPlannerToEdit, editedDestinationPlanner);
        expectedModel.commitAddressBook();

        // plannerMove -> module moved
        assertCommandSuccess(plannerMoveCommand, model, commandHistory, expectedMessage, expectedModel);

        // undo -> reverts addressbook back to previous state and filtered module list to show all modules
        expectedModel.undoAddressBook();
        assertCommandSuccess(new UndoCommand(), model, commandHistory, UndoCommand.MESSAGE_SUCCESS, expectedModel);

        // redo -> same first module deleted again
        expectedModel.redoAddressBook();
        assertCommandSuccess(new RedoCommand(), model, commandHistory, RedoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_moveToSamePlannerUnfilteredList_success() {
        Code validCodeToMove = new Code("CS1010");
        Year validYear = new Year("1");
        Semester validSemester = new Semester("1");

        PlannerMoveCommand plannerMoveCommand =
                new PlannerMoveCommand(validYear, validSemester, validCodeToMove);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.commitAddressBook();

        String expectedMessage =
                String.format(PlannerMoveCommand.MESSAGE_SUCCESS, validCodeToMove, validYear, validSemester);

        assertCommandSuccess(plannerMoveCommand, model, commandHistory, expectedMessage, expectedModel);

        // undo -> reverts addressbook back to previous state and filtered module list to show all modules
        expectedModel.undoAddressBook();
        assertCommandSuccess(new UndoCommand(), model, commandHistory, UndoCommand.MESSAGE_SUCCESS, expectedModel);

        // redo -> same first module deleted again
        expectedModel.redoAddressBook();
        assertCommandSuccess(new RedoCommand(), model, commandHistory, RedoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void executeUndoRedo_invalidPlannerUnfilteredList_success() {
        Code invalidCodeToMove = new Code("CS9999");
        Year validYear = new Year("1");
        Semester validSemester = new Semester("2");
        PlannerMoveCommand plannerMoveCommand =
                new PlannerMoveCommand(validYear, validSemester, invalidCodeToMove);

        // execution failed -> address book state not added into model
        assertCommandFailure(plannerMoveCommand, model, commandHistory,
                String.format(PlannerMoveCommand.MESSAGE_NONEXISTENT_CODE, invalidCodeToMove));

        // single address book state in model -> undoCommand and redoCommand fail
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
