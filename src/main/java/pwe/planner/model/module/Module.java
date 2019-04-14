package pwe.planner.model.module;

import static pwe.planner.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import pwe.planner.commons.util.StringUtil;
import pwe.planner.model.planner.Semester;
import pwe.planner.model.tag.Tag;

/**
 * Represents a Module in the application.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Module {
    /**
     * The format string representation of a {@link Module} object used by {@link Module#toString()}.
     */
    private static final String STRING_REPRESENTATION = "%1$s %2$s (%3$s Modular Credits)\n"
            + "Offered in Semesters: %4$s\n"
            + "Co-requisites: %5$s\n"
            + "Tags: %6$s";

    // Identity fields
    private final Code code;

    // Data fields
    private final Name name;
    private final Credits credits;
    private final Set<Tag> tags = new HashSet<>();
    private final Set<Code> corequisites = new HashSet<>();
    private final Set<Semester> semesters = new HashSet<>();

    /**
     * Every field must be present and not null.
     */
    public Module(Code code, Name name, Credits credits, Set<Semester> semesters, Set<Code> corequisites,
            Set<Tag> tags) {
        requireAllNonNull(code, name, credits, semesters, corequisites, tags);

        this.code = code;
        this.name = name;
        this.credits = credits;
        this.semesters.addAll(semesters);
        this.corequisites.addAll(corequisites);
        this.tags.addAll(tags);
    }

    public Code getCode() {
        return code;
    }

    public Name getName() {
        return name;
    }

    public Credits getCredits() {
        return credits;
    }

    /**
     * Returns an immutable {@code Semester} set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Semester> getSemesters() {
        return Collections.unmodifiableSet(semesters);
    }

    /**
     * Returns an immutable {@code Code} set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Code> getCorequisites() {
        return Collections.unmodifiableSet(corequisites);
    }

    /**
     * Returns an immutable {@code Tag} set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns true if both modules of the same name have at least one other identity field that is the same.
     * This defines a weaker notion of equality between two modules.
     */
    public boolean isSameModule(Module otherModule) {
        if (otherModule == this) {
            return true;
        }

        return otherModule != null && otherModule.getCode().equals(getCode());
    }

    /**
     * Returns true if both modules have the same identity and data fields.
     * This defines a stronger notion of equality between two modules.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Module)) {
            return false;
        }

        Module otherModule = (Module) other;
        return otherModule.getCode().equals(getCode())
                && otherModule.getName().equals(getName())
                && otherModule.getCredits().equals(getCredits())
                && otherModule.getCorequisites().equals(getCorequisites())
                && otherModule.getSemesters().equals(getSemesters())
                && otherModule.getTags().equals(getTags());
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, name, credits, semesters, corequisites, tags);
    }

    @Override
    public String toString() {

        final String allSemesters = StringUtil.joinStreamAsString(semesters.stream().sorted());

        final String allCorequisites = StringUtil.joinStreamAsString(corequisites.stream().sorted());

        final String allTags = StringUtil.joinStreamAsString(tags.stream().sorted());

        return String.format(STRING_REPRESENTATION, code, name, credits, allSemesters, allCorequisites, allTags);
    }
}
