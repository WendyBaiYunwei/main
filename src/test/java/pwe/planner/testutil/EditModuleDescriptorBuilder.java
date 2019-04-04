package pwe.planner.testutil;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import pwe.planner.logic.commands.EditCommand;
import pwe.planner.logic.commands.EditCommand.EditModuleDescriptor;
import pwe.planner.model.module.Code;
import pwe.planner.model.module.Credits;
import pwe.planner.model.module.Module;
import pwe.planner.model.module.Name;
import pwe.planner.model.tag.Tag;

/**
 * A utility class to help with building EditModuleDescriptor objects.
 */
public class EditModuleDescriptorBuilder {

    private EditCommand.EditModuleDescriptor descriptor;

    public EditModuleDescriptorBuilder() {
        descriptor = new EditCommand.EditModuleDescriptor();
    }

    public EditModuleDescriptorBuilder(EditCommand.EditModuleDescriptor descriptor) {
        this.descriptor = new EditModuleDescriptor(descriptor);
    }

    /**
     * Returns an {@code EditModuleDescriptor} with fields containing {@code module}'s details
     */
    public EditModuleDescriptorBuilder(Module module) {
        descriptor = new EditCommand.EditModuleDescriptor();
        descriptor.setName(module.getName());
        descriptor.setCredits(module.getCredits());
        descriptor.setCode(module.getCode());
        descriptor.setTags(module.getTags());
    }

    /**
     * Sets the {@code Name} of the {@code EditModuleDescriptor} that we are building.
     */
    public EditModuleDescriptorBuilder withName(String name) {
        descriptor.setName(new Name(name));
        return this;
    }

    /**
     * Sets the {@code Credits} of the {@code EditModuleDescriptor} that we are building.
     */
    public EditModuleDescriptorBuilder withCredits(String credits) {
        descriptor.setCredits(new Credits(credits));
        return this;
    }

    /**
     * Sets the {@code Code} of the {@code EditModuleDescriptor} that we are building.
     */
    public EditModuleDescriptorBuilder withCode(String code) {
        descriptor.setCode(new Code(code));
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and set it to the {@code EditModuleDescriptor}
     * that we are building.
     */
    public EditModuleDescriptorBuilder withTags(String... tags) {
        Set<Tag> tagSet = Stream.of(tags).map(Tag::new).collect(Collectors.toSet());
        descriptor.setTags(tagSet);
        return this;
    }

    /**
     * Parses the {@code corequisites} into a {@code Set<Code>} and set it to the {@code EditModuleDescriptor}
     * that we are building.
     */
    public EditModuleDescriptorBuilder withCorequisites(String... corequisites) {
        Set<Code> corequisitesSet = Stream.of(corequisites).map(Code::new).collect(Collectors.toSet());
        descriptor.setCorequisites(corequisitesSet);
        return this;
    }

    public EditCommand.EditModuleDescriptor build() {
        return descriptor;
    }
}
