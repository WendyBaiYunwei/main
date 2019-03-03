package seedu.address.model.planner.exceptions;

/**
 * Signals that the operation will result in duplicate DegreePlanners (DegreePlanners are considered duplicates if
 * they have the same identity).
 */
public class DuplicateDegreePlannerException extends RuntimeException {
    public DuplicateDegreePlannerException() {
        super("Operation would result in duplicate degreePlanners");
    }
}
