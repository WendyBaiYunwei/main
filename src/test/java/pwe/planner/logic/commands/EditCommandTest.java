package pwe.planner.logic.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static pwe.planner.commons.core.Messages.MESSAGE_INVALID_MODULE_DISPLAYED_INDEX;
import static pwe.planner.commons.util.StringUtil.joinStreamAsString;
import static pwe.planner.logic.commands.CommandTestUtil.DESC_AMY;
import static pwe.planner.logic.commands.CommandTestUtil.DESC_BOB;
import static pwe.planner.logic.commands.CommandTestUtil.VALID_CREDITS_BOB;
import static pwe.planner.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static pwe.planner.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static pwe.planner.logic.commands.CommandTestUtil.assertCommandFailure;
import static pwe.planner.logic.commands.CommandTestUtil.assertCommandSuccess;
import static pwe.planner.logic.commands.CommandTestUtil.showModuleAtIndex;
import static pwe.planner.testutil.TypicalIndexes.INDEX_FIRST_MODULE;
import static pwe.planner.testutil.TypicalIndexes.INDEX_SECOND_MODULE;

import java.util.Set;
import java.util.stream.Stream;

import org.junit.Test;

import pwe.planner.commons.core.index.Index;
import pwe.planner.logic.CommandHistory;
import pwe.planner.logic.commands.EditCommand.EditModuleDescriptor;
import pwe.planner.model.Model;
import pwe.planner.model.ModelManager;
import pwe.planner.model.UserPrefs;
import pwe.planner.model.module.Code;
import pwe.planner.model.module.Module;
import pwe.planner.model.planner.DegreePlanner;
import pwe.planner.model.planner.Semester;
import pwe.planner.model.util.SampleDataUtil;
import pwe.planner.testutil.EditModuleDescriptorBuilder;
import pwe.planner.testutil.ModuleBuilder;

/**
 * Contains integration tests (interaction with the Model, UndoCommand and RedoCommand) and unit tests for EditCommand.
 */
public class EditCommandTest {
    private Model model = new ModelManager(SampleDataUtil.getSampleApplication(), new UserPrefs());
    private CommandHistory commandHistory = new CommandHistory();

