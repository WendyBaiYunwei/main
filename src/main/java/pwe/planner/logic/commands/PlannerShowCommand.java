package pwe.planner.logic.commands;

import static java.util.Objects.requireNonNull;
import static pwe.planner.logic.parser.CliSyntax.OPERATOR_AND;
import static pwe.planner.logic.parser.CliSyntax.OPERATOR_LEFT_BRACKET;
import static pwe.planner.logic.parser.CliSyntax.OPERATOR_OR;
import static pwe.planner.logic.parser.CliSyntax.OPERATOR_RIGHT_BRACKET;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_SEMESTER;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_YEAR;

import java.util.function.Predicate;

import pwe.planner.logic.CommandHistory;
import pwe.planner.model.Model;
import pwe.planner.model.planner.DegreePlanner;

/**
 * Shows all semesters in degree plan that have year and semester contains any of the argument keywords.
 * Keyword matching is case insensitive.
 */
public class PlannerShowCommand extends Command {
    public static final String COMMAND_WORD = "planner_show";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Shows all semesters which satisfies the expression of search conditions "
            + "specified based on year and/or semester in the degree plan\n"
            + "Format: " + COMMAND_WORD
            + " [" + PREFIX_YEAR + "YEAR] "
            + "OPERATOR "
            + "[" + PREFIX_SEMESTER + "SEMESTER]\n"
            + "OPERATOR " + OPERATOR_AND
            + " for logical \"AND\" operation (both conditions A AND B must match)\n"
            + "OPERATOR " + OPERATOR_OR
            + " for logical \"OR\" operation (either conditions A OR B must match)\n"
            + "[Tip] You can also use parenthesis to group which search conditions to be evaluated first.\n"
            + "Example 1: " + COMMAND_WORD + " " + PREFIX_YEAR + "1 " + OPERATOR_AND + " "
            + PREFIX_SEMESTER + "2\n"
            + "Example 2: " + COMMAND_WORD + " " + PREFIX_YEAR + "1 " + OPERATOR_OR + " " + PREFIX_YEAR + "2\n"
            + "Example 3: " + COMMAND_WORD + " " + PREFIX_YEAR + "1 " + OPERATOR_AND + " "
            + OPERATOR_LEFT_BRACKET + " " + PREFIX_SEMESTER + "1 " + OPERATOR_OR + " " + PREFIX_SEMESTER + "2 "
            + OPERATOR_RIGHT_BRACKET + " ";

    public static final String MESSAGE_SUCCESS = "Number of semesters showed: %1$s";

    private final Predicate<DegreePlanner> predicate;

    public PlannerShowCommand(Predicate<DegreePlanner> predicate) {
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) {
        requireNonNull(model);

        model.updateFilteredDegreePlannerList(predicate);

        return new CommandResult(
                String.format(MESSAGE_SUCCESS, model.getFilteredDegreePlannerList().size()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof PlannerShowCommand)) {
            return false;
        }

        PlannerShowCommand otherCommand = (PlannerShowCommand) other;
        return predicate.equals(otherCommand.predicate);
    }
}
