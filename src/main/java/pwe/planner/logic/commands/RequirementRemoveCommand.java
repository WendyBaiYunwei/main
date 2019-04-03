package pwe.planner.logic.commands;

import static java.util.Objects.requireNonNull;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_CODE;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_NAME;

import java.util.HashSet;
import java.util.Set;

import pwe.planner.logic.CommandHistory;
import pwe.planner.logic.commands.exceptions.CommandException;
import pwe.planner.model.Model;
import pwe.planner.model.module.Code;
import pwe.planner.model.module.Name;
import pwe.planner.model.requirement.RequirementCategory;

/**
 * Removes a module from a requirement category.
 */
public class RequirementRemoveCommand extends Command {

    public static final String COMMAND_WORD = "requirement_remove";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Removes a module from a requirement category.\n"
            + "Parameters: "
            + PREFIX_NAME + "NAME "
            + PREFIX_CODE + "CODE "
            + "[" + PREFIX_CODE + "CODE]...\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + "IT Professionalism "
            + PREFIX_CODE + "IS4231 ";

    public static final String MESSAGE_SUCCESS = "Module removed from requirement category: %1$s";
    public static final String MESSAGE_NONEXISTENT_REQUIREMENT_CATEGORY =
            "The requirement category (%1$s) does not exist!";
    public static final String MESSAGE_NONEXISTENT_CODE =
            "The module to be removed from the requirement category does not exists in the module list!";
    public static final String MESSAGE_REQUIREMENT_CATEGORY_NONEXISTENT_CODE =
            "The module to be removed does not exists in %1$s";

    private final Name toFind;
    private final Set<Code> toRemove = new HashSet<>();

    /**
     * Creates an RequirementRemoveCommand to add the specified {@code Name, codeSet}
     */
    public RequirementRemoveCommand(Name name, Set<Code> codeSet) {
        requireNonNull(name);
        requireNonNull(codeSet);
        this.toFind = name;
        this.toRemove.addAll(codeSet);
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {
        requireNonNull(model);

        RequirementCategory currentRequirementCategory = model.getRequirementCategory(toFind);

        if (currentRequirementCategory == null) {
            throw new CommandException(String.format(MESSAGE_NONEXISTENT_REQUIREMENT_CATEGORY, toFind));
        }

        if (toRemove.stream().anyMatch(code -> !model.hasModuleCode(code))) {
            throw new CommandException(MESSAGE_NONEXISTENT_CODE);
        }

        if (!currentRequirementCategory.getCodeSet().containsAll(toRemove)) {
            throw new CommandException(String.format(MESSAGE_REQUIREMENT_CATEGORY_NONEXISTENT_CODE, toFind));
        }

        Set<Code> newCodeSet = new HashSet<>(currentRequirementCategory.getCodeSet());
        newCodeSet.removeAll(toRemove);

        RequirementCategory editedRequirementCategory =
                new RequirementCategory(toFind, currentRequirementCategory.getCredits(), newCodeSet);
        model.setRequirementCategory(currentRequirementCategory, editedRequirementCategory);
        model.commitApplication();
        return new CommandResult(String.format(MESSAGE_SUCCESS, toFind));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof RequirementRemoveCommand // instanceof handles nulls
                && toFind.equals(((RequirementRemoveCommand) other).toFind)
                && toRemove.equals(((RequirementRemoveCommand) other).toRemove));
    }
}
