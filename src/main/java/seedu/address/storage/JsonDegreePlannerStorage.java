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
import seedu.address.model.ReadOnlyAddressBook;

public class JsonDegreePlannerStorage implements DegreePlannerStorage {

    private static final Logger logger = LogsCenter.getLogger(JsonDegreePlannerStorage.class);

    private Path filePath;

    public JsonDegreePlannerStorage(Path filePath) {
        this.filePath = filePath;
    }

    public Path getDegreePlannerFilePath() {
        return filePath;
    }

    @Override
    public Optional<ReadOnlyAddressBook> readDegreePlanner() throws DataConversionException {
        return readDegreePlanner(filePath);
    }

    /**
     * Similar to {@link #readDegreePlanner()}.
     *
     * @param filePath location of the data. Cannot be null.
     * @throws DataConversionException if the file is not in the correct format.
     */
    public Optional<ReadOnlyAddressBook> readDegreePlanner(Path filePath) throws DataConversionException {
        requireNonNull(filePath);

        Optional<JsonSerializableDegreePlanner> jsonDegreePlanner = JsonUtil.readJsonFile(
                filePath, JsonSerializableDegreePlanner.class);
        if (!jsonDegreePlanner.isPresent()) {
            return Optional.empty();
        }

        try {
            return Optional.of(jsonDegreePlanner.get().toModelType());
        } catch (IllegalValueException ive) {
            logger.info("Illegal values found in " + filePath + ": " + ive.getMessage());
            throw new DataConversionException(ive);
        }
    }

    @Override
    public void saveDegreePlanner(ReadOnlyAddressBook addressBook) throws IOException {
        saveDegreePlanner(addressBook, filePath);
    }

    /**
     * Similar to {@link #saveDegreePlanner(ReadOnlyAddressBook)}.
     *
     * @param filePath location of the data. Cannot be null.
     */
    public void saveDegreePlanner(ReadOnlyAddressBook addressBook, Path filePath) throws IOException {
        requireNonNull(addressBook);
        requireNonNull(filePath);

        FileUtil.createIfMissing(filePath);
        JsonUtil.saveJsonFile(new JsonSerializableDegreePlanner(addressBook), filePath);
    }

}
