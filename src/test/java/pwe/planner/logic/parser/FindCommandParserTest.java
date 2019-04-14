package pwe.planner.logic.parser;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static pwe.planner.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static pwe.planner.logic.parser.CliSyntax.OPERATOR_AND;
import static pwe.planner.logic.parser.CliSyntax.OPERATOR_LEFT_BRACKET;
import static pwe.planner.logic.parser.CliSyntax.OPERATOR_OR;
import static pwe.planner.logic.parser.CliSyntax.OPERATOR_RIGHT_BRACKET;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_CODE;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_CREDITS;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_NAME;
import static pwe.planner.logic.parser.CommandParserTestUtil.assertParseFailure;
import static pwe.planner.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.Test;

import pwe.planner.logic.commands.FindCommand;
import pwe.planner.logic.parser.exceptions.ParseException;
import pwe.planner.model.module.Code;
import pwe.planner.model.module.CodeContainsKeywordsPredicate;
import pwe.planner.model.module.Credits;
import pwe.planner.model.module.CreditsContainsKeywordsPredicate;
import pwe.planner.model.module.Name;
import pwe.planner.model.module.NameContainsKeywordsPredicate;

public class FindCommandParserTest {

    private static final String WHITESPACE = " ";
    private FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        // No name argument -> assertFailure
        assertParseFailure(parser, PREFIX_NAME + "     ", Name.MESSAGE_CONSTRAINTS);
        // No code argument -> assertFailure
        assertParseFailure(parser, PREFIX_CODE + "     ", Code.MESSAGE_CONSTRAINTS);
        // No credits argument -> assertFailure
        assertParseFailure(parser, PREFIX_CREDITS + "     ", Credits.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_validArgs_returnsFindCommand() {
        // no leading and trailing whitespaces
        FindCommand expectedFindNameCommand =
                new FindCommand(new NameContainsKeywordsPredicate<>("Alice"));
        // single keyword
        assertParseSuccess(parser, PREFIX_NAME + "Alice", expectedFindNameCommand);

        FindCommand expectedFindCodeCommand =
                new FindCommand(new CodeContainsKeywordsPredicate<>("CS1231"));
        // single keyword
        assertParseSuccess(parser, PREFIX_CODE + "CS1231", expectedFindCodeCommand);

        FindCommand expectedFindCreditsCommand =
                new FindCommand(new CreditsContainsKeywordsPredicate<>("999"));
        // single keyword
        assertParseSuccess(parser, PREFIX_CREDITS + "999", expectedFindCreditsCommand);
    }

    @Test
    public void parse_validArgs() {
        // test for boolean OR
        // name/NAME || name/NAME
        assertParserSuccess(parser, PREFIX_NAME + "Programming " + WHITESPACE + OPERATOR_OR + WHITESPACE
                + PREFIX_NAME + "Discrete");
        // code/CODE || code/CODE
        assertParserSuccess(parser, PREFIX_CODE + "CS1231 " + WHITESPACE + OPERATOR_OR + WHITESPACE
                + PREFIX_CODE + "CS2040C");
        // credits/CREDITS || credits/CREDITS
        assertParserSuccess(parser, PREFIX_CREDITS + "4 " + WHITESPACE + OPERATOR_OR + WHITESPACE
                + PREFIX_CREDITS + "999");

        // test for boolean AND
        // name/NAME && name/NAME
        assertParserSuccess(parser, PREFIX_NAME + "Programming " + WHITESPACE + OPERATOR_AND + WHITESPACE
                + PREFIX_NAME + "Methodology");
        // code/CODE && code/CODE
        assertParserSuccess(parser, PREFIX_CODE + "CS1231 " + WHITESPACE + OPERATOR_AND + WHITESPACE
                + PREFIX_CODE + "CS2040C");
        // credits/CREDITS && credits/CREDITS
        assertParserSuccess(parser, PREFIX_CREDITS + "4 " + WHITESPACE + OPERATOR_AND + WHITESPACE
                + PREFIX_CREDITS + "999");

        // test for boolean AND and boolean OR
        // credits/CREDITS || credits/CREDITS && name/Programming
        assertParserSuccess(parser, PREFIX_CREDITS + "4 " + WHITESPACE + OPERATOR_OR + WHITESPACE
                + PREFIX_CREDITS + "999" + OPERATOR_AND + PREFIX_NAME + "programming");

        // credits/4 || (name/Information && name/Security)
        assertParserSuccess(parser, PREFIX_CREDITS + "4" + OPERATOR_OR + OPERATOR_LEFT_BRACKET + PREFIX_NAME
                + "information" + OPERATOR_AND + PREFIX_NAME + "security" + OPERATOR_RIGHT_BRACKET);

    }

    @Test
    public void parseInvalidArgs() {
        assertParseThrowsException(parser, "invalid/DoesNotExists");
        // name/Programming code/CS1231
        assertParseThrowsException(parser, PREFIX_NAME + "Programming " + PREFIX_CODE + "CS1231");
        // ()
        assertParseThrowsException(parser, OPERATOR_LEFT_BRACKET + OPERATOR_RIGHT_BRACKET);
        // ()()
        assertParseThrowsException(parser, OPERATOR_LEFT_BRACKET + OPERATOR_RIGHT_BRACKET + OPERATOR_LEFT_BRACKET
                + OPERATOR_RIGHT_BRACKET);
        // ())
        assertParseThrowsException(parser, OPERATOR_LEFT_BRACKET + OPERATOR_RIGHT_BRACKET + OPERATOR_RIGHT_BRACKET);
        // name/Programming ) code/CS1231
        assertParseThrowsException(parser, PREFIX_NAME + "Programming " + OPERATOR_RIGHT_BRACKET + PREFIX_CODE
                + "CS1231");
        // (name/Programming || code/CS1231
        assertParseThrowsException(parser, OPERATOR_LEFT_BRACKET + PREFIX_NAME + "Programming" + OPERATOR_OR
                + PREFIX_CODE + "CS1231");
        // credits/4 || (name/Programming AND code/CS1231))
        assertParseThrowsException(parser, PREFIX_CREDITS + "4" + OPERATOR_OR + OPERATOR_LEFT_BRACKET + PREFIX_NAME
                + "Programming" + OPERATOR_AND + PREFIX_CODE + "CS1231" + OPERATOR_RIGHT_BRACKET
                + OPERATOR_RIGHT_BRACKET);

    }

    /**
     * Assert if a parse is successful. This does not check if {@code FindCommand} is the same
     * Rather it check if a parser return a FindCommand.
     */
    private void assertParserSuccess(FindCommandParser parser, String input) {
        try {
            FindCommand command = parser.parse(input);
            assertNotNull("Expecting not null", command);
        } catch (ParseException e) {
            fail("Expecting no parser error");
        }
    }

    /**
     * Assert if a parse return an exception.
     */
    private void assertParseThrowsException(FindCommandParser parser, String str) {
        try {
            parser.parse(str);
            fail("Expected a parse error");
        } catch (ParseException ignore) {
            // we want the exception to be thrown.
        }
    }

}
