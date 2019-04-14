package pwe.planner.model.planner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static pwe.planner.testutil.TypicalDegreePlanners.YEAR_1_SEMESTER_1;
import static pwe.planner.testutil.TypicalDegreePlanners.YEAR_1_SEMESTER_2;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import pwe.planner.model.planner.exceptions.DegreePlannerNotFoundException;
import pwe.planner.model.planner.exceptions.DuplicateDegreePlannerException;
import pwe.planner.testutil.DegreePlannerBuilder;

public class UniqueDegreePlannerListTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final UniqueDegreePlannerList uniqueDegreePlannerList = new UniqueDegreePlannerList();

    @Test
    public void contains_nullDegreePlanner_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        uniqueDegreePlannerList.contains(null);
    }

    @Test
    public void getDegreePlannerByCode_nullCode_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        uniqueDegreePlannerList.getDegreePlannerByCode(null);
    }

    @Test
    public void contains_degreePlannerNotInList_returnsFalse() {
        assertFalse(uniqueDegreePlannerList.contains(YEAR_1_SEMESTER_1));
    }

    @Test
    public void contains_degreePlannerInList_returnsTrue() {
        uniqueDegreePlannerList.add(YEAR_1_SEMESTER_1);
        assertTrue(uniqueDegreePlannerList.contains(YEAR_1_SEMESTER_1));
    }

    @Test
    public void add_nullDegreePlanner_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        uniqueDegreePlannerList.add(null);
    }

    @Test
    public void add_duplicateDegreePlanner_throwsDuplicateDegreePlannerException() {
        uniqueDegreePlannerList.add(YEAR_1_SEMESTER_1);
        thrown.expect(DuplicateDegreePlannerException.class);
        uniqueDegreePlannerList.add(YEAR_1_SEMESTER_1);
    }

    @Test
    public void setDegreePlanner_nullTargetDegreePlanner_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        uniqueDegreePlannerList.setDegreePlanner(null, YEAR_1_SEMESTER_1);
    }

    @Test
    public void setDegreePlanner_nullEditedDegreePlanner_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        uniqueDegreePlannerList.setDegreePlanner(YEAR_1_SEMESTER_1, null);
    }

    @Test
    public void setDegreePlanner_targetDegreePlannerNotInList_throwsDegreePlannerNotFoundException() {
        thrown.expect(DegreePlannerNotFoundException.class);
        uniqueDegreePlannerList.setDegreePlanner(YEAR_1_SEMESTER_1, YEAR_1_SEMESTER_1);
    }

    @Test
    public void setDegreePlanner_editedDegreePlannerIsSameDegreePlanner_success() {
        uniqueDegreePlannerList.add(YEAR_1_SEMESTER_1);
        uniqueDegreePlannerList.setDegreePlanner(YEAR_1_SEMESTER_1, YEAR_1_SEMESTER_1);
        UniqueDegreePlannerList expectedUniqueDegreePlannerList = new UniqueDegreePlannerList();
        expectedUniqueDegreePlannerList.add(YEAR_1_SEMESTER_1);
        assertEquals(expectedUniqueDegreePlannerList, uniqueDegreePlannerList);
    }

    @Test
    public void setDegreePlanner_editedDegreePlannerHasSameIdentity_success() {
        uniqueDegreePlannerList.add(YEAR_1_SEMESTER_1);
        DegreePlanner editedDegreePlanner =
                new DegreePlannerBuilder(YEAR_1_SEMESTER_1).withCodes("CS1111", "CS2222").build();
        uniqueDegreePlannerList.setDegreePlanner(YEAR_1_SEMESTER_1, editedDegreePlanner);
        UniqueDegreePlannerList expectedUniqueDegreePlannerList = new UniqueDegreePlannerList();
        expectedUniqueDegreePlannerList.add(editedDegreePlanner);
        assertEquals(expectedUniqueDegreePlannerList, uniqueDegreePlannerList);
    }

    @Test
    public void setDegreePlanner_editedDegreePlannerHasDifferentIdentity_success() {
        uniqueDegreePlannerList.add(YEAR_1_SEMESTER_1);
        uniqueDegreePlannerList.setDegreePlanner(YEAR_1_SEMESTER_1, YEAR_1_SEMESTER_2);
        UniqueDegreePlannerList expectedUniqueDegreePlannerList = new UniqueDegreePlannerList();
        expectedUniqueDegreePlannerList.add(YEAR_1_SEMESTER_2);
        assertEquals(expectedUniqueDegreePlannerList, uniqueDegreePlannerList);
    }

    @Test
    public void setDegreePlanner_editedDegreePlannerHasNonUniqueIdentity_throwsDuplicateDegreePlannerException() {
        uniqueDegreePlannerList.add(YEAR_1_SEMESTER_1);
        uniqueDegreePlannerList.add(YEAR_1_SEMESTER_2);
        thrown.expect(DuplicateDegreePlannerException.class);
        uniqueDegreePlannerList.setDegreePlanner(YEAR_1_SEMESTER_1, YEAR_1_SEMESTER_2);
    }

    @Test
    public void remove_nullDegreePlanner_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        uniqueDegreePlannerList.remove(null);
    }

    @Test
    public void remove_degreePlannerDoesNotExist_throwsDegreePlannerNotFoundException() {
        thrown.expect(DegreePlannerNotFoundException.class);
        uniqueDegreePlannerList.remove(YEAR_1_SEMESTER_1);
    }

    @Test
    public void remove_existingDegreePlanner_removesDegreePlanner() {
        uniqueDegreePlannerList.add(YEAR_1_SEMESTER_1);
        uniqueDegreePlannerList.remove(YEAR_1_SEMESTER_1);
        UniqueDegreePlannerList expectedUniqueDegreePlannerList = new UniqueDegreePlannerList();
        assertEquals(expectedUniqueDegreePlannerList, uniqueDegreePlannerList);
    }

    @Test
    public void setDegreePlanners_nullUniqueDegreePlannerList_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        uniqueDegreePlannerList.setDegreePlanners((UniqueDegreePlannerList) null);
    }

    @Test
    public void setDegreePlanners_uniqueDegreePlannerList_replacesOwnListWithProvidedUniqueDegreePlannerList() {
        uniqueDegreePlannerList.add(YEAR_1_SEMESTER_1);
        UniqueDegreePlannerList expectedUniqueDegreePlannerList = new UniqueDegreePlannerList();
        expectedUniqueDegreePlannerList.add(YEAR_1_SEMESTER_2);
        uniqueDegreePlannerList.setDegreePlanners(expectedUniqueDegreePlannerList);
        assertEquals(expectedUniqueDegreePlannerList, uniqueDegreePlannerList);
    }

    @Test
    public void setDegreePlanners_nullList_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        uniqueDegreePlannerList.setDegreePlanners((List<DegreePlanner>) null);
    }

    @Test
    public void setDegreePlanners_list_replacesOwnListWithProvidedList() {
        uniqueDegreePlannerList.add(YEAR_1_SEMESTER_1);
        List<DegreePlanner> degreePlannerList = Collections.singletonList(YEAR_1_SEMESTER_2);
        uniqueDegreePlannerList.setDegreePlanners(degreePlannerList);
        UniqueDegreePlannerList expectedUniqueDegreePlannerList = new UniqueDegreePlannerList();
        expectedUniqueDegreePlannerList.add(YEAR_1_SEMESTER_2);
        assertEquals(expectedUniqueDegreePlannerList, uniqueDegreePlannerList);
    }

    @Test
    public void setDegreePlanners_listWithDuplicateDegreePlanners_throwsDuplicateDegreePlannerException() {
        List<DegreePlanner> listWithDuplicateDegreePlanners = Arrays.asList(YEAR_1_SEMESTER_1, YEAR_1_SEMESTER_1);
        thrown.expect(DuplicateDegreePlannerException.class);
        uniqueDegreePlannerList.setDegreePlanners(listWithDuplicateDegreePlanners);
    }

    @Test
    public void asUnmodifiableObservableList_modifyList_throwsUnsupportedOperationException() {
        thrown.expect(UnsupportedOperationException.class);
        uniqueDegreePlannerList.asUnmodifiableObservableList().remove(0);
    }
}
