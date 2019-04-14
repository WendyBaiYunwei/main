package pwe.planner.logic.commands;

import static java.util.Objects.requireNonNull;
import static pwe.planner.commons.util.CollectionUtil.requireAllNonNull;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_CODE;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_NAME;
import static pwe.planner.model.util.RequirementCategoryUtil.getRequirementCategoryWithCodeAdded;
import static pwe.planner.model.util.RequirementCategoryUtil.getRequirementCategoryWithCodeRemoved;
import static pwe.planner.model.util.RequirementCategoryUtil.getRequirementCategoryWithCodesAdded;
import static pwe.planner.model.util.RequirementCategoryUtil.getRequirementCategoryWithCodesRemoved;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import pwe.planner.commons.util.StringUtil;
import pwe.planner.logic.CommandHistory;
import pwe.planner.logic.commands.exceptions.CommandException;
import pwe.planner.model.Model;
import pwe.planner.model.module.Code;
import pwe.planner.model.module.Name;
import pwe.planner.model.requirement.RequirementCategory;

/**
 * Moves modules from a requirement category to another requirement category
 */
public class RequirementMoveCommand extends Command {

    public static final String COMMAND_WORD = "requirement_move";

    // This is declared before MESSAGE_USAGE to prevent illegal forward reference
    public static final String FORMAT_AND_EXAMPLES = "Format: " + COMMAND_WORD + ' '
            + PREFIX_NAME + "NAME "
            + PREFIX_CODE + "CODE "
            + "[" + PREFIX_CODE + "CODE]...\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + "Computing Foundation "
            + PREFIX_CODE + "CS1010";

    // General command help details
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Moves module(s) from one"
            + " requirement category to another. \n"
            + FORMAT_AND_EXAMPLES;

    // Command success message
    public static final String MESSAGE_SUCCESS = "Successfully moved module(s) (%1$s) to requirement category %2$s!";

    // Command failure messages
    public static final String MESSAGE_NONEXISTENT_REQUIREMENT_CATEGORY =
            "You cannot specify a requirement category (%1$s) that does not exist!\n"
            + "Perhaps you misspelled the name of the requirement category?\n"
            + "[Tip] You may want to refer to the requirement category list to see the requirement categories"
            + " in the application using the \"requirement_list\" command!";

    public static final String MESSAGE_CODE_NOT_IN_ANY_REQUIREMENT_CATEGORY =
            "You cannot move module(s) (%1$s) that does not exist in any requirement category!\n"
            + "Perhaps you were trying to add modules into the requirement category?\n"
            + "[Tip] You can try adding the module into the requirement category using the "
            + "\"requirement_add\" command!";

    public static final String MESSAGE_NONEXISTENT_CODE = "You cannot specify module(s) (%1$s) that does not exist!\n"
            + "Perhaps you were trying to add modules into the application?\n"
            + "[Tip] You may want to add the module into the module list first using the \"add\" command.\n"
            + "Afterwards, you can add the module into the requirement category using the "
            + "\"requirement_add\" command!";

    private final Name toFind;
    private final Set<Code> toMove = new HashSet<>();

    /**
     * Creates an RequirementMoveCommand to add the specified {@code RequirementMoveCommand}
     */
    public RequirementMoveCommand(Name name, Set<Code> codeSet) {
        requireAllNonNull(name, codeSet);

        this.toFind = name;
        this.toMove.addAll(codeSet);
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {
        requireNonNull(model);

        ObservableList<RequirementCategory> requirementCategories = model.getApplication().getRequirementCategoryList();
        RequirementCategory destinationRequirementCategory = model.getRequirementCategory(toFind);

        if (destinationRequirementCategory == null) {
            throw new CommandException(String.format(MESSAGE_NONEXISTENT_REQUIREMENT_CATEGORY, toFind));
        }

        List<Code> nonExistentCodes = toMove.stream().filter(code -> !model.hasModuleCode(code))
                .collect(Collectors.toList());

        if (!nonExistentCodes.isEmpty()) {
            String nonExistentCodesErrorMessage = StringUtil.joinStreamAsString(nonExistentCodes.stream().sorted());
            throw new CommandException(String.format(MESSAGE_NONEXISTENT_CODE, nonExistentCodesErrorMessage));
        }

        List<Code> codeNotInAnyRequirementCategory = toMove.stream().filter(code -> requirementCategories.stream()
                .noneMatch(requirementCategory -> requirementCategory.getCodeSet()
                .contains(code))).collect(Collectors.toList());

        if (!codeNotInAnyRequirementCategory.isEmpty()) {
            String codeNotInAnyRequirementCategoryErrorMessage =
                    StringUtil.joinStreamAsString(codeNotInAnyRequirementCategory.stream().sorted());
            throw new CommandException(String.format(MESSAGE_CODE_NOT_IN_ANY_REQUIREMENT_CATEGORY,
                    codeNotInAnyRequirementCategoryErrorMessage));
        }

        RequirementCategory singleSourceRequirementCategory = requirementCategories.stream()
                .filter(requirementCategory -> requirementCategory.getCodeSet()
                .containsAll(toMove)).findFirst().orElse(null);

        //If all codes to be moved is from a requirement category only, all the codes can be moved together
        if (singleSourceRequirementCategory != null) {
            //Check if destination code contains codes to move for edge cases
            if (!destinationRequirementCategory.getCodeSet().containsAll(toMove)) {
                RequirementCategory editedSourceRequirementCategory =
                        getRequirementCategoryWithCodesRemoved(singleSourceRequirementCategory, toMove);
                RequirementCategory editedDestinationRequirementCategory =
                        getRequirementCategoryWithCodesAdded(destinationRequirementCategory, toMove);

                model.setRequirementCategory(singleSourceRequirementCategory, editedSourceRequirementCategory);
                model.setRequirementCategory(destinationRequirementCategory, editedDestinationRequirementCategory);
            }
        } else {
            for (Code code : toMove) {
                //Check if destination code contains codes to move for edge cases
                if (!destinationRequirementCategory.getCodeSet().contains(code)) {
                    RequirementCategory sourceRequirementCategory = requirementCategories.stream()
                            .filter(reqCat -> reqCat.getCodeSet().contains(code)).findFirst().orElse(null);

                    RequirementCategory editedSourceRequirementCategory =
                            getRequirementCategoryWithCodeRemoved(sourceRequirementCategory, code);
                    RequirementCategory editedDestinationRequirementCategory =
                            getRequirementCategoryWithCodeAdded(destinationRequirementCategory, code);

                    model.setRequirementCategory(sourceRequirementCategory, editedSourceRequirementCategory);
                    model.setRequirementCategory(destinationRequirementCategory, editedDestinationRequirementCategory);

                    //reinitialize the updated destination requirement category
                    destinationRequirementCategory = model.getRequirementCategory(toFind);
                }
            }
        }

        String codesMoved = StringUtil.joinStreamAsString(toMove.stream().sorted());

        model.commitApplication();
        return new CommandResult(String.format(MESSAGE_SUCCESS, codesMoved, destinationRequirementCategory.getName()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof RequirementMoveCommand // instanceof handles nulls
                && toFind.equals(((RequirementMoveCommand) other).toFind)
                && toMove.equals(((RequirementMoveCommand) other).toMove));
    }

}
