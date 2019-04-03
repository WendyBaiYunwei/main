package pwe.planner.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import javafx.collections.ObservableList;
import pwe.planner.commons.exceptions.DataConversionException;
import pwe.planner.model.ReadOnlyApplication;
import pwe.planner.model.ReadOnlyUserPrefs;
import pwe.planner.model.UserPrefs;
import pwe.planner.model.module.Module;
import pwe.planner.model.planner.DegreePlanner;
import pwe.planner.model.requirement.RequirementCategory;

/**
 * API of the Storage component
 */

public interface Storage extends ApplicationStorage, UserPrefsStorage {

    @Override
    Optional<UserPrefs> readUserPrefs() throws DataConversionException, IOException;

    @Override
    void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException;

    @Override
    Optional<ReadOnlyApplication> readApplication() throws DataConversionException, IOException;

    @Override
    void saveApplication(ReadOnlyApplication application) throws IOException;

    @Override
    Path getModuleListFilePath();

    @Override
    Optional<ObservableList<Module>> readModuleList() throws DataConversionException, IOException;

    @Override
    void saveModuleList(ReadOnlyApplication application) throws IOException;

    @Override
    Path getDegreePlannerListFilePath();

    @Override
    Optional<ObservableList<DegreePlanner>> readDegreePlannerList() throws DataConversionException, IOException;

    @Override
    void saveDegreePlannerList(ReadOnlyApplication degreePlannerList) throws IOException;

    @Override
    Path getRequirementCategoryListFilePath();

    @Override
    Optional<ObservableList<RequirementCategory>> readRequirementCategoryList()
            throws DataConversionException, IOException;

    @Override
    void saveRequirementCategoryList(ReadOnlyApplication requirementCategoryList) throws IOException;

}
