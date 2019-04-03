package pwe.planner.testutil;

import java.util.HashSet;
import java.util.Set;

import pwe.planner.model.module.Code;
import pwe.planner.model.module.Credits;
import pwe.planner.model.module.Module;
import pwe.planner.model.module.Name;
import pwe.planner.model.tag.Tag;
import pwe.planner.model.util.SampleDataUtil;

/**
 * A utility class to help with building Module objects.
 */
public class ModuleBuilder {

    public static final String DEFAULT_NAME = "Alice Pauline";
    public static final String DEFAULT_CREDITS = "666";
    public static final String DEFAULT_CODE = "ABC1234Z";

    private Name name;
    private Credits credits;
    private Code code;
    private Set<Tag> tags;
    private Set<Code> corequisites;

    public ModuleBuilder() {
        name = new Name(DEFAULT_NAME);
        credits = new Credits(DEFAULT_CREDITS);
        code = new Code(DEFAULT_CODE);
        tags = new HashSet<>();
        corequisites = new HashSet<>();
    }

    /**
     * Initializes the ModuleBuilder with the data of {@code moduleToCopy}.
     */
    public ModuleBuilder(Module moduleToCopy) {
        name = moduleToCopy.getName();
        credits = moduleToCopy.getCredits();
        code = moduleToCopy.getCode();
        tags = new HashSet<>(moduleToCopy.getTags());
        corequisites = new HashSet<>(moduleToCopy.getCorequisites());
    }

    /**
     * Sets the {@code Name} of the {@code Module} that we are building.
     */
    public ModuleBuilder withName(String name) {
        this.name = new Name(name);
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and set it to the {@code Module} that we are building.
     */
    public ModuleBuilder withTags(String... tags) {
        this.tags = SampleDataUtil.getTagSet(tags);
        return this;
    }

    /**
     * Parses the {@code corequisites} into a {@code Set<Code>} and set it to the {@code Module} that we are building.
     */
    public ModuleBuilder withCorequisites(String... corequisites) {
        this.corequisites = SampleDataUtil.getCodeSet(corequisites);
        return this;
    }

    /**
     * Sets the {@code corequisites} of the {@code Module} that we are building.
     */
    public ModuleBuilder withCorequisites(Set<Code> corequisites) {
        this.corequisites = new HashSet<>(corequisites);
        return this;
    }

    /**
     * Sets the {@code Code} of the {@code Module} that we are building.
     */
    public ModuleBuilder withCode(String code) {
        this.code = new Code(code);
        return this;
    }

    /**
     * Sets the {@code Credits} of the {@code Module} that we are building.
     */
    public ModuleBuilder withCredits(String credits) {
        this.credits = new Credits(credits);
        return this;
    }

    public Module build() {
        return new Module(name, credits, code, tags, corequisites);
    }

}
