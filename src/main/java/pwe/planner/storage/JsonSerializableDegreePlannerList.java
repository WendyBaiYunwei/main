package pwe.planner.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import javafx.collections.ObservableList;

import pwe.planner.commons.exceptions.IllegalValueException;
import pwe.planner.model.ReadOnlyApplication;
import pwe.planner.model.module.Code;
import pwe.planner.model.planner.DegreePlanner;
import pwe.planner.model.planner.UniqueDegreePlannerList;

/**
 * An Immutable DegreePlannerList that is serializable to JSON format.
 */
@JsonRootName(value = "degreePlannerList")
public class JsonSerializableDegreePlannerList {

    public static final String MESSAGE_DUPLICATE_DEGREE_PLANNER =
            "DegreePlanner list contains duplicate degreePlanner(s).";

    public static final String MESSAGE_DUPLICATE_DEGREE_PLANNER_CODE =
            "The module code (%1$s) is added to more than one year/semester!";

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
    public JsonSerializableDegreePlannerList(ReadOnlyApplication source) {
        degreePlanners.addAll(source.getDegreePlannerList().stream().map(JsonAdaptedDegreePlannerList::new)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this degreePlanner list into the model's {@code DegreePlannerList} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public ObservableList<DegreePlanner> toModelType() throws IllegalValueException {
        UniqueDegreePlannerList uniqueDegreePlannerList = new UniqueDegreePlannerList();
        for (JsonAdaptedDegreePlannerList jsonAdaptedDegreePlannerList : degreePlanners) {
            DegreePlanner degreePlanner = jsonAdaptedDegreePlannerList.toModelType();
            if (uniqueDegreePlannerList.contains(degreePlanner)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_DEGREE_PLANNER);
            }
            uniqueDegreePlannerList.add(degreePlanner);
        }

        ObservableList<DegreePlanner> degreePlannerList = uniqueDegreePlannerList.asUnmodifiableObservableList();

        for (DegreePlanner degreePlanner : degreePlannerList) {
            for (Code code : degreePlanner.getCodes()) {
                long codeApperanceInDegreePlanners = degreePlannerList.stream()
                        .map(DegreePlanner::getCodes)
                        .filter(codes -> codes.contains(code))
                        .count();

                if (codeApperanceInDegreePlanners > 1) {
                    throw new IllegalValueException(String.format(MESSAGE_DUPLICATE_DEGREE_PLANNER_CODE, code));
                }
            }
        }
        return degreePlannerList;
    }
}
