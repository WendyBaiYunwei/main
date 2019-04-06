package pwe.planner.logic.commands;

import static java.util.Objects.requireNonNull;
import static pwe.planner.commons.core.Messages.MESSAGE_INVALID_MODULE_DISPLAYED_INDEX;

import java.util.List;

import pwe.planner.commons.core.index.Index;
import pwe.planner.logic.CommandHistory;
import pwe.planner.logic.commands.exceptions.CommandException;
import pwe.planner.model.Application;
import pwe.planner.model.Model;
import pwe.planner.model.module.Module;

/**
 * Deletes a module identified using it's displayed index in the module list.
 * Deletes a {@link Module} identified using it's displayed {@link Index} in the
 * {@link Application#modules module list}.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    // This is declared before MESSAGE_USAGE to prevent illegal forward reference
    public static final String FORMAT_AND_EXAMPLES = "Format: " + COMMAND_WORD + " INDEX \n"
            + "Example: To delete the first module in the displayed module list below, you can enter: "
            + COMMAND_WORD + " 1";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes a module in the displayed module list.\n"
            + "To choose which module you want to delete, please include the index number "
            + "(beside the module code) in the displayed module list.\n"
            + FORMAT_AND_EXAMPLES;

    public static final String MESSAGE_DELETE_MODULE_SUCCESS = "Successfully deleted the module:\n%1$s";

    private final Index targetIndex;

    public DeleteCommand(Index targetIndex) {
        requireNonNull(targetIndex);

        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {
        requireNonNull(model);

        List<Module> lastShownList = model.getFilteredModuleList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(MESSAGE_INVALID_MODULE_DISPLAYED_INDEX);
        }

        Module moduleToDelete = lastShownList.get(targetIndex.getZeroBased());
        model.deleteModule(moduleToDelete);
        model.commitApplication();
        return new CommandResult(String.format(MESSAGE_DELETE_MODULE_SUCCESS, moduleToDelete));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DeleteCommand // instanceof handles nulls
                && targetIndex.equals(((DeleteCommand) other).targetIndex)); // state check
    }
}
