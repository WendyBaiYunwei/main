package pwe.planner.storage;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import javafx.collections.ObservableList;

import pwe.planner.commons.exceptions.IllegalValueException;
import pwe.planner.model.module.Code;
import pwe.planner.model.requirement.RequirementCategory;
import pwe.planner.model.requirement.UniqueRequirementCategoryList;

/**
 * An immutable list of requirement categories that is serializable to JSON format.
 */
@JsonRootName(value = "requirementCategoryList")
public class JsonSerializableRequirementCategoryList {

    public static final String MESSAGE_DUPLICATE_REQUIREMENT_CATEGORY =
            "Requirement category list contains duplicate requirement categories.";

    public static final String MESSAGE_DUPLICATE_REQUIREMENT_CATEGORY_CODE =
            "The module code (%1$s) is added to more than one requirement category!";

    private final List<JsonAdaptedRequirementCategory> requirementCategories = new ArrayList<>();

    /**
     * Constructs a {@link JsonSerializableRequirementCategoryList} with the given list of
     * {@link JsonAdaptedRequirementCategory}.
     */
    @JsonCreator
    public JsonSerializableRequirementCategoryList(
            @JsonProperty("requirementCategories") List<JsonAdaptedRequirementCategory> requirementCategories) {
        requireNonNull(requirementCategories);

        this.requirementCategories.addAll(requirementCategories);
    }

    /**
     * Converts a given {@link ObservableList} of {@link RequirementCategory} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@link JsonSerializableModuleList}.
     */
    public JsonSerializableRequirementCategoryList(ObservableList<RequirementCategory> source) {
        requireNonNull(source);

        source.stream().map(JsonAdaptedRequirementCategory::new).forEach(requirementCategories::add);
    }

    /**
     * Converts the list of {@link JsonAdaptedRequirementCategory} into the model's
     * {@code ObservableList<RequirementCategory>} object.
     * <br><br>
     * Data constraints:<br>
     * - All requirement categories must be unique.<br>
     * - All codes within requirement categories must be unique (cannot appear in multiple requirement categories).
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public ObservableList<RequirementCategory> toModelType() throws IllegalValueException {
        // Ensure all requirement categories are unique
        UniqueRequirementCategoryList uniqueRequirementCategoryList = new UniqueRequirementCategoryList();
        for (JsonAdaptedRequirementCategory jsonAdaptedRequirementCategory : requirementCategories) {
            RequirementCategory requirementCategory = jsonAdaptedRequirementCategory.toModelType();
            if (uniqueRequirementCategoryList.contains(requirementCategory)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_REQUIREMENT_CATEGORY);
            }
            uniqueRequirementCategoryList.add(requirementCategory);
        }

        // Ensure that all module codes appears at most once
        ObservableList<RequirementCategory> requirementCategories = uniqueRequirementCategoryList
                .asUnmodifiableObservableList();
        List<Code> allRequirementCategoryCodes = requirementCategories.stream().map(RequirementCategory::getCodeSet)
                .flatMap(Collection::stream).collect(Collectors.toList());
        for (Code code : allRequirementCategoryCodes) {
            long codeAppearanceInRequirementCategories = allRequirementCategoryCodes.stream()
                    .filter(requirementCategoryCode -> requirementCategoryCode.equals(code))
                    .count();

            if (codeAppearanceInRequirementCategories > 1) {
                throw new IllegalValueException(String.format(MESSAGE_DUPLICATE_REQUIREMENT_CATEGORY_CODE, code));
            }
        }

        return requirementCategories;
    }
}
