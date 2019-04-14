package pwe.planner.logic.commands;

import static java.util.Objects.requireNonNull;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_CODE;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import pwe.planner.logic.CommandHistory;
import pwe.planner.logic.commands.exceptions.CommandException;
import pwe.planner.model.Model;
import pwe.planner.model.module.Code;
import pwe.planner.model.requirement.RequirementCategory;

/**
 * Removes a module from a requirement category.
 */
public class RequirementRemoveCommand extends Command {

    public static final String COMMAND_WORD = "requirement_remove";

    // This is declared before MESSAGE_USAGE to prevent illegal forward reference
    public static final String FORMAT_AND_EXAMPLES = "Format: " + COMMAND_WORD + ' '
            + PREFIX_CODE + "CODE "
            + "[" + PREFIX_CODE + "CODE]...\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_CODE + "IS4231 ";

    // General command help details
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Removes module(s) from the requirement category.\n"
            + FORMAT_AND_EXAMPLES;

    // Command success message
    public static final String MESSAGE_SUCCESS = "Successfully removed module(s) (%1$s)";

    //Command Failure messages
    public static final String MESSAGE_NONEXISTENT_CODE = "You cannot specify module(s) (%1$s) that does not exist!\n"
            + "Perhaps you misspelled a module code?\n"
            + "[Tip] You are able to view all the modules in application using the \"list\" command.";

    public static final String MESSAGE_CODE_NOT_IN_ANY_REQUIREMENT_CATEGORY =
            "You cannot remove module(s) (%1$s) that does not exist in any requirement category!\n"
            + "Perhaps you were trying to delete modules from the application?\n"
            + "[Tip] You are able to delete a module from the application using the \"delete\" command.\n"
            + "The module deleted will be automatically removed from the corresponding requirement category";

    private final Set<Code> toRemove = new HashSet<>();

    /**
     * Creates an RequirementRemoveCommand to remove the specified {@code codeSet}
     */
    public RequirementRemoveCommand(Set<Code> codeSet) {
        requireNonNull(codeSet);

        this.toRemove.addAll(codeSet);
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {
        requireNonNull(model);

        ObservableList<RequirementCategory> requirementCategories = model.getApplication().getRequirementCategoryList();

        List<Code> nonExistentCodes = toRemove.stream().filter(code -> !model.hasModuleCode(code))
                .collect(Collectors.toList());

        if (!nonExistentCodes.isEmpty()) {
            String nonExistentCodesErrorMessage = nonExistentCodes.stream().map(Code::toString)
                    .collect(Collectors.joining(", "));
            throw new CommandException(String.format(MESSAGE_NONEXISTENT_CODE, nonExistentCodesErrorMessage));
        }

        List<Code> codeNotInAnyRequirementCategory = toRemove.stream().filter(code -> requirementCategories.stream()
                .noneMatch(requirementCategory -> requirementCategory.getCodeSet()
                        .contains(code))).collect(Collectors.toList());

        if (!codeNotInAnyRequirementCategory.isEmpty()) {
            String codeNotInAnyRequirementCategoryErrorMessage = codeNotInAnyRequirementCategory.stream()
                    .map(Code::toString).collect(Collectors.joining(", "));
            throw new CommandException(String.format(MESSAGE_CODE_NOT_IN_ANY_REQUIREMENT_CATEGORY,
                    codeNotInAnyRequirementCategoryErrorMessage));
        }

        RequirementCategory singleSourceRequirementCategory = requirementCategories.stream()
                .filter(requirementCategory -> requirementCategory.getCodeSet()
                        .containsAll(toRemove)).findFirst().orElse(null);

        //If all codes to be removed is from a requirement category only, all the codes can be removed together
        if (singleSourceRequirementCategory != null) {
            Set<Code> newCodeSet = new HashSet<>(singleSourceRequirementCategory.getCodeSet());
            newCodeSet.removeAll(toRemove);

            RequirementCategory editedRequirementCategory = new RequirementCategory(
                    singleSourceRequirementCategory.getName(), singleSourceRequirementCategory.getCredits(),
                    newCodeSet);

            model.setRequirementCategory(singleSourceRequirementCategory, editedRequirementCategory);
        } else {
            //If all codes to be removed are from multiple requirement category, have to remove the codes individually
            for (Code code : toRemove) {
                RequirementCategory sourceRequirementCategory = requirementCategories.stream()
                        .filter(reqCat -> reqCat.getCodeSet().contains(code)).findFirst().orElse(null);

                Set<Code> newCodeSet = new HashSet<>(sourceRequirementCategory.getCodeSet());
                newCodeSet.remove(code);

                RequirementCategory editedRequirementCategory = new RequirementCategory(
                        sourceRequirementCategory.getName(), sourceRequirementCategory.getCredits(), newCodeSet);

                model.setRequirementCategory(sourceRequirementCategory, editedRequirementCategory);

            }
        }

        String codesMoved = toRemove.stream().map(Code::toString).collect(Collectors.joining(", "));

        model.commitApplication();
        return new CommandResult(String.format(MESSAGE_SUCCESS, codesMoved));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof RequirementRemoveCommand // instanceof handles nulls
                && toRemove.equals(((RequirementRemoveCommand) other).toRemove));
    }
}
