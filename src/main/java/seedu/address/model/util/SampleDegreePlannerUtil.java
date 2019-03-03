package seedu.address.model.util;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.model.DegreePlannerList;
import seedu.address.model.ReadOnlyDegreePlannerList;
import seedu.address.model.module.Code;
import seedu.address.model.planner.DegreePlanner;
import seedu.address.model.planner.Semester;
import seedu.address.model.planner.Year;

/**
 * Contains utility methods for populating {@code AddressBook} with sample data.
 */
public class SampleDegreePlannerUtil {
    public static DegreePlanner[] getSampleDegreePlanners() {
        return new DegreePlanner[] {
            new DegreePlanner(new Year("1"), new Semester("1"),
                    getCodeSet()),
            new DegreePlanner(new Year("1"), new Semester("2"),
                    getCodeSet()),
            new DegreePlanner(new Year("2"), new Semester("1"),
                    getCodeSet()),
            new DegreePlanner(new Year("2"), new Semester("2"),
                    getCodeSet()),
            new DegreePlanner(new Year("3"), new Semester("1"),
                    getCodeSet()),
            new DegreePlanner(new Year("3"), new Semester("2"),
                    getCodeSet()),
            new DegreePlanner(new Year("4"), new Semester("1"),
                    getCodeSet()),
            new DegreePlanner(new Year("4"), new Semester("2"),
                    getCodeSet())
        };
    }

    public static ReadOnlyDegreePlannerList getSampleDegreePlannerList() {
        DegreePlannerList sampleDegreePlannerList = new DegreePlannerList();
        for (DegreePlanner sampleDegreePlanner : getSampleDegreePlanners()) {
            sampleDegreePlannerList.addDegreePlanner(sampleDegreePlanner);
        }
        return sampleDegreePlannerList;
    }

    /**
     * Returns a tag set containing the list of strings given.
     */
    public static Set<Code> getCodeSet(String... strings) {
        return Arrays.stream(strings)
                .map(Code::new)
                .collect(Collectors.toSet());
    }

}
