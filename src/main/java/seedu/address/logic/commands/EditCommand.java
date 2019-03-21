package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
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
import seedu.address.commons.util.CollectionUtil;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.module.Code;
import seedu.address.model.module.Credits;
import seedu.address.model.module.Module;
import seedu.address.model.module.Name;
import seedu.address.model.tag.Tag;

/**
 * Edits the details of an existing module in the address book.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the module identified "
            + "by the index number used in the displayed module list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_CODE + "CODE] "
            + "[" + PREFIX_CREDITS + "CREDITS] "
            + "[" + PREFIX_TAG + "TAG]... "
            + "[" + PREFIX_COREQUISITE + "COREQUISITE]...\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_CREDITS + "8 ";

    public static final String MESSAGE_EDIT_MODULE_SUCCESS = "Edited Module: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_MODULE = "This module already exists in the address book.";
    public static final String MESSAGE_INVALID_COREQUISITE =
            "The module code (%1$s) cannot be a co-requisite of itself!";
    public static final String MESSAGE_NON_EXISTENT_COREQUISITE =
            "The corequisite module code (%1$s) does not exists in the module list";

    private final Index index;
    private final EditModuleDescriptor editModuleDescriptor;

    /**
     * @param index of the module in the filtered module list to edit
     * @param editModuleDescriptor details to edit the module with
     */
    public EditCommand(Index index, EditModuleDescriptor editModuleDescriptor) {
        requireNonNull(index);
        requireNonNull(editModuleDescriptor);

        this.index = index;
        this.editModuleDescriptor = new EditModuleDescriptor(editModuleDescriptor);
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
            throw new CommandException(MESSAGE_DUPLICATE_MODULE);
        }
        for (Code corequisite : editedModule.getCorequisites()) {
            if (moduleToEdit.getCode().equals(corequisite)) {
                throw new CommandException(String.format(MESSAGE_INVALID_COREQUISITE, corequisite));
            } else if (!model.hasModuleCode(corequisite)) {
                throw new CommandException(String.format(MESSAGE_NON_EXISTENT_COREQUISITE, corequisite));
            }
        }

        model.setModule(moduleToEdit, editedModule);
        model.updateFilteredModuleList(PREDICATE_SHOW_ALL_MODULES);
        model.commitAddressBook();
        return new CommandResult(String.format(MESSAGE_EDIT_MODULE_SUCCESS, editedModule));
    }

    /**
     * Creates and returns a {@code Module} with the details of {@code moduleToEdit}
     * edited with {@code editModuleDescriptor}.
     */
    private static Module createEditedModule(Module moduleToEdit, EditModuleDescriptor editModuleDescriptor) {
        assert moduleToEdit != null;

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
     * Stores the details to edit the module with. Each non-empty field value will replace the
     * corresponding field value of the module.
     */
    public static class EditModuleDescriptor {
        private Name name;
        private Credits credits;
        private Code code;
        private Set<Tag> tags;
        private Set<Code> corequisites;

        public EditModuleDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditModuleDescriptor(EditModuleDescriptor toCopy) {
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
            return CollectionUtil.isAnyNonNull(name, credits, code, tags, corequisites);
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
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        /**
         * Sets {@code corequisites} to this object's {@code corequisites}.
         * A defensive copy of {@code corequisites} is used internally.
         */
        public void setCorequisites(Set<Code> corequisites) {
            this.corequisites = (corequisites != null) ? new HashSet<>(corequisites) : null;
        }

        /**
         * Returns an unmodifiable {@code Code} set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code corequisites} is null.
         */
        public Optional<Set<Code>> getCorequisites() {
            return (corequisites != null) ? Optional.of(Collections.unmodifiableSet(corequisites)) : Optional.empty();
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
