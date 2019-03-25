package seedu.address.testutil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.collections.ObservableList;
import seedu.address.model.AddressBook;
import seedu.address.model.planner.DegreePlanner;

/**
 * A utility class containing a list of {@code DegreePlanner} objects to be used in tests.
 */
public class TypicalDegreePlanners {

    private static final DegreePlanner YEAR_1_SEMESTER_1 =
            new DegreePlannerBuilder().withYear("1").withSemester("1")
                    .withCodes("CS1231", "CS2100", "CS1010", "CS2040C", "CS2102").build();
    private static final DegreePlanner YEAR_1_SEMESTER_2 =
            new DegreePlannerBuilder().withYear("1").withSemester("2").build();
    private static final DegreePlanner YEAR_2_SEMESTER_1 =
            new DegreePlannerBuilder().withYear("2").withSemester("1").build();
    private static final DegreePlanner YEAR_2_SEMESTER_2 =
            new DegreePlannerBuilder().withYear("2").withSemester("2").build();
    private static final DegreePlanner YEAR_3_SEMESTER_1 =
            new DegreePlannerBuilder().withYear("3").withSemester("1").build();
    private static final DegreePlanner YEAR_3_SEMESTER_2 =
            new DegreePlannerBuilder().withYear("3").withSemester("2").build();
    private static final DegreePlanner YEAR_4_SEMESTER_1 =
            new DegreePlannerBuilder().withYear("4").withSemester("1").build();
    private static final DegreePlanner YEAR_4_SEMESTER_2 =
            new DegreePlannerBuilder().withYear("4").withSemester("2").build();

    /**
     * Returns an {@code AddressBook} with all the typical degree planners.
     */
    public static ObservableList<DegreePlanner> getTypicalDegreePlannerList() {
        AddressBook degreePlannerList = new AddressBook();
        for (DegreePlanner degreePlanner : getTypicalDegreePlanners()) {
            degreePlannerList.addDegreePlanner(degreePlanner);
        }
        return degreePlannerList.getDegreePlannerList();
    }

    public static List<DegreePlanner> getTypicalDegreePlanners() {
        return new ArrayList<>(Arrays.asList(YEAR_1_SEMESTER_1, YEAR_1_SEMESTER_2, YEAR_2_SEMESTER_1, YEAR_2_SEMESTER_2,
                YEAR_3_SEMESTER_1, YEAR_3_SEMESTER_2, YEAR_4_SEMESTER_1, YEAR_4_SEMESTER_2));
    }
}
