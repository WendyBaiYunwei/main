package pwe.planner.logic.commands;

import static java.util.Objects.requireNonNull;
import static pwe.planner.model.Model.PREDICATE_SHOW_ALL_DEGREE_PLANNERS;

import java.util.stream.Collectors;

import pwe.planner.logic.CommandHistory;
import pwe.planner.model.Model;
import pwe.planner.model.module.Code;
import pwe.planner.model.planner.DegreePlanner;

/**
 * Lists all degree planners in the application to the user.
 */
public class PlannerListAllCommand extends Command {

    public static final String COMMAND_WORD = "planner_list_all";

    public static final String MESSAGE_SUCCESS = "Listed all degree planners:\n%1$s";

    @Override
    public CommandResult execute(Model model, CommandHistory history) {
        requireNonNull(model);
        model.updateFilteredDegreePlannerList(PREDICATE_SHOW_ALL_DEGREE_PLANNERS);
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
        return new CommandResult(String.format(MESSAGE_SUCCESS, degreePlannerListContent.toString()));
    }
}
