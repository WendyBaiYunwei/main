package pwe.planner.logic.parser.exceptions;

import pwe.planner.commons.exceptions.IllegalValueException;

/**
 * Represent a boolean parser exceptions when handling the logic.
 */
public class BooleanParserException extends IllegalValueException {
    public BooleanParserException(String message) {
        super(message);
    }

    public BooleanParserException(String message, Throwable cause) {
        super(message, cause);
    }
}
