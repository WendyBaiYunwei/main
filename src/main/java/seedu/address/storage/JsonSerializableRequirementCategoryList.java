package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import javafx.collections.ObservableList;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.module.Code;
import seedu.address.model.requirement.RequirementCategory;
import seedu.address.model.requirement.UniqueRequirementCategoryList;

/**
 * An Immutable requirementCategoryList that is serializable to JSON format.
 */
@JsonRootName(value = "requirementCategoryList")
public class JsonSerializableRequirementCategoryList {

    public static final String MESSAGE_DUPLICATE_REQUIREMENT_CATEGORY =
            "Requirement category list contains duplicate requirement categories.";

    public static final String MESSAGE_DUPLICATE_REQUIREMENT_CATEGORY_CODE =
            "The module code (%1$s) is added to more than one requirement category!";

    private final List<JsonAdaptedRequirementCategoryList> requirementCategories = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableRequirementCategoryList} with the given requirementCategories.
     */
    @JsonCreator
    public JsonSerializableRequirementCategoryList(
            @JsonProperty("requirementCategories") List<JsonAdaptedRequirementCategoryList> requirementCategories) {
        this.requirementCategories.addAll(requirementCategories);
    }

    /**
     * Converts a given {@code JsonSerializableRequirementCategoryList} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableRequirementCategoryList}.
     */

    public JsonSerializableRequirementCategoryList(ReadOnlyAddressBook source) {
        requirementCategories
                .addAll(source.getRequirementCategoryList().stream().map(JsonAdaptedRequirementCategoryList::new)
                        .collect(Collectors.toList()));
    }

    /**
     * Converts this requirementCategory list into the model's {@code JsonAdaptedRequirementCategoryList} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public ObservableList<RequirementCategory> toModelType() throws IllegalValueException {
        UniqueRequirementCategoryList uniqueRequirementCategoryList = new UniqueRequirementCategoryList();
        for (JsonAdaptedRequirementCategoryList jsonAdaptedRequirementCategoryList : requirementCategories) {
            RequirementCategory requirementCategory = jsonAdaptedRequirementCategoryList.toModelType();
            if (uniqueRequirementCategoryList.contains(requirementCategory)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_REQUIREMENT_CATEGORY);
            }
            uniqueRequirementCategoryList.add(requirementCategory);
        }

        ObservableList<RequirementCategory> requirementCategories = uniqueRequirementCategoryList
                .asUnmodifiableObservableList();

        for (RequirementCategory requirementCategory : requirementCategories) {
            for (Code code : requirementCategory.getCodeSet()) {
                long codeApperanceInRequirementCategories = requirementCategories.stream()
                        .map(RequirementCategory::getCodeSet)
                        .filter(codes -> codes.contains(code))
                        .count();

                if (codeApperanceInRequirementCategories > 1) {
                    throw new IllegalValueException(String.format(MESSAGE_DUPLICATE_REQUIREMENT_CATEGORY_CODE, code));
                }
            }
        }
        return requirementCategories;
    }
}
