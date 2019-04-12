package pwe.planner.logic.parser;

import static java.util.Objects.requireNonNull;
import static pwe.planner.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_CODE;
import static pwe.planner.logic.parser.ParserUtil.arePrefixesPresent;

import java.util.Set;

import pwe.planner.logic.commands.PlannerRemoveCommand;
import pwe.planner.logic.parser.exceptions.ParseException;
import pwe.planner.model.module.Code;

/**
 * Parses input arguments and creates a new PlannerRemoveCommand object.
 */
public class PlannerRemoveCommandParser implements Parser<PlannerRemoveCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the PlannerRemoveCommand
     * and returns an PlannerRemoveCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format.
     */
    public PlannerRemoveCommand parse(String args) throws ParseException {
        requireNonNull(args);

        if (args.isEmpty()) {
            throw new ParseException(PlannerRemoveCommand.MESSAGE_USAGE);
        }

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_CODE);

        if (!arePrefixesPresent(argMultimap, PREFIX_CODE) || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, PlannerRemoveCommand.MESSAGE_USAGE));
        }

        Set<Code> codes = ParserUtil.parseCodes(argMultimap.getAllValues(PREFIX_CODE));

        return new PlannerRemoveCommand(codes);
    }
}
