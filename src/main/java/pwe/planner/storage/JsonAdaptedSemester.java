package pwe.planner.storage;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import pwe.planner.commons.exceptions.IllegalValueException;
import pwe.planner.model.planner.Semester;

/**
 * Jackson-friendly version of {@link Semester}.
 */
public class JsonAdaptedSemester {

    private final String semesterValue;

    /**
     * Constructs a {@link JsonAdaptedSemester} with the given {@code semesterValue}.
     */
    @JsonCreator
    public JsonAdaptedSemester(String semesterValue) {
        requireNonNull(semesterValue);

        this.semesterValue = semesterValue;
    }

    /**
     * Converts a given {@link Semester} into this class for Jackson use.
     */
    public JsonAdaptedSemester(Semester source) {
        requireNonNull(source);

        semesterValue = source.plannerSemester;
    }

    @JsonValue
    public String getSemesterValue() {
        return semesterValue;
    }

    /**
     * Converts this Jackson-friendly adapted tag object into the model's {@link Semester} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted semester.
     */
    public Semester toModelType() throws IllegalValueException {
        if (!Semester.isValidSemester(semesterValue)) {
            throw new IllegalValueException(Semester.MESSAGE_SEMESTER_CONSTRAINTS);
        }
        return new Semester(semesterValue);
    }

}
