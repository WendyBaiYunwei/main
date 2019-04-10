package pwe.planner.logic.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
import pwe.planner.logic.commands.exceptions.CommandException;
import pwe.planner.model.Model;
import pwe.planner.model.ModelManager;
import pwe.planner.model.UserPrefs;
import pwe.planner.model.module.Code;
import pwe.planner.model.planner.DegreePlanner;
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
        Set<Code> defaultCodeSet = new HashSet<>();
        defaultCodeSet.add(defaultCode);
        new PlannerAddCommand(null, defaultSemester, defaultCodeSet);
    }

    @Test
    public void constructor_nullSemester_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        Year defaultYear = new Year("1");
        Code defaultCode = new ModuleBuilder().build().getCode();
        Set<Code> defaultCodeSet = new HashSet<>();
        defaultCodeSet.add(defaultCode);
        new PlannerAddCommand(defaultYear, null, defaultCodeSet);
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
        Set<Code> validCodeSet = new HashSet<>();
        validCodeSet.add(validCode);

        Model expectedModel = new ModelManager(
                new JsonSerializableApplication(getTypicalModuleList(), getTypicalDegreePlannerList(),
                        getTypicalRequirementCategoriesList()).toModelType(), new UserPrefs());

        DegreePlanner selectedDegreePlanner = model.getApplication().getDegreePlannerList().stream()
                .filter(degreePlanner -> (degreePlanner.getYear().equals(validYear)
                        && degreePlanner.getSemester().equals(validSemester))).findFirst().orElse(null);

        assert selectedDegreePlanner != null;
        Set<Code> selectedCodeSet = new HashSet<>(selectedDegreePlanner.getCodes());
        selectedCodeSet.addAll(validCodeSet);
        DegreePlanner editedDegreePlanner = new DegreePlanner(selectedDegreePlanner.getYear(),
                selectedDegreePlanner.getSemester(), selectedCodeSet);
        expectedModel.setDegreePlanner(selectedDegreePlanner, editedDegreePlanner);

        expectedModel.commitApplication();

        assertCommandSuccess(new PlannerAddCommand(validYear, validSemester, validCodeSet), model, commandHistory,
                String.format(PlannerAddCommand.MESSAGE_SUCCESS, validYear, validSemester, validCodeSet, "None"),
                expectedModel);
    }

    @Test
    public void execute_duplicatePlannerCodes_throwsCommandException() throws Exception {
        Year validYear = new Year("1");
        Semester validSemester = new Semester("1");
        Code validCode = new Code("CS1010");
        Set<Code> validCodeSet = new HashSet<>();
        validCodeSet.add(validCode);

        PlannerAddCommand plannerAddCommand = new PlannerAddCommand(validYear, validSemester, validCodeSet);

        thrown.expect(CommandException.class);
        thrown.expectMessage(String.format(PlannerAddCommand.MESSAGE_DUPLICATE_CODE, validCodeSet));
        plannerAddCommand.execute(model, commandHistory);
    }

    @Test
    public void execute_nonexistentPlannerCodes_throwsCommandException() throws Exception {
        Year validYear = new Year("1");
        Semester validSemester = new Semester("1");
        Code nonexistentCode = new Code("CS9999");
        Set<Code> nonexistentCodeSet = new HashSet<>();
        nonexistentCodeSet.add(nonexistentCode);

        PlannerAddCommand plannerAddCommand = new PlannerAddCommand(validYear, validSemester, nonexistentCodeSet);

        thrown.expect(CommandException.class);
        thrown.expectMessage(String.format(PlannerAddCommand.MESSAGE_NONEXISTENT_MODULES, nonexistentCodeSet));
        plannerAddCommand.execute(model, commandHistory);
    }

    @Test
    public void equals() {
        Year year = new Year("1");
        Semester semester = new Semester("1");
        Code code = new Code("CS1010");
        Set<Code> codeSet = new HashSet<>();
        codeSet.add(code);
        Code anotherCode = new Code("IS1103");
        Set<Code> anotherCodeSet = new HashSet<>();
        anotherCodeSet.add(anotherCode);

        PlannerAddCommand plannerAddACommand = new PlannerAddCommand(year, semester, codeSet);
        PlannerAddCommand plannerAddBCommand = new PlannerAddCommand(year, semester, anotherCodeSet);

        // same object -> returns true
        assertTrue(plannerAddACommand.equals(plannerAddACommand));

        // same values -> returns true
        PlannerAddCommand plannerAddACommandCopy = new PlannerAddCommand(year, semester, codeSet);
        assertTrue(plannerAddACommand.equals(plannerAddACommandCopy));

        // different types -> returns false
        assertFalse(plannerAddACommand.equals(1));

        // different module -> returns false
        assertFalse(plannerAddACommand.equals(plannerAddBCommand));
    }
}

