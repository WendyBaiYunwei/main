package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CODE;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.module.Code;
import seedu.address.model.module.Module;
import seedu.address.model.planner.DegreePlanner;

/**
 * Removes module(s) from the degree plan.
 * Related Co-requisites are removed as well.
 */
public class PlannerRemoveCommand extends Command {

    public static final String COMMAND_WORD = "planner_remove";
    //add in example
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Removes module(s) from the degree plan.\n"
            + "Parameters: "
            + PREFIX_CODE + "CODE "
            + "Example: " + COMMAND_WORD + " code/CS1010 code/CS2040C";

    public static final String MESSAGE_SUCCESS = "Removed module(s) from the degree plan:\n%1$s";
    public static final String COREQ_MESSAGE_SUCCESS = "\nCo-requisite(s) removed:\n%1$s";
    public static final String MESSAGE_CODE_DOES_NOT_EXIST = "The module(s) %1$s does not exist in the degree plan.";
    private Set<Code> codesToRemove;

    /**
     * Creates a PlannerRemoveCommand to remove the specified {@code codes} from the degree planner
     * Related Co-requisites are removed as well.
     */
    public PlannerRemoveCommand(Set<Code> plannerModules) {
        codesToRemove = plannerModules;
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {
        requireNonNull(model);

        Set<Code> nonExistentPlannerCodes = codesToRemove.stream().filter(code -> model.getAddressBook()
                .getDegreePlannerList().stream().map(DegreePlanner::getCodes)
                .noneMatch(codes -> codes.contains(code))).collect(Collectors.toSet());
        if (nonExistentPlannerCodes.size() > 0) {
            throw new CommandException(String.format(MESSAGE_CODE_DOES_NOT_EXIST, nonExistentPlannerCodes));
        }

        Set<Code> coreqRemoved = new HashSet<>();
        ObservableList<DegreePlanner> degreePlannerList = model.getAddressBook().getDegreePlannerList();
        ObservableList<Module> modules = model.getAddressBook().getModuleList();

        for (DegreePlanner selectedDegreePlanner : degreePlannerList) {
            Set<Code> newCodeSet = new HashSet<>(selectedDegreePlanner.getCodes());
            for (Code codeToRemove : codesToRemove) {
                newCodeSet.remove(codeToRemove);
                for (Module module : modules) {
                    //Removes the Co-requisites of modules that exist in the degree plan.
                    //Related Co-requisites that do not exist in the degree plan will be skipped.
                    if (codeToRemove.equals(module.getCode()) && module.getCorequisites().size() > 0) {
                        coreqRemoved.addAll(module.getCorequisites().stream()
                                .filter(newCodeSet::contains).collect(Collectors.toSet()));
                        newCodeSet.removeAll(coreqRemoved);
                    }
                }
            }
            DegreePlanner editedDegreePlanner = new DegreePlanner(selectedDegreePlanner.getYear(),
                    selectedDegreePlanner.getSemester(), newCodeSet);
            model.setDegreePlanner(selectedDegreePlanner, editedDegreePlanner);
        }

        model.commitAddressBook();

        coreqRemoved.removeAll(codesToRemove);

        if (coreqRemoved.size() > 0) {
            return new CommandResult(String.format(MESSAGE_SUCCESS, codesToRemove)
                    + String.format(COREQ_MESSAGE_SUCCESS, coreqRemoved));
        } else {
            return new CommandResult(String.format(MESSAGE_SUCCESS, codesToRemove));
        }
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof PlannerRemoveCommand // instanceof handles nulls
                && codesToRemove.equals(((PlannerRemoveCommand) other).codesToRemove)); // state check
    }
}

