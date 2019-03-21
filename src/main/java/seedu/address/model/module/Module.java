package seedu.address.model.module;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.model.tag.Tag;

/**
 * Represents a Module in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Module {

    // Identity fields
    private final Code code;

    // Data fields
    private final Name name;
    private final Credits credits;
    private final Set<Tag> tags = new HashSet<>();
    private final Set<Code> corequisites = new HashSet<>();

    /**
     * Every field must be present and not null.
     */
    public Module(Name name, Credits credits, Code code, Set<Tag> tags, Set<Code> corequisites) {
        requireAllNonNull(name, credits, code, tags, corequisites);
        this.name = name;
        this.credits = credits;
        this.code = code;
        this.tags.addAll(tags);
        this.corequisites.addAll(corequisites);
    }

    public Name getName() {
        return name;
    }

    public Credits getCredits() {
        return credits;
    }

    public Code getCode() {
        return code;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns an immutable {@code Code} set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Code> getCorequisites() {
        return Collections.unmodifiableSet(corequisites);
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
        return otherModule.getName().equals(getName())
                && otherModule.getCredits().equals(getCredits())
                && otherModule.getCode().equals(getCode())
                && otherModule.getTags().equals(getTags())
                && otherModule.getCorequisites().equals(getCorequisites());
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, credits, code, tags, corequisites);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getName())
                .append(" Credits: ")
                .append(getCredits())
                .append(" Code: ")
                .append(getCode())
                .append(" Tags: ");

        if (getTags().isEmpty()) {
            builder.append("None");
        } else {
            getTags().forEach(builder::append);
        }

        builder.append(" Co-requisites: ");
        if (getCorequisites().isEmpty()) {
            builder.append("None");
        } else {
            builder.append(getCorequisites().stream().map(Code::toString).collect(Collectors.joining(", ")));
        }

        return builder.toString();
    }

}
