package pwe.planner.logic.parser;

import static pwe.planner.logic.commands.ClearCommand.MESSAGE_USAGE;
import static pwe.planner.logic.commands.ClearCommand.PLANNER;
import static pwe.planner.logic.commands.ClearCommand.REQUIREMENT;
import static pwe.planner.logic.parser.CommandParserTestUtil.assertParseFailure;
import static pwe.planner.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.Test;

import pwe.planner.commons.core.Messages;
import pwe.planner.logic.commands.ClearCommand;

public class ClearCommandParserTest {
    private ClearCommandParser parser = new ClearCommandParser();

    @Test
    public void clear_success() {

        // empty argument
        assertParseSuccess(parser, "", new ClearCommand());
        // multiple whitespaces
        assertParseSuccess(parser, "                   ", new ClearCommand());

        // clear command with valid argument
        assertParseSuccess(parser, PLANNER, new ClearCommand(PLANNER));
        assertParseSuccess(parser, REQUIREMENT, new ClearCommand(REQUIREMENT));

        // clear command with case insensitive
        assertParseSuccess(parser, "PLANNER", new ClearCommand(PLANNER));
        assertParseSuccess(parser, "REQUIREMENT", new ClearCommand(REQUIREMENT));

        // clear command with mixed cases
        assertParseSuccess(parser, "PlAnNeR", new ClearCommand(PLANNER));
        assertParseSuccess(parser, "ReQuIrEmEnt", new ClearCommand(REQUIREMENT));
    }

    @Test
    public void clear_badArgument() {

        // invalid argument
        assertParseFailure(parser, "notExisting", String.format(
                Messages.MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE));
        // invalid argument with leading and trailing space.
        assertParseFailure(parser, "            notExisting             ", String.format(
                Messages.MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE));

        // substring of a valid argument -> false
        assertParseFailure(parser, "planne", String.format(
                Messages.MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE));
        assertParseFailure(parser, "modul", String.format(
                Messages.MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE));
        assertParseFailure(parser, "requriemen", String.format(
                Messages.MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE));

    }
}
