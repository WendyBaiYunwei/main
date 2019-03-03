package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CREDITS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import java.util.Arrays;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.module.CodeContainsKeywordsPredicate;
import seedu.address.model.module.CreditsContainsKeywordsPredicate;
import seedu.address.model.module.KeywordsPredicate;
import seedu.address.model.module.NameContainsKeywordsPredicate;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns an FindCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }
        KeywordsPredicate predicate = getKeywordsPredicate(args);
        return new FindCommand(predicate);
    }

    private KeywordsPredicate getKeywordsPredicate(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_CODE, PREFIX_CREDITS);
        KeywordsPredicate predicate = null;
        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            String[] nameKeywords = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get())
                    .toString().split("\\s+");
            predicate = new NameContainsKeywordsPredicate(Arrays.asList(nameKeywords));
        } else if (argMultimap.getValue(PREFIX_CODE).isPresent()) {
            String[] codeKeywords = ParserUtil.parseCode(argMultimap.getValue(PREFIX_CODE)
                    .get()).toString().split("\\s+");
            predicate = new CodeContainsKeywordsPredicate(Arrays.asList(codeKeywords));
        } else if (argMultimap.getValue(PREFIX_CREDITS).isPresent()) {
            String[] creditKeywords = ParserUtil.parseCredits(argMultimap.getValue(PREFIX_CREDITS)
                    .get()).toString().split("\\s+");
            predicate = new CreditsContainsKeywordsPredicate(Arrays.asList(creditKeywords));
        } else {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }
        return predicate;
    }
}
