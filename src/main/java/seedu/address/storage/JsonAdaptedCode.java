package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import seedu.address.commons.exceptions.IllegalValueException;

import seedu.address.model.module.Code;

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
        this.codeValue = codeValue;
    }

    /**
     * Converts a given {@code Tag} into this class for Jackson use.
     */
    public JsonAdaptedCode(Code source) {
        codeValue = source.value;
    }

    @JsonValue
    public String getCodeValue() {
        return codeValue;
    }

    /**
     * Converts this Jackson-friendly adapted tag object into the model's {@code Tag} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted tag.
     */
    public Code toModelType() throws IllegalValueException {
        if (!Code.isValidCode(codeValue)) {
            throw new IllegalValueException(Code.MESSAGE_CONSTRAINTS);
        }
        return new Code(codeValue);
    }

}
