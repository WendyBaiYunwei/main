package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import seedu.address.logic.CommandHistory;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.requirement.RequirementCategory;

/**
 * Adds a module to the address book.
 */
public class RequirementAddCommand extends Command {

    public static final String COMMAND_WORD = "requirement_add";

    public static final String MESSAGE_USAGE =
            COMMAND_WORD + ": Adds a module to a requirement category. " + "Parameters: "
                    + PREFIX_NAME + "NAME "
                    + "[" + PREFIX_CODE + "CODE]...\n"
                    + "Example: " + COMMAND_WORD + " "
                    + PREFIX_NAME
                    + "IT Professionalism " + PREFIX_CODE + "IS4231 ";

    public static final String MESSAGE_SUCCESS = "Module added to %1$s ";
    public static final String MESSAGE_REQUIREMENT_CATEGORY_DOES_NOT_EXIST =
            "The requirement category: %1$s does not exist";
    public static final String MESSAGE_MODULE_DOES_NOT_EXIST =
            "The module code does not exists in the module list";
    public static final String MESSAGE_DUPLICATE_MODULE_IN_REQUIREMENT_CATEGORY =
            "The module has already been added to %1$s ";

    private final RequirementCategory toAdd;

    /**
     * Creates an RequirementAddCommand to add the specified {@code RequirementCategory}
     */
    public RequirementAddCommand(RequirementCategory requirementCategory) {
        requireNonNull(requirementCategory);
        toAdd = requirementCategory;
    }

    @Override public CommandResult execute(Model model, CommandHistory history) throws CommandException {
        requireNonNull(model);

        if (!model.hasRequirementCategory(toAdd)) {
            throw new CommandException(String.format(MESSAGE_REQUIREMENT_CATEGORY_DOES_NOT_EXIST, toAdd.getName()));
        }

        if (!model.doesModuleExistInApplication(toAdd, model)) {
            throw new CommandException(MESSAGE_MODULE_DOES_NOT_EXIST);
        }

        if (model.isModuleInRequirementCategory(toAdd)) {
            throw new CommandException(
                    String.format(MESSAGE_DUPLICATE_MODULE_IN_REQUIREMENT_CATEGORY, toAdd.getName()));
        }

        model.addModuleToRequirementCategory(toAdd);
        model.commitAddressBook();
        return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd.getName()));

    }

    @Override public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof RequirementAddCommand // instanceof handles nulls
                && toAdd.equals(((RequirementAddCommand) other).toAdd));
    }
}
