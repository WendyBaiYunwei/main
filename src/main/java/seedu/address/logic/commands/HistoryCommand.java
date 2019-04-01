package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import seedu.address.logic.CommandHistory;
import seedu.address.model.Model;

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
        ObservableList<String> commandHistory = history.getHistory();

        if (commandHistory.isEmpty()) {
            return new CommandResult(MESSAGE_NO_HISTORY);
        }

        String reversedCommandHistory = commandHistory.stream()
                .sorted(Collections.reverseOrder())
                .map(command -> "- " + command)
                .collect(Collectors.joining("\n"));

        return new CommandResult(String.format(MESSAGE_SUCCESS, reversedCommandHistory));
    }

}
