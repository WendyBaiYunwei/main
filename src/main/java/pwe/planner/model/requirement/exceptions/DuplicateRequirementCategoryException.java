package pwe.planner.model.requirement.exceptions;

/**
 * Signals that the operation will result in duplicate RequirementCategory (RequirementCategory are considered
 * duplicates if they have the same identity).
 */
public class DuplicateRequirementCategoryException extends RuntimeException {
    public DuplicateRequirementCategoryException() {
        super("Operation would result in duplicate requirement");
    }
}
