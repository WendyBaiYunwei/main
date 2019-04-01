package seedu.address.logic.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.testutil.TypicalDegreePlanners.getTypicalDegreePlannerList;
import static seedu.address.testutil.TypicalModules.getTypicalModuleList;
import static seedu.address.testutil.TypicalRequirementCategories.getTypicalRequirementCategoriesList;

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
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.JsonSerializableAddressBook;

public class PlannerAddCommandTest {

    @Rule public ExpectedException thrown = ExpectedException.none();

    private CommandHistory commandHistory = new CommandHistory();
    private Model model;
    private Set<Code> codeList = new HashSet<>();
    private Year year = new Year("1");
    private Semester semester = new Semester("1");

    @Before public void setUp() throws IllegalValueException {
        model = new ModelManager(
                new JsonSerializableAddressBook(getTypicalModuleList(), getTypicalDegreePlannerList(),
                        getTypicalRequirementCategoriesList())
                        .toModelType(), new UserPrefs());
    }

    @Test public void constructor_nullParameters_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        new PlannerAddCommand(null, null, null);
    }

    @Test
    public void execute_plannerCodesAcceptedByModel_addSuccessful() throws Exception {
        codeList.clear();
        codeList.addAll(SampleDataUtil.getCodeSet());
        CommandResult commandResult = new PlannerAddCommand(year, semester, codeList).execute(model, commandHistory);
        DegreePlanner currentDegreePlanner = model.getAddressBook().getDegreePlannerList().stream()
                .filter(degreePlanner -> (degreePlanner.getYear().equals(year)
                        && degreePlanner.getSemester().equals(semester)))
                .findFirst()
                .orElse(null);
        assertEquals(String.format(PlannerAddCommand.MESSAGE_SUCCESS, codeList), commandResult.getFeedbackToUser());
        assertEquals(codeList, currentDegreePlanner.getCodes());
        assertEquals(commandHistory, commandHistory);
    }

    @Test public void execute_nonExistentModuleListCode_throwsCommandException() {
        codeList.clear();
        codeList.add(new Code("CS2010"));
        assertCommandFailure(new PlannerAddCommand(year, semester, codeList), model, commandHistory,
                PlannerAddCommand.MESSAGE_MODULE_DOES_NOT_EXIST);
    }

    @Test public void execute_duplicatePlannerCode_throwsCommandException() {
        codeList.clear();
        Code code = new Code("CS1010");
        codeList.add(code);
        codeList.add(code);
        assertCommandFailure(new PlannerAddCommand(year, semester, codeList), model, commandHistory,
                PlannerAddCommand.MESSAGE_DUPLICATE_CODE);
    }

    @Test
    public void equals() {
        Set<Code> codeListCopy = new HashSet<>();
        Code code = new Code("CS2040C");
        Code codeCopy = new Code("CS2107");
        codeList.clear();
        codeListCopy.clear();
        codeList.add(code);
        codeListCopy.add(codeCopy);
        PlannerAddCommand plannerAddACommand = new PlannerAddCommand(year, semester, codeList);
        PlannerAddCommand plannerAddBCommand = new PlannerAddCommand(year, semester, codeListCopy);

        // same object -> returns true
        assertTrue(plannerAddACommand.equals(plannerAddACommand));

        // same values -> returns true
        PlannerAddCommand plannerAddACommandCopy = new PlannerAddCommand(year, semester, codeList);
        assertTrue(plannerAddACommand.equals(plannerAddACommandCopy));

        // different types -> returns false
        assertFalse(plannerAddACommand.equals(1));

        // null -> returns false
        assertFalse(plannerAddACommand.equals(null));

        // different module -> returns false
        assertFalse(plannerAddACommand.equals(plannerAddBCommand));
    }
}
