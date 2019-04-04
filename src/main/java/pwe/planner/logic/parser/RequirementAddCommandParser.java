package pwe.planner.logic.parser;

import static java.util.Objects.requireNonNull;
import static pwe.planner.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_CODE;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_NAME;
import static pwe.planner.logic.parser.ParserUtil.arePrefixesPresent;
import static pwe.planner.logic.parser.ParserUtil.parseCodes;
import static pwe.planner.logic.parser.ParserUtil.parseName;

import java.util.Set;

import pwe.planner.logic.commands.RequirementAddCommand;
import pwe.planner.logic.parser.exceptions.ParseException;
import pwe.planner.model.module.Code;
import pwe.planner.model.module.Name;

/**
 * Parses input arguments and creates a new AddCommand object
 */
public class RequirementAddCommandParser {
    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public RequirementAddCommand parse(String args) throws ParseException {
        requireNonNull(args);

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_CODE);

        if (!arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_CODE) || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, RequirementAddCommand.MESSAGE_USAGE));
        }

        Name name = parseName(argMultimap.getValue(PREFIX_NAME).get());
        Set<Code> codeList = parseCodes(argMultimap.getAllValues(PREFIX_CODE));

        return new RequirementAddCommand(name, codeList);
    }
}
