package seedu.address.model.planner;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import seedu.address.model.module.Code;

/**
 * Represents a DegreePlanner in the degreePlanner list.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class DegreePlanner {

    // Identity fields
    private final Year year;
    private final Semester semester;

    // Data fields
    private final Set<Code> codes = new HashSet<>();

    /**
     * Every field must be present and not null.
     */
    public DegreePlanner(Year year, Semester semester, Set<Code> codes) {
        requireAllNonNull(year, semester, codes);
        this.year = year;
        this.semester = semester;
        this.codes.addAll(codes);
    }

    public Set<Code> getCodes() {
        return Collections.unmodifiableSet(codes);
    }

    public Year getYear() {
        return year;
    }

    public Semester getSemester() {
        return semester;
    }

    /**
     * Returns true if both degreePlanners in planner have same year and semester.
     * This defines a weaker notion of equality between two degreePlaners.
     */
    public boolean isSameDegreePlanner(DegreePlanner otherDegreePlanner) {
        if (otherDegreePlanner == this) {
            return true;
        }

        return otherDegreePlanner != null
                && otherDegreePlanner.getYear().equals(getYear())
                && otherDegreePlanner.getSemester().equals(getSemester());
    }

    /**
     * Returns true if both degreePlanners in planner have the same identity and data fields.
     * This defines a stronger notion of equality between two degreePlanners.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof DegreePlanner)) {
            return false;
        }

        DegreePlanner otherDegreePlanner = (DegreePlanner) other;
        return otherDegreePlanner.getYear().equals(getYear())
                && otherDegreePlanner.getSemester().equals(getSemester())
                && otherDegreePlanner.getCodes().equals(getCodes());
    }

    @Override
    public int hashCode() {
        return Objects.hash(codes, year, semester);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(" Year: ")
                .append(getYear())
                .append(" Semester: ")
                .append(getSemester())
                .append(" Codes: ")
                .append(getCodes());
        return builder.toString();
    }

    void addModuleCode(Code code){
        codes.add(code);
    }
}
