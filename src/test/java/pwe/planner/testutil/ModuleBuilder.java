package pwe.planner.testutil;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import pwe.planner.model.module.Code;
import pwe.planner.model.module.Credits;
import pwe.planner.model.module.Module;
import pwe.planner.model.module.Name;
import pwe.planner.model.planner.Semester;
import pwe.planner.model.tag.Tag;
import pwe.planner.model.util.SampleDataUtil;

/**
 * A utility class to help with building Module objects.
 */
public class ModuleBuilder {

    public static final String DEFAULT_NAME = "Alice Pauline";
    public static final String DEFAULT_CREDITS = "666";
    public static final String DEFAULT_CODE = "ABC1234Z";
    public static final String DEFAULT_SEMESTERS_ONE = "1";
    public static final String DEFAULT_SEMESTERS_TWO = "2";

    private Code code;
    private Name name;
    private Credits credits;
    private Set<Code> corequisites;
    private Set<Semester> semesters;
    private Set<Tag> tags;

    public ModuleBuilder() {
        code = new Code(DEFAULT_CODE);
        name = new Name(DEFAULT_NAME);
        credits = new Credits(DEFAULT_CREDITS);
        semesters = new HashSet<>();
        semesters.add(new Semester(DEFAULT_SEMESTERS_ONE));
        semesters.add(new Semester(DEFAULT_SEMESTERS_TWO));
        corequisites = new HashSet<>();
        tags = new HashSet<>();
    }

    /**
     * Initializes the ModuleBuilder with the data of {@code moduleToCopy}.
     */
    public ModuleBuilder(Module moduleToCopy) {
        code = moduleToCopy.getCode();
        name = moduleToCopy.getName();
        credits = moduleToCopy.getCredits();
        semesters = new HashSet<>(moduleToCopy.getSemesters());
        corequisites = new HashSet<>(moduleToCopy.getCorequisites());
        tags = new HashSet<>(moduleToCopy.getTags());
    }

    /**
     * Sets the {@code Code} of the {@code Module} that we are building.
     */
    public ModuleBuilder withCode(String code) {
        this.code = new Code(code);
        return this;
    }

    /**
     * Sets the {@code Name} of the {@code Module} that we are building.
     */
    public ModuleBuilder withName(String name) {
        this.name = new Name(name);
        return this;
    }

    /**
     * Sets the {@code Credits} of the {@code Module} that we are building.
     */
    public ModuleBuilder withCredits(String credits) {
        this.credits = new Credits(credits);
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
     * Parses the {@code semesters} into a {@code Set<Semester>} and set it to the {@code Module} that we are building.
     */
    public ModuleBuilder withSemesters(String... semesters) {
        requireNonNull(semesters);

        this.semesters = Arrays.stream(semesters).map(Semester::new).collect(Collectors.toSet());
        return this;
    }

    /**
     * Sets the {@code semesters} of the {@code Module} that we are building.
     */
    public ModuleBuilder withSemesters(Set<Semester> semesters) {
        this.semesters = new HashSet<>(semesters);
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and set it to the {@code Module} that we are building.
     */
    public ModuleBuilder withTags(String... tags) {
        this.tags = SampleDataUtil.getTagSet(tags);
        return this;
    }

    public Module build() {
        return new Module(code, name, credits, semesters, corequisites, tags);
    }

}
