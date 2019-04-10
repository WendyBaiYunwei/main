package pwe.planner.logic.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static pwe.planner.logic.commands.CommandTestUtil.assertCommandFailure;
import static pwe.planner.logic.commands.CommandTestUtil.assertCommandSuccess;
import static pwe.planner.testutil.TypicalDegreePlanners.getTypicalDegreePlannerList;
import static pwe.planner.testutil.TypicalModules.getTypicalModuleList;
import static pwe.planner.testutil.TypicalRequirementCategories.getTypicalRequirementCategoriesList;

import java.util.HashSet;
import java.util.List;
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
import pwe.planner.model.planner.DegreePlanner;
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
                        getTypicalRequirementCategoriesList()).toModelType(), new UserPrefs());
    }

    @Test
    public void constructor_nullCodes_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        new PlannerRemoveCommand(null);
    }

    @Test
    public void execute_parametersAcceptedByModel_removeSuccessful() throws Exception {
        Code validCode = new Code("CS2100");
        Set<Code> validCodeSet = new HashSet<>();
        validCodeSet.add(validCode);
        Code anotherValidCode = new Code("CS1010");
        validCodeSet.add(anotherValidCode);
        Model model = new ModelManager(
                new JsonSerializableApplication(getTypicalModuleList(), getTypicalDegreePlannerList(),
                        getTypicalRequirementCategoriesList()).toModelType(), new UserPrefs());
        Model expectedModel = new ModelManager(
                new JsonSerializableApplication(getTypicalModuleList(), getTypicalDegreePlannerList(),
                        getTypicalRequirementCategoriesList()).toModelType(), new UserPrefs());

        List<DegreePlanner> degreePlannerList = expectedModel.getApplication().getDegreePlannerList();
        for (DegreePlanner selectedDegreeePlanner : degreePlannerList) {
            Set<Code> selectedCodeSet = new HashSet<>(selectedDegreeePlanner.getCodes());
            selectedCodeSet.removeAll(validCodeSet);
            DegreePlanner editedDegreePlanner = new DegreePlanner(selectedDegreeePlanner.getYear(),
                    selectedDegreeePlanner.getSemester(), selectedCodeSet);
            expectedModel.setDegreePlanner(selectedDegreeePlanner, editedDegreePlanner);
        }
        expectedModel.commitApplication();

        assertCommandSuccess(new PlannerRemoveCommand(validCodeSet), model, commandHistory,
                String.format(PlannerRemoveCommand.MESSAGE_SUCCESS, validCodeSet, "None"), expectedModel);
    }

    @Test
    public void execute_nonexistentPlannerCodes_throwsCommandException() throws Exception {
        Code nonexistentCode = new Code("CS9999");
        Set<Code> nonexistentCodeSet = new HashSet<>();
        nonexistentCodeSet.add(nonexistentCode);

        PlannerRemoveCommand plannerRemoveCommand = new PlannerRemoveCommand(nonexistentCodeSet);
        assertCommandFailure(plannerRemoveCommand, model, commandHistory,
                String.format(PlannerRemoveCommand.MESSAGE_NONEXISTENT_CODES, nonexistentCodeSet));
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

