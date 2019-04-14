package pwe.planner.logic.parser;

import static pwe.planner.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static pwe.planner.logic.commands.CommandTestUtil.PREAMBLE_NON_EMPTY;
import static pwe.planner.logic.commands.CommandTestUtil.PREAMBLE_WHITESPACE;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_CODE;
import static pwe.planner.logic.parser.CommandParserTestUtil.assertParseFailure;
import static pwe.planner.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Set;

import org.junit.Test;

import pwe.planner.logic.commands.RequirementRemoveCommand;
import pwe.planner.model.module.Code;

public class RequirementRemoveCommandParserTest {

    private RequirementRemoveCommandParser parser = new RequirementRemoveCommandParser();

    @Test
    public void parse_nullValue_failure() {
        //empty input
        assertParseFailure(parser, "", RequirementRemoveCommand.MESSAGE_USAGE);

    }

    @Test
    public void parse_invalidValue_failure() {
        // invalid format -> false
        assertParseFailure(parser, " INVALID INPUT", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                RequirementRemoveCommand.MESSAGE_USAGE));

        // invalid code format -> false
        assertParseFailure(parser, " " + PREFIX_CODE + "1231", Code.MESSAGE_CONSTRAINTS);

        // invalid code format -> false
        assertParseFailure(parser, " " + PREFIX_CODE + "CS1010 " + PREFIX_CODE + "1231",
                Code.MESSAGE_CONSTRAINTS);

        // invalid code format -> false
        assertParseFailure(parser, " " + PREFIX_CODE + "1010 " + PREFIX_CODE + "CS1231",
                Code.MESSAGE_CONSTRAINTS);

        // non-empty preamble -> false
        assertParseFailure(parser,
                PREAMBLE_NON_EMPTY + " " + PREFIX_CODE + "CS1231",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, RequirementRemoveCommand.MESSAGE_USAGE));

    }

    @Test
    public void parse_validArgs_returnsRequirementRemoveCommand() {
        Set<Code> validCodeSet = Set.of(new Code("CS1010"));
        Set<Code> validMultiCodeSet = Set.of(new Code("CS1010"), new Code("CS1231"));

        RequirementRemoveCommand expectedRequirementRemoveCommand = new RequirementRemoveCommand(validCodeSet);

        //normal command -> true
        assertParseSuccess(parser, " " + PREFIX_CODE + "CS1010", expectedRequirementRemoveCommand);

        // whitespace only preamble -> true
        assertParseSuccess(parser, PREAMBLE_WHITESPACE + " " + PREFIX_CODE + "CS1010",
                expectedRequirementRemoveCommand);

        // same command -> true
        assertParseSuccess(parser, " " + PREFIX_CODE + "CS1010 ", expectedRequirementRemoveCommand);

        expectedRequirementRemoveCommand = new RequirementRemoveCommand(validMultiCodeSet);

        //normal command with multi codes -> true
        assertParseSuccess(parser, " " + PREFIX_CODE + "CS1010 " + PREFIX_CODE + "CS1231",
                expectedRequirementRemoveCommand);

        //whitespace only preamble -> true
        assertParseSuccess(parser, PREAMBLE_WHITESPACE + " " + PREFIX_CODE + "CS1010 " + PREFIX_CODE + "CS1231",
                expectedRequirementRemoveCommand);

        // same command -> true
        assertParseSuccess(parser, " " + PREFIX_CODE + "CS1010 " + PREFIX_CODE + "CS1231",
                expectedRequirementRemoveCommand);

    }
}
