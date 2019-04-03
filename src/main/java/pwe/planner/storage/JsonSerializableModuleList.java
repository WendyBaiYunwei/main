package pwe.planner.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import javafx.collections.ObservableList;
import pwe.planner.commons.exceptions.IllegalValueException;
import pwe.planner.model.ReadOnlyApplication;
import pwe.planner.model.module.Code;
import pwe.planner.model.module.Module;
import pwe.planner.model.module.UniqueModuleList;

/**
 * An Immutable Application that is serializable to JSON format.
 */
@JsonRootName(value = "modules")
class JsonSerializableModuleList {

    public static final String MESSAGE_DUPLICATE_MODULE = "Modules list contains duplicate module(s).";
    public static final String MESSAGE_INVALID_COREQUISITE =
            "The module code (%1$s) cannot be a co-requisite of itself!";
    public static final String MESSAGE_NON_EXISTENT_COREQUISITE =
            "The corequisite module code (%1$s) does not exists in the module list";
    public static final String MESSAGE_ONE_WAY_COREQUISITE =
            "The module code (%1$s) is a corequisite of module code (%2$s), but not the other way round!";

    private final List<JsonAdaptedModule> modules = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableModuleList} with the given modules.
     */

    @JsonCreator
    public JsonSerializableModuleList(@JsonProperty("modules") List<JsonAdaptedModule> modules) {
        this.modules.addAll(modules);
    }

    /**
     * Converts a given {@code ReadOnlyApplication} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableModuleList}.
     */
    public JsonSerializableModuleList(ReadOnlyApplication source) {
        modules.addAll(source.getModuleList().stream().map(JsonAdaptedModule::new).collect(Collectors.toList()));
    }

    /**
     * Converts this application into the model's {@code Application} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public ObservableList<Module> toModelType() throws IllegalValueException {
        UniqueModuleList uniqueModuleList = new UniqueModuleList();
        for (JsonAdaptedModule jsonAdaptedModule : modules) {
            Module module = jsonAdaptedModule.toModelType();
            if (uniqueModuleList.contains(module)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_MODULE);
            }
            uniqueModuleList.add(module);
        }

        ObservableList<Module> moduleList = uniqueModuleList.asUnmodifiableObservableList();

        for (Module module : moduleList) {
            for (Code corequisite : module.getCorequisites()) {
                if (module.getCode().equals(corequisite)) {
                    throw new IllegalValueException(String.format(MESSAGE_INVALID_COREQUISITE, corequisite));
                }

                Optional<Module> corequisiteModuleOptional = moduleList.stream()
                        .filter(currentModule -> currentModule.getCode().equals(corequisite))
                        .findFirst();

                if (!corequisiteModuleOptional.isPresent()) {
                    throw new IllegalValueException(String.format(MESSAGE_NON_EXISTENT_COREQUISITE, corequisite));
                }
                if (!corequisiteModuleOptional.get().getCorequisites().contains(module.getCode())) {
                    throw new IllegalValueException(String.format(MESSAGE_ONE_WAY_COREQUISITE, module.getCode(),
                            corequisite));
                }
            }
        }
        return moduleList;
    }

}
