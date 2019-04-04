package pwe.planner.logic;

import static org.junit.Assert.assertEquals;
import static pwe.planner.commons.core.Messages.MESSAGE_INVALID_MODULE_DISPLAYED_INDEX;
import static pwe.planner.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;
import static pwe.planner.logic.commands.CommandTestUtil.CODE_DESC_AMY;
import static pwe.planner.logic.commands.CommandTestUtil.CREDITS_DESC_AMY;
import static pwe.planner.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static pwe.planner.testutil.TypicalModules.AMY;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import pwe.planner.logic.commands.AddCommand;
import pwe.planner.logic.commands.CommandResult;
import pwe.planner.logic.commands.HistoryCommand;
import pwe.planner.logic.commands.ListCommand;
import pwe.planner.logic.commands.PlannerListAllCommand;
import pwe.planner.logic.commands.exceptions.CommandException;
import pwe.planner.logic.parser.exceptions.ParseException;
import pwe.planner.model.Model;
import pwe.planner.model.ModelManager;
import pwe.planner.model.ReadOnlyApplication;
import pwe.planner.model.UserPrefs;
import pwe.planner.model.module.Code;
import pwe.planner.model.module.Module;
import pwe.planner.model.planner.DegreePlanner;
import pwe.planner.storage.JsonApplicationStorage;
import pwe.planner.storage.JsonUserPrefsStorage;
import pwe.planner.storage.StorageManager;
import pwe.planner.testutil.ModuleBuilder;

