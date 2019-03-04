package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CREDITS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SEMESTER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_YEAR;

import java.util.Set;

import seedu.address.logic.CommandHistory;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.module.Code;
import seedu.address.model.module.Module;
import seedu.address.model.planner.DegreePlanner;
import seedu.address.model.planner.DegreePlannerModule;
import seedu.address.model.planner.Semester;
import seedu.address.model.planner.Year;

/**
 * Adds a module to the address book.
 */
public class PlannerAddCommand extends Command {

    public static final String COMMAND_WORD = "planner_add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a module to the degree planner. "
            + "Parameters: "
            + PREFIX_CODE + "CODE "
            + PREFIX_YEAR + "YEAR"
            + PREFIX_SEMESTER + "SEMESTER";

    public static final String MESSAGE_SUCCESS = "New module added: %1$s";
    public static final String MESSAGE_DUPLICATE_MODULE = "This module already exists in the address book";
    private DegreePlannerModule toAdd;

    /**
     * Creates an AddCommand to add the specified {@Code code Module}
     */
    public PlannerAddCommand(DegreePlannerModule plannerModule) {
        requireNonNull(plannerModule);
        toAdd = plannerModule;
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {
        requireNonNull(model);

        if (model.hasDegreePlannerModule(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_MODULE);
        }

        model.addDegreePlannerModule(toAdd);
        model.commitDegreePlannerList();
        return new CommandResult(String.format(MESSAGE_SUCCESS,toAdd));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof PlannerAddCommand // instanceof handles nulls
                && toAdd.equals(((PlannerAddCommand) other).toAdd));
    }
}

