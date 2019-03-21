package seedu.address.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static seedu.address.testutil.TypicalModules.ALICE;
import static seedu.address.testutil.TypicalModules.HOON;
import static seedu.address.testutil.TypicalModules.IDA;
import static seedu.address.testutil.TypicalModules.getTypicalModuleList;
import static seedu.address.testutil.TypicalRequirementCategories.getTypicalRequirementCategoriesList;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import seedu.address.commons.exceptions.DataConversionException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;

public class JsonAddressBookStorageTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonAddressBookStorageTest");

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Test
    public void readAddressBook_nullFilePath_throwsNullPointerException() throws Exception {
        thrown.expect(NullPointerException.class);
        readAddressBook(null, null);
    }

    private java.util.Optional<ReadOnlyAddressBook> readAddressBook(String moduleListFilePath,
            String requirementCategoryListFilePath) throws Exception {
        return new JsonAddressBookStorage(Paths.get(moduleListFilePath), Paths.get(requirementCategoryListFilePath))
                .readAddressBook(addToTestDataPathIfNotNull(moduleListFilePath),
                        addToTestDataPathIfNotNull(requirementCategoryListFilePath));
    }

    private Path addToTestDataPathIfNotNull(String prefsFileInTestDataFolder) {
        return prefsFileInTestDataFolder != null
                ? TEST_DATA_FOLDER.resolve(prefsFileInTestDataFolder)
                : null;
    }

    @Test
    public void read_missingFile_emptyResult() throws Exception {
        assertFalse(readAddressBook("NonExistentFile1.json", "NonExistentFile2.json").isPresent());
    }

    @Test
    public void read_notJsonFormat_exceptionThrown() throws Exception {

        thrown.expect(DataConversionException.class);
        readAddressBook("notJsonFormatAddressBook.json", "notJsonFormatAddressBook.json");

        // IMPORTANT: Any code below an exception-throwing line (like the one above) will be ignored.
        // That means you should not have more than one exception test in one method
    }

    @Test
    public void readAddressBook_invalidModuleAddressBook_throwDataConversionException() throws Exception {
        thrown.expect(DataConversionException.class);
        readAddressBook("invalidModuleAddressBook.json", "invalidModuleAddressBook.json");
    }

    @Test
    public void readAddressBook_invalidAndValidModuleAddressBook_throwDataConversionException() throws Exception {
        thrown.expect(DataConversionException.class);
        readAddressBook("invalidAndValidModuleAddressBook.json", "invalidAndValidModuleAddressBook.json");
    }

    @Test
    public void readAndSaveAddressBook_allInOrder_success() throws Exception {
        Path moduleListFilePath = testFolder.getRoot().toPath().resolve("TempAddressBook.json");
        Path requirementCategoryListFilePath = testFolder.getRoot().toPath().resolve("TempAddressBook1.json");
        AddressBook original =
                new JsonSerializableAddressBook(getTypicalModuleList(), getTypicalRequirementCategoriesList())
                        .toModelType();
        JsonAddressBookStorage jsonAddressBookStorage =
                new JsonAddressBookStorage(moduleListFilePath, requirementCategoryListFilePath);

        // Save in new file and read back
        jsonAddressBookStorage.saveModuleList(original, moduleListFilePath);
        jsonAddressBookStorage.saveRequirementCategoryList(original, requirementCategoryListFilePath);
        ReadOnlyAddressBook readBack =
                jsonAddressBookStorage.readAddressBook(moduleListFilePath, requirementCategoryListFilePath).get();
        assertEquals(original, new AddressBook(readBack));

        // Modify data, overwrite exiting file, and read back
        original.addModule(HOON);
        original.removeModule(ALICE);
        jsonAddressBookStorage.saveModuleList(original, moduleListFilePath);
        readBack = jsonAddressBookStorage.readAddressBook(moduleListFilePath, requirementCategoryListFilePath).get();
        assertEquals(original, new AddressBook(readBack));

        // Save and read without specifying file path
        original.addModule(IDA);
        jsonAddressBookStorage.saveModuleList(original); // file path not specified
        readBack = jsonAddressBookStorage.readAddressBook().get(); // file path not specified
        assertEquals(original, new AddressBook(readBack));

    }

    @Test
    public void saveAddressBook_nullAddressBook_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        saveAddressBook(null, "SomeFile1.json", "SomeFile2.json");
    }

    /**
     * Saves {@code addressBook} at the specified {@code filePath}.
     */
    private void saveAddressBook(ReadOnlyAddressBook addressBook, String moduleListFilePath,
            String requirementCategoryListFilePath) {
        try {
            new JsonAddressBookStorage(Paths.get(moduleListFilePath), Paths.get(requirementCategoryListFilePath))
                    .saveModuleList(addressBook, addToTestDataPathIfNotNull(moduleListFilePath));
        } catch (IOException ioe) {
            throw new AssertionError("There should not be an error writing to the file.", ioe);
        }
    }

    @Test
    public void saveAddressBook_nullFilePath_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        saveAddressBook(new AddressBook(), null, null);
    }
}
