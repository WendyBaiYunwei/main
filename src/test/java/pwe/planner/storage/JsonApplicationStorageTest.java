package pwe.planner.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static pwe.planner.testutil.TypicalDegreePlanners.getTypicalDegreePlannerList;
import static pwe.planner.testutil.TypicalModules.ALICE;
import static pwe.planner.testutil.TypicalModules.HOON;
import static pwe.planner.testutil.TypicalModules.IDA;
import static pwe.planner.testutil.TypicalModules.getTypicalModuleList;
import static pwe.planner.testutil.TypicalRequirementCategories.getTypicalRequirementCategoriesList;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import pwe.planner.commons.exceptions.DataConversionException;
import pwe.planner.model.Application;
import pwe.planner.model.ReadOnlyApplication;

public class JsonApplicationStorageTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonApplicationStorageTest");

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Test
    public void readApplication_nullFilePath_throwsNullPointerException() throws Exception {
        thrown.expect(NullPointerException.class);
        readApplication(null, null, null);
    }

    /**
     * Reads files at the specified {@code moduleListFilePath}, {@code degreePlannerListFilePath}
     * and {@code requirementCategoryListFilePath}.
     */
    private java.util.Optional<ReadOnlyApplication> readApplication(String moduleListFilePath,
            String degreePlannerListFilePath, String requirementCategoryListFilePath) throws Exception {
        return new JsonApplicationStorage(Paths.get(moduleListFilePath), Paths.get(degreePlannerListFilePath),
                Paths.get(requirementCategoryListFilePath))
                .readApplication(addToTestDataPathIfNotNull(moduleListFilePath),
                        addToTestDataPathIfNotNull(degreePlannerListFilePath),
                        addToTestDataPathIfNotNull(requirementCategoryListFilePath));
    }

    private Path addToTestDataPathIfNotNull(String prefsFileInTestDataFolder) {
        return prefsFileInTestDataFolder != null
                ? TEST_DATA_FOLDER.resolve(prefsFileInTestDataFolder)
                : null;
    }

    @Test
    public void read_missingFile_emptyResult() throws Exception {
        assertFalse(readApplication("NonExistentModuleListFile.json",
                "NonExistentDegreePlannerListFile.json",
                "NonExistentRequirementCategoryListFile.json").isPresent());
    }

    @Test
    public void read_notJsonFormat_exceptionThrown() throws Exception {

        thrown.expect(DataConversionException.class);
        readApplication("notJsonFormatList.json",
                "notJsonFormatList.json",
                "notJsonFormatList.json");

        // IMPORTANT: Any code below an exception-throwing line (like the one above) will be ignored.
        // That means you should not have more than one exception test in one method
    }

    @Test
    public void readApplication_invalidModuleApplication_throwDataConversionException() throws Exception {
        thrown.expect(DataConversionException.class);
        readApplication("invalidModuleList.json",
                "invalidDegreePlannerList.json",
                "invalidModuleList.json");
    }

    @Test
    public void readApplication_invalidAndValidModuleApplication_throwDataConversionException() throws Exception {
        thrown.expect(DataConversionException.class);
        readApplication("invalidAndValidModuleList.json",
                "invalidAndValidDegreePlannerList.json",
                "invalidAndValidModuleList.json");
    }

    @Test
    public void readAndSaveApplication_allInOrder_success() throws Exception {
        Path moduleListFilePath = testFolder.getRoot().toPath().resolve("TempModuleApplication.json");
        Path degreePlannerListFilePath = testFolder.getRoot().toPath().resolve("TempDegreePlannerApplication.json");
        Path requirementCategoryListFilePath =
                testFolder.getRoot().toPath().resolve("TempRequirementCategoryApplication.json");
        Application original = new JsonSerializableApplication(getTypicalModuleList(), getTypicalDegreePlannerList(),
                getTypicalRequirementCategoriesList()).toModelType();
        JsonApplicationStorage jsonApplicationStorage =
                new JsonApplicationStorage(moduleListFilePath, degreePlannerListFilePath,
                        requirementCategoryListFilePath);

        // Save in new file and read back
        jsonApplicationStorage.saveModuleList(original, moduleListFilePath);
        jsonApplicationStorage.saveDegreePlannerList(original, degreePlannerListFilePath);
        jsonApplicationStorage.saveRequirementCategoryList(original, requirementCategoryListFilePath);
        ReadOnlyApplication readBack =
                jsonApplicationStorage
                        .readApplication(moduleListFilePath, degreePlannerListFilePath, requirementCategoryListFilePath)
                        .get();
        assertEquals(original, new Application(readBack));

        // Modify data, overwrite exiting file, and read back
        original.addModule(HOON);
        original.removeModule(ALICE);
        jsonApplicationStorage.saveModuleList(original, moduleListFilePath);
        jsonApplicationStorage.saveDegreePlannerList(original, degreePlannerListFilePath);
        readBack = jsonApplicationStorage
                .readApplication(moduleListFilePath, degreePlannerListFilePath, requirementCategoryListFilePath).get();
        assertEquals(original, new Application(readBack));

        // Save and read without specifying file path
        original.addModule(IDA);
        jsonApplicationStorage.saveModuleList(original); // file path not specified
        readBack = jsonApplicationStorage.readApplication().get(); // file path not specified
        assertEquals(original, new Application(readBack));

    }

    @Test
    public void saveApplication_nullApplication_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        saveApplication(null, "SomeModuleListFile.json",
                "SomeDegreePlannerListFile.json",
                "SomeRequirementCategoryListFile.json");
    }

    /**
     * Saves {@code application} at the specified {@code filePath}.
     */
    private void saveApplication(ReadOnlyApplication application, String moduleListFilePath,
            String degreePlannerListFilePath,
            String requirementCategoryListFilePath) {
        try {
            new JsonApplicationStorage(Paths.get(moduleListFilePath), Paths.get(degreePlannerListFilePath),
                    Paths.get(requirementCategoryListFilePath))
                    .saveModuleList(application, addToTestDataPathIfNotNull(moduleListFilePath));
        } catch (IOException ioe) {
            throw new AssertionError("There should not be an error writing to the file.", ioe);
        }
    }

    @Test
    public void saveApplication_nullFilePath_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        saveApplication(new Application(), null, null, null);
    }
}
