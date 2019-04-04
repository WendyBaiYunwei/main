package pwe.planner.model.util;

import static pwe.planner.model.util.SampleDataUtil.getCodeSet;

import pwe.planner.model.Application;
import pwe.planner.model.ReadOnlyApplication;
import pwe.planner.model.module.Credits;
import pwe.planner.model.module.Name;
import pwe.planner.model.planner.DegreePlanner;
import pwe.planner.model.planner.Semester;
import pwe.planner.model.planner.Year;
import pwe.planner.model.requirement.RequirementCategory;

/**
 * Contains utility methods for populating {@code Application} with sample data.
 */
public class InitialDataUtil {
    private static final DegreePlanner YEAR_1_SEMESTER_1 = new DegreePlanner(
            new Year("1"),
            new Semester("1"),
            getCodeSet()
    );

    private static final DegreePlanner YEAR_1_SEMESTER_2 = new DegreePlanner(
            new Year("1"),
            new Semester("2"),
            getCodeSet()
    );

    private static final DegreePlanner YEAR_1_SEMESTER_3 = new DegreePlanner(
            new Year("1"),
            new Semester("3"),
            getCodeSet()
    );

    private static final DegreePlanner YEAR_1_SEMESTER_4 = new DegreePlanner(
            new Year("1"),
            new Semester("4"),
            getCodeSet()
    );

    private static final DegreePlanner YEAR_2_SEMESTER_1 = new DegreePlanner(
            new Year("2"),
            new Semester("1"),
            getCodeSet()
    );

    private static final DegreePlanner YEAR_2_SEMESTER_2 = new DegreePlanner(
            new Year("2"),
            new Semester("2"),
            getCodeSet()
    );

    private static final DegreePlanner YEAR_2_SEMESTER_3 = new DegreePlanner(
            new Year("2"),
            new Semester("3"),
            getCodeSet()
    );

    private static final DegreePlanner YEAR_2_SEMESTER_4 = new DegreePlanner(
            new Year("2"),
            new Semester("4"),
            getCodeSet()
    );

    private static final DegreePlanner YEAR_3_SEMESTER_1 = new DegreePlanner(
            new Year("3"),
            new Semester("1"),
            getCodeSet()
    );

    private static final DegreePlanner YEAR_3_SEMESTER_2 = new DegreePlanner(
            new Year("3"),
            new Semester("2"),
            getCodeSet()
    );

    private static final DegreePlanner YEAR_3_SEMESTER_3 = new DegreePlanner(
            new Year("3"),
            new Semester("3"),
            getCodeSet()
    );

    private static final DegreePlanner YEAR_3_SEMESTER_4 = new DegreePlanner(
            new Year("3"),
            new Semester("4"),
            getCodeSet()
    );

    private static final DegreePlanner YEAR_4_SEMESTER_1 = new DegreePlanner(
            new Year("4"),
            new Semester("1"),
            getCodeSet()
    );

    private static final DegreePlanner YEAR_4_SEMESTER_2 = new DegreePlanner(
            new Year("4"),
            new Semester("2"),
            getCodeSet()
    );

    private static final DegreePlanner YEAR_4_SEMESTER_3 = new DegreePlanner(
            new Year("4"),
            new Semester("3"),
            getCodeSet()
    );

    private static final DegreePlanner YEAR_4_SEMESTER_4 = new DegreePlanner(
            new Year("4"),
            new Semester("4"),
            getCodeSet()
    );

    private static final RequirementCategory COMPUTING_FOUNDATION = new RequirementCategory(
            new Name("Computing Foundation"),
            new Credits("36"),
            getCodeSet()
    );
    private static final RequirementCategory INFORMATION_SECURITY_REQUIREMENTS = new RequirementCategory(
            new Name("Information Security Requirements"),
            new Credits("32"),
            getCodeSet()
    );

    private static final RequirementCategory INFORMATION_SECURITY_ELECTIVES = new RequirementCategory(
            new Name("Information Security Electives"),
            new Credits("12"),
            getCodeSet()
    );

    private static final RequirementCategory COMPUTING_BREADTH = new RequirementCategory(
            new Name("Computing Breadth"),
            new Credits("20"),
            getCodeSet()
    );

    private static final RequirementCategory IT_PROFESSIONALISM = new RequirementCategory(
            new Name("IT Professionalism"),
            new Credits("8"),
            getCodeSet()
    );

    private static final RequirementCategory MATHEMATICS = new RequirementCategory(
            new Name("Mathematics"),
            new Credits("12"),
            getCodeSet()
    );

    private static final RequirementCategory GENERAL_EDUCATION = new RequirementCategory(
            new Name("General Education"),
            new Credits("20"),
            getCodeSet()
    );

    private static final RequirementCategory UNRESTRICTED_ELECTIVES = new RequirementCategory(
            new Name("Unrestricted Electives"),
            new Credits("12"),
            getCodeSet()
    );

    public static RequirementCategory[] getInitialRequirementCategories() {
        return new RequirementCategory[] {
            COMPUTING_FOUNDATION,
            INFORMATION_SECURITY_REQUIREMENTS,
            INFORMATION_SECURITY_ELECTIVES,
            COMPUTING_BREADTH,
            IT_PROFESSIONALISM,
            MATHEMATICS,
            GENERAL_EDUCATION,
            UNRESTRICTED_ELECTIVES
        };
    }

    public static DegreePlanner[] getInitialDegreePlanners() {
        return new DegreePlanner[] {
            YEAR_1_SEMESTER_1,
            YEAR_1_SEMESTER_2,
            YEAR_1_SEMESTER_3,
            YEAR_1_SEMESTER_4,
            YEAR_2_SEMESTER_1,
            YEAR_2_SEMESTER_2,
            YEAR_2_SEMESTER_3,
            YEAR_2_SEMESTER_4,
            YEAR_3_SEMESTER_1,
            YEAR_3_SEMESTER_2,
            YEAR_3_SEMESTER_3,
            YEAR_3_SEMESTER_4,
            YEAR_4_SEMESTER_1,
            YEAR_4_SEMESTER_2,
            YEAR_4_SEMESTER_3,
            YEAR_4_SEMESTER_4
        };
    }

    public static ReadOnlyApplication getInitialApplication() {
        Application initialAb = new Application();
        for (RequirementCategory initialRequirementCategory : getInitialRequirementCategories()) {
            initialAb.addRequirementCategory(initialRequirementCategory);
        }
        for (DegreePlanner initialDegreePlanner : getInitialDegreePlanners()) {
            initialAb.addDegreePlanner(initialDegreePlanner);
        }
        return initialAb;
    }
}
