package pwe.planner.logic.commands;

import static java.util.Objects.requireNonNull;
import static pwe.planner.commons.core.Messages.MESSAGE_INVALID_MODULE_DISPLAYED_INDEX;
import static pwe.planner.commons.util.CollectionUtil.isAnyNonNull;
import static pwe.planner.commons.util.CollectionUtil.requireAllNonNull;
import static pwe.planner.commons.util.StringUtil.joinStreamAsString;
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
    public static final String MESSAGE_COREQUISITES_NOT_IN_DEGREE_PLANNER =
            "You cannot edit the module (%1$s) to have co-requisite module(s) (%2$s) when Year %3$s Semester %4$s of "
            + "the degree plan contains the module (%1$s), but not the all the co-requisite module(s) (%5$s)!\n"
            + "[Tip] You can use the \"planner_add\" command to add the co-requisite module(s) (%5$s) into Year %3$s "
            + "Semester %4$s of the degree plan, then edit the module (%1$s) to have co-requisite module(s) (%2$s)!";
    public static final String MESSAGE_DUPLICATE_MODULE =
            "You cannot edit module (%1$s) to have the module code (%2$s).\n"
            + "Module (%2$s) already exists in the module list!";
    public static final String MESSAGE_SELF_REFERENCING_COREQUISITE =
            "You cannot edit the module (%1$s) to have itself as a co-requisite module!\n"
            + "Perhaps you might have entered the new co-requisites module(s) incorrectly?";
    public static final String MESSAGE_INVALID_SEMESTER =
            "You cannot edit the semesters offering module (%1$s) from (%2$s) to (%3$s), as the module can no longer "
            + "be taken in Year %4$s Semester %5$s (as indicated in your degree plan)!\n"
            + "[Tip] You can remove module (%1$s) from the degree plan first, edit the semesters offering the module "
            + "to (%3$s), and then add the module back to a suitable year/semester of the degree plan!\n";
    public static final String MESSAGE_NON_EXISTENT_COREQUISITE =
            "You cannot edit the module (%1$s) to have a co-requisite module(s) (%2$s) "
            + "that does not exists in the module list!\n"
            + "[Tip] You can add the module (%1$s), and specify the module(s) (%2$s) as co-requisite module(s) instead!"
            + '\n';
    public static final String MESSAGE_NOT_EDITED = "You didn't specify what module details you want to edit.\n"
            + "Perhaps you would like to see the command format and example again?\n"
            + FORMAT_AND_EXAMPLES;
    public static final String MESSAGE_MODULE_AND_COREQUISITES_DIFFERENT_SEMESTERS =
            "You cannot edit the module (%1$s) to have co-requisite module(s) that are already being "
            + "added to different semesters of the degree plan!\n"
            + "[Tip] You can move all co-requisite module(s) to the same semester, or remove them from the degree plan."
            + "\nBelow is a list of the co-requisite module(s) that are causing the error:\n%2$s";
    public static final String MESSAGE_MODULE_NOT_IN_DEGREE_PLANNER_BUT_SOME_COREQUISITES_IN_DEGREE_PLANNER =
            "You cannot edit the module (%1$s) to have co-requisite module(s) (%2$s) that are already added to Year "
            + "%3$s Semester %4$s of the degree plan!\n"
            + "[Tip] You can add the module (%1$s) to Year %3$s Semester %4$s of the degree plan first, then edit "
            + "the module (%1$s) to have co-requisite modules (%5$s)!";

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

        List<Module> lastShownModuleList = model.getFilteredModuleList();
        ensureValidIndexInModuleList(lastShownModuleList);

        Module moduleToEdit = lastShownModuleList.get(index.getZeroBased());
        Module editedModule = createEditedModule(moduleToEdit, editModuleDescriptor);

        ensureEditedModuleIsUniqueInModel(model, moduleToEdit, editedModule);
        ensureEditedModuleIsNotCorequisiteOfItself(moduleToEdit, editedModule);
        ensureEditedCorequisitesExistsInModel(model, moduleToEdit, editedModule);
        if (!moduleToEdit.getCorequisites().equals(editedModule.getCorequisites())) {
            ensureDegreePlannerUnaffectedByEditedCorequisites(model, moduleToEdit, editedModule);
        }
        if (!moduleToEdit.getSemesters().equals(editedModule.getSemesters())) {
            ensureDegreePlannerUnaffectedByEditedSemesters(model, moduleToEdit, editedModule);
        }

        // Edit and cascade the changes of the module to the rest of the entire application
        model.editModule(moduleToEdit, editedModule);
        model.updateFilteredModuleList(PREDICATE_SHOW_ALL_MODULES);
        model.commitApplication();

        // Get latest copy of edited module after cascading changes
        Optional<Module> editedModuleInModuleList = model.getFilteredModuleList().stream()
                .filter(module -> editedModule.getCode().equals(module.getCode())).findFirst();

        assert editedModuleInModuleList.isPresent();

        String successMessage = String.format(MESSAGE_EDIT_MODULE_SUCCESS, moduleToEdit.getCode(),
                editedModuleInModuleList.get());
        return new CommandResult(successMessage);
    }

    /**
     * Ensures that {@code index} is valid in the displayed {@code module list}.
     * @param lastShownModuleList the displayed module list
     * @throws CommandException
     */
    private void ensureValidIndexInModuleList(List<Module> lastShownModuleList) throws CommandException {
        assert lastShownModuleList != null;

        if (index.getZeroBased() >= lastShownModuleList.size()) {
            throw new CommandException(MESSAGE_INVALID_MODULE_DISPLAYED_INDEX);
        }
    }

    /**
     * Ensures that the edited module is unique in the module list.
     * @throws CommandException if editing the module will result in duplicate modules
     */
    private void ensureEditedModuleIsUniqueInModel(Model model, Module moduleToEdit, Module editedModule)
            throws CommandException {
        assert model != null;
        assert moduleToEdit != null;
        assert editedModule != null;

        if (!moduleToEdit.isSameModule(editedModule) && model.hasModule(editedModule)) {
            String exceptionMessage = String.format(MESSAGE_DUPLICATE_MODULE, moduleToEdit.getCode(),
                    editedModule.getCode());
            throw new CommandException(exceptionMessage);
        }
    }

    /**
     * Ensures that the edited module is not a co-requisite of itself.
     * @throws CommandException if the above constraint is violated.
     */
    private void ensureEditedModuleIsNotCorequisiteOfItself(Module moduleToEdit, Module editedModule)
            throws CommandException {
        assert moduleToEdit != null;
        assert editedModule != null;

        boolean hasOriginalCodeInCorequisites = editedModule.getCorequisites().contains(moduleToEdit.getCode());
        boolean hasEditedCodeInCorequisites = editedModule.getCorequisites().contains(editedModule.getCode());
        if (hasOriginalCodeInCorequisites || hasEditedCodeInCorequisites) {
            String exceptionMessage = String.format(MESSAGE_SELF_REFERENCING_COREQUISITE, moduleToEdit.getCode());
            throw new CommandException(exceptionMessage);
        }
    }

    /**
     * Ensures that the corequisites of the edited module exists in the module list.
     * @throws CommandException if the above constraint is violated.
     */
    private void ensureEditedCorequisitesExistsInModel(Model model, Module moduleToEdit, Module editedModule)
            throws CommandException {
        assert model != null;
        assert moduleToEdit != null;
        assert editedModule != null;

        Set<Code> nonExistentCorequisites = editedModule.getCorequisites().stream()
                .filter(corequisite -> !model.hasModuleCode(corequisite)).collect(Collectors.toSet());

        if (!nonExistentCorequisites.isEmpty()) {
            String exceptionMessage = String.format(MESSAGE_NON_EXISTENT_COREQUISITE, moduleToEdit.getCode(),
                    joinStreamAsString(nonExistentCorequisites.stream().sorted()));
            throw new CommandException(exceptionMessage);
        }
    }

    /**
     * Ensures that the editing the module does not affect the data integrity of the degree planner.
     * - Edited module is not in the degree plan, but at least one co-requisite module is in the degree plan.
     * - Edited module and corequisites exists in different semesters of the degree plan.
     * - Edited module is in the degree plan, but at least one co-requisite module is not.
     * @throws CommandException if any of the above constraints is violated.
     */
    private void ensureDegreePlannerUnaffectedByEditedCorequisites(Model model, Module moduleToEdit,
            Module editedModule) throws CommandException {
        assert model != null;
        assert moduleToEdit != null;
        assert editedModule != null;

        List<Code> editedCorequisitesInDegreePlanner = editedModule.getCorequisites().stream()
                .filter(code -> model.getDegreePlannerByCode(code) != null).collect(Collectors.toList());

        List<DegreePlanner> degreePlannersContainingEditedCorequisites = editedCorequisitesInDegreePlanner.stream()
                .map(model::getDegreePlannerByCode).distinct().collect(Collectors.toList());

        if (degreePlannersContainingEditedCorequisites.size() > 1) {
            List<DegreePlanner> problematicDegreePlanners = degreePlannersContainingEditedCorequisites.stream()
                    .map((degreePlanner) -> {
                        Set<Code> newCodes = new HashSet<>(degreePlanner.getCodes());
                        newCodes.retainAll(editedModule.getCorequisites());
                        return new DegreePlanner(degreePlanner.getYear(), degreePlanner.getSemester(), newCodes);
                    }).collect(Collectors.toList());

            String exceptionMessage = String.format(MESSAGE_MODULE_AND_COREQUISITES_DIFFERENT_SEMESTERS,
                    moduleToEdit.getCode(), joinStreamAsString(problematicDegreePlanners.stream().sorted(), "\n"));
            throw new CommandException(exceptionMessage);
        }

        DegreePlanner degreePlannerContainingModuleToEdit = model.getDegreePlannerByCode(moduleToEdit.getCode());
        boolean isModuleToEditInDegreePlanner = degreePlannerContainingModuleToEdit != null;
        boolean isEditedCorequisitesInDegreePlanner = !degreePlannersContainingEditedCorequisites.isEmpty();

        if (!isModuleToEditInDegreePlanner && isEditedCorequisitesInDegreePlanner) {
            DegreePlanner degreePlannerContainingEditedCorequisites = degreePlannersContainingEditedCorequisites.get(0);
            Set<Code> existingCorequisites = degreePlannerContainingEditedCorequisites.getCodes().stream()
                    .filter(editedModule.getCorequisites()::contains).collect(Collectors.toSet());
            String existingCorequisitesInDegreePlanner = joinStreamAsString(existingCorequisites.stream().sorted());
            String year = degreePlannersContainingEditedCorequisites.get(0).getYear().toString();
            String semester = degreePlannersContainingEditedCorequisites.get(0).getSemester().toString();
            String editedCorequisites = joinStreamAsString(editedModule.getCorequisites().stream().sorted());
            String exceptionMessage = String.format(
                    MESSAGE_MODULE_NOT_IN_DEGREE_PLANNER_BUT_SOME_COREQUISITES_IN_DEGREE_PLANNER,
                    moduleToEdit.getCode(), existingCorequisitesInDegreePlanner, year, semester, editedCorequisites);
            throw new CommandException(exceptionMessage);
        }
        if (isModuleToEditInDegreePlanner && isEditedCorequisitesInDegreePlanner) {
            DegreePlanner degreePlannerContainingEditedCorequisites = degreePlannersContainingEditedCorequisites.get(0);
            boolean isDegreePlannersSemesterEquals = degreePlannerContainingEditedCorequisites.getSemester()
                    .equals(degreePlannerContainingModuleToEdit.getSemester());
            if (!isDegreePlannersSemesterEquals) {
                List<DegreePlanner> problematicDegreePlanners = degreePlannersContainingEditedCorequisites.stream()
                        .map((degreePlanner) -> {
                            Set<Code> newCodes = new HashSet<>(degreePlanner.getCodes());
                            newCodes.retainAll(editedModule.getCorequisites());
                            return new DegreePlanner(degreePlanner.getYear(), degreePlanner.getSemester(), newCodes);
                        }).collect(Collectors.toList());

                String exceptionMessage = String.format(MESSAGE_MODULE_AND_COREQUISITES_DIFFERENT_SEMESTERS,
                        moduleToEdit.getCode(), joinStreamAsString(problematicDegreePlanners.stream().sorted(), "\n"));
                throw new CommandException(exceptionMessage);
            }
        }

        boolean hasMissingCorequisitesInDegreePlanner =
                editedCorequisitesInDegreePlanner.size() != editedModule.getCorequisites().size();

        if (isModuleToEditInDegreePlanner && hasMissingCorequisitesInDegreePlanner) {
            Set<Code> missingCorequisites = new HashSet<>(editedModule.getCorequisites());
            missingCorequisites.removeAll(editedCorequisitesInDegreePlanner);
            String exceptionMessage = String.format(MESSAGE_COREQUISITES_NOT_IN_DEGREE_PLANNER, moduleToEdit.getCode(),
                    joinStreamAsString(editedModule.getCorequisites().stream().sorted()),
                    degreePlannerContainingModuleToEdit.getYear(), degreePlannerContainingModuleToEdit.getSemester(),
                    joinStreamAsString(missingCorequisites.stream().sorted()));
            throw new CommandException(exceptionMessage);
        }
    }

    /**
     * Ensures that the edited module remains valid after updating semesters.
     * If the module is in semester X, but the edited module's semesters do not contain X, the edited module is
     * considered invalid.
     * @throws CommandException if the above constraint is violated.
     */
    private void ensureDegreePlannerUnaffectedByEditedSemesters(Model model, Module moduleToEdit,
            Module editedModule) throws CommandException {
        assert model != null;
        assert moduleToEdit != null;
        assert editedModule != null;

        DegreePlanner degreePlannerContainingModuleToEdit = model.getDegreePlannerByCode(moduleToEdit.getCode());
        if (degreePlannerContainingModuleToEdit == null) {
            return;
        }

        if (!editedModule.getSemesters().contains(degreePlannerContainingModuleToEdit.getSemester())) {
            String semestersToEdit = joinStreamAsString(moduleToEdit.getSemesters().stream().sorted());
            String editedSemesters = joinStreamAsString(editedModule.getSemesters().stream().sorted());
            String exceptionMessage = String.format(MESSAGE_INVALID_SEMESTER, moduleToEdit.getCode(), semestersToEdit,
                    editedSemesters, degreePlannerContainingModuleToEdit.getYear(),
                    degreePlannerContainingModuleToEdit.getSemester());
            throw new CommandException(exceptionMessage);
        }
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
