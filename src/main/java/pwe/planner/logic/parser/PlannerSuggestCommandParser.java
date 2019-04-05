package pwe.planner.logic.parser;

import static java.util.Objects.requireNonNull;
import static pwe.planner.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_CODE;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_CREDITS;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_SEMESTER;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_TAG;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_YEAR;
import static pwe.planner.logic.parser.ParserUtil.arePrefixesPresent;
import static pwe.planner.logic.parser.ParserUtil.parseCodes;
import static pwe.planner.logic.parser.ParserUtil.parseCredits;
import static pwe.planner.logic.parser.ParserUtil.parseSemester;
import static pwe.planner.logic.parser.ParserUtil.parseTags;
import static pwe.planner.logic.parser.ParserUtil.parseYear;

import java.util.Set;

import pwe.planner.logic.commands.PlannerAddCommand;
import pwe.planner.logic.commands.PlannerSuggestCommand;
import pwe.planner.logic.parser.exceptions.ParseException;
import pwe.planner.model.module.Code;
import pwe.planner.model.module.Credits;
import pwe.planner.model.tag.Tag;

/**
 * Parses input arguments and creates a new PlannerSuggestCommand object.
 */
public class PlannerSuggestCommandParser implements Parser<PlannerSuggestCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the PlannerSuggestCommand
     * and returns an PlannerSuggestCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format.
     */
    public PlannerSuggestCommand parse(String args) throws ParseException {
        requireNonNull(args);

        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_CREDITS, PREFIX_TAG);

        if (!arePrefixesPresent(argMultimap, PREFIX_CREDITS, PREFIX_TAG)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(
                    MESSAGE_INVALID_COMMAND_FORMAT, PlannerSuggestCommand.MESSAGE_USAGE));
        }

        Credits credits = parseCredits(argMultimap.getValue(PREFIX_CREDITS).get());
        Set<Tag> tags = parseTags(argMultimap.getAllValues(PREFIX_TAG));
        return new PlannerSuggestCommand(credits, tags);
    }
}
