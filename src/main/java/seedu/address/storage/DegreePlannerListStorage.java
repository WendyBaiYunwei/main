package seedu.address.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import seedu.address.commons.exceptions.DataConversionException;
import seedu.address.model.ReadOnlyDegreePlannerList;

/**
 * Represents a storage for {@link seedu.address.model.DegreePlannerList}.
 */
public interface DegreePlannerListStorage {
    /**
     * Returns the file path of the data file.
     */
    Path getDegreePlannerListFilePath();

    /**
     * Returns DegreePlannerList data as a {@link ReadOnlyDegreePlannerList}.
     * Returns {@code Optional.empty()} if storage file is not found.
     *
     * @throws DataConversionException if the data in storage is not in the expected format.
     * @throws IOException             if there was any problem when reading from the storage.
     */
    Optional<ReadOnlyDegreePlannerList> readDegreePlannerList() throws DataConversionException, IOException;

    /**
     * @see #getDegreePlannerListFilePath()
     */
    Optional<ReadOnlyDegreePlannerList> readDegreePlannerList(Path filePath)
            throws DataConversionException, IOException;

    /**
     * Saves the given {@link ReadOnlyDegreePlannerList} to the storage.
     *
     * @param degreePlannerList cannot be null.
     * @throws IOException if there was any problem writing to the file.
     */
    void saveDegreePlannerList(ReadOnlyDegreePlannerList degreePlannerList) throws IOException;

    /**
     * @see #saveDegreePlannerList(ReadOnlyDegreePlannerList)
     */
    void saveDegreePlannerList(ReadOnlyDegreePlannerList addressBook, Path filePath) throws IOException;
}
