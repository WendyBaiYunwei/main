package seedu.address.storage;

import static org.junit.Assert.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.testutil.TypicalModules;
import seedu.address.testutil.TypicalRequirementCategories;

public class JsonSerializableAddressBookTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableAddressBookTest");
    private static final Path TYPICAL_MODULES_FILE = TEST_DATA_FOLDER.resolve("typicalModulesAddressBook.json");
    private static final Path TYPICAL_REQUIREMENT_CATEGORY_FILE =
            TEST_DATA_FOLDER.resolve("typicalRequirementCategoryAddressBook.json");
    private static final Path INVALID_MODULE_FILE = TEST_DATA_FOLDER.resolve("invalidModuleAddressBook.json");
    private static final Path DUPLICATE_MODULE_FILE = TEST_DATA_FOLDER.resolve("duplicateModuleAddressBook.json");

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void toModelType_typicalModulesFile_success() throws Exception {
        JsonSerializableModuleList dataFromFile = JsonUtil.readJsonFile(TYPICAL_MODULES_FILE,
                JsonSerializableModuleList.class).get();
        JsonSerializableRequirementCategoryList dataFromFile2 = JsonUtil.readJsonFile(TYPICAL_REQUIREMENT_CATEGORY_FILE,
                JsonSerializableRequirementCategoryList.class).get();
        JsonSerializableAddressBook jsonSerializableAddressBook =
                new JsonSerializableAddressBook(dataFromFile.toModelType(), dataFromFile2.toModelType());
        AddressBook addressBookFromFile =
                jsonSerializableAddressBook.toModelType();
        JsonSerializableAddressBook typicalAddressBookData =
                new JsonSerializableAddressBook(TypicalModules.getTypicalModuleList(),
                        TypicalRequirementCategories.getTypicalRequirementCategoriesList());
        AddressBook typicalModulesAddressBook =
                typicalAddressBookData.toModelType();
        assertEquals(addressBookFromFile, typicalModulesAddressBook);
    }

    @Test
    public void toModelType_invalidModuleFile_throwsIllegalValueException() throws Exception {
        JsonSerializableModuleList dataFromFile = JsonUtil.readJsonFile(INVALID_MODULE_FILE,
                JsonSerializableModuleList.class).get();
        thrown.expect(IllegalValueException.class);
        dataFromFile.toModelType();
    }

    @Test
    public void toModelType_duplicateModules_throwsIllegalValueException() throws Exception {
        JsonSerializableModuleList dataFromFile = JsonUtil.readJsonFile(DUPLICATE_MODULE_FILE,
                JsonSerializableModuleList.class).get();
        thrown.expect(IllegalValueException.class);
        thrown.expectMessage(JsonSerializableModuleList.MESSAGE_DUPLICATE_MODULE);
        dataFromFile.toModelType();
    }

}
