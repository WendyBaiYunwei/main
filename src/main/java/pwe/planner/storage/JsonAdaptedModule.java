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

    private final String code;
    private final String name;
    private final String credits;
    private final List<JsonAdaptedSemester> semesters = new ArrayList<>();
    private final List<JsonAdaptedCode> corequisites = new ArrayList<>();
    private final List<JsonAdaptedTag> tags = new ArrayList<>();
    /**
     * Constructs a {@code JsonAdaptedModule} with the given module details.
     */
    @JsonCreator
    public JsonAdaptedModule(@JsonProperty("code") String code,
            @JsonProperty("name") String name,
            @JsonProperty("credits") String credits,
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
     * Converts a given {@code Module} into this class for Jackson use.
     */
    public JsonAdaptedModule(Module source) {
        requireNonNull(source);

        code = source.getCode().value;
        name = source.getName().fullName;
        credits = source.getCredits().value;
        semesters.addAll(source.getSemesters().stream().map(JsonAdaptedSemester::new).collect(Collectors.toList()));
        corequisites.addAll(source.getCorequisites().stream().map(JsonAdaptedCode::new).collect(Collectors.toList()));
        tags.addAll(source.getTags().stream().map(JsonAdaptedTag::new).collect(Collectors.toList()));
    }

    /**
     * Converts this Jackson-friendly adapted module object into the model's {@code Module} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted module.
     */
    public Module toModelType() throws IllegalValueException {
        // Check valid Code
        if (code == null) {
            String exceptionMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Code.class.getSimpleName());
            throw new IllegalValueException(exceptionMessage);
        }
        if (!Code.isValidCode(code)) {
            throw new IllegalValueException(Code.MESSAGE_CONSTRAINTS);
        }
        final Code modelCode = new Code(code);

        // Check valid Name
        if (name == null) {
            String exceptionMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName());
            throw new IllegalValueException(exceptionMessage);
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(name);

        // Check valid Credits
        if (credits == null) {
            String exceptionMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Credits.class.getSimpleName());
            throw new IllegalValueException(exceptionMessage);
        }
        if (!Credits.isValidCredits(credits)) {
            throw new IllegalValueException(Credits.MESSAGE_CONSTRAINTS);
        }
        final Credits modelCredits = new Credits(credits);

        // Check valid Semesters
        if (semesters.isEmpty()) {
            String exceptionMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Semester.class.getSimpleName());
            throw new IllegalValueException(exceptionMessage);
        }
        final Set<Semester> modelSemesters = new HashSet<>();
        for (JsonAdaptedSemester semester : semesters) {
            modelSemesters.add(semester.toModelType());
        }

        // Check valid Corequisites
        final Set<Code> modelCorequisites = new HashSet<>();
        for (JsonAdaptedCode corequisite : corequisites) {
            modelCorequisites.add(corequisite.toModelType());
        }

        // Check valid Tags
        final Set<Tag> modelTags = new HashSet<>();
        for (JsonAdaptedTag tag : tags) {
            modelTags.add(tag.toModelType());
        }

        return new Module(modelCode, modelName, modelCredits, modelSemesters, modelCorequisites, modelTags);
    }

}
