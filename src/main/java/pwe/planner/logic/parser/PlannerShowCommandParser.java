package pwe.planner.logic.parser;

import static java.util.Objects.requireNonNull;
import static pwe.planner.commons.core.LogsCenter.getLogger;
import static pwe.planner.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_SEMESTER;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_YEAR;

import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;

import pwe.planner.logic.commands.PlannerShowCommand;
import pwe.planner.logic.parser.exceptions.BooleanParserException;
import pwe.planner.logic.parser.exceptions.BooleanParserPredicateException;
import pwe.planner.logic.parser.exceptions.ParseException;
import pwe.planner.model.planner.DegreePlanner;

/**
 * Parses input arguments and creates a new PlannerShowCommand object
 */
public class PlannerShowCommandParser implements Parser<PlannerShowCommand> {
    private static final Logger logger = getLogger(PlannerShowCommandParser.class);
    private static final List<Prefix> PREFIXES = List.of(
            PREFIX_YEAR,
            PREFIX_SEMESTER
    );

    /**
     * Parses the given {@code String} of arguments in the context of the PlannerShowCommand
     * and returns an PlannerShowCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public PlannerShowCommand parse(String args) throws ParseException {
        requireNonNull(args);

        if (args.isEmpty()) {
            throw new ParseException(PlannerShowCommand.MESSAGE_USAGE);
        }

        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, PlannerShowCommand.MESSAGE_USAGE));
        }
        try {
            BooleanExpressionParser<DegreePlanner> expressionParser = new BooleanExpressionParser<>(args, PREFIXES);
            Predicate<DegreePlanner> predicate = expressionParser.parse();
            return new PlannerShowCommand(predicate);
        } catch (BooleanParserPredicateException predicateException) {
            logger.warning(predicateException.getMessage());
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, PlannerShowCommand.MESSAGE_USAGE));
        } catch (BooleanParserException parserException) {
            throw new ParseException(parserException.getMessage());
        }
    }
}
