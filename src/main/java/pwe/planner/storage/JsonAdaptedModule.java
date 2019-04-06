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
import pwe.planner.model.tag.Tag;

/**
 * Jackson-friendly version of {@link Module}.
 */
class JsonAdaptedModule {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Module's %s field is missing!";

    private final String name;
    private final String credits;
    private final String code;
    private final List<JsonAdaptedTag> tagged = new ArrayList<>();
    private final List<JsonAdaptedCode> corequisites = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedModule} with the given module details.
     */
    @JsonCreator
    public JsonAdaptedModule(@JsonProperty("name") String name, @JsonProperty("credits") String credits,
            @JsonProperty("code") String code, @JsonProperty("tagged") List<JsonAdaptedTag> tagged,
            @JsonProperty("corequisites") List<JsonAdaptedCode> corequisites) {
        this.name = name;
        this.credits = credits;
        this.code = code;

        if (tagged != null) {
            this.tagged.addAll(tagged);
        }

        if (corequisites != null) {
            this.corequisites.addAll(corequisites);
        }
    }

    /**
     * Converts a given {@code Module} into this class for Jackson use.
     */
    public JsonAdaptedModule(Module source) {
        requireNonNull(source);

        name = source.getName().fullName;
        credits = source.getCredits().value;
        code = source.getCode().value;
        tagged.addAll(source.getTags().stream()
                .map(JsonAdaptedTag::new)
                .collect(Collectors.toList()));
        corequisites.addAll(source.getCorequisites().stream()
                .map(JsonAdaptedCode::new)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this Jackson-friendly adapted module object into the model's {@code Module} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted module.
     */
    public Module toModelType() throws IllegalValueException {
        final List<Tag> moduleTags = new ArrayList<>();
        for (JsonAdaptedTag tag : tagged) {
            moduleTags.add(tag.toModelType());
        }

        final List<Code> moduleCorequisites = new ArrayList<>();
        for (JsonAdaptedCode corequisite : corequisites) {
            moduleCorequisites.add(corequisite.toModelType());
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

        if (code == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Code.class.getSimpleName()));
        }
        if (!Code.isValidCode(code)) {
            throw new IllegalValueException(Code.MESSAGE_CONSTRAINTS);
        }
        final Code modelCode = new Code(code);

        final Set<Tag> modelTags = new HashSet<>(moduleTags);
        final Set<Code> modelCorequisites = new HashSet<>(moduleCorequisites);
        return new Module(modelName, modelCredits, modelCode, modelTags, modelCorequisites);
    }

}
