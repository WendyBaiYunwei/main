package seedu.address.model.module;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.testutil.TypicalModules.ALICE;
import static seedu.address.testutil.TypicalModules.BOB;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import seedu.address.model.module.exceptions.DuplicateModuleException;
import seedu.address.model.module.exceptions.ModuleNotFoundException;
import seedu.address.testutil.ModuleBuilder;

public class UniquePlannerListTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final UniquePlannerList uniquePlannerList = new UniquePlannerList();

    @Test
    public void contains_nullModule_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        uniquePlannerList.plannerContains(null);
    }

    @Test
    public void contains_moduleNotInList_returnsFalse() {
        assertFalse(uniquePlannerList.plannerContains(ALICE));
    }

    @Test
    public void contains_moduleInList_returnsTrue() {
        uniquePlannerList.plannerAdd(ALICE);
        assertTrue(uniquePlannerList.plannerContains(ALICE));
    }

    @Test
    public void contains_moduleWithSameIdentityFieldsInList_returnsTrue() {
        uniquePlannerList.plannerAdd(ALICE);
        Module editedAlice = new ModuleBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        assertTrue(uniquePlannerList.plannerContains(editedAlice));
    }

    @Test
    public void plannerAdd_nullModule_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        uniquePlannerList.plannerAdd(null);
    }

    @Test
    public void plannerAdd_duplicateModule_throwsDuplicateModuleException() {
        uniquePlannerList.plannerAdd(ALICE);
        thrown.expect(DuplicateModuleException.class);
        uniquePlannerList.plannerAdd(ALICE);
    }

    @Test
    public void setModule_nullTargetModule_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        uniquePlannerList.plannerSetModule(null, ALICE);
    }

    @Test
    public void setModule_nullEditedModule_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        uniquePlannerList.plannerSetModule(ALICE, null);
    }

    @Test
    public void setModule_targetModuleNotInList_throwsModuleNotFoundException() {
        thrown.expect(ModuleNotFoundException.class);
        uniquePlannerList.plannerSetModule(ALICE, ALICE);
    }

    @Test
    public void setModule_editedModuleIsSameModule_success() {
        uniquePlannerList.plannerAdd(ALICE);
        uniquePlannerList.plannerSetModule(ALICE, ALICE);
        UniquePlannerList expectedUniquePlannerList = new UniquePlannerList();
        expectedUniquePlannerList.plannerAdd(ALICE);
        assertEquals(expectedUniquePlannerList, uniquePlannerList);
    }

    @Test
    public void setModule_editedModuleHasSameIdentity_success() {
        uniquePlannerList.plannerAdd(ALICE);
        Module editedAlice = new ModuleBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        uniquePlannerList.plannerSetModule(ALICE, editedAlice);
        UniquePlannerList expectedUniquePlannerList = new UniquePlannerList();
        expectedUniquePlannerList.plannerAdd(editedAlice);
        assertEquals(expectedUniquePlannerList, uniquePlannerList);
    }

    @Test
    public void setModule_editedModuleHasDifferentIdentity_success() {
        uniquePlannerList.plannerAdd(ALICE);
        uniquePlannerList.plannerSetModule(ALICE, BOB);
        UniquePlannerList expectedUniquePlannerList = new UniquePlannerList();
        expectedUniquePlannerList.plannerAdd(BOB);
        assertEquals(expectedUniquePlannerList, uniquePlannerList);
    }

    @Test
    public void setModule_editedModuleHasNonUniqueIdentity_throwsDuplicateModuleException() {
        uniquePlannerList.plannerAdd(ALICE);
        uniquePlannerList.plannerAdd(BOB);
        thrown.expect(DuplicateModuleException.class);
        uniquePlannerList.plannerSetModule(ALICE, BOB);
    }

    @Test
    public void plannerRemove_nullModule_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        uniquePlannerList.plannerRemove(null);
    }

    @Test
    public void plannerRemove_moduleDoesNotExist_throwsModuleNotFoundException() {
        thrown.expect(ModuleNotFoundException.class);
        uniquePlannerList.plannerRemove(ALICE);
    }

    @Test
    public void plannerRemove_existingModule_plannerRemovesModule() {
        uniquePlannerList.plannerAdd(ALICE);
        uniquePlannerList.plannerRemove(ALICE);
        UniquePlannerList expectedUniquePlannerList = new UniquePlannerList();
        assertEquals(expectedUniquePlannerList, uniquePlannerList);
    }

    @Test
    public void plannerSetModules_nullUniquePlannerList_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        uniquePlannerList.plannerSetModules((UniquePlannerList) null);
    }

    @Test
    public void plannerSetModules_uniquePlannerList_replacesOwnListWithProvidedUniquePlannerList() {
        uniquePlannerList.plannerAdd(ALICE);
        UniquePlannerList expectedUniquePlannerList = new UniquePlannerList();
        expectedUniquePlannerList.plannerAdd(BOB);
        uniquePlannerList.plannerSetModules(expectedUniquePlannerList);
        assertEquals(expectedUniquePlannerList, uniquePlannerList);
    }

    @Test
    public void plannerSetModules_nullList_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        uniquePlannerList.plannerSetModules((List<Module>) null);
    }

    @Test
    public void plannerSetModules_list_replacesOwnListWithProvidedList() {
        uniquePlannerList.plannerAdd(ALICE);
        List<Module> moduleList = Collections.singletonList(BOB);
        uniquePlannerList.plannerSetModules(moduleList);
        UniquePlannerList expectedUniquePlannerList = new UniquePlannerList();
        expectedUniquePlannerList.plannerAdd(BOB);
        assertEquals(expectedUniquePlannerList, uniquePlannerList);
    }

    @Test
    public void plannerSetModules_listWithDuplicateModules_throwsDuplicateModuleException() {
        List<Module> listWithDuplicateModules = Arrays.asList(ALICE, ALICE);
        thrown.expect(DuplicateModuleException.class);
        uniquePlannerList.plannerSetModules(listWithDuplicateModules);
    }

    @Test
    public void asPlannerUnmodifiableObservableList_modifyList_throwsUnsupportedOperationException() {
        thrown.expect(UnsupportedOperationException.class);
        uniquePlannerList.asPlannerUnmodifiableObservableList().remove(0);
    }
}