    public EditCommandTest() {}

    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        Module moduleInFilteredList =
                model.getFilteredModuleList().get(INDEX_FIRST_MODULE.getZeroBased());
        Module editedModule = new ModuleBuilder().build();
        EditCommand.EditModuleDescriptor descriptor = new EditModuleDescriptorBuilder(editedModule).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_MODULE, descriptor);

        String expectedMessage = String.format(
                EditCommand.MESSAGE_EDIT_MODULE_SUCCESS, moduleInFilteredList.getCode(), editedModule
        );

        Model expectedModel = new ModelManager(model.getApplication(), new UserPrefs());

        expectedModel.editModule(model.getFilteredModuleList().get(0), editedModule);
        expectedModel.commitApplication();

        assertCommandSuccess(editCommand, model, commandHistory, expectedMessage, expectedModel);
    }

    @Test
    public void execute_someFieldsSpecifiedUnfilteredList_success() {
        Index indexLastModule = Index.fromOneBased(model.getFilteredModuleList().size());
        Module lastModule = model.getFilteredModuleList().get(indexLastModule.getZeroBased());

        ModuleBuilder moduleInList = new ModuleBuilder(lastModule);
        Module editedModule = moduleInList.withName(VALID_NAME_BOB).withCredits(VALID_CREDITS_BOB)
                .withTags(VALID_TAG_HUSBAND).build();

        EditCommand.EditModuleDescriptor descriptor = new EditModuleDescriptorBuilder().withName(VALID_NAME_BOB)
                .withCredits(VALID_CREDITS_BOB).withTags(VALID_TAG_HUSBAND).build();
        EditCommand editCommand = new EditCommand(indexLastModule, descriptor);

        String expectedMessage = String.format(
                EditCommand.MESSAGE_EDIT_MODULE_SUCCESS, lastModule.getCode(), editedModule
        );

        Model expectedModel = new ModelManager(model.getApplication(), new UserPrefs());
        expectedModel.editModule(lastModule, editedModule);
        expectedModel.commitApplication();

        assertCommandSuccess(editCommand, model, commandHistory, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noFieldSpecifiedUnfilteredList_success() {
        Module firstModule = model.getFilteredModuleList().get(INDEX_FIRST_MODULE.getZeroBased());
        EditCommand editCommand = new EditCommand(INDEX_FIRST_MODULE, new EditCommand.EditModuleDescriptor());
        Module editedModule = model.getFilteredModuleList().get(INDEX_FIRST_MODULE.getZeroBased());

        String expectedMessage = String.format(
                EditCommand.MESSAGE_EDIT_MODULE_SUCCESS, firstModule.getCode(), editedModule
        );

        Model expectedModel = new ModelManager(model.getApplication(), new UserPrefs());
        expectedModel.commitApplication();

        assertCommandSuccess(editCommand, model, commandHistory, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_success() {
        showModuleAtIndex(model, INDEX_FIRST_MODULE);

        Module moduleInFilteredList = model.getFilteredModuleList().get(INDEX_FIRST_MODULE.getZeroBased());
        Module editedModule = new ModuleBuilder(moduleInFilteredList).withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_MODULE,
                new EditModuleDescriptorBuilder().withName(VALID_NAME_BOB).build());

        String expectedMessage = String.format(
                EditCommand.MESSAGE_EDIT_MODULE_SUCCESS, moduleInFilteredList.getCode(), editedModule
        );

        Model expectedModel = new ModelManager(model.getApplication(), new UserPrefs());
        expectedModel.editModule(model.getFilteredModuleList().get(0), editedModule);
        expectedModel.commitApplication();

        assertCommandSuccess(editCommand, model, commandHistory, expectedMessage, expectedModel);
    }

    @Test
    public void execute_duplicateModuleUnfilteredList_failure() {
        Module firstModule = model.getFilteredModuleList().get(INDEX_FIRST_MODULE.getZeroBased());
        Module secondModule = model.getFilteredModuleList().get(INDEX_SECOND_MODULE.getZeroBased());
        EditModuleDescriptor descriptor = new EditModuleDescriptorBuilder(firstModule).build();
        EditCommand editCommand = new EditCommand(INDEX_SECOND_MODULE, descriptor);

        String expectedMessage = String.format(
                EditCommand.MESSAGE_DUPLICATE_MODULE, secondModule.getCode(), firstModule.getCode()
        );

        assertCommandFailure(editCommand, model, commandHistory, expectedMessage);
    }

    @Test
    public void execute_duplicateModuleFilteredList_failure() {
        showModuleAtIndex(model, INDEX_FIRST_MODULE);

        // edit module in filtered list into a duplicate in application
        Module moduleToEdit = model.getApplication().getModuleList().get(INDEX_FIRST_MODULE.getZeroBased());
        Module moduleToClone = model.getApplication().getModuleList().get(INDEX_SECOND_MODULE.getZeroBased());
        EditCommand editCommand = new EditCommand(INDEX_FIRST_MODULE,
                new EditModuleDescriptorBuilder(moduleToClone).build());
        String expectedMessage = String.format(EditCommand.MESSAGE_DUPLICATE_MODULE, moduleToEdit.getCode(),
                moduleToClone.getCode());

        assertCommandFailure(editCommand, model, commandHistory, expectedMessage);
    }

    @Test
    public void execute_invalidModuleIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredModuleList().size() + 1);
        EditCommand.EditModuleDescriptor descriptor =
                new EditModuleDescriptorBuilder().withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(outOfBoundIndex, descriptor);

        assertCommandFailure(editCommand, model, commandHistory, MESSAGE_INVALID_MODULE_DISPLAYED_INDEX);
    }

    /**
     * Edit filtered list where index is larger than size of filtered list,
     * but smaller than size of application
     */
    @Test
    public void execute_invalidModuleIndexFilteredList_failure() {
        showModuleAtIndex(model, INDEX_FIRST_MODULE);
        Index outOfBoundIndex = INDEX_SECOND_MODULE;
        // ensures that outOfBoundIndex is still in bounds of application list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getApplication().getModuleList().size());

        EditCommand editCommand = new EditCommand(outOfBoundIndex,
                new EditModuleDescriptorBuilder().withName(VALID_NAME_BOB).build());

        assertCommandFailure(editCommand, model, commandHistory, MESSAGE_INVALID_MODULE_DISPLAYED_INDEX);
    }

    @Test
    public void executeUndoRedo_validIndexUnfilteredList_success() throws Exception {
        Module editedModule = new ModuleBuilder().build();
        Module moduleToEdit = model.getFilteredModuleList().get(INDEX_FIRST_MODULE.getZeroBased());
        EditCommand.EditModuleDescriptor descriptor = new EditModuleDescriptorBuilder(editedModule).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_MODULE, descriptor);
        Model expectedModel = new ModelManager(model.getApplication(), new UserPrefs());
        expectedModel.editModule(moduleToEdit, editedModule);
        expectedModel.commitApplication();

        // edit -> first module edited
        editCommand.execute(model, commandHistory);

        // undo -> reverts application back to previous state and filtered module list to show all modules
        expectedModel.undoApplication();
        assertCommandSuccess(new UndoCommand(), model, commandHistory, UndoCommand.MESSAGE_SUCCESS, expectedModel);

        // redo -> same first module edited again
        expectedModel.redoApplication();
        assertCommandSuccess(new RedoCommand(), model, commandHistory, RedoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void executeUndoRedo_invalidIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredModuleList().size() + 1);
        EditCommand.EditModuleDescriptor descriptor =
                new EditModuleDescriptorBuilder().withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(outOfBoundIndex, descriptor);

        // execution failed -> application state not added into model
        assertCommandFailure(editCommand, model, commandHistory,
                MESSAGE_INVALID_MODULE_DISPLAYED_INDEX);

        // single application state in model -> undoCommand and redoCommand fail
        assertCommandFailure(new UndoCommand(), model, commandHistory, UndoCommand.MESSAGE_FAILURE);
        assertCommandFailure(new RedoCommand(), model, commandHistory, RedoCommand.MESSAGE_FAILURE);
    }

    /**
     * 1. Edits a {@code Module} from a filtered list.
     * 2. Undo the edit.
     * 3. The unfiltered list should be shown now. Verify that the index of the previously edited module in the
     * unfiltered list is different from the index at the filtered list.
     * 4. Redo the edit. This ensures {@code RedoCommand} edits the module object regardless of indexing.
     */
    @Test
    public void executeUndoRedo_validIndexFilteredList_sameModuleEdited() throws Exception {
        showModuleAtIndex(model, INDEX_SECOND_MODULE);
        Module moduleToEdit = model.getFilteredModuleList().get(INDEX_FIRST_MODULE.getZeroBased());

        Module editedModule = new ModuleBuilder().withCorequisites(moduleToEdit.getCorequisites()).build();
        EditModuleDescriptor descriptor = new EditModuleDescriptorBuilder(editedModule).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_MODULE, descriptor);
        Model expectedModel = new ModelManager(model.getApplication(), new UserPrefs());

        expectedModel.editModule(moduleToEdit, editedModule);
        expectedModel.commitApplication();

        // edit -> edits second module in unfiltered module list / first module in filtered module list
        editCommand.execute(model, commandHistory);

        // undo -> reverts application back to previous state and filtered module list to show all modules
        expectedModel.undoApplication();
        assertCommandSuccess(new UndoCommand(), model, commandHistory, UndoCommand.MESSAGE_SUCCESS, expectedModel);

        assertNotEquals(model.getFilteredModuleList().get(INDEX_FIRST_MODULE.getZeroBased()), moduleToEdit);
        // redo -> edits same second module in unfiltered module list
        expectedModel.redoApplication();
        assertCommandSuccess(new RedoCommand(), model, commandHistory, RedoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    /**
     * 1. Edits a {@code Module} in the filtered module list to have another module that is not added to the degree plan
     * as a corequisite -> success
     */
    @Test
    public void execute_nonExistentCorequisites_throwsCommandException() {
        showModuleAtIndex(model, INDEX_FIRST_MODULE);

        Module moduleToEdit = model.getFilteredModuleList().get(INDEX_FIRST_MODULE.getZeroBased());
        String nonExistentCorequisite = "ZYX9876W";
        EditCommand.EditModuleDescriptor editModuleDescriptor = new EditModuleDescriptorBuilder()
                .withCorequisites(nonExistentCorequisite).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_MODULE, editModuleDescriptor);
        String expectedMessage = String.format(EditCommand.MESSAGE_NON_EXISTENT_COREQUISITE, moduleToEdit.getCode(),
                nonExistentCorequisite);

        assertCommandFailure(editCommand, model, commandHistory, expectedMessage);
    }

    @Test
    public void execute_directSelfReferencingCorequisites_throwsCommandException() {
        showModuleAtIndex(model, INDEX_FIRST_MODULE);

        Module moduleToEdit = model.getFilteredModuleList().get(INDEX_FIRST_MODULE.getZeroBased());
        String editedModuleCode = moduleToEdit.getCode().toString();
        EditCommand.EditModuleDescriptor editModuleDescriptor = new EditModuleDescriptorBuilder()
                .withCorequisites(editedModuleCode).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_MODULE, editModuleDescriptor);
        String expectedMessage = String.format(EditCommand.MESSAGE_SELF_REFERENCING_COREQUISITE, moduleToEdit.getCode(),
                editedModuleCode);

        assertCommandFailure(editCommand, model, commandHistory, expectedMessage);
    }

    @Test
    public void execute_indirectSelfReferencingCorequisites_throwsCommandException() {
        showModuleAtIndex(model, INDEX_FIRST_MODULE);

        Module moduleToEdit = model.getFilteredModuleList().get(INDEX_FIRST_MODULE.getZeroBased());
        String originalModuleCode = moduleToEdit.getCode().toString();
        String editedModuleCode = "CS1337";
        EditCommand.EditModuleDescriptor editModuleDescriptor = new EditModuleDescriptorBuilder()
                .withCode(editedModuleCode).withCorequisites(originalModuleCode).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_MODULE, editModuleDescriptor);
        String expectedMessage = String.format(EditCommand.MESSAGE_SELF_REFERENCING_COREQUISITE, originalModuleCode,
                editedModuleCode);

        assertCommandFailure(editCommand, model, commandHistory, expectedMessage);
    }

    @Test
    public void execute_moduleAndCorequisiteModuleExistsInDifferentSemesters_throwsCommandException() {
        showModuleAtIndex(model, INDEX_FIRST_MODULE);

        Module moduleToEdit = model.getFilteredModuleList().get(INDEX_FIRST_MODULE.getZeroBased());
        Code corequisiteModuleInDifferentSemester = new Code("CS2040C");
        DegreePlanner originalDegreePlanner = model.getDegreePlannerByCode(corequisiteModuleInDifferentSemester);
        assert originalDegreePlanner != null;
        assert !originalDegreePlanner.equals(model.getDegreePlannerByCode(moduleToEdit.getCode()));

        DegreePlanner truncatedDegreePlanner = new DegreePlanner(originalDegreePlanner.getYear(),
                originalDegreePlanner.getSemester(), Set.of(corequisiteModuleInDifferentSemester));

        EditCommand.EditModuleDescriptor editModuleDescriptor = new EditModuleDescriptorBuilder()
                .withCorequisites(corequisiteModuleInDifferentSemester.toString()).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_MODULE, editModuleDescriptor);
        String expectedMessage = String.format(EditCommand.MESSAGE_MODULE_AND_COREQUISITES_DIFFERENT_SEMESTERS,
                moduleToEdit.getCode(), truncatedDegreePlanner);

        assertCommandFailure(editCommand, model, commandHistory, expectedMessage);
    }

    @Test
    public void execute_moduleAndCorequisiteModulesExistsInDifferentSemesters_throwsCommandException() {
        showModuleAtIndex(model, INDEX_FIRST_MODULE);

        Module moduleToEdit = model.getFilteredModuleList().get(INDEX_FIRST_MODULE.getZeroBased());
        Code firstCorequisiteModuleInDifferentSemester = new Code("CS2040C");
        Code secondCorequisiteModuleInDifferentSemester = new Code("GER1000");
        DegreePlanner firstDegreePlanner = model.getDegreePlannerByCode(firstCorequisiteModuleInDifferentSemester);
        DegreePlanner secondDegreePlanner = model.getDegreePlannerByCode(secondCorequisiteModuleInDifferentSemester);
        assert firstDegreePlanner != null;
        assert secondDegreePlanner != null;
        assert !firstDegreePlanner.equals(secondDegreePlanner);

        DegreePlanner truncatedFirstDegreePlanner = new DegreePlanner(firstDegreePlanner.getYear(),
                firstDegreePlanner.getSemester(), Set.of(firstCorequisiteModuleInDifferentSemester));
        DegreePlanner truncatedSecondDegreePlanner = new DegreePlanner(secondDegreePlanner.getYear(),
                secondDegreePlanner.getSemester(), Set.of(secondCorequisiteModuleInDifferentSemester));

        Stream<DegreePlanner> stream = Stream.of(truncatedFirstDegreePlanner, truncatedSecondDegreePlanner).sorted();

        EditCommand.EditModuleDescriptor editModuleDescriptor = new EditModuleDescriptorBuilder()
                .withCorequisites(firstCorequisiteModuleInDifferentSemester.toString(),
                        secondCorequisiteModuleInDifferentSemester.toString()).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_MODULE, editModuleDescriptor);
        String expectedMessage = String.format(EditCommand.MESSAGE_MODULE_AND_COREQUISITES_DIFFERENT_SEMESTERS,
                moduleToEdit.getCode(), joinStreamAsString(stream, "\n"));

        assertCommandFailure(editCommand, model, commandHistory, expectedMessage);
    }

    @Test
    public void execute_moduleNotInDegreePlannerButCorequisiteInDegreePlanner_throwsCommandException() {
        Index lastModuleIndex = Index.fromOneBased(model.getFilteredModuleList().size());
        Module moduleToEdit = model.getFilteredModuleList().get(lastModuleIndex.getZeroBased());
        assert model.getDegreePlannerByCode(moduleToEdit.getCode()) == null;

        Code corequisiteModule = new Code("CS2040C");
        DegreePlanner degreePlannerContainingCorequisite = model.getDegreePlannerByCode(corequisiteModule);

        EditCommand.EditModuleDescriptor editModuleDescriptor = new EditModuleDescriptorBuilder()
                .withCorequisites(corequisiteModule.toString()).build();
        EditCommand editCommand = new EditCommand(lastModuleIndex, editModuleDescriptor);

        String expectedMessage = String.format(
                EditCommand.MESSAGE_MODULE_NOT_IN_DEGREE_PLANNER_BUT_SOME_COREQUISITES_IN_DEGREE_PLANNER,
                moduleToEdit.getCode(), corequisiteModule,
                degreePlannerContainingCorequisite.getYear(), degreePlannerContainingCorequisite.getSemester(),
                joinStreamAsString(editModuleDescriptor.getCorequisites().get().stream().sorted()));

        assertCommandFailure(editCommand, model, commandHistory, expectedMessage);
    }

    @Test
    public void execute_editExistingModuleInDegreePlannerInvalidSemester_throwsCommandException() {
        showModuleAtIndex(model, INDEX_FIRST_MODULE);

        Module moduleToEdit = model.getFilteredModuleList().get(INDEX_FIRST_MODULE.getZeroBased());
        DegreePlanner degreePlannerContainingModule = model.getDegreePlannerByCode(moduleToEdit.getCode());
        Semester editedSemester = new Semester("2");
        assert degreePlannerContainingModule != null;
        assert !degreePlannerContainingModule.getSemester().equals(editedSemester);

        EditCommand.EditModuleDescriptor editModuleDescriptor = new EditModuleDescriptorBuilder()
                .withSemesters(editedSemester.toString()).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_MODULE, editModuleDescriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_INVALID_SEMESTER, moduleToEdit.getCode(),
                joinStreamAsString(moduleToEdit.getSemesters().stream().sorted()),
                editedSemester, degreePlannerContainingModule.getYear(), degreePlannerContainingModule.getSemester()
        );
        assertCommandFailure(editCommand, model, commandHistory, expectedMessage);
    }

    @Test
    public void execute_missingCorequisiteInDegreePlanner_throwsCommandException() {
        showModuleAtIndex(model, INDEX_FIRST_MODULE);

        Module moduleToEdit = model.getFilteredModuleList().get(INDEX_FIRST_MODULE.getZeroBased());
        DegreePlanner degreePlannerContainingModule = model.getDegreePlannerByCode(moduleToEdit.getCode());
        assert degreePlannerContainingModule != null;

        Code missingCorequisite = new Code("ST2334");
        assert model.getDegreePlannerByCode(missingCorequisite) == null;

        EditCommand.EditModuleDescriptor editModuleDescriptor = new EditModuleDescriptorBuilder()
                .withCorequisites(missingCorequisite.toString()).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_MODULE, editModuleDescriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_COREQUISITES_NOT_IN_DEGREE_PLANNER,
                moduleToEdit.getCode(), missingCorequisite, degreePlannerContainingModule.getYear(),
                degreePlannerContainingModule.getSemester(), missingCorequisite);
        assertCommandFailure(editCommand, model, commandHistory, expectedMessage);
    }

    @Test
    public void equals() {
        final EditCommand standardCommand = new EditCommand(INDEX_FIRST_MODULE, DESC_AMY);

        // same values -> returns true
        EditCommand.EditModuleDescriptor copyDescriptor = new EditCommand.EditModuleDescriptor(DESC_AMY);
        EditCommand commandWithSameValues = new EditCommand(INDEX_FIRST_MODULE, copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new EditCommand(INDEX_SECOND_MODULE, DESC_AMY)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new EditCommand(INDEX_FIRST_MODULE, DESC_BOB)));
    }

}
