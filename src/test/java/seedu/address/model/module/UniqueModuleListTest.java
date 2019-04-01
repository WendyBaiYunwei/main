package seedu.address.model.module;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CODE_BOB;
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

public class UniqueModuleListTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final UniqueModuleList uniqueModuleList = new UniqueModuleList();

    @Test
    public void contains_nullModule_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        uniqueModuleList.contains(null);
    }

    @Test
    public void getModuleByCode_nullCode_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        uniqueModuleList.getModuleByCode(null);
    }

    @Test
    public void contains_moduleNotInList_returnsFalse() {
        assertFalse(uniqueModuleList.contains(ALICE));
    }

    @Test
    public void contains_moduleInList_returnsTrue() {
        uniqueModuleList.add(ALICE);
        assertTrue(uniqueModuleList.contains(ALICE));
    }

    @Test
    public void add_nullModule_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        uniqueModuleList.add(null);
    }

    @Test
    public void add_duplicateModule_throwsDuplicateModuleException() {
        uniqueModuleList.add(ALICE);
        thrown.expect(DuplicateModuleException.class);
        uniqueModuleList.add(ALICE);
    }

    @Test
    public void setModule_nullTargetModule_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        uniqueModuleList.setModule(null, ALICE);
    }

    @Test
    public void setModule_nullEditedModule_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        uniqueModuleList.setModule(ALICE, null);
    }

    @Test
    public void setModule_targetModuleNotInList_throwsModuleNotFoundException() {
        thrown.expect(ModuleNotFoundException.class);
        uniqueModuleList.setModule(ALICE, ALICE);
    }

    @Test
    public void setModule_editedModuleIsSameModule_success() {
        uniqueModuleList.add(ALICE);
        uniqueModuleList.setModule(ALICE, ALICE);
        UniqueModuleList expectedUniqueModuleList = new UniqueModuleList();
        expectedUniqueModuleList.add(ALICE);
        assertEquals(expectedUniqueModuleList, uniqueModuleList);
    }

    @Test
    public void setModule_editedModuleHasSameIdentity_success() {
        uniqueModuleList.add(ALICE);
        Module editedAlice = new ModuleBuilder(ALICE).withCode(VALID_CODE_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        uniqueModuleList.setModule(ALICE, editedAlice);
        UniqueModuleList expectedUniqueModuleList = new UniqueModuleList();
        expectedUniqueModuleList.add(editedAlice);
        assertEquals(expectedUniqueModuleList, uniqueModuleList);
    }

    @Test
    public void setModule_editedModuleHasDifferentIdentity_success() {
        uniqueModuleList.add(ALICE);
        uniqueModuleList.setModule(ALICE, BOB);
        UniqueModuleList expectedUniqueModuleList = new UniqueModuleList();
        expectedUniqueModuleList.add(BOB);
        assertEquals(expectedUniqueModuleList, uniqueModuleList);
    }

    @Test
    public void setModule_editedModuleHasNonUniqueIdentity_throwsDuplicateModuleException() {
        uniqueModuleList.add(ALICE);
        uniqueModuleList.add(BOB);
        thrown.expect(DuplicateModuleException.class);
        uniqueModuleList.setModule(ALICE, BOB);
    }

    @Test
    public void remove_nullModule_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        uniqueModuleList.remove(null);
    }

    @Test
    public void remove_moduleDoesNotExist_throwsModuleNotFoundException() {
        thrown.expect(ModuleNotFoundException.class);
        uniqueModuleList.remove(ALICE);
    }

    @Test
    public void remove_existingModule_removesModule() {
        uniqueModuleList.add(ALICE);
        uniqueModuleList.remove(ALICE);
        UniqueModuleList expectedUniqueModuleList = new UniqueModuleList();
        assertEquals(expectedUniqueModuleList, uniqueModuleList);
    }

    @Test
    public void setModules_nullUniqueModuleList_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        uniqueModuleList.setModules((UniqueModuleList) null);
    }

    @Test
    public void setModules_uniqueModuleList_replacesOwnListWithProvidedUniqueModuleList() {
        uniqueModuleList.add(ALICE);
        UniqueModuleList expectedUniqueModuleList = new UniqueModuleList();
        expectedUniqueModuleList.add(BOB);
        uniqueModuleList.setModules(expectedUniqueModuleList);
        assertEquals(expectedUniqueModuleList, uniqueModuleList);
    }

    @Test
    public void setModules_nullList_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        uniqueModuleList.setModules((List<Module>) null);
    }

    @Test
    public void setModules_list_replacesOwnListWithProvidedList() {
        uniqueModuleList.add(ALICE);
        List<Module> moduleList = Collections.singletonList(BOB);
        uniqueModuleList.setModules(moduleList);
        UniqueModuleList expectedUniqueModuleList = new UniqueModuleList();
        expectedUniqueModuleList.add(BOB);
        assertEquals(expectedUniqueModuleList, uniqueModuleList);
    }

    @Test
    public void setModules_listWithDuplicateModules_throwsDuplicateModuleException() {
        List<Module> listWithDuplicateModules = Arrays.asList(ALICE, ALICE);
        thrown.expect(DuplicateModuleException.class);
        uniqueModuleList.setModules(listWithDuplicateModules);
    }

    @Test
    public void asUnmodifiableObservableList_modifyList_throwsUnsupportedOperationException() {
        thrown.expect(UnsupportedOperationException.class);
        uniqueModuleList.asUnmodifiableObservableList().remove(0);
    }
}
