package seedu.address.model.planner;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a DegreePlanner's Semester in the degreePlanner list.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Semester {

    public static final String MESSAGE_SEMESTER_CONSTRAINTS =
            "Semester should be greater than 0 and less than 5.";

    /*
     * The semester should be valid and in the correct S format.
     */
    public static final String SEMESTER_VALIDATION_REGEX =
            "[1-4]{1}";

    public final String plannerSemester;

    /**
     * Constructs a {@code Semester}.
     *
     * @param semester A valid semester.
     */
    public Semester(String semester) {
        requireNonNull(semester);
        checkArgument(isValidSemester(semester), MESSAGE_SEMESTER_CONSTRAINTS);
        plannerSemester = semester;
    }

    /**
     * Returns true if a given string is a valid date.
     */
    public static boolean isValidSemester(String test) {
        return test.matches(SEMESTER_VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return plannerSemester;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Semester // instanceof handles nulls
                && plannerSemester.equals(((Semester) other).plannerSemester)); // state check
    }

    @Override
    public int hashCode() {
        return plannerSemester.hashCode();
    }
}
