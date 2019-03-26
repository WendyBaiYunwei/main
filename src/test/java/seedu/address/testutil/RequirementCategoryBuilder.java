package seedu.address.testutil;

import java.util.HashSet;
import java.util.Set;

import seedu.address.model.module.Code;
import seedu.address.model.module.Credits;
import seedu.address.model.module.Name;
import seedu.address.model.requirement.RequirementCategory;
import seedu.address.model.util.SampleDataUtil;

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
