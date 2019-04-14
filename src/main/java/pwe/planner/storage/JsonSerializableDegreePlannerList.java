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
import pwe.planner.model.planner.DegreePlanner;
import pwe.planner.model.planner.UniqueDegreePlannerList;

/**
 * An immutable list of degree planners that is serializable to JSON format.
 */
@JsonRootName(value = "degreePlannerList")
public class JsonSerializableDegreePlannerList {

    public static final String MESSAGE_DUPLICATE_DEGREE_PLANNER =
            "DegreePlanner list contains duplicate degreePlanner(s).";

    public static final String MESSAGE_DUPLICATE_DEGREE_PLANNER_CODE =
            "The module code (%1$s) is added to more than one year/semester!";

    private final List<JsonAdaptedDegreePlanner> degreePlanners = new ArrayList<>();

    /**
     * Constructs a {@link JsonSerializableDegreePlannerList} with the given list of {@link JsonAdaptedDegreePlanner}
     */
    @JsonCreator
    public JsonSerializableDegreePlannerList(
            @JsonProperty("degreePlanners") List<JsonAdaptedDegreePlanner> degreePlanners) {
        requireNonNull(degreePlanners);

        this.degreePlanners.addAll(degreePlanners);
    }

    /**
     * Converts a given {@link ObservableList} of {@link DegreePlanner} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@link JsonSerializableDegreePlannerList}.
     */

    public JsonSerializableDegreePlannerList(ObservableList<DegreePlanner> source) {
        requireNonNull(source);

        source.stream().map(JsonAdaptedDegreePlanner::new).forEach(degreePlanners::add);
    }

    /**
     * Converts the list of {@link JsonAdaptedDegreePlanner} into the model's
     * {@code ObservableList<DegreePlanner>} object.
     * <br><br>
     * Data constraints:<br>
     * - All degree planners must be unique.<br>
     * - All codes within requirement categories must be unique (cannot appear in multiple degree planners).
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public ObservableList<DegreePlanner> toModelType() throws IllegalValueException {
        // Ensure all degree planners are unique
        UniqueDegreePlannerList uniqueDegreePlannerList = new UniqueDegreePlannerList();
        for (JsonAdaptedDegreePlanner jsonAdaptedDegreePlanner : degreePlanners) {
            DegreePlanner degreePlanner = jsonAdaptedDegreePlanner.toModelType();
            if (uniqueDegreePlannerList.contains(degreePlanner)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_DEGREE_PLANNER);
            }
            uniqueDegreePlannerList.add(degreePlanner);
        }

        // Ensure that all module codes appears at most once
        ObservableList<DegreePlanner> degreePlannerList = uniqueDegreePlannerList.asUnmodifiableObservableList();
        List<Code> allDegreePlannerCodes = degreePlannerList.stream().map(DegreePlanner::getCodes)
                .flatMap(Collection::stream).collect(Collectors.toList());
        for (Code code : allDegreePlannerCodes) {
            long codeAppearanceInDegreePlanners = allDegreePlannerCodes.stream()
                    .filter(degreePlannerCode -> degreePlannerCode.equals(code))
                    .count();

            if (codeAppearanceInDegreePlanners > 1) {
                throw new IllegalValueException(String.format(MESSAGE_DUPLICATE_DEGREE_PLANNER_CODE, code));
            }
        }

        return degreePlannerList;
    }
}
