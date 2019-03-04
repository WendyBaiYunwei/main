package seedu.address.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import seedu.address.commons.exceptions.DataConversionException;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyDegreePlannerList;
import seedu.address.model.ReadOnlyRequirementCategoryList;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;

/**
 * API of the Storage component
 */

public interface Storage
        extends AddressBookStorage, UserPrefsStorage, DegreePlannerListStorage, RequirementCategoryListStorage {

    @Override
    Optional<UserPrefs> readUserPrefs() throws DataConversionException, IOException;

    @Override
    void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException;

    @Override
    Path getAddressBookFilePath();

    @Override
    Optional<ReadOnlyAddressBook> readAddressBook() throws DataConversionException, IOException;

    @Override
    void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException;

    @Override
    Path getDegreePlannerListFilePath();

    @Override
    Optional<ReadOnlyDegreePlannerList> readDegreePlannerList() throws DataConversionException, IOException;

    @Override
    void saveDegreePlannerList(ReadOnlyDegreePlannerList degreePlannerList) throws IOException;

    @Override
    Path getRequirementCategoryListFilePath();

    @Override
    Optional<ReadOnlyRequirementCategoryList> readRequirementCategoryList() throws DataConversionException, IOException;

    @Override
    void saveRequirementCategoryList(ReadOnlyRequirementCategoryList requirementCategoryList) throws IOException;


}

