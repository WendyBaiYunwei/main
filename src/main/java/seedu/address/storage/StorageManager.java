package seedu.address.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataConversionException;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyDegreePlannerList;
import seedu.address.model.ReadOnlyRequirementCategoryList;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;

/**
 * Manages storage of AddressBook data in local storage.
 */
public class StorageManager implements Storage {

    private static final Logger logger = LogsCenter.getLogger(StorageManager.class);
    private AddressBookStorage addressBookStorage;
    private UserPrefsStorage userPrefsStorage;
    private DegreePlannerListStorage degreePlannerListStorage;
    private RequirementCategoryListStorage requirementCategoryListStorage;

    public StorageManager(AddressBookStorage addressBookStorage,
                          RequirementCategoryListStorage requirementCategoryListStorage,
            DegreePlannerListStorage degreePlannerListStorage, UserPrefsStorage userPrefsStorage) {
        super();
        this.addressBookStorage = addressBookStorage;
        this.degreePlannerListStorage = degreePlannerListStorage;
        this.requirementCategoryListStorage = requirementCategoryListStorage;
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
        userPrefsStorage.saveUserPrefs(userPrefs);
    }


    // ================ AddressBook methods ==============================

    @Override
    public Path getAddressBookFilePath() {
        return addressBookStorage.getAddressBookFilePath();
    }

    @Override
    public Optional<ReadOnlyAddressBook> readAddressBook() throws DataConversionException, IOException {
        return readAddressBook(addressBookStorage.getAddressBookFilePath());
    }

    @Override
    public Optional<ReadOnlyAddressBook> readAddressBook(Path filePath) throws DataConversionException, IOException {
        logger.fine("Attempting to read data from file: " + filePath);
        return addressBookStorage.readAddressBook(filePath);
    }

    @Override
    public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
        saveAddressBook(addressBook, addressBookStorage.getAddressBookFilePath());
    }

    @Override
    public void saveAddressBook(ReadOnlyAddressBook addressBook, Path filePath) throws IOException {
        logger.fine("Attempting to write to data file: " + filePath);
        addressBookStorage.saveAddressBook(addressBook, filePath);
    }

    // ================ DegreePlannerList methods ========================

    @Override
    public Path getDegreePlannerListFilePath() {
        return degreePlannerListStorage.getDegreePlannerListFilePath();
    }

    @Override
    public Optional<ReadOnlyDegreePlannerList> readDegreePlannerList() throws DataConversionException, IOException {
        return readDegreePlannerList(degreePlannerListStorage.getDegreePlannerListFilePath());
    }

    @Override
    public Optional<ReadOnlyDegreePlannerList> readDegreePlannerList(Path filePath)
            throws DataConversionException, IOException {
        logger.fine("Attempting to read data from file: " + filePath);
        return degreePlannerListStorage.readDegreePlannerList(filePath);
    }

    @Override
    public void saveDegreePlannerList(ReadOnlyDegreePlannerList degreePlannerList) throws IOException {
        saveDegreePlannerList(degreePlannerList, degreePlannerListStorage.getDegreePlannerListFilePath());
    }

    @Override
    public void saveDegreePlannerList(ReadOnlyDegreePlannerList degreePlannerList, Path filePath) throws IOException {
        logger.fine("Attempting to write to data file: " + filePath);
        degreePlannerListStorage.saveDegreePlannerList(degreePlannerList, filePath);
    }

    // ================ RequirementCategoryList methods ============================================================

    @Override
    public Path getRequirementCategoryListFilePath() {
        return requirementCategoryListStorage.getRequirementCategoryListFilePath();
    }

    @Override
    public Optional<ReadOnlyRequirementCategoryList> readRequirementCategoryList()
            throws DataConversionException, IOException {
        return readRequirementCategoryList(requirementCategoryListStorage.getRequirementCategoryListFilePath());
    }

    @Override
    public Optional<ReadOnlyRequirementCategoryList> readRequirementCategoryList(Path filePath)
            throws DataConversionException, IOException {
        logger.fine("Attempting to read data from file: " + filePath);
        return requirementCategoryListStorage.readRequirementCategoryList(filePath);
    }

    @Override
    public void saveRequirementCategoryList(ReadOnlyRequirementCategoryList requirementCategoryList)
            throws IOException {
        saveRequirementCategoryList(requirementCategoryList,
                requirementCategoryListStorage.getRequirementCategoryListFilePath());
    }

    @Override
    public void saveRequirementCategoryList(ReadOnlyRequirementCategoryList requirementCategoryList, Path filePath)
            throws IOException {
        logger.fine("Attempting to write to data file: " + filePath);
        requirementCategoryListStorage.saveRequirementCategoryList(requirementCategoryList, filePath);
    }
}

