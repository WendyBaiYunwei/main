package pwe.planner.logic.commands;

import static pwe.planner.logic.commands.CommandTestUtil.assertCommandSuccess;
import static pwe.planner.testutil.TypicalDegreePlanners.getTypicalDegreePlannerList;
import static pwe.planner.testutil.TypicalModules.getTypicalModuleList;
import static pwe.planner.testutil.TypicalRequirementCategories.getTypicalRequirementCategoriesList;

import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import javafx.collections.ObservableList;
import pwe.planner.commons.exceptions.IllegalValueException;
import pwe.planner.logic.CommandHistory;
import pwe.planner.model.Model;
import pwe.planner.model.ModelManager;
import pwe.planner.model.UserPrefs;
import pwe.planner.model.module.Code;
import pwe.planner.model.requirement.RequirementCategory;
import pwe.planner.storage.JsonSerializableApplication;

public class RequirementListCommandTest {

    private Model model;
    private Model expectedModel;
    private CommandHistory commandHistory = new CommandHistory();

    @Before public void setUp() throws IllegalValueException {
        model = new ModelManager(new JsonSerializableApplication(getTypicalModuleList(), getTypicalDegreePlannerList(),
                getTypicalRequirementCategoriesList()).toModelType(), new UserPrefs());
        expectedModel = new ModelManager(model.getApplication(), new UserPrefs());
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
