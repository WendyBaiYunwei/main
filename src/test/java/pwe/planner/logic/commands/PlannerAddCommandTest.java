package pwe.planner.logic.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static pwe.planner.testutil.TypicalDegreePlanners.getTypicalDegreePlannerList;
import static pwe.planner.testutil.TypicalModules.getTypicalModuleList;
import static pwe.planner.testutil.TypicalRequirementCategories.getTypicalRequirementCategoriesList;

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
import pwe.planner.model.planner.Semester;
import pwe.planner.model.planner.Year;
import pwe.planner.storage.JsonSerializableApplication;
import pwe.planner.testutil.ModuleBuilder;

public class PlannerAddCommandTest {
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
    public void constructor_nullYear_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        Semester defaultSemester = new Semester("1");
        Code defaultCode = new ModuleBuilder().build().getCode();
        new PlannerAddCommand(null, defaultSemester, defaultCode);
    }

    @Test
    public void constructor_nullSemester_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        Year defaultYear = new Year("1");
        Code defaultCode = new ModuleBuilder().build().getCode();
        new PlannerAddCommand(defaultYear, null, defaultCode);
    }

    @Test
    public void constructor_nullCode_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        Year defaultYear = new Year("1");
        Semester defaultSemester = new Semester("1");
        new PlannerAddCommand(defaultYear, defaultSemester, null);
    }

    @Test
    public void execute_parametersAcceptedByModel_addSuccessful() throws Exception {
        Year validYear = new Year("1");
        Semester validSemester = new Semester("1");
        Code validCode = new Code("CS2105");

        CommandResult commandResult = new PlannerAddCommand(validYear, validSemester, validCode)
                .execute(model, commandHistory);

        assertEquals(String.format(PlannerAddCommand.MESSAGE_SUCCESS, validYear, validSemester, validCode, "None"),
                commandResult.getFeedbackToUser());
    }

    @Test
    public void execute_duplicatePlannerCodes_throwsCommandException() throws Exception {
        Year validYear = new Year("1");
        Semester validSemester = new Semester("1");
        Code validCode = new Code("CS1010");

        PlannerAddCommand plannerAddCommand = new PlannerAddCommand(validYear, validSemester, validCode);

        thrown.expect(CommandException.class);
        thrown.expectMessage(String.format(PlannerAddCommand.MESSAGE_DUPLICATE_CODE, validCode));
        plannerAddCommand.execute(model, commandHistory);
    }

    @Test
    public void execute_nonexistentPlannerCodes_throwsCommandException() throws Exception {
        Year validYear = new Year("1");
        Semester validSemester = new Semester("1");
        Code nonexistentCode = new Code("CS9999");

        PlannerAddCommand plannerAddCommand = new PlannerAddCommand(validYear, validSemester, nonexistentCode);

        thrown.expect(CommandException.class);
        thrown.expectMessage(String.format(PlannerAddCommand.MESSAGE_NONEXISTENT_MODULES, nonexistentCode));
        plannerAddCommand.execute(model, commandHistory);
    }

    @Test
    public void equals() {
        Year year = new Year("1");
        Semester semester = new Semester("1");
        Code code = new ModuleBuilder().build().getCode();
        Code codeCopy = new ModuleBuilder().withCode("IS1103").build().getCode();

        PlannerAddCommand plannerAddACommand = new PlannerAddCommand(year, semester, code);
        PlannerAddCommand plannerAddBCommand = new PlannerAddCommand(year, semester, codeCopy);

        // same object -> returns true
        assertTrue(plannerAddACommand.equals(plannerAddACommand));

        // same values -> returns true
        PlannerAddCommand plannerAddACommandCopy = new PlannerAddCommand(year, semester, code);
        assertTrue(plannerAddACommand.equals(plannerAddACommandCopy));

        // different types -> returns false
        assertFalse(plannerAddACommand.equals(1));

        // different module -> returns false
        assertFalse(plannerAddACommand.equals(plannerAddBCommand));
    }
}

