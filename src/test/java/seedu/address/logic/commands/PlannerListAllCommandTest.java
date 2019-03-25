package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalDegreePlanners.getTypicalDegreePlannerList;
import static seedu.address.testutil.TypicalModules.getTypicalModuleList;
import static seedu.address.testutil.TypicalRequirementCategories.getTypicalRequirementCategoriesList;

import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.CommandHistory;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.module.Code;
import seedu.address.model.planner.DegreePlanner;
import seedu.address.storage.JsonSerializableAddressBook;

/**
 * Contains integration tests (interaction with the Model) and unit tests for PlannerListAllCommand.
 */
public class PlannerListAllCommandTest {
    private Model model;
    private Model expectedModel;
    private CommandHistory commandHistory = new CommandHistory();

    @Before
    public void setUp() throws IllegalValueException {
        //ToDo: Implement getTypicalDegreePlannerList for DegreePlannerList and update the codes below
        model = new ModelManager(
                new JsonSerializableAddressBook(getTypicalModuleList(), getTypicalDegreePlannerList(),
                        getTypicalRequirementCategoriesList())
                        .toModelType(), new UserPrefs());
        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_plannerListIsNotFiltered_showsSameList() {
        StringBuilder degreePlannerListContent = new StringBuilder();
        for (DegreePlanner degreePlanner : model.getFilteredDegreePlannerList()) {
            degreePlannerListContent
                    .append("Year: " + degreePlanner.getYear() + " Semester: " + degreePlanner.getSemester() + "\n");
            if (degreePlanner.getCodes().isEmpty()) {
                degreePlannerListContent.append("No module inside");
            } else {
                degreePlannerListContent
                        .append("Modules: " + degreePlanner.getCodes().stream().map(Code::toString).collect(
                                Collectors.joining(", ")));
            }
            degreePlannerListContent.append("\n\n");
        }
        String expectedMessage =
                String.format(PlannerListAllCommand.MESSAGE_SUCCESS, degreePlannerListContent.toString());
        assertCommandSuccess(new PlannerListAllCommand(), model, commandHistory, expectedMessage,
                expectedModel);
    }

}
