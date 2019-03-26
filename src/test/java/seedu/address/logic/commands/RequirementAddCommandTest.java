package seedu.address.logic.commands;

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
import seedu.address.model.module.Name;
import seedu.address.storage.JsonSerializableAddressBook;

public class RequirementAddCommandTest {

    @Rule public ExpectedException thrown = ExpectedException.none();

    private CommandHistory commandHistory = new CommandHistory();
    private Model model;
    private Set<Code> codeList = new HashSet<>();

    @Before public void setUp() throws IllegalValueException {
        model = new ModelManager(
                new JsonSerializableAddressBook(getTypicalModuleList(), getTypicalDegreePlannerList(),
                        getTypicalRequirementCategoriesList())
                        .toModelType(), new UserPrefs());
    }

    @Test public void constructor_nullModule_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        new RequirementAddCommand(null, null);
    }

    @Test public void execute_nonExistentRequirementCategory_throwsCommandException() {
        codeList.clear();
        Name nonExistentRequirementCategoryName = new Name("DOES NOT EXIST");
        assertCommandFailure(new RequirementAddCommand(nonExistentRequirementCategoryName, codeList),
                model, commandHistory, String.format(RequirementAddCommand.MESSAGE_NONEXISTENT_REQUIREMENT_CATEGORY,
                        nonExistentRequirementCategoryName));
    }

    @Test public void execute_nonExistentCode_throwsCommandException() {
        codeList.clear();
        codeList.add(new Code("CS2010"));
        Name requirementCategoryName = new Name("Computing Foundation");
        assertCommandFailure(new RequirementAddCommand(requirementCategoryName, codeList), model, commandHistory,
                RequirementAddCommand.MESSAGE_NONEXISTENT_CODE);
    }

    @Test public void execute_duplicateCode_throwsCommandException() {
        codeList.clear();
        codeList.add(new Code("CS2100"));
        Name requirementCategoryName = new Name("Computing Foundation");
        assertCommandFailure(new RequirementAddCommand(requirementCategoryName, codeList), model, commandHistory,
                String.format(RequirementAddCommand.MESSAGE_DUPLICATE_CODE,
                        requirementCategoryName));
    }

}
