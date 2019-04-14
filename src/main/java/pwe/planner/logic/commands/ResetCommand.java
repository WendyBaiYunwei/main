package pwe.planner.logic.commands;

import static java.util.Objects.requireNonNull;

import pwe.planner.logic.CommandHistory;
import pwe.planner.model.Model;
import pwe.planner.model.util.SampleDataUtil;

/**
 * Populate the application with sample data.
 */
public class ResetCommand extends Command {
    public static final String COMMAND_WORD = "reset";
    public static final String MESSAGE_SUCCESS = "Application has been populated with the sample data!\n"
            + "[Tip] If you unintentionally used this command, do use the undo command to revert back the changes!";

    @Override
    public CommandResult execute(Model model, CommandHistory history) {
        requireNonNull(model);

        model.setApplication(SampleDataUtil.getSampleApplication());
        model.commitApplication();
        return new CommandResult(MESSAGE_SUCCESS);
    }
}

