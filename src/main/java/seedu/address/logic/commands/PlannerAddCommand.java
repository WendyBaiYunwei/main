package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SEMESTER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_YEAR;

import seedu.address.logic.CommandHistory;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.planner.DegreePlanner;

/**
 * Adds a module to the address book.
 */
public class PlannerAddCommand extends Command {

    public static final String COMMAND_WORD = "planner_add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a module to the degree planner. "
            + "Parameters: "
            + PREFIX_CODE + "CODE "
            + PREFIX_YEAR + "YEAR "
            + PREFIX_SEMESTER + "SEMESTER";

    public static final String MESSAGE_SUCCESS = "New module added: %1$s";
    public static final String MESSAGE_DUPLICATE_MODULE = "This module already exists in the degree planner";
    public static final String MESSAGE_MODULE_DOES_NOT_EXIST = "The module code does not exist in the application";
    private DegreePlanner toAdd;

    /**
     * Creates a PlannerAddCommand to add the specified {@Code code Module} to the degree planner
     */
    public PlannerAddCommand(DegreePlanner plannerModules) {
        requireNonNull(plannerModules);
        toAdd = plannerModules;
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {
        requireNonNull(model);

        if (model.hasDegreePlannerModules(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_MODULE);
        }
        if (toAdd.getCodes().stream().anyMatch(code -> !model.existingPlannerModules(code))) {
            throw new CommandException(MESSAGE_MODULE_DOES_NOT_EXIST);
        }
        model.addDegreePlannerModules(toAdd);
        model.commitAddressBook();
        return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof PlannerAddCommand // instanceof handles nulls
                && toAdd.equals(((PlannerAddCommand) other).toAdd));
    }
}

