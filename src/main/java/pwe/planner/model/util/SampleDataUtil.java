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
    // Sample modules that can be used
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
            getTagSet("algorithm", "linkedlist", "stack", "queue", "hashtable", "heap", "avltree", "graph", "sssp")
    );

    private static final Module CS2100 = new Module(
            new Code("CS2100"),
            new Name("Computer Organisation"),
            new Credits("4"),
            getSemesterSet("1", "2"),
            getCorequisiteSet(),
            getTagSet("boolean", "mips", "assembly", "circuit", "flipflop", "pipelining", "cache")
    );

    private static final Module CS2101 = new Module(
            new Code("CS2101"),
            new Name("Effective Communication for Computing Professionals"),
            new Credits("4"),
            getSemesterSet("1", "2"),
            getCorequisiteSet("CS2113T"),
            getTagSet("communication", "presentation", "negotiation", "documentation", "reflection")
    );

    private static final Module CS2102 = new Module(
            new Code("CS2102"),
            new Name("Database Systems"),
            new Credits("4"),
            getSemesterSet("1", "2", "4"),
            getCorequisiteSet(),
            getTagSet("database", "rdbms", "entity", "sql", "normalisation")
    );

    private static final Module CS2105 = new Module(
            new Code("CS2105"),
            new Name("Introduction to Computer Networks"),
            new Credits("4"),
            getSemesterSet("1", "2"),
            getCorequisiteSet(),
            getTagSet("network", "tcp", "ip", "udp", "rdt")
    );

    private static final Module CS2106 = new Module(
            new Code("CS2106"),
            new Name("Introduction to Operating Systems"),
            new Credits("4"),
            getSemesterSet("1", "2"),
            getCorequisiteSet(),
            getTagSet("os", "syscall", "process", "scheduling", "memory")
    );

    private static final Module CS2107 = new Module(
            new Code("CS2107"),
            new Name("Introduction to Information Security"),
            new Credits("4"),
            getSemesterSet("1", "2"),
            getCorequisiteSet(),
            getTagSet("infosec", "crypto", "law")
    );

    private static final Module CS2113T = new Module(
            new Code("CS2113T"),
            new Name("Software Engineering and Object-Oriented Programming"),
            new Credits("4"),
            getSemesterSet("1", "2"),
            getCorequisiteSet("CS2101"),
            getTagSet("oop", "rcs", "uml", "junit", "design", "architecture")
    );

    private static final Module CS3235 = new Module(
            new Code("CS3235"),
            new Name("Introduction to Computer Security"),
            new Credits("4"),
            getSemesterSet("1", "2"),
            getCorequisiteSet(),
            getTagSet("crypto", "appsec", "memory", "overflow", "sandbox", "netsec", "websec")
    );

    private static final Module CS4238 = new Module(
            new Code("CS4238"),
            new Name("Computer Security Practices"),
            new Credits("4"),
            getSemesterSet("1", "2"),
            getCorequisiteSet(),
            getTagSet("appsec", "binary", "exploit", "memory", "overflow", "netsec", "websec")
    );

    private static final Module CS5331 = new Module(
            new Code("CS5331"),
            new Name("Web Security"),
            new Credits("4"),
            getSemesterSet("1", "2"),
            getCorequisiteSet(),
            getTagSet("websec", "xss", "sqli", "clickjacking", "sop", "cors", "oauth")
    );

    private static final Module GER1000 = new Module(
            new Code("GER1000"),
            new Name("Quantitative Reasoning"),
            new Credits("4"),
            getSemesterSet("1", "2"),
            getCorequisiteSet(),
            getTagSet("quantitative", "association", "measurement", "risk", "rate", "probability")
    );

    private static final Module GEQ1000 = new Module(
            new Code("GEQ1000"),
            new Name("Asking Questions"),
            new Credits("4"),
            getSemesterSet("1", "2"),
            getCorequisiteSet(),
            getTagSet("thinking", "philosophy", "design", "engineering", "science", "economics")
    );

    private static final Module IFS4205 = new Module(
            new Code("IFS4205"),
            new Name("Information Security Capstone Project"),
            new Credits("8"),
            getSemesterSet("1"),
            getCorequisiteSet(),
            getTagSet("capstone", "infosec", "project", "design", "systems")
    );

    private static final Module IS1103 = new Module(
            new Code("IS1103"),
            new Name("IS Innovations in Organisations and Society"),
            new Credits("4"),
            getSemesterSet("1"),
            getCorequisiteSet(),
            getTagSet("law", "ethics", "professionalism", "decision", "social", "privacy")
    );

    private static final Module IS3103 = new Module(
            new Code("IS3103"),
            new Name("Information Systems Leadership and Communication"),
            new Credits("4"),
            getSemesterSet("1", "2"),
            getCorequisiteSet(),
            getTagSet("business", "leadership", "communication", "interview")
    );

    private static final Module IS4231 = new Module(
            new Code("IS4231"),
            new Name("Information Security Management"),
            new Credits("4"),
            getSemesterSet("1"),
            getCorequisiteSet(),
            getTagSet("infosec", "business", "policies", "continuity", "risk", "management")
    );

    private static final Module MA1301 = new Module(
            new Code("MA1301"),
            new Name("Introductory Mathematics"),
            new Credits("4"),
            getSemesterSet("1", "2"),
            getCorequisiteSet(),
            getTagSet("sequence", "series", "functions", "derivatives", "integrals", "vectors")
    );

    private static final Module MA1101R = new Module(
            new Code("MA1101R"),
            new Name("Linear Algebra I"),
            new Credits("4"),
            getSemesterSet("1", "2"),
            getCorequisiteSet(),
            getTagSet("proving", "vectors", "matrices", "determinants", "euclidean")
    );

    private static final Module MA1521 = new Module(
            new Code("MA1521"),
            new Name("Calculus for Computing"),
            new Credits("4"),
            getSemesterSet("1", "2"),
            getCorequisiteSet(),
            getTagSet("calculus", "derivatives", "integrals", "sequence", "series")
    );

    private static final Module ST2334 = new Module(
            new Code("ST2334"),
            new Name("Probability and Statistics"),
            new Credits("4"),
            getSemesterSet("1", "2"),
            getCorequisiteSet(),
            getTagSet("probability", "statistics", "variance", "distribution", "hypothesis")
    );

    // Sample requirement categories that can be used
    private static final RequirementCategory COMPUTING_FOUNDATION = new RequirementCategory(
            new Name("Computing Foundation"),
            new Credits("36"),
            getCodeSet("CS1010", "CS1231", "CS2040C", "CS2100", "CS2102", "CS2105", "CS2106", "CS2113T", "IS3103")
    );

    private static final RequirementCategory INFORMATION_SECURITY_REQUIREMENTS = new RequirementCategory(
            new Name("Information Security Requirements"),
            new Credits("20"),
            getCodeSet("CS2107", "CS3235", "IFS4205", "IS4231")
    );

    private static final RequirementCategory INFORMATION_SECURITY_ELECTIVES = new RequirementCategory(
            new Name("Information Security Electives"),
            new Credits("12"),
            getCodeSet("CS4238", "CS5331")
    );

    private static final RequirementCategory COMPUTING_BREADTH = new RequirementCategory(
            new Name("Computing Breadth"),
            new Credits("20"),
            getCodeSet()
    );

    private static final RequirementCategory IT_PROFESSIONALISM = new RequirementCategory(
            new Name("IT Professionalism"),
            new Credits("8"),
            getCodeSet("IS1103", "CS2101")
    );

    private static final RequirementCategory MATHEMATICS = new RequirementCategory(
            new Name("Mathematics"),
            new Credits("12"),
            getCodeSet("MA1101R", "MA1521", "ST2334")
    );

    private static final RequirementCategory GENERAL_EDUCATION = new RequirementCategory(
            new Name("General Education"),
            new Credits("20"),
            getCodeSet("GER1000", "GEQ1000")
    );

    private static final RequirementCategory UNRESTRICTED_ELECTIVES = new RequirementCategory(
            new Name("Unrestricted Electives"),
            new Credits("32"),
            getCodeSet("MA1301")
    );

    // Sample degree planners that can be used
    private static final DegreePlanner YEAR_1_SEMESTER_1 = new DegreePlanner(
            new Year("1"),
            new Semester("1"),
            getCodeSet("CS1010", "MA1301", "GER1000")
    );

    private static final DegreePlanner YEAR_1_SEMESTER_2 = new DegreePlanner(
            new Year("1"),
            new Semester("2"),
            getCodeSet("CS1231", "CS2040C", "CS2100", "CS2107")
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
            getCodeSet("CS2102", "CS2105", "CS2106", "CS2101", "CS2113T")
    );

    private static final DegreePlanner YEAR_2_SEMESTER_2 = new DegreePlanner(
            new Year("2"),
            new Semester("2"),
            getCodeSet("CS3235")
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

    public static Module[] getSampleModules() {
        return new Module[] {
            CS1010, CS1231, CS2040C, CS2100, CS2101, CS2102, CS2105, CS2106, CS2107, CS2113T, CS3235, CS4238, CS5331,
            GER1000, GEQ1000,
            IFS4205, IS1103, IS3103, IS4231,
            MA1301, MA1101R, MA1521, ST2334
        };
    }

    // Getter to retrieve all sample requirement categories
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

    // Getter to retrieve all sample degree planners
    public static DegreePlanner[] getSampleDegreePlanners() {
        return new DegreePlanner[] {
            YEAR_1_SEMESTER_1, YEAR_1_SEMESTER_2, YEAR_1_SEMESTER_3, YEAR_1_SEMESTER_4,
            YEAR_2_SEMESTER_1, YEAR_2_SEMESTER_2, YEAR_2_SEMESTER_3, YEAR_2_SEMESTER_4,
            YEAR_3_SEMESTER_1, YEAR_3_SEMESTER_2, YEAR_3_SEMESTER_3, YEAR_3_SEMESTER_4,
            YEAR_4_SEMESTER_1, YEAR_4_SEMESTER_2, YEAR_4_SEMESTER_3, YEAR_4_SEMESTER_4
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
