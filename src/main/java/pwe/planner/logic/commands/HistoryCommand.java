package pwe.planner.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import pwe.planner.logic.CommandHistory;
import pwe.planner.model.Model;

/**
 * Lists all the commands entered by user from the start of app launch.
 */
public class HistoryCommand extends Command {

    public static final String COMMAND_WORD = "history";
    public static final String MESSAGE_SUCCESS = "Entered commands (from most recent to earliest):\n%1$s";
    public static final String MESSAGE_NO_HISTORY = "You have not entered any commands yet!";

    @Override
    public CommandResult execute(Model model, CommandHistory history) {
        requireNonNull(history);

        List<String> commandHistory = history.getHistory();

        if (commandHistory.isEmpty()) {
            return new CommandResult(MESSAGE_NO_HISTORY);
        }

        List<String> reversedCommandHistory = new ArrayList<>(commandHistory);
        Collections.reverse(reversedCommandHistory);

        String preppedReversedCommandHistory = reversedCommandHistory.stream()
                .map(command -> "- " + command)
                .collect(Collectors.joining("\n"));

        return new CommandResult(String.format(MESSAGE_SUCCESS, preppedReversedCommandHistory));
    }

}
