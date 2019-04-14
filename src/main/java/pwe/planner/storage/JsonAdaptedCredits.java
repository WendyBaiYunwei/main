package pwe.planner.storage;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import pwe.planner.commons.exceptions.IllegalValueException;
import pwe.planner.model.module.Credits;

/**
 * Jackson-friendly version of {@link Credits}.
 */
public class JsonAdaptedCredits {

    private final String creditsValue;

    /**
     * Constructs a {@code JsonAdaptedCredits} with the given {@code creditsValue}.
     */
    @JsonCreator
    public JsonAdaptedCredits(String creditsValue) {
        requireNonNull(creditsValue);

        this.creditsValue = creditsValue;
    }

    /**
     * Converts a given {@link Credits} into this class for Jackson use.
     */
    public JsonAdaptedCredits(Credits source) {
        requireNonNull(source);

        creditsValue = source.value;
    }

    @JsonValue
    public String getCreditsValue() {
        return creditsValue;
    }

    /**
     * Converts this Jackson-friendly adapted credits object into the model's {@link Credits} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted credits.
     */
    public Credits toModelType() throws IllegalValueException {
        if (!Credits.isValidCredits(creditsValue)) {
            throw new IllegalValueException(Credits.MESSAGE_CONSTRAINTS);
        }
        return new Credits(creditsValue);
    }

}
