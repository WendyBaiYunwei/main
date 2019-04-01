package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.isAnyNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_COREQUISITE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CREDITS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_MODULES;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.module.Code;
import seedu.address.model.module.Credits;
import seedu.address.model.module.Module;
import seedu.address.model.module.Name;
import seedu.address.model.tag.Tag;

/**
 * Edits the details of an existing {@link Module} in the {@link seedu.address.model.AddressBook#modules module list}.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    // This is declared before MESSAGE_USAGE to prevent illegal forward reference
    public static final String FORMAT_AND_EXAMPLES = "Format: " + COMMAND_WORD
            + " INDEX "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_CODE + "CODE] "
            + "[" + PREFIX_CREDITS + "CREDITS] "
            + "[" + PREFIX_TAG + "TAG]... "
            + "[" + PREFIX_COREQUISITE + "COREQUISITE]...\n"
            + "Example: To edit the number of credits assigned to the first module in the displayed module list below"
            + ", you can enter: " + COMMAND_WORD + " 1 " + PREFIX_CREDITS + "8 ";

    // General command help details
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Edits the existing details (e.g. name, code, credits) of a module in the displayed module list.\n"
            + "To choose which module you want to edit, please include the index number "
            + "(beside the module code) in the displayed module list."
            + '\n' + FORMAT_AND_EXAMPLES;

    // Command success message
    public static final String MESSAGE_EDIT_MODULE_SUCCESS = "Successfully edited the module (%1$s) to:\n%2$s";

    // Command failure messages
    public static final String MESSAGE_DUPLICATE_MODULE =
            "You cannot edit module (%1$s) to have the module code %2$s.\n"
            + "Module (%2$s) already exists in the module list!";
    public static final String MESSAGE_INVALID_COREQUISITE = "A module cannot be a co-requisite of itself!\n"
            + "Perhaps you might have entered the new co-requisites module(s) incorrectly?";
    public static final String MESSAGE_NOT_EDITED = "You didn't specify what module details you want to edit.\n"
            + "Perhaps you would like to see the command format and example again?\n"
            + FORMAT_AND_EXAMPLES;
    public static final String MESSAGE_NON_EXISTENT_COREQUISITE =
            "You cannot edit the module (%1$s) to have a co-requisite module (%2$s) "
            + "that does not exists in the module list!\n"
            + "[Tip] You can add the module (%1$s), and specify the module (%2$s) as a co-requisite instead!\n";

    private final Index index;
    private final EditModuleDescriptor editModuleDescriptor;

    /**
     * @param index of the module in the filtered module list to edit
     * @param editModuleDescriptor details to edit the module with
     */
    public EditCommand(Index index, EditModuleDescriptor editModuleDescriptor) {
        requireAllNonNull(index, editModuleDescriptor);

        this.index = index;
        this.editModuleDescriptor = editModuleDescriptor;
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {
        requireNonNull(model);

        List<Module> lastShownList = model.getFilteredModuleList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_MODULE_DISPLAYED_INDEX);
        }

        Module moduleToEdit = lastShownList.get(index.getZeroBased());
        Module editedModule = createEditedModule(moduleToEdit, editModuleDescriptor);

        if (!moduleToEdit.isSameModule(editedModule) && model.hasModule(editedModule)) {
            throw new CommandException(
                    String.format(MESSAGE_DUPLICATE_MODULE, moduleToEdit.getCode(), editedModule.getCode())
            );
        }
        for (Code corequisite : editedModule.getCorequisites()) {
            if (moduleToEdit.getCode().equals(corequisite)) {
                throw new CommandException(MESSAGE_INVALID_COREQUISITE);
            } else if (!model.hasModuleCode(corequisite)) {
                throw new CommandException(
                        String.format(MESSAGE_NON_EXISTENT_COREQUISITE, moduleToEdit.getCode(), corequisite)
                );
            }
        }

        model.editModule(moduleToEdit, editedModule);
        model.updateFilteredModuleList(PREDICATE_SHOW_ALL_MODULES);
        model.commitAddressBook();

        Module editedModuleInModuleList = model.getFilteredModuleList().stream()
                .filter(module -> editedModule.getCode().equals(module.getCode())).findFirst().get();

        return new CommandResult(
                String.format(MESSAGE_EDIT_MODULE_SUCCESS, moduleToEdit.getCode(), editedModuleInModuleList)
        );
    }

    /**
     * Creates and returns a {@code Module} with the details of {@code moduleToEdit}
     * edited with {@code editModuleDescriptor}.
     */
    private static Module createEditedModule(Module moduleToEdit, EditModuleDescriptor editModuleDescriptor) {
        assert moduleToEdit != null;
        assert editModuleDescriptor != null;

        Name updatedName = editModuleDescriptor.getName().orElse(moduleToEdit.getName());
        Credits updatedCredits = editModuleDescriptor.getCredits().orElse(moduleToEdit.getCredits());
        Code updatedCode = editModuleDescriptor.getCode().orElse(moduleToEdit.getCode());
        Set<Tag> updatedTags = editModuleDescriptor.getTags().orElse(moduleToEdit.getTags());
        Set<Code> updatedCorequisites = editModuleDescriptor.getCorequisites().orElse(moduleToEdit.getCorequisites());

        return new Module(updatedName, updatedCredits, updatedCode, updatedTags, updatedCorequisites);
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand)) {
            return false;
        }

        // state check
        EditCommand e = (EditCommand) other;
        return index.equals(e.index)
                && editModuleDescriptor.equals(e.editModuleDescriptor);
    }

    /**
     * Stores the details to edit the module with.<br>
     * Each non-empty field value will replace the corresponding field value of the module.
     */
    public static class EditModuleDescriptor {
        private Name name;
        private Credits credits;
        private Code code;
        private Set<Tag> tags;
        private Set<Code> corequisites;

        public EditModuleDescriptor() {}

        /**
         * Copy constructor.<br>
         * Defensive copies of {@code tags} and {@code corequisites} are used internally to prevent accidental
         * modifications.
         */
        public EditModuleDescriptor(EditModuleDescriptor toCopy) {
            requireNonNull(toCopy);

            setName(toCopy.name);
            setCredits(toCopy.credits);
            setCode(toCopy.code);
            setTags(toCopy.tags);
            setCorequisites(toCopy.corequisites);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return isAnyNonNull(name, credits, code, tags, corequisites);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setCredits(Credits credits) {
            this.credits = credits;
        }

        public Optional<Credits> getCredits() {
            return Optional.ofNullable(credits);
        }

        public void setCode(Code code) {
            this.code = code;
        }

        public Optional<Code> getCode() {
            return Optional.ofNullable(code);
        }

        /**
         * Sets {@code tags} to this object's {@link #tags}.<br>
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null)
                    ? new HashSet<>(tags)
                    : null;
        }

        /**
         * Returns an unmodifiable {@link Tag} set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * <br><br>
         * Returns {@code Optional#empty()} if {@link #tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return tags != null
                    ? Optional.of(Collections.unmodifiableSet(tags))
                    : Optional.empty();
        }

        /**
         * Sets {@code corequisites} to this object's {@link #corequisites}.
         * A defensive copy of {@code corequisites} is used internally.
         */
        public void setCorequisites(Set<Code> corequisites) {
            this.corequisites = (corequisites != null)
                    ? new HashSet<>(corequisites)
                    : null;
        }

        /**
         * Returns an unmodifiable {@link Code} set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * <br><br>
         * Returns {@code Optional#empty()} if {@code corequisites} is null.
         */
        public Optional<Set<Code>> getCorequisites() {
            return corequisites != null
                    ? Optional.of(Collections.unmodifiableSet(corequisites))
                    : Optional.empty();
        }

        @Override
        public boolean equals(Object other) {
            // short circuit if same object
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditModuleDescriptor)) {
                return false;
            }

            // state check
            EditModuleDescriptor e = (EditModuleDescriptor) other;
            return getName().equals(e.getName())
                    && getCredits().equals(e.getCredits())
                    && getCode().equals(e.getCode())
                    && getTags().equals(e.getTags())
                    && getCorequisites().equals(e.getCorequisites());
        }
    }
}
