package pwe.planner.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import javafx.collections.ObservableList;
import pwe.planner.commons.exceptions.DataConversionException;
import pwe.planner.model.Application;
import pwe.planner.model.ReadOnlyApplication;
import pwe.planner.model.module.Module;
import pwe.planner.model.planner.DegreePlanner;
import pwe.planner.model.requirement.RequirementCategory;

/**
 * Represents a storage for {@link Application}.
 */
public interface ApplicationStorage {

    /**
     * Returns the file path of the data file.
     */
    Path getModuleListFilePath();

    Path getDegreePlannerListFilePath();

    Path getRequirementCategoryListFilePath();

    /**
     * Returns Application data as a {@link ReadOnlyApplication}.
     * Returns {@code Optional.empty()} if storage file is not found.
     *
     * @throws DataConversionException if the data in storage is not in the expected format.
     * @throws IOException             if there was any problem when reading from the storage.
     */
    Optional<ObservableList<Module>> readModuleList() throws DataConversionException, IOException;

    /**
     * @see #getModuleListFilePath()
     * @see #getDegreePlannerListFilePath()
     */
    Optional<ObservableList<Module>> readModuleList(Path filePath) throws DataConversionException, IOException;

    Optional<ReadOnlyApplication> readApplication() throws DataConversionException, IOException;

    /**
     * @see #getModuleListFilePath()
     * @see #getDegreePlannerListFilePath()
     */
    Optional<ReadOnlyApplication> readApplication(Path moduleListFilePath, Path degreePlannerListFilePath,
            Path requirementCategoryListFilePath)
            throws DataConversionException, IOException;

    /**
     * Saves the given {@link ReadOnlyApplication} to the storage.
     *
     * @param application cannot be null.
     * @throws IOException if there was any problem writing to the file.
     */
    void saveApplication(ReadOnlyApplication application) throws IOException;

    /**
     * Saves the given {@link ReadOnlyApplication} to the storage.
     *
     * @param application cannot be null.
     * @throws IOException if there was any problem writing to the file.
     */
    void saveModuleList(ReadOnlyApplication application) throws IOException;

    /**
     * @see #saveApplication(ReadOnlyApplication)
     */
    void saveModuleList(ReadOnlyApplication application, Path filePath) throws IOException;

    /**
     * Returns degree planner list data as a {@link ReadOnlyApplication}.
     * Returns {@code Optional.empty()} if storage file is not found.
     *
     * @throws DataConversionException if the data in storage is not in the expected format.
     * @throws IOException             if there was any problem when reading from the storage.
     */
    Optional<ObservableList<DegreePlanner>> readDegreePlannerList()
            throws DataConversionException, IOException;

    /**
     * @see #getDegreePlannerListFilePath()
     */
    Optional<ObservableList<DegreePlanner>> readDegreePlannerList(Path filePath)
            throws DataConversionException, IOException;

    /**
     * Saves the given {@link ReadOnlyApplication} to the storage.
     *
     * @param degreePlannerList cannot be null.
     * @throws IOException if there was any problem writing to the file.
     */
    void saveDegreePlannerList(ReadOnlyApplication degreePlannerList) throws IOException;

    /**
     * @see #saveDegreePlannerList(ReadOnlyApplication)
     */
    void saveDegreePlannerList(ReadOnlyApplication application, Path filePath) throws IOException;

    /**
     * Returns RequirementList data as a {@link ReadOnlyApplication}.
     * Returns {@code Optional.empty()} if storage file is not found.
     *
     * @throws DataConversionException if the data in storage is not in the expected format.
     * @throws IOException             if there was any problem when reading from the storage.
     */
    Optional<ObservableList<RequirementCategory>> readRequirementCategoryList()
            throws DataConversionException, IOException;

    /**
     * @see #getRequirementCategoryListFilePath() ()
     */
    Optional<ObservableList<RequirementCategory>> readRequirementCategoryList(Path filePath)
            throws DataConversionException, IOException;

    /**
     * Saves the given {@link ReadOnlyApplication} to the storage.
     *
     * @param requirementCategoryList cannot be null.
     * @throws IOException if there was any problem writing to the file.
     */
    void saveRequirementCategoryList(ReadOnlyApplication requirementCategoryList) throws IOException;

    /**
     * @see #saveRequirementCategoryList(ReadOnlyApplication)
     */
    void saveRequirementCategoryList(ReadOnlyApplication application, Path filePath) throws IOException;
}
