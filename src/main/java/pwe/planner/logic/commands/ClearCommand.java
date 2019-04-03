package pwe.planner.logic.commands;

import static java.util.Objects.requireNonNull;
import static pwe.planner.model.util.InitialDataUtil.getInitialApplication;

import pwe.planner.logic.CommandHistory;
import pwe.planner.model.Application;
import pwe.planner.model.Model;

/**
 * Clears all existing data in {@link Application}, and populates the initial (empty)
 * degree planners and requirement categories.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";
    public static final String MESSAGE_SUCCESS = "Successfully cleared all data!\n"
            + "[Tip] If you unintentionally used this command, do use the undo command to revert back the changes!";

    @Override
    public CommandResult execute(Model model, CommandHistory history) {
        requireNonNull(model);
        model.setApplication(getInitialApplication());
        model.commitApplication();
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
