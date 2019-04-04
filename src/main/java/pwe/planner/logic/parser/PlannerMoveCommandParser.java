package pwe.planner.logic.parser;

import static java.util.Objects.requireNonNull;
import static pwe.planner.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_CODE;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_SEMESTER;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_YEAR;
import static pwe.planner.logic.parser.ParserUtil.arePrefixesPresent;
import static pwe.planner.logic.parser.ParserUtil.parseCode;
import static pwe.planner.logic.parser.ParserUtil.parseSemester;
import static pwe.planner.logic.parser.ParserUtil.parseYear;

import pwe.planner.logic.commands.PlannerMoveCommand;
import pwe.planner.logic.parser.exceptions.ParseException;
import pwe.planner.model.module.Code;
import pwe.planner.model.planner.Semester;
import pwe.planner.model.planner.Year;

/**
 * Parses input arguments and creates a new PlannerMoveCommand object
 */
public class PlannerMoveCommandParser implements Parser<PlannerMoveCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the PlannerMoveCommand
     * and returns an PlannerMoveCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public PlannerMoveCommand parse(String args) throws ParseException {
        requireNonNull(args);

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_YEAR, PREFIX_SEMESTER, PREFIX_CODE);

        if (!arePrefixesPresent(argMultimap, PREFIX_YEAR, PREFIX_SEMESTER, PREFIX_CODE)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, PlannerMoveCommand.MESSAGE_USAGE));
        }

        Year year = parseYear(argMultimap.getValue(PREFIX_YEAR).get());
        Semester semester = parseSemester(argMultimap.getValue(PREFIX_SEMESTER).get());
        Code code = parseCode(argMultimap.getValue(PREFIX_CODE).get());

        return new PlannerMoveCommand(year, semester, code);
    }
}
