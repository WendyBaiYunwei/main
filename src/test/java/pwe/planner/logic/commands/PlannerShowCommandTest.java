package pwe.planner.logic.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static pwe.planner.logic.commands.CommandTestUtil.assertCommandSuccess;
import static pwe.planner.testutil.TypicalDegreePlanners.YEAR_1_SEMESTER_1;
import static pwe.planner.testutil.TypicalDegreePlanners.YEAR_1_SEMESTER_2;
import static pwe.planner.testutil.TypicalDegreePlanners.YEAR_2_SEMESTER_1;
import static pwe.planner.testutil.TypicalDegreePlanners.YEAR_3_SEMESTER_1;
import static pwe.planner.testutil.TypicalDegreePlanners.getTypicalDegreePlannerList;
import static pwe.planner.testutil.TypicalModules.getTypicalModuleList;
import static pwe.planner.testutil.TypicalRequirementCategories.getTypicalRequirementCategoriesList;

import java.util.Arrays;

import org.junit.Test;

import pwe.planner.commons.exceptions.IllegalValueException;
import pwe.planner.logic.CommandHistory;
import pwe.planner.model.Model;
import pwe.planner.model.ModelManager;
import pwe.planner.model.UserPrefs;
import pwe.planner.model.planner.DegreePlanner;
import pwe.planner.model.planner.SemesterContainsKeywordPredicate;
import pwe.planner.model.planner.YearContainsKeywordPredicate;
import pwe.planner.storage.JsonSerializableApplication;

public class PlannerShowCommandTest {
    private Model model = new ModelManager(
            new JsonSerializableApplication(getTypicalModuleList(), getTypicalDegreePlannerList(),
                    getTypicalRequirementCategoriesList()).toModelType(), new UserPrefs());
    private Model expectedModel = new ModelManager(
            new JsonSerializableApplication(getTypicalModuleList(), getTypicalDegreePlannerList(),
                    getTypicalRequirementCategoriesList()).toModelType(), new UserPrefs());
    private CommandHistory commandHistory = new CommandHistory();

    public PlannerShowCommandTest() throws IllegalValueException {}

    @Test
    public void execute_validYear_success() {
        YearContainsKeywordPredicate<DegreePlanner> predicate = prepareYearPredicate("1");
        PlannerShowCommand command = new PlannerShowCommand(predicate);
        expectedModel.updateFilteredDegreePlannerList(predicate);
        String expectedMessage =
                String.format(PlannerShowCommand.MESSAGE_SUCCESS, expectedModel.getFilteredDegreePlannerList().size());
        assertCommandSuccess(command, model, commandHistory, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(YEAR_1_SEMESTER_1, YEAR_1_SEMESTER_2), model.getFilteredDegreePlannerList());
    }

    @Test
    public void execute_validSemester_success() {
        SemesterContainsKeywordPredicate<DegreePlanner> predicate = prepareSemesterPredicate("1");
        PlannerShowCommand command = new PlannerShowCommand(predicate);
        expectedModel.updateFilteredDegreePlannerList(predicate);
        String expectedMessage =
                String.format(PlannerShowCommand.MESSAGE_SUCCESS, expectedModel.getFilteredDegreePlannerList().size());
        assertCommandSuccess(command, model, commandHistory, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(YEAR_1_SEMESTER_1, YEAR_2_SEMESTER_1, YEAR_3_SEMESTER_1),
                model.getFilteredDegreePlannerList());
    }

    @Test
    public void equals() {
        YearContainsKeywordPredicate<DegreePlanner> firstPredicate = prepareYearPredicate("1");
        SemesterContainsKeywordPredicate<DegreePlanner> secondPredicate = prepareSemesterPredicate("1");

        PlannerShowCommand plannerShowFirstCommand = new PlannerShowCommand(firstPredicate);
        PlannerShowCommand plannerShowSecondCommand = new PlannerShowCommand(secondPredicate);

        // same object -> returns true
        assertTrue(plannerShowFirstCommand.equals(plannerShowFirstCommand));

        // same values -> returns true
        PlannerShowCommand plannerShowFirstCommandCopy = new PlannerShowCommand(firstPredicate);
        assertTrue(plannerShowFirstCommand.equals(plannerShowFirstCommandCopy));

        // different types -> returns false
        assertFalse(plannerShowFirstCommand.equals(1));

        // different module -> returns false
        assertFalse(plannerShowFirstCommand.equals(plannerShowSecondCommand));
    }

    /**
     * Parses {@code userInput} into a {@code YearContainsKeywordPredicate}.
     */
    private YearContainsKeywordPredicate<DegreePlanner> prepareYearPredicate(String userInput) {
        return new YearContainsKeywordPredicate<>(userInput);
    }

    /**
     * Parses {@code userInput} into a {@code SemesterContainsKeywordPredicate}.
     */
    private SemesterContainsKeywordPredicate<DegreePlanner> prepareSemesterPredicate(String userInput) {
        return new SemesterContainsKeywordPredicate<>(userInput);
    }
}
