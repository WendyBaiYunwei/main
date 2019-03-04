package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CREDITS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

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
            + PREFIX_NAME + "NAME "
            + PREFIX_CREDITS + "CREDITS "
            + PREFIX_CODE + "CODE "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + "John Doe "
            + PREFIX_CREDITS + "98765432 "
            + PREFIX_CODE + "311, Clementi Ave 2, #02-25 "
            + PREFIX_TAG + "friends "
            + PREFIX_TAG + "owesMoney";

    public static final String MESSAGE_SUCCESS = "New module added: %1$s";
    public static final String MESSAGE_DUPLICATE_MODULE = "This module already exists in the address book";
    private final Code codeToAdd;
    private final Year yearToAdd;
    private final Semester semesterToAdd;
    private final Set<Code> codesToAdd;

    /**
     * Creates an AddCommand to add the specified {@Code code Module}
     */
    public PlannerAddCommand(Code code, Year year, Semester semester, Set<Code> codes) {
        requireNonNull(code);
        requireNonNull(year);
        requireNonNull(semester);
        codeToAdd = code;
        yearToAdd = year;
        semesterToAdd = semester;
        codesToAdd = codes;
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {
        requireNonNull(model);

        if (model.hasDegreePlannerModule(codeToAdd, yearToAdd, semesterToAdd, codesToAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_MODULE);
        }

        model.addDegreePlannerModule(codeToAdd, yearToAdd, semesterToAdd, codesToAdd);
        model.commitDegreePlannerList();
        return new CommandResult(String.format(MESSAGE_SUCCESS, codesToAdd));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof PlannerAddCommand // instanceof handles nulls
                && codeToAdd.equals(((PlannerAddCommand) other).codeToAdd));
    }
}

