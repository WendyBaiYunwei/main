package pwe.planner.logic.commands;

import static java.util.Objects.requireNonNull;
import static pwe.planner.commons.util.CollectionUtil.requireAllNonNull;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_CODE;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_SEMESTER;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_YEAR;

import java.util.Collections;
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

    public static final String MESSAGE_SUCCESS = "Successfully added new module(s) to year %1$s semester %2$s of"
            + " the degree plan: %3$s\nCo-requisite(s) added: %4$s";
    public static final String MESSAGE_DUPLICATE_CODE = "You cannot add the module(s) %1$s, as the module code(s) %1$s"
            + " already exists in the degree plan!";
    public static final String MESSAGE_INVALID_COREQ = "You cannot add the module(s) %2$s, as the co-requisite(s) %1$s "
            + "of module(s) %2$s already exists in a different year and semester of the degree plan.\n"
            + "Co-requisite module(s) have to be in the same year and semester of the degree plan.";
    public static final String MESSAGE_NONEXISTENT_MODULES = "You cannot add module(s) %1$s that does not exist in the "
            + "module list.\n [Tip] Maybe you want to review the module list and select module(s) from there.";
    public static final String MESSAGE_NONEXISTENT_DEGREE_PLANNER = "You cannot add to year %1$s semester "
            + "%2$s as it does not exist in the degree plan.";
    public static final String MESSAGE_CODE_INVALID_SEMESTER = "You cannot add the module(s) as module(s) %1$s is not "
            + "offered in semester %2$s.\n[Tip] Maybe you can review the semesters of the modules "
            + ", and perhaps use edit command to amend.";
    public static final String MESSAGE_COREQ_INVALID_SEMESTER = "You cannot add the module(s) as the co-requisite(s) "
            + "%1$s of module(s) %2$s is not offered in semester %3$s!\n[Tip] Maybe you can review the semesters of the"
            + " modules, and perhaps use edit command to amend.";
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
        if (!duplicatePlannerCodes.isEmpty()) {
            // Converts a set to a string to remove the brackets of set.
            String duplicatePlannerCodeString = duplicatePlannerCodes.stream().map(Code::toString)
                    .collect(Collectors.joining(", "));
            throw new CommandException(String.format(MESSAGE_DUPLICATE_CODE, duplicatePlannerCodeString));
        }

        Set<Code> nonExistentModuleCodes = codesToAdd.stream()
                .filter(codeToCheck -> !model.hasModuleCode(codeToCheck)).collect(Collectors.toSet());
        if (!nonExistentModuleCodes.isEmpty()) {
            String nonExistentModuleString = nonExistentModuleCodes.stream().map(Code::toString)
                    .collect(Collectors.joining(", "));
            throw new CommandException(String.format(MESSAGE_NONEXISTENT_MODULES, nonExistentModuleString));
        }

        Set<Code> invalidSemesterCodes = codesToAdd.stream().filter(codeToCheck -> !model.getModuleByCode(codeToCheck)
                        .getSemesters().contains(semesterToAddTo)).collect(Collectors.toSet());
        if (!invalidSemesterCodes.isEmpty()) {
            String invalidSemCodesString = invalidSemesterCodes.stream().map(Code::toString)
                    .collect(Collectors.joining(", "));
            throw new CommandException(String.format(MESSAGE_CODE_INVALID_SEMESTER, invalidSemCodesString,
                    semesterToAddTo));
        }

        // Returns the co-requisite(s) of codes to add.
        Set<Code> coreqsOfCodesToAdd = new HashSet<>();
        codesToAdd.stream().map(model::getModuleByCode).map(Module::getCorequisites)
                .forEach(coreqsOfCodesToAdd::addAll);
        // Returns the invalid co-requisite(s) of codes to add.
        Set<Code> invalidSemesterCoreqs = coreqsOfCodesToAdd.stream()
                .filter(codeToCheck -> !model.getModuleByCode(codeToCheck).getSemesters().contains(semesterToAddTo))
                    .collect(Collectors.toSet());
        if (!invalidSemesterCoreqs.isEmpty()) {
            // Returns the codes to add that has invalid co-requisite(s).
            Set<Code> invalidCodesToAdd = codesToAdd.stream()
                    .filter(codeToCheck -> !Collections.disjoint(model.getModuleByCode(codeToCheck)
                            .getCorequisites(), invalidSemesterCoreqs)).collect(Collectors.toSet());

            String invalidCodeString = invalidCodesToAdd.stream().map(Code::toString)
                    .collect(Collectors.joining(", "));
            String invalidSemCoreqsString = invalidSemesterCoreqs.stream().map(Code::toString)
                    .collect(Collectors.joining(", "));

            throw new CommandException(String.format(MESSAGE_COREQ_INVALID_SEMESTER, invalidSemCoreqsString,
                    invalidCodeString, semesterToAddTo));
        }

        Set<Code> selectedCodeSet = new HashSet<>(selectedDegreePlanner.getCodes());
        Set<Code> coreqsAdded = new HashSet<>();

        for (Code codeToAdd : codesToAdd) {
            selectedCodeSet.add(codeToAdd);
            // Gets the module for the current code to add.
            Module module = model.getModuleByCode(codeToAdd);

            // Returns the relevant duplicate co-requisite(s) of the code to add in the entire degree plan.
            Set<Code> duplicateCoreqs = module.getCorequisites().stream().filter(coreqToCheck -> model.getApplication()
                    .getDegreePlannerList().stream().map(DegreePlanner::getCodes)
                    .anyMatch(selectedPlannerCodes -> selectedPlannerCodes.contains(coreqToCheck)))
                    .collect(Collectors.toSet());
            Set<Code> invalidCoreqs = new HashSet<>(duplicateCoreqs);
            // Returns the invalid duplicate co-requisite(s) that exists in a different section of the degree plan.
            invalidCoreqs.removeAll(selectedDegreePlanner.getCodes());
            if (!invalidCoreqs.isEmpty()) {
                String invalidCoreqsString = invalidCoreqs.stream().map(Code::toString)
                        .collect(Collectors.joining(", "));
                String codesToAddString = codesToAdd.stream().map(Code::toString)
                        .collect(Collectors.joining(", "));
                throw new CommandException(String.format(MESSAGE_INVALID_COREQ, invalidCoreqsString, codesToAddString));
            }

            coreqsAdded.addAll(module.getCorequisites());

            // Returns the valid duplicate co-requisite(s) that exists in the selected section of the degree plan.
            duplicateCoreqs.retainAll(selectedDegreePlanner.getCodes());
            // Records the co-requisites added for feedback to users.
            coreqsAdded.removeAll(duplicateCoreqs);
            selectedCodeSet.addAll(coreqsAdded);
        }

        coreqsAdded.removeAll(codesToAdd);

        DegreePlanner editedDegreePlanner = new DegreePlanner(yearToAddTo, semesterToAddTo, selectedCodeSet);
        model.setDegreePlanner(selectedDegreePlanner, editedDegreePlanner);
        model.commitApplication();

        String codesToAddString = codesToAdd.stream().map(Code::toString).collect(Collectors.joining(", "));
        String coreqsAddedString = coreqsAdded.stream().map(Code::toString).collect(Collectors.joining(", "));

        return new CommandResult(String.format(MESSAGE_SUCCESS, yearToAddTo, semesterToAddTo,
                codesToAddString, coreqsAdded.isEmpty() ? "None" : coreqsAddedString));
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

