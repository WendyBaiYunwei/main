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
import pwe.planner.commons.exceptions.IllegalValueException;
import pwe.planner.commons.util.FileUtil;
import pwe.planner.commons.util.JsonUtil;
import pwe.planner.model.ReadOnlyApplication;
import pwe.planner.model.module.Module;
import pwe.planner.model.planner.DegreePlanner;
import pwe.planner.model.requirement.RequirementCategory;

/**
 * A class to call JsonSerializableApplication methods
 */
public class JsonApplicationStorage implements ApplicationStorage {

    private static final Logger logger = LogsCenter.getLogger(JsonSerializableApplication.class);

    private Path moduleListFilePath;
    private Path degreePlannerListFilePath;
    private Path requirementCategoryListFilePath;

    public JsonApplicationStorage(Path moduleListFilePath, Path degreePlannerListFilePath,
            Path requirementCategoryListFilePath) {
        requireAllNonNull(moduleListFilePath, degreePlannerListFilePath, requirementCategoryListFilePath);

        this.moduleListFilePath = moduleListFilePath;
        this.degreePlannerListFilePath = degreePlannerListFilePath;
        this.requirementCategoryListFilePath = requirementCategoryListFilePath;
    }

    public Path getModuleListFilePath() {
        return moduleListFilePath;
    }

    public Path getDegreePlannerListFilePath() {
        return degreePlannerListFilePath;
    }

    public Path getRequirementCategoryListFilePath() {
        return requirementCategoryListFilePath;
    }

    @Override
    public void saveApplication(ReadOnlyApplication application) throws IOException {
        requireNonNull(application);

        saveModuleList(application, getModuleListFilePath());
        saveDegreePlannerList(application, getDegreePlannerListFilePath());
        saveRequirementCategoryList(application, getRequirementCategoryListFilePath());
    }

    @Override
    public Optional<ReadOnlyApplication> readApplication() throws DataConversionException {
        return readApplication(moduleListFilePath, degreePlannerListFilePath, requirementCategoryListFilePath);
    }

    /**
     * @param moduleListFilePath cannot be null
     * @param degreePlannerListFilePath cannot be null
     * @param requirementCategoryListFilePath cannot be null
     */
    public Optional<ReadOnlyApplication> readApplication(Path moduleListFilePath, Path degreePlannerListFilePath,
            Path requirementCategoryListFilePath)
            throws DataConversionException {
        requireAllNonNull(moduleListFilePath, degreePlannerListFilePath, requirementCategoryListFilePath);

        Optional<ObservableList<Module>> optionalModuleObservableList = readModuleList(moduleListFilePath);
        Optional<ObservableList<DegreePlanner>> optionalDegreePlannerObservableList =
                readDegreePlannerList(degreePlannerListFilePath);
        Optional<ObservableList<RequirementCategory>> optionalRequirementCategoryObservableList =
                readRequirementCategoryList(requirementCategoryListFilePath);

        if (!optionalModuleObservableList.isPresent()) {
            return Optional.empty();
        } else if (!optionalDegreePlannerObservableList.isPresent()) {
            return Optional.empty();
        } else if (!optionalRequirementCategoryObservableList.isPresent()) {
            return Optional.empty();
        }

        try {
            JsonSerializableApplication application =
                    new JsonSerializableApplication(optionalModuleObservableList.get(),
                            optionalDegreePlannerObservableList.get(),
                            optionalRequirementCategoryObservableList.get());
            ReadOnlyApplication jsonApplication = application.toModelType();
            return Optional.of(jsonApplication);
        } catch (IllegalValueException ive) {
            logger.info("Illegal values found in " + moduleListFilePath + ": " + ive.getMessage());
            logger.info("Illegal values found in " + degreePlannerListFilePath + ": " + ive.getMessage());
            logger.info("Illegal values found in " + requirementCategoryListFilePath + ": " + ive.getMessage());
            throw new DataConversionException(ive);
        }
    }

    @Override
    public Optional<ObservableList<Module>> readModuleList() throws DataConversionException {
        return readModuleList(moduleListFilePath);
    }

