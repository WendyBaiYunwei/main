package pwe.planner;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Supplier;

import javafx.stage.Screen;
import javafx.stage.Stage;
import pwe.planner.commons.core.Config;
import pwe.planner.commons.core.GuiSettings;
import pwe.planner.commons.exceptions.DataConversionException;
import pwe.planner.model.Application;
import pwe.planner.model.Model;
import pwe.planner.model.ModelManager;
import pwe.planner.model.ReadOnlyApplication;
import pwe.planner.model.UserPrefs;
import pwe.planner.storage.JsonApplicationStorage;
import pwe.planner.storage.UserPrefsStorage;
import pwe.planner.testutil.TestUtil;
import systemtests.ModelHelper;

/**
 * This class is meant to override some properties of MainApp so that it will be suited for
 * testing
 */
public class TestApp extends MainApp {

    public static final Path SAVE_LOCATION_FOR_MODULE_LIST_TESTING =
            TestUtil.getFilePathInSandboxFolder("sampleModuleListData.json");
    public static final Path SAVE_LOCATION_FOR_DEGREE_PLANNER_LIST_TESTING =
            TestUtil.getFilePathInSandboxFolder("sampleDegreePlannerListData.json");
    public static final Path SAVE_LOCATION_FOR_REQUIREMENT_CATEGORY_LIST_TESTING =
            TestUtil.getFilePathInSandboxFolder("sampleRequirementCategoryListData.json");

    protected static final Path DEFAULT_PREF_FILE_LOCATION_FOR_TESTING =
            TestUtil.getFilePathInSandboxFolder("pref_testing.json");
    protected Supplier<ReadOnlyApplication> initialDataSupplier = () -> null;
    protected Path saveModuleListFileLocation = SAVE_LOCATION_FOR_MODULE_LIST_TESTING;
    protected Path saveDegreePlannerListFileLocation = SAVE_LOCATION_FOR_DEGREE_PLANNER_LIST_TESTING;
    protected Path saveRequirementCategoryListFileLocation = SAVE_LOCATION_FOR_REQUIREMENT_CATEGORY_LIST_TESTING;

    public TestApp() {
    }

    public TestApp(Supplier<ReadOnlyApplication> initialDataSupplier, Path saveModuleListFileLocation,
            Path saveDegreePlannerListFileLocation, Path saveRequirementCategoryListFileLocation) {
        super();
        this.initialDataSupplier = initialDataSupplier;
        this.saveModuleListFileLocation = saveModuleListFileLocation;
        this.saveDegreePlannerListFileLocation = saveDegreePlannerListFileLocation;
        this.saveRequirementCategoryListFileLocation = saveRequirementCategoryListFileLocation;

        // If some initial local data has been provided, write those to the file
        if (initialDataSupplier.get() != null) {
            JsonApplicationStorage jsonApplicationStorage =
                    new JsonApplicationStorage(saveModuleListFileLocation, saveDegreePlannerListFileLocation,
                            saveRequirementCategoryListFileLocation);
            try {
                jsonApplicationStorage.saveApplication(initialDataSupplier.get());
            } catch (IOException ioe) {
                throw new AssertionError(ioe);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected Config initConfig(Path configFilePath) {
        Config config = super.initConfig(configFilePath);
        config.setUserPrefsFilePath(DEFAULT_PREF_FILE_LOCATION_FOR_TESTING);
        return config;
    }

    @Override
    protected UserPrefs initPrefs(UserPrefsStorage storage) {
        UserPrefs userPrefs = super.initPrefs(storage);
        double x = Screen.getPrimary().getVisualBounds().getMinX();
        double y = Screen.getPrimary().getVisualBounds().getMinY();
        userPrefs.setGuiSettings(new GuiSettings(600.0, 600.0, (int) x, (int) y));
        userPrefs.setModuleListFilePath(saveModuleListFileLocation);
        userPrefs.setRequirementCategoryListFilePath(saveRequirementCategoryListFileLocation);
        userPrefs.setDegreePlannerListFilePath(saveDegreePlannerListFileLocation);
        return userPrefs;
    }

    /**
     * Returns a defensive copy of the application data stored inside the storage file.
     */
    public Application readStorageapplication() {
        try {
            return new Application(storage.readApplication().get());
        } catch (DataConversionException dce) {
            throw new AssertionError("Encountered invalid format when parsing application save data.", dce);
        } catch (IOException ioe) {
            throw new AssertionError("Storage file cannot be found.", ioe);
        }
    }

    /**
     * Returns the file path of the storage file.
     */
    public Path getStorageSaveLocation() {
        return storage.getModuleListFilePath();
    }

    /**
     * Returns a defensive copy of the model.
     */
    public Model getModel() {
        Model copy = new ModelManager((model.getApplication()), new UserPrefs());
        ModelHelper.setFilteredList(copy, model.getFilteredModuleList());
        return copy;
    }

    @Override
    public void start(Stage primaryStage) {
        ui.start(primaryStage);
    }

}
