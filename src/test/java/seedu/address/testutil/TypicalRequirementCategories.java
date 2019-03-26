package seedu.address.testutil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.collections.ObservableList;
import seedu.address.model.AddressBook;
import seedu.address.model.requirement.RequirementCategory;

/**
 * A utility class containing a list of {@code Module} objects to be used in tests.
 */
public class TypicalRequirementCategories {

    public static final RequirementCategory COMPUTING_FOUNDATION =
            new RequirementCategoryBuilder().withName("Computing Foundation")
                    .withCredits("36").withCodes("CS2100").build();
    public static final RequirementCategory INFORMATION_SECURITY_REQUIREMENTS =
            new RequirementCategoryBuilder().withName("Information Security Requirements")
                    .withCredits("32").build();
    public static final RequirementCategory INFORMATION_SECURITY_ELECTIVES =
            new RequirementCategoryBuilder().withName("Information Security Electives")
                    .withCredits("12").build();
    public static final RequirementCategory COMPUTING_BREADTH =
            new RequirementCategoryBuilder().withName("Computing Breadth")
                    .withCredits("20").build();
    public static final RequirementCategory IT_PROFESSIONALISM =
            new RequirementCategoryBuilder().withName("IT Professionalism")
                    .withCredits("8").build();
    public static final RequirementCategory MATHEMATICS =
            new RequirementCategoryBuilder().withName("Mathematics")
                    .withCredits("12").build();
    public static final RequirementCategory GENERAL_EDUCATION =
            new RequirementCategoryBuilder().withName("General Education")
                    .withCredits("20").build();
    public static final RequirementCategory UNRESTRICTED_ELECTIVES =
            new RequirementCategoryBuilder().withName("Unrestricted Electives")
                    .withCredits("12").build();

    private TypicalRequirementCategories() {} // prevents instantiation

    /**
     * Returns an {@code AddressBook} with all the typical modules.
     */
    public static ObservableList<RequirementCategory> getTypicalRequirementCategoriesList() {
        AddressBook requirementCategoryList = new AddressBook();
        for (RequirementCategory requirementCategory : getTypicalRequirementCategories()) {
            requirementCategoryList.addRequirementCategory(requirementCategory);
        }
        return requirementCategoryList.getRequirementCategoryList();
    }

    public static List<RequirementCategory> getTypicalRequirementCategories() {
        return new ArrayList<>(
                Arrays.asList(COMPUTING_FOUNDATION, INFORMATION_SECURITY_REQUIREMENTS, INFORMATION_SECURITY_ELECTIVES,
                        COMPUTING_BREADTH, IT_PROFESSIONALISM, MATHEMATICS, GENERAL_EDUCATION, UNRESTRICTED_ELECTIVES));
    }
}
