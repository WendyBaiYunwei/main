package pwe.planner.storage;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import pwe.planner.commons.exceptions.IllegalValueException;
import pwe.planner.model.module.Code;
import pwe.planner.model.planner.DegreePlanner;
import pwe.planner.model.planner.Semester;
import pwe.planner.model.planner.Year;

/**
 * Jackson-friendly version of {@link DegreePlanner}.
 */
public class JsonAdaptedDegreePlanner {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Degree Planner's %s field is missing!";

    private final JsonAdaptedYear year;
    private final JsonAdaptedSemester semester;
    private final List<JsonAdaptedCode> codes = new ArrayList<>();

    /**
     * Constructs a {@link JsonAdaptedDegreePlanner} with the given degree planner details.
     */
    @JsonCreator
    public JsonAdaptedDegreePlanner(@JsonProperty("year") JsonAdaptedYear year,
            @JsonProperty("semester") JsonAdaptedSemester semester,
            @JsonProperty("codes") List<JsonAdaptedCode> codes) {
        this.year = year;
        this.semester = semester;
        this.codes.addAll(codes);
    }

    /**
     * Converts a given {@link DegreePlanner} into this class for Jackson use.
     */
    public JsonAdaptedDegreePlanner(DegreePlanner source) {
        requireNonNull(source);

        year = new JsonAdaptedYear(source.getYear());
        semester = new JsonAdaptedSemester(source.getSemester());
        codes.addAll(source.getCodes().stream().map(JsonAdaptedCode::new).collect(Collectors.toList()));
    }

    /**
     * Converts this Jackson-friendly adapted degree planner object into the model's {@link DegreePlanner} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted degree planner.
     */
    public DegreePlanner toModelType() throws IllegalValueException {
        // Check valid Year
        if (year == null) {
            throw new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, Year.class.getSimpleName()));
        }
        final Year modelYear = year.toModelType();

        // Check valid Semester
        if (semester == null) {
            throw new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, Semester.class.getSimpleName()));
        }
        final Semester modelSemester = semester.toModelType();

        // Check valid Codes
        final Set<Code> modelCodes = new HashSet<>();
        for (JsonAdaptedCode code : codes) {
            modelCodes.add(code.toModelType());
        }

        return new DegreePlanner(modelYear, modelSemester, modelCodes);
    }
}
