package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import javafx.collections.ObservableList;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.requirement.RequirementCategory;

/**
 * An Immutable requirementCategoryList that is serializable to JSON format.
 */
@JsonRootName(value = "requirementCategoryList")
public class JsonSerializableRequirementCategoryList {

    public static final String MESSAGE_DUPLICATE_REQUIREMENT_CATEGORY =
            "Requirement category list contains duplicate requirement categories.";

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
        AddressBook requirementCategoryList = new AddressBook();
        for (JsonAdaptedRequirementCategoryList jsonAdaptedRequirementCategoryList : requirementCategories) {
            RequirementCategory requirementCategory = jsonAdaptedRequirementCategoryList.toModelType();
            if (requirementCategoryList.hasRequirementCategory(requirementCategory)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_REQUIREMENT_CATEGORY);
            }
            requirementCategoryList.addRequirementCategory(requirementCategory);
        }
        return requirementCategoryList.getRequirementCategoryList();
    }
}
