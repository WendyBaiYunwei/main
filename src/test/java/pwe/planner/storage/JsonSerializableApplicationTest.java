package pwe.planner.storage;

import static org.junit.Assert.assertEquals;
import static pwe.planner.testutil.TypicalModules.getTypicalModuleList;
import static pwe.planner.testutil.TypicalRequirementCategories.getTypicalRequirementCategoriesList;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import pwe.planner.commons.exceptions.IllegalValueException;
import pwe.planner.commons.util.JsonUtil;
import pwe.planner.model.Application;
import pwe.planner.testutil.TypicalDegreePlanners;

public class JsonSerializableApplicationTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableApplicationTest");
    private static final Path TYPICAL_MODULES_FILE = TEST_DATA_FOLDER.resolve("typicalModulesList.json");
    private static final Path TYPICAL_DEGREE_PLANNER_FILE =
            TEST_DATA_FOLDER.resolve("typicalDegreePlannerList.json");
    private static final Path TYPICAL_REQUIREMENT_CATEGORY_FILE =
            TEST_DATA_FOLDER.resolve("typicalRequirementCategoryList.json");
    private static final Path INVALID_MODULE_FILE = TEST_DATA_FOLDER.resolve("invalidModuleList.json");
    private static final Path DUPLICATE_MODULE_FILE = TEST_DATA_FOLDER.resolve("duplicateModuleList.json");

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void toModelType_typicalModulesFile_success() throws Exception {
        JsonSerializableModuleList dataFromModuleListFile = JsonUtil.readJsonFile(TYPICAL_MODULES_FILE,
                JsonSerializableModuleList.class).get();
        JsonSerializableDegreePlannerList dataFromDegreePlannerListFile =
                JsonUtil.readJsonFile(TYPICAL_DEGREE_PLANNER_FILE, JsonSerializableDegreePlannerList.class).get();
        JsonSerializableRequirementCategoryList dataFromRequirementCategoryListFile =
                JsonUtil.readJsonFile(TYPICAL_REQUIREMENT_CATEGORY_FILE,
                        JsonSerializableRequirementCategoryList.class).get();
        JsonSerializableApplication jsonSerializableApplication =
                new JsonSerializableApplication(dataFromModuleListFile.toModelType(),
                        dataFromDegreePlannerListFile.toModelType(),
                        dataFromRequirementCategoryListFile.toModelType());
        Application applicationFromFile =
                jsonSerializableApplication.toModelType();
        JsonSerializableApplication typicalApplicationData =
                new JsonSerializableApplication(getTypicalModuleList(),
                        TypicalDegreePlanners.getTypicalDegreePlannerList(), getTypicalRequirementCategoriesList());
        Application typicalApplication =
                typicalApplicationData.toModelType();
        assertEquals(applicationFromFile, typicalApplication);
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
