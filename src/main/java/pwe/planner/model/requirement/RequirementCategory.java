package pwe.planner.model.requirement;

import static java.util.Objects.requireNonNull;
import static pwe.planner.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import pwe.planner.model.module.Code;
import pwe.planner.model.module.Credits;
import pwe.planner.model.module.Name;

/**
 * Represents a RequirementCategory in the requirement list.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class RequirementCategory {

    // Identity fields
    private final Name name;

    // Data fields
    private final Credits credits;
    private final Set<Code> codeSet = new HashSet<>();

    /**
     * Every field must be present and not null.
     */
    public RequirementCategory(Name name, Credits credits, Set<Code> codeSet) {
        requireAllNonNull(name, credits, codeSet);

        this.name = name;
        this.credits = credits;
        this.codeSet.addAll(codeSet);
    }

    public Name getName() {
        return name;
    }

    public Credits getCredits() {
        return credits;
    }

    /**
     * Returns an immutable setCode, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Code> getCodeSet() {
        return Collections.unmodifiableSet(codeSet);
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
     * Returns true if the current codeSet contains an equivalent code in the parameter toCheck
     */
    public boolean hasModuleCode(Set<Code> codeSetToCheck) {
        requireNonNull(codeSetToCheck);

        return codeSetToCheck.stream().anyMatch(codeSet::contains);
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
                && otherRequirementCategory.getCodeSet().equals(getCodeSet());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, credits, codeSet);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(" Name: ")
                .append(getName())
                .append(" Credits: ")
                .append(getCredits())
                .append(" Module Codes: ")
                .append(getCodeSet());
        return builder.toString();
    }

}
