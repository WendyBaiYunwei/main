package pwe.planner.logic.parser;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static pwe.planner.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static pwe.planner.logic.parser.CliSyntax.OPERATOR_AND;
import static pwe.planner.logic.parser.CliSyntax.OPERATOR_LEFT_BRACKET;
import static pwe.planner.logic.parser.CliSyntax.OPERATOR_OR;
import static pwe.planner.logic.parser.CliSyntax.OPERATOR_RIGHT_BRACKET;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_SEMESTER;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_YEAR;
import static pwe.planner.logic.parser.CommandParserTestUtil.assertParseFailure;
import static pwe.planner.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.Test;

import pwe.planner.logic.commands.PlannerShowCommand;
import pwe.planner.logic.parser.exceptions.ParseException;
import pwe.planner.model.planner.Semester;
import pwe.planner.model.planner.SemesterContainsKeywordPredicate;
import pwe.planner.model.planner.Year;
import pwe.planner.model.planner.YearContainsKeywordPredicate;

public class PlannerShowCommandParserTest {
    private static final String WHITESPACE = " ";
    private PlannerShowCommandParser parser = new PlannerShowCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        //Displays usage
        assertParseFailure(parser, "", PlannerShowCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "     ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, PlannerShowCommand.MESSAGE_USAGE));
        // No year argument -> assertFailure
        assertParseFailure(parser, PREFIX_YEAR + "     ", Year.MESSAGE_YEAR_CONSTRAINTS);
        // No semester argument -> assertFailure
        assertParseFailure(parser, PREFIX_SEMESTER + "     ", Semester.MESSAGE_SEMESTER_CONSTRAINTS);
    }

    @Test
    public void parse_validArgs_returnsPlannerShowCommand() {
        PlannerShowCommand expectedPlannerShowYearCommand =
                new PlannerShowCommand(new YearContainsKeywordPredicate<>("1"));
        // single year
        assertParseSuccess(parser, PREFIX_YEAR + "1", expectedPlannerShowYearCommand);

        PlannerShowCommand expectedPlannerShowSemesterCommand =
                new PlannerShowCommand(new SemesterContainsKeywordPredicate<>("1"));
        // single semester
        assertParseSuccess(parser, PREFIX_SEMESTER + "1", expectedPlannerShowSemesterCommand);
    }

    @Test
    public void parse_validArgs() {
        // test for boolean OR
        // year/1 || year/2
        assertParserSuccess(parser, PREFIX_YEAR + "1" + WHITESPACE + OPERATOR_OR + WHITESPACE
                + PREFIX_YEAR + "2");
        // sem/1 || sem/2
        assertParserSuccess(parser, PREFIX_SEMESTER + "1" + WHITESPACE + OPERATOR_OR + WHITESPACE
                + PREFIX_SEMESTER + "2");

        // test for boolean AND
        // year/1 && year/1
        assertParserSuccess(parser, PREFIX_YEAR + "1" + WHITESPACE + OPERATOR_AND + WHITESPACE
                + PREFIX_YEAR + "1");
        // sem/1 && sem/1
        assertParserSuccess(parser, PREFIX_SEMESTER + "1" + WHITESPACE + OPERATOR_AND + WHITESPACE
                + PREFIX_SEMESTER + "1");
        // year/1 && sem/2
        assertParserSuccess(parser, PREFIX_YEAR + "1" + WHITESPACE + OPERATOR_AND + WHITESPACE
                + PREFIX_SEMESTER + "2");

        // test for boolean AND OR with LEFT_BRACKET RIGHT_BRACKET
        // year/1 && ( sem/1 || sem/2 )
        assertParserSuccess(parser, PREFIX_YEAR + "1" + WHITESPACE + OPERATOR_AND + WHITESPACE
                + OPERATOR_LEFT_BRACKET + WHITESPACE + PREFIX_SEMESTER + "1" + WHITESPACE + OPERATOR_OR
                + WHITESPACE + PREFIX_SEMESTER + "2" + WHITESPACE + OPERATOR_RIGHT_BRACKET);
    }

    @Test
    public void parseInvalidArgs() {
        assertParseThrowsException(parser, "invalid/DoesNotExists");
        // year/1 sem/2
        assertParseThrowsException(parser, PREFIX_YEAR + "1" + WHITESPACE + PREFIX_SEMESTER + "2");
        // ()
        assertParseThrowsException(parser, OPERATOR_LEFT_BRACKET + OPERATOR_RIGHT_BRACKET);
        // ()()
        assertParseThrowsException(parser, OPERATOR_LEFT_BRACKET + OPERATOR_RIGHT_BRACKET + OPERATOR_LEFT_BRACKET
                + OPERATOR_RIGHT_BRACKET);
        // ())
        assertParseThrowsException(parser, OPERATOR_LEFT_BRACKET + OPERATOR_RIGHT_BRACKET + OPERATOR_RIGHT_BRACKET);
        // year/1 ) sem/2
        assertParseThrowsException(parser,
                PREFIX_YEAR + "1" + WHITESPACE + OPERATOR_RIGHT_BRACKET + PREFIX_SEMESTER + "2");
        // (year/1 || sem/2
        assertParseThrowsException(parser, OPERATOR_LEFT_BRACKET + PREFIX_YEAR + "1" + WHITESPACE + OPERATOR_OR
                + PREFIX_SEMESTER + "2");
        // year/1 && (sem/1 || sem/2))
        assertParseThrowsException(parser, PREFIX_YEAR + "1" + WHITESPACE + OPERATOR_AND + OPERATOR_LEFT_BRACKET
                + PREFIX_SEMESTER + "1" + WHITESPACE + OPERATOR_OR + PREFIX_SEMESTER + "2" + OPERATOR_RIGHT_BRACKET
                + OPERATOR_RIGHT_BRACKET);
    }

    /**
     * Assert if a parse is successful.
     * Checks if a parser return a PlannerShowCommand.
     */
    private void assertParserSuccess(PlannerShowCommandParser parser, String input) {
        try {
            PlannerShowCommand command = parser.parse(input);
            assertNotNull("Expecting not null", command);
        } catch (ParseException e) {
            fail("Expecting no parser error");
        }
    }

    /**
     * Assert if a parse return an exception.
     */
    private void assertParseThrowsException(PlannerShowCommandParser parser, String str) {
        try {
            parser.parse(str);
            fail("Expected a parse error");
        } catch (ParseException ignore) {
            // we want the exception to be thrown.
        }
    }
}
