package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_COREQUISITE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CREDITS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import seedu.address.logic.CommandHistory;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.module.Code;
import seedu.address.model.module.Module;

/**
 * Adds a module to the address book.
 */
public class AddCommand extends Command {

    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a module to module list. "
            + "Parameters: "
            + PREFIX_NAME + "NAME "
            + PREFIX_CODE + "CODE "
            + PREFIX_CREDITS + "CREDITS "
            + "[" + PREFIX_TAG + "TAG]... "
            + "[" + PREFIX_COREQUISITE + "COREQUISITE]...\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + "Data Structures and Algorithms "
            + PREFIX_CODE + "CS2040C "
            + PREFIX_CREDITS + "4 "
            + PREFIX_TAG + "linkedlist "
            + PREFIX_TAG + "stack "
            + PREFIX_TAG + "queue "
            + PREFIX_COREQUISITE + "CS1010";

    public static final String MESSAGE_SUCCESS = "New module added: %1$s";
    public static final String MESSAGE_DUPLICATE_MODULE = "This module already exists in the module list";
    public static final String MESSAGE_NON_EXISTENT_COREQUISITE =
            "The corequisite module code (%1$s) does not exists in the module list";

    private final Module toAdd;

    /**
     * Creates an AddCommand to add the specified {@code Module}
     */
    public AddCommand(Module module) {
        requireNonNull(module);
        toAdd = module;
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {
        requireNonNull(model);

        if (model.hasModule(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_MODULE);
        }

        for (Code corequisite : toAdd.getCorequisites()) {
            if (!model.hasModuleCode(corequisite)) {
                throw new CommandException(String.format(MESSAGE_NON_EXISTENT_COREQUISITE, corequisite));
            }
        }

        model.addModule(toAdd);
        model.commitAddressBook();
        return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof AddCommand // instanceof handles nulls
                && toAdd.equals(((AddCommand) other).toAdd));
    }
}
