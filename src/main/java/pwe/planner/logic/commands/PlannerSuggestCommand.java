package pwe.planner.logic.commands;

import static java.lang.Math.abs;
import static java.util.Objects.requireNonNull;
import static pwe.planner.commons.util.CollectionUtil.requireAllNonNull;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_CREDITS;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
            + PREFIX_TAG + "TAG "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_CREDITS + "2 "
            + PREFIX_TAG + "algorithms "
            + PREFIX_TAG + "c ";

    public static final String MESSAGE_SUCCESS = "The list is sorted with the more recommended module(s)"
            + "at the top.\nModule(s) must take:\n%1$s\nModule(s) recommended:\n%2$s";
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
        Set<Code> plannerCoreqs = new HashSet<>();
        plannerCodes.stream().map(model::getModuleByCode).map(Module::getCorequisites).forEach(plannerCoreqs::addAll);
        Set<Code> missingPlannerCoreqs = new HashSet<>(plannerCoreqs);
        // Returns the Co-requisite(s) that already exists in the degree plan.
        plannerCoreqs.retainAll(plannerCodes);
        // Returns the relevant Co-requisite(s) that do not exists in the degree plan.
        missingPlannerCoreqs.removeAll(plannerCoreqs);
        ObservableList<Module> moduleList = model.getApplication().getModuleList();

        List<ModuleToSuggest> modulesToSuggest = new ArrayList<ModuleToSuggest>();
        for (Module module : moduleList) {
            // finds the matching tags for each module
            Set<Tag> matchingTags = new HashSet<>(tagsToFind);
            matchingTags.retainAll(module.getTags());

            // finds the creditDifference
            int credit = Integer.valueOf((module.getCredits().toString()));
            int bestCredits = Integer.valueOf(creditsToFind.toString());
            int creditDifference = abs(credit - bestCredits);

            if (matchingTags.size() > 0) {
                ModuleToSuggest moduleToSuggest =
                        new ModuleToSuggest(creditDifference, matchingTags.size(), module.getCode());
                modulesToSuggest.add(moduleToSuggest);
            }
        }
        modulesToSuggest.removeAll(plannerCodes);

        modulesToSuggest.sort(new SortModulesToSuggest());
        List<Code> codesToSuggest = new ArrayList<>();
        for (ModuleToSuggest moduleToSuggest : modulesToSuggest) {
            codesToSuggest.add(moduleToSuggest.getModuleCode());
        }

        model.commitApplication();
        if (missingPlannerCoreqs.size() == 0 && codesToSuggest.size() == 0) {
            return new CommandResult(String.format(MESSAGE_SUCCESS, "None ", "None"));
        } else if (missingPlannerCoreqs.size() == 0) {
            return new CommandResult(String.format(MESSAGE_SUCCESS, "None ", codesToSuggest));
        } else if (codesToSuggest.size() == 0) {
            return new CommandResult(String.format(MESSAGE_SUCCESS, missingPlannerCoreqs, " None"));
        } else {
            return new CommandResult(String.format(MESSAGE_SUCCESS, missingPlannerCoreqs, codesToSuggest));
        }
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof PlannerSuggestCommand // instanceof handles nulls
                && tagsToFind.equals(((PlannerSuggestCommand) other).tagsToFind)
                && creditsToFind.equals(((PlannerSuggestCommand) other).creditsToFind));
    }
}

