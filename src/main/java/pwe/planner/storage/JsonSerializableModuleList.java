package pwe.planner.storage;

import static java.util.Objects.requireNonNull;

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
 * An immutable list of {@link Module modules} that is serializable to JSON format.
 */
@JsonRootName(value = "modules")
class JsonSerializableModuleList {
    public static final String MESSAGE_DUPLICATE_MODULE = "Modules list contains duplicate module(s).";
    public static final String MESSAGE_NON_EXISTENT_COREQUISITE =
            "The corequisite module code (%1$s) does not exists in the module list";
    public static final String MESSAGE_ONE_WAY_COREQUISITE =
            "The module code (%1$s) is a corequisite of module code (%2$s), but not the other way round!";

    private final List<JsonAdaptedModule> modules = new ArrayList<>();

    /**
     * Constructs a {@link JsonSerializableModuleList} with the given list of {@link JsonAdaptedModule}.
     */
    @JsonCreator
    public JsonSerializableModuleList(@JsonProperty("modules") List<JsonAdaptedModule> modules) {
        requireNonNull(modules);

        this.modules.addAll(modules);
    }

    /**
     * Converts a given {@link ObservableList} of {@link Module} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@link JsonSerializableRequirementCategoryList}.
     */
    public JsonSerializableModuleList(ReadOnlyApplication source) {
        requireNonNull(source);

        modules.addAll(source.getModuleList().stream().map(JsonAdaptedModule::new).collect(Collectors.toList()));
    }

    /**
     * Converts the list of {@link JsonAdaptedModule} into the model's {@code ObservableList<Module>} object.<br>
     * Checks for additional data constraints on top of {@link JsonAdaptedModule#toModelType()}.
     * <br><br>
     * Data constraints:<br>
     * - All modules must be unique.<br>
     * - All module co-requisites must refer to existing modules in module list.<br>
     * - All module co-requisites must be two-way<br>
     *   (A has B as a co-requisite, and vice versa).<br>
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public ObservableList<Module> toModelType() throws IllegalValueException {
        // Ensure all modules are unique
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
                Optional<Module> corequisiteModuleOptional = moduleList.stream()
                        .filter(currentModule -> currentModule.getCode().equals(corequisite))
                        .findFirst();

                // Ensure that all module co-requisites refers to existing modules in module list
                if (!corequisiteModuleOptional.isPresent()) {
                    throw new IllegalValueException(String.format(MESSAGE_NON_EXISTENT_COREQUISITE, corequisite));
                }
                // Ensure that module co-requisites must be two-way (A has B as a co-requisite, and vice versa)
                if (!corequisiteModuleOptional.get().getCorequisites().contains(module.getCode())) {
                    throw new IllegalValueException(String.format(MESSAGE_ONE_WAY_COREQUISITE, module.getCode(),
                            corequisite));
                }
            }
        }

        return moduleList;
    }

}
