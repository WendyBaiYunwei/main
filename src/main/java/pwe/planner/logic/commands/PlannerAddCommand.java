package pwe.planner.logic.commands;

import static java.util.Objects.requireNonNull;
import static pwe.planner.commons.util.CollectionUtil.requireAllNonNull;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_CODE;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_SEMESTER;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_YEAR;

import java.util.HashSet;
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
 * Adds a module to the degree plan.
 * Related Co-requisite(s) are added as well.
 */
public class PlannerAddCommand extends Command {

    public static final String COMMAND_WORD = "planner_add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a module to the degree plan. "
            + "Parameters: "
            + PREFIX_YEAR + "YEAR "
            + PREFIX_SEMESTER + "SEMESTER "
            + PREFIX_CODE + "CODE\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_YEAR + "2 "
            + PREFIX_SEMESTER + "2 "
            + PREFIX_CODE + "CS2040C ";

    public static final String MESSAGE_SUCCESS = "Added new module to year %1$s semester %2$s of"
            + " the degree plan: \n%3$s\nCo-requisite(s) added:\n%4$s";
    public static final String MESSAGE_DUPLICATE_CODE = "The module %1$s already exists in the degree plan.";
    public static final String MESSAGE_DUPLICATE_COREQ = "The Co-requisite(s) %1$s of module %2$s already exists"
            + " in a different year and semester of the degree plan.\nModules that are Co-requisites to each other"
            + " have to be in the same year and semester of the degree plan.";
    public static final String MESSAGE_NONEXISTENT_MODULES = "The module %1$s does not exist in the module list.";
    public static final String MESSAGE_NONEXISTENT_DEGREE_PLANNER = "The degree plan of year %1$s and semester"
            + "%2$s does not exist.";
    private Year yearToAddTo;
    private Semester semesterToAddTo;
    private Code codeToAdd;

    /**
     * Creates a PlannerAddCommand to add the specified {@code code} to the degree plan.
     */
    public PlannerAddCommand(Year year, Semester semester, Code code) {
        requireAllNonNull(year, semester, code);
        yearToAddTo = year;
        semesterToAddTo = semester;
        codeToAdd = code;
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {
        requireNonNull(model);

        DegreePlanner selectedDegreePlanner = model.getApplication().getDegreePlannerList().stream()
                .filter(degreePlanner -> (degreePlanner.getYear().equals(yearToAddTo)
                        && degreePlanner.getSemester().equals(semesterToAddTo))).findFirst().orElse(null);
        if (selectedDegreePlanner == null) {
            throw new CommandException(String.format(MESSAGE_NONEXISTENT_DEGREE_PLANNER, yearToAddTo, semesterToAddTo));
        }

        boolean isDuplicatePlannerCode = model.getApplication().getDegreePlannerList().stream()
                .map(DegreePlanner::getCodes).anyMatch(selectedPlannerCodes -> selectedPlannerCodes
                        .contains(codeToAdd));
        if (isDuplicatePlannerCode) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_CODE, codeToAdd));
        }

        if (!model.hasModuleCode(codeToAdd)) {
            throw new CommandException(String.format(MESSAGE_NONEXISTENT_MODULES, codeToAdd));
        }

        Set<Code> selectedCodeSet = new HashSet<>(selectedDegreePlanner.getCodes());
        Set<Code> coreqsAdded = new HashSet<>();

        selectedCodeSet.add(codeToAdd);
        // Adds Co-requisite(s).
        Module module = model.getModuleByCode(codeToAdd);

        // Returns the relevant duplicate Co-requisite(s) in the entire degree plan.
        Set<Code> duplicateCoreqs = module.getCorequisites().stream().filter(coreqToCheck -> model.getApplication()
                .getDegreePlannerList().stream().map(DegreePlanner::getCodes)
                .anyMatch(selectedPlannerCodes -> selectedPlannerCodes.contains(coreqToCheck)))
                .collect(Collectors.toSet());
        Set<Code> invalidDuplicateCoreqs = new HashSet<>(duplicateCoreqs);
        // Returns the invalid duplicate Co-requisite(s) that exists in a different section of the degree plan.
        invalidDuplicateCoreqs.removeAll(selectedDegreePlanner.getCodes());
        if (invalidDuplicateCoreqs.size() > 0) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_COREQ, invalidDuplicateCoreqs, codeToAdd));
        }

        coreqsAdded.addAll(module.getCorequisites());

        // Returns the valid duplicate Co-requisite(s) that exists in the selected section of the degree plan.
        duplicateCoreqs.retainAll(selectedDegreePlanner.getCodes());
        coreqsAdded.removeAll(duplicateCoreqs);
        selectedCodeSet.addAll(coreqsAdded);
        coreqsAdded.remove(codeToAdd);

        DegreePlanner editedDegreePlanner = new DegreePlanner(yearToAddTo, semesterToAddTo, selectedCodeSet);
        model.setDegreePlanner(selectedDegreePlanner, editedDegreePlanner);
        model.commitApplication();

        if (coreqsAdded.size() > 0) {
            return new CommandResult(String.format(MESSAGE_SUCCESS, yearToAddTo, semesterToAddTo,
                    codeToAdd, coreqsAdded));
        } else {
            return new CommandResult(String.format(MESSAGE_SUCCESS, yearToAddTo, semesterToAddTo, codeToAdd,
                    "None"));
        }
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof PlannerAddCommand // instanceof handles nulls
                && yearToAddTo.equals(((PlannerAddCommand) other).yearToAddTo)
                && semesterToAddTo.equals(((PlannerAddCommand) other).semesterToAddTo)
                && codeToAdd.equals(((PlannerAddCommand) other).codeToAdd));
    }
}
