package pwe.planner.testutil;

import static pwe.planner.logic.parser.CliSyntax.PREFIX_CODE;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_COREQUISITE;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_CREDITS;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_NAME;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_SEMESTER;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Set;

import pwe.planner.logic.commands.AddCommand;
import pwe.planner.logic.commands.EditCommand.EditModuleDescriptor;
import pwe.planner.model.module.Code;
import pwe.planner.model.module.Module;
import pwe.planner.model.planner.Semester;
import pwe.planner.model.tag.Tag;

/**
 * A utility class for Module.
 */
public class ModuleUtil {

    /**
     * Returns an add command string for adding the {@code module}.
     */
    public static String getAddCommand(Module module) {
        return AddCommand.COMMAND_WORD + " " + getModuleDetails(module);
    }

    /**
     * Returns the part of command string for the given {@code module}'s details.
     */
    public static String getModuleDetails(Module module) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX_CODE + module.getCode().value + " ")
                .append(PREFIX_NAME + module.getName().fullName + " ")
                .append(PREFIX_CREDITS + module.getCredits().value + " ");
        module.getSemesters().stream().forEach(s -> sb.append(PREFIX_SEMESTER + s.plannerSemester + " "));
        module.getCorequisites().stream().forEach(s -> sb.append(PREFIX_COREQUISITE + s.value + " "));
        module.getTags().stream().forEach(s -> sb.append(PREFIX_TAG + s.tagName + " "));
        return sb.toString();
    }

    /**
     * Returns the part of command string for the given {@code EditModuleDescriptor}'s details.
     */
    public static String getEditModuleDescriptorDetails(EditModuleDescriptor descriptor) {
        StringBuilder sb = new StringBuilder();
        descriptor.getCode().ifPresent(code -> sb.append(PREFIX_CODE).append(code.value).append(' '));
        descriptor.getName().ifPresent(name -> sb.append(PREFIX_NAME).append(name.fullName).append(' '));
        descriptor.getCredits().ifPresent(credits -> sb.append(PREFIX_CREDITS).append(credits.value).append(' '));
        if (descriptor.getSemesters().isPresent()) {
            Set<Semester> semesters = descriptor.getSemesters().get();
            if (semesters.isEmpty()) {
                sb.append(PREFIX_SEMESTER).append(' ');
            } else {
                semesters.forEach(s -> sb.append(PREFIX_SEMESTER).append(s.plannerSemester).append(' '));
            }
        }
        if (descriptor.getCorequisites().isPresent()) {
            Set<Code> corequisites = descriptor.getCorequisites().get();
            if (corequisites.isEmpty()) {
                sb.append(PREFIX_COREQUISITE).append(' ');
            } else {
                corequisites.forEach(s -> sb.append(PREFIX_COREQUISITE).append(s.value).append(' '));
            }
        }
        if (descriptor.getTags().isPresent()) {
            Set<Tag> tags = descriptor.getTags().get();
            if (tags.isEmpty()) {
                sb.append(PREFIX_TAG).append(' ');
            } else {
                tags.forEach(s -> sb.append(PREFIX_TAG).append(s.tagName).append(' '));
            }
        }
        return sb.toString();
    }
}
