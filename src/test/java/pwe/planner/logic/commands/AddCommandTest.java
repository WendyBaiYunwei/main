package pwe.planner.logic.commands;

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
import pwe.planner.commons.core.GuiSettings;
import pwe.planner.logic.CommandHistory;
import pwe.planner.logic.commands.exceptions.CommandException;
import pwe.planner.model.Application;
import pwe.planner.model.Model;
import pwe.planner.model.ReadOnlyApplication;
import pwe.planner.model.ReadOnlyUserPrefs;
import pwe.planner.model.module.Code;
import pwe.planner.model.module.Module;
import pwe.planner.model.module.Name;
import pwe.planner.model.planner.DegreePlanner;
import pwe.planner.model.requirement.RequirementCategory;
import pwe.planner.testutil.ModuleBuilder;

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
        thrown.expectMessage(String.format(AddCommand.MESSAGE_DUPLICATE_MODULE, validModule.getCode()));
        addCommand.execute(modelStub, commandHistory);
    }

    @Test
    public void execute_nonExistentCorequisites_throwsCommandException() throws Exception {
        String nonExistentCorequisite = "ZYX9876W";
        Module invalidModule = new ModuleBuilder().withCorequisites(nonExistentCorequisite).build();
        AddCommand addCommand = new AddCommand(invalidModule);
        ModelStub modelStub = new ModelStubWithModule(invalidModule);
        String expectedMessage = String.format(AddCommand.MESSAGE_DUPLICATE_MODULE, invalidModule.getCode(),
                nonExistentCorequisite);

        thrown.expect(CommandException.class);
        thrown.expectMessage(expectedMessage);
        addCommand.execute(modelStub, commandHistory);
    }

    @Test
    public void execute_addMultipleValidCorequisites_successful() throws CommandException {
        ModelStubAcceptingModuleAdded modelStub = new ModelStubAcceptingModuleAdded();
        Module moduleA = new ModuleBuilder().withCode("AA1111").build();
        Module moduleB = new ModuleBuilder().withCode("BB2222").build();
        modelStub.addModule(moduleA);
        modelStub.addModule(moduleB);

        Module moduleWithCorequisites = new ModuleBuilder().withCode("CC3333").withCorequisites("AA1111", "BB2222")
                .build();

        AddCommand addCommand = new AddCommand(moduleWithCorequisites);
        addCommand.execute(modelStub, commandHistory);
    }

    @Test
    public void equals() {
        Module cs1010 = new ModuleBuilder().withName("Programming Methodology").build();
        Module cs1231 = new ModuleBuilder().withName("Data Structures").build();
        AddCommand addCS1010Command = new AddCommand(cs1010);
        AddCommand addCS1231Command = new AddCommand(cs1231);

        // same object -> returns true
        assertTrue(addCS1010Command.equals(addCS1010Command));

        // same values -> returns true
        AddCommand addCS1010CommandCopy = new AddCommand(cs1010);
        assertTrue(addCS1010Command.equals(addCS1010CommandCopy));

        // different types -> returns false
        assertFalse(addCS1010Command.equals(1));

        // null -> returns false
        assertFalse(addCS1010Command.equals(null));

        // different module -> returns false
        assertFalse(addCS1010Command.equals(addCS1231Command));
    }

    /**
     * A default model stub that have all of the methods failing.
     */
    private class ModelStub implements Model {
        @Override
        public ReadOnlyUserPrefs getUserPrefs() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
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
        public Path getModuleListFilePath() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setModuleListFilePath(Path moduleListFilePath) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Path getDegreePlannerListFilePath() {
            //ToDo: implement error check
            return null;
        }

        @Override
        public void setDegreePlannerListFilePath(Path degreePlannerListFilePath) {
            //ToDo: implement error check
        }

        @Override
        public Path getRequirementCategoryListFilePath() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setRequirementCategoryListFilePath(Path requirementCategoryListFilePath) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addModule(Module module) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyApplication getApplication() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setApplication(ReadOnlyApplication application) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void resetPlanner() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void resetRequirement() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasModule(Module module) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Module getModuleByCode(Code code) {
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
        public void editModule(Module target, Module editedModule) {
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
        public boolean canUndoApplication() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean canRedoApplication() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void undoApplication() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void redoApplication() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void commitApplication() {
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
        public boolean hasDegreePlanner(DegreePlanner degreePlanner) {
            return false;
        }

        @Override
        public DegreePlanner getDegreePlannerByCode(Code toCheck) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deleteDegreePlanner(DegreePlanner degreePlanner) {
            //ToDo: implement AssertionError
        }

        @Override
        public void addDegreePlanner(DegreePlanner degreePlanner) {
            //ToDo: implement error check
        }

        @Override
        public void setDegreePlanner(DegreePlanner target, DegreePlanner editedDegreePlanner) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void moveModuleBetweenPlanner(DegreePlanner sourcePlanner, DegreePlanner destinationPlanner, Code code) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<DegreePlanner> getFilteredDegreePlannerList() {
            return null;
        }

        @Override
        public void updateFilteredDegreePlannerList(Predicate<DegreePlanner> predicate) {
            //ToDo: implement error check
        }

        @Override
        public boolean hasRequirementCategory(Name requirementCategoryName) {
            return false;
        }

        @Override
        public boolean hasRequirementCategory(RequirementCategory requirementCategory) {
            return false;
        }

        @Override
        public RequirementCategory getRequirementCategory(Name requirementCategoryName) {
            //ToDo: implement error check
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addRequirementCategory(RequirementCategory requirementCategory) {
            //ToDo: implement error check
        }

        @Override
        public void setRequirementCategory(RequirementCategory target,
                RequirementCategory editedRequirementCategory) {
            //ToDo: implement error check
        }

        @Override
        public ObservableList<RequirementCategory> getFilteredRequirementCategoryList() {
            return null;
        }

        @Override
        public void updateFilteredRequirementCategoryList(Predicate<RequirementCategory> predicate) {
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
        public boolean hasModuleCode(Code code) {
            requireNonNull(code);

            return modulesAdded.stream().map(Module::getCode).anyMatch(code::equals);
        }

        @Override
        public void addModule(Module module) {
            requireNonNull(module);
            modulesAdded.add(module);
        }

        @Override
        public DegreePlanner getDegreePlannerByCode(Code toCheck) {
            requireNonNull(toCheck);
            return null;
        }

        @Override
        public void commitApplication() {
            // called by {@code AddCommand#execute()}
        }

        @Override
        public ReadOnlyApplication getApplication() {
            return new Application();
        }
    }

}
