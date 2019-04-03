package pwe.planner.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static pwe.planner.testutil.TypicalDegreePlanners.getTypicalDegreePlannerList;
import static pwe.planner.testutil.TypicalRequirementCategories.getTypicalRequirementCategoriesList;

import java.nio.file.Path;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import pwe.planner.commons.core.GuiSettings;
import pwe.planner.model.Application;
import pwe.planner.model.ReadOnlyApplication;
import pwe.planner.model.UserPrefs;
import pwe.planner.testutil.TypicalModules;

public class StorageManagerTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    private StorageManager storageManager;

    @Before
    public void setUp() {
        JsonApplicationStorage applicationStorage =
                new JsonApplicationStorage(getTempFilePath("ab"), getTempFilePath("dp"), getTempFilePath("reqCat"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(getTempFilePath("prefs"));

        storageManager = new StorageManager(applicationStorage, userPrefsStorage);
    }

    private Path getTempFilePath(String fileName) {
        return testFolder.getRoot().toPath().resolve(fileName);
    }

    @Test
    public void prefsReadSave() throws Exception {
        /*
         * Note: This is an integration test that verifies the StorageManager is properly wired to the
         * {@link JsonUserPrefsStorage} class.
         * More extensive testing of UserPref saving/reading is done in {@link JsonUserPrefsStorageTest} class.
         */
        UserPrefs original = new UserPrefs();
        original.setGuiSettings(new GuiSettings(300, 600, 4, 6));
        storageManager.saveUserPrefs(original);
        UserPrefs retrieved = storageManager.readUserPrefs().get();
        assertEquals(original, retrieved);
    }

    @Test
    public void applicationReadSave() throws Exception {
        /*
         * Note: This is an integration test that verifies the StorageManager is properly wired to the
         * {@link JsonApplicationStorage} class.
         * More extensive testing of UserPref saving/reading is done in {@link JsonApplicationStorageTest} class.
         */
        Application original =
                new JsonSerializableApplication(TypicalModules.getTypicalModuleList(), getTypicalDegreePlannerList(),
                        getTypicalRequirementCategoriesList()).toModelType();
        storageManager.saveApplication(original);
        ReadOnlyApplication retrieved = storageManager.readApplication().get();
        assertEquals(original, new Application(retrieved));
    }

    @Test
    public void getModuleListFilePath() {
        assertNotNull(storageManager.getModuleListFilePath());
    }

}
