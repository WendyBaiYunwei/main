package pwe.planner.logic.parser.exceptions;

/**
 *  Boolean Parser exception when trying to create a predicate from keyword.
 */
public class BooleanParserPredicateException extends BooleanParserException {
    public BooleanParserPredicateException(String message) {
        super(message);
    }

    public BooleanParserPredicateException(String message, Throwable cause) {
        super(message, cause);
    }
}
