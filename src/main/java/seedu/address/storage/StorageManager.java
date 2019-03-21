package seedu.address.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataConversionException;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyDegreePlannerList;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;
import seedu.address.model.module.Module;
import seedu.address.model.requirement.RequirementCategory;

/**
 * Manages storage of AddressBook data in local storage.
 */
public class StorageManager implements Storage {

    private static final Logger logger = LogsCenter.getLogger(StorageManager.class);
    private AddressBookStorage addressBookStorage;
    private UserPrefsStorage userPrefsStorage;
    private DegreePlannerListStorage degreePlannerListStorage;

    public StorageManager(AddressBookStorage addressBookStorage, DegreePlannerListStorage degreePlannerListStorage,
            UserPrefsStorage userPrefsStorage) {
        super();
        this.addressBookStorage = addressBookStorage;
        this.degreePlannerListStorage = degreePlannerListStorage;
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

    @Override
    public Optional<ReadOnlyAddressBook> readAddressBook() throws DataConversionException, IOException {
        return readAddressBook(addressBookStorage.getModuleListFilePath(),
                addressBookStorage.getRequirementCategoryListFilePath());
    }

    @Override
    public Optional<ReadOnlyAddressBook> readAddressBook(Path moduleListFilePath, Path requirementCategoryListFilePath)
            throws DataConversionException, IOException {
        logger.fine("Attempting to read data from file: " + moduleListFilePath);
        logger.fine("Attempting to read data from file: " + requirementCategoryListFilePath);
        return addressBookStorage.readAddressBook(moduleListFilePath, requirementCategoryListFilePath);
    }

    @Override
    public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
        saveModuleList(addressBook, addressBookStorage.getModuleListFilePath());
        saveRequirementCategoryList(addressBook, addressBookStorage.getRequirementCategoryListFilePath());
    }


    // ================ AddressBook methods ==============================

    @Override
    public Path getModuleListFilePath() {
        return addressBookStorage.getModuleListFilePath();
    }

    @Override
    public Optional<ObservableList<Module>> readModuleList() throws DataConversionException, IOException {
        return readModuleList(addressBookStorage.getModuleListFilePath());
    }

    @Override
    public Optional<ObservableList<Module>> readModuleList(Path filePath) throws DataConversionException, IOException {
        logger.fine("Attempting to read data from file: " + filePath);
        return addressBookStorage.readModuleList(filePath);
    }

    @Override
    public void saveModuleList(ReadOnlyAddressBook addressBook) throws IOException {
        saveModuleList(addressBook, addressBookStorage.getModuleListFilePath());
    }

    @Override
    public void saveModuleList(ReadOnlyAddressBook addressBook, Path filePath) throws IOException {
        logger.fine("Attempting to write to data file: " + filePath);
        addressBookStorage.saveModuleList(addressBook, filePath);
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
        return addressBookStorage.getRequirementCategoryListFilePath();
    }

    @Override
    public Optional<ObservableList<RequirementCategory>> readRequirementCategoryList()
            throws DataConversionException, IOException {
        return readRequirementCategoryList(addressBookStorage.getRequirementCategoryListFilePath());
    }

    @Override
    public Optional<ObservableList<RequirementCategory>> readRequirementCategoryList(Path filePath)
            throws DataConversionException, IOException {
        logger.fine("Attempting to read data from file: " + filePath);
        return addressBookStorage.readRequirementCategoryList(filePath);
    }

    @Override
    public void saveRequirementCategoryList(ReadOnlyAddressBook requirementCategoryList)
            throws IOException {
        saveRequirementCategoryList(requirementCategoryList,
                addressBookStorage.getRequirementCategoryListFilePath());
    }

    @Override
    public void saveRequirementCategoryList(ReadOnlyAddressBook requirementCategoryList, Path filePath)
            throws IOException {
        logger.fine("Attempting to write to data file: " + filePath);
        addressBookStorage.saveRequirementCategoryList(requirementCategoryList, filePath);
    }
}

