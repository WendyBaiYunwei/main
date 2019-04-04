package pwe.planner.testutil;

import java.util.HashSet;
import java.util.Set;

import pwe.planner.model.module.Code;
import pwe.planner.model.module.Credits;
import pwe.planner.model.module.Name;
import pwe.planner.model.requirement.RequirementCategory;
import pwe.planner.model.util.SampleDataUtil;

/**
 * A utility class to help with building RequirementCategory objects.
 */
public class RequirementCategoryBuilder {

    public static final String DEFAULT_NAME = "Computing Foundation";
    public static final String DEFAULT_CREDITS = "36";

    private Name name;
    private Credits credits;
    private Set<Code> codeSet;

    public RequirementCategoryBuilder() {
        name = new Name(DEFAULT_NAME);
        credits = new Credits(DEFAULT_CREDITS);
        codeSet = new HashSet<>();
    }

    /**
     * Initializes the RequirementCategoryBuilder with the data of {@code requirementCategoryToCopy}.
     */
    public RequirementCategoryBuilder(RequirementCategory requirementCategoryToCopy) {
        name = requirementCategoryToCopy.getName();
        credits = requirementCategoryToCopy.getCredits();
        codeSet = new HashSet<>(requirementCategoryToCopy.getCodeSet());
    }

    /**
     * Sets the {@code Name} of the {@code RequirementCategory} that we are building.
     */
    public RequirementCategoryBuilder withName(String name) {
        this.name = new Name(name);
        return this;
    }

    /**
     * Parses the {@code codes} into a {@code Set<Code>} and set it to the {@code RequirementCategory} that we are
     * building.
     */
    public RequirementCategoryBuilder withCodes(String... codes) {
        this.codeSet = SampleDataUtil.getCodeSet(codes);
        return this;
    }

    /**
     * Sets the {@code Credits} of the {@code RequirementCategory} that we are building.
     */
    public RequirementCategoryBuilder withCredits(String credits) {
        this.credits = new Credits(credits);
        return this;
    }

    public RequirementCategory build() {
        return new RequirementCategory(name, credits, codeSet);
    }

}
