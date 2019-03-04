package seedu.address.model.planner;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;

import seedu.address.model.module.Code;

public class DegreePlannerModule {

    // Identity fields
    private final Year year;
    private final Semester semester;
    private final Code code;

    /**
     * Every field must be present and not null.
     */
    public DegreePlannerModule(Code code, Year year, Semester semester) {
        requireAllNonNull(year, semester, code);
        this.year = year;
        this.semester = semester;
        this.code = code;
    }

    public Code getCode() {
        return code;
    }

    public Year getYear() {
        return year;
    }

    public Semester getSemester() {
        return semester;
    }


    /**
     * Returns true if both modules of the same name have at least one other identity field that is the same.
     * This defines a weaker notion of equality between two modules.
     */
    public boolean isSamePlannerModule(DegreePlannerModule otherModule) {
        if (otherModule == this) {
            return true;
        }

        return otherModule != null
                && otherModule.getCode().equals(getCode())
                && otherModule.getYear().equals(getYear())
                && otherModule.getSemester().equals(getSemester());
    }


    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(code, year, semester);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(" Code: ")
                .append(getCode())
                .append(" Year: ")
                .append(getYear())
                .append(" Semester: ")
                .append(getSemester());
        return builder.toString();
    }

}
