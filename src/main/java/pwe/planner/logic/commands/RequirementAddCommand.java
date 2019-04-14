package pwe.planner.logic.commands;

import static java.util.Objects.requireNonNull;
import static pwe.planner.commons.util.CollectionUtil.requireAllNonNull;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_CODE;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_NAME;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import pwe.planner.commons.util.StringUtil;
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

    // This is declared before MESSAGE_USAGE to prevent illegal forward reference
    public static final String FORMAT_AND_EXAMPLES = "Format: " + COMMAND_WORD + ' '
            + PREFIX_NAME + "NAME "
            + PREFIX_CODE + "CODE "
            + "[" + PREFIX_CODE + "CODE]...\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + "Computing Foundation "
            + PREFIX_CODE + "CS1010";

    // General command help details
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a module to a requirement category.\n"
            + FORMAT_AND_EXAMPLES;

    // Command success message
    public static final String MESSAGE_SUCCESS = "Successfully added module(s) (%1$s) the requirement category %2$s!";

    // Command failure messages
    public static final String MESSAGE_NONEXISTENT_REQUIREMENT_CATEGORY =
            "You cannot specify a requirement category (%1$s) that does not exist!\n"
            + "Perhaps you misspelled the name of the requirement category?\n"
            + "[Tip] You may want to refer to the requirement category list to see the requirement categories"
            + " in the application using the \"requirement_list\" command!";

    public static final String MESSAGE_NONEXISTENT_CODE = "You cannot specify module(s) (%1$s) that does not exist!\n"
            + "Perhaps you were trying to add modules into the application?\n"
            + "[Tip] You may want to add the module into the module list first using the \"add\" command.\n"
            + "Afterwards, you can add the module into the requirement category using the "
            + "\"requirement_add\" command!";

    public static final String MESSAGE_DUPLICATE_CODE =
            "The module has already been added to requirement category %1$s\n"
            + "Perhaps you misspelled a module code?\n"
            + "[Tip] You are able to view all the modules in application using the \"list\" command.";

    public static final String MESSAGE_EXISTING_CODE =
            "The module to be added already exists in another requirement category!\n"
            + "Perhaps you misspelled a module code?\n"
            + "[Tip] You may want to refer to the requirement category list to see the requirement categories"
            + " in the application using the \"requirement_list\" command!";

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

        List<Code> nonExistentCodes = toAdd.stream().filter(code -> !model.hasModuleCode(code))
                .collect(Collectors.toList());

        if (!nonExistentCodes.isEmpty()) {
            String nonExistentCodesErrorMessage = StringUtil.joinStreamAsString(nonExistentCodes.stream().sorted());
            throw new CommandException(String.format(MESSAGE_NONEXISTENT_CODE, nonExistentCodesErrorMessage));
        }

        if (currentRequirementCategory.hasModuleCode(toAdd)) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_CODE, currentRequirementCategory.getName()));
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
                currentRequirementCategory.getName(), currentRequirementCategory.getCredits(), newCodeSet
        );
        model.setRequirementCategory(currentRequirementCategory, editedRequirementCategory);
        model.commitApplication();

        String codesAdded = StringUtil.joinStreamAsString(toAdd.stream().sorted());

        return new CommandResult(String.format(MESSAGE_SUCCESS, codesAdded, currentRequirementCategory.getName()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof RequirementAddCommand // instanceof handles nulls
                && toFind.equals(((RequirementAddCommand) other).toFind)
                && toAdd.equals(((RequirementAddCommand) other).toAdd));
    }

}
