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
import pwe.planner.model.module.Module;
import pwe.planner.model.module.Name;
import pwe.planner.model.planner.Semester;
import pwe.planner.model.tag.Tag;

/**
 * Jackson-friendly version of {@link Module}.
 */
class JsonAdaptedModule {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Module's %s field is missing!";
    public static final String MESSAGE_INVALID_COREQUISITE =
            "The module code (%1$s) cannot be a co-requisite of itself!";

    private final JsonAdaptedCode code;
    private final JsonAdaptedName name;
    private final JsonAdaptedCredits credits;
    private final List<JsonAdaptedSemester> semesters = new ArrayList<>();
    private final List<JsonAdaptedCode> corequisites = new ArrayList<>();
    private final List<JsonAdaptedTag> tags = new ArrayList<>();

    /**
     * Constructs a {@link JsonAdaptedModule} with the given module details.
     */
    @JsonCreator
    public JsonAdaptedModule(@JsonProperty("code") JsonAdaptedCode code,
            @JsonProperty("name") JsonAdaptedName name,
            @JsonProperty("credits") JsonAdaptedCredits credits,
            @JsonProperty("semesters") List<JsonAdaptedSemester> semesters,
            @JsonProperty("corequisites") List<JsonAdaptedCode> corequisites,
            @JsonProperty("tagged") List<JsonAdaptedTag> tags) {
        this.code = code;
        this.name = name;
        this.credits = credits;

        if (semesters != null) {
            this.semesters.addAll(semesters);
        }

        if (corequisites != null) {
            this.corequisites.addAll(corequisites);
        }

        if (tags != null) {
            this.tags.addAll(tags);
        }
    }

    /**
     * Converts a given {@link Module} into this class for Jackson use.
     */
    public JsonAdaptedModule(Module source) {
        requireNonNull(source);

        code = new JsonAdaptedCode(source.getCode());
        name = new JsonAdaptedName(source.getName());
        credits = new JsonAdaptedCredits(source.getCredits());
        semesters.addAll(source.getSemesters().stream().map(JsonAdaptedSemester::new).collect(Collectors.toList()));
        corequisites.addAll(source.getCorequisites().stream().map(JsonAdaptedCode::new).collect(Collectors.toList()));
        tags.addAll(source.getTags().stream().map(JsonAdaptedTag::new).collect(Collectors.toList()));
    }

    /**
     * Converts this Jackson-friendly adapted module object into the model's {@link Module} object.
     * <br><br>
     * Data constraints:<br>
     * - Module code must be valid.<br>
     * - Module name must be valid.<br>
     * - Module credits must be valid.<br>
     * - Semesters offering module must be valid.<br>
     * - Module co-requisites must not contain current module code.<br>
     * - Module tags must be valid.<br>
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted module.
     */
    public Module toModelType() throws IllegalValueException {
        // Check valid Code
        if (code == null) {
            String exceptionMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Code.class.getSimpleName());
            throw new IllegalValueException(exceptionMessage);
        }
        final Code modelCode = code.toModelType();

        // Check valid Name
        if (name == null) {
            String exceptionMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName());
            throw new IllegalValueException(exceptionMessage);
        }
        final Name modelName = name.toModelType();

        // Check valid Credits
        if (credits == null) {
            String exceptionMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Credits.class.getSimpleName());
            throw new IllegalValueException(exceptionMessage);
        }
        final Credits modelCredits = credits.toModelType();

        // Check valid Semesters
        final Set<Semester> modelSemesters = new HashSet<>();
        for (JsonAdaptedSemester semester : semesters) {
            modelSemesters.add(semester.toModelType());
        }

        // Check valid Corequisites
        final Set<Code> modelCorequisites = new HashSet<>();
        for (JsonAdaptedCode corequisite : corequisites) {
            Code corequisiteCode = corequisite.toModelType();
            if (corequisiteCode.equals(modelCode)) {
                throw new IllegalValueException(String.format(MESSAGE_INVALID_COREQUISITE, corequisiteCode));
            }
            modelCorequisites.add(corequisiteCode);
        }

        // Check valid Tags
        final Set<Tag> modelTags = new HashSet<>();
        for (JsonAdaptedTag tag : tags) {
            modelTags.add(tag.toModelType());
        }

        return new Module(modelCode, modelName, modelCredits, modelSemesters, modelCorequisites, modelTags);
    }

}
