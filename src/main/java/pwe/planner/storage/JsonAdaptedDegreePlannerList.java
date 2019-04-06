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
public class JsonAdaptedDegreePlannerList {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "degreePlanner's %s field is missing!";

    private final String year;
    private final String semester;
    private final List<JsonAdaptedCode> codes = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedModule} with the given module details.
     */
    @JsonCreator
    public JsonAdaptedDegreePlannerList(@JsonProperty("year") String year,
            @JsonProperty("semester") String semester, @JsonProperty("codes") List<JsonAdaptedCode> codes) {
        this.year = year;
        this.semester = semester;
        this.codes.addAll(codes);
    }

    /**
     * Converts a given {@code DegreePlanner} into this class for Jackson use.
     */
    public JsonAdaptedDegreePlannerList(DegreePlanner source) {
        requireNonNull(source);

        year = source.getYear().year;
        semester = source.getSemester().plannerSemester;
        codes.addAll(source.getCodes().stream().map(JsonAdaptedCode::new).collect(Collectors.toList()));
    }

    /**
     * Converts this Jackson-friendly adapted degreePlanner object into the model's {@code DegreePlanner} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted degreePlanner.
     */
    public DegreePlanner toModelType() throws IllegalValueException {
        final List<Code> plannerCodes = new ArrayList<>();
        for (JsonAdaptedCode code : codes) {
            plannerCodes.add(code.toModelType());
        }

        if (year == null) {
            throw new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, Year.class.getSimpleName()));
        }
        if (!Year.isValidYear(year)) {
            throw new IllegalValueException(Year.MESSAGE_YEAR_CONSTRAINTS);
        }
        final Year modelYear = new Year(year);

        if (semester == null) {
            throw new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, Semester.class.getSimpleName()));
        }
        if (!Semester.isValidSemester(semester)) {
            throw new IllegalValueException(Semester.MESSAGE_SEMESTER_CONSTRAINTS);
        }
        final Semester modelSemester = new Semester(semester);

        final Set<Code> modelCodes = new HashSet<>(plannerCodes);

        return new DegreePlanner(modelYear, modelSemester, modelCodes);
    }
}
