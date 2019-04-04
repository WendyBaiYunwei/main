package pwe.planner.testutil;

import java.util.HashSet;
import java.util.Set;

import pwe.planner.model.module.Code;
import pwe.planner.model.planner.DegreePlanner;
import pwe.planner.model.planner.Semester;
import pwe.planner.model.planner.Year;
import pwe.planner.model.util.SampleDataUtil;

/**
 * A utility class to help with building DegreePlanner objects.
 * Example usage: <br>
 * {@code DegreePlanner dp = new DegreePlannerBuilder().withYear("1).withSemester("1").build();}
 */
public class DegreePlannerBuilder {

    public static final String DEFAULT_YEAR = "1";
    public static final String DEFAULT_SEMESTER = "1";

    private Year year;
    private Semester semester;
    private Set<Code> codes;

    public DegreePlannerBuilder() {
        year = new Year(DEFAULT_YEAR);
        semester = new Semester(DEFAULT_SEMESTER);
        codes = new HashSet<>();
    }

    /**
     * Initializes the DegreePlannerBuilder with the data of {@code degreePlannerToCopy}.
     */
    public DegreePlannerBuilder(DegreePlanner degreePlannerToCopy) {
        year = degreePlannerToCopy.getYear();
        semester = degreePlannerToCopy.getSemester();
        codes = new HashSet<Code>(degreePlannerToCopy.getCodes());
    }

    /**
     * Sets the {@code Year} of the {@code DegreePlanner} that we are building.
     */
    public DegreePlannerBuilder withYear(String year) {
        this.year = new Year(year);
        return this;
    }

    /**
     * Sets the {@code Semester} of the {@code DegreePlanner} that we are building.
     */
    public DegreePlannerBuilder withSemester(String semester) {
        this.semester = new Semester(semester);
        return this;
    }

    /**
     * Parses the {@code codes} into a {@code Set<Code>} and set it to the {@code DegreePlanner} that we are building.
     */
    public DegreePlannerBuilder withCodes(String... codes) {
        this.codes = SampleDataUtil.getCodeSet(codes);
        return this;
    }

    public DegreePlanner build() {
        return new DegreePlanner(year, semester, codes);
    }
}
