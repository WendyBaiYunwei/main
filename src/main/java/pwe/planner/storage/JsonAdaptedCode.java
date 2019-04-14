package pwe.planner.storage;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import pwe.planner.commons.exceptions.IllegalValueException;
import pwe.planner.model.module.Code;

/**
 * Jackson-friendly version of {@link Code}.
 */
public class JsonAdaptedCode {

    private final String codeValue;

    /**
     * Constructs a {@code JsonAdaptedCode} with the given {@code codeValue}.
     */
    @JsonCreator
    public JsonAdaptedCode(String codeValue) {
        requireNonNull(codeValue);

        this.codeValue = codeValue;
    }

    /**
     * Converts a given {@link Code} into this class for Jackson use.
     */
    public JsonAdaptedCode(Code source) {
        requireNonNull(source);

        codeValue = source.value;
    }

    @JsonValue
    public String getCodeValue() {
        return codeValue;
    }

    /**
     * Converts this Jackson-friendly adapted code object into the model's {@link Code} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted code.
     */
    public Code toModelType() throws IllegalValueException {
        if (!Code.isValidCode(codeValue)) {
            throw new IllegalValueException(Code.MESSAGE_CONSTRAINTS);
        }
        return new Code(codeValue);
    }

}
