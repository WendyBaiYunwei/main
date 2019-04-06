package pwe.planner.testutil;

import static pwe.planner.testutil.DegreePlannerBuilder.DEFAULT_YEAR;

import pwe.planner.model.planner.Year;

/**
 * A utility class to help with building Year objects.
 */
public class YearBuilder {

    private Year year;

    public YearBuilder() {
        year = new Year(DEFAULT_YEAR);
    }

    /**
     * Initializes the YearBuilder with the data of {@code yearToCopy}.
     */
    public YearBuilder(Year yearToCopy) {
        year = yearToCopy;
    }

    /**
     * Sets the {@code Year} of the {@code Year} that we are building.
     */
    public YearBuilder withYear(String inputYear) {
        this.year = new Year(inputYear);
        return this;
    }

    public Year build() {
        return new Year(DEFAULT_YEAR);
    }

}
