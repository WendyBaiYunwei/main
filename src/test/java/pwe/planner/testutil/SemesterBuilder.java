package pwe.planner.testutil;

import static pwe.planner.testutil.DegreePlannerBuilder.DEFAULT_SEMESTER;

import pwe.planner.model.planner.Semester;

/**
 * A utility class to help with building Semester objects.
 */
public class SemesterBuilder {

    private Semester semester;

    public SemesterBuilder() {
        semester = new Semester(DEFAULT_SEMESTER);
    }

    /**
     * Initializes the SemesterBuilder with the data of {@code semesterToCopy}.
     */
    public SemesterBuilder(Semester semToCopy) {
        semester = semToCopy;
    }

    /**
     * Sets the {@code Semester} of the {@code Semester} that we are building.
     */
    public SemesterBuilder withSemester(String inputSemester) {
        this.semester = new Semester(inputSemester);
        return this;
    }

    public Semester build() {
        return new Semester(DEFAULT_SEMESTER);
    }

}
