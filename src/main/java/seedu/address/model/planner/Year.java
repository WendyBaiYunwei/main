package seedu.address.model.planner;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a DegreePlanner's Year in the degreePlanner list.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Year {

    public static final String MESSAGE_YEAR_CONSTRAINTS =
            "Year should be an integer greater than 0 and smaller than 5.";

    /*
     * The date should be valid and in the correct Year format.
     */
    public static final String YEAR_VALIDATION_REGEX =
            "[1-4]{1}";


    public final String year;

    /**
     * Constructs a {@code Year}.
     *
     * @param year A valid year.
     */
    public Year(String year) {
        requireNonNull(year);
        checkArgument(isValidYear(year), MESSAGE_YEAR_CONSTRAINTS);
        this.year = year;
    }

    /**
     * Returns true if a given string is a valid year.
     */
    public static boolean isValidYear(String test) {
        return test.matches(YEAR_VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return year;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Year // instanceof handles nulls
                && year.equals(((Year) other).year)); // state check
    }

    @Override
    public int hashCode() {
        return year.hashCode();
    }
}
