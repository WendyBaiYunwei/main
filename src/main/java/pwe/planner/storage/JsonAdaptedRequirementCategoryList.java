package pwe.planner.storage;

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
public class JsonAdaptedRequirementCategoryList {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "requirement's %s field is missing!";

    private final String name;
    private final String credits;
    private final List<JsonAdaptedCode> codeList = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedRequirementCategoryList} with the given requirement details.
     */
    @JsonCreator
    public JsonAdaptedRequirementCategoryList(@JsonProperty("name") String name,
            @JsonProperty("credits") String credits,
            @JsonProperty("codeList") List<JsonAdaptedCode> codeList) {
        this.name = name;
        this.credits = credits;
        if (codeList != null) {
            this.codeList.addAll(codeList);
        }
    }

    /**
     * Converts a given {@code RequirementCategory} into this class for Jackson use.
     */
    public JsonAdaptedRequirementCategoryList(RequirementCategory source) {
        name = source.getName().fullName;
        credits = source.getCredits().value;
        codeList.addAll(source.getCodeSet().stream().map(JsonAdaptedCode::new).collect(Collectors.toList()));
    }

    /**
     * Converts this Jackson-friendly adapted requirement object into the model's {@code RequirementCategory} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted requirement.
     */
    public RequirementCategory toModelType() throws IllegalValueException {
        final List<Code> codes = new ArrayList<>();
        for (JsonAdaptedCode code : codeList) {
            codes.add(code.toModelType());
        }

        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(name);

        if (credits == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Credits.class.getSimpleName()));
        }
        if (!Credits.isValidCredits(credits)) {
            throw new IllegalValueException(Credits.MESSAGE_CONSTRAINTS);
        }
        final Credits modelCredits = new Credits(credits);

        final Set<Code> modelCodes = new HashSet<>(codes);
        return new RequirementCategory(modelName, modelCredits, modelCodes);
    }
}
