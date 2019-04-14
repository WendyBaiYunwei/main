package pwe.planner.logic.commands;

import static java.util.Objects.requireNonNull;
import static pwe.planner.commons.util.CollectionUtil.requireAllNonNull;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_CODE;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_SEMESTER;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_YEAR;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import pwe.planner.logic.CommandHistory;
import pwe.planner.logic.commands.exceptions.CommandException;
import pwe.planner.model.Model;
import pwe.planner.model.module.Code;
import pwe.planner.model.module.Module;
import pwe.planner.model.planner.DegreePlanner;
import pwe.planner.model.planner.Semester;
import pwe.planner.model.planner.Year;

/**
 * Moves a module in the degree plan
 */
public class PlannerMoveCommand extends Command {

    public static final String COMMAND_WORD = "planner_move";

    // General command help details
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Moves a module along with its co-requisites to"
            + " the given year/semester of the degree plan.\n"
            + "Format: " + COMMAND_WORD + " "
            + PREFIX_YEAR + "YEAR "
            + PREFIX_SEMESTER + "SEMESTER "
            + PREFIX_CODE + "CODE\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_YEAR + "1 "
            + PREFIX_SEMESTER + "2 "
            + PREFIX_CODE + "CS1010";

    // Command success message
    public static final String MESSAGE_SUCCESS = "Successfully moved %1$s to Year %2$s Semester %3$s of "
            + "degree planner!\n" + "Co-Requisite(s) moved along: %4$s";

    // Command failure messages
    public static final String MESSAGE_NONEXISTENT_CODE =
            "You cannot move a module %1$s that does not exist in the degree plan!\n"
            + "Perhaps you were trying to add the module into the degree plan?\n"
            + "[Tip] You may want to try adding the module into the degree plan using the "
            + "\"planner_add\" command!";

    public static final String MESSAGE_NONEXISTENT_DEGREE_PLANNER =
            "You cannot move a module as Year %1$s Semester %2$s does not exist in the degree plan!\n"
            + "Perhaps you mistyped either year or semester?\n"
            + "[Tip] You may want to refer to the degree plan to see the semesters"
            + " available in the application using the \"planner_list\" command!";

    public static final String MESSAGE_UNAVAILABLE_SEMESTER =
            "You cannot move a module that is not offered in Semester %1$s\n"
            + "Perhaps you may want to check the module list for the semester(s) the module is offered in.\n"
            + "[Tip] You may want to refer to the degree plan to see the semesters"
            + " available in the application using the \"planner_list\" command!";

    public static final String MESSAGE_UNAVAILABLE_COREQUISITES =
            "You cannot move a module that has co-requisite(s) %1$s, which isn't offered in Semester %2$s\n"
            + "Perhaps, you may want to check the semester(s) the co-requisite modules are offered in.";

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

        Module moduleToMove = model.getModuleByCode(toMove);
        if (!moduleToMove.getSemesters().contains(destinationSemester)) {
            throw new CommandException(String.format(MESSAGE_UNAVAILABLE_SEMESTER, destinationSemester));
        }

        if (destinationPlanner == null) {
            throw new CommandException(
                    String.format(MESSAGE_NONEXISTENT_DEGREE_PLANNER, destinationYear, destinationSemester));
        }

        Set<Code> codesNotOffered = moduleToMove.getCorequisites().stream()
                .filter(corequisite -> !model.getModuleByCode(corequisite).getSemesters()
                        .contains(destinationPlanner.getSemester()))
                .collect(Collectors.toSet());

        if (!codesNotOffered.isEmpty()) {
            String codesNotOfferedContent =
                    codesNotOffered.stream().sorted().map(Code::toString).collect(Collectors.joining(", "));
            throw new CommandException(
                    String.format(MESSAGE_UNAVAILABLE_COREQUISITES, codesNotOfferedContent, destinationSemester));
        }

        model.moveModuleBetweenPlanner(sourcePlanner, destinationPlanner, toMove);

        Set<Code> corequisites = moduleToMove.getCorequisites();
        String corequisitesMoved = corequisites.isEmpty()
                ? "None"
                : corequisites.stream().sorted().map(Code::toString).collect(Collectors.joining(", "));

        model.commitApplication();
        return new CommandResult(
                String.format(MESSAGE_SUCCESS, toMove, destinationYear, destinationSemester, corequisitesMoved));
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
