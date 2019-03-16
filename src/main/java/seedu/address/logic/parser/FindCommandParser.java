package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CREDITS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.module.Code;
import seedu.address.model.module.CodeContainsKeywordsPredicate;
import seedu.address.model.module.Credits;
import seedu.address.model.module.CreditsContainsKeywordsPredicate;
import seedu.address.model.module.KeywordsPredicate;
import seedu.address.model.module.Module;
import seedu.address.model.module.Name;
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
        Predicate<Module> predicate = getKeywordsPredicate(args);
        return new FindCommand(predicate);
    }

    private Predicate<Module> getKeywordsPredicate(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_CODE, PREFIX_CREDITS);
        List<KeywordsPredicate> predicates = new ArrayList<>();
        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            String[] nameArgs = argMultimap.getValue(PREFIX_NAME).get().split("\\s+");
            List<String> nameKeywords = ParserUtil.parseMultiNames(nameArgs).stream().map(Name::toString)
                    .collect(Collectors.toList());
            predicates.add(new NameContainsKeywordsPredicate(nameKeywords));
        }
        if (argMultimap.getValue(PREFIX_CODE).isPresent()) {
            String[] codeArgs = argMultimap.getValue(PREFIX_CODE).get().split("\\s+");
            List<String> codeKeywords = ParserUtil.parseMultiCodes(codeArgs).stream().map(Code::toString)
                    .collect(Collectors.toList());
            predicates.add(new CodeContainsKeywordsPredicate(codeKeywords));
        }
        if (argMultimap.getValue(PREFIX_CREDITS).isPresent()) {
            String[] creditsArgs = argMultimap.getValue(PREFIX_CREDITS).get().split("\\s+");
            List<String> creditKeywords = ParserUtil.parseMultiCredits(creditsArgs).stream()
                    .map(Credits::toString).collect(Collectors.toList());
            predicates.add(new CreditsContainsKeywordsPredicate(creditKeywords));
        }
        if (predicates.size() == 0) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        // We have to use `Predicate<Module` instead of keywordsPredicate
        // because after predicate.or(...) this will return a Predicate<Module>
        Predicate<Module> predicate = predicates.get(0);
        for (int i = 1; i < predicates.size(); i++) {
            predicate = predicate.or(predicates.get(i));
        }

        return predicate;
    }
}
