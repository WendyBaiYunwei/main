package seedu.address.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import javafx.collections.ObservableList;
import seedu.address.commons.exceptions.DataConversionException;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;
import seedu.address.model.module.Module;
import seedu.address.model.planner.DegreePlanner;
import seedu.address.model.requirement.RequirementCategory;

/**
 * API of the Storage component
 */

public interface Storage extends AddressBookStorage, UserPrefsStorage {

    @Override
    Optional<UserPrefs> readUserPrefs() throws DataConversionException, IOException;

    @Override
    void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException;

    @Override
    Optional<ReadOnlyAddressBook> readAddressBook() throws DataConversionException, IOException;

    @Override
    void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException;

    @Override
    Path getModuleListFilePath();

    @Override
    Optional<ObservableList<Module>> readModuleList() throws DataConversionException, IOException;

    @Override
    void saveModuleList(ReadOnlyAddressBook addressBook) throws IOException;

    @Override
    Path getDegreePlannerListFilePath();

    @Override
    Optional<ObservableList<DegreePlanner>> readDegreePlannerList() throws DataConversionException, IOException;

    @Override
    void saveDegreePlannerList(ReadOnlyAddressBook degreePlannerList) throws IOException;

    @Override
    Path getRequirementCategoryListFilePath();

    @Override
    Optional<ObservableList<RequirementCategory>> readRequirementCategoryList()
            throws DataConversionException, IOException;

    @Override
    void saveRequirementCategoryList(ReadOnlyAddressBook requirementCategoryList) throws IOException;

}
