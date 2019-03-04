package seedu.address.model.requirement;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import seedu.address.model.module.Code;
import seedu.address.model.module.Credits;
import seedu.address.model.module.Name;

/**
 * Represents a RequirementCategory in the requirement list.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class RequirementCategory {

    // Identity fields
    private final Name name;

    // Data fields
    private final Credits credits;
    private final Set<Code> codeList = new HashSet<>();

    /**
     * Every field must be present and not null.
     */
    public RequirementCategory(Name name, Credits credits, Set<Code> codeList) {
        requireAllNonNull(name, credits, codeList);
        this.name = name;
        this.credits = credits;
        this.codeList.addAll(codeList);
    }

    public Name getName() {
        return name;
    }

    public Credits getCredits() {
        return credits;
    }

    /**
     * Returns an immutable codeList set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Code> getCodeList() {
        return Collections.unmodifiableSet(codeList);
    }

    /**
     * Returns true if both requirement in PWE have same code.
     * This defines a weaker notion of equality between two requirement.
     */
    public boolean isSameRequirementCategory(RequirementCategory otherRequirementCategory) {
        if (otherRequirementCategory == this) {
            return true;
        }

        return otherRequirementCategory != null
                && otherRequirementCategory.getName().equals(getName());
    }

    /**
     * Returns true if both requirement in PWE have the same identity and data fields.
     * This defines a stronger notion of equality between two requirement.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof RequirementCategory)) {
            return false;
        }

        RequirementCategory otherRequirementCategory = (RequirementCategory) other;
        return otherRequirementCategory.getName().equals(getName())
                && otherRequirementCategory.getCredits().equals(getCredits())
                && otherRequirementCategory.getCodeList().equals(getCodeList());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, credits, codeList);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(" Name: ")
                .append(getName())
                .append(" Credits: ")
                .append(getCredits())
                .append(" Module Codes: ")
                .append(getCodeList());
        return builder.toString();
    }

}
