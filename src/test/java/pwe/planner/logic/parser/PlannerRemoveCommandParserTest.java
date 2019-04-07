package pwe.planner.logic.parser;

import static pwe.planner.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_CODE;
import static pwe.planner.logic.parser.CommandParserTestUtil.assertParseFailure;
import static pwe.planner.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import pwe.planner.logic.commands.PlannerRemoveCommand;
import pwe.planner.model.module.Code;

public class PlannerRemoveCommandParserTest {
    private PlannerRemoveCommandParser parser = new PlannerRemoveCommandParser();

    @Test
    public void parse_invalidValue_failure() {
        // invalid code
        assertParseFailure(parser, " " + PREFIX_CODE + "INVALID", Code.MESSAGE_CONSTRAINTS);

        // invalid code
        assertParseFailure(parser, " " + PREFIX_CODE + null, Code.MESSAGE_CONSTRAINTS);

        // one valid one invalid code
        assertParseFailure(parser, " " + PREFIX_CODE + "CS1010 " + PREFIX_CODE
                + "INVALID", Code.MESSAGE_CONSTRAINTS);

    }

    @Test
    public void parse_validArgs_returnsPlannerRemoveCommand() {
        Code validCode = new Code("CS1010");
        Code anotherValidCode = new Code("CS2100");
        Set<Code> validCodeSet = new HashSet<>();
        validCodeSet.add(validCode);
        validCodeSet.add(anotherValidCode);

        PlannerRemoveCommand expectedPlannerRemoveCommand = new PlannerRemoveCommand(validCodeSet);

        assertParseSuccess(parser, " " + PREFIX_CODE + "CS1010 " + PREFIX_CODE
                + "CS2100", expectedPlannerRemoveCommand);
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, PlannerRemoveCommand.MESSAGE_USAGE);

        // missing code prefix
        assertParseFailure(parser, " " + "CS1010", expectedMessage);
    }
}
