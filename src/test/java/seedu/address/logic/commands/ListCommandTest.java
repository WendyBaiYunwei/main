package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showModuleAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_MODULE;
import static seedu.address.testutil.TypicalModules.getTypicalModuleList;
import static seedu.address.testutil.TypicalRequirementCategories.getTypicalRequirementCategoriesList;

import org.junit.Before;
import org.junit.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.CommandHistory;
import seedu.address.model.DegreePlannerList;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.storage.JsonSerializableAddressBook;

/**
 * Contains integration tests (interaction with the Model) and unit tests for ListCommand.
 */
public class ListCommandTest {

    private Model model;
    private Model expectedModel;
    private CommandHistory commandHistory = new CommandHistory();

    @Before
    public void setUp() throws IllegalValueException {
        //ToDo: Implement getTypicalDegreePlannerList for DegreePlannerList and update the codes below
        model = new ModelManager(
                new JsonSerializableAddressBook(getTypicalModuleList(), getTypicalRequirementCategoriesList())
                        .toModelType(), new DegreePlannerList(),
                new UserPrefs());
        expectedModel = new ModelManager(model.getAddressBook(), model.getDegreePlannerList(),
                new UserPrefs());
    }

    @Test
    public void execute_listIsNotFiltered_showsSameList() {
        assertCommandSuccess(new ListCommand(), model, commandHistory, ListCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_listIsFiltered_showsEverything() {
        showModuleAtIndex(model, INDEX_FIRST_MODULE);
        assertCommandSuccess(new ListCommand(), model, commandHistory, ListCommand.MESSAGE_SUCCESS, expectedModel);
    }
}
