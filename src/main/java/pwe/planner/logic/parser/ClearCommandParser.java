package pwe.planner.logic.parser;

import static java.util.Objects.requireNonNull;
import static pwe.planner.logic.commands.ClearCommand.MESSAGE_USAGE;
import static pwe.planner.logic.commands.ClearCommand.PLANNER;
import static pwe.planner.logic.commands.ClearCommand.REQUIREMENT;

import java.util.Set;

import pwe.planner.commons.core.Messages;
import pwe.planner.logic.commands.ClearCommand;
import pwe.planner.logic.parser.exceptions.ParseException;
import pwe.planner.model.Application;

/**
 * Parse the argument and clear the {@link Application} based on the arguments
 */
public class ClearCommandParser implements Parser<ClearCommand> {
    private Set<String> acceptedArgument = Set.of(PLANNER, REQUIREMENT);

    /**
     * Parse the user provided argument.
     *
     * @param args user provided argument
     * @return {@link ClearCommand}
     * @throws ParseException if no argument is provided
     */
    public ClearCommand parse(String args) throws ParseException {
        requireNonNull(args);

        String trimmedLowerArgs = args.trim().toLowerCase();
        if (trimmedLowerArgs.isEmpty()) {
            return new ClearCommand();
        }
        if (acceptedArgument.contains(trimmedLowerArgs)) {
            return new ClearCommand(trimmedLowerArgs);
        } else {
            throw new ParseException(String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE));
        }
    }
}
