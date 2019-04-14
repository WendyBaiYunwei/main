package pwe.planner.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static pwe.planner.logic.commands.CommandTestUtil.VALID_CODE_BOB;
import static pwe.planner.model.Model.PREDICATE_SHOW_ALL_MODULES;
import static pwe.planner.testutil.TypicalModules.ALICE;
import static pwe.planner.testutil.TypicalModules.BENSON;
import static pwe.planner.testutil.TypicalModules.BOB;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import pwe.planner.commons.core.GuiSettings;
import pwe.planner.model.module.Module;
import pwe.planner.model.module.NameContainsKeywordsPredicate;
import pwe.planner.model.module.exceptions.ModuleNotFoundException;
import pwe.planner.testutil.ApplicationBuilder;
import pwe.planner.testutil.ModuleBuilder;

public class ModelManagerTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ModelManager modelManager = new ModelManager();

    @Test
    public void constructor() {
        assertEquals(new UserPrefs(), modelManager.getUserPrefs());
        assertEquals(new GuiSettings(), modelManager.getGuiSettings());
        assertEquals(new Application(), new Application(modelManager.getApplication()));
        assertEquals(null, modelManager.getSelectedModule());
    }

    @Test
    public void setUserPrefs_nullUserPrefs_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        modelManager.setUserPrefs(null);
    }

    @Test
    public void setUserPrefs_validUserPrefs_copiesUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setModuleListFilePath(Paths.get("pwe/planner/file/path"));
        userPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        modelManager.setUserPrefs(userPrefs);
        assertEquals(userPrefs, modelManager.getUserPrefs());

        // Modifying userPrefs should not modify modelManager's userPrefs
        UserPrefs oldUserPrefs = new UserPrefs(userPrefs);
        userPrefs.setModuleListFilePath(Paths.get("new/pwe/planner/file/path"));
        assertEquals(oldUserPrefs, modelManager.getUserPrefs());
    }

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        modelManager.setGuiSettings(null);
    }

    @Test
    public void setGuiSettings_validGuiSettings_setsGuiSettings() {
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4);
        modelManager.setGuiSettings(guiSettings);
        assertEquals(guiSettings, modelManager.getGuiSettings());
    }

    @Test
    public void setModuleListFilePath_nullPath_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        modelManager.setModuleListFilePath(null);
    }

    @Test
    public void setModuleListFilePath_validPath_setsModuleListFilePath() {
        Path path = Paths.get("pwe/planner/file/path");
        modelManager.setModuleListFilePath(path);
        assertEquals(path, modelManager.getModuleListFilePath());
    }

    @Test
    public void hasModule_nullModule_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        modelManager.hasModule(null);
    }

    @Test
    public void getModuleByCode_nullCode_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        modelManager.getModuleByCode(null);
    }

    @Test
    public void hasModule_moduleNotInapplication_returnsFalse() {
        assertFalse(modelManager.hasModule(ALICE));
    }

    @Test
    public void hasModule_moduleInapplication_returnsTrue() {
        modelManager.addModule(ALICE);
        assertTrue(modelManager.hasModule(ALICE));
    }

    @Test
    public void deleteModule_moduleIsSelectedAndFirstModuleInFilteredModuleList_selectionCleared() {
        modelManager.addModule(ALICE);
        modelManager.setSelectedModule(ALICE);
        modelManager.deleteModule(ALICE);
        assertEquals(null, modelManager.getSelectedModule());
    }

    @Test
    public void deleteModule_moduleIsSelectedAndSecondModuleInFilteredModuleList_firstModuleSelected() {
        modelManager.addModule(ALICE);
        modelManager.addModule(BOB);
        assertEquals(Arrays.asList(ALICE, BOB), modelManager.getFilteredModuleList());
        modelManager.setSelectedModule(BOB);
        modelManager.deleteModule(BOB);
        assertEquals(ALICE, modelManager.getSelectedModule());
    }

    @Test
    public void setModule_moduleIsSelected_selectedModuleUpdated() {
        modelManager.addModule(ALICE);
        modelManager.setSelectedModule(ALICE);
        Module updatedAlice = new ModuleBuilder(ALICE).withCode(VALID_CODE_BOB).build();
        modelManager.editModule(ALICE, updatedAlice);
        assertEquals(updatedAlice, modelManager.getSelectedModule());
    }

    @Test
    public void getFilteredModuleList_modifyList_throwsUnsupportedOperationException() {
        thrown.expect(UnsupportedOperationException.class);
        modelManager.getFilteredModuleList().remove(0);
    }

    @Test
    public void setSelectedModule_moduleNotInFilteredModuleList_throwsModuleNotFoundException() {
        thrown.expect(ModuleNotFoundException.class);
        modelManager.setSelectedModule(ALICE);
    }

    @Test
    public void setSelectedModule_moduleInFilteredModuleList_setsSelectedModule() {
        modelManager.addModule(ALICE);
        assertEquals(Collections.singletonList(ALICE), modelManager.getFilteredModuleList());
        modelManager.setSelectedModule(ALICE);
        assertEquals(ALICE, modelManager.getSelectedModule());
    }

    @Test
    public void equals() {
        Application application = new ApplicationBuilder().withModule(ALICE).withModule(BENSON).build();
        Application differentApplication = new Application();
        UserPrefs userPrefs = new UserPrefs();

        // same values -> returns true
        modelManager = new ModelManager(application, userPrefs);
        ModelManager modelManagerCopy = new ModelManager(application, userPrefs);
        assertTrue(modelManager.equals(modelManagerCopy));

        // same object -> returns true
        assertTrue(modelManager.equals(modelManager));

        // null -> returns false
        assertFalse(modelManager.equals(null));

        // different types -> returns false
        assertFalse(modelManager.equals(5));

        // different application -> returns false

        assertFalse(modelManager.equals(new ModelManager(differentApplication, userPrefs)));

        // different filteredList -> returns false
        String[] keywords = ALICE.getName().fullName.split("\\s+");
        modelManager.updateFilteredModuleList(new NameContainsKeywordsPredicate<>(Arrays.asList(keywords)));

        assertFalse(modelManager
                .equals(new ModelManager(application, userPrefs)));

        // resets modelManager to initial state for upcoming tests
        modelManager.updateFilteredModuleList(PREDICATE_SHOW_ALL_MODULES);

        // different userPrefs -> returns false
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setModuleListFilePath(Paths.get("differentFilePath"));
        assertFalse(modelManager.equals(new ModelManager(application, differentUserPrefs)));

    }
}
