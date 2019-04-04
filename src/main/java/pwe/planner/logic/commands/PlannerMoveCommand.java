package pwe.planner.logic.commands;

import static java.util.Objects.requireNonNull;
import static pwe.planner.commons.util.CollectionUtil.requireAllNonNull;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_CODE;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_SEMESTER;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_YEAR;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import pwe.planner.logic.CommandHistory;
import pwe.planner.logic.commands.exceptions.CommandException;
import pwe.planner.model.Model;
import pwe.planner.model.module.Code;
import pwe.planner.model.planner.DegreePlanner;
import pwe.planner.model.planner.Semester;
import pwe.planner.model.planner.Year;

/**
 * Moves a module in the degree plan
 */
public class PlannerMoveCommand extends Command {

    public static final String COMMAND_WORD = "planner_move";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Moves a module in a degree plan. "
            + "Parameters: "
            + PREFIX_YEAR + "YEAR "
            + PREFIX_SEMESTER + "SEMESTER\n"
            + PREFIX_CODE + "CODE "
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_YEAR + "1 "
            + PREFIX_SEMESTER + "2 "
            + PREFIX_CODE + "CS1010";

    public static final String MESSAGE_SUCCESS =
            "Successfully moved %1$s to Year %2$s Semester %3$s of the degree plan!";
    public static final String MESSAGE_NONEXISTENT_CODE =
            "The module %1$s does not exist in the degree plan!";
    public static final String MESSAGE_NONEXISTENT_DEGREE_PLANNER =
            "Year %1$s Semester %2$s does not exist in the degree planner!";

    private final Year destinationYear;
    private final Semester destinationSemester;
    private final Code toMove;

    /**
     * Creates an PlannerMoveCommand to add the specified {@code PlannerMoveCommand}
     */
    public PlannerMoveCommand(Year year, Semester semester, Code code) {
        requireAllNonNull(year, semester, code);

        this.destinationYear = year;
        this.destinationSemester = semester;
        this.toMove = code;
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {
        requireNonNull(model);

        DegreePlanner toFind = new DegreePlanner(destinationYear, destinationSemester, Collections.emptySet());
        DegreePlanner sourcePlanner = model.getDegreePlannerByCode(toMove);
        DegreePlanner destinationPlanner = model.getApplication().getDegreePlannerList().stream()
                .filter(toFind::isSameDegreePlanner)
                .findFirst()
                .orElse(null);

        if (sourcePlanner == null) {
            throw new CommandException(String.format(MESSAGE_NONEXISTENT_CODE, toMove));
        }

        if (destinationPlanner == null) {
            throw new CommandException(
                    String.format(MESSAGE_NONEXISTENT_DEGREE_PLANNER, destinationYear, destinationSemester));
        }

        if (!sourcePlanner.isSameDegreePlanner(destinationPlanner)) {
            Set<Code> newSourceCodes = new HashSet<>(sourcePlanner.getCodes());
            newSourceCodes.remove(toMove);
            Set<Code> newDestinationCodes = new HashSet<>(destinationPlanner.getCodes());
            newDestinationCodes.add(toMove);

            DegreePlanner editedSourcePlanner =
                    new DegreePlanner(sourcePlanner.getYear(), sourcePlanner.getSemester(), newSourceCodes);
            DegreePlanner editedDestinationPlanner =
                    new DegreePlanner(destinationPlanner.getYear(), destinationPlanner.getSemester(),
                            newDestinationCodes);

            model.setDegreePlanner(sourcePlanner, editedSourcePlanner);
            model.setDegreePlanner(destinationPlanner, editedDestinationPlanner);
        }

        model.commitApplication();
        return new CommandResult(String.format(MESSAGE_SUCCESS, toMove, destinationYear, destinationSemester));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof PlannerMoveCommand)) {
            return false;
        }

        PlannerMoveCommand otherCommand = (PlannerMoveCommand) other;
        return destinationYear.equals(otherCommand.destinationYear)
                && destinationSemester.equals(otherCommand.destinationSemester)
                && toMove.equals(otherCommand.toMove);
    }

}
