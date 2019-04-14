package pwe.planner.testutil;

import static pwe.planner.logic.parser.CliSyntax.PREFIX_CODE;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_NAME;

import java.util.Set;

import pwe.planner.logic.commands.RequirementAddCommand;
import pwe.planner.logic.commands.RequirementRemoveCommand;
import pwe.planner.model.module.Code;
import pwe.planner.model.module.Name;

/**
 * A utility class for RequirementCategory.
 */
public class RequirementUtil {

    /**
     * Returns an remove command string for removing the {@code code}.
     */
    public static String getRequirementRemoveCommand(Name requirementCategoryName, Set<Code> codeSet) {
        StringBuilder sb = new StringBuilder();
        sb.append(RequirementRemoveCommand.COMMAND_WORD).append(" ");
        sb.append(PREFIX_NAME).append(requirementCategoryName.toString()).append(" ");
        codeSet.stream().forEach(s -> sb.append(PREFIX_CODE).append(s.value).append(" "));
        return sb.toString();
    }

    /**
     * Returns an add command string for adding the {@code code}.
     */
    public static String getRequirementAddCommand(Name requirementCategoryName, Set<Code> codeSet) {
        StringBuilder sb = new StringBuilder();
        sb.append(RequirementAddCommand.COMMAND_WORD).append(" ");
        sb.append(PREFIX_NAME).append(requirementCategoryName.toString()).append(" ");
        codeSet.stream().forEach(s -> sb.append(PREFIX_CODE).append(s.value).append(" "));
        return sb.toString();
    }

}