    /**
     * @param filePath location of the data. Cannot be null.
     */
    public Optional<ObservableList<Module>> readModuleList(Path filePath) throws DataConversionException {
        requireNonNull(filePath);

        Optional<JsonSerializableModuleList> jsonAppliction = JsonUtil.readJsonFile(
                filePath, JsonSerializableApplication.getJsonSerializableModuleListClass());
        if (!jsonAppliction.isPresent()) {
            return Optional.empty();
        }

        try {
            return Optional.of(jsonAppliction.get().toModelType());
        } catch (IllegalValueException ive) {
            logger.info("Illegal values found in " + filePath + ": " + ive.getMessage());
            throw new DataConversionException(ive);
        }
    }

    @Override
    public void saveModuleList(ReadOnlyApplication application) throws IOException {
        requireNonNull(application);

        saveModuleList(application, moduleListFilePath);
    }

    /**
     * @param filePath location of the data. Cannot be null.
     */
    public void saveModuleList(ReadOnlyApplication application, Path filePath) throws IOException {
        requireAllNonNull(application, filePath);

        FileUtil.createIfMissing(filePath);
        JsonUtil.saveJsonFile(new JsonSerializableModuleList(application), filePath);
    }

    @Override
    public Optional<ObservableList<DegreePlanner>> readDegreePlannerList() throws DataConversionException {
        return readDegreePlannerList(degreePlannerListFilePath);
    }

    /**
     * @param filePath location of the data. Cannot be null.
     */
    public Optional<ObservableList<DegreePlanner>> readDegreePlannerList(Path filePath)
            throws DataConversionException {
        requireNonNull(filePath);

        Optional<JsonSerializableDegreePlannerList> jsonApplication = JsonUtil.readJsonFile(
                filePath, JsonSerializableApplication.getJsonSerializableDegreePlannerListClass());
        if (!jsonApplication.isPresent()) {
            return Optional.empty();
        }

        try {
            return Optional.of(jsonApplication.get().toModelType());
        } catch (IllegalValueException ive) {
            logger.info("Illegal values found in " + filePath + ": " + ive.getMessage());
            throw new DataConversionException(ive);
        }
    }

    @Override
    public void saveDegreePlannerList(ReadOnlyApplication degreePlannerList)
            throws IOException {
        requireNonNull(degreePlannerList);

        saveDegreePlannerList(degreePlannerList, degreePlannerListFilePath);
    }

    /**
     * @param filePath location of the data. Cannot be null.
     */
    public void saveDegreePlannerList(ReadOnlyApplication application, Path filePath)
            throws IOException {
        requireAllNonNull(application, filePath);

        FileUtil.createIfMissing(filePath);
        JsonSerializableDegreePlannerList serializedDegreePlannerList =
                new JsonSerializableDegreePlannerList(application.getDegreePlannerList());
        JsonUtil.saveJsonFile(serializedDegreePlannerList, filePath);
    }

    @Override
    public Optional<ObservableList<RequirementCategory>> readRequirementCategoryList() throws DataConversionException {
        return readRequirementCategoryList(requirementCategoryListFilePath);
    }

    /**
     * @param filePath location of the data. Cannot be null.
     */
    public Optional<ObservableList<RequirementCategory>> readRequirementCategoryList(Path filePath)
            throws DataConversionException {
        requireNonNull(filePath);

        Optional<JsonSerializableRequirementCategoryList> jsonApplication = JsonUtil.readJsonFile(
                filePath, JsonSerializableApplication.getJsonSerializableRequirementCategoryListClass());
        if (!jsonApplication.isPresent()) {
            return Optional.empty();
        }

        try {
            return Optional.of(jsonApplication.get().toModelType());
        } catch (IllegalValueException ive) {
            logger.info("Illegal values found in " + filePath + ": " + ive.getMessage());
            throw new DataConversionException(ive);
        }
    }

    @Override
    public void saveRequirementCategoryList(ReadOnlyApplication requirementCategoryList)
            throws IOException {
        requireNonNull(requirementCategoryList);

        saveRequirementCategoryList(requirementCategoryList, requirementCategoryListFilePath);
    }

    /**
     * @param filePath location of the data. Cannot be null.
     */
    public void saveRequirementCategoryList(ReadOnlyApplication application, Path filePath)
            throws IOException {
        requireAllNonNull(application, filePath);

        FileUtil.createIfMissing(filePath);
        JsonSerializableRequirementCategoryList serializedRequirementCategoryList =
                new JsonSerializableRequirementCategoryList(application.getRequirementCategoryList());
        JsonUtil.saveJsonFile(serializedRequirementCategoryList, filePath);
    }

}
