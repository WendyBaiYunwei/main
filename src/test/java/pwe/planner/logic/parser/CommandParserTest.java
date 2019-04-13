package pwe.planner.logic.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pwe.planner.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static pwe.planner.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_CODE;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_NAME;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_SEMESTER;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_YEAR;
import static pwe.planner.testutil.TypicalIndexes.INDEX_FIRST_MODULE;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import pwe.planner.logic.commands.AddCommand;
import pwe.planner.logic.commands.ClearCommand;
import pwe.planner.logic.commands.DeleteCommand;
import pwe.planner.logic.commands.EditCommand;
import pwe.planner.logic.commands.EditCommand.EditModuleDescriptor;
import pwe.planner.logic.commands.ExitCommand;
import pwe.planner.logic.commands.FindCommand;
import pwe.planner.logic.commands.HelpCommand;
import pwe.planner.logic.commands.HistoryCommand;
import pwe.planner.logic.commands.ListCommand;
import pwe.planner.logic.commands.PlannerAddCommand;
import pwe.planner.logic.commands.PlannerListCommand;
import pwe.planner.logic.commands.PlannerMoveCommand;
import pwe.planner.logic.commands.RedoCommand;
import pwe.planner.logic.commands.RequirementAddCommand;
import pwe.planner.logic.commands.RequirementListCommand;
import pwe.planner.logic.commands.RequirementRemoveCommand;
import pwe.planner.logic.commands.SelectCommand;
import pwe.planner.logic.commands.UndoCommand;
import pwe.planner.logic.parser.exceptions.ParseException;
import pwe.planner.model.module.Code;
import pwe.planner.model.module.Module;
import pwe.planner.model.module.Name;
import pwe.planner.model.module.NameContainsKeywordsPredicate;
import pwe.planner.model.planner.Semester;
import pwe.planner.model.planner.Year;
import pwe.planner.testutil.EditModuleDescriptorBuilder;
import pwe.planner.testutil.ModuleBuilder;
import pwe.planner.testutil.ModuleUtil;
import pwe.planner.testutil.RequirementUtil;

public class CommandParserTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final CommandParser parser = new CommandParser();

    @Test
    public void parseCommand_add() throws Exception {
        Module module = new ModuleBuilder().build();
        AddCommand command = (AddCommand) parser.parseCommand(ModuleUtil.getAddCommand(module));
        assertEquals(new AddCommand(module), command);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " planner") instanceof ClearCommand);
    }

    @Test
    public void parseCommand_delete() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(
                DeleteCommand.COMMAND_WORD + " " + INDEX_FIRST_MODULE.getOneBased());
        assertEquals(new DeleteCommand(INDEX_FIRST_MODULE), command);
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Module module = new ModuleBuilder().build();
        EditModuleDescriptor descriptor = new EditModuleDescriptorBuilder(module).build();
        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD + " "
                + INDEX_FIRST_MODULE.getOneBased() + " " + ModuleUtil.getEditModuleDescriptorDetails(descriptor));
        assertEquals(new EditCommand(INDEX_FIRST_MODULE, descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_find() throws Exception {
        List<String> keywords = Arrays.asList("foo");
        FindCommand command = (FindCommand) parser.parseCommand(FindCommand.COMMAND_WORD + " "
                + PREFIX_NAME + "foo");
        assertEquals(new FindCommand(new NameContainsKeywordsPredicate<>(keywords)), command);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_history() throws Exception {
        assertTrue(parser.parseCommand(HistoryCommand.COMMAND_WORD) instanceof HistoryCommand);
        assertTrue(parser.parseCommand(HistoryCommand.COMMAND_WORD + " 3") instanceof HistoryCommand);

        try {
            parser.parseCommand("histories");
            throw new AssertionError("The expected ParseException was not thrown.");
        } catch (ParseException pe) {
            assertEquals(MESSAGE_UNKNOWN_COMMAND, pe.getMessage());
        }
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD) instanceof ListCommand);
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD + " 3") instanceof ListCommand);
    }

    @Test
    public void parseCommand_select() throws Exception {
        SelectCommand command = (SelectCommand) parser.parseCommand(
                SelectCommand.COMMAND_WORD + " " + INDEX_FIRST_MODULE.getOneBased());
        assertEquals(new SelectCommand(INDEX_FIRST_MODULE), command);
    }

    @Test
    public void parseCommand_plannerList() throws Exception {
        assertTrue(parser.parseCommand(PlannerListCommand.COMMAND_WORD) instanceof PlannerListCommand);
        assertTrue(parser.parseCommand(PlannerListCommand.COMMAND_WORD + " 3") instanceof PlannerListCommand);
    }

    @Test
    public void parseCommand_plannerMove() throws Exception {
        PlannerMoveCommand command = (PlannerMoveCommand) parser.parseCommand(
                PlannerMoveCommand.COMMAND_WORD + " " + PREFIX_YEAR + "1 " + PREFIX_SEMESTER + "2 " + PREFIX_CODE
                        + "CS1010");
        assertEquals(new PlannerMoveCommand(new Year("1"), new Semester("2"), new Code("CS1010")), command);
    }

    @Test
    public void parseCommand_plannerAdd() throws Exception {
        Set<Code> codesToAdd = Set.of(new Code("CS1010"));
        PlannerAddCommand command = (PlannerAddCommand) parser.parseCommand(
                PlannerAddCommand.COMMAND_WORD + " " + PREFIX_YEAR + "1 " + PREFIX_SEMESTER + "2 " + PREFIX_CODE
                        + "CS1010");
        assertEquals(new PlannerAddCommand(new Year("1"), new Semester("2"), codesToAdd), command);
    }

    @Test
    public void parseCommand_requirementAdd() throws Exception {
        Name name = new Name("Computing Foundation");
        Set<Code> codeSet = new HashSet<>();
        codeSet.add(new Code("CS1010"));
        RequirementAddCommand command = (RequirementAddCommand)
                parser.parseCommand(RequirementUtil.getRequirementAddCommand(name, codeSet));
        assertEquals(new RequirementAddCommand(name, codeSet), command);
    }

    @Test
    public void parseCommand_requirementList() throws Exception {
        assertTrue(parser.parseCommand(RequirementListCommand.COMMAND_WORD) instanceof RequirementListCommand);
    }

    @Test
    public void parseCommand_requirementRemove() throws Exception {
        Name name = new Name("Computing Foundation");
        Set<Code> codeSet = new HashSet<>();
        codeSet.add(new Code("CS1010"));
        RequirementRemoveCommand command = (RequirementRemoveCommand)
                parser.parseCommand(RequirementUtil.getRequirementRemoveCommand(name, codeSet));
        assertEquals(new RequirementRemoveCommand(name, codeSet), command);
    }

    @Test
    public void parseCommand_redoCommandWord_returnsRedoCommand() throws Exception {
        assertTrue(parser.parseCommand(RedoCommand.COMMAND_WORD) instanceof RedoCommand);
        assertTrue(parser.parseCommand("redo 1") instanceof RedoCommand);
    }

    @Test
    public void parseCommand_undoCommandWord_returnsUndoCommand() throws Exception {
        assertTrue(parser.parseCommand(UndoCommand.COMMAND_WORD) instanceof UndoCommand);
        assertTrue(parser.parseCommand("undo 3") instanceof UndoCommand);
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() throws Exception {
        thrown.expect(ParseException.class);
        thrown.expectMessage(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        parser.parseCommand("");
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() throws Exception {
        thrown.expect(ParseException.class);
        thrown.expectMessage(MESSAGE_UNKNOWN_COMMAND);
        parser.parseCommand("unknownCommand");
    }
}
