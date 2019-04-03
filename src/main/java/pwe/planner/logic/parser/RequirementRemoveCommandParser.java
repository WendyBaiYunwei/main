package pwe.planner.logic.parser;

import static pwe.planner.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_CODE;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_NAME;
import static pwe.planner.logic.parser.ParserUtil.arePrefixesPresent;

import java.util.Set;

import pwe.planner.logic.commands.RequirementRemoveCommand;
import pwe.planner.logic.parser.exceptions.ParseException;
import pwe.planner.model.module.Code;
import pwe.planner.model.module.Name;

/**
 * Parses input arguments and creates a new RequirementRemoveCommand object
 */
public class RequirementRemoveCommandParser implements Parser<RequirementRemoveCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the RequirementRemoveCommand
     * and returns an RequirementRemoveCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public RequirementRemoveCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_CODE);

        if (!arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_CODE) || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, RequirementRemoveCommand.MESSAGE_USAGE));
        }

        Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
        Set<Code> codeList = ParserUtil.parseCodes(argMultimap.getAllValues(PREFIX_CODE));

        return new RequirementRemoveCommand(name, codeList);
    }
}
