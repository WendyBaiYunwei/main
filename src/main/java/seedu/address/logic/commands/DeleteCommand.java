package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.module.Module;

/**
 * Deletes a module identified using it's displayed index from the address book.
 * Deletes a {@link Module} identified using it's displayed {@link Index} in the
 * {@link seedu.address.model.AddressBook#modules module list}.
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
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {
        requireNonNull(model);
        List<Module> lastShownList = model.getFilteredModuleList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_MODULE_DISPLAYED_INDEX);
        }

        Module moduleToDelete = lastShownList.get(targetIndex.getZeroBased());
        model.deleteModule(moduleToDelete);
        model.commitAddressBook();
        return new CommandResult(String.format(MESSAGE_DELETE_MODULE_SUCCESS, moduleToDelete));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DeleteCommand // instanceof handles nulls
                && targetIndex.equals(((DeleteCommand) other).targetIndex)); // state check
    }
}
