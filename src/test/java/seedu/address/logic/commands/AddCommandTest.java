package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javafx.beans.property.ReadOnlyProperty;
import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyDegreePlannerList;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.module.Code;
import seedu.address.model.module.Module;
import seedu.address.model.planner.DegreePlanner;
import seedu.address.model.requirement.RequirementCategory;
import seedu.address.testutil.ModuleBuilder;

public class AddCommandTest {

    private static final CommandHistory EMPTY_COMMAND_HISTORY = new CommandHistory();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private CommandHistory commandHistory = new CommandHistory();

    @Test
    public void constructor_nullModule_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        new AddCommand(null);
    }

    @Test
    public void execute_moduleAcceptedByModel_addSuccessful() throws Exception {
        ModelStubAcceptingModuleAdded modelStub = new ModelStubAcceptingModuleAdded();
        Module validModule = new ModuleBuilder().build();

        CommandResult commandResult = new AddCommand(validModule).execute(modelStub, commandHistory);

        assertEquals(String.format(AddCommand.MESSAGE_SUCCESS, validModule), commandResult.getFeedbackToUser());
        assertEquals(Arrays.asList(validModule), modelStub.modulesAdded);
        assertEquals(EMPTY_COMMAND_HISTORY, commandHistory);
    }

    @Test
    public void execute_duplicateModule_throwsCommandException() throws Exception {
        Module validModule = new ModuleBuilder().build();
        AddCommand addCommand = new AddCommand(validModule);
        ModelStub modelStub = new ModelStubWithModule(validModule);

        thrown.expect(CommandException.class);
        thrown.expectMessage(AddCommand.MESSAGE_DUPLICATE_MODULE);
        addCommand.execute(modelStub, commandHistory);
    }

    @Test
    public void equals() {
        Module alice = new ModuleBuilder().withName("Alice").build();
        Module bob = new ModuleBuilder().withName("Bob").build();
        AddCommand addAliceCommand = new AddCommand(alice);
        AddCommand addBobCommand = new AddCommand(bob);

        // same object -> returns true
        assertTrue(addAliceCommand.equals(addAliceCommand));

        // same values -> returns true
        AddCommand addAliceCommandCopy = new AddCommand(alice);
        assertTrue(addAliceCommand.equals(addAliceCommandCopy));

        // different types -> returns false
        assertFalse(addAliceCommand.equals(1));

        // null -> returns false
        assertFalse(addAliceCommand.equals(null));

        // different module -> returns false
        assertFalse(addAliceCommand.equals(addBobCommand));
    }

    /**
     * A default model stub that have all of the methods failing.
     */
    private class ModelStub implements Model {
        @Override
        public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyUserPrefs getUserPrefs() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public GuiSettings getGuiSettings() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Path getAddressBookFilePath() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBookFilePath(Path addressBookFilePath) {
            throw new AssertionError("This method should not be called.");
        }

        @Override public Path getDegreePlannerListFilePath() {
            //ToDo: implement error check
            return null;
        }

        @Override public void setDegreePlannerListFilePath(Path degreePlannerListFilePath) {
            //ToDo: implement error check
        }

        @Override public Path getRequirementCategoryListFilePath() {
            throw new AssertionError("This method should not be called.");
        }

        @Override public void setRequirementCategoryListFilePath(Path requirementCategoryListFilePath) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addModule(Module module) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBook(ReadOnlyAddressBook newData) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasModule(Module module) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasModuleCode(Code code) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deleteModule(Module target) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setModule(Module target, Module editedModule) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Module> getFilteredModuleList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredModuleList(Predicate<Module> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean canUndoAddressBook() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean canRedoAddressBook() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void undoAddressBook() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void redoAddressBook() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void commitAddressBook() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyProperty<Module> selectedModuleProperty() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Module getSelectedModule() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setSelectedModule(Module module) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyDegreePlannerList getDegreePlannerList() {
            return null;
        }

        @Override public boolean hasDegreePlanner(DegreePlanner degreePlanner) {
            return false;
        }

        @Override
        public boolean hasDegreePlannerModules(DegreePlanner plannerModules) {
            //ToDo: implement error check
        }
        @Override public void deleteDegreePlanner(DegreePlanner degreePlanner) {
            //ToDo: implement AssertionError
        }

        @Override public void addDegreePlanner(DegreePlanner degreePlanner) {
            //ToDo: implement error check
        }

        @Override
        public void addDegreePlannerModules(DegreePlanner plannerModules) {
            //ToDo: implement error check
        }

        @Override public void setDegreePlanner(DegreePlanner target, DegreePlanner editedDegreePlanner) {
            //ToDo: implement error check
        }

        @Override public ObservableList<DegreePlanner> getFilteredDegreePlannerList() {
            return null;
        }

        @Override public void updateFilteredDegreePlannerList(Predicate<DegreePlanner> predicate) {
            //ToDo: implement error check
        }

        @Override public boolean canUndoDegreePlannerList() {
            //ToDo: implement AssertionError
            return false;
        }

        @Override public boolean canRedoDegreePlannerList() {
            //ToDo: implement error check
            return false;
        }

        @Override public void undoDegreePlannerList() {
            //ToDo: implement error check
        }

        @Override public void redoDegreePlannerList() {
            //ToDo: implement error check
        }

        @Override public void commitDegreePlannerList() {
            //ToDo: implement error check
        }

        @Override public boolean hasRequirementCategory(RequirementCategory degreePlanner) {
            //ToDo: implement error check
            return false;
        }

        @Override public void deleteRequirementCategory(RequirementCategory degreePlanner) {
            //ToDo: implement AssertionError
        }

        @Override public void addRequirementCategory(RequirementCategory degreePlanner) {
            //ToDo: implement error check
        }

        @Override public void setRequirementCategory(RequirementCategory target,
                RequirementCategory editedDegreePlanner) {
            //ToDo: implement error check
        }

        @Override
        public boolean isModuleInRequirementCategory(RequirementCategory requirementCategoryModule) {
            return false;
        }

        @Override
        public void addModuleToRequirementCategory(RequirementCategory requirementCategoryModule) {
            //ToDo: implement error check
        }

        @Override
        public boolean doesModuleExistInApplication(RequirementCategory requirementCategory, Model model) {
            return false;
        }

        @Override public ObservableList<RequirementCategory> getFilteredRequirementCategoryList() {
            return null;
        }

        @Override public void updateFilteredRequirementCategoryList(Predicate<RequirementCategory> predicate) {
            //ToDo: implement error check
        }

        @Override
        public RequirementCategory getSelectedRequirementCategory() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setSelectedRequirementCategory(RequirementCategory requirementCategory) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean existingPlannerModules(DegreePlanner toAdd, Model model) {
            //ToDo: implement error check
        }

        @Override
        public ReadOnlyProperty<RequirementCategory> selectedRequirementCategoryProperty() {
            throw new AssertionError("This method should not be called.");
        }

    }

    /**
     * A Model stub that contains a single module.
     */
    private class ModelStubWithModule extends ModelStub {
        private final Module module;

        ModelStubWithModule(Module module) {
            requireNonNull(module);
            this.module = module;
        }

        @Override
        public boolean hasModule(Module module) {
            requireNonNull(module);
            return this.module.isSameModule(module);
        }
    }

    /**
     * A Model stub that always accept the module being added.
     */
    private class ModelStubAcceptingModuleAdded extends ModelStub {
        final ArrayList<Module> modulesAdded = new ArrayList<>();

        @Override
        public boolean hasModule(Module module) {
            requireNonNull(module);
            return modulesAdded.stream().anyMatch(module::isSameModule);
        }

        @Override
        public void addModule(Module module) {
            requireNonNull(module);
            modulesAdded.add(module);
        }

        @Override
        public void commitAddressBook() {
            // called by {@code AddCommand#execute()}
        }


        @Override
        public ReadOnlyAddressBook getAddressBook() {
            return new AddressBook();
        }
    }

}
