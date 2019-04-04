package pwe.planner.logic.commands;

import static java.util.Objects.requireNonNull;
import static pwe.planner.model.Model.PREDICATE_SHOW_ALL_MODULES;

import pwe.planner.logic.CommandHistory;
import pwe.planner.logic.commands.exceptions.CommandException;
import pwe.planner.model.Model;

/**
 * Reverts the {@code model}'s application to its previous state.
 */
public class UndoCommand extends Command {

    public static final String COMMAND_WORD = "undo";
    public static final String MESSAGE_SUCCESS = "Undo success!";
    public static final String MESSAGE_FAILURE = "No more commands to undo!";

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {
        requireNonNull(model);

        if (!model.canUndoApplication()) {
            throw new CommandException(MESSAGE_FAILURE);
        }

        model.undoApplication();
        model.updateFilteredModuleList(PREDICATE_SHOW_ALL_MODULES);
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
