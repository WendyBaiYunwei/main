package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.ReadOnlyRequirementCategoryList;
import seedu.address.model.RequirementCategoryList;
import seedu.address.model.requirement.RequirementCategory;

/**
 * An Immutable RequirementList that is serializable to JSON format.
 */
@JsonRootName(value = "requirementCategoryList")
public class JsonSerializableRequirementCategoryList {

    public static final String MESSAGE_DUPLICATE_REQUIREMENT =
            "Requirement list contains duplicate requirements(s).";

    private final List<JsonAdaptedRequirementCategoryList> requirementCategories = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableRequirementList} with the given requirements.
     */
    @JsonCreator
    public JsonSerializableRequirementCategoryList(
            @JsonProperty("requirementCategories") List<JsonAdaptedRequirementCategoryList> requirementCategories) {
        this.requirementCategories.addAll(requirementCategories);
    }

    /**
     * Converts a given {@code ReadOnlyRequirementList} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableRequirementList}.
     */
    public JsonSerializableRequirementCategoryList(ReadOnlyRequirementCategoryList source) {
        requirementCategories
                .addAll(source.getRequirementCategoryList().stream().map(JsonAdaptedRequirementCategoryList::new)
                        .collect(Collectors.toList()));
    }

    /**
     * Converts this requirement list into the model's {@code RequirementList} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public RequirementCategoryList toModelType() throws IllegalValueException {
        RequirementCategoryList requirementCategoryList = new RequirementCategoryList();
        for (JsonAdaptedRequirementCategoryList jsonAdaptedRequirementCategoryList : requirementCategories) {
            RequirementCategory requirementCategory = jsonAdaptedRequirementCategoryList.toModelType();
            if (requirementCategoryList.hasRequirementCategory(requirementCategory)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_REQUIREMENT);
            }
            requirementCategoryList.addRequirementCategory(requirementCategory);
        }
        return requirementCategoryList;
    }
}
