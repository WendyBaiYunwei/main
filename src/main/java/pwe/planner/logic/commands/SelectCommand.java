package pwe.planner.logic.commands;

import static java.util.Objects.requireNonNull;
import static pwe.planner.commons.core.Messages.MESSAGE_INVALID_MODULE_DISPLAYED_INDEX;

import java.util.List;

import pwe.planner.commons.core.index.Index;
import pwe.planner.logic.CommandHistory;
import pwe.planner.logic.commands.exceptions.CommandException;
import pwe.planner.model.Model;
import pwe.planner.model.module.Module;

/**
 * Selects a module identified using it's displayed index from the application.
 */
public class SelectCommand extends Command {

    public static final String COMMAND_WORD = "select";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Selects the module identified by the index number used in the displayed module list.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_SELECT_MODULE_SUCCESS = "Selected Module: %1$s";

    private final Index targetIndex;

    public SelectCommand(Index targetIndex) {
        requireNonNull(targetIndex);

        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {
        requireNonNull(model);

        List<Module> filteredModuleList = model.getFilteredModuleList();

        if (targetIndex.getZeroBased() >= filteredModuleList.size()) {
            throw new CommandException(MESSAGE_INVALID_MODULE_DISPLAYED_INDEX);
        }

        model.setSelectedModule(filteredModuleList.get(targetIndex.getZeroBased()));
        return new CommandResult(String.format(MESSAGE_SELECT_MODULE_SUCCESS, targetIndex.getOneBased()));

    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof SelectCommand // instanceof handles nulls
                && targetIndex.equals(((SelectCommand) other).targetIndex)); // state check
    }
}
