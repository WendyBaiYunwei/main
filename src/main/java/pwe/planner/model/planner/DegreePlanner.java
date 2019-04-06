package pwe.planner.model.planner;

import static pwe.planner.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import pwe.planner.model.module.Code;

/**
 * Represents a DegreePlanner in the degreePlanner list.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class DegreePlanner implements Comparable<DegreePlanner> {
    /**
     * The format string representation of a {@link DegreePlanner} object used by {@link DegreePlanner#toString()}.
     */
    private static final String STRING_REPRESENTATION = "Year %1$s Semester %2$s: %3$s";

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
        final String allCodes = codes.isEmpty()
                ? "No modules"
                : codes.stream().sorted().map(Code::toString).collect(Collectors.joining(", "));

        return String.format(STRING_REPRESENTATION, year, semester, allCodes);
    }

    @Override
    public int compareTo(DegreePlanner other) {
        int differenceInYear = year.compareTo(other.year);
        if (differenceInYear == 0) {
            return semester.compareTo(other.semester);
        }
        return differenceInYear;
    }
}
