package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
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
import seedu.address.model.module.Module;
import seedu.address.storage.JsonSerializableAddressBook;
import seedu.address.testutil.ModuleBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code AddCommand}.
 */
public class AddCommandIntegrationTest {

    private Model model;
    private CommandHistory commandHistory = new CommandHistory();

    @Before
    public void setUp() throws IllegalValueException {
        //ToDo: Implement getTypicalDegreePlannerList for DegreePlannerList and update the codes below
        model = new ModelManager(
                new JsonSerializableAddressBook(getTypicalModuleList(), getTypicalRequirementCategoriesList())
                        .toModelType(), new DegreePlannerList(),
                new UserPrefs());
    }

    @Test
    public void execute_newModule_success() {
        Module validModule = new ModuleBuilder().build();
        Model expectedModel = new ModelManager(model.getAddressBook(), model.getDegreePlannerList(),
                new UserPrefs());
        expectedModel.addModule(validModule);
        expectedModel.commitAddressBook();

        assertCommandSuccess(new AddCommand(validModule), model, commandHistory,
                String.format(AddCommand.MESSAGE_SUCCESS, validModule), expectedModel);
    }

    @Test
    public void execute_duplicateModule_throwsCommandException() {
        Module moduleInList = model.getAddressBook().getModuleList().get(0);
        assertCommandFailure(new AddCommand(moduleInList), model, commandHistory,
                AddCommand.MESSAGE_DUPLICATE_MODULE);
    }

}
