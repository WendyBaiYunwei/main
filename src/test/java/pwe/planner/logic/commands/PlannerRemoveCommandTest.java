package pwe.planner.logic.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
import pwe.planner.logic.commands.exceptions.CommandException;
import pwe.planner.model.Model;
import pwe.planner.model.ModelManager;
import pwe.planner.model.UserPrefs;
import pwe.planner.model.module.Code;
import pwe.planner.storage.JsonSerializableApplication;

public class PlannerRemoveCommandTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private CommandHistory commandHistory = new CommandHistory();

    private Model model;

    @Before
    public void setUp() throws IllegalValueException {
        model = new ModelManager(
                new JsonSerializableApplication(getTypicalModuleList(), getTypicalDegreePlannerList(),
                        getTypicalRequirementCategoriesList()).toModelType(),
                new UserPrefs());
    }

    @Test
    public void constructor_nullCodes_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        new PlannerRemoveCommand(null);
    }

    @Test
    public void execute_parametersAcceptedByModel_removeWithCoreqSuccessful() throws Exception {
        Code validCode = new Code("CS2102");
        Set<Code> validCodeSet = new HashSet<>();
        validCodeSet.add(validCode);
        Code anotherValidCode = new Code("CS1010");
        validCodeSet.add(anotherValidCode);
        Code coreqRemoved = new Code("CS1231");
        Set<Code> coreqsRemoved = new HashSet<>();
        coreqsRemoved.add(coreqRemoved);

        CommandResult commandResult = new PlannerRemoveCommand(validCodeSet)
                .execute(model, commandHistory);

        assertEquals(String.format(PlannerRemoveCommand.MESSAGE_SUCCESS, validCodeSet, coreqsRemoved),
                commandResult.getFeedbackToUser());
    }

    @Test
    public void execute_parametersAcceptedByModel_removeWithoutCoreqSuccessful() throws Exception {
        Code validCode = new Code("CS2100");
        Set<Code> validCodeSet = new HashSet<>();
        validCodeSet.add(validCode);
        Code anotherValidCode = new Code("CS1010");
        validCodeSet.add(anotherValidCode);

        CommandResult commandResult = new PlannerRemoveCommand(validCodeSet)
                .execute(model, commandHistory);

        assertEquals(String.format(PlannerRemoveCommand.MESSAGE_SUCCESS, validCodeSet, "None"),
                commandResult.getFeedbackToUser());
    }

    @Test
    public void execute_nonexistentPlannerCodes_throwsCommandException() throws Exception {
        Code nonexistentCode = new Code("CS9999");
        Set<Code> nonexistentCodeSet = new HashSet<>();
        nonexistentCodeSet.add(nonexistentCode);

        PlannerRemoveCommand plannerRemoveCommand = new PlannerRemoveCommand(nonexistentCodeSet);

        thrown.expect(CommandException.class);
        thrown.expectMessage(String.format(PlannerRemoveCommand.MESSAGE_NONEXISTENT_CODES, nonexistentCodeSet));
        plannerRemoveCommand.execute(model, commandHistory);
    }

    @Test
    public void equals() {
        Code code = new Code("CS1010");
        Set<Code> codeSet = new HashSet<>();
        codeSet.add(code);
        Code anotherCode = new Code("IS1103");
        Set<Code> anotherCodeSet = new HashSet<>();
        anotherCodeSet.add(anotherCode);

        PlannerRemoveCommand plannerRemoveACommand = new PlannerRemoveCommand(codeSet);
        PlannerRemoveCommand plannerRemoveBCommand = new PlannerRemoveCommand(anotherCodeSet);

        // same object -> returns true
        assertTrue(plannerRemoveACommand.equals(plannerRemoveACommand));

        // same values -> returns true
        PlannerRemoveCommand plannerAddACommandCopy = new PlannerRemoveCommand(codeSet);
        assertTrue(plannerRemoveACommand.equals(plannerAddACommandCopy));

        // different types -> returns false
        assertFalse(plannerRemoveACommand.equals(1));

        // different module -> returns false
        assertFalse(plannerRemoveACommand.equals(plannerRemoveBCommand));
    }
}

