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
 * Related Co-requisite(s) are removed as well.
 */
public class PlannerRemoveCommand extends Command {

    public static final String COMMAND_WORD = "planner_remove";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Removes module(s) from the degree plan.\n"
            + "Parameters: "
            + PREFIX_CODE + "CODE "
            + "[" + PREFIX_CODE + "CODE]...\n"
            + "Example: " + COMMAND_WORD + " code/CS1010 code/CS2040C";

    public static final String MESSAGE_SUCCESS = "Removed module(s) from the degree plan:\n%1$s\n"
            + "Co-requisite(s) removed:\n%2$s";
    public static final String MESSAGE_NONEXISTENT_CODES = "The module(s) %1$s does not exist in the degree plan.";
    private Set<Code> codesToRemove;

    /**
     * Creates a PlannerRemoveCommand to remove the specified {@code codes} from the degree planner
     * Related Co-requisite(s) are removed as well.
     */
    public PlannerRemoveCommand(Set<Code> codes) {
        requireNonNull(codes);

        codesToRemove = codes;
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {
        requireNonNull(model);

        Set<Code> nonExistentPlannerCodes = codesToRemove.stream().filter(codeToCheck -> model.getApplication()
                .getDegreePlannerList().stream().map(DegreePlanner::getCodes)
                .noneMatch(selectedPlannerCodes -> selectedPlannerCodes.contains(codeToCheck)))
                .collect(Collectors.toSet());
        if (!nonExistentPlannerCodes.isEmpty()) {
            throw new CommandException(String.format(MESSAGE_NONEXISTENT_CODES, nonExistentPlannerCodes));
        }

        Set<Code> coreqsRemoved = new HashSet<>();
        ObservableList<DegreePlanner> degreePlannerList = model.getApplication().getDegreePlannerList();

        for (DegreePlanner selectedDegreePlanner : degreePlannerList) {
            Set<Code> selectedCodeSet = new HashSet<>(selectedDegreePlanner.getCodes());
            Set<Code> coreqsOfCodesToRemove = new HashSet<>();

            codesToRemove.stream().map(model::getModuleByCode).map(Module::getCorequisites)
                    .forEach(coreqsOfCodesToRemove::addAll);
            // returns relevant Co-requisite(s) that exists in the degree plan
            coreqsOfCodesToRemove.retainAll(selectedCodeSet);
            coreqsRemoved.addAll(coreqsOfCodesToRemove);
            selectedCodeSet.removeAll(coreqsOfCodesToRemove);
            selectedCodeSet.removeAll(codesToRemove);
            DegreePlanner editedDegreePlanner = new DegreePlanner(selectedDegreePlanner.getYear(),
                    selectedDegreePlanner.getSemester(), selectedCodeSet);
            model.setDegreePlanner(selectedDegreePlanner, editedDegreePlanner);
        }

        coreqsRemoved.removeAll(codesToRemove);
        model.commitApplication();

        return new CommandResult(String.format(MESSAGE_SUCCESS,
                codesToRemove, coreqsRemoved.isEmpty() ? "None" : coreqsRemoved));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof PlannerRemoveCommand // instanceof handles nulls
                && codesToRemove.equals(((PlannerRemoveCommand) other).codesToRemove)); // state check
    }
}
