package pwe.planner.logic.parser;

import static java.util.Objects.requireNonNull;
import static pwe.planner.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_CODE;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_NAME;
import static pwe.planner.logic.parser.ParserUtil.arePrefixesPresent;
import static pwe.planner.logic.parser.ParserUtil.parseCodes;
import static pwe.planner.logic.parser.ParserUtil.parseName;

import java.util.Set;

import pwe.planner.logic.commands.RequirementMoveCommand;
import pwe.planner.logic.parser.exceptions.ParseException;
import pwe.planner.model.module.Code;
import pwe.planner.model.module.Name;

/**
 * Parses input arguments and creates a new RequirementMoveCommand object
 */
public class RequirementMoveCommandParser implements Parser<RequirementMoveCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the RequirementMoveCommand
     * and returns an RequirementMoveCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public RequirementMoveCommand parse(String args) throws ParseException {
        requireNonNull(args);

        if (args.isEmpty()) {
            throw new ParseException(RequirementMoveCommand.MESSAGE_USAGE);
        }

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_CODE);

        if (!arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_CODE) || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    RequirementMoveCommand.MESSAGE_USAGE));
        }

        Name name = parseName(argMultimap.getValue(PREFIX_NAME).get());
        Set<Code> codeSet = parseCodes(argMultimap.getAllValues(PREFIX_CODE));

        return new RequirementMoveCommand(name, codeSet);
    }
}
