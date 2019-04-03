package pwe.planner.logic.commands;

import static java.util.Objects.requireNonNull;
import static pwe.planner.model.Model.PREDICATE_SHOW_ALL_REQUIREMENT_CATEGORIES;

import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import pwe.planner.logic.CommandHistory;
import pwe.planner.model.Model;
import pwe.planner.model.module.Code;
import pwe.planner.model.requirement.RequirementCategory;

/**
 * Lists all requirement categories in the application to the user.
 */
public class RequirementListCommand extends Command {

    public static final String COMMAND_WORD = "requirement_list";

    public static final String MESSAGE_SUCCESS = "Successfully listed all requirement categories: \n%1$s";

    @Override public CommandResult execute(Model model, CommandHistory history) {
        requireNonNull(model);
        model.updateFilteredRequirementCategoryList(PREDICATE_SHOW_ALL_REQUIREMENT_CATEGORIES);
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

        return new CommandResult(String.format(MESSAGE_SUCCESS, requirementListContent.toString()));

    }
}
