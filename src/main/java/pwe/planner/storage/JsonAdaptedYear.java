package pwe.planner.storage;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import pwe.planner.commons.exceptions.IllegalValueException;
import pwe.planner.model.planner.Year;

/**
 * Jackson-friendly version of {@link Year}.
 */
public class JsonAdaptedYear {

    private final String yearValue;

    /**
     * Constructs a {@link JsonAdaptedYear} with the given {@code yearValue}.
     */
    @JsonCreator
    public JsonAdaptedYear(String yearValue) {
        requireNonNull(yearValue);

        this.yearValue = yearValue;
    }

    /**
     * Converts a given {@link Year} into this class for Jackson use.
     */
    public JsonAdaptedYear(Year source) {
        requireNonNull(source);

        yearValue = source.year;
    }

    @JsonValue
    public String getYearValue() {
        return yearValue;
    }

    /**
     * Converts this Jackson-friendly adapted year object into the model's {@link Year} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted year.
     */
    public Year toModelType() throws IllegalValueException {
        if (!Year.isValidYear(yearValue)) {
            throw new IllegalValueException(Year.MESSAGE_YEAR_CONSTRAINTS);
        }
        return new Year(yearValue);
    }

}
