package pwe.planner.logic.commands;

import static java.util.Objects.requireNonNull;
import static pwe.planner.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static pwe.planner.model.util.InitialDataUtil.getInitialApplication;

import pwe.planner.logic.CommandHistory;
import pwe.planner.logic.commands.exceptions.CommandException;
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
    public static final String MESSAGE_USAGE = COMMAND_WORD + " [requirement or planner]\n"
            + "requirement - clear all data related to requirement categories\n"
            + "planner - clear all data related to degree planner";

    public static final String PLANNER = "planner";
    public static final String REQUIREMENT = "requirement";
    private String panelToClear = "";

    // default constructor
    public ClearCommand() {
    }

    public ClearCommand(String panelToClear) {
        requireNonNull(panelToClear);
        this.panelToClear = panelToClear;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ClearCommand // instanceof handles nulls
                && panelToClear.equals(((ClearCommand) other).panelToClear));
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {
        requireNonNull(model);

        // initialize with default constructor
        if (panelToClear.isEmpty()) {
            model.setApplication(getInitialApplication());
        } else {
            if (panelToClear.equals(PLANNER)) {
                model.resetPlanner();
            } else if (panelToClear.equals(REQUIREMENT)) {
                model.resetRequirement();
            } else {
                throw new CommandException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE));
            }
        }

        model.commitApplication();
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
