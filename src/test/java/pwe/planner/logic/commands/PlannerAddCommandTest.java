package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static pwe.planner.testutil.DegreePlannerBuilder.DEFAULT_SEMESTER;

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
import pwe.planner.logic.commands.CommandResult;
import pwe.planner.logic.commands.PlannerAddCommand;
import pwe.planner.logic.commands.exceptions.CommandException;
import pwe.planner.model.Application;
import pwe.planner.model.Model;
import pwe.planner.model.ReadOnlyApplication;
import pwe.planner.model.ReadOnlyUserPrefs;
import pwe.planner.model.module.Code;
import pwe.planner.model.module.Module;
import pwe.planner.model.module.Name;
import pwe.planner.model.planner.DegreePlanner;
import pwe.planner.model.planner.Semester;
import pwe.planner.model.planner.Year;
import pwe.planner.testutil.ModuleBuilder;
import pwe.planner.testutil.SemesterBuilder;
import pwe.planner.testutil.YearBuilder;

public class PlannerAddCommandTest {

    private static final CommandHistory EMPTY_COMMAND_HISTORY = new CommandHistory();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private CommandHistory commandHistory = new CommandHistory();

    @Test
    public void constructor_nullYear_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        Semester defaultSemester = new SemesterBuilder().build();
        Code defaultCode = new ModuleBuilder().build().getCode();
        new PlannerAddCommand(null, defaultSemester, defaultCode);
    }

    @Test
    public void constructor_nullSemester_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        Year defaultYear = new YearBuilder().build();
        Code defaultCode = new ModuleBuilder().build().getCode();
        new PlannerAddCommand(defaultYear, null, defaultCode);
    }

    @Test
    public void constructor_nullCode_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        Year defaultYear = new YearBuilder().build();
        Semester defaultSemester = new SemesterBuilder().build();
        new PlannerAddCommand(defaultYear, defaultSemester, null);
    }

    @Test
    public void execute_parametersAcceptedByModel_addSuccessful() throws Exception {
        ModelStubAcceptingCodeAdded modelStub = new ModelStubAcceptingCodeAdded();
        Year validYear = new YearBuilder().build();
        Semester validSemester = new SemesterBuilder().build();
        Code validCode = new ModuleBuilder().build().getCode();

        CommandResult commandResult = new PlannerAddCommand(validYear, validSemester, validCode)
                .execute(modelStub, commandHistory);

        assertEquals(String.format(PlannerAddCommand.MESSAGE_SUCCESS, validCode), commandResult.getFeedbackToUser());
        assertEquals(Arrays.asList(validCode), modelStub.plannerCodesAdded);
        assertEquals(EMPTY_COMMAND_HISTORY, commandHistory);
    }

    @Test
    public void execute_duplicatePlannerCodes_throwsCommandException() throws Exception {
        Year validYear = new YearBuilder().build();
        Semester validSemester = new SemesterBuilder().build();
        Code validCode = new ModuleBuilder().build().getCode();
        PlannerAddCommand plannerAddCommand = new PlannerAddCommand(validYear, validSemester, validCode);
        ModelStub modelStub = new ModelStubWithCode(validCode);

        thrown.expect(CommandException.class);
        thrown.expectMessage(PlannerAddCommand.MESSAGE_DUPLICATE_CODE);
        plannerAddCommand.execute(modelStub, commandHistory);
    }

    @Test
    public void equals() {
        Code alice = new CodeBuilder().withName("Alice").build();
        Code bob = new CodeBuilder().withName("Bob").build();
        PlannerAddCommand addAliceCommand = new PlannerAddCommand(alice);
        PlannerAddCommand addBobCommand = new PlannerAddCommand(bob);

        // same object -> returns true
        assertTrue(addAliceCommand.equals(addAliceCommand));

        // same values -> returns true
        PlannerAddCommand addAliceCommandCopy = new PlannerAddCommand(alice);
        assertTrue(addAliceCommand.equals(addAliceCommandCopy));

        // different types -> returns false
        assertFalse(addAliceCommand.equals(1));

        // null -> returns false
        assertFalse(addAliceCommand.equals(null));

        // different code -> returns false
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
        public Path getPlannerAddressBookFilePath() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setPlannerAddressBookFilePath(Path addressBookFilePath) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addCode(Code code) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setPlannerAddressBook(ReadOnlyPlannerAddressBook newData) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyPlannerAddressBook getPlannerAddressBook() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasCode(Code code) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deleteCode(Code target) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setCode(Code target, Code editedCode) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Code> getFilteredCodeList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredCodeList(Predicate<Code> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean canUndoPlannerAddressBook() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean canRedoPlannerAddressBook() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void undoPlannerAddressBook() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void redoPlannerAddressBook() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void commitPlannerAddressBook() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyProperty<Code> selectedCodeProperty() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Code getSelectedCode() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setSelectedCode(Code code) {
            throw new AssertionError("This method should not be called.");
        }
    }

    /**
     * A Model stub that contains a single code.
     */
    private class ModelStubWithCode extends ModelStub {
        private final Code code;

        ModelStubWithCode(Code code) {
            requireNonNull(code);
            this.code = code;
        }

        @Override
        public boolean hasCode(Code code) {
            requireNonNull(code);
            return this.code.isSameCode(code);
        }
    }

    /**
     * A Model stub that always accept the code being added.
     */
    private class ModelStubAcceptingCodeAdded extends ModelStub {
        final ArrayList<Code> plannerCodesAdded = new ArrayList<>();

        @Override
        public boolean hasCode(Code code) {
            requireNonNull(code);
            return codesPlannerAdded.stream().anyMatch(code::isSameCode);
        }

        @Override
        public void addCode(Code code) {
            requireNonNull(code);
            codesPlannerAdded.add(code);
        }

        @Override
        public void commitPlannerAddressBook() {
            // called by {@code PlannerAddCommand#execute()}
        }

        @Override
        public ReadOnlyPlannerAddressBook getPlannerAddressBook() {
            return new PlannerAddressBook();
        }
    }

}
