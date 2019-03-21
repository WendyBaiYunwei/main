package seedu.address.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import javafx.collections.ObservableList;
import seedu.address.commons.exceptions.DataConversionException;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.module.Module;
import seedu.address.model.requirement.RequirementCategory;

/**
 * Represents a storage for {@link seedu.address.model.AddressBook}.
 */
public interface AddressBookStorage {

    /**
     * Returns the file path of the data file.
     */
    Path getModuleListFilePath();

    Path getRequirementCategoryListFilePath();

    /**
     * Returns AddressBook data as a {@link ReadOnlyAddressBook}.
     * Returns {@code Optional.empty()} if storage file is not found.
     *
     * @throws DataConversionException if the data in storage is not in the expected format.
     * @throws IOException             if there was any problem when reading from the storage.
     */
    Optional<ObservableList<Module>> readModuleList() throws DataConversionException, IOException;

    /**
     * @see #getModuleListFilePath()
     */
    Optional<ObservableList<Module>> readModuleList(Path filePath) throws DataConversionException, IOException;

    Optional<ReadOnlyAddressBook> readAddressBook() throws DataConversionException, IOException;

    /**
     * @see #getModuleListFilePath()
     */
    Optional<ReadOnlyAddressBook> readAddressBook(Path moduleListFilePath, Path requirementCategoryListFilePath)
            throws DataConversionException, IOException;

    /**
     * Saves the given {@link ReadOnlyAddressBook} to the storage.
     *
     * @param addressBook cannot be null.
     * @throws IOException if there was any problem writing to the file.
     */
    void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException;

    /**
     * Saves the given {@link ReadOnlyAddressBook} to the storage.
     *
     * @param addressBook cannot be null.
     * @throws IOException if there was any problem writing to the file.
     */
    void saveModuleList(ReadOnlyAddressBook addressBook) throws IOException;

    /**
     * @see #saveAddressBook(ReadOnlyAddressBook)
     */
    void saveModuleList(ReadOnlyAddressBook addressBook, Path filePath) throws IOException;

    /**
     * Returns RequirementList data as a {@link ReadOnlyAddressBook}.
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
     * Saves the given {@link ReadOnlyAddressBook} to the storage.
     *
     * @param requirementCategoryList cannot be null.
     * @throws IOException if there was any problem writing to the file.
     */
    void saveRequirementCategoryList(ReadOnlyAddressBook requirementCategoryList) throws IOException;

    /**
     * @see #saveRequirementCategoryList(ReadOnlyAddressBook)
     */
    void saveRequirementCategoryList(ReadOnlyAddressBook addressBook, Path filePath) throws IOException;
}
