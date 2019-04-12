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
 * Adds module(s) to the degree plan.
 * Related co-requisite(s) are added as well.
 */
public class PlannerAddCommand extends Command {

    public static final String COMMAND_WORD = "planner_add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds module(s) to the degree plan. "
            + "Parameters: "
            + PREFIX_YEAR + "YEAR "
            + PREFIX_SEMESTER + "SEMESTER "
            + PREFIX_CODE + "CODE "
            + "[" + PREFIX_CODE + "CODE]...\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_YEAR + "2 "
            + PREFIX_SEMESTER + "2 "
            + PREFIX_CODE + "CS2040C "
            + PREFIX_CODE + "CS2113T "
            + PREFIX_CODE + "CS2100";

    public static final String MESSAGE_SUCCESS = "Added new module(s) to year %1$s semester %2$s of"
            + " the degree plan: \n%3$s\nCo-requisite(s) added: %4$s";
    public static final String MESSAGE_DUPLICATE_CODE = "The module(s) %1$s already exists in the degree plan.";
    public static final String MESSAGE_INVALID_COREQ = "The co-requisite(s) %1$s of module(s) %2$s already exists"
            + " in a different year and semester of the degree plan.\nCo-requisite module(s)"
            + " have to be in the same year and semester of the degree plan.";
    public static final String MESSAGE_NONEXISTENT_MODULES = "The module(s) %1$s does not exist in the module list.";
    public static final String MESSAGE_NONEXISTENT_DEGREE_PLANNER = "Year %1$s Semester"
            + "%2$s does not exist in the degree plan!";
    private Year yearToAddTo;
    private Semester semesterToAddTo;
    private Set<Code> codesToAdd;

    /**
     * Creates a PlannerAddCommand to add the specified {@code codes} to the degree plan.
     */
    public PlannerAddCommand(Year year, Semester semester, Set<Code> codes) {
        requireAllNonNull(year, semester, codes);

        yearToAddTo = year;
        semesterToAddTo = semester;
        codesToAdd = codes;
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

        Set<Code> duplicatePlannerCodes = codesToAdd.stream().filter(codesToCheck -> model.getApplication()
                .getDegreePlannerList().stream().map(DegreePlanner::getCodes)
                .anyMatch(selectedPlannerCodes -> selectedPlannerCodes.contains(codesToCheck)))
                .collect(Collectors.toSet());
        if (duplicatePlannerCodes.size() > 0) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_CODE, duplicatePlannerCodes));
        }

        Set<Code> nonExistentModuleCodes = codesToAdd.stream()
                .filter(codesToCheck -> !model.hasModuleCode(codesToCheck)).collect(Collectors.toSet());
        if (nonExistentModuleCodes.size() > 0) {
            throw new CommandException(String.format(MESSAGE_NONEXISTENT_MODULES, nonExistentModuleCodes));
        }

        Set<Code> selectedCodeSet = new HashSet<>(selectedDegreePlanner.getCodes());
        Set<Code> coreqsAdded = new HashSet<>();

        for (Code codeToAdd : codesToAdd) {
            selectedCodeSet.add(codeToAdd);
            // Adds co-requisite(s).
            Module module = model.getModuleByCode(codeToAdd);

            // Returns the relevant duplicate co-requisite(s) in the entire degree plan.
            Set<Code> duplicateCoreqs = module.getCorequisites().stream().filter(coreqToCheck -> model.getApplication()
                    .getDegreePlannerList().stream().map(DegreePlanner::getCodes)
                    .anyMatch(selectedPlannerCodes -> selectedPlannerCodes.contains(coreqToCheck)))
                    .collect(Collectors.toSet());
            Set<Code> invalidDuplicateCoreqs = new HashSet<>(duplicateCoreqs);
            // Returns the invalid duplicate co-requisite(s) that exists in a different section of the degree plan.
            invalidDuplicateCoreqs.removeAll(selectedDegreePlanner.getCodes());
            if (invalidDuplicateCoreqs.size() > 0) {
                throw new CommandException(String.format(MESSAGE_INVALID_COREQ, invalidDuplicateCoreqs, codesToAdd));
            }

            coreqsAdded.addAll(module.getCorequisites());

            // Returns the valid duplicate co-requisite(s) that exists in the selected section of the degree plan.
            duplicateCoreqs.retainAll(selectedDegreePlanner.getCodes());
            coreqsAdded.removeAll(duplicateCoreqs);
            selectedCodeSet.addAll(coreqsAdded);
        }

        coreqsAdded.removeAll(codesToAdd);

        DegreePlanner editedDegreePlanner = new DegreePlanner(yearToAddTo, semesterToAddTo, selectedCodeSet);
        model.setDegreePlanner(selectedDegreePlanner, editedDegreePlanner);
        model.commitApplication();

        return new CommandResult(String.format(MESSAGE_SUCCESS, yearToAddTo, semesterToAddTo,
                codesToAdd, coreqsAdded.isEmpty() ? "None" : coreqsAdded));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof PlannerAddCommand // instanceof handles nulls
                && yearToAddTo.equals(((PlannerAddCommand) other).yearToAddTo)
                && semesterToAddTo.equals(((PlannerAddCommand) other).semesterToAddTo)
                && codesToAdd.equals(((PlannerAddCommand) other).codesToAdd));
    }
}

