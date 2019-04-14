package pwe.planner.storage;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import pwe.planner.commons.exceptions.IllegalValueException;
import pwe.planner.model.module.Name;

/**
 * Jackson-friendly version of {@link Name}.
 */
public class JsonAdaptedName {

    private final String nameValue;

    /**
     * Constructs a {@link JsonAdaptedName} with the given {@code nameValue}.
     */
    @JsonCreator
    public JsonAdaptedName(String nameValue) {
        requireNonNull(nameValue);

        this.nameValue = nameValue;
    }

    /**
     * Converts a given {@link Name} into this class for Jackson use.
     */
    public JsonAdaptedName(Name source) {
        requireNonNull(source);

        nameValue = source.fullName;
    }

    @JsonValue
    public String getNameValue() {
        return nameValue;
    }

    /**
     * Converts this Jackson-friendly adapted name object into the model's {@link Name} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted name.
     */
    public Name toModelType() throws IllegalValueException {
        if (!Name.isValidName(nameValue)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(nameValue);
    }

}
