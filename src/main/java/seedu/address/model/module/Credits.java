package seedu.address.model.module;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Module's credits in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidCredits(String)}
 */
public class Credits {

    public static final String MESSAGE_CONSTRAINTS =
            "Credits should only contain numbers between 0 and 999.";
    public static final String VALIDATION_REGEX = "0|([\\d&&[^0]]{1}[\\d]{0,2})";
    public final String value;

    /**
     * Constructs a {@code Credits}.
     *
     * @param credits A valid amount of credits.
     */
    public Credits(String credits) {
        requireNonNull(credits);
        checkArgument(isValidCredits(credits), MESSAGE_CONSTRAINTS);
        value = credits;
    }

    /**
     * Returns true if a given string is a valid amount of credits.
     */
    public static boolean isValidCredits(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Credits // instanceof handles nulls
                && value.equals(((Credits) other).value)); // state check
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
