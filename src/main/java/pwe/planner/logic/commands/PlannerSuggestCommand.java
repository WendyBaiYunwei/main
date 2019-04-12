package pwe.planner.logic.commands;

import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.util.Objects.requireNonNull;
import static pwe.planner.commons.util.CollectionUtil.requireAllNonNull;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_CREDITS;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import pwe.planner.logic.CommandHistory;
import pwe.planner.logic.commands.exceptions.CommandException;
import pwe.planner.model.Model;
import pwe.planner.model.module.Code;
import pwe.planner.model.module.Credits;
import pwe.planner.model.module.Module;
import pwe.planner.model.planner.DegreePlanner;
import pwe.planner.model.planner.ModuleToSuggest;
import pwe.planner.model.planner.SortModulesToSuggest;
import pwe.planner.model.tag.Tag;

/**
 * Suggests module(s) to take.
 */
public class PlannerSuggestCommand extends Command {

    public static final String COMMAND_WORD = "planner_suggest";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Suggests module(s) to take. "
            + "Parameters: "
            + PREFIX_CREDITS + "CREDITS "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_CREDITS + "2 "
            + PREFIX_TAG + "algorithms "
            + PREFIX_TAG + "c";

    public static final String MESSAGE_SUCCESS = "The list is sorted with the more recommended module(s)"
            + " in front.\nModule(s) recommended:\n%1$s\nModule(s) with relevant"
            + " tags:\n%2$s\nModule(s) with matching credits:\n%3$s";
    private static final int MAX_NUMBER_OF_ELEMENETS = 10;

    private Credits creditsToFind;
    private Set<Tag> tagsToFind;

    /**
     * Creates a PlannerSuggestCommand to suggest {@code codes} to take.
     */
    public PlannerSuggestCommand(Credits bestCredits, Set<Tag> tags) {
        requireAllNonNull(bestCredits, tags);

        creditsToFind = bestCredits;
        tagsToFind = tags;
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {
        requireNonNull(model);

        Set<Code> plannerCodes = new HashSet<>();
        model.getApplication().getDegreePlannerList()
                .stream().map(DegreePlanner::getCodes).forEach(plannerCodes::addAll);

        ObservableList<Module> moduleList = model.getApplication().getModuleList();
        List<ModuleToSuggest> modulesToSuggest = new ArrayList<>();
        List<ModuleToSuggest> modulesWithMatchingTags = new ArrayList<>();
        List<ModuleToSuggest> modulesWithMatchingCredits = new ArrayList<>();
        for (Module module : moduleList) {
            // finds the matching tags for each module
            Set<Tag> matchingTags = new HashSet<>(tagsToFind);
            matchingTags.retainAll(module.getTags());

            // finds the creditDifference
            int credit = Integer.valueOf((module.getCredits().toString()));
            int bestCredits = Integer.valueOf(creditsToFind.toString());
            int creditDifference = abs(credit - bestCredits);

            ModuleToSuggest moduleToSuggest =
                    new ModuleToSuggest(creditDifference, matchingTags.size(), module.getCode());
            modulesToSuggest.add(moduleToSuggest);

            if (!matchingTags.isEmpty()) {
                modulesWithMatchingTags.add(moduleToSuggest);
            }

            if (creditDifference == 0) {
                modulesWithMatchingCredits.add(moduleToSuggest);
            }
        }

        modulesToSuggest.sort(new SortModulesToSuggest());
        modulesWithMatchingTags.sort(new SortModulesToSuggest());

        //Returns codes to suggest based on both credits and tags.
        List<Code> codesToSuggest = new ArrayList<>();
        for (ModuleToSuggest moduleToSuggest : modulesToSuggest) {
            codesToSuggest.add(moduleToSuggest.getModuleCode());
        }
        codesToSuggest.removeAll(plannerCodes);
        List<Code> truncatedList = codesToSuggest.subList(0, min(codesToSuggest.size(), MAX_NUMBER_OF_ELEMENETS));

        //Returns codes with matching tags.
        List<Code> codesWithMatchingTags = modulesWithMatchingTags.stream()
                .map(ModuleToSuggest::getModuleCode).collect(Collectors.toList());
        codesWithMatchingTags.removeAll(plannerCodes);

        //Returns codes with matching credits.
        List<Code> codesWithMatchingCredits = new ArrayList<>();
        for (ModuleToSuggest moduleWithMatchingCredits : modulesWithMatchingCredits) {
            codesWithMatchingCredits.add(moduleWithMatchingCredits.getModuleCode());
        }
        codesWithMatchingCredits.removeAll(plannerCodes);

        return new CommandResult(String.format(MESSAGE_SUCCESS, truncatedList.isEmpty() ? "None" : truncatedList,
                codesWithMatchingTags.isEmpty() ? "None" : codesWithMatchingTags,
                codesWithMatchingCredits.isEmpty() ? "None" : codesWithMatchingCredits));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof PlannerSuggestCommand // instanceof handles nulls
                && tagsToFind.equals(((PlannerSuggestCommand) other).tagsToFind)
                && creditsToFind.equals(((PlannerSuggestCommand) other).creditsToFind));
    }

}
