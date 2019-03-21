package seedu.address.model.module;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CODE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CREDITS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.testutil.TypicalModules.ALICE;
import static seedu.address.testutil.TypicalModules.BOB;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import seedu.address.testutil.ModuleBuilder;

public class ModuleTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void asObservableList_modifyList_throwsUnsupportedOperationException() {
        Module module = new ModuleBuilder().build();
        thrown.expect(UnsupportedOperationException.class);
        module.getTags().remove(0);
    }

    @Test
    public void isSameModule() {
        // same object -> returns true
        assertTrue(ALICE.isSameModule(ALICE));

        // null -> returns false
        assertFalse(ALICE.isSameModule(null));

        // different credits and code -> returns false
        Module editedAlice = new ModuleBuilder(ALICE).withCredits(VALID_CREDITS_BOB).withCode(VALID_CODE_BOB).build();
        assertFalse(ALICE.isSameModule(editedAlice));

        // different name but same code -> returns true
        editedAlice = new ModuleBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertTrue(ALICE.isSameModule(editedAlice));

        // same name, same code, different attributes -> returns true
        editedAlice = new ModuleBuilder(ALICE)
                .withTags(VALID_TAG_HUSBAND).build();
        assertTrue(ALICE.isSameModule(editedAlice));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Module aliceCopy = new ModuleBuilder(ALICE).build();
        assertTrue(ALICE.equals(aliceCopy));

        // same object -> returns true
        assertTrue(ALICE.equals(ALICE));

        // null -> returns false
        assertFalse(ALICE.equals(null));

        // different type -> returns false
        assertFalse(ALICE.equals(5));

        // different module -> returns false
        assertFalse(ALICE.equals(BOB));

        // different name -> returns false
        Module editedAlice = new ModuleBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different credits -> returns false
        editedAlice = new ModuleBuilder(ALICE).withCredits(VALID_CREDITS_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different code -> returns false
        editedAlice = new ModuleBuilder(ALICE).withCode(VALID_CODE_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different tags -> returns false
        editedAlice = new ModuleBuilder(ALICE).withTags(VALID_TAG_HUSBAND).build();
        assertFalse(ALICE.equals(editedAlice));
    }
}
