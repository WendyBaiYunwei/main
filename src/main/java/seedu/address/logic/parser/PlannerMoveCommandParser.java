package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SEMESTER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_YEAR;
import static seedu.address.logic.parser.ParserUtil.arePrefixesPresent;

import seedu.address.logic.commands.PlannerMoveCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.module.Code;
import seedu.address.model.planner.Semester;
import seedu.address.model.planner.Year;

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

        Year year = ParserUtil.parseYear(argMultimap.getValue(PREFIX_YEAR).get());
        Semester semester = ParserUtil.parseSemester(argMultimap.getValue(PREFIX_SEMESTER).get());
        Code code = ParserUtil.parseCode(argMultimap.getValue(PREFIX_CODE).get());

        return new PlannerMoveCommand(year, semester, code);
    }
}
