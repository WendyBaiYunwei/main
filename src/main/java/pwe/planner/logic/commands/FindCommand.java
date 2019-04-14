package pwe.planner.logic.commands;

import static java.util.Objects.requireNonNull;
import static pwe.planner.commons.core.Messages.MESSAGE_MODULES_LISTED_OVERVIEW;
import static pwe.planner.logic.parser.CliSyntax.OPERATOR_AND;
import static pwe.planner.logic.parser.CliSyntax.OPERATOR_LEFT_BRACKET;
import static pwe.planner.logic.parser.CliSyntax.OPERATOR_OR;
import static pwe.planner.logic.parser.CliSyntax.OPERATOR_RIGHT_BRACKET;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_CODE;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_CREDITS;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_NAME;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.function.Predicate;

import pwe.planner.logic.CommandHistory;
import pwe.planner.model.Model;
import pwe.planner.model.module.Module;

/**
 * Finds and lists all modules in application whose name or code contains any of the argument keywords.
 * Keyword matching is case insensitive.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all modules which satisfies the expression"
            + " of search conditions specified and displays them as a list with index numbers.\n"
            + "Parameters: "
            + "[" + PREFIX_NAME + "NAME] "
            + "OPERATOR "
            + "[" + PREFIX_CODE + "CODE] "
            + "OPERATOR "
            + "[" + PREFIX_CREDITS + "CREDITS]"
            + "OPERATOR "
            + "[" + PREFIX_TAG + "TAGS]\n"
            + "OPERATOR " + OPERATOR_AND + "for logical \"AND\" operation (both conditions A AND B must match)\n"
            + "OPERATOR " + OPERATOR_OR + " for logical \"OR\" operation (either conditions A OR B must match)\n"
            + "You can also use parenthesis to group what search conditions to evaluate first.\n"
            + "Example 1 " + COMMAND_WORD + " " + PREFIX_NAME + "Programming " + OPERATOR_OR + " "
            + PREFIX_NAME + "Data\n"
            + "Example 2: " + COMMAND_WORD + " " + PREFIX_NAME + "Programming " + OPERATOR_AND + " "
            + PREFIX_NAME + "Methodology\n"
            + "Example 3: " + COMMAND_WORD + " " + PREFIX_NAME + "Programming " + OPERATOR_AND + " "
            + OPERATOR_LEFT_BRACKET + " " + PREFIX_CODE + "CS1231 " + OPERATOR_OR + " " + PREFIX_CODE + "CS1010 "
            + OPERATOR_RIGHT_BRACKET + " ";

    private final Predicate<Module> predicate;

    public FindCommand(Predicate<Module> predicate) {
        requireNonNull(predicate);

        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) {
        requireNonNull(model);

        model.updateFilteredModuleList(predicate);
        return new CommandResult(
                String.format(MESSAGE_MODULES_LISTED_OVERVIEW, model.getFilteredModuleList().size()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof FindCommand // instanceof handles nulls
                && predicate.equals(((FindCommand) other).predicate)); // state check
    }
}
