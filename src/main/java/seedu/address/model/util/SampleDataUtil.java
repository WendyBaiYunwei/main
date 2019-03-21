package seedu.address.model.util;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.module.Code;
import seedu.address.model.module.Credits;
import seedu.address.model.module.Module;
import seedu.address.model.module.Name;
import seedu.address.model.requirement.RequirementCategory;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods for populating {@code AddressBook} with sample data.
 */
public class SampleDataUtil {
    private static final Module CS1010 = new Module(
            new Name("Programming Methodology"),
            new Credits("4"),
            new Code("CS1010"),
            getTagSet("programming", "algorithms", "c", "imperative"),
            getCodeSet()
    );

    private static final Module CS1231 = new Module(
            new Name("Discrete Structures"),
            new Credits("4"),
            new Code("CS1231"),
            getTagSet("math", "logic", "proving"),
            getCodeSet()
    );

    private static final Module CS2040C = new Module(
            new Name("Data Structures and Algorithms"),
            new Credits("4"),
            new Code("CS2040C"),
            getTagSet("linkedlist", "stack", "queue", "hashtable", "heap", "avltree", "graph", "sssp"),
            getCodeSet()
    );

    private static final Module CS2100 = new Module(
            new Name("Computer Organisation"),
            new Credits("4"),
            new Code("CS2100"),
            getTagSet("boolean", "mips", "assembly", "circuit", "flipflop", "pipelining", "cache"),
            getCodeSet()
    );

    private static final Module CS2102 = new Module(
            new Name("Database Systems"),
            new Credits("4"),
            new Code("CS2102"),
            getTagSet("database", "rdbms", "entity", "sql", "normalisation"),
            getCodeSet()
    );

    private static final RequirementCategory COMPUTING_FOUNDATION = new RequirementCategory(
            new Name("Computing Foundation"), new Credits("36"),
            getCodeSet("CS1010", "CS1231", "CS2040C", "CS2100", "CS2102")
    );
    private static final RequirementCategory INFORMATION_SECURITY_REQUIREMENTS = new RequirementCategory(
            new Name("Information Security Requirements"), new Credits("32"), getCodeSet()
    );

    private static final RequirementCategory INFORMATION_SECURITY_ELECTIVES = new RequirementCategory(
            new Name("Information Security Electives"), new Credits("12"), getCodeSet()
    );

    private static final RequirementCategory COMPUTING_BREADTH = new RequirementCategory(
            new Name("Computing Breadth"), new Credits("20"), getCodeSet()
    );

    private static final RequirementCategory IT_PROFESSIONALISM = new RequirementCategory(
            new Name("IT Professionalism"), new Credits("8"), getCodeSet()
    );

    private static final RequirementCategory MATHEMATICS = new RequirementCategory(
            new Name("Mathematics"), new Credits("12"), getCodeSet()
    );

    private static final RequirementCategory GENERAL_EDUCATION = new RequirementCategory(
            new Name("General Education"), new Credits("20"), getCodeSet()
    );

    private static final RequirementCategory UNRESTRICTED_ELECTIVES = new RequirementCategory(
            new Name("Unrestricted Electives"), new Credits("12"), getCodeSet()
    );

    public static Module[] getSampleModules() {
        return new Module[] {
            CS1010, CS1231, CS2040C, CS2100, CS2102
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

    public static ReadOnlyAddressBook getSampleAddressBook() {
        AddressBook sampleAb = new AddressBook();
        for (Module sampleModule : getSampleModules()) {
            sampleAb.addModule(sampleModule);
        }
        for (RequirementCategory sampleRequirementCategory : getSampleRequirementCategories()) {
            sampleAb.addRequirementCategory(sampleRequirementCategory);
        }
        return sampleAb;
    }

    /**
     * Returns a code set containing the list of strings given.
     */
    public static Set<Code> getCodeSet(String... strings) {
        return Arrays.stream(strings)
                .map(Code::new)
                .collect(Collectors.toSet());
    }

    /**
     * Returns a tag set containing the list of strings given.
     */
    public static Set<Tag> getTagSet(String... strings) {
        return Arrays.stream(strings)
                .map(Tag::new)
                .collect(Collectors.toSet());
    }

}
