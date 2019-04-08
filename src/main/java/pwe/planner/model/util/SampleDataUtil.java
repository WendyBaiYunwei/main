package pwe.planner.model.util;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import pwe.planner.model.Application;
import pwe.planner.model.ReadOnlyApplication;
import pwe.planner.model.module.Code;
import pwe.planner.model.module.Credits;
import pwe.planner.model.module.Module;
import pwe.planner.model.module.Name;
import pwe.planner.model.planner.DegreePlanner;
import pwe.planner.model.planner.Semester;
import pwe.planner.model.planner.Year;
import pwe.planner.model.requirement.RequirementCategory;
import pwe.planner.model.tag.Tag;

/**
 * Contains utility methods for populating {@code Application} with sample data.
 */
public class SampleDataUtil {
    private static final Module CS1010 = new Module(
            new Code("CS1010"),
            new Name("Programming Methodology"),
            new Credits("4"),
            getSemesterSet("1", "2", "4"),
            getCorequisiteSet(),
            getTagSet("programming", "algorithms", "c", "imperative")
    );

    private static final Module CS1231 = new Module(
            new Code("CS1231"),
            new Name("Discrete Structures"),
            new Credits("4"),
            getSemesterSet("1", "2"),
            getCorequisiteSet(),
            getTagSet("math", "logic", "proving")
    );

    private static final Module CS2040C = new Module(
            new Code("CS2040C"),
            new Name("Data Structures and Algorithms"),
            new Credits("4"),
            getSemesterSet("1", "2"),
            getCorequisiteSet(),
            getTagSet("linkedlist", "stack", "queue", "hashtable", "heap", "avltree", "graph", "sssp")
    );

    private static final Module CS2100 = new Module(
            new Code("CS2100"),
            new Name("Computer Organisation"),
            new Credits("4"),
            getSemesterSet("1", "2"),
            getCorequisiteSet(),
            getTagSet("boolean", "mips", "assembly", "circuit", "flipflop", "pipelining", "cache")
    );

    private static final Module CS2102 = new Module(
            new Code("CS2102"),
            new Name("Database Systems"),
            new Credits("4"),
            getSemesterSet("1", "2"),
            getCorequisiteSet(),
            getTagSet("database", "rdbms", "entity", "sql", "normalisation")
    );

    private static final DegreePlanner YEAR_1_SEMESTER_1 = new DegreePlanner(
            new Year("1"),
            new Semester("1"),
            getCodeSet("CS1010", "CS1231", "CS2040C", "CS2100", "CS2102")
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
            new Name("Computing Foundation"), new Credits("36"),
            getCodeSet("CS1010", "CS1231", "CS2040C", "CS2100", "CS2102")
    );
    private static final RequirementCategory INFORMATION_SECURITY_REQUIREMENTS = new RequirementCategory(
            new Name("Information Security Requirements"),
            new Credits("20"),
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

    public static Module[] getSampleModules() {
        return new Module[] {
            CS1010, CS1231, CS2040C, CS2100, CS2102
        };
    }

    public static DegreePlanner[] getSampleDegreePlanners() {
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

    public static RequirementCategory[] getSampleRequirementCategories() {
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

    public static ReadOnlyApplication getSampleApplication() {
        Application sampleAb = new Application();
        for (Module sampleModule : getSampleModules()) {
            sampleAb.addModule(sampleModule);
        }
        for (RequirementCategory sampleRequirementCategory : getSampleRequirementCategories()) {
            sampleAb.addRequirementCategory(sampleRequirementCategory);
        }
        for (DegreePlanner sampleDegreePlanner : getSampleDegreePlanners()) {
            sampleAb.addDegreePlanner(sampleDegreePlanner);
        }
        return sampleAb;
    }

    /**
     * Returns a semester set containing the list of strings given.
     */
    public static Set<Semester> getSemesterSet(String... strings) {
        requireNonNull(strings);

        return Arrays.stream(strings)
                .map(Semester::new)
                .collect(Collectors.toSet());
    }

    /**
     * Returns a code set containing the list of strings given.
     */
    public static Set<Code> getCodeSet(String... strings) {
        requireNonNull(strings);

        return Arrays.stream(strings)
                .map(Code::new)
                .collect(Collectors.toSet());
    }

    /**
     * Returns a code set containing the list of strings given.
     * Syntactic sugar for {@link #getCodeSet(String...)}
     */
    public static Set<Code> getCorequisiteSet(String... strings) {
        return getCodeSet(strings);
    }

    /**
     * Returns a tag set containing the list of strings given.
     */
    public static Set<Tag> getTagSet(String... strings) {
        requireNonNull(strings);

        return Arrays.stream(strings)
                .map(Tag::new)
                .collect(Collectors.toSet());
    }

}
