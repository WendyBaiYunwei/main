package pwe.planner.logic.commands;

import static java.util.Objects.requireNonNull;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_CODE;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import pwe.planner.logic.CommandHistory;
import pwe.planner.logic.commands.exceptions.CommandException;
import pwe.planner.model.Model;
import pwe.planner.model.module.Code;
import pwe.planner.model.module.Module;
import pwe.planner.model.planner.DegreePlanner;

/**
 * Removes module(s) from the degree plan.
 * Related co-requisite(s) are removed as well.
 */
public class PlannerRemoveCommand extends Command {

    public static final String COMMAND_WORD = "planner_remove";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Removes module(s) from the degree plan.\n"
            + "Parameters: "
            + "Format: " + COMMAND_WORD + " "
            + PREFIX_CODE + "CODE "
            + "[" + PREFIX_CODE + "CODE]...\n"
            + "Example: " + COMMAND_WORD + " code/CS1010 code/CS2040C";

    public static final String MESSAGE_SUCCESS = "Removed module(s) %1$s from the degree plan\n"
            + "Co-requisite(s) removed: %2$s";
    public static final String MESSAGE_NONEXISTENT_CODES = "The module code(s) %1$s does not exist in the"
            + " degree plan.";
    private Set<Code> codesToRemove;

    /**
     * Creates a PlannerRemoveCommand to remove the specified {@code codes} from the degree planner
     * Related co-requisite(s) are removed as well.
     */
    public PlannerRemoveCommand(Set<Code> codes) {
        requireNonNull(codes);

        codesToRemove = codes;
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {
        requireNonNull(model);

        // Returns codes that the user wants to remove but are non-existent in the degree plan.
        Set<Code> nonExistentPlannerCodes = codesToRemove.stream().filter(codeToCheck -> model.getApplication()
                .getDegreePlannerList().stream().map(DegreePlanner::getCodes)
                .noneMatch(selectedPlannerCodes -> selectedPlannerCodes.contains(codeToCheck)))
                .collect(Collectors.toSet());
        if (!nonExistentPlannerCodes.isEmpty()) {
            // Converts the set to a string to remove the square brackets.
            String nonExistentCodesString = nonExistentPlannerCodes.stream().map(Code::toString)
                    .collect(Collectors.joining(", "));
            throw new CommandException(String.format(MESSAGE_NONEXISTENT_CODES, nonExistentCodesString));
        }

        Set<Code> coreqsRemoved = new HashSet<>();
        Set<Code> coreqsOfCodesToRemove = new HashSet<>();
        // Adds co-requisites of codes to remove to a set.
        codesToRemove.stream().map(model::getModuleByCode).map(Module::getCorequisites)
                .forEach(coreqsOfCodesToRemove::addAll);

        ObservableList<DegreePlanner> degreePlannerList = model.getApplication().getDegreePlannerList();
        for (DegreePlanner degreePlanner : degreePlannerList) {
            Set<Code> selectedCodeSet = new HashSet<>(degreePlanner.getCodes());
            // Returns relevant codes that are not just co-requisites to the code to remove, but are also existing
            // in the selected section of the degree plan.
            coreqsOfCodesToRemove.retainAll(selectedCodeSet);

            // Removes the relevant co-requisites.
            selectedCodeSet.removeAll(coreqsOfCodesToRemove);
            // Removes the codes to remove.
            selectedCodeSet.removeAll(codesToRemove);
            // Updates the selected section of the degree plan.
            DegreePlanner editedDegreePlanner = new DegreePlanner(degreePlanner.getYear(),
                    degreePlanner.getSemester(), selectedCodeSet);
            model.setDegreePlanner(degreePlanner, editedDegreePlanner);

            // Combines the removed co-requisites together into a set for feedback to user.
            coreqsRemoved.addAll(coreqsOfCodesToRemove);
        }

        coreqsRemoved.removeAll(codesToRemove);
        model.commitApplication();

        // Converts a set to a string to remove the brackets of set.
        String removedCodesString = codesToRemove.stream().map(Code::toString).collect(Collectors.joining(", "));
        String coreqsRemovedString = coreqsRemoved.isEmpty() ? "None" : coreqsRemoved.stream().map(Code::toString)
                .collect(Collectors.joining(", "));

        return new CommandResult(String.format(MESSAGE_SUCCESS, removedCodesString, coreqsRemovedString));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof PlannerRemoveCommand // instanceof handles nulls
                && codesToRemove.equals(((PlannerRemoveCommand) other).codesToRemove)); // state check
    }
}
