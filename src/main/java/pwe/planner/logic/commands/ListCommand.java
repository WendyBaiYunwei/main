package pwe.planner.logic.commands;

import static java.util.Objects.requireNonNull;
import static pwe.planner.model.Model.PREDICATE_SHOW_ALL_MODULES;

import pwe.planner.logic.CommandHistory;
import pwe.planner.model.Model;

/**
 * Lists all modules in the application to the user.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";

    public static final String MESSAGE_SUCCESS = "Listed all modules";


    @Override
    public CommandResult execute(Model model, CommandHistory history) {
        requireNonNull(model);

        model.updateFilteredModuleList(PREDICATE_SHOW_ALL_MODULES);
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
