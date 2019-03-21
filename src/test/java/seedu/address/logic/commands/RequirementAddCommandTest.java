package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
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
import seedu.address.model.DegreePlannerList;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.module.Code;
import seedu.address.model.module.Credits;
import seedu.address.model.module.Name;
import seedu.address.model.requirement.RequirementCategory;
import seedu.address.storage.JsonSerializableAddressBook;

public class RequirementAddCommandTest {

    @Rule public ExpectedException thrown = ExpectedException.none();

    private CommandHistory commandHistory = new CommandHistory();
    private Model model;
    private Set<Code> codeList = new HashSet<>();

    @Before public void setUp() throws IllegalValueException {
        model = new ModelManager(
                new JsonSerializableAddressBook(getTypicalModuleList(), getTypicalRequirementCategoriesList())
                        .toModelType(), new DegreePlannerList(), new UserPrefs());
    }

    @Test public void constructor_nullModule_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        new RequirementAddCommand(null);
    }

    @Test public void execute_moduleToBeAddedDoesNotExist_throwsCommandException() {
        codeList.clear();
        codeList.add(new Code("CS2010"));
        RequirementCategory requirementCategory =
                new RequirementCategory(new Name("Computing Foundation"), new Credits("0"), codeList);
        assertCommandFailure(new RequirementAddCommand(requirementCategory), model, commandHistory,
                RequirementAddCommand.MESSAGE_MODULE_DOES_NOT_EXIST);
    }

}
