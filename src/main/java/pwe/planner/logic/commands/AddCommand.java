package pwe.planner.logic.commands;

import static java.util.Objects.requireNonNull;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_CODE;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_COREQUISITE;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_CREDITS;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_NAME;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_TAG;

import pwe.planner.logic.CommandHistory;
import pwe.planner.logic.commands.exceptions.CommandException;
import pwe.planner.model.Application;
import pwe.planner.model.Model;
import pwe.planner.model.module.Code;
import pwe.planner.model.module.Module;

/**
 * Adds a {@link Module} to the {@link Application#modules module list}.
 */
public class AddCommand extends Command {

    public static final String COMMAND_WORD = "add";

    // This is declared before MESSAGE_USAGE to prevent illegal forward reference
    public static final String FORMAT_AND_EXAMPLES = "Format: " + COMMAND_WORD + ' '
            + PREFIX_CODE + "CODE "
            + PREFIX_NAME + "NAME "
            + PREFIX_CREDITS + "CREDITS "
            + "[" + PREFIX_COREQUISITE + "COREQUISITE]... "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_CODE + "CS2113T "
            + PREFIX_NAME + "Software Engineering and Object-Oriented Programming "
            + PREFIX_CREDITS + "4 "
            + PREFIX_COREQUISITE + "CS2101 "
            + PREFIX_TAG + "OOP "
            + PREFIX_TAG + "RCS "
            + PREFIX_TAG + "UML";

    // General command help details
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a new module to the module list.\n"
            + FORMAT_AND_EXAMPLES;

    // Command success message
    public static final String MESSAGE_SUCCESS = "Successfully added a new module:\n%1$s";

    // Command failure messages
    public static final String MESSAGE_DUPLICATE_MODULE =
            "You cannot add a new module (%1$s), as the module code %1$s already exists in the module list!";

    public static final String MESSAGE_NON_EXISTENT_COREQUISITE =
            "You cannot add a new module (%1$s) that has a co-requisite module (%2$s) "
            + "which does not exists in the module list!\n"
            + "[Tip] You can try adding the module (%1$s) without specifying the co-requisite module (%2$s) first.\n"
            + "Afterwards, add the module (%2$s) and specify the module (%1$s) as a co-requisite.\n"
            + "This will make both modules (%1$s & %2$s) co-requisites!";

    private final Module toAdd;

    /**
     * Creates an AddCommand to add the specified {@code Module}
     */
    public AddCommand(Module module) {
        requireNonNull(module);
        toAdd = module;
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {
        requireNonNull(model);

        if (model.hasModule(toAdd)) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_MODULE, toAdd.getCode()));
        }

        for (Code corequisite : toAdd.getCorequisites()) {
            if (!model.hasModuleCode(corequisite)) {
                throw new CommandException(String.format(
                        MESSAGE_NON_EXISTENT_COREQUISITE, toAdd.getCode(), corequisite)
                );
            }
        }

        model.addModule(toAdd);
        model.commitApplication();
        return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof AddCommand // instanceof handles nulls
                && toAdd.equals(((AddCommand) other).toAdd));
    }
}
