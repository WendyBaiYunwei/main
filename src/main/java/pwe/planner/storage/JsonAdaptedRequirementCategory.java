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
import pwe.planner.model.module.Credits;
import pwe.planner.model.module.Name;
import pwe.planner.model.requirement.RequirementCategory;

/**
 * Jackson-friendly version of {@link RequirementCategory}.
 */
public class JsonAdaptedRequirementCategory {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Requirement Category's %s field is missing!";

    private final JsonAdaptedName name;
    private final JsonAdaptedCredits credits;
    private final List<JsonAdaptedCode> codeList = new ArrayList<>();

    /**
     * Constructs a {@link JsonAdaptedRequirementCategory} with the given requirement category details.
     */
    @JsonCreator
    public JsonAdaptedRequirementCategory(@JsonProperty("name") JsonAdaptedName name,
            @JsonProperty("credits") JsonAdaptedCredits credits,
            @JsonProperty("codeList") List<JsonAdaptedCode> codeList) {
        this.name = name;
        this.credits = credits;
        if (codeList != null) {
            this.codeList.addAll(codeList);
        }
    }

    /**
     * Converts a given {@link RequirementCategory} into this class for Jackson use.
     */
    public JsonAdaptedRequirementCategory(RequirementCategory source) {
        requireNonNull(source);

        name = new JsonAdaptedName(source.getName());
        credits = new JsonAdaptedCredits(source.getCredits());
        codeList.addAll(source.getCodeSet().stream().map(JsonAdaptedCode::new).collect(Collectors.toList()));
    }

    /**
     * Converts this Jackson-friendly adapted requirement catergory object into the model's {@link RequirementCategory}
     * object.
     * @throws IllegalValueException if there were any data constraints violated in the adapted requirement category.
     */
    public RequirementCategory toModelType() throws IllegalValueException {
        final List<Code> codes = new ArrayList<>();
        for (JsonAdaptedCode code : codeList) {
            codes.add(code.toModelType());
        }

        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        final Name modelName = name.toModelType();

        if (credits == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Credits.class.getSimpleName()));
        }
        final Credits modelCredits = credits.toModelType();

        final Set<Code> modelCodes = new HashSet<>(codes);
        return new RequirementCategory(modelName, modelCredits, modelCodes);
    }
}
