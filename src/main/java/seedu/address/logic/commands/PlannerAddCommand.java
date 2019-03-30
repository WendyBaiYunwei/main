package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SEMESTER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_YEAR;

import java.util.HashSet;
import java.util.Set;

import seedu.address.logic.CommandHistory;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.module.Code;
import seedu.address.model.planner.DegreePlanner;
import seedu.address.model.planner.Semester;
import seedu.address.model.planner.Year;

/**
 * Adds one/multiple modules to the degree planner.
 */
public class PlannerAddCommand extends Command {

    public static final String COMMAND_WORD = "planner_add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds one/multiple modules to the degree plan. "
            + "Parameters: "
            + PREFIX_CODE + "CODE "
            + PREFIX_YEAR + "YEAR "
            + PREFIX_SEMESTER + "SEMESTER";

    public static final String MESSAGE_SUCCESS = "The new module/modules added to degree plan: %1$s";
    public static final String MESSAGE_DUPLICATE_MODULE = "Some/one of the modules already exist in degree plan";
    public static final String MESSAGE_MODULE_DOES_NOT_EXIST = "Some/one of the modules do"
            + " not exist in the module list";
    private Year yearToAdd;
    private Semester semToAdd;
    private Set<Code> codesToAdd;

    /**
     * Creates a PlannerAddCommand to add the specified {@Code code Module} to the degree plan
     */
    public PlannerAddCommand(Year year, Semester semester, Set<Code> codes) {
        requireNonNull(year);
        requireNonNull(semester);
        requireNonNull(codes);
        yearToAdd = year;
        semToAdd = semester;
        codesToAdd = codes;
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {
        requireNonNull(model);

        DegreePlanner currentDegreePlanner = model.getDegreePlanner(yearToAdd, semToAdd);

        if (codesToAdd.stream().anyMatch(code -> model.existingPlannerModules(code))) {
            throw new CommandException(MESSAGE_DUPLICATE_MODULE);
        }

        if (codesToAdd.stream().anyMatch(code -> !model.existingModuleListModules(code))) {
            throw new CommandException(MESSAGE_MODULE_DOES_NOT_EXIST);
        }

        Set<Code> newCodeSet = new HashSet<>(currentDegreePlanner.getCodes());
        newCodeSet.addAll(codesToAdd);

        DegreePlanner editedDegreePlanner = new DegreePlanner(
                currentDegreePlanner.getYear(), currentDegreePlanner.getSemester(), newCodeSet);
        model.setDegreePlanner(currentDegreePlanner, editedDegreePlanner);
        model.commitAddressBook();
        return new CommandResult(String.format(MESSAGE_SUCCESS, codesToAdd));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof PlannerAddCommand // instanceof handles nulls
                && yearToAdd.equals(((PlannerAddCommand) other).yearToAdd)
                && semToAdd.equals(((PlannerAddCommand) other).semToAdd)
                && codesToAdd.equals(((PlannerAddCommand) other).codesToAdd));
    }
}

