package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataConversionException;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.FileUtil;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.ReadOnlyDegreePlannerList;

/**
 * A class to access DegreePlannerList data stored as a json file on the hard disk.
 */
public class JsonDegreePlannerListStorage implements DegreePlannerListStorage {

    private static final Logger logger = LogsCenter.getLogger(JsonAddressBookStorage.class);

    private Path filePath;

    public JsonDegreePlannerListStorage(Path filePath) {
        this.filePath = filePath;
    }

    public Path getDegreePlannerListFilePath() {
        return filePath;
    }

    @Override
    public Optional<ReadOnlyDegreePlannerList> readDegreePlannerList() throws DataConversionException {
        return readDegreePlannerList(filePath);
    }

    /**
     * Similar to {@link #readDegreePlannerList()}.
     *
     * @param filePath location of the data. Cannot be null.
     * @throws DataConversionException if the file is not in the correct format.
     */
    public Optional<ReadOnlyDegreePlannerList> readDegreePlannerList(Path filePath) throws DataConversionException {
        requireNonNull(filePath);

        Optional<JsonSerializableDegreePlannerList> jsonDegreePlannerList = JsonUtil.readJsonFile(
                filePath, JsonSerializableDegreePlannerList.class);
        if (!jsonDegreePlannerList.isPresent()) {
            return Optional.empty();
        }

        try {
            return Optional.of(jsonDegreePlannerList.get().toModelType());
        } catch (IllegalValueException ive) {
            logger.info("Illegal values found in " + filePath + ": " + ive.getMessage());
            throw new DataConversionException(ive);
        }
    }

    @Override
    public void saveDegreePlannerList(ReadOnlyDegreePlannerList degreePlannerList) throws IOException {
        saveDegreePlannerList(degreePlannerList, filePath);
    }

    /**
     * Similar to {@link #saveDegreePlannerList(ReadOnlyDegreePlannerList)}.
     *
     * @param filePath location of the data. Cannot be null.
     */
    public void saveDegreePlannerList(ReadOnlyDegreePlannerList degreePlannerList, Path filePath) throws IOException {
        requireNonNull(degreePlannerList);
        requireNonNull(filePath);

        FileUtil.createIfMissing(filePath);
        JsonUtil.saveJsonFile(new JsonSerializableDegreePlannerList(degreePlannerList), filePath);
    }
}
