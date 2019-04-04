package pwe.planner.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static pwe.planner.testutil.TypicalModules.AMY;
import static pwe.planner.testutil.TypicalModules.BOB;
import static pwe.planner.testutil.TypicalModules.CARL;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import pwe.planner.testutil.ApplicationBuilder;

public class VersionedApplicationTest {

    private final ReadOnlyApplication applicationWithAmy = new ApplicationBuilder().withModule(AMY).build();
    private final ReadOnlyApplication applicationWithBob = new ApplicationBuilder().withModule(BOB).build();
    private final ReadOnlyApplication applicationWithCarl = new ApplicationBuilder().withModule(CARL).build();
    private final ReadOnlyApplication emptyapplication = new ApplicationBuilder().build();

    @Test
    public void commit_singleapplication_noStatesRemovedCurrentStateSaved() {
        VersionedApplication versionedapplication = prepareapplicationList(emptyapplication);

        versionedapplication.commit();
        assertapplicationListStatus(versionedapplication,
                Collections.singletonList(emptyapplication),
                emptyapplication,
                Collections.emptyList());
    }

    @Test
    public void commit_multipleapplicationPointerAtEndOfStateList_noStatesRemovedCurrentStateSaved() {
        VersionedApplication versionedapplication = prepareapplicationList(
                emptyapplication, applicationWithAmy, applicationWithBob);

        versionedapplication.commit();
        assertapplicationListStatus(versionedapplication,
                Arrays.asList(emptyapplication, applicationWithAmy, applicationWithBob),
                applicationWithBob,
                Collections.emptyList());
    }

    @Test
    public void commit_multipleapplicationPointerNotAtEndOfStateList_statesAfterPointerRemovedCurrentStateSaved() {
        VersionedApplication versionedapplication = prepareapplicationList(
                emptyapplication, applicationWithAmy, applicationWithBob);
        shiftCurrentStatePointerLeftwards(versionedapplication, 2);

        versionedapplication.commit();
        assertapplicationListStatus(versionedapplication,
                Collections.singletonList(emptyapplication),
                emptyapplication,
                Collections.emptyList());
    }

    @Test
    public void canUndo_multipleapplicationPointerAtEndOfStateList_returnsTrue() {
        VersionedApplication versionedapplication = prepareapplicationList(
                emptyapplication, applicationWithAmy, applicationWithBob);

        assertTrue(versionedapplication.canUndo());
    }

    @Test
    public void canUndo_multipleapplicationPointerAtStartOfStateList_returnsTrue() {
        VersionedApplication versionedapplication = prepareapplicationList(
                emptyapplication, applicationWithAmy, applicationWithBob);
        shiftCurrentStatePointerLeftwards(versionedapplication, 1);

        assertTrue(versionedapplication.canUndo());
    }

    @Test
    public void canUndo_singleapplication_returnsFalse() {
        VersionedApplication versionedapplication = prepareapplicationList(emptyapplication);

        assertFalse(versionedapplication.canUndo());
    }

    @Test
    public void canUndo_multipleapplicationPointerAtStartOfStateList_returnsFalse() {
        VersionedApplication versionedapplication = prepareapplicationList(
                emptyapplication, applicationWithAmy, applicationWithBob);
        shiftCurrentStatePointerLeftwards(versionedapplication, 2);

        assertFalse(versionedapplication.canUndo());
    }

    @Test
    public void canRedo_multipleapplicationPointerNotAtEndOfStateList_returnsTrue() {
        VersionedApplication versionedapplication = prepareapplicationList(
                emptyapplication, applicationWithAmy, applicationWithBob);
        shiftCurrentStatePointerLeftwards(versionedapplication, 1);

        assertTrue(versionedapplication.canRedo());
    }

    @Test
    public void canRedo_multipleapplicationPointerAtStartOfStateList_returnsTrue() {
        VersionedApplication versionedapplication = prepareapplicationList(
                emptyapplication, applicationWithAmy, applicationWithBob);
        shiftCurrentStatePointerLeftwards(versionedapplication, 2);

        assertTrue(versionedapplication.canRedo());
    }

    @Test
    public void canRedo_singleapplication_returnsFalse() {
        VersionedApplication versionedapplication = prepareapplicationList(emptyapplication);

        assertFalse(versionedapplication.canRedo());
    }

    @Test
    public void canRedo_multipleapplicationPointerAtEndOfStateList_returnsFalse() {
        VersionedApplication versionedapplication = prepareapplicationList(
                emptyapplication, applicationWithAmy, applicationWithBob);

        assertFalse(versionedapplication.canRedo());
    }

    @Test
    public void undo_multipleapplicationPointerAtEndOfStateList_success() {
        VersionedApplication versionedapplication = prepareapplicationList(
                emptyapplication, applicationWithAmy, applicationWithBob);

        versionedapplication.undo();
        assertapplicationListStatus(versionedapplication,
                Collections.singletonList(emptyapplication),
                applicationWithAmy,
                Collections.singletonList(applicationWithBob));
    }

    @Test
    public void undo_multipleapplicationPointerNotAtStartOfStateList_success() {
        VersionedApplication versionedapplication = prepareapplicationList(
                emptyapplication, applicationWithAmy, applicationWithBob);
        shiftCurrentStatePointerLeftwards(versionedapplication, 1);

        versionedapplication.undo();
        assertapplicationListStatus(versionedapplication,
                Collections.emptyList(),
                emptyapplication,
                Arrays.asList(applicationWithAmy, applicationWithBob));
    }

