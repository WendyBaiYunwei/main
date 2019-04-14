package pwe.planner.model.requirement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static pwe.planner.testutil.TypicalRequirementCategories.COMPUTING_BREADTH;
import static pwe.planner.testutil.TypicalRequirementCategories.COMPUTING_FOUNDATION;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import pwe.planner.model.module.Code;
import pwe.planner.model.module.Credits;
import pwe.planner.model.module.Name;
import pwe.planner.model.requirement.exceptions.DuplicateRequirementCategoryException;
import pwe.planner.model.requirement.exceptions.RequirementCategoryNotFoundException;
import pwe.planner.testutil.RequirementCategoryBuilder;

public class UniqueRequirementCategoryListTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final UniqueRequirementCategoryList uniqueRequirementCategoryList = new UniqueRequirementCategoryList();

    @Test
    public void contains_nullRequirementCategory_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        uniqueRequirementCategoryList.contains((RequirementCategory) null);
    }

    @Test
    public void contains_nullRequirementCategoryName_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        uniqueRequirementCategoryList.contains((Name) null);
    }

    @Test
    public void getRequirementCategoryByName_nullName_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        uniqueRequirementCategoryList.getRequirementCategory(null);
    }

    @Test
    public void contains_requirementCategoryNameNotInList_returnsFalse() {
        assertFalse(uniqueRequirementCategoryList.contains(new Name("SOMETHING")));
    }

    @Test
    public void contains_requirementCategoryNotInList_returnsFalse() {
        Set<Code> codeSet = Set.of(new Code("CS2100"));
        RequirementCategory requirementCategory =
                new RequirementCategory(new Name("SOMETHING"), new Credits("12"), codeSet);
        assertFalse(uniqueRequirementCategoryList.contains(requirementCategory));
    }


    @Test
    public void contains_requirementCategoryInList_returnsTrue() {
        uniqueRequirementCategoryList.add(COMPUTING_FOUNDATION);
        assertTrue(uniqueRequirementCategoryList.contains(COMPUTING_FOUNDATION));
    }

    @Test
    public void add_nullRequirementCategory_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        uniqueRequirementCategoryList.add(null);
    }

    @Test
    public void add_duplicateRequirementCategory_throwsDuplicateRequirementCategoryException() {
        uniqueRequirementCategoryList.add(COMPUTING_FOUNDATION);
        thrown.expect(DuplicateRequirementCategoryException.class);
        uniqueRequirementCategoryList.add(COMPUTING_FOUNDATION);
    }

    @Test
    public void setRequirementCategory_nullTargetRequirementCategory_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        uniqueRequirementCategoryList.setRequirementCategory(null, COMPUTING_FOUNDATION);
    }

    @Test
    public void setRequirementCategory_nullEditedRequirementCategory_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        uniqueRequirementCategoryList.setRequirementCategory(COMPUTING_FOUNDATION, null);
    }

    @Test
    public void setRequirementCategory_targetRequirementCategoryNotInList_throwsRequirementCategoryNotFoundException() {
        thrown.expect(RequirementCategoryNotFoundException.class);
        uniqueRequirementCategoryList.setRequirementCategory(COMPUTING_FOUNDATION, COMPUTING_FOUNDATION);
    }

    @Test
    public void setRequirementCategory_editedRequirementCategoryIsSameRequirementCategory_success() {
        uniqueRequirementCategoryList.add(COMPUTING_FOUNDATION);
        uniqueRequirementCategoryList.setRequirementCategory(COMPUTING_FOUNDATION, COMPUTING_FOUNDATION);
        UniqueRequirementCategoryList expectedRequirementCategoryList = new UniqueRequirementCategoryList();
        expectedRequirementCategoryList.add(COMPUTING_FOUNDATION);
        assertEquals(expectedRequirementCategoryList, uniqueRequirementCategoryList);
    }

    @Test
    public void setRequirementCategory_editedRequirementCategoryHasSameIdentity_success() {
        uniqueRequirementCategoryList.add(COMPUTING_FOUNDATION);
        RequirementCategory editedRequirementCategory =
                new RequirementCategoryBuilder(COMPUTING_FOUNDATION).withCodes("CS9999").build();
        uniqueRequirementCategoryList.setRequirementCategory(COMPUTING_FOUNDATION, editedRequirementCategory);
        UniqueRequirementCategoryList expectedRequirementCategoryList = new UniqueRequirementCategoryList();
        expectedRequirementCategoryList.add(editedRequirementCategory);
        assertEquals(expectedRequirementCategoryList, uniqueRequirementCategoryList);
    }

    @Test
    public void setRequirementCategory_editedRequirementCategoryHasDifferentIdentity_success() {
        uniqueRequirementCategoryList.add(COMPUTING_FOUNDATION);
        uniqueRequirementCategoryList.setRequirementCategory(COMPUTING_FOUNDATION, COMPUTING_BREADTH);
        UniqueRequirementCategoryList expectedRequirementCategoryList = new UniqueRequirementCategoryList();
        expectedRequirementCategoryList.add(COMPUTING_BREADTH);
        assertEquals(expectedRequirementCategoryList, uniqueRequirementCategoryList);
    }

    @Test
    public void setRequirementCategory_editedRequirementCategoryNonUniqueIdentity_throwsDuplicateReqCatException() {
        uniqueRequirementCategoryList.add(COMPUTING_FOUNDATION);
        uniqueRequirementCategoryList.add(COMPUTING_BREADTH);
        thrown.expect(DuplicateRequirementCategoryException.class);
        uniqueRequirementCategoryList.setRequirementCategory(COMPUTING_FOUNDATION, COMPUTING_BREADTH);
    }

    @Test
    public void remove_nullRequirementCategory_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        uniqueRequirementCategoryList.remove(null);
    }

    @Test
    public void remove_requirementCategoryDoesNotExist_throwsRequirementCategoryNotFoundException() {
        thrown.expect(RequirementCategoryNotFoundException.class);
        uniqueRequirementCategoryList.remove(COMPUTING_FOUNDATION);
    }

    @Test
    public void remove_existingRequirementCategory_removesRequirementCategory() {
        uniqueRequirementCategoryList.add(COMPUTING_FOUNDATION);
        uniqueRequirementCategoryList.remove(COMPUTING_FOUNDATION);
        UniqueRequirementCategoryList expectedRequirementCategoryList = new UniqueRequirementCategoryList();
        assertEquals(expectedRequirementCategoryList, uniqueRequirementCategoryList);
    }

    @Test
    public void setRequirementCategories_nullUniqueRequirementCategoryList_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        uniqueRequirementCategoryList.setRequirementCategories((UniqueRequirementCategoryList) null);
    }

    @Test
    public void setRequirementCategories_uniqueRequirementCategoryList_replacesOwnListWithProvidedUniqueReqCatList() {
        uniqueRequirementCategoryList.add(COMPUTING_FOUNDATION);
        UniqueRequirementCategoryList expectedRequirementCategoryList = new UniqueRequirementCategoryList();
        expectedRequirementCategoryList.add(COMPUTING_BREADTH);
        uniqueRequirementCategoryList.setRequirementCategories(expectedRequirementCategoryList);
        assertEquals(expectedRequirementCategoryList, uniqueRequirementCategoryList);
    }

    @Test
    public void setRequirementCategories_nullList_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        uniqueRequirementCategoryList.setRequirementCategories((List<RequirementCategory>) null);
    }

    @Test
    public void setRequirementCategories_list_replacesOwnListWithProvidedList() {
        uniqueRequirementCategoryList.add(COMPUTING_FOUNDATION);
        List<RequirementCategory> requirementCategoryList = Collections.singletonList(COMPUTING_BREADTH);
        uniqueRequirementCategoryList.setRequirementCategories(requirementCategoryList);
        UniqueRequirementCategoryList expectedRequirementCategoryList = new UniqueRequirementCategoryList();
        expectedRequirementCategoryList.add(COMPUTING_BREADTH);
        assertEquals(expectedRequirementCategoryList, uniqueRequirementCategoryList);
    }

    @Test
    public void setRequirementCategories_listWithDuplicateRequirementCategory_throwsDuplicateReqCatException() {
        List<RequirementCategory> listWithRequirementCategory =
                Arrays.asList(COMPUTING_FOUNDATION, COMPUTING_FOUNDATION);
        thrown.expect(DuplicateRequirementCategoryException.class);
        uniqueRequirementCategoryList.setRequirementCategories(listWithRequirementCategory);
    }

    @Test
    public void asUnmodifiableObservableList_modifyList_throwsUnsupportedOperationException() {
        thrown.expect(UnsupportedOperationException.class);
        uniqueRequirementCategoryList.asUnmodifiableObservableList().remove(0);
    }
}
