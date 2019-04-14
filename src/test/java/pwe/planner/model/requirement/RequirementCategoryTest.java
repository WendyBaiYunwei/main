package pwe.planner.model.requirement;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static pwe.planner.testutil.TypicalRequirementCategories.COMPUTING_BREADTH;
import static pwe.planner.testutil.TypicalRequirementCategories.COMPUTING_FOUNDATION;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import pwe.planner.testutil.RequirementCategoryBuilder;

public class RequirementCategoryTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void asObservableList_modifyList_throwsUnsupportedOperationException() {
        RequirementCategory requirementCategory = new RequirementCategoryBuilder().build();
        thrown.expect(UnsupportedOperationException.class);
        requirementCategory.getCodeSet().remove(0);
    }

    @Test
    public void isSameRequirementCategory() {
        // same object -> true
        assertTrue(COMPUTING_FOUNDATION.isSameRequirementCategory(COMPUTING_FOUNDATION));

        // null -> false
        assertFalse(COMPUTING_FOUNDATION.isSameRequirementCategory(null));

        // different name, credits and code -> false
        RequirementCategory editedRequirementCategory =
                new RequirementCategoryBuilder().withName("DIFFERENT").withCredits("14").withCodes("CS9999").build();
        assertFalse(COMPUTING_FOUNDATION.isSameRequirementCategory(editedRequirementCategory));

        // different name, credits and same code -> false
        editedRequirementCategory =
                new RequirementCategoryBuilder(COMPUTING_FOUNDATION).withName("DIFFERENT").withCredits("14").build();
        assertFalse(COMPUTING_FOUNDATION.isSameRequirementCategory(editedRequirementCategory));

        // different name, code and same credits -> false
        editedRequirementCategory =
                new RequirementCategoryBuilder(COMPUTING_FOUNDATION).withName("DIFFERENT").withCodes("CS1231").build();
        assertFalse(COMPUTING_FOUNDATION.isSameRequirementCategory(editedRequirementCategory));

        // different name and same code, credits -> false
        editedRequirementCategory =
                new RequirementCategoryBuilder(COMPUTING_FOUNDATION).withName("DIFFERENT").build();
        assertFalse(COMPUTING_FOUNDATION.isSameRequirementCategory(editedRequirementCategory));

        // different credits, code and same name -> false
        editedRequirementCategory =
                new RequirementCategoryBuilder(COMPUTING_FOUNDATION).withCredits("14").withCodes("CS9999").build();
        assertTrue(COMPUTING_FOUNDATION.isSameRequirementCategory(editedRequirementCategory));
    }

    @Test
    public void equals() {
        // same values -> true
        RequirementCategory requirementCategoryCopy = new RequirementCategoryBuilder(COMPUTING_FOUNDATION).build();
        assertTrue(COMPUTING_FOUNDATION.equals(requirementCategoryCopy));

        // same object -> true
        assertTrue(COMPUTING_FOUNDATION.equals(COMPUTING_FOUNDATION));

        // different type -> false
        assertFalse(COMPUTING_FOUNDATION.equals(3));

        // different requirement category -> false
        assertFalse(COMPUTING_FOUNDATION.equals(COMPUTING_BREADTH));

        // different name -> false
        RequirementCategory editedRequirementCategory =
                new RequirementCategoryBuilder(COMPUTING_FOUNDATION).withName("DIFFERENT").build();
        assertFalse(COMPUTING_FOUNDATION.equals(editedRequirementCategory));

        // different credits -> false
        editedRequirementCategory = new RequirementCategoryBuilder(COMPUTING_FOUNDATION).withCredits("14").build();
        assertFalse(COMPUTING_FOUNDATION.equals(editedRequirementCategory));

        // different codes -> false
        editedRequirementCategory = new RequirementCategoryBuilder(COMPUTING_FOUNDATION).withCodes("CS9999").build();
        assertFalse(COMPUTING_FOUNDATION.equals(editedRequirementCategory));
    }

}
