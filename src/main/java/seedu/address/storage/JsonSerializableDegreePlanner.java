package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.DegreePlanner;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.module.Module;

/**
 * An Immutable DegreePlanner that is serializable to JSON format.
 */
@JsonRootName(value = "DegreePlanner")
class JsonSerializableDegreePlanner {

    public static final String MESSAGE_DUPLICATE_MODULE = "Modules list contains duplicate module(s).";

    private final List<JsonAdaptedModule> plannerModules = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableDegreePlanner} with the given plannerModules.
     */
    @JsonCreator
    public JsonSerializableDegreePlanner(@JsonProperty("plannerModules") List<JsonAdaptedModule> plannerModules) {
        this.plannerModules.addAll(plannerModules);
    }

    /**
     * Converts a given {@code ReadOnlyDegreePlanner} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableDegreePlanner}.
     */
    public JsonSerializableDegreePlanner(ReadOnlyAddressBook source) {
        plannerModules.addAll(source.getModuleList().stream().map(JsonAdaptedModule::new).collect(Collectors.toList()));
    }

    /**
     * Converts this address book into the model's {@code DegreePlanner} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public DegreePlanner toModelType() throws IllegalValueException {
        DegreePlanner degreePlanner = new DegreePlanner();
        for (JsonAdaptedModule jsonAdaptedModule : plannerModules) {
            Module module = jsonAdaptedModule.toModelType();
            if (degreePlanner.plannerHasModule(module)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_MODULE);
            }
            degreePlanner.plannerAddModule(module);
        }
        return degreePlanner;
    }

}
