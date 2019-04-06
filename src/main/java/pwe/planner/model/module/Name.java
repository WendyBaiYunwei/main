package pwe.planner.model.module;

import static java.util.Objects.requireNonNull;
import static pwe.planner.commons.util.AppUtil.checkArgument;

/**
 * Represents a Module's name in the application.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}
 */
public class Name {

    public static final String MESSAGE_CONSTRAINTS =
            "Names should only contain alphanumeric characters, punctuations (excluding \"(\", \")\", \"&\", \"|\") "
            + "and spaces, and it should not be blank.\n"
            + "If you are using punctuations, perhaps you may want to consider replacing \"()\" with \"[]\", \"&\" with"
            + " \"and\", and \"|\" with \"l\" (lowercase L) instead!";

    /*
     * The first character of the name must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String VALIDATION_REGEX = "^[\\p{Graph}&&[^\\(\\)\\|\\&]][\\p{Print}&&[^\\(\\)\\|\\&]]*$";
    public final String fullName;

    /**
     * Constructs a {@code Name}.
     *
     * @param name A valid name.
     */
    public Name(String name) {
        requireNonNull(name);
        checkArgument(isValidName(name), MESSAGE_CONSTRAINTS);

        fullName = name;
    }

    /**
     * Returns true if a given string is a valid name.
     */
    public static boolean isValidName(String test) {
        requireNonNull(test);

        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Name // instanceof handles nulls
                && fullName.equals(((Name) other).fullName)); // state check
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }

}
