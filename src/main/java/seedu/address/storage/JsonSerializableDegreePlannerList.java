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
import seedu.address.model.planner.DegreePlanner;

/**
 * An Immutable DegreePlannerList that is serializable to JSON format.
 */
@JsonRootName(value = "degreePlannerList")
public class JsonSerializableDegreePlannerList {

    public static final String MESSAGE_DUPLICATE_DEGREE_PLANNER =
            "DegreePlanner list contains duplicate degreePlanner(s).";

    private final List<JsonAdaptedDegreePlannerList> degreePlanners = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableDegreePlannerList} with the given degree planners.
     */
    @JsonCreator
    public JsonSerializableDegreePlannerList(
            @JsonProperty("degreePlanners") List<JsonAdaptedDegreePlannerList> degreePlanners) {
        this.degreePlanners.addAll(degreePlanners);
    }

    /**
     * Converts a given {@code ReadOnlyDegreePlannerList} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableDegreePlannerList}.
     */
    public JsonSerializableDegreePlannerList(ReadOnlyAddressBook source) {
        degreePlanners.addAll(source.getDegreePlannerList().stream().map(JsonAdaptedDegreePlannerList::new)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this degreePlanner list into the model's {@code DegreePlannerList} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public ObservableList<DegreePlanner> toModelType() throws IllegalValueException {
        AddressBook degreePlannerList = new AddressBook();
        for (JsonAdaptedDegreePlannerList jsonAdaptedDegreePlannerList : degreePlanners) {
            DegreePlanner degreePlanner = jsonAdaptedDegreePlannerList.toModelType();
            if (degreePlannerList.hasDegreePlanner(degreePlanner)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_DEGREE_PLANNER);
            }
            degreePlannerList.addDegreePlanner(degreePlanner);
        }
        return degreePlannerList.getDegreePlannerList();
    }
}
