package pwe.planner.logic.commands;

import static java.util.Objects.requireNonNull;
import static pwe.planner.commons.core.Messages.MESSAGE_INVALID_MODULE_DISPLAYED_INDEX;
import static pwe.planner.commons.util.CollectionUtil.isAnyNonNull;
import static pwe.planner.commons.util.CollectionUtil.requireAllNonNull;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_CODE;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_COREQUISITE;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_CREDITS;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_NAME;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_SEMESTER;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_TAG;
import static pwe.planner.model.Model.PREDICATE_SHOW_ALL_MODULES;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import pwe.planner.commons.core.index.Index;
import pwe.planner.logic.CommandHistory;
import pwe.planner.logic.commands.exceptions.CommandException;
import pwe.planner.model.Application;
import pwe.planner.model.Model;
import pwe.planner.model.module.Code;
import pwe.planner.model.module.Credits;
import pwe.planner.model.module.Module;
import pwe.planner.model.module.Name;
import pwe.planner.model.planner.DegreePlanner;
import pwe.planner.model.planner.Semester;
import pwe.planner.model.tag.Tag;

/**
 * Edits the details of an existing {@link Module} in the {@link Application#modules module list}.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    // This is declared before MESSAGE_USAGE to prevent illegal forward reference
    public static final String FORMAT_AND_EXAMPLES = "Format: " + COMMAND_WORD
            + " INDEX "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_CODE + "CODE] "
            + "[" + PREFIX_CREDITS + "CREDITS] "
            + "[" + PREFIX_SEMESTER + "SEMESTER]..."
            + "[" + PREFIX_COREQUISITE + "COREQUISITE]...\n"
            + "[" + PREFIX_TAG + "TAG]... "
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
    public static final String MESSAGE_INVALID_SEMESTER =
            "You cannot edit the semesters offering module (%1$s) from (%2$s) to (%3$s), as the module can no longer "
            + "be taken in Year %4$s Semester %5$s (according to your degree plan)!\n"
            + "[Tip] You can remove module (%1$s) from the degree plan first, edit the semesters offering the module "
            + "to (%3$s), and then add the module back to a suitable year/semester of the degree plan!\n";
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
            throw new CommandException(MESSAGE_INVALID_MODULE_DISPLAYED_INDEX);
        }

        Module moduleToEdit = lastShownList.get(index.getZeroBased());
        Module editedModule = createEditedModule(moduleToEdit, editModuleDescriptor);

        // Check if module to edit is the same as edited module
        if (!moduleToEdit.isSameModule(editedModule) && model.hasModule(editedModule)) {
            throw new CommandException(
                    String.format(MESSAGE_DUPLICATE_MODULE, moduleToEdit.getCode(), editedModule.getCode())
            );
        }

        boolean hasSemestersChanged = !moduleToEdit.getSemesters().equals(editedModule.getSemesters());
        Optional<DegreePlanner> degreePlannerContainingModuleToEdit = model.getApplication().getDegreePlannerList()
                .stream()
                .filter(degreePlanner -> (degreePlanner.getCodes().contains(moduleToEdit.getCode())))
                .findFirst();

        /**
         * Checks if there are any issues updating a module's semesters.
         * There is no issues updating a module's semesters if the module is not added to any {@link DegreePlanner}.
         * However, if {@code moduleToEdit} exists in a {@link DegreePlanner}} and {@code moduleToEdit#getSemesters()}
         * is different from {@code editedModule#getSemeters()}, we need to check if {@link DegreePlanner#getSemester()}
         * is within {@code editedModule#getSemesters()}.
         *
         * If {@link DegreePlanner#getSemester()} is within {@code editedModule#getSemesters()}, there are no issues.
         * otherwise, editing the semesters of the module will violate data integrity, which results in an exception.
         */
        if (hasSemestersChanged && degreePlannerContainingModuleToEdit.isPresent()) {
            DegreePlanner degreePlanner = degreePlannerContainingModuleToEdit.get();
            Semester semester = degreePlanner.getSemester();
            if (!editedModule.getSemesters().contains(semester)) {
                String semestersToEdit = moduleToEdit.getSemesters().stream().sorted().map(Semester::toString)
                        .collect(Collectors.joining(", "));
                if (semestersToEdit.isEmpty()) {
                    semestersToEdit = "None";
                }
                String editedSemesters = editedModule.getSemesters().stream().sorted().map(Semester::toString)
                        .collect(Collectors.joining(", "));
                if (editedSemesters.isEmpty()) {
                    editedSemesters = "None";
                }
                String exceptionMessage = String.format(MESSAGE_INVALID_SEMESTER, moduleToEdit.getCode(),
                        semestersToEdit, editedSemesters, degreePlanner.getYear(), degreePlanner.getSemester());
                throw new CommandException(exceptionMessage);
            }
        }

        // Ensure that all module co-requisites exists, and no module co-requisite is the same as code of module to edit
        for (Code corequisite : editedModule.getCorequisites()) {
            if (moduleToEdit.getCode().equals(corequisite)) {
                throw new CommandException(MESSAGE_INVALID_COREQUISITE);
            } else if (!model.hasModuleCode(corequisite)) {
                throw new CommandException(
                        String.format(MESSAGE_NON_EXISTENT_COREQUISITE, moduleToEdit.getCode(), corequisite)
                );
            }
        }

        // Edit and cascade the changes of the module to the rest of the entire application
        model.editModule(moduleToEdit, editedModule);
        model.updateFilteredModuleList(PREDICATE_SHOW_ALL_MODULES);
        model.commitApplication();

        // Get latest copy of edited module after cascading changes
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

        Code updatedCode = editModuleDescriptor.getCode().orElse(moduleToEdit.getCode());
        Name updatedName = editModuleDescriptor.getName().orElse(moduleToEdit.getName());
        Credits updatedCredits = editModuleDescriptor.getCredits().orElse(moduleToEdit.getCredits());
        Set<Code> updatedCorequisites = editModuleDescriptor.getCorequisites().orElse(moduleToEdit.getCorequisites());
        Set<Semester> updatedSemesters = editModuleDescriptor.getSemesters().orElse(moduleToEdit.getSemesters());
        Set<Tag> updatedTags = editModuleDescriptor.getTags().orElse(moduleToEdit.getTags());

        return new Module(updatedCode, updatedName, updatedCredits, updatedSemesters, updatedCorequisites, updatedTags);
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
        private Code code;
        private Name name;
        private Credits credits;
        private Set<Code> corequisites;
        private Set<Semester> semesters;
        private Set<Tag> tags;

        public EditModuleDescriptor() {}

        /**
         * Copy constructor.<br>
         * Defensive copies of {@code tags} and {@code corequisites} are used internally to prevent accidental
         * modifications.
         */
        public EditModuleDescriptor(EditModuleDescriptor toCopy) {
            requireNonNull(toCopy);

            setCode(toCopy.code);
            setName(toCopy.name);
            setCredits(toCopy.credits);
            setCorequisites(toCopy.corequisites);
            setSemesters(toCopy.semesters);
            setTags(toCopy.tags);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return isAnyNonNull(code, name, credits, corequisites, semesters, tags);
        }

        public Optional<Code> getCode() {
            return Optional.ofNullable(code);
        }

        public void setCode(Code code) {
            this.code = code;
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

        /**
         * Returns an unmodifiable {@link Semester} set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * <br><br>
         * Returns {@code Optional#empty()} if {@code semesters} is null.
         */
        public Optional<Set<Semester>> getSemesters() {
            return semesters != null
                    ? Optional.of(Collections.unmodifiableSet(semesters))
                    : Optional.empty();
        }

        /**
         * Sets {@code semesters} to this object's {@link #semesters}.
         * A defensive copy of {@code semesters} is used internally.
         */
        public void setSemesters(Set<Semester> semesters) {
            this.semesters = (semesters != null)
                    ? new HashSet<>(semesters)
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
         * Sets {@code tags} to this object's {@link #tags}.<br>
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null)
                    ? new HashSet<>(tags)
                    : null;
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
            return getCode().equals(e.getCode())
                    && getName().equals(e.getName())
                    && getCredits().equals(e.getCredits())
                    && getSemesters().equals(e.getSemesters())
                    && getCorequisites().equals(e.getCorequisites())
                    && getTags().equals(e.getTags());
        }
    }
}
