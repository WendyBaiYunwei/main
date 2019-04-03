package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.ParserUtil.arePrefixesPresent;

import java.util.Set;

import seedu.address.logic.commands.RequirementAddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.module.Code;
import seedu.address.model.module.Name;

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
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_CODE);

        if (!arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_CODE) || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, RequirementAddCommand.MESSAGE_USAGE));
        }

        Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
        Set<Code> codeList = ParserUtil.parseCodes(argMultimap.getAllValues(PREFIX_CODE));

        return new RequirementAddCommand(name, codeList);
    }
}
