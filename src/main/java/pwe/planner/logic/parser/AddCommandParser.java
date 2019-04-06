package pwe.planner.logic.parser;

import static java.util.Objects.requireNonNull;
import static pwe.planner.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_CODE;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_COREQUISITE;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_CREDITS;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_NAME;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_TAG;
import static pwe.planner.logic.parser.ParserUtil.arePrefixesPresent;
import static pwe.planner.logic.parser.ParserUtil.parseCode;
import static pwe.planner.logic.parser.ParserUtil.parseCorequisites;
import static pwe.planner.logic.parser.ParserUtil.parseCredits;
import static pwe.planner.logic.parser.ParserUtil.parseName;
import static pwe.planner.logic.parser.ParserUtil.parseTags;

import java.util.Set;

import pwe.planner.logic.commands.AddCommand;
import pwe.planner.logic.parser.exceptions.ParseException;
import pwe.planner.model.module.Code;
import pwe.planner.model.module.Credits;
import pwe.planner.model.module.Module;
import pwe.planner.model.module.Name;
import pwe.planner.model.tag.Tag;

/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddCommandParser implements Parser<AddCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the {@link AddCommand}
     * and returns an {@link AddCommand} object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public AddCommand parse(String args) throws ParseException {
        requireNonNull(args);

        if (args.isEmpty()) {
            throw new ParseException(AddCommand.MESSAGE_USAGE);
        }

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(
                args, PREFIX_NAME, PREFIX_CREDITS, PREFIX_CODE, PREFIX_TAG, PREFIX_COREQUISITE
        );

        boolean isAnyRequiredPrefixAbsent = !arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_CODE, PREFIX_CREDITS);
        if (isAnyRequiredPrefixAbsent || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }

        Code code = parseCode(argMultimap.getValue(PREFIX_CODE).get());
        Name name = parseName(argMultimap.getValue(PREFIX_NAME).get());
        Credits credits = parseCredits(argMultimap.getValue(PREFIX_CREDITS).get());
        Set<Code> corequisiteList = parseCorequisites(argMultimap.getAllValues(PREFIX_COREQUISITE));
        Set<Tag> tagList = parseTags(argMultimap.getAllValues(PREFIX_TAG));

        Module module = new Module(name, credits, code, tagList, corequisiteList);
        return new AddCommand(module);
    }
}
