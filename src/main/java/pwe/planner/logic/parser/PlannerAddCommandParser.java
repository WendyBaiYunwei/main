package pwe.planner.logic.parser;

import static pwe.planner.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_CODE;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_SEMESTER;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_YEAR;
import static pwe.planner.logic.parser.ParserUtil.arePrefixesPresent;

import java.util.Set;

import pwe.planner.logic.commands.PlannerAddCommand;
import pwe.planner.logic.parser.exceptions.ParseException;
import pwe.planner.model.module.Code;
import pwe.planner.model.planner.Semester;
import pwe.planner.model.planner.Year;

/**
 * Parses input arguments and creates a new PlannerAddCommand object.
 */
public class PlannerAddCommandParser implements Parser<PlannerAddCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the PlannerAddCommand
     * and returns an PlannerAddCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format.
     */
    public PlannerAddCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_CODE, PREFIX_YEAR , PREFIX_SEMESTER);

        if (!arePrefixesPresent(argMultimap, PREFIX_CODE, PREFIX_YEAR , PREFIX_SEMESTER)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, PlannerAddCommand.MESSAGE_USAGE));
        }

        Code code = ParserUtil.parseCode(argMultimap.getValue(PREFIX_CODE).get());
        Year year = ParserUtil.parseYear(argMultimap.getValue(PREFIX_YEAR).get());
        Semester semester = ParserUtil.parseSemester(argMultimap.getValue(PREFIX_SEMESTER).get());

        return new PlannerAddCommand(year, semester, code);
    }
}
