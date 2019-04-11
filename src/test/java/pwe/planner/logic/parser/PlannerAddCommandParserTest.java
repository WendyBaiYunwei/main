package pwe.planner.logic.parser;

import static pwe.planner.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_CODE;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_SEMESTER;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_YEAR;
import static pwe.planner.logic.parser.CommandParserTestUtil.assertParseFailure;
import static pwe.planner.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import pwe.planner.logic.commands.PlannerAddCommand;
import pwe.planner.model.module.Code;
import pwe.planner.model.planner.Semester;
import pwe.planner.model.planner.Year;

public class PlannerAddCommandParserTest {
    private PlannerAddCommandParser parser = new PlannerAddCommandParser();

    @Test
    public void parse_invalidValue_failure() {

        // invalid year
        assertParseFailure(parser, " " + PREFIX_YEAR + "-1 " + PREFIX_SEMESTER + "1 "
                + PREFIX_CODE + "CS1010", Year.MESSAGE_YEAR_CONSTRAINTS);

        // invalid year
        assertParseFailure(parser, " " + PREFIX_YEAR + "% " + PREFIX_SEMESTER + "1 "
                + PREFIX_CODE + "CS1010", Year.MESSAGE_YEAR_CONSTRAINTS);

        // invalid semester
        assertParseFailure(parser, " " + PREFIX_YEAR + "1 " + PREFIX_SEMESTER + "-1 "
                + PREFIX_CODE + "CS1010", Semester.MESSAGE_SEMESTER_CONSTRAINTS);

        // invalid semester
        assertParseFailure(parser, " " + PREFIX_YEAR + "1 " + PREFIX_SEMESTER + "% "
                + PREFIX_CODE + "CS1010", Semester.MESSAGE_SEMESTER_CONSTRAINTS);

        // invalid code
        assertParseFailure(parser, " " + PREFIX_YEAR + "1 " + PREFIX_SEMESTER + "1 "
                + PREFIX_CODE + "INVALID", Code.MESSAGE_CONSTRAINTS);

    }

    @Test
    public void parse_validArgs_returnsPlannerAddCommand() {
        Code validCode = new Code("CS1010");
        Set<Code> validCodeSet = new HashSet<>();
        validCodeSet.add(validCode);
        Year validYear = new Year("1");
        Semester validSemester = new Semester("4");

        PlannerAddCommand expectedPlannerAddCommand =
                new PlannerAddCommand(validYear, validSemester, validCodeSet);

        assertParseSuccess(parser, " " + PREFIX_YEAR + "1 " + PREFIX_SEMESTER + "4 "
                + PREFIX_CODE + "CS1010", expectedPlannerAddCommand);
    }

    @Test
    public void parse_missingParts_failure() {
        // no year specified
        assertParseFailure(parser, " " + PREFIX_YEAR + " " + PREFIX_SEMESTER + "3 "
                + PREFIX_CODE + "CS1010", Year.MESSAGE_YEAR_CONSTRAINTS);

        // no semester specified
        assertParseFailure(parser, " " + PREFIX_YEAR + "4 " + PREFIX_SEMESTER + " "
                + PREFIX_CODE + "CS1010", Semester.MESSAGE_SEMESTER_CONSTRAINTS);

        // no code specified
        assertParseFailure(parser, " " + PREFIX_YEAR + "1 " + PREFIX_SEMESTER + "2 "
                + PREFIX_CODE + " ", Code.MESSAGE_CONSTRAINTS);

    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, PlannerAddCommand.MESSAGE_USAGE);

        // missing parameter year number
        assertParseFailure(parser, " " + PREFIX_YEAR + null + PREFIX_SEMESTER + "1 "
                + PREFIX_CODE + "CS1010", expectedMessage);

        // missing parameter year number
        assertParseFailure(parser, " " + PREFIX_YEAR + "1 " + PREFIX_SEMESTER + null
                + PREFIX_CODE + "CS1010", expectedMessage);

        // missing year prefix
        assertParseFailure(parser, "" + "1 " + PREFIX_SEMESTER + "2 " + PREFIX_CODE + "CS1010", expectedMessage);

        // missing semester prefix
        assertParseFailure(parser, " " + PREFIX_YEAR + "1 " + "2 " + PREFIX_CODE + "CS1010", expectedMessage);

        // missing code prefix
        assertParseFailure(parser, " " + PREFIX_YEAR + "1 " + PREFIX_SEMESTER + "2 " + "CS1010", expectedMessage);
    }
}