public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy exception");

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private Model model = new ModelManager();
    private Logic logic;

    @Before
    public void setUp() throws Exception {
        JsonApplicationStorage applicationStorage =
                new JsonApplicationStorage(temporaryFolder.newFile().toPath(), temporaryFolder.newFile().toPath(),
                        temporaryFolder.newFile().toPath());
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.newFile().toPath());
        StorageManager storage =
                new StorageManager(applicationStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand, MESSAGE_UNKNOWN_COMMAND);
        assertHistoryCorrect(invalidCommand);
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteCommand = "delete 9";
        assertCommandException(deleteCommand, MESSAGE_INVALID_MODULE_DISPLAYED_INDEX);
        assertHistoryCorrect(deleteCommand);
    }

    @Test
    public void execute_validCommand_success() {
        String listCommand = ListCommand.COMMAND_WORD;
        assertCommandSuccess(listCommand, ListCommand.MESSAGE_SUCCESS, model);
        assertHistoryCorrect(listCommand);
    }

    @Test
    public void execute_validPlannerListCommand_success() {
        String plannerListCommand = PlannerListAllCommand.COMMAND_WORD;
        StringBuilder degreePlannerListContent = new StringBuilder();
        for (DegreePlanner degreePlanner : model.getFilteredDegreePlannerList()) {
            degreePlannerListContent
                    .append("Year: " + degreePlanner.getYear() + " Semester: " + degreePlanner.getSemester() + "\n");
            if (degreePlanner.getCodes().isEmpty()) {
                degreePlannerListContent.append("No module inside");
            } else {
                degreePlannerListContent
                        .append("Modules: " + degreePlanner.getCodes().stream().map(Code::toString).collect(
                                Collectors.joining(", ")));
            }
            degreePlannerListContent.append("\n\n");
        }
        String expectedMessage =
                String.format(PlannerListAllCommand.MESSAGE_SUCCESS, degreePlannerListContent.toString());
        assertCommandSuccess(plannerListCommand, expectedMessage, model);
        assertHistoryCorrect(plannerListCommand);
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() throws Exception {
        // Setup LogicManager with JsonApplicationIoExceptionThrowingStub
        JsonApplicationStorage applicationStorage =
                new JsonApplicationIoExceptionThrowingStub(temporaryFolder.newFile().toPath(),
                        temporaryFolder.newFile().toPath(), temporaryFolder.newFile().toPath());
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.newFile().toPath());

        StorageManager storage = new StorageManager(applicationStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);

        // Execute add command
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + CREDITS_DESC_AMY + CODE_DESC_AMY;
        Module expectedModule = new ModuleBuilder(AMY).withTags().build();
        ModelManager expectedModel = new ModelManager();
        expectedModel.addModule(expectedModule);
        expectedModel.commitApplication();
        String expectedMessage = LogicManager.FILE_OPS_ERROR_MESSAGE + DUMMY_IO_EXCEPTION;
        assertCommandBehavior(CommandException.class, addCommand, expectedMessage, expectedModel);
        assertHistoryCorrect(addCommand);
    }

    @Test
    public void getFilteredModuleList_modifyList_throwsUnsupportedOperationException() {
        thrown.expect(UnsupportedOperationException.class);
        logic.getFilteredModuleList().remove(0);
    }

    /**
     * Executes the command, confirms that no exceptions are thrown and that the result message is correct.
     * Also confirms that {@code expectedModel} is as specified.
     *
     * @see #assertCommandBehavior(Class, String, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage, Model expectedModel) {
        assertCommandBehavior(null, inputCommand, expectedMessage, expectedModel);
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the result message is correct.
     *
     * @see #assertCommandBehavior(Class, String, String, Model)
     */
    private void assertParseException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     *
     * @see #assertCommandBehavior(Class, String, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     *
     * @see #assertCommandBehavior(Class, String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<?> expectedException, String expectedMessage) {
        Model expectedModel = new ModelManager(model.getApplication(), new UserPrefs());
        assertCommandBehavior(expectedException, inputCommand, expectedMessage, expectedModel);
    }

    /**
     * Executes the command, confirms that the result message is correct and that the expected exception is thrown,
     * and also confirms that the following two parts of the LogicManager object's state are as expected:<br>
     * - the internal model manager data are same as those in the {@code expectedModel} <br>
     * - {@code expectedModel}'s application was saved to the storage file.
     */
    private void assertCommandBehavior(Class<?> expectedException, String inputCommand,
            String expectedMessage, Model expectedModel) {

        try {
            CommandResult result = logic.execute(inputCommand);
            assertEquals(expectedException, null);
            assertEquals(expectedMessage, result.getFeedbackToUser());
        } catch (CommandException | ParseException e) {
            assertEquals(expectedException, e.getClass());
            assertEquals(expectedMessage, e.getMessage());
        }

        assertEquals(expectedModel, model);
    }

    /**
     * Asserts that the result display shows all the {@code expectedCommands} upon the execution of
     * {@code HistoryCommand}.
     */
    private void assertHistoryCorrect(String... expectedCommands) {
        try {
            CommandResult result = logic.execute(HistoryCommand.COMMAND_WORD);
            String expectedMessage = String.format(
                    HistoryCommand.MESSAGE_SUCCESS, Arrays.stream(expectedCommands)
                            .sorted(Collections.reverseOrder())
                            .map(command -> "- " + command)
                            .collect(Collectors.joining("\n"))
            );
            assertEquals(expectedMessage, result.getFeedbackToUser());
        } catch (ParseException | CommandException e) {
            throw new AssertionError("Parsing and execution of HistoryCommand.COMMAND_WORD should succeed.", e);
        }
    }

    /**
     * A stub class to throw an {@code IOException} when the save method is called.
     */
    private static class JsonApplicationIoExceptionThrowingStub extends JsonApplicationStorage {
        private JsonApplicationIoExceptionThrowingStub(Path moduleListFilePath, Path degreePlannerListFilePath,
                Path requirementCategoryListFilePath) {
            super(moduleListFilePath, degreePlannerListFilePath, requirementCategoryListFilePath);
        }

        @Override
        public void saveModuleList(ReadOnlyApplication application, Path filePath) throws IOException {
            throw DUMMY_IO_EXCEPTION;
        }
    }
}
