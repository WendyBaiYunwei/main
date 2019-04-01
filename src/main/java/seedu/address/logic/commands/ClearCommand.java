package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.util.InitialDataUtil.getInitialAddressBook;

import seedu.address.logic.CommandHistory;
import seedu.address.model.Model;

/**
 * Clears all existing data in {@link seedu.address.model.AddressBook}, and populates the initial (empty)
 * degree planners and requirement categories.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";
    public static final String MESSAGE_SUCCESS = "Successfully cleared all data!\n"
            + "[Tip] If you unintentionally used this command, do use the undo command to revert back the changes!";

    @Override
    public CommandResult execute(Model model, CommandHistory history) {
        requireNonNull(model);
        model.setAddressBook(getInitialAddressBook());
        model.commitAddressBook();
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
