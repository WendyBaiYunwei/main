package pwe.planner.logic.commands;

import static pwe.planner.logic.commands.CommandTestUtil.assertCommandSuccess;

import org.junit.Test;

import pwe.planner.logic.CommandHistory;
import pwe.planner.model.Model;
import pwe.planner.model.ModelManager;
import pwe.planner.model.util.SampleDataUtil;

public class ResetCommandTest {
    private CommandHistory commandHistory = new CommandHistory();

    @Test
    public void execute_reset_command() {
        Model model = new ModelManager();
        Model expectedModel = new ModelManager();
        expectedModel.setApplication(SampleDataUtil.getSampleApplication());
        expectedModel.commitApplication();

        assertCommandSuccess(new ResetCommand(), model, commandHistory, ResetCommand.MESSAGE_SUCCESS, expectedModel);
    }

}
