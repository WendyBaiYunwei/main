package pwe.planner.logic.commands;

import static java.util.Objects.requireNonNull;
import static pwe.planner.model.Model.PREDICATE_SHOW_ALL_DEGREE_PLANNERS;

import java.util.stream.Collectors;

import pwe.planner.logic.CommandHistory;
import pwe.planner.model.Model;
import pwe.planner.model.planner.DegreePlanner;

/**
 * Lists all degree planners in the application to the user.
 */
public class PlannerListCommand extends Command {

    public static final String COMMAND_WORD = "planner_list";

    public static final String MESSAGE_SUCCESS = "Listed all degree planners:\n%1$s";

    @Override
    public CommandResult execute(Model model, CommandHistory history) {
        requireNonNull(model);

        model.updateFilteredDegreePlannerList(PREDICATE_SHOW_ALL_DEGREE_PLANNERS);
        String degreePlannerListContent = model.getApplication().getDegreePlannerList().stream()
                .map(DegreePlanner::toString)
                .collect(Collectors.joining("\n"));
        return new CommandResult(String.format(MESSAGE_SUCCESS, degreePlannerListContent));
    }
}
