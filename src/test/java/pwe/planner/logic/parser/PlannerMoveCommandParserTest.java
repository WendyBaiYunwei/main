package pwe.planner.logic.parser;

import static pwe.planner.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static pwe.planner.logic.commands.CommandTestUtil.PREAMBLE_NON_EMPTY;
import static pwe.planner.logic.commands.CommandTestUtil.PREAMBLE_WHITESPACE;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_CODE;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_SEMESTER;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_YEAR;
import static pwe.planner.logic.parser.CommandParserTestUtil.assertParseFailure;
import static pwe.planner.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.Test;

import pwe.planner.logic.commands.PlannerMoveCommand;
import pwe.planner.model.module.Code;
import pwe.planner.model.planner.Semester;
import pwe.planner.model.planner.Year;

public class PlannerMoveCommandParserTest {
    private PlannerMoveCommandParser parser = new PlannerMoveCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        //Displays usage
        assertParseFailure(parser, "", PlannerMoveCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_invalidValue_failure() {
        // invalid code
        assertParseFailure(parser, " " + PREFIX_YEAR + "1 " + PREFIX_SEMESTER + "1 "
                + PREFIX_CODE + "INVALID", Code.MESSAGE_CONSTRAINTS);

        // invalid year
        assertParseFailure(parser, " " + PREFIX_YEAR + "999 " + PREFIX_SEMESTER + "1 "
                + PREFIX_CODE + "CS1010", Year.MESSAGE_YEAR_CONSTRAINTS);

        // invalid semester
        assertParseFailure(parser, " " + PREFIX_YEAR + "1 " + PREFIX_SEMESTER + "999 "
                + PREFIX_CODE + "CS1010", Semester.MESSAGE_SEMESTER_CONSTRAINTS);

        // two invalid values, only first invalid value reported
        assertParseFailure(parser, " " + PREFIX_YEAR + "1 " + PREFIX_SEMESTER + "999 "
                + PREFIX_CODE + "INVALID", Semester.MESSAGE_SEMESTER_CONSTRAINTS);

        // non-empty preamble
        assertParseFailure(parser,
                PREAMBLE_NON_EMPTY + " " + PREFIX_YEAR + "1 " + PREFIX_SEMESTER + "1 " + PREFIX_CODE + "CS1010 ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, PlannerMoveCommand.MESSAGE_USAGE));

    }

    @Test
    public void parse_validArgs_returnsPlannerMoveCommand() {
        Code validCodeToMove = new Code("CS1010");
        Year validYear = new Year("1");
        Semester validSemester = new Semester("2");

        PlannerMoveCommand expectedPlannerMoveCommand =
                new PlannerMoveCommand(validYear, validSemester, validCodeToMove);

        assertParseSuccess(parser, " " + PREFIX_YEAR + "1 " + PREFIX_SEMESTER + "2 "
                + PREFIX_CODE + "CS1010", expectedPlannerMoveCommand);

        // whitespace only preamble
        assertParseSuccess(parser,
                PREAMBLE_WHITESPACE + " " + PREFIX_YEAR + "1 " + PREFIX_SEMESTER + "2 "
                        + PREFIX_CODE + "CS1010", expectedPlannerMoveCommand);

        // multiple codes - last code accepted
        assertParseSuccess(parser, " " + PREFIX_YEAR + "1 " + PREFIX_SEMESTER + "2 "
                + PREFIX_CODE + "CS1011 " + PREFIX_CODE + "CS1010 ", expectedPlannerMoveCommand);

        // multiple years - last year accepted
        assertParseSuccess(parser, " " + PREFIX_YEAR + "2 " + PREFIX_YEAR + "1 " + PREFIX_SEMESTER + "2 "
                + PREFIX_CODE + "CS1010 ", expectedPlannerMoveCommand);

        // multiple semesters - last semester accepted
        assertParseSuccess(parser, " " + PREFIX_YEAR + "1 " + PREFIX_SEMESTER + "1 " + PREFIX_SEMESTER + "2 "
                + PREFIX_CODE + "CS1010 ", expectedPlannerMoveCommand);
    }

    @Test
    public void parse_missingParts_failure() {
        // no year specified
        assertParseFailure(parser, " " + PREFIX_YEAR + " " + PREFIX_SEMESTER + "2 "
                + PREFIX_CODE + "CS1010", Year.MESSAGE_YEAR_CONSTRAINTS);

        // no semester specified
        assertParseFailure(parser, " " + PREFIX_YEAR + "1 " + PREFIX_SEMESTER + " "
                + PREFIX_CODE + "CS1010", Semester.MESSAGE_SEMESTER_CONSTRAINTS);

        // no code specified
        assertParseFailure(parser, " " + PREFIX_YEAR + "1 " + PREFIX_SEMESTER + "2 "
                + PREFIX_CODE + " ", Code.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, PlannerMoveCommand.MESSAGE_USAGE);

        // missing year prefix
        assertParseFailure(parser, " " + "1 " + PREFIX_SEMESTER + "2 " + PREFIX_CODE + "CS1010", expectedMessage);

        // missing semester prefix
        assertParseFailure(parser, " " + PREFIX_YEAR + "1 " + "2 " + PREFIX_CODE + "CS1010", expectedMessage);

        // missing code prefix
        assertParseFailure(parser, " " + PREFIX_YEAR + "1 " + PREFIX_SEMESTER + "2 " + "CS1010", expectedMessage);

        // all prefixes missing
        assertParseFailure(parser, "1 " + "2 " + "CS1010", expectedMessage);
    }
}
