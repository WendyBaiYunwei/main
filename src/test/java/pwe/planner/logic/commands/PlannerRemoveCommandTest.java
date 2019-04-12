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
import pwe.planner.storage.JsonSerializableApplication;

public class PlannerRemoveCommandTest {
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
    public void constructor_nullCodes_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        new PlannerRemoveCommand(null);
    }

    @Test
    public void execute_parametersAcceptedByModel_removeSuccessful() {
        Set<Code> validCodeSet = Set.of(new Code("CS2100"), new Code("CS1010"));

        Model expectedModel = new ModelManager(model.getApplication(), new UserPrefs());

        List<DegreePlanner> degreePlannerList = expectedModel.getApplication().getDegreePlannerList();
        for (DegreePlanner selectedDegreeePlanner : degreePlannerList) {
            Set<Code> selectedCodeSet = new HashSet<>(selectedDegreeePlanner.getCodes());
            selectedCodeSet.removeAll(validCodeSet);
            DegreePlanner editedDegreePlanner = new DegreePlanner(selectedDegreeePlanner.getYear(),
                    selectedDegreeePlanner.getSemester(), selectedCodeSet);
            expectedModel.setDegreePlanner(selectedDegreeePlanner, editedDegreePlanner);
        }
        expectedModel.commitApplication();

        String removedCodesString = validCodeSet.stream().map(Code::toString).collect(Collectors.joining(", "));

        assertCommandSuccess(new PlannerRemoveCommand(validCodeSet), model, commandHistory,
                String.format(PlannerRemoveCommand.MESSAGE_SUCCESS, removedCodesString, "None"), expectedModel);
    }

    @Test
    public void execute_nonexistentPlannerCodes_throwsCommandException() {
        Set<Code> nonexistentCodeSet = Set.of(new Code("CS9999"));

        String nonexistentCodesString = nonexistentCodeSet.stream().map(Code::toString)
                .collect(Collectors.joining(", "));

        PlannerRemoveCommand plannerRemoveCommand = new PlannerRemoveCommand(nonexistentCodeSet);
        assertCommandFailure(plannerRemoveCommand, model, commandHistory,
                String.format(PlannerRemoveCommand.MESSAGE_NONEXISTENT_CODES, nonexistentCodesString));
    }

    @Test
    public void equals() {
        Set<Code> codeSet = Set.of(new Code("CS1010"));
        Set<Code> anotherCodeSet = Set.of(new Code("IS1103"));

        PlannerRemoveCommand plannerRemoveCommand = new PlannerRemoveCommand(codeSet);
        PlannerRemoveCommand plannerRemoveCommandToCompare = new PlannerRemoveCommand(anotherCodeSet);

        // same object -> returns true
        assertTrue(plannerRemoveCommand.equals(plannerRemoveCommand));

        // same values -> returns true
        PlannerRemoveCommand plannerAddACommandCopy = new PlannerRemoveCommand(codeSet);
        assertTrue(plannerRemoveCommand.equals(plannerAddACommandCopy));

        // different types -> returns false
        assertFalse(plannerRemoveCommand.equals(1));

        // different module -> returns false
        assertFalse(plannerRemoveCommand.equals(plannerRemoveCommandToCompare));
    }
}

