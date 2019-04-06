package pwe.planner.logic.commands;

import static java.util.Objects.requireNonNull;
import static pwe.planner.commons.util.CollectionUtil.requireAllNonNull;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_CODE;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_NAME;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import pwe.planner.logic.CommandHistory;
import pwe.planner.logic.commands.exceptions.CommandException;
import pwe.planner.model.Model;
import pwe.planner.model.module.Code;
import pwe.planner.model.module.Name;
import pwe.planner.model.requirement.RequirementCategory;

/**
 * Adds a module to a requirement category.
 */
public class RequirementAddCommand extends Command {

    public static final String COMMAND_WORD = "requirement_add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a module to a requirement category.\n"
            + "Parameters: "
            + PREFIX_NAME + "NAME "
            + "[" + PREFIX_CODE + "CODE]...\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + "IT Professionalism "
            + PREFIX_CODE + "IS4231 ";

    public static final String MESSAGE_SUCCESS = "Module added to requirement category: %1$s";
    public static final String MESSAGE_NONEXISTENT_REQUIREMENT_CATEGORY =
            "The requirement category (%1$s) does not exist!";
    public static final String MESSAGE_NONEXISTENT_CODE =
            "The module to be added to the requirement category does not exists in the module list!";
    public static final String MESSAGE_DUPLICATE_CODE =
            "The module has already been added to %1$s ";
    public static final String MESSAGE_EXISTING_CODE =
            "The module to be added already exists in another requirement category!";

    private final Name toFind;
    private final Set<Code> toAdd = new HashSet<>();

    /**
     * Creates an RequirementAddCommand to add the specified {@code RequirementCategory}
     */
    public RequirementAddCommand(Name name, Set<Code> codeSet) {
        requireAllNonNull(name, codeSet);

        this.toFind = name;
        this.toAdd.addAll(codeSet);
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {
        requireNonNull(model);

        RequirementCategory currentRequirementCategory = model.getRequirementCategory(toFind);

        if (currentRequirementCategory == null) {
            throw new CommandException(String.format(MESSAGE_NONEXISTENT_REQUIREMENT_CATEGORY, toFind));
        }

        if (toAdd.stream().anyMatch(code -> !model.hasModuleCode(code))) {
            throw new CommandException(MESSAGE_NONEXISTENT_CODE);
        }

        if (currentRequirementCategory.hasModuleCode(toAdd)) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_CODE, toFind));
        }

        Stream<RequirementCategory> requirementCategories = model.getApplication()
                .getRequirementCategoryList().stream();

        boolean isAnyCodeInRequirementCategories = requirementCategories.anyMatch(reqCat -> toAdd.stream()
                .anyMatch(code -> reqCat.getCodeSet().contains(code)));

        if (isAnyCodeInRequirementCategories) {
            throw new CommandException(MESSAGE_EXISTING_CODE);
        }

        Set<Code> newCodeSet = new HashSet<>(currentRequirementCategory.getCodeSet());
        newCodeSet.addAll(toAdd);

        RequirementCategory editedRequirementCategory = new RequirementCategory(
                toFind, currentRequirementCategory.getCredits(), newCodeSet);
        model.setRequirementCategory(currentRequirementCategory, editedRequirementCategory);
        model.commitApplication();
        return new CommandResult(String.format(MESSAGE_SUCCESS, toFind));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof RequirementAddCommand // instanceof handles nulls
                && toFind.equals(((RequirementAddCommand) other).toFind)
                && toAdd.equals(((RequirementAddCommand) other).toAdd));
    }

}
