package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_DEGREE_PLANNERS;

import java.util.stream.Collectors;

import seedu.address.logic.CommandHistory;
import seedu.address.model.Model;
import seedu.address.model.module.Code;
import seedu.address.model.planner.DegreePlanner;

/**
 * Lists all degree planners in the address book to the user.
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
