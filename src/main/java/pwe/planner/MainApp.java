package pwe.planner;

import static java.util.Objects.requireNonNull;
import static pwe.planner.model.util.InitialDataUtil.getInitialApplication;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.stage.Stage;
import pwe.planner.commons.core.Config;
import pwe.planner.commons.core.LogsCenter;
import pwe.planner.commons.core.Version;
import pwe.planner.commons.exceptions.DataConversionException;
import pwe.planner.commons.util.ConfigUtil;
import pwe.planner.commons.util.StringUtil;
import pwe.planner.logic.Logic;
import pwe.planner.logic.LogicManager;
import pwe.planner.model.Model;
import pwe.planner.model.ModelManager;
import pwe.planner.model.ReadOnlyApplication;
import pwe.planner.model.ReadOnlyUserPrefs;
import pwe.planner.model.UserPrefs;
import pwe.planner.model.util.SampleDataUtil;
import pwe.planner.storage.ApplicationStorage;
import pwe.planner.storage.JsonApplicationStorage;
import pwe.planner.storage.JsonUserPrefsStorage;
import pwe.planner.storage.Storage;
import pwe.planner.storage.StorageManager;
import pwe.planner.storage.UserPrefsStorage;
import pwe.planner.ui.Ui;
import pwe.planner.ui.UiManager;

/**
 * The main entry point to the application.
 */
public class MainApp extends Application {

    public static final Version VERSION = new Version(1, 3, 0, false);

    private static final Logger logger = LogsCenter.getLogger(MainApp.class);

    protected Ui ui;
    protected Logic logic;
    protected Storage storage;
    protected Model model;
    protected Config config;

    @Override
    public void init() throws Exception {
        logger.info("=============================[ Initializing PlanWithEase ]===========================");
        super.init();

        AppParameters appParameters = AppParameters.parse(getParameters());
        config = initConfig(appParameters.getConfigPath());

        UserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(config.getUserPrefsFilePath());
        UserPrefs userPrefs = initPrefs(userPrefsStorage);
        ApplicationStorage applicationStorage =
                new JsonApplicationStorage(userPrefs.getModuleListFilePath(), userPrefs.getDegreePlannerListFilePath(),
                        userPrefs.getRequirementCategoryListFilePath());

        storage = new StorageManager(applicationStorage, userPrefsStorage);

        initLogging(config);

        model = initModelManager(storage, userPrefs);

        logic = new LogicManager(model, storage);

        ui = new UiManager(logic);
    }

    /**
     * Returns a {@code ModelManager} with the data from {@code storage}'s module list, requirement categories
     * list, degree planner list and {@code userPrefs}. <br>
     * The data from the sample application will be used instead if {@code storage}'s module list,
     * requirement categories list and degree planner list is not found,
     * or an empty application will be used instead if errors occur when reading {@code storage}'s module list,
     * requirement categories list and degree planner list.
     */
    private Model initModelManager(Storage storage, ReadOnlyUserPrefs userPrefs) {
        assert storage != null;
        assert userPrefs != null;

        Optional<ReadOnlyApplication> applicationOptional;

        ReadOnlyApplication initialData;

        try {
            applicationOptional = storage.readApplication();

            if (!applicationOptional.isPresent()) {
                logger.info("Data file not found. Will be starting the application with sample data");
            }
            initialData = applicationOptional.orElseGet(SampleDataUtil::getSampleApplication);
        } catch (DataConversionException e) {
            logger.warning(
                    "Data file not in the correct format. Will be starting the application with empty module list");
            initialData = getInitialApplication();
        } catch (IOException e) {
            logger.warning(
                    "Problem while reading from the file. Will be starting the application with empty module list");
            initialData = getInitialApplication();
        }

        return new ModelManager(initialData, userPrefs);

    }

    private void initLogging(Config config) {
        assert config != null;

        LogsCenter.init(config);
    }

    /**
     * Returns a {@code Config} using the file at {@code configFilePath}. <br>
     * The default file path {@code Config#DEFAULT_CONFIG_FILE} will be used instead
     * if {@code configFilePath} is null.
     */
    protected Config initConfig(Path configFilePath) {
        Config initializedConfig;
        Path configFilePathUsed;

        configFilePathUsed = Config.DEFAULT_CONFIG_FILE;

        if (configFilePath != null) {
            logger.info("Custom Config file specified " + configFilePath);
            configFilePathUsed = configFilePath;
        }

        logger.info("Using config file : " + configFilePathUsed);

        try {
            Optional<Config> configOptional = ConfigUtil.readConfig(configFilePathUsed);
            initializedConfig = configOptional.orElse(new Config());
        } catch (DataConversionException e) {
            logger.warning("Config file at " + configFilePathUsed + " is not in the correct format. "
                    + "Using default config properties");
            initializedConfig = new Config();
        }

        //Update config file in case it was missing to begin with or there are new/unused fields
        try {
            ConfigUtil.saveConfig(initializedConfig, configFilePathUsed);
        } catch (IOException e) {
            logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
        }
        return initializedConfig;
    }

    /**
     * Returns a {@code UserPrefs} using the file at {@code storage}'s user prefs file path,
     * or a new {@code UserPrefs} with default configuration if errors occur when
     * reading from the file.
     */
    protected UserPrefs initPrefs(UserPrefsStorage storage) {
        assert storage != null;

        Path prefsFilePath = storage.getUserPrefsFilePath();
        logger.info("Using prefs file : " + prefsFilePath);

        UserPrefs initializedPrefs;
        try {
            Optional<UserPrefs> prefsOptional = storage.readUserPrefs();
            initializedPrefs = prefsOptional.orElse(new UserPrefs());
        } catch (DataConversionException e) {
            logger.warning("UserPrefs file at " + prefsFilePath + " is not in the correct format. "
                    + "Using default user prefs");
            initializedPrefs = new UserPrefs();
        } catch (IOException e) {
            logger.warning(
                    "Problem while reading from the file. Will be starting the application with empty module list");
            initializedPrefs = new UserPrefs();
        }

        //Update prefs file in case it was missing to begin with or there are new/unused fields
        try {
            storage.saveUserPrefs(initializedPrefs);
        } catch (IOException e) {
            logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
        }

        return initializedPrefs;
    }

    @Override
    public void start(Stage primaryStage) {
        requireNonNull(primaryStage);

        logger.info("Starting PlanWithEase " + MainApp.VERSION);
        ui.start(primaryStage);
    }

    @Override
    public void stop() {
        logger.info("============================ [ Stopping PlanWithEase ] =============================");
        try {
            storage.saveUserPrefs(model.getUserPrefs());
        } catch (IOException e) {
            logger.severe("Failed to save preferences " + StringUtil.getDetails(e));
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
