package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_COREQUISITE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CREDITS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.ParserUtil.arePrefixesPresent;

import java.util.Set;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.module.Code;
import seedu.address.model.module.Credits;
import seedu.address.model.module.Module;
import seedu.address.model.module.Name;
import seedu.address.model.tag.Tag;

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

        Code code = ParserUtil.parseCode(argMultimap.getValue(PREFIX_CODE).get());
        Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
        Credits credits = ParserUtil.parseCredits(argMultimap.getValue(PREFIX_CREDITS).get());
        Set<Code> corequisiteList = ParserUtil.parseCorequisites(argMultimap.getAllValues(PREFIX_COREQUISITE));
        Set<Tag> tagList = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));

        Module module = new Module(name, credits, code, tagList, corequisiteList);
        return new AddCommand(module);
    }

}
