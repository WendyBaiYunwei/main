package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import static seedu.address.logic.parser.CliSyntax.PREFIX_CODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SEMESTER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_YEAR;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.logic.CommandHistory;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.module.Code;
import seedu.address.model.module.Module;
import seedu.address.model.planner.DegreePlanner;
import seedu.address.model.planner.Semester;
import seedu.address.model.planner.Year;

/**
 * Adds module(s) to the degree plan.
 * Related Co-requisite(s) are added as well.
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

    private static final String MESSAGE_SUCCESS = "Added new module(s) to year %1$s semester %2$s of"
            + " the degree plan: \n%3$s";
    private static final String COREQ_MESSAGE_SUCCESS = "\nCo-requisite(s) added:\n%1$s";
    private static final String MESSAGE_DUPLICATE_CODE = "The module(s) %1$s already exists in the degree plan.";
    private static final String MESSAGE_MODULE_DOES_NOT_EXIST = "The module(s) %1$s does not exist in the module list.";
    private static final String MESSAGE_PLAN_DOES_NOT_EXIST = "The degree plan of year %1$s and semester"
            + "%2$s does not exist.";
    private Year yearToAddTo;
    private Semester semesterToAddTo;
    private Set<Code> codesToAdd;

    /**
     * Creates a PlannerAddCommand to add the specified {@code codes} to the degree plan.
     */
    public PlannerAddCommand(Year year, Semester semester, Set<Code> codes) {
        requireNonNull(year);
        requireNonNull(semester);
        requireNonNull(codes);
        yearToAddTo = year;
        semesterToAddTo = semester;
        codesToAdd = codes;
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {
        requireNonNull(model);

        DegreePlanner selectedDegreePlanner = model.getAddressBook()
                .getDegreePlannerList().stream().filter(degreePlanner -> (degreePlanner.getYear().equals(yearToAddTo)
                        && degreePlanner.getSemester().equals(semesterToAddTo))).findFirst().orElse(null);
        if (selectedDegreePlanner == null) {
            throw new CommandException(String.format(MESSAGE_PLAN_DOES_NOT_EXIST, yearToAddTo, semesterToAddTo));
        }

        Set<Code> existingPlannerCodes = codesToAdd.stream().filter(code -> model.getAddressBook()
                .getDegreePlannerList().stream().map(DegreePlanner::getCodes)
                .anyMatch(codes -> codes.contains(code))).collect(Collectors.toSet());
        if (existingPlannerCodes.size() > 0) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_CODE, existingPlannerCodes));
        }

        Set<Code> nonExistentModuleCodes = codesToAdd.stream().filter(code -> !model.hasModuleCode(code))
                .collect(Collectors.toSet());
        if (nonExistentModuleCodes.size() > 0) {
            throw new CommandException(String.format(MESSAGE_MODULE_DOES_NOT_EXIST, nonExistentModuleCodes));
        }

        Set<Code> selectedCodeSet = new HashSet<>(selectedDegreePlanner.getCodes());
        Set<Code> coreqAdded = new HashSet<>();
        selectedCodeSet.addAll(codesToAdd);
        for (Code codeToAdd :codesToAdd) {
            selectedCodeSet.add(codeToAdd);
            //adds Co-requisite(s)
            Module module = model.getModuleByCode(codeToAdd);
            if (module.getCorequisites().size() > 0) {
                coreqAdded.addAll(module.getCorequisites());
                selectedCodeSet.addAll(module.getCorequisites());
            }
        }

        coreqAdded.removeAll(codesToAdd);

        DegreePlanner editedDegreePlanner = new DegreePlanner(yearToAddTo, semesterToAddTo, selectedCodeSet);
        model.setDegreePlanner(selectedDegreePlanner, editedDegreePlanner);
        model.commitAddressBook();

        if (coreqAdded.size() > 0) {
            return new CommandResult(String.format(MESSAGE_SUCCESS, yearToAddTo, semesterToAddTo,
                    codesToAdd) + String.format(COREQ_MESSAGE_SUCCESS, coreqAdded));
        } else {
            return new CommandResult(String.format(MESSAGE_SUCCESS, yearToAddTo, semesterToAddTo, codesToAdd));
        }
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
