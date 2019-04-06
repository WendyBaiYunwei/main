package pwe.planner.storage;

import static java.util.Objects.requireNonNull;
import static pwe.planner.commons.util.CollectionUtil.requireAllNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import pwe.planner.commons.core.LogsCenter;
import pwe.planner.commons.exceptions.DataConversionException;
import pwe.planner.model.ReadOnlyApplication;
import pwe.planner.model.ReadOnlyUserPrefs;
import pwe.planner.model.UserPrefs;
import pwe.planner.model.module.Module;
import pwe.planner.model.planner.DegreePlanner;
import pwe.planner.model.requirement.RequirementCategory;

/**
 * Manages storage of Application data in local storage.
 */
public class StorageManager implements Storage {

    private static final Logger logger = LogsCenter.getLogger(StorageManager.class);
    private ApplicationStorage applicationStorage;
    private UserPrefsStorage userPrefsStorage;

    public StorageManager(ApplicationStorage applicationStorage,
            UserPrefsStorage userPrefsStorage) {
        super();

        requireAllNonNull(applicationStorage, userPrefsStorage);
        this.applicationStorage = applicationStorage;
        this.userPrefsStorage = userPrefsStorage;
    }

    // ================ UserPrefs methods ==============================

    @Override
    public Path getUserPrefsFilePath() {
        return userPrefsStorage.getUserPrefsFilePath();
    }

    @Override
    public Optional<UserPrefs> readUserPrefs() throws DataConversionException, IOException {
        return userPrefsStorage.readUserPrefs();
    }

    @Override
    public void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException {
        requireNonNull(userPrefs);

        userPrefsStorage.saveUserPrefs(userPrefs);
    }

    @Override
    public Optional<ReadOnlyApplication> readApplication() throws DataConversionException, IOException {
        return readApplication(applicationStorage.getModuleListFilePath(),
                applicationStorage.getDegreePlannerListFilePath(),
                applicationStorage.getRequirementCategoryListFilePath());
    }

    @Override
    public Optional<ReadOnlyApplication> readApplication(Path moduleListFilePath, Path degreePlannerListFilePath,
            Path requirementCategoryListFilePath)
            throws DataConversionException, IOException {
        requireAllNonNull(moduleListFilePath, degreePlannerListFilePath, requirementCategoryListFilePath);

        logger.fine("Attempting to read data from file: " + moduleListFilePath);
        logger.fine("Attempting to read data from file: " + degreePlannerListFilePath);
        logger.fine("Attempting to read data from file: " + requirementCategoryListFilePath);
        return applicationStorage
                .readApplication(moduleListFilePath, degreePlannerListFilePath, requirementCategoryListFilePath);
    }

    @Override
    public void saveApplication(ReadOnlyApplication application) throws IOException {
        requireNonNull(application);

        saveModuleList(application, applicationStorage.getModuleListFilePath());
        saveDegreePlannerList(application, applicationStorage.getDegreePlannerListFilePath());
        saveRequirementCategoryList(application, applicationStorage.getRequirementCategoryListFilePath());
    }


    // ================ Application methods ==============================

    @Override
    public Path getModuleListFilePath() {
        return applicationStorage.getModuleListFilePath();
    }

    @Override
    public Optional<ObservableList<Module>> readModuleList() throws DataConversionException, IOException {
        return readModuleList(applicationStorage.getModuleListFilePath());
    }

    @Override
    public Optional<ObservableList<Module>> readModuleList(Path filePath) throws DataConversionException, IOException {
        requireNonNull(filePath);

        logger.fine("Attempting to read data from file: " + filePath);
        return applicationStorage.readModuleList(filePath);
    }

    @Override
    public void saveModuleList(ReadOnlyApplication application) throws IOException {
        requireNonNull(application);

        saveModuleList(application, applicationStorage.getModuleListFilePath());
    }

    @Override
    public void saveModuleList(ReadOnlyApplication application, Path filePath) throws IOException {
        requireAllNonNull(application, filePath);

        logger.fine("Attempting to write to data file: " + filePath);
        applicationStorage.saveModuleList(application, filePath);
    }

    // ================ DegreePlannerList methods ========================

    @Override
    public Path getDegreePlannerListFilePath() {
        return applicationStorage.getDegreePlannerListFilePath();
    }

    @Override
    public Optional<ObservableList<DegreePlanner>> readDegreePlannerList() throws DataConversionException, IOException {
        return readDegreePlannerList(applicationStorage.getDegreePlannerListFilePath());
    }

    @Override
    public Optional<ObservableList<DegreePlanner>> readDegreePlannerList(Path filePath)
            throws DataConversionException, IOException {
        requireNonNull(filePath);

        logger.fine("Attempting to read data from file: " + filePath);
        return applicationStorage.readDegreePlannerList(filePath);
    }

    @Override
    public void saveDegreePlannerList(ReadOnlyApplication degreePlannerList) throws IOException {
        requireNonNull(degreePlannerList);

        saveDegreePlannerList(degreePlannerList, applicationStorage.getDegreePlannerListFilePath());
    }

    @Override
    public void saveDegreePlannerList(ReadOnlyApplication application, Path filePath) throws IOException {
        requireAllNonNull(application, filePath);

        logger.fine("Attempting to write to data file: " + filePath);
        applicationStorage.saveDegreePlannerList(application, filePath);
    }

    // ================ RequirementCategoryList methods ============================================================

    @Override
    public Path getRequirementCategoryListFilePath() {
        return applicationStorage.getRequirementCategoryListFilePath();
    }

    @Override
    public Optional<ObservableList<RequirementCategory>> readRequirementCategoryList()
            throws DataConversionException, IOException {
        return readRequirementCategoryList(applicationStorage.getRequirementCategoryListFilePath());
    }

    @Override
    public Optional<ObservableList<RequirementCategory>> readRequirementCategoryList(Path filePath)
            throws DataConversionException, IOException {
        requireNonNull(filePath);

        logger.fine("Attempting to read data from file: " + filePath);
        return applicationStorage.readRequirementCategoryList(filePath);
    }

    @Override
    public void saveRequirementCategoryList(ReadOnlyApplication requirementCategoryList) throws IOException {
        requireNonNull(requirementCategoryList);

        saveRequirementCategoryList(requirementCategoryList,
                applicationStorage.getRequirementCategoryListFilePath());
    }

    @Override
    public void saveRequirementCategoryList(ReadOnlyApplication application, Path filePath) throws IOException {
        requireAllNonNull(application, filePath);

        logger.fine("Attempting to write to data file: " + filePath);
        applicationStorage.saveRequirementCategoryList(application, filePath);
    }
}