    @Test
    public void undo_singleapplication_throwsNoUndoableStateException() {
        VersionedApplication versionedapplication = prepareapplicationList(emptyapplication);

        assertThrows(VersionedApplication.NoUndoableStateException.class, versionedapplication::undo);
    }

    @Test
    public void undo_multipleapplicationPointerAtStartOfStateList_throwsNoUndoableStateException() {
        VersionedApplication versionedapplication = prepareapplicationList(
                emptyapplication, applicationWithAmy, applicationWithBob);
        shiftCurrentStatePointerLeftwards(versionedapplication, 2);

        assertThrows(VersionedApplication.NoUndoableStateException.class, versionedapplication::undo);
    }

    @Test
    public void redo_multipleapplicationPointerNotAtEndOfStateList_success() {
        VersionedApplication versionedapplication = prepareapplicationList(
                emptyapplication, applicationWithAmy, applicationWithBob);
        shiftCurrentStatePointerLeftwards(versionedapplication, 1);

        versionedapplication.redo();
        assertapplicationListStatus(versionedapplication,
                Arrays.asList(emptyapplication, applicationWithAmy),
                applicationWithBob,
                Collections.emptyList());
    }

    @Test
    public void redo_multipleapplicationPointerAtStartOfStateList_success() {
        VersionedApplication versionedapplication = prepareapplicationList(
                emptyapplication, applicationWithAmy, applicationWithBob);
        shiftCurrentStatePointerLeftwards(versionedapplication, 2);

        versionedapplication.redo();
        assertapplicationListStatus(versionedapplication,
                Collections.singletonList(emptyapplication),
                applicationWithAmy,
                Collections.singletonList(applicationWithBob));
    }

    @Test
    public void redo_singleapplication_throwsNoRedoableStateException() {
        VersionedApplication versionedapplication = prepareapplicationList(emptyapplication);

        assertThrows(VersionedApplication.NoRedoableStateException.class, versionedapplication::redo);
    }

    @Test
    public void redo_multipleapplicationPointerAtEndOfStateList_throwsNoRedoableStateException() {
        VersionedApplication versionedapplication = prepareapplicationList(
                emptyapplication, applicationWithAmy, applicationWithBob);

        assertThrows(VersionedApplication.NoRedoableStateException.class, versionedapplication::redo);
    }

    @Test
    public void equals() {
        VersionedApplication versionedapplication = prepareapplicationList(applicationWithAmy, applicationWithBob);

        // same values -> returns true
        VersionedApplication copy = prepareapplicationList(applicationWithAmy, applicationWithBob);
        assertTrue(versionedapplication.equals(copy));

        // same object -> returns true
        assertTrue(versionedapplication.equals(versionedapplication));

        // null -> returns false
        assertFalse(versionedapplication.equals(null));

        // different types -> returns false
        assertFalse(versionedapplication.equals(1));

        // different state list -> returns false
        VersionedApplication differentapplicationList = prepareapplicationList(applicationWithBob, applicationWithCarl);
        assertFalse(versionedapplication.equals(differentapplicationList));

        // different current pointer index -> returns false
        VersionedApplication differentCurrentStatePointer = prepareapplicationList(
                applicationWithAmy, applicationWithBob);
        shiftCurrentStatePointerLeftwards(versionedapplication, 1);
        assertFalse(versionedapplication.equals(differentCurrentStatePointer));
    }

    /**
     * Asserts that {@code versionedapplication} is currently pointing at {@code expectedCurrentState},
     * states before {@code versionedapplication#currentStatePointer} is equal to {@code expectedStatesBeforePointer},
     * and states after {@code versionedapplication#currentStatePointer} is equal to {@code expectedStatesAfterPointer}.
     */
    private void assertapplicationListStatus(VersionedApplication versionedapplication,
            List<ReadOnlyApplication> expectedStatesBeforePointer,
            ReadOnlyApplication expectedCurrentState,
            List<ReadOnlyApplication> expectedStatesAfterPointer) {
        // check state currently pointing at is correct
        assertEquals(new Application(versionedapplication), expectedCurrentState);

        // shift pointer to start of state list
        while (versionedapplication.canUndo()) {
            versionedapplication.undo();
        }

        // check states before pointer are correct
        for (ReadOnlyApplication expectedapplication : expectedStatesBeforePointer) {
            assertEquals(expectedapplication, new Application(versionedapplication));
            versionedapplication.redo();
        }

        // check states after pointer are correct
        for (ReadOnlyApplication expectedapplication : expectedStatesAfterPointer) {
            versionedapplication.redo();
            assertEquals(expectedapplication, new Application(versionedapplication));
        }

        // check that there are no more states after pointer
        assertFalse(versionedapplication.canRedo());

        // revert pointer to original position
        expectedStatesAfterPointer.forEach(unused -> versionedapplication.undo());
    }

    /**
     * Creates and returns a {@code VersionedApplication} with the {@code applicationStates} added into it, and the
     * {@code VersionedApplication#currentStatePointer} at the end of list.
     */
    private VersionedApplication prepareapplicationList(ReadOnlyApplication... applicationStates) {
        assertFalse(applicationStates.length == 0);

        VersionedApplication versionedapplication = new VersionedApplication(applicationStates[0]);
        for (int i = 1; i < applicationStates.length; i++) {
            versionedapplication.resetData(applicationStates[i]);
            versionedapplication.commit();
        }

        return versionedapplication;
    }

    /**
     * Shifts the {@code versionedapplication#currentStatePointer} by {@code count} to the left of its list.
     */
    private void shiftCurrentStatePointerLeftwards(VersionedApplication versionedapplication, int count) {
        for (int i = 0; i < count; i++) {
            versionedapplication.undo();
        }
    }
}
