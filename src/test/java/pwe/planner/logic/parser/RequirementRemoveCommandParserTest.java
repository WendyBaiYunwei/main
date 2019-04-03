package pwe.planner.logic.parser;

import static pwe.planner.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static pwe.planner.logic.commands.CommandTestUtil.PREAMBLE_NON_EMPTY;
import static pwe.planner.logic.commands.CommandTestUtil.PREAMBLE_WHITESPACE;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_CODE;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_NAME;
import static pwe.planner.logic.parser.CommandParserTestUtil.assertParseFailure;
import static pwe.planner.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import pwe.planner.logic.commands.RequirementRemoveCommand;
import pwe.planner.model.module.Code;
import pwe.planner.model.module.Name;

public class RequirementRemoveCommandParserTest {

    private RequirementRemoveCommandParser parser = new RequirementRemoveCommandParser();

    @Test
    public void parse_invalidValue_failure() {
        // invalid format
        assertParseFailure(parser, " INVALID INPUT", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                RequirementRemoveCommand.MESSAGE_USAGE));

        // invalid name format
        assertParseFailure(parser, " " + PREFIX_NAME + "  " + PREFIX_CODE + "CS1010 ",
                Name.MESSAGE_CONSTRAINTS);

        // invalid code format
        assertParseFailure(parser, " " + PREFIX_NAME + "Computing Foundation " + PREFIX_CODE + "1231",
                Code.MESSAGE_CONSTRAINTS);

        // invalid name and code format
        assertParseFailure(parser, " " + PREFIX_NAME + "   " + PREFIX_CODE + "1231",
                Name.MESSAGE_CONSTRAINTS);

        // non-empty preamble
        assertParseFailure(parser,
                PREAMBLE_NON_EMPTY + " " + PREFIX_NAME + "Computing Foundation " + PREFIX_CODE + "CS1231",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, RequirementRemoveCommand.MESSAGE_USAGE));

    }

    @Test
    public void parse_validArgs_returnsRequirementDeleteCommand() {
        Name validName = new Name("Computing Foundation");
        Set<Code> validCodeSet = new HashSet<>();
        validCodeSet.add(new Code("CS1010"));

        RequirementRemoveCommand expectedRequirementRemoveCommand =
                new RequirementRemoveCommand(validName, validCodeSet);

        assertParseSuccess(parser, " " + PREFIX_NAME + "Computing Foundation " + PREFIX_CODE + "CS1010 ",
                expectedRequirementRemoveCommand);

        // whitespace only preamble
        assertParseSuccess(parser,
                PREAMBLE_WHITESPACE + " " + PREFIX_NAME + "Computing Foundation " + PREFIX_CODE + "CS1010 ",
                expectedRequirementRemoveCommand);

        // multiple requirement categories specified - last requirement category accepted
        assertParseSuccess(parser, " " + PREFIX_NAME + "Mathematics " + PREFIX_NAME
                + "Computing Foundation " + PREFIX_CODE + "CS1010", expectedRequirementRemoveCommand);

    }
}
