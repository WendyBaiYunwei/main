package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SEMESTER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_YEAR;

import java.util.Set;
import java.util.stream.Stream;

import seedu.address.logic.commands.PlannerAddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.module.Code;
import seedu.address.model.planner.DegreePlanner;
import seedu.address.model.planner.Semester;
import seedu.address.model.planner.Year;

/**
 * Parses input arguments and creates a new PlannerAddCommand object
 */
public class PlannerAddCommandParser implements Parser<PlannerAddCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the PlannerAddCommand
     * and returns an PlannerAddCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public PlannerAddCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_CODE, PREFIX_YEAR , PREFIX_SEMESTER);

        if (!arePrefixesPresent(argMultimap, PREFIX_CODE, PREFIX_YEAR , PREFIX_SEMESTER)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, PlannerAddCommand.MESSAGE_USAGE));
        }

        Set<Code> codes = ParserUtil.parseCodes(argMultimap.getAllValues(PREFIX_CODE));
        Year year = ParserUtil.parseYear(argMultimap.getValue(PREFIX_YEAR).get());
        Semester semester = ParserUtil.parseSemester(argMultimap.getValue(PREFIX_SEMESTER).get());

        return new PlannerAddCommand(year, semester, codes);
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

}
