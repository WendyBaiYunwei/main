package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalDegreePlanners.getTypicalDegreePlannerList;
import static seedu.address.testutil.TypicalModules.getTypicalModuleList;
import static seedu.address.testutil.TypicalRequirementCategories.getTypicalRequirementCategoriesList;

import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import javafx.collections.ObservableList;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.CommandHistory;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.module.Code;
import seedu.address.model.requirement.RequirementCategory;
import seedu.address.storage.JsonSerializableAddressBook;

public class RequirementListCommandTest {

    private Model model;
    private Model expectedModel;
    private CommandHistory commandHistory = new CommandHistory();

    @Before public void setUp() throws IllegalValueException {
        model = new ModelManager(new JsonSerializableAddressBook(getTypicalModuleList(), getTypicalDegreePlannerList(),
                getTypicalRequirementCategoriesList()).toModelType(), new UserPrefs());
        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
    }

    @Test public void execute_displayList() {
        StringBuilder requirementListContent = new StringBuilder();

        ObservableList<RequirementCategory> requirementCategories = model.getFilteredRequirementCategoryList();

        for (RequirementCategory requirementCategory : requirementCategories) {
            requirementListContent.append(requirementCategory.getName()).append(" ");

            int currentCredits = requirementCategory.getCodeSet().stream()
                    .map(code -> model.getModuleByCode(code).getCredits().toString())
                    .map(Integer::parseInt).reduce(0, (totalCredits, credit) -> totalCredits + credit);

            requirementListContent.append("(").append(currentCredits).append("/")
                    .append(requirementCategory.getCredits()).append(" Modular Credits Fulfilled) \n");

            if (requirementCategory.getCodeSet().isEmpty()) {
                requirementListContent.append("No modules added!");
            } else {
                requirementListContent.append("Modules: ").append(requirementCategory.getCodeSet().stream()
                        .map(Code::toString).sorted().collect(Collectors.joining(", ")));
            }

            requirementListContent.append("\n\n");
        }
        String expectedMessage =
                String.format(RequirementListCommand.MESSAGE_SUCCESS, requirementListContent.toString());
        assertCommandSuccess(new RequirementListCommand(), model, commandHistory, expectedMessage, expectedModel);
    }
}
